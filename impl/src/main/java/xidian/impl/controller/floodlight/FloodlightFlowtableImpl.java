/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.impl.controller.floodlight;

import java.util.concurrent.Future;

import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightflowtable.rev190602.FloodlightflowtableService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightflowtable.rev190602.GetFloodlightSwitchFlowtableInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightflowtable.rev190602.GetFloodlightSwitchFlowtableOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightflowtable.rev190602.GetFloodlightSwitchFlowtableOutputBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import xidian.impl.util.HttpUtils;

public class FloodlightFlowtableImpl implements FloodlightflowtableService {

	@Override
	public Future<RpcResult<GetFloodlightSwitchFlowtableOutput>> getFloodlightSwitchFlowtable(
			GetFloodlightSwitchFlowtableInput input) {
		String portUrl = FloodlightUrls.GET_FLOW_TABLE.replace("{switch-id}", input.getDpid().getValue());
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(), portUrl);
		String response = HttpUtils.sendHttpGet(url);
		GetFloodlightSwitchFlowtableOutputBuilder builder = new GetFloodlightSwitchFlowtableOutputBuilder();
		builder.setResult(response);
		return RpcResultBuilder.success(builder.build()).buildFuture();
	}
}
