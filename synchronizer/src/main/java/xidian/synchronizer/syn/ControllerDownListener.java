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
import org.opendaylight.yang.gen.v1.urn.opendaylight.role.service.rev150727.SalRoleService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.RyutopoService;

import xidian.synchronizer.syn.tasks.TopoTask;

public class ControllerDownListener implements ControllersListener {

	private DataBroker dataBroker;
	private FloodlighttopoService floodlighttopoService;
	private RyutopoService ryutopoService;
	private SalRoleService salRoleService;

	public ControllerDownListener(DataBroker dataBroker, FloodlighttopoService floodlighttopoService,
			RyutopoService ryutopoService, SalRoleService salRoleService) {
		this.dataBroker = dataBroker;
		this.floodlighttopoService = floodlighttopoService;
		this.ryutopoService = ryutopoService;
		this.salRoleService = salRoleService;
	}

	@Override
	public void onControllerDown(ControllerDown notification) {
		// controller down trigger topoTask  
		new TopoTask(dataBroker, floodlighttopoService, ryutopoService, salRoleService).start();
	}

}
