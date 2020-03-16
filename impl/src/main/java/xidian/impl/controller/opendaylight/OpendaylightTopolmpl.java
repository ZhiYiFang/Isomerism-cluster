/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.impl.controller.opendaylight;

import java.util.concurrent.Future;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylighttopo.rev200301.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylighttopo.rev200301.OpendaylighttopoService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylighttopo.rev200301.GetOpendaylightHealthInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylighttopo.rev200301.GetOpendaylightHealthInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylighttopo.rev200301.GetOpendaylightHealthOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylighttopo.rev200301.GetOpendaylightHealthOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylighttopo.rev200301.GetOpendaylightLinksInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylighttopo.rev200301.GetOpendaylightLinksInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylighttopo.rev200301.GetOpendaylightLinksOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylighttopo.rev200301.GetOpendaylightLinksOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylighttopo.rev200301.GetOpendaylightSwitchesInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylighttopo.rev200301.GetOpendaylightSwitchesInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylighttopo.rev200301.GetOpendaylightSwitchesOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylighttopo.rev200301.GetOpendaylightSwitchesOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylighttopo.rev200301.GetOpendaylightSwitchPortDescInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylighttopo.rev200301.GetOpendaylightSwitchPortDescOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylighttopo.rev200301.GetOpendaylightHealthOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylighttopo.rev200301.GetOpendaylightSwitchPortInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylighttopo.rev200301.GetOpendaylightSwitchPortOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylighttopo.rev200301.GetOpendaylightSwitchPortOutputBuilder;

import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import com.google.gson.JsonParser;
import xidian.impl.util.HttpUtils;
import xidian.impl.util.DpidUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpendaylightTopolmpl implements OpendaylighttopoService {

    private Logger LOG = LoggerFactory.getLogger(OpendaylightTopolmpl.class);
    
    @Override
    public Future<RpcResult<GetOpendaylightSwitchPortOutput>> getOpendaylightSwitchPort(GetOpendaylightSwitchPortInput input) {
        String datapathid = DpidUtils.getOdlSwFromDpid(input.getDpid().getValue());

        String portUrl = OpendaylightUrls.GET_PORT.replace("{switch-id}", datapathid);
        String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(), portUrl);
        String response = HttpUtils.sendHttpGet(url);
        //先转JsonObject 再转JsonArray
        JsonObject pObject = new JsonParser().parse(response).getAsJsonObject();
        JsonArray portArray = pObject.getAsJsonArray("node");
        response = portArray.get(0).getAsJsonObject().getAsJsonArray("node-connector").toString();

        GetOpendaylightSwitchPortOutputBuilder builder = new GetOpendaylightSwitchPortOutputBuilder();
        builder.setResult(response);
        //builder.setResult(url);
        return RpcResultBuilder.success(builder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<GetOpendaylightSwitchPortDescOutput>> getOpendaylightSwitchPortDesc(
            GetOpendaylightSwitchPortDescInput input) {
        String datapathid = DpidUtils.getOdlSwFromDpid(input.getDpid().getValue());
        String portUrl = OpendaylightUrls.GET_PORT_DESC.replace("{switch-id}", datapathid);
        String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(), portUrl);
        String response = HttpUtils.sendHttpGet(url);
        //先转JsonObject 再转JsonArray
        JsonObject pObject = new JsonParser().parse(response).getAsJsonObject();
        JsonArray portArray = pObject.getAsJsonArray("node");
        response = portArray.get(0).getAsJsonObject().getAsJsonArray("node-connector").toString();

        GetOpendaylightSwitchPortDescOutputBuilder builder = new GetOpendaylightSwitchPortDescOutputBuilder();
        builder.setResult(response);
        return RpcResultBuilder.success(builder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<GetOpendaylightSwitchesOutput>> getOpendaylightSwitches(GetOpendaylightSwitchesInput input) {
        String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(),
                OpendaylightUrls.GET_SWITCHES);
        String response = HttpUtils.sendHttpGet(url);
        //先转JsonObject
        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
        jsonObject = jsonObject.getAsJsonObject("nodes");
        //再转JsonArray 加上数据头
        response = jsonObject.getAsJsonArray("node").toString();
        LOG.info("GET ODL SW");
        GetOpendaylightSwitchesOutputBuilder builder = new GetOpendaylightSwitchesOutputBuilder();
        builder.setResult(response);
        return RpcResultBuilder.success(builder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<GetOpendaylightLinksOutput>> getOpendaylightLinks(GetOpendaylightLinksInput input) {
        String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(),
                OpendaylightUrls.GET_LINKS);
        String response = HttpUtils.sendHttpGet(url);
        LOG.info("456");
        GetOpendaylightLinksOutputBuilder builder = new GetOpendaylightLinksOutputBuilder();
        builder.setResult(response);
        return RpcResultBuilder.success(builder.build()).buildFuture();
    }

    @Override
    public Future<RpcResult<GetOpendaylightHealthOutput>> getOpendaylightHealth(GetOpendaylightHealthInput input) {
        String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(),
                OpendaylightUrls.GET_HEALTH);
        try {
            String response = HttpUtils.sendHttpGet(url);
            GetOpendaylightHealthOutputBuilder builder = new GetOpendaylightHealthOutputBuilder();
            JsonParser parser = new JsonParser();
            builder.setResult(parser.parse(response).getAsJsonObject().get("healthy").getAsBoolean());
            return RpcResultBuilder.success(builder.build()).buildFuture();
        }catch (Exception e) {
            return RpcResultBuilder.success(new GetOpendaylightHealthOutputBuilder().setResult(false)).buildFuture();
        }
    }
}
