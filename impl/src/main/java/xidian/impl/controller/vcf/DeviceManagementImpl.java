/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.impl.controller.vcf;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.ConfigPortStatusInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.ConfigPortStatusOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.ConfigPortStatusOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.DevicemanagementService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.GetDeviceFlowTableInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.GetDeviceFlowTableOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.GetDeviceFlowTableOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.GetDeviceMatchAbilityInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.GetDeviceMatchAbilityOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.GetDeviceMatchAbilityOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.GetDeviceRoleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.GetDeviceRoleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.GetDeviceRoleOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.GetDevicesInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.GetDevicesOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.GetDevicesOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.GetGatewaysInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.GetGatewaysOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.GetGatewaysOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.GetPortsInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.GetPortsOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.GetPortsOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.VcfFlowModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.VcfFlowModOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.VcfFlowModOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.flow.basic.FlowModFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.vcf.flow.mod.input.FlowInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.vcf.flow.mod.input.flow.input.Instructions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.vcf.flow.mod.input.flow.input.Matches;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.vcf.flow.mod.input.flow.input.instructions.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.vcf.flow.mod.input.flow.input.instructions.action.Apply;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.vcf.flow.mod.input.flow.input.instructions.action.Other;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.vcf.flow.mod.input.flow.input.instructions.action.apply.ApplyActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.guard.rev150105.DatapathId;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import com.google.gson.Gson;

import xidian.impl.controller.vcf.message.FlowMessage;
import xidian.impl.controller.vcf.message.VcfFlow;
import xidian.impl.controller.vcf.message.VcfInstruction;
import xidian.impl.controller.vcf.message.VcfMatch;
import xidian.impl.controller.vcf.message.Actions.ApplyAction;
import xidian.impl.util.HttpUtils;

public class DeviceManagementImpl implements DevicemanagementService {

	private Gson gson;

	public void init() {
		gson = new Gson();
	}

	@Override
	public Future<RpcResult<GetDeviceFlowTableOutput>> getDeviceFlowTable(GetDeviceFlowTableInput input) {
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(), VcfUrls.GET_FLOWS);
		DatapathId dpid = input.getDpid();
		Ipv4Address switchIp = input.getSwitchIp();
		Long switchPort = input.getPortId();
		Integer tableId = input.getTableId();
		Ipv4Address srcIp = input.getSrcIp();
		Ipv4Address dstIp = input.getDstIp();
		MacAddress srcMac = input.getSrcMac();
		MacAddress dstMac = input.getDstMac();
		String srcIpmask = input.getSrcIpmask();
		String dstIpmask = input.getDstIpmask();
		Integer vlan = input.getVlan();
		String ethType = input.getEthType();
		if (dpid != null) {
			url = url + "?" + "dpid=" + dpid.getValue();
		}
		if (switchIp != null) {
			url = url.contains("?") ? url + "&switch_ip=" + switchIp.getValue()
					: url + "?" + "switch_ip=" + switchIp.getValue();
		}
		// 前两个判断以后会有一个条件了
		if (switchPort != null) {
			url = url + "&port=" + switchPort.longValue();
		}
		if (tableId != null) {
			url = url + "&table_id=" + tableId.intValue();
		}
		if (srcIp != null) {
			url = url + "&src_ip=" + srcIp.getValue();
		}
		if (dstIp != null) {
			url = url + "&dst_ip=" + dstIp.getValue();
		}
		if (srcMac != null) {
			url = url + "&src_mac=" + srcMac.getValue();
		}
		if (dstMac != null) {
			url = url + "&dst_mac=" + dstMac.getValue();
		}
		if (srcIpmask != null) {
			url = url + "&src_ipmask=" + srcIpmask;
		}
		if (dstIpmask != null) {
			url = url + "&dst_ipmaks=" + dstIpmask;
		}
		if (vlan != null) {
			url = url + "&vlan=" + vlan.intValue();
		}
		if (ethType != null) {
			url = url + "&eth_type=" + ethType;
		}
		String result = HttpUtils.sendHttpGet(url);
		return RpcResultBuilder.success(new GetDeviceFlowTableOutputBuilder().setResult(result).build()).buildFuture();
	}

	@Override
	public Future<RpcResult<GetDeviceMatchAbilityOutput>> getDeviceMatchAbility(GetDeviceMatchAbilityInput input) {
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(),
				VcfUrls.GET_DEVICE_MATCH_ABILITY);
		DatapathId dpid = input.getDpid();
		url = url + dpid.getValue() + "/features/match";
		String reuslt = HttpUtils.sendHttpGet(url);
		return RpcResultBuilder.success(new GetDeviceMatchAbilityOutputBuilder().setResult(reuslt).build())
				.buildFuture();
	}

	@Override
	public Future<RpcResult<GetPortsOutput>> getPorts(GetPortsInput input) {
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(),
				VcfUrls.GET_GATEWAYS + input.getDpid().getValue() + "/ports");
		Long portId = input.getPortId();
		if (portId != null) {
			url = url + "/" + portId.longValue();
		}
		String result = HttpUtils.sendHttpGet(url);
		return RpcResultBuilder.success(new GetPortsOutputBuilder().setResult(result).build()).buildFuture();
	}

	@Override
	public Future<RpcResult<GetGatewaysOutput>> getGateways(GetGatewaysInput input) {
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(), VcfUrls.GET_GATEWAYS);
		DatapathId dpid = input.getDpid();
		if (dpid != null) {
			url = url + "dpid=" + dpid.getValue();
		}
		String result = HttpUtils.sendHttpGet(url);
		return RpcResultBuilder.success(new GetGatewaysOutputBuilder().setResult(result).build()).buildFuture();
	}

	@Override
	public Future<RpcResult<GetDevicesOutput>> getDevices(GetDevicesInput input) {
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(),
				VcfUrls.GET_OPENFLOW_DEVICES);
		DatapathId dpid = input.getDpid();
		if (dpid != null) {
			url = url + "?" + "dpid=" + dpid.getValue();
		}
		String result = HttpUtils.sendHttpGet(url);
		return RpcResultBuilder.success(new GetDevicesOutputBuilder().setResult(result).build()).buildFuture();
	}

	@Override
	public Future<RpcResult<ConfigPortStatusOutput>> configPortStatus(ConfigPortStatusInput input) {
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(), VcfUrls.GET_GATEWAYS
				+ input.getDpid().getValue() + "/ports/" + input.getPortId().longValue() + "/action");
		String result = HttpUtils.sendHttpPostJson(url, input.getPortAction().getName());
		return RpcResultBuilder.success(new ConfigPortStatusOutputBuilder().setResult(result).build()).buildFuture();
	}

	@Override
	public Future<RpcResult<GetDeviceRoleOutput>> getDeviceRole(GetDeviceRoleInput input) {
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(),
				VcfUrls.GET_GATEWAYS + input.getDpid().getValue() + "/controllers/");
		String result = HttpUtils.sendHttpGet(url);
		return RpcResultBuilder.success(new GetDeviceRoleOutputBuilder().setResult(result).build()).buildFuture();
	}

	@Override
	public Future<RpcResult<VcfFlowModOutput>> vcfFlowMod(VcfFlowModInput input) {
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(),
				VcfUrls.GET_DEVICE_MATCH_ABILITY + input.getDpid().getValue() + "/flows");
		FlowMessage flowMessage = new FlowMessage();
		flowMessage.setVersion("1.3.0");

		// 构造VcfFlow
		// vcfFlow基本项
		FlowInput flowInput = input.getFlowInput();
		VcfFlow vcfFlow = new VcfFlow();
		vcfFlow.setPriority(flowInput.getPriority().toString());
		vcfFlow.setFlow_mod_cmd(flowInput.getFlowModCmd().getName());
		vcfFlow.setTable_id(flowInput.getTableId().toString());
		vcfFlow.setIdle_timeout(flowInput.getIdleTimeout().toString());
		vcfFlow.setHard_timeout(flowInput.getHardTimeout().toString());
		List<String> flags = new ArrayList<>();
		if (flowInput.getFlowModFlags() != null) {
			for (FlowModFlags flowFlag : flowInput.getFlowModFlags()) {
				flags.add(flowFlag.getFlowModeFlags());
			}

		}
		vcfFlow.setFlow_mod_flags(flags);
		// vcfFlow Match项
		Matches m = flowInput.getMatches();
		List<VcfMatch> match = new ArrayList<>();
		if(m.getEthType()!=null) {
			match.add(new VcfMatch(m.getEthType(), null, null, null, null, null, null, null));
		}
		if(m.getEthSrc()!=null) {
			match.add(new VcfMatch(null, m.getEthSrc().getValue(), null, null, null, null, null, null));
		}
		if(m.getEthDst()!=null) {
			match.add(new VcfMatch(null,null, m.getEthDst().getValue(), null, null, null, null, null));
		}
		if(m.getIpv4Src()!=null) {
			match.add(new VcfMatch(null, null, null, m.getIpv4Src().getValue(), null, null, null, null));
		}
		if(m.getIpv4Dst()!=null) {
			match.add(new VcfMatch(null, null, null, null, m.getIpv4Dst().getValue(), null, null, null));
		}
		if(m.getIpProto()!=null) {
			match.add(new VcfMatch(null, null, null, null, null, m.getIpProto().getName(), null, null));
		}
		if(m.getSrcPort()!=null) {
			match.add(new VcfMatch(null, null, null, null, null, null, m.getSrcPort().getValue().toString(), null));
		}
		if(m.getDstPort() !=null) {
			match.add(new VcfMatch(null, null, null, null, null, null, null, m.getDstPort().getValue().toString()));
		}
		vcfFlow.setMatch(match);
		// vcf instructions项
		List<VcfInstruction> instructions = new ArrayList<>();
		List<Instructions> intructionsInput = flowInput.getInstructions();
		for (Instructions in : intructionsInput) {
			VcfInstruction vcfInstruction = new VcfInstruction();
			Action action = in.getAction();
			List<ApplyAction> appAs = new ArrayList<>();
			if (action instanceof Apply) {
				appAs = new ArrayList<>();
				List<ApplyActions> applyActions = ((Apply) action).getApplyActions();
				for (ApplyActions ac : applyActions) {
					String outputPort = ac.getOutput();
					appAs.add(new ApplyAction(outputPort));
				}
			} else if (action instanceof Other) {
				// ...
			}
			vcfInstruction.setApply_actions(appAs);
			instructions.add(vcfInstruction);
		}
		vcfFlow.setInstructions(instructions);
		flowMessage.setFlow(vcfFlow);
		String json = gson.toJson(flowMessage);
		String result = HttpUtils.sendHttpPostJson(url, json);
		return RpcResultBuilder.success(new VcfFlowModOutputBuilder().setResult(result).build()).buildFuture();
	}

}
