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
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.Isomerism;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.FloodlighttopoService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.RyutopoService;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

import xidian.synchronizer.syn.tasks.TopoTask;

public class TopologyChangeListener implements DataTreeChangeListener<Isomerism> {

	private final DataBroker dataBroker;
	private final FloodlighttopoService floodlighttopoService;
	private final RyutopoService ryutopoService;
	ListenerRegistration<TopologyChangeListener> listenerReg;

	public TopologyChangeListener(DataBroker dataBroker, FloodlighttopoService floodlighttopoService,
			RyutopoService ryutopoService) {
		this.dataBroker = dataBroker;
		this.floodlighttopoService = floodlighttopoService;
		this.ryutopoService = ryutopoService;
	}

	public void init() {
		InstanceIdentifier<Isomerism> id = InstanceIdentifier.create(Isomerism.class);
		listenerReg = dataBroker
				.registerDataTreeChangeListener(new DataTreeIdentifier(LogicalDatastoreType.CONFIGURATION, id), this);
	}

	public void close() {
		listenerReg.close();
	}

	@Override
	public void onDataTreeChanged(Collection<DataTreeModification<Isomerism>> changes) {
		for (final DataTreeModification<Isomerism> change : changes) {
			final DataObjectModification<Isomerism> rootNode = change.getRootNode();
			switch (rootNode.getModificationType()) {
			case SUBTREE_MODIFIED:
				new TopoTask(dataBroker, floodlighttopoService, ryutopoService).start();;
				break;
			case WRITE:
				// 刚创建的时候需要汇聚一下拓扑
				TopoTask topoTask = new TopoTask(dataBroker, floodlighttopoService, ryutopoService);
				topoTask.start();
				break;
			case DELETE:
				break;
			}
		}
	}
}
