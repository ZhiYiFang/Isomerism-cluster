/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.impl.controller.ryu;

import java.util.concurrent.Future;

import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuHealthInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuHealthOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuHealthOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuLinksInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuLinksOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuLinksOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuSwitchPortDescInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuSwitchPortDescOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuSwitchPortDescOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuSwitchPortInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuSwitchPortOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuSwitchPortOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuSwitchesInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuSwitchesListInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuSwitchesListOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuSwitchesListOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuSwitchesOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuSwitchesOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.RyutopoService;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import xidian.impl.util.DpidUtils;
import xidian.impl.util.HttpUtils;


public class RyuTopoImpl implements RyutopoService {

	@Override
	public Future<RpcResult<GetRyuSwitchPortOutput>> getRyuSwitchPort(GetRyuSwitchPortInput input) {
		
		String intDpid = DpidUtils.getIntStringValueFromDpid(input.getDpid());
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(),
				RyuUrls.GET_PORT.replace("{dpidInt}", intDpid));
		String response = HttpUtils.sendHttpGet(url);
		GetRyuSwitchPortOutputBuilder builder = new GetRyuSwitchPortOutputBuilder();
		builder.setResult(response);
		return RpcResultBuilder.success(builder.build()).buildFuture();
		
	}

	@Override
	public Future<RpcResult<GetRyuSwitchPortDescOutput>> getRyuSwitchPortDesc(GetRyuSwitchPortDescInput input) {
		String intDpid = DpidUtils.getIntStringValueFromDpid(input.getDpid());
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(),
				RyuUrls.GET_PORT_DESC.replace("{dpidInt}", intDpid));
		String response = HttpUtils.sendHttpGet(url);
		GetRyuSwitchPortDescOutputBuilder builder = new GetRyuSwitchPortDescOutputBuilder();
		builder.setResult(response);
		return RpcResultBuilder.success(builder.build()).buildFuture();
	}

	@Override
	public Future<RpcResult<GetRyuSwitchesOutput>> getRyuSwitches(GetRyuSwitchesInput input) {
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(), RyuUrls.GET_SWITCHES);
		String response = HttpUtils.sendHttpGet(url);
		GetRyuSwitchesOutputBuilder builder = new GetRyuSwitchesOutputBuilder();
		builder.setResult(response);
		return RpcResultBuilder.success(builder.build()).buildFuture();
	}

	@Override
	public Future<RpcResult<GetRyuLinksOutput>> getRyuLinks(GetRyuLinksInput input) {
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(), RyuUrls.GET_LINKS);
		String response = HttpUtils.sendHttpGet(url);
		GetRyuLinksOutputBuilder builder = new GetRyuLinksOutputBuilder();
		builder.setResult(response);
		return RpcResultBuilder.success(builder.build()).buildFuture();
	}

	@Override
	public Future<RpcResult<GetRyuSwitchesListOutput>> getRyuSwitchesList(GetRyuSwitchesListInput input) {
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(),
				RyuUrls.GET_SWITCHES_LIST);
		String response = HttpUtils.sendHttpGet(url);

		GetRyuSwitchesListOutputBuilder builder = new GetRyuSwitchesListOutputBuilder();
		builder.setResult(response);
		return RpcResultBuilder.success(builder.build()).buildFuture();
	}

	@Override
	public Future<RpcResult<GetRyuHealthOutput>> getRyuHealth(GetRyuHealthInput input) {
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(), RyuUrls.GET_SWITCHES);
		String response = HttpUtils.sendHttpGet(url);
		GetRyuHealthOutputBuilder builder = new GetRyuHealthOutputBuilder();
		builder.setResult(response.contains("["));
		return RpcResultBuilder.success(builder.build()).buildFuture();
	}

}
