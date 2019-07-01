/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.impl.controller.ryu;

import java.util.concurrent.Future;

import org.opendaylight.yang.gen.v1.urn.opendaylight.ryuflowtable.rev190602.GetRyuSwitchFlowtableInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryuflowtable.rev190602.GetRyuSwitchFlowtableOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryuflowtable.rev190602.GetRyuSwitchFlowtableOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryuflowtable.rev190602.RyuFlowModInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryuflowtable.rev190602.RyuFlowModInput.Cmd;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryuflowtable.rev190602.RyuFlowModOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryuflowtable.rev190602.RyuflowtableService;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import xidian.impl.util.HttpUtils;

public class RyuFlowtableImpl implements RyuflowtableService{

	@Override
	public Future<RpcResult<GetRyuSwitchFlowtableOutput>> getRyuSwitchFlowtable(GetRyuSwitchFlowtableInput input) {
		String portUrl = RyuUrls.GET_FLOW_TABLE.replace("{dpidInt}", input.getDpid().getValue());
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(), portUrl);
		String response = HttpUtils.sendHttpGet(url);
		GetRyuSwitchFlowtableOutputBuilder builder = new GetRyuSwitchFlowtableOutputBuilder();
		builder.setResult(response);
		return RpcResultBuilder.success(builder.build()).buildFuture();
	}

	@Override
	public Future<RpcResult<RyuFlowModOutput>> ryuFlowMod(RyuFlowModInput input) {
		Cmd cmd = input.getCmd();
		switch(cmd) {
		case Add:
			break;
		case Delete:
			break;
		case Modify:
			break;
		}
		return null;
	}

}
