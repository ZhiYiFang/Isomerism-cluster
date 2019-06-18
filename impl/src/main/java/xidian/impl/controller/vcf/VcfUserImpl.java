/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.impl.controller.vcf;

import java.util.concurrent.Future;

import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfuser.rev181126.AddUserInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfuser.rev181126.AddUserOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfuser.rev181126.AddUserOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfuser.rev181126.ChangePasswordInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfuser.rev181126.ChangePasswordOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfuser.rev181126.ChangePasswordOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfuser.rev181126.DeleteUserInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfuser.rev181126.DeleteUserOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfuser.rev181126.DeleteUserOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfuser.rev181126.GetUsersInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfuser.rev181126.GetUsersOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfuser.rev181126.GetUsersOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfuser.rev181126.VcfuserService;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import com.google.gson.Gson;

import xidian.impl.controller.vcf.message.AddUserMessage;
import xidian.impl.controller.vcf.message.ChangePasswordMessage;
import xidian.impl.util.HttpUtils;

public class VcfUserImpl implements VcfuserService {
	private Gson gson;

	public void init() {
		gson = new Gson();
	}

	@Override
	public Future<RpcResult<GetUsersOutput>> getUsers(GetUsersInput input) {
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(), VcfUrls.GET_USER);
		String result = HttpUtils.sendHttpGet(url);
		return RpcResultBuilder.success(new GetUsersOutputBuilder().setResult(result).build()).buildFuture();
	}

	@Override
	public Future<RpcResult<DeleteUserOutput>> deleteUser(DeleteUserInput input) {
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(),
				VcfUrls.DELETE_USER + input.getUser());
		String result = HttpUtils.sendHttpDelete(url);
		return RpcResultBuilder.success(new DeleteUserOutputBuilder().setResult(result).build()).buildFuture();
	}

	@Override
	public Future<RpcResult<AddUserOutput>> addUser(AddUserInput input) {
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(), VcfUrls.ADD_USER);
		AddUserMessage addUserMessage = new AddUserMessage(input.getUser(), input.getPassword(), input.getRole().getName());
		String json = gson.toJson(addUserMessage);
		String result = HttpUtils.sendHttpPostJson(url, json);
		return RpcResultBuilder.success(new AddUserOutputBuilder().setResult(result).build()).buildFuture();
	}

	@Override
	public Future<RpcResult<ChangePasswordOutput>> changePassword(ChangePasswordInput input) {
		String url = HttpUtils.getBasicURL(input.getIp().getValue(), input.getPort().getValue(), VcfUrls.CHANGE_USER);
		String json = gson
				.toJson(new ChangePasswordMessage(input.getUser(), input.getOldPassword(), input.getNewPassword()));
		String result = HttpUtils.sendHttpPutJson(url, json);
		return RpcResultBuilder.success(new ChangePasswordOutputBuilder().setResult(result).build()).buildFuture();
	}
}
