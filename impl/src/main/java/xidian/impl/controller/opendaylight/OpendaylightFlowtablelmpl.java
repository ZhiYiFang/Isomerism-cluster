/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.impl.controller.opendaylight;

import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightflowtable.rev190602.GetFloodlightSwitchFlowtableInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightflowtable.rev190602.GetFloodlightSwitchFlowtableOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightflowtable.rev190602.GetFloodlightSwitchFlowtableOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylightflowtable.rev200301.OpendaylightflowtableService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylightflowtable.rev200301.GetOpendaylightSwitchFlowtableInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylightflowtable.rev200301.GetOpendaylightSwitchFlowtableInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylightflowtable.rev200301.GetOpendaylightSwitchFlowtableOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylightflowtable.rev200301.GetOpendaylightSwitchFlowtableOutputBuilder;

import java.util.concurrent.Future;

import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import xidian.impl.controller.opendaylight.OpendaylightUrls;
import xidian.impl.util.DpidUtils;
import xidian.impl.util.HttpUtils;

public class OpendaylightFlowtablelmpl implements OpendaylightflowtableService {

    @Override
    public Future<RpcResult<GetOpendaylightSwitchFlowtableOutput>> getOpendaylightSwitchFlowtable(GetOpendaylightSwitchFlowtableInput input) {
        String datapathid = DpidUtils.getOdlSwFromDpid(input.getDpid().getValue());
        String portUrl = OpendaylightUrls.GET_FLOW_TABLE.replace("{switch-id}", datapathid);
        String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(), portUrl);
        String response = HttpUtils.sendHttpGet(url);
        GetOpendaylightSwitchFlowtableOutputBuilder builder = new GetOpendaylightSwitchFlowtableOutputBuilder();
        builder.setResult(response);
        return RpcResultBuilder.success(builder.build()).buildFuture();
    }
}
