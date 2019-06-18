/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.synchronizer.syn.utils;

public class MoveOrder {

	private String destinationIP;
	private String destinationPort;
	private String switchDpid;
	
	public String getDestinationIP() {
		return destinationIP;
	}
	public void setDestinationIP(String destinationIP) {
		this.destinationIP = destinationIP;
	}
	public String getDestinationPort() {
		return destinationPort;
	}
	public void setDestinationPort(String destinationPort) {
		this.destinationPort = destinationPort;
	}
	public String getSwitchDpid() {
		return switchDpid;
	}
	public void setSwitchDpid(String switchDpid) {
		this.switchDpid = switchDpid;
	}
	
	public MoveOrder(String destinationIP, String destinationPort, String switchName) {
		super();
		this.destinationIP = destinationIP;
		this.destinationPort = destinationPort;
		this.switchDpid = switchName;
	}
	public MoveOrder(String destinationIP, String switchName) {
		super();
		this.destinationIP = destinationIP;
		this.destinationPort = "6653";
		this.switchDpid = switchName;
	}
}
