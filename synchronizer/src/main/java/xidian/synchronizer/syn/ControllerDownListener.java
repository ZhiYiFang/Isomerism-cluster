/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.synchronizer.syn;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.ControllerDown;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.ControllersListener;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.FloodlighttopoService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.RyutopoService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylighttopo.rev200301.OpendaylighttopoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xidian.synchronizer.syn.tasks.TopoTask;

public class ControllerDownListener implements ControllersListener {

	private DataBroker dataBroker;
	private FloodlighttopoService floodlighttopoService;
	private RyutopoService ryutopoService;
	private OpendaylighttopoService opendaylighttopoService;
	private Logger LOG = LoggerFactory.getLogger(ControllerDownListener.class);
	public ControllerDownListener(DataBroker dataBroker, FloodlighttopoService floodlighttopoService,
			RyutopoService ryutopoService,OpendaylighttopoService opendaylighttopoService) {
		this.dataBroker = dataBroker;
		this.floodlighttopoService = floodlighttopoService;
		this.ryutopoService = ryutopoService;
		this.opendaylighttopoService = opendaylighttopoService;
	}

	@Override
	public void onControllerDown(ControllerDown notification) {
		LOG.info("Receive controller down notification");
		// controller down trigger topoTask  
		new TopoTask(dataBroker, floodlighttopoService, ryutopoService, opendaylighttopoService).start();
	}

}
