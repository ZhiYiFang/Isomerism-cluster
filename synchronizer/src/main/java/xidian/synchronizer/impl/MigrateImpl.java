/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package xidian.synchronizer.impl;

import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.Isomerism;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.IsomerismControllers;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.IsomerismControllersBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.IsomerismControllersKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.IsomerismControllers.ControllerStatus;
import org.opendaylight.yang.gen.v1.urn.opendaylight.migrate.rev190726.MigrateAbnormalControllerInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.migrate.rev190726.MigrateAbnormalControllerOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.migrate.rev190726.MigrateService;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.binding.KeyedInstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;

public class MigrateImpl implements MigrateService {
	private final DataBroker dataBroker;

	public MigrateImpl(DataBroker dataBroker) {
		this.dataBroker = dataBroker;
	}

	@Override
	public Future<RpcResult<MigrateAbnormalControllerOutput>> migrateAbnormalController(
			MigrateAbnormalControllerInput input) {
		Ipv4Address controllerIp = input.getControllerIp();
		WriteTransaction writeControllerStatus = dataBroker.newWriteOnlyTransaction();
		
		KeyedInstanceIdentifier<IsomerismControllers, IsomerismControllersKey> statusPath = InstanceIdentifier
				.create(Isomerism.class).child(IsomerismControllers.class, new IsomerismControllersKey(controllerIp));
		
		// 改变控制器的状态
		IsomerismControllersBuilder builder = new IsomerismControllersBuilder();
		builder.setIp(controllerIp);
		builder.setKey(new IsomerismControllersKey(controllerIp));
		builder.setControllerStatus(ControllerStatus.Abnormal);
		writeControllerStatus.merge(LogicalDatastoreType.CONFIGURATION, statusPath, builder.build());
		writeControllerStatus.submit();
		
		// 迁移控制器的
		return null;
	}
}
