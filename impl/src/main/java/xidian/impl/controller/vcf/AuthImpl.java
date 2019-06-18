/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.impl.controller.vcf;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.ControllerTypes.TypeName;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.Isomerism;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.IsomerismBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.IsomerismControllers;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.IsomerismControllersBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.IsomerismControllersKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.isomerism.controllers.UserBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfauth.rev181125.LoginInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfauth.rev181125.LoginOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfauth.rev181125.LoginOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfauth.rev181125.VcfauthService;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import xidian.impl.controller.vcf.message.Login;
import xidian.impl.controller.vcf.message.VCFAuthMessage;
import xidian.impl.controller.vcf.message.WithoutAuthMessage;
import xidian.impl.util.HttpUtils;

public class AuthImpl implements VcfauthService {

	private Logger LOG;
	private Gson gson;
	private JsonParser jsonParser;
	private DataBroker dataBroker;

	public AuthImpl(DataBroker dataBroker) {
		this.dataBroker = dataBroker;
	}

	public void init() {
		LOG = LoggerFactory.getLogger(AuthImpl.class);
		gson = new Gson();
		jsonParser = new JsonParser();
	}

	private String pack(LoginInput input) {
		String result = gson
				.toJson(new VCFAuthMessage(new Login(input.getUser(), input.getPassword(), input.getDomain())));
		return result;
	}

	@Override
	public Future<RpcResult<LoginOutput>> login(LoginInput input) {
		Ipv4Address ip = input.getIp();
		PortNumber port = input.getPort();
		String result = HttpUtils.sendHttpPostJson("http://" + ip.getValue() + ":" + port.getValue() + VcfUrls.LOGIN,
				pack(input));
		LoginOutputBuilder outputBuilder = new LoginOutputBuilder();
		LOG.info(result);
		String token = null;
		String result2 = "";
		// 登录成功以后
		if (result.contains("token")) {
			// 将这个控制器加入到中间件的数据库中
			token = jsonParser.parse(result).getAsJsonObject().getAsJsonObject("record").get("token").getAsString();
			WriteTransaction wot = dataBroker.newWriteOnlyTransaction();
			InstanceIdentifier<Isomerism> path = InstanceIdentifier.builder(Isomerism.class).build();
			IsomerismBuilder isomerismBuilder = new IsomerismBuilder();
			IsomerismControllersBuilder isomerismControllersBuilder = new IsomerismControllersBuilder();
			List<IsomerismControllers> controllers = new ArrayList<>();
			String ipMiddle = input.getMiddleWireIp().getValue();
			TypeName type = input.getTypeName();
			isomerismControllersBuilder.setIp(ip);
			isomerismControllersBuilder.setPort(port);
			isomerismControllersBuilder.setKey(new IsomerismControllersKey(ip));
			isomerismControllersBuilder.setMidlleWare(new Ipv4Address(ipMiddle));
			isomerismControllersBuilder.setTypeName(type);
			UserBuilder userBuilder = new UserBuilder();
			userBuilder.setName(input.getUser());
			userBuilder.setDomain(input.getDomain());
			userBuilder.setPassword(input.getPassword());
			userBuilder.setToken(token);
			isomerismControllersBuilder.setUser(userBuilder.build());
			controllers.add(isomerismControllersBuilder.build());
			isomerismBuilder.setIsomerismControllers(controllers);
			wot.merge(LogicalDatastoreType.OPERATIONAL, path, isomerismBuilder.build());
			CheckedFuture<Void, TransactionCommitFailedException> future = wot.submit();

			Futures.addCallback(future, new FutureCallback<Void>() {
				@Override
				public void onSuccess(Void result) {
					LOG.info("Add new controller {} to middle ware", ipMiddle + ":" + type);
				}

				@Override
				public void onFailure(Throwable t) {

				}
			});

			// 根据token将中间件设置成免认证用户
			result2 = HttpUtils.sendHttpPostJsonWithToken(
					"http://" + ip.getValue() + ":" + port.getValue() + VcfUrls.WIHOUR_AUTH + ipMiddle
							+ "/sdn-admin",
					gson.toJson(new WithoutAuthMessage("", ipMiddle, "sdn-admin")), token);
		}
		outputBuilder.setResult(result2);
		return RpcResultBuilder.success(outputBuilder.build()).buildFuture();
	}

}
