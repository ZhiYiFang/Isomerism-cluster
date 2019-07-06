/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.impl.controller.vcf;

import java.util.concurrent.Future;

import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.connectivitytest.rev181126.ConnectivitytestService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.connectivitytest.rev181126.IsConnectedInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.connectivitytest.rev181126.IsConnectedOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.connectivitytest.rev181126.IsConnectedOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.guard.rev150105.DatapathId;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import xidian.impl.util.HttpUtils;



public class ConnectivityTestImpl implements ConnectivitytestService{



	@Override
	public Future<RpcResult<IsConnectedOutput>> isConnected(IsConnectedInput input) {
		Ipv4Address ip = input.getIp();
		PortNumber port = input.getPort();
		DatapathId srcDpid = input.getSrcDpid();
		DatapathId dstDpid = input.getDstDpid();
		Ipv4Address srcIp = input.getSrcIp();
		Ipv4Address dstIp = input.getDstIp();
		String url = "http://" + ip.getValue() + ":" + port.getValue() + VcfUrls.IS_CONNECTED + srcDpid.getValue() + "/"
				+ srcIp.getValue() + "/" + dstDpid.getValue() + "/" + dstIp.getValue();

		String result = HttpUtils.sendHttpGet(url);
		IsConnectedOutputBuilder builder = new IsConnectedOutputBuilder();
		builder.setResult(result);
		return RpcResultBuilder.success(builder.build()).buildFuture();
	}

}
