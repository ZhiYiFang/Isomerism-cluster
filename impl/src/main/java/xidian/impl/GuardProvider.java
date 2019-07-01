/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package xidian.impl;

import java.util.Collection;
import java.util.List;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataObjectModification;
import org.opendaylight.controller.md.sal.binding.api.DataTreeChangeListener;
import org.opendaylight.controller.md.sal.binding.api.DataTreeIdentifier;
import org.opendaylight.controller.md.sal.binding.api.DataTreeModification;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.BasicSetting;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.Isomerism;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.basic.setting.MininetSetting;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.basic.setting.RedisSetting;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.IsomerismControllers;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xidian.impl.util.InstructionUtils;
import xidian.impl.util.RedisService;



public class GuardProvider implements DataTreeChangeListener<BasicSetting>{

    private static final Logger LOG = LoggerFactory.getLogger(GuardProvider.class);

    private final DataBroker dataBroker;
    ListenerRegistration<GuardProvider> listenerReg;
    
    public GuardProvider(final DataBroker dataBroker) {
        this.dataBroker = dataBroker;
    }

    /**
     * Method called when the blueprint container is created.
     */
    public void init() {
		InstanceIdentifier<BasicSetting> id = InstanceIdentifier.create(BasicSetting.class);
		listenerReg = dataBroker
				.registerDataTreeChangeListener(new DataTreeIdentifier(LogicalDatastoreType.CONFIGURATION, id), this);
    }

    /**
     * Method called when the blueprint container is destroyed.
     */
    public void close() {
    	listenerReg.close();
        LOG.info("GuardProvider Closed");
    }

	@Override
	public void onDataTreeChanged(Collection<DataTreeModification<BasicSetting>> changes) {
		for (final DataTreeModification<BasicSetting> change : changes) {
			final DataObjectModification<BasicSetting> rootNode = change.getRootNode();
			RedisSetting redisSettings = null;
			MininetSetting mininetSettings = null;
			switch (rootNode.getModificationType()) {
			case SUBTREE_MODIFIED:
			case WRITE:
				redisSettings = rootNode.getDataAfter().getRedisSetting();
				mininetSettings = rootNode.getDataAfter().getMininetSetting();
				RedisService.host = redisSettings.getIp().getValue();
				RedisService.port = redisSettings.getPort().getValue().toString();
				
				InstructionUtils.mininetIp = mininetSettings.getIp().getValue();
				InstructionUtils.passwd = mininetSettings.getPassword();
				
				break;
			case DELETE:
				break;
			}
		}		
	}
}