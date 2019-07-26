/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.impl.controller.floodlight;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Prefix;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightacl.rev190713.Acl.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightacl.rev190713.Acl.NwProto;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightacl.rev190713.AddFloodlightAclInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightacl.rev190713.AddFloodlightAclOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightacl.rev190713.AddFloodlightAclOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightacl.rev190713.FloodlightaclService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightacl.rev190713.GetFloodlightAclInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightacl.rev190713.GetFloodlightAclOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightacl.rev190713.GetFloodlightAclOutputBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import com.google.gson.Gson;

import xidian.impl.util.HttpUtils;

public class FloodlightACLImpl implements FloodlightaclService {
	private Gson gson = new Gson();

	@Override
	public Future<RpcResult<AddFloodlightAclOutput>> addFloodlightAcl(AddFloodlightAclInput input) {
		NwProto nwproto = input.getNwProto();
		Ipv4Prefix srcIp = input.getSrcIp();
		Ipv4Prefix dstIp = input.getDstIp();
		PortNumber port = input.getTpDst();
		Action action = input.getAction();
		Map<String, String> map = new HashMap<>();
		if (nwproto != null) {
			map.put("nw-proto", nwproto.getName());
		}
		if (srcIp != null) {
			map.put("src-ip", srcIp.getValue());
		}
		if (dstIp != null) {
			map.put("dst-ip", dstIp.getValue());
		}
		if (port != null) {
			map.put("tp-dst", port.getValue().toString());
		}
		if (action != null) {
			map.put("action", action.getName());
		}
		String response = HttpUtils.sendHttpPostJson(HttpUtils.getBasicURL(input.getControllerIp().getValue(),
				input.getControllerPort().getValue(), FloodlightUrls.ADD_ACL), gson.toJson(map));
		return RpcResultBuilder.success(new AddFloodlightAclOutputBuilder().setResult(response)).buildFuture();
	}

	@Override
	public Future<RpcResult<GetFloodlightAclOutput>> getFloodlightAcl(GetFloodlightAclInput input) {
		Ipv4Address ip = input.getControllerIp();
		PortNumber port = input.getControllerPort();
		String response = HttpUtils
				.sendHttpGet(HttpUtils.getBasicURL(ip.getValue(), port.getValue(), FloodlightUrls.GET_ACL));
		return RpcResultBuilder.success(new GetFloodlightAclOutputBuilder().setResult(response)).buildFuture();
	}



}
