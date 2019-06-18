/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */


package xidian.synchronizer.syn.topoEle;

import java.util.List;

public class SwitchWithPorts {

	private String dpid;
	private List<Integer> ports;
	
	public SwitchWithPorts(String dpid) {
		super();
		this.dpid = dpid;
	}
	public SwitchWithPorts(String dpid, List<Integer> ports) {
		super();
		this.dpid = dpid;
		this.ports = ports;
	}
	public String getDpid() {
		return dpid;
	}
	public void setDpid(String dpid) {
		this.dpid = dpid;
	}
	public List<Integer> getPorts() {
		return ports;
	}
	public void setPorts(List<Integer> ports) {
		this.ports = ports;
	}
	
	
}
