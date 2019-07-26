/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.synchronizer.syn.tasks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.NotificationPublishService;
import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Prefix;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.AlertCommon.MessageLevel;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.AlertCommon.MessageType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.AlertCommon.SourceType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.BasicSetting;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.ControllerDownBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.ControllerTypes.TypeName;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.ControllersService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.Isomerism;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.SendAlertInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.controller.down.DownControllerBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.controller.down.NewControllerBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.IsomerismControllers;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.IsomerismControllers.ControllerStatus;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.IsomerismControllersBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.IsomerismControllersKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightacl.rev190713.Acl.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightacl.rev190713.Acl.NwProto;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightacl.rev190713.AddFloodlightAclInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightacl.rev190713.FloodlightaclService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightacl.rev190713.GetFloodlightAclInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightacl.rev190713.GetFloodlightAclOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.FloodlighttopoService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.GetFloodlightHealthInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuHealthInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.RyutopoService;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.binding.KeyedInstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import xidian.synchronizer.rediskey.ACLKey;
import xidian.synchronizer.rediskey.CSKey;
import xidian.synchronizer.rediskey.OdlLinksKey;
import xidian.synchronizer.rediskey.RedisController;
import xidian.synchronizer.rediskey.SCKey;
import xidian.synchronizer.syn.SynchronizeMessage;
import xidian.synchronizer.syn.topoEle.Link;
import xidian.synchronizer.util.InstructionUtils;
import xidian.synchronizer.util.MoveOrder;
import xidian.synchronizer.util.RedisService;

public class AliveTestTask extends TimerTask {

	private DataBroker dataBroker;
	private FloodlighttopoService floodlighttopoService;
	private RyutopoService ryutopoService;
	private RedisService redisService;
	private NotificationPublishService notificationPublishService;
	private ControllersService controllerService;
	private FloodlightaclService floodlightaclService;

	private Logger LOG = LoggerFactory.getLogger(AliveTestTask.class);

	public AliveTestTask(DataBroker dataBroker, FloodlighttopoService floodlighttopoService,
			RyutopoService ryutopoService, NotificationPublishService notificationPublishService,
			ControllersService controllerService, FloodlightaclService floodlightaclService) {
		this.dataBroker = dataBroker;
		this.floodlighttopoService = floodlighttopoService;
		this.ryutopoService = ryutopoService;
		redisService = RedisService.getInstance();
		this.notificationPublishService = notificationPublishService;
		this.controllerService = controllerService;
		this.floodlightaclService = floodlightaclService;
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
					ReadOnlyTransaction readStatus = dataBroker.newReadOnlyTransaction();
					KeyedInstanceIdentifier<IsomerismControllers, IsomerismControllersKey> statusPath = InstanceIdentifier
							.create(Isomerism.class).child(IsomerismControllers.class, new IsomerismControllersKey(ip));
					ControllerStatus status = readStatus.read(LogicalDatastoreType.CONFIGURATION, statusPath).get()
							.get().getControllerStatus();
					
					// syn acl
					if (isAlive && c.getTypeName().equals(TypeName.Floodlight)) {
						GetFloodlightAclInputBuilder input = new GetFloodlightAclInputBuilder();
						input.setControllerIp(c.getIp());
						input.setControllerPort(c.getPort());
						Future<RpcResult<GetFloodlightAclOutput>> res = floodlightaclService
								.getFloodlightAcl(input.build());
						String list = res.get().getResult().getResult();
						RedisService.getInstance().set(ACLKey.getACL, c.getIp().getValue(), list);
					}
				
					// 宕机控制器控制的交换机迁移到临近的控制器上
					if (!isAlive && status.equals(ControllerStatus.Up)) {

						LOG.info("Controller: [" + ip.getValue() + " " + c.getTypeName() + "] is down!");

						// Send alert to server

						HashMap<String, String> map = new HashMap<String, String>();
						map.put("Type", "down");
						map.put("Target", ip.getValue());
						map.put("Target_type", c.getTypeName().getName());
						InstanceIdentifier<BasicSetting> isorIpPath = InstanceIdentifier.create(BasicSetting.class);
						Ipv4Address sourceIp = readStatus.read(LogicalDatastoreType.CONFIGURATION, isorIpPath).get()
								.get().getMiddleIp();

						sendAlert(new Gson().toJson(map), SynchronizeMessage.alertNo, MessageLevel.High, sourceIp,
								MessageType.Alert);

						// 控制器状态标记成down
						WriteTransaction writeStatus = dataBroker.newWriteOnlyTransaction();
//						IsomerismControllers con = writeStatus.read(LogicalDatastoreType.CONFIGURATION, statusPath).get().get();
						IsomerismControllersBuilder builder = new IsomerismControllersBuilder();
						builder.setIp(ip);
						builder.setKey(new IsomerismControllersKey(ip));
						builder.setControllerStatus(ControllerStatus.Down);
						writeStatus.merge(LogicalDatastoreType.CONFIGURATION, statusPath, builder.build());
						writeStatus.submit();
// don't exec if is down already
						// 获取所有临近的控制器
						Set<RedisController> adjControllers = getAdjacentController(c);

						// 根据策略选择迁移目的地控制器
						RedisController adjController = chooseAController(adjControllers);

						if (adjController.getType().equals(TypeName.Floodlight)
								&& c.getTypeName().equals(TypeName.Floodlight)) {
							try {
								synACL(c, adjController);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								LOG.info("Synchronize ACL rules falied");
							}
						}
						
						// calculate
						HashMap<String, String> map2 = new HashMap<String, String>();
						map.put("Type", adjController.getType().getName());
						map.put("Ip", adjController.getIp().getValue());
						sendAlert(new Gson().toJson(map2), SynchronizeMessage.alertNo, MessageLevel.Medium, sourceIp,
								MessageType.Calculate);

						LOG.info("Move the switches to controller " + "[" + adjController.getIp().getValue() + " "
								+ adjController.getType() + "]");
						// 找到宕机控制器控制的交换机 并向临近控制器迁移

						// action
						sendAlert("true", SynchronizeMessage.alertNo, MessageLevel.Medium, sourceIp,
								MessageType.Action);

						redisService = RedisService.getInstance();
						List<String> switches = redisService.get(CSKey.getSwitches, c.getIp().getValue(), List.class);
						for (String sw : switches) {
							MoveOrder order = new MoveOrder(adjController.getIp().getValue(), sw);
							InstructionUtils.moveSwitch(order);
						}

						// test
						sendAlert("true", SynchronizeMessage.alertNo, MessageLevel.Medium, sourceIp, MessageType.Test);

						// 迁移完成以后 发送notification
						sendNofitication(ip, type, adjController);

						// normal
						sendAlert("true", SynchronizeMessage.alertNo, MessageLevel.Medium, sourceIp,
								MessageType.Normal);
					}
				}
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	private void synACL(IsomerismControllers c, RedisController adjController) throws Exception{
		String response = RedisService.getInstance().get(ACLKey.getACL, c.getIp().getValue(),
				String.class);

		JsonParser parser = new JsonParser();
		JsonArray array = parser.parse(response).getAsJsonArray();
		Iterator<JsonElement> it = array.iterator();

		LOG.info("Synchronize ACL rules from" + c.getIp().getValue() + " to "
				+ adjController.getIp().getValue());

		while (it.hasNext()) {
			JsonObject ele = it.next().getAsJsonObject();
			String id = ele.get("id").getAsString();
			String srcIp = ele.get("nw_src").getAsString();
			String dstIp = ele.get("nw_dst").getAsString();
			String proto = ele.get("nw_proto").getAsString();
			String tp = ele.get("tp_dst").getAsString();
			String action = ele.get("action").getAsString();
			
			AddFloodlightAclInputBuilder input = new AddFloodlightAclInputBuilder();
			input.setControllerIp(adjController.getIp());
			input.setControllerPort(adjController.getPort());
			input.setSrcIp(new Ipv4Prefix(srcIp));
			input.setDstIp(new Ipv4Prefix(dstIp));
			input.setNwProto(NwProto.forValue(Integer.valueOf(proto)));
			input.setTpDst(new PortNumber(Integer.valueOf(tp)));
			input.setAction(Action.valueOf(action));
			
			floodlightaclService.addFloodlightAcl(input.build());
		}
	}
	private void sendAlert(String desc, int alertNo, MessageLevel level, Ipv4Address sourceIp, MessageType type) {
		SendAlertInputBuilder input = new SendAlertInputBuilder();
		input.setAlertNo(alertNo);
		input.setMessageLevel(level);
		input.setMessageType(type);

		input.setMessageDesc(desc);
		input.setSourceIp(sourceIp);
		input.setSourceType(SourceType.Isomerism);
		controllerService.sendAlert(input.build());
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
				min = switches.size();
			ret = controller;
		}

		return ret;
	}

	private Set<RedisController> getAdjacentController(IsomerismControllers controller) {
		// 确定这个控制器的临近控制器
		redisService = RedisService.getInstance();
		List<String> ses = redisService.get(CSKey.getEdgeSwitches, controller.getIp().getValue(), List.class);
		// 根据边界链路关系来确定边界交换机对端的交换机
		String odlLinks = redisService.get(OdlLinksKey.getOdlLinks, "", String.class);
		JsonParser parser = new JsonParser();
		JsonArray ja = parser.parse(odlLinks).getAsJsonArray();
		Gson gson = new Gson();

		Set<RedisController> adjacentControllers = new HashSet<>();

		// iter edge switches
		for (String sw : ses) {
			Iterator<JsonElement> it = ja.iterator();
			// odl links
			while (it.hasNext()) {
				JsonElement ele = it.next();
				Link link = gson.fromJson(ele, Link.class);
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
