/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.impl.controller.vcf;

import java.util.concurrent.Future;

import org.opendaylight.yang.gen.v1.urn.opendaylight.linkmanager.rev181126.GetVcfLinkInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.linkmanager.rev181126.GetVcfLinkOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.linkmanager.rev181126.GetVcfLinkOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.linkmanager.rev181126.GetVcfTunnelStaticsInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.linkmanager.rev181126.GetVcfTunnelStaticsOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.linkmanager.rev181126.GetVcfTunnelStaticsOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.linkmanager.rev181126.LinkmanagerService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.guard.rev150105.DatapathId;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import xidian.impl.util.HttpUtils;



public class LinkManagerImpl implements LinkmanagerService {

	@Override
	public Future<RpcResult<GetVcfTunnelStaticsOutput>> getVcfTunnelStatics(GetVcfTunnelStaticsInput input) {
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(), VcfUrls.TUNNEL_STATUS);
		DatapathId dpid = input.getDpid();
		if (dpid != null) {
			url = url + "?" + "dpid=" + dpid.getValue();
		}
		String result = HttpUtils.sendHttpGet(url);
		GetVcfTunnelStaticsOutputBuilder builder = new GetVcfTunnelStaticsOutputBuilder();
		builder.setResult(result);
		return RpcResultBuilder.success(builder.build()).buildFuture();
	}

	@Override
	public Future<RpcResult<GetVcfLinkOutput>> getVcfLink(GetVcfLinkInput input) {
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(), VcfUrls.LINK_STATUS);
		DatapathId dpid = input.getDpid();
		if (dpid != null) {
			url = url + "?" + "dpid=" + dpid.getValue();
		}
		String result = HttpUtils.sendHttpGet(url);
		return RpcResultBuilder.success(new GetVcfLinkOutputBuilder().setResult(result).build()).buildFuture();
	}

}
