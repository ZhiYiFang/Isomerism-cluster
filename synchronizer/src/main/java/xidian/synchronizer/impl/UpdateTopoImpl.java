/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.synchronizer.impl;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.DataPlaneLink;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.data.plane.link.DataPlaneLinks;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.FloodlighttopoService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.RyutopoService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.updatetopo.rev190705.UpdateGetLinksOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.updatetopo.rev190705.UpdateGetLinksOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.updatetopo.rev190705.UpdatetopoService;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import xidian.synchronizer.syn.tasks.TopoTask;

public class UpdateTopoImpl implements UpdatetopoService{

	private final DataBroker dataBroker;
	private final FloodlighttopoService floodlighttopoService;
	private final RyutopoService ryutopoService;
	private final Logger LOG = LoggerFactory.getLogger(UpdateTopoImpl.class);
	
	public UpdateTopoImpl(DataBroker dataBroker, FloodlighttopoService floodlighttopoService,
			RyutopoService ryutopoService) {
		this.dataBroker = dataBroker;
		this.floodlighttopoService = floodlighttopoService;
		this.ryutopoService = ryutopoService;
	}
	
	@Override
	public Future<RpcResult<UpdateGetLinksOutput>> updateGetLinks() {
		LOG.info("Update topology information");
		// update now
		new TopoTask(dataBroker, floodlighttopoService, ryutopoService).start();
		
		// read from dataBroker
		
		ReadOnlyTransaction readLinks = dataBroker.newReadOnlyTransaction();
		InstanceIdentifier<DataPlaneLink> path = InstanceIdentifier.create(DataPlaneLink.class);
		String result = "";
		try {
			Optional<DataPlaneLink> op = readLinks.read(LogicalDatastoreType.CONFIGURATION, path).get();
			if(op.isPresent()) {
				List<DataPlaneLinks> links = op.get().getDataPlaneLinks();
				result = links.toString();
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return RpcResultBuilder.success(new UpdateGetLinksOutputBuilder().setReault(result).build()).buildFuture();
	}

}
