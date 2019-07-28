/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package xidian.synchronizer.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.Isomerism;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.IsomerismControllers;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.IsomerismControllers.ControllerStatus;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.IsomerismControllersBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.IsomerismControllersKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.FloodlighttopoService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.migrate.rev190726.MigrateAbnormalControllerInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.migrate.rev190726.MigrateAbnormalControllerOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.migrate.rev190726.MigrateAbnormalControllerOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.migrate.rev190726.MigrateService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.RyutopoService;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.binding.KeyedInstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import xidian.synchronizer.rediskey.CSKey;
import xidian.synchronizer.rediskey.OdlLinksKey;
import xidian.synchronizer.rediskey.RedisController;
import xidian.synchronizer.rediskey.SCKey;
import xidian.synchronizer.syn.tasks.TopoTask;
import xidian.synchronizer.syn.topoEle.Link;
import xidian.synchronizer.util.InstructionUtils;
import xidian.synchronizer.util.MoveOrder;
import xidian.synchronizer.util.RedisService;

public class MigrateImpl implements MigrateService {
	private final DataBroker dataBroker;
	private final FloodlighttopoService floodlighttopoService;
	private final RyutopoService ryutopoService;

	private RedisService redisService;

	public MigrateImpl(DataBroker dataBroker, FloodlighttopoService floodlighttopoService,
			RyutopoService ryutopoService) {
		this.dataBroker = dataBroker;
		this.floodlighttopoService = floodlighttopoService;
		this.ryutopoService = ryutopoService;
	}

	@Override
	public Future<RpcResult<MigrateAbnormalControllerOutput>> migrateAbnormalController(
			MigrateAbnormalControllerInput input) {
		Ipv4Address controllerIp = input.getControllerIp();
		WriteTransaction writeControllerStatus = dataBroker.newWriteOnlyTransaction();

		KeyedInstanceIdentifier<IsomerismControllers, IsomerismControllersKey> statusPath = InstanceIdentifier
				.create(Isomerism.class).child(IsomerismControllers.class, new IsomerismControllersKey(controllerIp));

		// 迁移控制器的
		Set<RedisController> adjControllers = getAdjacentController(controllerIp.getValue());
		if(adjControllers.size() == 0) {
			return RpcResultBuilder.success(new MigrateAbnormalControllerOutputBuilder().setTargetIp("no normal controller").build()).buildFuture();
		}
		RedisController ret = chooseAController(adjControllers); // destination Controller

		redisService = RedisService.getInstance();
		List<String> switches = redisService.get(CSKey.getSwitches, controllerIp.getValue(), List.class);
		for (String sw : switches) {
			MoveOrder order = new MoveOrder(ret.getIp().getValue(), sw);
			InstructionUtils.moveSwitch(order);
		}

		MigrateAbnormalControllerOutputBuilder outputBuilder = new MigrateAbnormalControllerOutputBuilder();
		outputBuilder.setTargetIp(ret.getIp().getValue());
		outputBuilder.setTypeName(ret.getType());

		// 改变控制器的状态
		IsomerismControllersBuilder builder = new IsomerismControllersBuilder();
		builder.setIp(controllerIp);
		builder.setKey(new IsomerismControllersKey(controllerIp));
		builder.setControllerStatus(ControllerStatus.Abnormal);
		writeControllerStatus.merge(LogicalDatastoreType.CONFIGURATION, statusPath, builder.build());
		writeControllerStatus.submit();

		return RpcResultBuilder.success(outputBuilder.build()).buildFuture();
	}

	private Set<RedisController> getAdjacentController(String abIp) {
		// 确定这个控制器的临近控制器
		redisService = RedisService.getInstance();
		List<String> ses = redisService.get(CSKey.getEdgeSwitches, abIp, List.class);
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
					if (!controllerIp.getIp().getValue().equals(abIp))
						adjacentControllers.add(controllerIp);
				}
			}
		}
		return adjacentControllers;
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
}
