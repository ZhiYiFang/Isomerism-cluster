/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.synchronizer.syn.tasks;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.NotificationPublishService;
import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.ControllerDownBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.ControllerTypes.TypeName;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.Isomerism;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.controller.down.DownControllerBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.controller.down.NewControllerBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.IsomerismControllers;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.IsomerismControllers.ControllerStatus;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.IsomerismControllersBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.IsomerismControllersKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.FloodlighttopoService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.GetFloodlightHealthInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuHealthInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.RyutopoService;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.binding.KeyedInstanceIdentifier;

import com.google.common.base.Optional;
import com.google.gson.JsonParser;

import xidian.synchronier.rediskey.CSKey;
import xidian.synchronier.rediskey.OdlLinksKey;
import xidian.synchronier.rediskey.RedisController;
import xidian.synchronier.rediskey.SCKey;
import xidian.synchronier.util.InstructionUtils;
import xidian.synchronier.util.MoveOrder;
import xidian.synchronier.util.RedisService;
import xidian.synchronizer.syn.topoEle.Link;

public class AliveTestTask extends TimerTask {

	private DataBroker dataBroker;
	private FloodlighttopoService floodlighttopoService;
	private RyutopoService ryutopoService;
	private JsonParser jsonParser;
	private RedisService redisService;
	private NotificationPublishService notificationPublishService;

	public AliveTestTask(DataBroker dataBroker, FloodlighttopoService floodlighttopoService,
			RyutopoService ryutopoService, NotificationPublishService notificationPublishService) {
		this.dataBroker = dataBroker;
		this.floodlighttopoService = floodlighttopoService;
		this.ryutopoService = ryutopoService;
		this.jsonParser = new JsonParser();
		redisService = RedisService.getInstance();
		this.notificationPublishService = notificationPublishService;
	}

	public void run() {
		ReadOnlyTransaction read = dataBroker.newReadOnlyTransaction();
		InstanceIdentifier<Isomerism> path = InstanceIdentifier.create(Isomerism.class);
		Isomerism result = null;

		try {

			Optional<Isomerism> optional = read.read(LogicalDatastoreType.CONFIGURATION, path).get();

			if (optional.isPresent()) {
				result = optional.get();
				List<IsomerismControllers> controllers = result.getIsomerismControllers();
				for (IsomerismControllers c : controllers) {
					Ipv4Address ip = c.getIp();
					PortNumber port = c.getPort();
					TypeName type = c.getTypeName();
					boolean isAlive = false;

					switch (type) {
					case Agile:
						break;
					case Apic:
						break;
					case Floodlight:
						isAlive = isFloodlightAlive(c);
						break;
					case Ryu:
						isAlive = isRyuAlive(c);
						break;
					case Vcf:
						break;
					default:
						break;
					}

					// 宕机控制器控制的交换机迁移到临近的控制器上
					if (!isAlive) {

						// 控制器状态标记成down
						WriteTransaction writeStatus = dataBroker.newWriteOnlyTransaction();
						KeyedInstanceIdentifier<IsomerismControllers, IsomerismControllersKey> statusPath = InstanceIdentifier
								.create(Isomerism.class)
								.child(IsomerismControllers.class, new IsomerismControllersKey(ip));
						IsomerismControllersBuilder builder = new IsomerismControllersBuilder();
						builder.setControllerStatus(ControllerStatus.Down);
						writeStatus.merge(LogicalDatastoreType.CONFIGURATION, statusPath, builder.build());
						writeStatus.submit();

						// 获取所有临近的控制器
						Set<RedisController> adjControllers = getAdjacentController(c);

						// 根据策略选择迁移目的地控制器
						RedisController adjController = chooseAController(adjControllers);

						// 找到宕机控制器控制的交换机 并向临近控制器迁移
						List<String> switches = redisService.get(CSKey.getSwitches, c.getIp().getValue(), List.class);
						for (String sw : switches) {
							MoveOrder order = new MoveOrder(adjController.getIp().getValue(), sw);
							InstructionUtils.moveSwitch(order);
						}
						
						// 迁移完成以后 发送notification
						sendNofitication(ip, type, adjController);
						
						// 然后写
					}
				}
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendNofitication(Ipv4Address ip, TypeName type, RedisController adjController) {
		try {
			Thread.sleep(1000);
			ControllerDownBuilder builder = new ControllerDownBuilder();
			DownControllerBuilder downController = new DownControllerBuilder();
			downController.setIp(ip);
			downController.setTypeName(type);
			builder.setDownController(downController.build());
			NewControllerBuilder newController = new NewControllerBuilder();
			newController.setIp(adjController.getIp());
			newController.setTypeName(adjController.getType());
			builder.setNewController(newController.build());
			notificationPublishService.putNotification(builder.build());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private RedisController chooseAController(Set<RedisController> adjControllers) {
		int min = Integer.MAX_VALUE;
		RedisController ret = null;
		// 暂时根据控制的交换机的数量来选择
		for (RedisController controller : adjControllers) {
			List<String> switches = redisService.get(CSKey.getEdgeSwitches, controller.getIp().getValue(), List.class);
			if (switches.size() < min)
				ret = controller;
		}

		return ret;
	}

	private Set<RedisController> getAdjacentController(IsomerismControllers controller) {
		// 确定这个控制器的临近控制器
		List<String> ses = redisService.get(CSKey.getEdgeSwitches, controller.getIp().getValue(), List.class);
		// 根据边界链路关系来确定边界交换机对端的交换机
		HashSet<Link> odlLinks = redisService.get(OdlLinksKey.getOdlLinks, "", HashSet.class);

		Set<RedisController> adjacentControllers = new HashSet<>();
		for (String sw : ses) {
			for (Link link : odlLinks) {
				String srcSw = link.getSrcSwitch();
				String dstSw = link.getDstSwitch();
				String adjacentSw = sw.equals(srcSw) ? dstSw : sw.equals(dstSw) ? srcSw : null;
				if (adjacentSw != null) {
					RedisController controllerIp = redisService.get(SCKey.getController, adjacentSw,
							RedisController.class);
					if (!controllerIp.getIp().getValue().equals(controller.getIp().getValue()))
						adjacentControllers.add(controllerIp);
				}
			}
		}
		return adjacentControllers;
	}

	private boolean isFloodlightAlive(IsomerismControllers controller) {
		GetFloodlightHealthInputBuilder input = new GetFloodlightHealthInputBuilder();
		input.setIp(controller.getIp());
		input.setPort(controller.getPort());
		boolean result = false;
		try {
			result = floodlighttopoService.getFloodlightHealth(input.build()).get().getResult().isResult();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private boolean isRyuAlive(IsomerismControllers controller) {

		GetRyuHealthInputBuilder input = new GetRyuHealthInputBuilder();
		input.setIp(controller.getIp());
		input.setPort(controller.getPort());
		boolean result = false;
		try {
			result = ryutopoService.getRyuHealth(input.build()).get().getResult().isResult();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}
