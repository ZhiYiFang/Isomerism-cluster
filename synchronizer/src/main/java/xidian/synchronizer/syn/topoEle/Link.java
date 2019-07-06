/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.synchronizer.syn.topoEle;

public class Link {

	private boolean isBidirectional;
	private String srcSwitch;
	private int srcPort;
	private String dstSwitch;
	private int dstPort;
	
	@Override
	public String toString() {
		return "Link [isBidirectional=" + isBidirectional + ", srcSwitch=" + srcSwitch + ", srcPort=" + srcPort
				+ ", dstSwitch=" + dstSwitch + ", dstPort=" + dstPort + "]";
	}
	
	public Link reverseLink() {
		return new Link(isBidirectional, dstSwitch, dstPort, srcSwitch, srcPort);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dstPort;
		result = prime * result + ((dstSwitch == null) ? 0 : dstSwitch.hashCode());
		result = prime * result + (isBidirectional ? 1231 : 1237);
		result = prime * result + srcPort;
		result = prime * result + ((srcSwitch == null) ? 0 : srcSwitch.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Link other = (Link) obj;
		if (dstPort != other.dstPort)
			return false;
		if (dstSwitch == null) {
			if (other.dstSwitch != null)
				return false;
		} else if (!dstSwitch.equals(other.dstSwitch))
			return false;
		if (isBidirectional != other.isBidirectional)
			return false;
		if (srcPort != other.srcPort)
			return false;
		if (srcSwitch == null) {
			if (other.srcSwitch != null)
				return false;
		} else if (!srcSwitch.equals(other.srcSwitch))
			return false;
		return true;
	}



	public Link(boolean isBidirectional, String srcSwitch, int srcPort, String dstSwitch, int dstPort) {
		super();
		this.isBidirectional = isBidirectional;
		this.srcSwitch = srcSwitch;
		this.srcPort = srcPort;
		this.dstSwitch = dstSwitch;
		this.dstPort = dstPort;
	}
	public boolean isBidirectional() {
		return isBidirectional;
	}
	public void setBidirectional(boolean isBidirectional) {
		this.isBidirectional = isBidirectional;
	}
	public String getSrcSwitch() {
		return srcSwitch;
	}
	public void setSrcSwitch(String srcSwitch) {
		this.srcSwitch = srcSwitch;
	}
	public int getSrcPort() {
		return srcPort;
	}
	public void setSrcPort(int srcPort) {
		this.srcPort = srcPort;
	}
	public String getDstSwitch() {
		return dstSwitch;
	}
	public void setDstSwitch(String dstSwitch) {
		this.dstSwitch = dstSwitch;
	}
	public int getDstPort() {
		return dstPort;
	}
	public void setDstPort(int dstPort) {
		this.dstPort = dstPort;
	}
	
}
