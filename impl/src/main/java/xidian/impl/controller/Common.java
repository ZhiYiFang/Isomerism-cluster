/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package xidian.impl.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.ReadFailedException;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.AlertCommon.MessageLevel;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.AlertCommon.MessageType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.AlertCommon.SourceType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.BasicSetting;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.ControllerTypes.TypeName;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.ControllersService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.FlowBasic.FlowModCmd;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.basic.setting.AttackConfirmUrl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.basic.setting.ServerUrl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.FlowModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.FlowModOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.FlowModOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.GetAllSwitchesOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.GetAllSwitchesOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.GetSwitchFlowTableInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.GetSwitchFlowTableOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.GetSwitchFlowTableOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.Isomerism;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.SendAlertInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.SendAlertOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.SendAlertOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.flow.mod.input.FlowInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.flow.mod.input.flow.input.Matches;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.get.all.switches.output.Switches;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.get.all.switches.output.SwitchesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.IsomerismControllers;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.IsomerismControllers.ControllerStatus;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightflowtable.rev190602.FloodlightflowtableService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightflowtable.rev190602.GetFloodlightSwitchFlowtableInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.FloodlighttopoService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.GetFloodlightSwitchesInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.GetFloodlightSwitchesOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.GetFloodligtSwitchPortDescInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.GetFloodligtSwitchPortInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylighttopo.rev200301.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylightflowtable.rev200301.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylighttopo.rev200301.OpendaylighttopoService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylighttopo.rev200301.GetOpendaylightSwitchesInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.guard.rev150105.DatapathId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryuflowtable.rev190602.GetRyuSwitchFlowtableInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryuflowtable.rev190602.RyuflowtableService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuSwitchPortDescInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuSwitchPortInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuSwitchesListInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuSwitchesListOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.RyutopoService;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import xidian.impl.controller.floodlight.FloodlightUrls;
import xidian.impl.controller.ryu.RyuAction;
import xidian.impl.controller.ryu.RyuFlow;
import xidian.impl.controller.ryu.RyuMatch;
import xidian.impl.controller.ryu.RyuUrls;
import xidian.impl.rediskey.RedisController;
import xidian.impl.rediskey.SCKey;
import xidian.impl.util.DpidUtils;
import xidian.impl.util.HttpUtils;
import xidian.impl.util.RedisService;

public class Common implements ControllersService {

	private final DataBroker dataBroker;
	private final FloodlighttopoService floodlightTopoService;
	private final FloodlightflowtableService floodlightflowtableService;
	private final RyutopoService ryutopoService;
	private final RyuflowtableService ryuflowtableService;
	private final OpendaylighttopoService opendaylighttopoService;
	private final OpendaylightflowtableService opendaylightflowtableService;
	private RedisService redisService = RedisService.getInstance();
	private final Logger LOG = LoggerFactory.getLogger(Common.class);
	private static int flowNum = 0;
	private static String defaultFlowName = "static_flow" + flowNum;

	public Common(DataBroker dataBroker, FloodlighttopoService floodlightTopoService,
			FloodlightflowtableService floodlightflowtableService, RyutopoService ryutopoService,
			RyuflowtableService ryuflowtableService, OpendaylighttopoService opendaylighttopoService,
				  OpendaylightflowtableService opendaylightflowtableService) {
		super();
		this.dataBroker = dataBroker;
		this.floodlightTopoService = floodlightTopoService;
		this.floodlightflowtableService = floodlightflowtableService;
		this.ryutopoService = ryutopoService;
		this.ryuflowtableService = ryuflowtableService;
		this.opendaylighttopoService = opendaylighttopoService;
		this.opendaylightflowtableService = opendaylightflowtableService;
	}

	@Override
	public Future<RpcResult<GetAllSwitchesOutput>> getAllSwitches() {
		LOG.info("Get all switches information");
		ReadOnlyTransaction readControllers = dataBroker.newReadOnlyTransaction();
		InstanceIdentifier<Isomerism> controllersPath = InstanceIdentifier.create(Isomerism.class);
		List<Switches> ret = new ArrayList<>();
		try {
			Optional<Isomerism> optional = readControllers.read(LogicalDatastoreType.CONFIGURATION, controllersPath)
					.get();
			if (optional.isPresent()) {
				List<IsomerismControllers> controllers = optional.get().getIsomerismControllers();
				for (IsomerismControllers controller : controllers) {
					if (controller.getControllerStatus().equals(ControllerStatus.Down)) {
						continue;
					}
					TypeName type = controller.getTypeName();
					Ipv4Address ip = controller.getIp();
					PortNumber port = controller.getPort();
					switch (type) {
						case Agile:
							break;
						case Apic:
							break;
						case Floodlight:
							ret.addAll(getFloodlightSwitches(ip, port));
							break;
						case Ryu:
							ret.addAll(getRyuSwitches(ip, port));
							break;
						case Opendaylight:
							ret.addAll(getOpendaylightSwitches(ip, port));
							break;
						case Vcf:
							break;
						default:
							break;
					}
				}
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GetAllSwitchesOutputBuilder outputBuilder = new GetAllSwitchesOutputBuilder();
		outputBuilder.setSwitches(ret);
		return RpcResultBuilder.success(outputBuilder.build()).buildFuture();
	}

	private List<Switches> getRyuSwitches(Ipv4Address ip, PortNumber port) {
		GetRyuSwitchesListInputBuilder builder = new GetRyuSwitchesListInputBuilder();
		builder.setIp(ip);
		builder.setPort(port);
		List<Switches> ret = new ArrayList<>();
		try {
			GetRyuSwitchesListOutput result = ryutopoService.getRyuSwitchesList(builder.build()).get().getResult();
			String json = result.getResult();
			JsonParser parser = new JsonParser();
			JsonArray jsonArray = parser.parse(json).getAsJsonArray();
			Iterator<JsonElement> it = jsonArray.iterator();
			while (it.hasNext()) {
				JsonElement ele = it.next();
				// 后边会给Switches里边添加字段
				SwitchesBuilder switchesBuilder = new SwitchesBuilder();
				DatapathId dpid = new DatapathId(DpidUtils.getDpidFromInt(ele.getAsInt()));
				switchesBuilder.setDpid(dpid);

				GetRyuSwitchPortInputBuilder input = new GetRyuSwitchPortInputBuilder();
				input.setDpid(dpid);
				input.setIp(ip);
				input.setPort(port);
				String portJson = ryutopoService.getRyuSwitchPort(input.build()).get().getResult().getResult();
				GetRyuSwitchPortDescInputBuilder input2 = new GetRyuSwitchPortDescInputBuilder();
				input2.setDpid(dpid);
				input2.setIp(ip);
				input2.setPort(port);
				String portDescJson = ryutopoService.getRyuSwitchPortDesc(input2.build()).get().getResult().getResult();
				switchesBuilder.setPort(portJson);
				switchesBuilder.setPortDesc(portDescJson);
				ret.add(switchesBuilder.build());
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return ret;
	}

	private List<Switches> getFloodlightSwitches(Ipv4Address ip, PortNumber port) {
		GetFloodlightSwitchesInputBuilder builder = new GetFloodlightSwitchesInputBuilder();
		builder.setIp(ip);
		builder.setPort(port);
		GetFloodlightSwitchesOutput result = null;
		List<Switches> ret = new ArrayList<>();
		try {
			result = floodlightTopoService.getFloodlightSwitches(builder.build()).get().getResult();
			String json = result.getResult();
			JsonParser parser = new JsonParser();
			JsonArray jsonArray = parser.parse(json).getAsJsonArray();
			Iterator<JsonElement> it = jsonArray.iterator();
			while (it.hasNext()) {
				JsonElement ele = it.next();
				SwitchesBuilder switchesBuilder = new SwitchesBuilder();
				DatapathId dpid = new DatapathId(ele.getAsJsonObject().get("switchDPID").getAsString());
				switchesBuilder.setDpid(dpid);
				GetFloodligtSwitchPortInputBuilder input = new GetFloodligtSwitchPortInputBuilder();
				input.setDpid(dpid);
				input.setIp(ip);
				input.setPort(port);
				String portJson = floodlightTopoService.getFloodligtSwitchPort(input.build()).get().getResult()
						.getResult();
				switchesBuilder.setPort(portJson);
				GetFloodligtSwitchPortDescInputBuilder input2 = new GetFloodligtSwitchPortDescInputBuilder();
				input2.setDpid(dpid);
				input2.setIp(ip);
				input2.setPort(port);
				String portDescJson = floodlightTopoService.getFloodligtSwitchPortDesc(input2.build()).get().getResult()
						.getResult();
				switchesBuilder.setPortDesc(portDescJson);
				ret.add(switchesBuilder.build());
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	private List<Switches> getOpendaylightSwitches(Ipv4Address ip, PortNumber port) {
		GetOpendaylightSwitchesInputBuilder builder = new GetOpendaylightSwitchesInputBuilder();
		builder.setIp(ip);
		builder.setPort(port);
		GetOpendaylightSwitchesOutput result = null;
		List<Switches> ret = new ArrayList<>();
		try {
			result = opendaylighttopoService.getOpendaylightSwitches(builder.build()).get().getResult();
			String json = result.getResult();
			JsonParser parser = new JsonParser();
			JsonArray jsonArray = parser.parse(json).getAsJsonArray();
			Iterator<JsonElement> it = jsonArray.iterator();
			while (it.hasNext()) {
				JsonElement ele = it.next();
				SwitchesBuilder switchesBuilder = new SwitchesBuilder();
				DatapathId dpid = new DatapathId(DpidUtils.getDpidFromOdlSw(ele.getAsJsonObject().get("id").getAsString()));
				switchesBuilder.setDpid(dpid);
				GetOpendaylightSwitchPortInputBuilder input = new GetOpendaylightSwitchPortInputBuilder();
				input.setDpid(dpid);
				input.setIp(ip);
				input.setPort(port);
				String portJson = opendaylighttopoService.getOpendaylightSwitchPort(input.build()).get().getResult()
						.getResult();
				switchesBuilder.setPort(portJson);
				GetOpendaylightSwitchPortDescInputBuilder input2 = new GetOpendaylightSwitchPortDescInputBuilder();
				input2.setDpid(dpid);
				input2.setIp(ip);
				input2.setPort(port);
				String portDescJson = opendaylighttopoService.getOpendaylightSwitchPortDesc(input2.build()).get().getResult()
						.getResult();
				switchesBuilder.setPortDesc(portDescJson);
				ret.add(switchesBuilder.build());
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public Future<RpcResult<GetSwitchFlowTableOutput>> getSwitchFlowTable(GetSwitchFlowTableInput input) {
		DatapathId dpid = input.getDpid();
		LOG.info("Get switch:" + dpid.getValue() + "'s flowtable");
		redisService = RedisService.getInstance();
		RedisController redisController = redisService.get(SCKey.getController, dpid.getValue(), RedisController.class);
		String flowTable = null;
		switch (redisController.getType()) {
		case Agile:
			break;
		case Apic:
			break;
		case Floodlight:
			flowTable = getFloodlightFlowTatle(redisController.getIp(), redisController.getPort(), dpid);
			break;
		case Ryu:
			flowTable = getRyuFlowTatle(redisController.getIp(), redisController.getPort(), dpid);
			break;
		case Vcf:
			break;
		default:
			break;
		}
		return RpcResultBuilder.success(new GetSwitchFlowTableOutputBuilder().setResult(flowTable).build())
				.buildFuture();
	}

	private String getRyuFlowTatle(Ipv4Address ip, PortNumber port, DatapathId dpid) {
		GetRyuSwitchFlowtableInputBuilder input = new GetRyuSwitchFlowtableInputBuilder();
		input.setIp(ip);
		input.setPort(port);
		input.setDpid(dpid);
		String ret = null;
		try {
			ret = ryuflowtableService.getRyuSwitchFlowtable(input.build()).get().getResult().getResult();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	private String getFloodlightFlowTatle(Ipv4Address ip, PortNumber port, DatapathId dpid) {
		GetFloodlightSwitchFlowtableInputBuilder input = new GetFloodlightSwitchFlowtableInputBuilder();
		input.setIp(ip);
		input.setPort(port);
		input.setDpid(dpid);
		String ret = null;
		try {
			ret = floodlightflowtableService.getFloodlightSwitchFlowtable(input.build()).get().getResult().getResult();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public Future<RpcResult<SendAlertOutput>> sendAlert(SendAlertInput input) {
		// 接受警告消息
		int alertNo = input.getAlertNo();
		MessageType type = input.getMessageType();
		MessageLevel level = input.getMessageLevel();
		String desc = input.getMessageDesc();
		String abConIp = input.getAbnormalControllerIp();

		Date time = new Date();
		DateFormat bf = new SimpleDateFormat("yyyy-MM-dd E a HH:mm:ss");
		String timeString = bf.format(time);
		SourceType sourceType = input.getSourceType();
		Ipv4Address sourceIp = input.getSourceIp();
		ReadOnlyTransaction read = dataBroker.newReadOnlyTransaction();

		InstanceIdentifier<BasicSetting> pathBasic = InstanceIdentifier.create(BasicSetting.class);
		InstanceIdentifier<AttackConfirmUrl> pathAttack = InstanceIdentifier.create(BasicSetting.class)
				.child(AttackConfirmUrl.class);
		InstanceIdentifier<ServerUrl> pathServer = InstanceIdentifier.create(BasicSetting.class).child(ServerUrl.class);

		try {
			Optional<BasicSetting> opBasic = read.read(LogicalDatastoreType.CONFIGURATION, pathBasic).get();
			Optional<AttackConfirmUrl> opAttack = read.read(LogicalDatastoreType.CONFIGURATION, pathAttack).get();
			Optional<ServerUrl> opServer = read.read(LogicalDatastoreType.CONFIGURATION, pathServer).get();

			Map<String, String> map = new HashMap<>();

			// map.put("MiddleIp", opBasic.get().getMiddleIp().getValue());
			map.put("AlertNo", String.valueOf(alertNo));
			map.put("MessageType", type.getName());
			map.put("MessageLevel", level.getName());
			map.put("MessageDesc", desc);
			map.put("MessageTime", timeString);
			map.put("SourceType", sourceType.getName());
			map.put("SourceIP", sourceIp.getValue());
			map.put("AbConIP", abConIp);

			Gson gson = new Gson();
			// 警告消息发送给用户服务平台
			String postJson = gson.toJson(map);

			if (opAttack.isPresent()) {
				String url = opAttack.get().getAttackConfirmSettingUrl();
				String response = HttpUtils.sendHttpPostJson(url, postJson);
				LOG.info("Send alert:" + postJson + " to attack confirm system");
			}
			if (opServer.isPresent()) {
				String url2 = opServer.get().getServerSettingUrl();
				String response2 = HttpUtils.sendHttpPostJson(url2, postJson);
				LOG.info("Send alert:" + postJson + " to user service system");
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return RpcResultBuilder.success(new SendAlertOutputBuilder().setResult(true).build()).buildFuture();
	}

	@Override
	public Future<RpcResult<FlowModOutput>> flowMod(FlowModInput input) {
		DatapathId dpid = input.getDpid();
		LOG.info("Send flow mod to switch:" + dpid.getValue());
		RedisService redis = RedisService.getInstance();
		RedisController redisController = redis.get(SCKey.getController, dpid.getValue(), RedisController.class);
		TypeName type = redisController.getType();
		String ret = null;
		switch (type) {
		case Agile:
			break;
		case Apic:
			break;
		case Floodlight:
			ret = floodlightFlowMod(redisController, input);
			break;
		case Ryu:
			ret = ryuFlowMod(redisController, input);
			break;
		case Vcf:
			break;
		}
		return RpcResultBuilder.success(new FlowModOutputBuilder().setResult(ret).build()).buildFuture();
	}
	private String opendaylightFlowMod(RedisController redisController, FlowModInput input) {
		String ret = "";
		FlowInput flowInput = input.getFlowInput();
		if (input.getDpid() == null) {
			return "fail";
		}

		Map<String, String> flow = new HashMap<String, String>();
		if (flowInput != null) {
			if (flowInput.getPriority() != null)
				flow.put("priority", flowInput.getPriority().toString());
			if (flowInput.getIdleTimeout() != null)
				flow.put("idle_timeout", flowInput.getIdleTimeout().toString());
			if (flowInput.getHardTimeout() != null)
				flow.put("hard_timeout", flowInput.getHardTimeout().toString());
			if (flowInput.getTableId() != null)
				flow.put("table_id", flowInput.getTableId().toString());
			flow.put("id","1");
		}

		Matches matchInput = flowInput.getMatches();
		Map<String, String> match = new HashMap<String, String>();
		if (matchInput != null) {
			if (matchInput.getEthSrc() != null)
				match.put("eth_src", matchInput.getEthSrc().getValue());
			if (matchInput.getEthDst() != null)
				match.put("eth_dst", matchInput.getEthDst().getValue());
			if (matchInput.getIpv4Src() != null)
				match.put("ipv4_src", matchInput.getIpv4Src().getValue());
			if (matchInput.getIpv4Dst() != null)
				flow.put("ipv4_dst", matchInput.getIpv4Dst().getValue());
			if (matchInput.getSrcPort() != null)
				flow.put("tcp_src", matchInput.getSrcPort().getValue().toString());
			if (matchInput.getDstPort() != null)
				flow.put("tcp_dst", matchInput.getDstPort().getValue().toString());
		}
		return ret;
	}

	private String floodlightFlowMod(RedisController redisController, FlowModInput input) {
		FlowInput flowInput = input.getFlowInput();
		if (input.getDpid() == null) {
			return "fail";
		}

		Map<String, String> flow = new HashMap<String, String>();
		flow.put("switch", input.getDpid().getValue());
		if (flowInput != null) {
			if (flowInput.getPriority() != null)
				flow.put("priority", flowInput.getPriority().toString());
			if (flowInput.getIdleTimeout() != null)
				flow.put("idle_timeout", flowInput.getIdleTimeout().toString());
			if (flowInput.getHardTimeout() != null)
				flow.put("hard_timeout", flowInput.getHardTimeout().toString());
			if (flowInput.getTableId() != null)
				flow.put("table", flowInput.getTableId().toString());
			if (flowInput.getFlowName() != null) {
				flow.put("name", flowInput.getFlowName());
			} else {
				flow.put("name", "flow_id"+flowNum);
				flowNum++;
			}
		}

		Matches matchInput = flowInput.getMatches();
		if (matchInput != null) {
			if (matchInput.getEthSrc() != null)
				flow.put("eth_src", matchInput.getEthSrc().getValue());
			if (matchInput.getEthDst() != null)
				flow.put("eth_dst", matchInput.getEthDst().getValue());
			if (matchInput.getIpv4Src() != null)
				flow.put("ipv4_src", matchInput.getIpv4Src().getValue());
			if (matchInput.getIpv4Dst() != null)
				flow.put("ipv4_dst", matchInput.getIpv4Dst().getValue());
			if (matchInput.getSrcPort() != null)
				flow.put("tcp_src", matchInput.getSrcPort().getValue().toString());
			if (matchInput.getDstPort() != null)
				flow.put("tcp_dst", matchInput.getDstPort().getValue().toString());
		}
		flow.put("eth_type", "0x0800");
		flow.put("active", "true");
		flow.put("ip_proto", "0x06");
		String outputAction = "output="+flowInput.getOutPutPort();
		if (flowInput.getOutPutPort() != null)
			flow.put("actions", outputAction);

		FlowModCmd cmd = flowInput.getFlowModCmd();
		String postJson = new Gson().toJson(flow);
		String url = null;
		String ret = null;
		switch (cmd) {
		case Add:
		case Modify:
			url = HttpUtils.getBasicURL(redisController.getIp().getValue(), redisController.getPort().getValue(),
					FloodlightUrls.ADD_MODIFY_FLOW);
			ret = HttpUtils.sendHttpPostJson(url, postJson);
			break;
		case Delete:

			// 暂时先全部删除
			url = HttpUtils.getBasicURL(redisController.getIp().getValue(), redisController.getPort().getValue(),
					FloodlightUrls.DELETE_ALL_FLOW.replace("{switch-id}", input.getDpid().getValue()));
//			ret = HttpUtils.sendHttpPostJson(url, postJson);
			ret = HttpUtils.sendHttpGet(url);
			break;
		}
		return ret;
	}

	private String ryuFlowMod(RedisController redisController, FlowModInput input) {
		FlowInput flowInput = input.getFlowInput();
		String dpid = "";
		if (input.getDpid() != null) {
			dpid = DpidUtils.getIntStringValueFromDpid(input.getDpid());
		}
		RyuFlow flow = new RyuFlow();
		flow.setDpid(dpid);
		if (flowInput != null) {
			if (flowInput.getHardTimeout() != null)
				flow.setHard_timeout(flowInput.getHardTimeout().toString());
			if (flowInput.getIdleTimeout() != null)
				flow.setIdle_timeout(flowInput.getIdleTimeout().toString());
			if (flowInput.getPriority() != null)
				flow.setPriority(flowInput.getPriority().toString());
			if (flowInput.getTableId() != null)
				flow.setTable_id(flowInput.getTableId().toString());
		}

		RyuMatch match = new RyuMatch();
		Matches matchInput = flowInput.getMatches();
		if (matchInput != null) {
			if (matchInput.getEthDst() != null)
				match.setDl_dst(matchInput.getEthDst().getValue());
			if (matchInput.getEthSrc() != null)
				match.setDl_src(matchInput.getEthSrc().getValue());
			if (matchInput.getIpv4Dst() != null)
				match.setNw_dst(matchInput.getIpv4Dst().getValue());
			if (matchInput.getIpv4Src() != null)
				match.setNw_src(matchInput.getIpv4Src().getValue());
			if (matchInput.getDstPort() != null)
				match.setTp_dst(matchInput.getDstPort().getValue().toString());
			if (matchInput.getSrcPort() != null)
				match.setTp_src(matchInput.getSrcPort().getValue().toString());
		}

		match.setDl_type("2048");
		match.setNw_type("6");
		flow.setMatch(match);
		List<RyuAction> actions = new ArrayList<RyuAction>();
		actions.add(new RyuAction(flowInput.getOutPutPort()));
		flow.setActions(actions);
		Gson gson = new Gson();

		String postJson = gson.toJson(flow);
		FlowModCmd cmd = flowInput.getFlowModCmd();
		String ret = null;
		String url = null;
		switch (cmd) {
		case Add:
			url = HttpUtils.getBasicURL(redisController.getIp().getValue(), redisController.getPort().getValue(),
					RyuUrls.ADD_FLOW_ENTRY);
			ret = HttpUtils.sendHttpPostJson(url, postJson);
			break;
		case Delete:
			url = HttpUtils.getBasicURL(redisController.getIp().getValue(), redisController.getPort().getValue(),
					RyuUrls.DELETE_ALL_FLOW_ENTRY);
			ret = HttpUtils.sendHttpPostJson(url, postJson);
			break;
		case Modify:
			url = HttpUtils.getBasicURL(redisController.getIp().getValue(), redisController.getPort().getValue(),
					RyuUrls.MODIFY_ALL_FLOW_ENTRY);
			ret = HttpUtils.sendHttpPostJson(url, postJson);
			break;
		default:
			break;
		}
		if (ret.equals("") || ret == null)
			ret = "success";
		return ret;
	}

}
