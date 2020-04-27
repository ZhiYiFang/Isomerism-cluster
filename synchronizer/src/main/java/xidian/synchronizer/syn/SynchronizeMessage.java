/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.synchronizer.syn;

import java.util.Timer;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.NotificationPublishService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.ControllersService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.DevicemanagementService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlightacl.rev190713.FloodlightaclService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.FloodlighttopoService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.linkmanager.rev181126.LinkmanagerService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.RyutopoService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfstatistics.rev181126.VcfstatisticsService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.opendaylighttopo.rev200301.OpendaylighttopoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xidian.synchronizer.syn.tasks.AliveTestTask;

public class SynchronizeMessage {
	public static int alertNo = 0;
	private Logger LOG = LoggerFactory.getLogger(SynchronizeMessage.class);
	private DataBroker dataBroker;
	private Timer timer = new Timer();
	private FloodlighttopoService floodlighttopoService;
	private RyutopoService ryutopoService;
	private NotificationPublishService notificationPublishService;
	private ControllersService controllerService;
	private FloodlightaclService floodlightaclService;
	private OpendaylighttopoService opendaylighttopoService;
	
	public SynchronizeMessage(DataBroker dataBroker, DevicemanagementService devicemanagementService,
			LinkmanagerService linkmanagerService, VcfstatisticsService vcfstatisticsService,
			FloodlighttopoService floodlighttopoService, RyutopoService ryutopoService,
			NotificationPublishService notificationPublishService, ControllersService controllerService,
			FloodlightaclService floodlightaclService, OpendaylighttopoService opendaylighttopoService) {
		super();
		this.dataBroker = dataBroker;
		this.floodlighttopoService = floodlighttopoService;
		this.ryutopoService = ryutopoService;
		this.notificationPublishService = notificationPublishService;
		this.controllerService = controllerService;
		this.floodlightaclService = floodlightaclService;
		this.opendaylighttopoService = opendaylighttopoService;
	}

	public void init() {
		LOG.info("SynchronizeMessage initialized");
		synchronize();
	}

	public void synchronize() {
		// timer.schedule(new SynTask(dataBroker, devicemanagementService,
		// linkmanagerService, vcfstatisticsService), 10000,
		// 20000);
		timer.schedule(new AliveTestTask(dataBroker, floodlighttopoService, ryutopoService, notificationPublishService,
				controllerService, floodlightaclService), 5000, 10000);
	}

}
