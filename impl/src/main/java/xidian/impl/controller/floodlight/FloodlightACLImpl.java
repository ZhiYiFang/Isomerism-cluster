/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.impl.controller.floodlight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightacl.rev190713.GetFloodlightaclInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightacl.rev190713.GetFloodlightaclOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightacl.rev190713.GetFloodlightaclOutputBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
		map.put("nw-proto", nwproto == null ? "" : nwproto.getName());
		map.put("src-ip", srcIp == null ? "" : srcIp.getValue());
		map.put("dst-ip", dstIp == null ? "" : dstIp.getValue());
		map.put("tp-dst", port == null ? "" : port.getValue().toString());
		map.put("action", action == null ? "" : action.getName());
		String response = HttpUtils.sendHttpPostJson(HttpUtils.getBasicURL(input.getControllerIp().getValue(),
				input.getControllerPort().getValue(), FloodlightUrls.ADD_ACL), gson.toJson(map));
		return RpcResultBuilder.success(new AddFloodlightAclOutputBuilder().setResult(response)).buildFuture();
	}

	@Override
	public Future<RpcResult<GetFloodlightaclOutput>> getFloodlightacl(GetFloodlightaclInput input) {
		Ipv4Address ip = input.getControllerIp();
		PortNumber port = input.getControllerPort();
		String response = HttpUtils
				.sendHttpGet(HttpUtils.getBasicURL(ip.getValue(), port.getValue(), FloodlightUrls.GET_ACL));
		return RpcResultBuilder.success(new GetFloodlightaclOutputBuilder().setResult(response)).buildFuture();
		

	}

	private String getValue(String key, JsonObject obj) {
		JsonElement ele = obj.get(key);
		return ele == null ? "" : ele.getAsString();
	}
}
