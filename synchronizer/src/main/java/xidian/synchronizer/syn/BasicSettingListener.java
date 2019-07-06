/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.synchronizer.syn;

import java.util.Collection;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataObjectModification;
import org.opendaylight.controller.md.sal.binding.api.DataTreeChangeListener;
import org.opendaylight.controller.md.sal.binding.api.DataTreeIdentifier;
import org.opendaylight.controller.md.sal.binding.api.DataTreeModification;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.BasicSetting;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.basic.setting.MininetSetting;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.basic.setting.RedisSetting;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

import xidian.synchronier.util.InstructionUtils;
import xidian.synchronier.util.RedisService;




public class BasicSettingListener implements DataTreeChangeListener<BasicSetting> {
	private final DataBroker dataBroker;

	ListenerRegistration<BasicSettingListener> listenerReg;

	public BasicSettingListener(DataBroker dataBroker) {
		this.dataBroker = dataBroker;
	}

	public void init() {
		InstanceIdentifier<BasicSetting> id = InstanceIdentifier.create(BasicSetting.class);
		listenerReg = dataBroker
				.registerDataTreeChangeListener(new DataTreeIdentifier(LogicalDatastoreType.CONFIGURATION, id), this);
	}

	public void close() {
		listenerReg.close();
	}

	@Override
	public void onDataTreeChanged(Collection<DataTreeModification<BasicSetting>> changes) {
		for (final DataTreeModification<BasicSetting> change : changes) {
			final DataObjectModification<BasicSetting> rootNode = change.getRootNode();
			switch (rootNode.getModificationType()) {
			case SUBTREE_MODIFIED:
			case WRITE:
				BasicSetting data = change.getRootNode().getDataAfter();
				String middleIp = data.getMiddleIp().getValue();
				
				MininetSetting mininetSetting = data.getMininetSetting();
				InstructionUtils.middleIp = middleIp;
				InstructionUtils.mininetIp = mininetSetting.getIp().getValue();
				InstructionUtils.passwd = mininetSetting.getPassword();
				
				RedisSetting redisSettings = data.getRedisSetting();
				RedisService.host = redisSettings.getIp().getValue();
				RedisService.port = redisSettings.getPort().getValue().toString();
				
				break;
			case DELETE:
				break;
			}
		}
	}

}
