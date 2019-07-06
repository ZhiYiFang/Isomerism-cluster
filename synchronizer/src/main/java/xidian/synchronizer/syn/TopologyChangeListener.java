/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.synchronizer.syn;

import java.util.Collection;
import java.util.List;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataObjectModification;
import org.opendaylight.controller.md.sal.binding.api.DataTreeChangeListener;
import org.opendaylight.controller.md.sal.binding.api.DataTreeIdentifier;
import org.opendaylight.controller.md.sal.binding.api.DataTreeModification;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.Isomerism;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.IsomerismControllers;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.FloodlighttopoService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.role.service.rev150727.SalRoleService;
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
				// 如果有控制器的拓扑变化也要汇聚一下拓扑
				Isomerism before = change.getRootNode().getDataBefore();
				Isomerism after = change.getRootNode().getDataAfter();
				List<IsomerismControllers> beforeControllers = before.getIsomerismControllers();
				List<IsomerismControllers> afterControllers = after.getIsomerismControllers();

				// 多个异构控制器需要重新汇聚拓扑
				if (beforeControllers.size() != afterControllers.size()) {
					TopoTask topoTask = new TopoTask(dataBroker, floodlighttopoService, ryutopoService);
					topoTask.start();
				}

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
