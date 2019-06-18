/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.impl.controller.floodlight;

import java.util.concurrent.Future;

import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.FloodlighttopoService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.GetFloodlightHealthInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.GetFloodlightHealthOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.GetFloodlightHealthOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.GetFloodlightLinksInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.GetFloodlightLinksOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.GetFloodlightLinksOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.GetFloodlightSwitchesInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.GetFloodlightSwitchesOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.GetFloodlightSwitchesOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.GetFloodligtSwitchPortDescInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.GetFloodligtSwitchPortDescOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.GetFloodligtSwitchPortDescOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.GetFloodligtSwitchPortInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.GetFloodligtSwitchPortOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.GetFloodligtSwitchPortOutputBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import com.google.gson.JsonParser;

import xidian.impl.util.HttpUtils;

public class FloodlightTopoImpl implements FloodlighttopoService {

	@Override
	public Future<RpcResult<GetFloodligtSwitchPortOutput>> getFloodligtSwitchPort(GetFloodligtSwitchPortInput input) {
		String portUrl = FloodlightUrls.GET_PORT.replace("{switch-id}", input.getDpid().getValue());
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(), portUrl);
		String response = HttpUtils.sendHttpGet(url);
		GetFloodligtSwitchPortOutputBuilder builder = new GetFloodligtSwitchPortOutputBuilder();
		builder.setResult(response);
		return RpcResultBuilder.success(builder.build()).buildFuture();
	}

	@Override
	public Future<RpcResult<GetFloodligtSwitchPortDescOutput>> getFloodligtSwitchPortDesc(
			GetFloodligtSwitchPortDescInput input) {
		String portUrl = FloodlightUrls.GET_PORT_DESC.replace("{switch-id}", input.getDpid().getValue());
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(), portUrl);
		String response = HttpUtils.sendHttpGet(url);
		GetFloodligtSwitchPortDescOutputBuilder builder = new GetFloodligtSwitchPortDescOutputBuilder();
		builder.setResult(response);
		return RpcResultBuilder.success(builder.build()).buildFuture();
	}

	@Override
	public Future<RpcResult<GetFloodlightSwitchesOutput>> getFloodlightSwitches(GetFloodlightSwitchesInput input) {
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(),
				FloodlightUrls.GET_SWITCHES);
		String response = HttpUtils.sendHttpGet(url);
		GetFloodlightSwitchesOutputBuilder builder = new GetFloodlightSwitchesOutputBuilder();
		builder.setResult(response);
		return RpcResultBuilder.success(builder.build()).buildFuture();
	}

	@Override
	public Future<RpcResult<GetFloodlightLinksOutput>> getFloodlightLinks(GetFloodlightLinksInput input) {
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(),
				FloodlightUrls.GET_LINKS);
		String response = HttpUtils.sendHttpGet(url);
		GetFloodlightLinksOutputBuilder builder = new GetFloodlightLinksOutputBuilder();
		builder.setResult(response);
		return RpcResultBuilder.success(builder.build()).buildFuture();
	}

	@Override
	public Future<RpcResult<GetFloodlightHealthOutput>> getFloodlightHealth(GetFloodlightHealthInput input) {
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(),
				FloodlightUrls.GET_HEALTH);
		String response = HttpUtils.sendHttpGet(url);
		GetFloodlightHealthOutputBuilder builder = new GetFloodlightHealthOutputBuilder();
		JsonParser parser = new JsonParser();
		builder.setResult(parser.parse(response).getAsJsonObject().get("healthy").getAsBoolean());
		return RpcResultBuilder.success(builder.build()).buildFuture();
	}

}
