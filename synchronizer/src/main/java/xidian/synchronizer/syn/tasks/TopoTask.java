/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.synchronizer.syn.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.ControllerTypes.TypeName;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.DataPlaneLink;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.DataPlaneLinkBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.Isomerism;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.data.plane.link.DataPlaneLinks;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.data.plane.link.DataPlaneLinksBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.IsomerismControllers;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.isomerism.IsomerismControllers.ControllerStatus;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.FloodlighttopoService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.GetFloodlightLinksInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.GetFloodlightLinksOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.GetFloodlightSwitchesInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.GetFloodlightSwitchesOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.floodlighttopo.rev190515.GetFloodligtSwitchPortInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.RemoveFlowInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.guard.rev150105.DatapathId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuLinksInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuLinksOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.GetRyuSwitchesInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ryutopo.rev190515.RyutopoService;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.link.attributes.Destination;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.link.attributes.Source;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import xidian.synchronizer.rediskey.CSKey;
import xidian.synchronizer.rediskey.OdlLinksKey;
import xidian.synchronizer.rediskey.RedisController;
import xidian.synchronizer.rediskey.SCKey;
import xidian.synchronizer.syn.topoEle.Link;
import xidian.synchronizer.syn.topoEle.LinksFrame;
import xidian.synchronizer.syn.topoEle.SwitchWithPortFrame;
import xidian.synchronizer.syn.topoEle.SwitchWithPorts;
import xidian.synchronizer.util.DpidUtils;
import xidian.synchronizer.util.InstructionUtils;
import xidian.synchronizer.util.MoveOrder;
import xidian.synchronizer.util.RedisService;

public class TopoTask extends Thread {

	private DataBroker dataBroker;
	private FloodlighttopoService floodlighttopoService;
	private RyutopoService ryutopoService;
	private JsonParser jsonParser;
	private RedisService redisService;
	private Set<String> edgeSwitches;
	Map<String, String> switchToController;
	private Logger LOG = LoggerFactory.getLogger(TopoTask.class);

	public TopoTask(DataBroker dataBroker, FloodlighttopoService floodlighttopoService, RyutopoService ryutopoService) {
		this.dataBroker = dataBroker;
		this.floodlighttopoService = floodlighttopoService;
		this.ryutopoService = ryutopoService;
		this.jsonParser = new JsonParser();
		this.redisService = RedisService.getInstance();
	}

	@Override
	public void run() {
		LOG.info("Start TopoTask");
		ReadOnlyTransaction read = dataBroker.newReadOnlyTransaction();
		InstanceIdentifier<Isomerism> path = InstanceIdentifier.create(Isomerism.class);
		Isomerism result = null;
		try {
			Optional<Isomerism> optional = read.read(LogicalDatastoreType.CONFIGURATION, path).get();

			if (optional.isPresent()) {
				result = optional.get();
				List<IsomerismControllers> controllers = result.getIsomerismControllers();
				Map<String, LinksFrame> linkFrames = new HashMap<>();
				Map<String, RedisController> controllerIpToRedis = new HashMap<>();
				Map<String, SwitchWithPortFrame> switchWithPortFrames = new HashMap<>();

				LinksFrame linkFrame = null;
				SwitchWithPortFrame switchWithPortFrame = null;

				for (IsomerismControllers controller : controllers) {
					if (controller.getControllerStatus().equals(ControllerStatus.Down)
							|| controller.getControllerStatus().equals(ControllerStatus.Abnormal)) {
						continue;
					}
					TypeName type = controller.getTypeName();
					switch (type) {
					case Agile:
						break;
					case Apic:
						break;
					case Floodlight:
						linkFrame = getFloodlightLinksFrame(controller);
						switchWithPortFrame = getFloodlightPortsFrame(controller);
						break;
					case Ryu:
						linkFrame = getRyuLinksFrame(controller);
						switchWithPortFrame = getRyuPortsFrame(controller);
						break;
					case Vcf:
						break;
					default:
						break;
					}
					linkFrames.put(controller.getIp().getValue(), linkFrame);
					controllerIpToRedis.put(controller.getIp().getValue(),
							new RedisController(controller.getIp(), controller.getTypeName(), controller.getPort()));
					switchWithPortFrames.put(controller.getIp().getValue(), switchWithPortFrame);
				}

				// 建立switch和controller的映射表 方便后边switch查询controller
				switchToController = new HashMap<String, String>();
				// 确定边界交换机
				edgeSwitches = new HashSet<>();

				// 开始连接链路
				Set<String> domains = linkFrames.keySet();
				// 忽略controllerID将所有链路信息聚合在一起
				Set<Link> allLinks = new HashSet<>();
				Set<SwitchWithPorts> allSwitchPorts = new HashSet<>();

				for (String s : domains) {
					allLinks.addAll(linkFrames.get(s).getFrames());
					List<SwitchWithPorts> frames = switchWithPortFrames.get(s).getFrames();
					allSwitchPorts.addAll(frames);
					for (SwitchWithPorts frame : frames) {
						switchToController.put(frame.getDpid(), s);
					}
				}

				// 遍历链路确定链路端口
				List<String> linkPorts = new ArrayList<>();
				for (Link link : allLinks) {
					linkPorts.add(link.getSrcSwitch() + ":" + link.getSrcPort());
					linkPorts.add(link.getDstSwitch() + ":" + link.getDstPort());
				}

				// 遍历所有交换机
				for (SwitchWithPorts sw : allSwitchPorts) {
					// 这个交换机如果存在一个端口 这个端口不再链路中就是边界交换机
					List<Integer> ports = sw.getPorts();
					for (Integer port : ports) {
						String temp = sw.getDpid() + ":" + port;
						if (!linkPorts.contains(temp)) {
							edgeSwitches.add(sw.getDpid());
							break;
						}
					}
				}

				// 暂时先全部加到odl上
				// for (SwitchWithPorts sw : allSwitchPorts) {
				//    edgeSwitches.add(sw.getDpid());
				// }

				// 让中间件与所有的边界交换机建立连接来确定这些边界交换机的连接关系
				for (String dpid : edgeSwitches) {
					InstructionUtils.moveToMiddle(dpid);
				}
				Thread.sleep(3000);

				// 连接建立完成后获取异构中间件的链路情况 确定各边界交换机之间的连接关系
				List<org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link> links = getOdlLinks(
						dataBroker);

				HashSet<Link> odlinks = new HashSet<>();

				for (org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link link : links) {
					Source source = link.getSource();
					String sourceString = source.getSourceTp().getValue();
					Destination destination = link.getDestination();
					String destinationString = destination.getDestTp().getValue();
					String srcSwitch = DpidUtils.getDpidFromOdlSw(sourceString);
					String dstSwitch = DpidUtils.getDpidFromOdlSw(destinationString);
					int srcPort = Integer.valueOf(sourceString.split(":")[2]);
					int dstPort = Integer.valueOf(destinationString.split(":")[2]);
//					odlinks.add(new Link(true, srcSwitch, srcPort, dstSwitch, dstPort));
					Link reverse = new Link(true, dstSwitch, dstPort, srcSwitch, srcPort);
					if (odlinks.contains(reverse)) {

					} else {
						odlinks.add(new Link(true, srcSwitch, srcPort, dstSwitch, dstPort));
					}
				}

				writeToRedis(switchWithPortFrames, switchToController, edgeSwitches, odlinks, controllerIpToRedis);
				// 把ODL检测到的边界交换机的连接关系加入进去
				for (Link l : odlinks) {
					if (allLinks.contains(l.reverseLink()))
						continue;
					allLinks.add(l);
				}
				// allLinks写入到dataStore中
				storeLink(dataBroker, allLinks);

			}

		} catch (Exception e) {
		} finally {
			// 恢复交换机原来的连接关系
			restoreSwitchConnection(edgeSwitches, switchToController);
		}
	}

	private void writeToRedis(Map<String, SwitchWithPortFrame> switchWithPortFrames,
			Map<String, String> switchToController, Set<String> edgeSwitches, HashSet<Link> odllinks,
			Map<String, RedisController> controllerIpToRedis) {

		// flush all expired values
		redisService.flushAll();

		// odllinks写入到redis
		redisService.set(OdlLinksKey.getOdlLinks, "", odllinks);

		// 控制器对应的交换机以及交换机的端口写入redis
		for (String controller : switchWithPortFrames.keySet()) {
			SwitchWithPortFrame sp = switchWithPortFrames.get(controller);
			List<String> switches = new ArrayList<>();
			for (SwitchWithPorts switchAndPorts : sp.getFrames()) {
				switches.add(switchAndPorts.getDpid());
			}
			redisService.set(CSKey.getSwitches, controller, switches);
		}

		// switchToController写入redis
		for (String sw : switchToController.keySet()) {
			String controllerIp = switchToController.get(sw);
			redisService.set(SCKey.getController, sw, controllerIpToRedis.get(controllerIp));
		}

		// 每一个控制器的边界交换机写入redis
		for (String controller : switchWithPortFrames.keySet()) {
			List<SwitchWithPorts> switches = switchWithPortFrames.get(controller).getFrames();
			List<String> sws = new ArrayList<>();
			for (SwitchWithPorts sw : switches) {
				if (edgeSwitches.contains(sw.getDpid())) {
					sws.add(sw.getDpid());
				}
			}
			redisService.set(CSKey.getEdgeSwitches, controller, sws);
		}
	}

	private void storeLink(DataBroker dataBroker, Set<Link> allLinks) {
		InstanceIdentifier<DataPlaneLink> path = InstanceIdentifier.create(DataPlaneLink.class);
		DataPlaneLinkBuilder builder = new DataPlaneLinkBuilder();
		List<DataPlaneLinks> list = new ArrayList<>();
		for (Link s : allLinks) {
			DataPlaneLinksBuilder builder2 = new DataPlaneLinksBuilder();
			builder2.setDstPort(String.valueOf(s.getDstPort()));
			builder2.setDstSwitch(s.getDstSwitch());
			builder2.setSrcSwitch(s.getSrcSwitch());
			builder2.setSrcPort(String.valueOf(s.getSrcPort()));
			builder2.setIsBidirectional(s.isBidirectional());
			list.add(builder2.build());
		}
		builder.setDataPlaneLinks(list);
		WriteTransaction write = dataBroker.newWriteOnlyTransaction();
		write.put(LogicalDatastoreType.CONFIGURATION, path, builder.build(), true);
		write.submit();
	}

	private void restoreSwitchConnection(Set<String> edgeSwitches, Map<String, String> switchToController) {
		if(edgeSwitches == null || edgeSwitches.size() == 0 || switchToController == null || switchToController.size() == 0)
			return;
//		setAllSlave();
		RemoveFlowInputBuilder input = new RemoveFlowInputBuilder();
		for (String sw : edgeSwitches) {
			String controllerIp = switchToController.get(sw);
			MoveOrder order = new MoveOrder(controllerIp, sw);
			InstructionUtils.moveSwitch(order);
		}
	}

	private List<Node> getAllNodes(DataBroker dataBroker) {
		InstanceIdentifier.InstanceIdentifierBuilder<Nodes> nodesInsIdBuilder = InstanceIdentifier
				.<Nodes>builder(Nodes.class);
		// 所有节点信息
		Nodes nodes = null;
		// 创建读事务
		ReadOnlyTransaction readOnlyTransaction = dataBroker.newReadOnlyTransaction();
		try {
			Optional<Nodes> dataObjectOptional = readOnlyTransaction
					.read(LogicalDatastoreType.OPERATIONAL, nodesInsIdBuilder.build()).get();
			// 如果数据不为空，获取到nodes
			if (dataObjectOptional.isPresent()) {
				nodes = dataObjectOptional.get();
			}
		} catch (InterruptedException e) {
		} catch (ExecutionException e) {
		} finally {
			readOnlyTransaction.close();
		}
		return nodes.getNode();
	}

	private List<org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link> getOdlLinks(
			DataBroker dataBroker) {
		ReadOnlyTransaction read = dataBroker.newReadOnlyTransaction();
		InstanceIdentifier<NetworkTopology> path = InstanceIdentifier.create(NetworkTopology.class);
		NetworkTopology networkTopology = null;
		try {
			Optional<NetworkTopology> op = read.read(LogicalDatastoreType.OPERATIONAL, path).get();
			if (!op.isPresent()) {
				return null;
			}
			networkTopology = op.get();
		} catch (InterruptedException | ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		List<org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link> links = networkTopology
				.getTopology().get(0).getLink();
//		List<Topology> topo = networkTopology.getTopology();
		return links;
	}

	private SwitchWithPortFrame getRyuPortsFrame(IsomerismControllers controller) {
		SwitchWithPortFrame frame = new SwitchWithPortFrame(controller.getIp().getValue());
		List<SwitchWithPorts> switchWithPorts = new ArrayList<>();

		GetRyuSwitchesInputBuilder input = new GetRyuSwitchesInputBuilder();
		input.setIp(controller.getIp());
		input.setPort(controller.getPort());

		try {
			String result = ryutopoService.getRyuSwitches(input.build()).get().getResult().getResult();
			JsonArray array = jsonParser.parse(result).getAsJsonArray();
			Iterator<JsonElement> it = array.iterator();
			while (it.hasNext()) {
				JsonElement ele = it.next();
				String dpid = ele.getAsJsonObject().get("dpid").getAsString();
				StringBuffer sb = new StringBuffer();
				char[] chars = dpid.toCharArray();
				for (int i = 0; i < chars.length; i++) {
					sb.append(chars[i]);
					if (i % 2 == 1 && i != chars.length - 1) {
						sb.append(":");
					}
				}
				String sw = sb.toString();

				SwitchWithPorts switchPorts = new SwitchWithPorts(sw);
				List<Integer> ports = new ArrayList<>();
				Iterator<JsonElement> iter = ele.getAsJsonObject().get("ports").getAsJsonArray().iterator();
				while (iter.hasNext()) {
					JsonElement element = iter.next();
					ports.add(element.getAsJsonObject().get("port_no").getAsInt());
				}
				switchPorts.setPorts(ports);
				switchWithPorts.add(switchPorts);
			}
			frame.setFrames(switchWithPorts);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return frame;
	}

	private SwitchWithPortFrame getFloodlightPortsFrame(IsomerismControllers controller) {

		SwitchWithPortFrame frame = new SwitchWithPortFrame(controller.getIp().getValue());
		List<SwitchWithPorts> switchWithPorts = new ArrayList<>();
		GetFloodlightSwitchesInputBuilder input = new GetFloodlightSwitchesInputBuilder();
		input.setIp(controller.getIp());
		input.setPort(controller.getPort());
		GetFloodlightSwitchesOutput result = null;
		try {
			result = floodlighttopoService.getFloodlightSwitches(input.build()).get().getResult();
			String jsonString = result.getResult();
			JsonArray jsonArray = jsonParser.parse(jsonString).getAsJsonArray();
			Iterator<JsonElement> it = jsonArray.iterator();
			int size = jsonArray.size();

			// 遍历每一个交换机
			for (int i = 0; i < size; i++) {
				JsonElement ele = jsonArray.get(i);
				String switchDpid = ele.getAsJsonObject().get("switchDPID").getAsString();
				GetFloodligtSwitchPortInputBuilder inputBuilder = new GetFloodligtSwitchPortInputBuilder();
				inputBuilder.setIp(controller.getIp());
				inputBuilder.setPort(controller.getPort());
				inputBuilder.setDpid(new DatapathId(switchDpid));

				SwitchWithPorts switchPorts = new SwitchWithPorts(switchDpid);

				// 获取这个交换机的端口
				String portsJson = floodlighttopoService.getFloodligtSwitchPort(inputBuilder.build()).get().getResult()
						.getResult();
				JsonArray portsArray = jsonParser.parse(portsJson).getAsJsonObject().get("port_reply").getAsJsonArray();
				Iterator<JsonElement> it2 = portsArray.iterator();

				List<Integer> ports = new ArrayList<>();
				// 遍历这个交换机的每一个端口
				while (it2.hasNext()) {
					JsonElement ele2 = it2.next();
					Iterator<JsonElement> iterator = ele2.getAsJsonObject().get("port").getAsJsonArray().iterator();
					while (iterator.hasNext()) {
						JsonElement portInfo = iterator.next();
						String portString = portInfo.getAsJsonObject().get("portNumber").getAsString();
						if (!portString.equals("local")) {
							ports.add(Integer.valueOf(portString));
						}
					}
				}
				switchPorts.setPorts(ports);
				switchWithPorts.add(switchPorts);
			}

			frame.setFrames(switchWithPorts);

		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return frame;
	}

	private LinksFrame getFloodlightLinksFrame(IsomerismControllers controller) {
		LinksFrame frame = new LinksFrame(controller.getIp().getValue());
		List<Link> links = new ArrayList<>();
		GetFloodlightLinksInputBuilder builder = new GetFloodlightLinksInputBuilder();
		builder.setIp(controller.getIp());
		builder.setPort(controller.getPort());
		Future<RpcResult<GetFloodlightLinksOutput>> future = floodlighttopoService.getFloodlightLinks(builder.build());
		try {
			String linkJson = future.get().getResult().getResult();
			JsonArray array = jsonParser.parse(linkJson).getAsJsonArray();
			Iterator<JsonElement> it = array.iterator();
			while (it.hasNext()) {
				JsonElement ele = it.next();
				if (ele.getAsJsonObject().get("type").getAsString().equals("internal")) {
					String srcSwitch = ele.getAsJsonObject().get("src-switch").getAsString();
					int srcPort = ele.getAsJsonObject().get("src-port").getAsInt();
					String dstSwitch = ele.getAsJsonObject().get("dst-switch").getAsString();
					int dstPort = ele.getAsJsonObject().get("dst-port").getAsInt();
					String bidirectional = ele.getAsJsonObject().get("direction").getAsString();
					Link link = new Link(bidirectional.equals("bidirectional"), srcSwitch, srcPort, dstSwitch, dstPort);
					links.add(link);
				}
			}
			frame.setFrames(links);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return frame;
	}

	private LinksFrame getRyuLinksFrame(IsomerismControllers controller) {
		LinksFrame frame = new LinksFrame(controller.getIp().getValue());
		List<Link> links = new ArrayList<>();
		GetRyuLinksInputBuilder builder = new GetRyuLinksInputBuilder();
		builder.setIp(controller.getIp());
		builder.setPort(controller.getPort());
		Future<RpcResult<GetRyuLinksOutput>> future = ryutopoService.getRyuLinks(builder.build());
		try {
			String linkJson = future.get().getResult().getResult();
			JsonArray array = jsonParser.parse(linkJson).getAsJsonArray();
			Iterator<JsonElement> it = array.iterator();
			while (it.hasNext()) {
				JsonElement ele = it.next();
				JsonObject jsonObject = ele.getAsJsonObject();
				JsonObject srcObject = jsonObject.get("src").getAsJsonObject();
				JsonObject dstObject = jsonObject.get("dst").getAsJsonObject();

				String srcDpid = srcObject.get("dpid").getAsString();
				String srcSwitch = DpidUtils.getDpidFromString(srcDpid);
				int srcPort = srcObject.get("port_no").getAsInt();

				String dstDpid = dstObject.get("dpid").getAsString();
				String dstSwitch = DpidUtils.getDpidFromString(dstDpid);
				int dstPort = dstObject.get("port_no").getAsInt();

				Link reverse = new Link(true, dstSwitch, dstPort, srcSwitch, srcPort);
				if (links.contains(reverse)) {
//					links.remove(reverse);
//					reverse.setBidirectional(true);
//					links.add(reverse);
				} else {
					links.add(new Link(true, srcSwitch, srcPort, dstSwitch, dstPort));
				}
			}
			frame.setFrames(links);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return frame;
	}
}
