/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.impl.controller.vcf;

import java.util.concurrent.Future;

import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfstatistics.rev181126.GetVcfControllersStatusInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfstatistics.rev181126.GetVcfControllersStatusOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfstatistics.rev181126.GetVcfControllersStatusOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfstatistics.rev181126.GetVcfMeterInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfstatistics.rev181126.GetVcfMeterOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfstatistics.rev181126.GetVcfMeterOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfstatistics.rev181126.GetVcfPortStatusInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfstatistics.rev181126.GetVcfPortStatusOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfstatistics.rev181126.GetVcfPortStatusOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfstatistics.rev181126.VcfstatisticsService;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import xidian.impl.util.HttpUtils;


public class StatisticsImpl implements VcfstatisticsService {

	@Override
	public Future<RpcResult<GetVcfPortStatusOutput>> getVcfPortStatus(GetVcfPortStatusInput input) {
		String ip = input.getIp().getValue();
		int port = input.getPort().getValue();
		String portId = input.getPortId();
		String dpid = input.getDpid().getValue();
		String url = HttpUtils.getBasicURL(ip, port, VcfUrls.PORT_STATIC);
		
		if (portId == null && dpid == null) {
		} else if (portId == null && dpid != null) {
			url = url+"?"+"dpid="+dpid;
		} else if (portId != null && dpid == null) {
			url = url+"?"+"port_id="+portId;
		} else {
			url = url +"?"+"dpid="+dpid+"&"+"port_id="+portId;
		}
		
		String result = HttpUtils.sendHttpGet(url);
		GetVcfPortStatusOutputBuilder builder = new GetVcfPortStatusOutputBuilder();
		builder.setResult(result);
		return RpcResultBuilder.success(builder.build()).buildFuture();
	}

	@Override
	public Future<RpcResult<GetVcfMeterOutput>> getVcfMeter(GetVcfMeterInput input) {
		String ip = input.getIp().getValue();
		int port = input.getPort().getValue();
		String merterId = input.getMeterId();
		String dpid = input.getDpid().getValue();
		String url = HttpUtils.getBasicURL(ip, port, VcfUrls.METER_STATIC);
		
		if(merterId == null && dpid == null) {
			
		} else if (merterId != null && dpid == null) {
			url = url+"?"+"meterid="+merterId;
		} else if (merterId == null && dpid != null) {
			url = url+"?"+"dpid="+dpid;
		} else {
			url = url+"?"+"dpid="+dpid+"&"+"meterid="+merterId;
		}
		String result = HttpUtils.sendHttpGet(url);
		GetVcfMeterOutputBuilder builder = new GetVcfMeterOutputBuilder();
		return RpcResultBuilder.success(builder.setResult(result).build()).buildFuture();
	}

	@Override
	public Future<RpcResult<GetVcfControllersStatusOutput>> getVcfControllersStatus(
			GetVcfControllersStatusInput input) {
		String result = HttpUtils.sendHttpGet(
				"http://" + input.getIp().getValue() + ":" + input.getPort().getValue() + VcfUrls.CONTROLLER_STATIC);
		GetVcfControllersStatusOutputBuilder builder = new GetVcfControllersStatusOutputBuilder();
		builder.setResult(result);
		return RpcResultBuilder.success(builder.build()).buildFuture();
	}

}
