/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.synchronizer.syn.tasks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.ControllerTypes.TypeName;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.Isomerism;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.IsomerismControllers;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.DevicemanagementService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.GetDeviceFlowTableInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.GetDeviceFlowTableOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.GetDevicesInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.GetDevicesOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.devicemanagement.rev181126.GetPortsInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.linkmanager.rev181126.LinkmanagerService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.guard.rev150105.DatapathId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.vcfstatistics.rev181126.VcfstatisticsService;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;

import com.google.common.base.Optional;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class SynTask extends TimerTask{

	private DataBroker dataBroker;
	private JsonParser parser = new JsonParser();
	private DevicemanagementService devicemanagementService;
	private LinkmanagerService linkmanagerService;
	private VcfstatisticsService vcfstatisticsService;
	
	public SynTask(DataBroker dataBroker, DevicemanagementService devicemanagementService,
			LinkmanagerService linkmanagerService, VcfstatisticsService vcfstatisticsService) {
		super();
		this.dataBroker = dataBroker;
		this.devicemanagementService = devicemanagementService;
		this.linkmanagerService = linkmanagerService;
		this.vcfstatisticsService = vcfstatisticsService;
	}
	
	public void run() {
		ReadOnlyTransaction read = dataBroker.newReadOnlyTransaction();
		InstanceIdentifier<Isomerism> path = InstanceIdentifier.create(Isomerism.class);
		Isomerism result = null;
		try {
			Optional<Isomerism> optional = read.read(LogicalDatastoreType.CONFIGURATION, path).get();
			if (optional.isPresent()) {
				result = optional.get();
				List<IsomerismControllers> controllers = result.getIsomerismControllers();
				for (IsomerismControllers c : controllers) {
					Ipv4Address ip = c.getIp();
					PortNumber port = c.getPort();
					TypeName type = c.getTypeName();
					switch (type) {
					case Agile:
						break;
					case Apic:
						break;
					case Floodlight:
						synFloodlight(ip, port);
						break;
					case Ryu:
						synRyu(ip, port);
						break;
					case Vcf:
						synVcf(ip, port);
						break;
					default:
						break;
					}
				}
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void synFloodlight(Ipv4Address ip, PortNumber port) {
		
	}
	
	private void synRyu(Ipv4Address ip, PortNumber port) {
		
	}
	
	private void synVcf(Ipv4Address ip, PortNumber port) {
//		WriteTransaction write = dataBroker.newWriteOnlyTransaction();
//		InstanceIdentifier<IsomerismControllers> path2 = InstanceIdentifier.create(Isomerism.class)
//				.child(IsomerismControllers.class, new IsomerismControllersKey(ip));
//		try {
//			IsomerismControllersBuilder isomerismControllers = new IsomerismControllersBuilder();
//			isomerismControllers.setKey(new IsomerismControllersKey(ip));
//			// get link info
//			String linkInfo = linkmanagerService
//					.getVcfLink(new GetVcfLinkInputBuilder().setIp(ip).setPort(port).build()).get()
//					.getResult().getResult();
//			isomerismControllers.setTopoInfo(new TopoInfoBuilder().setLinks(linkInfo).build());
//			// controller status
//			String controllerStatus = vcfstatisticsService.getVcfControllersStatus(
//					new GetVcfControllersStatusInputBuilder().setIp(ip).setPort(port).build()).get()
//					.getResult().getResult();
//			if(controllerStatus.contains("result")) isomerismControllers.setControllerStatus(ControllerStatus.Up);
//			else isomerismControllers.setControllerStatus(ControllerStatus.Down);
//			// 获取这个控制器下的全部的Device设备
//			FlowTablesInfoBuilder flowTablesInfoBuilder = new FlowTablesInfoBuilder();
//			PortsInfoBuilder portsInfoBuilder = new PortsInfoBuilder();
//			List<Tables> tables = new ArrayList<>();
//			List<SwitchesPorts> ports = new ArrayList<>();
//			List<String> allDevices = getAllDevices(devicemanagementService, ip, port);
//			// 获取每个dpid的table和port信息
//			for (String dpid : allDevices) {
//				String table = getDeviceFlowTable(devicemanagementService, ip, port, dpid);
//				TablesBuilder tablesBuilder = new TablesBuilder();
//				tablesBuilder.setDpid(new DatapathId(dpid));
//				tablesBuilder.setKey(new TablesKey(new DatapathId(dpid)));
//				tablesBuilder.setTable(table);
//				tables.add(tablesBuilder.build());
//				String portInfo = getDevicePortInfo(devicemanagementService, ip, port, dpid);
//				SwitchesPortsBuilder switchesPortsBuilder = new SwitchesPortsBuilder();
//				switchesPortsBuilder.setDpid(new DatapathId(dpid));
//				switchesPortsBuilder.setKey(new SwitchesPortsKey(new DatapathId(dpid)));
//				switchesPortsBuilder.setPortsInfo(portInfo);
//				ports.add(switchesPortsBuilder.build());
//			}
//			flowTablesInfoBuilder.setTables(tables);
//			portsInfoBuilder.setSwitchesPorts(ports);
//			isomerismControllers.setFlowTablesInfo(flowTablesInfoBuilder.build());
//			isomerismControllers.setPortsInfo(portsInfoBuilder.build());
//			write.merge(LogicalDatastoreType.OPERATIONAL, path2, isomerismControllers.build());
//			CheckedFuture<Void, TransactionCommitFailedException> future = write.submit();
//		} catch (InterruptedException | ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	private String getDevicePortInfo(DevicemanagementService devicemanagementService, Ipv4Address ip, PortNumber port,
			String dpid) throws InterruptedException, ExecutionException {
		GetPortsInputBuilder builder = new GetPortsInputBuilder();
		builder.setDpid(new DatapathId(dpid));
		builder.setIp(ip);
		builder.setPort(port);
		String portInfo = devicemanagementService.getPorts(builder.build()).get().getResult().getResult();
		return portInfo;
	}

	private String getDeviceFlowTable(DevicemanagementService devicemanagementService, Ipv4Address ip, PortNumber port,
			String dpid) throws InterruptedException, ExecutionException {
		GetDeviceFlowTableInputBuilder input = new GetDeviceFlowTableInputBuilder();
		input.setIp(ip);
		input.setPort(port);
		input.setDpid(new DatapathId(dpid));
		GetDeviceFlowTableOutput output = devicemanagementService.getDeviceFlowTable(input.build()).get().getResult();
		String table = output.getResult();
		return table;
	}

	private List<String> getAllDevices(DevicemanagementService devicemanagementService, Ipv4Address ip, PortNumber port)
			throws InterruptedException, ExecutionException {
		List<String> allDevices = new ArrayList<>();
		GetDevicesInputBuilder getDevicesInputBuilder = new GetDevicesInputBuilder();
		getDevicesInputBuilder.setIp(ip);
		getDevicesInputBuilder.setPort(port);
		Future<RpcResult<GetDevicesOutput>> allDevice = devicemanagementService
				.getDevices(getDevicesInputBuilder.build());
		String devices = allDevice.get().getResult().getResult();

		JsonArray jsonArray = parser.parse(devices).getAsJsonObject().get("datapaths").getAsJsonArray();
		Iterator<JsonElement> iterator = jsonArray.iterator();
		while (iterator.hasNext()) {
			JsonElement element = iterator.next();
			String dpid = element.getAsJsonObject().get("dpid").getAsString();
			allDevices.add(dpid);
		}
		return allDevices;
	}
}
