/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.impl.controller.vcf.message;

public class FlowMessage {

	private String version;
	private VcfFlow flow;
	public FlowMessage(String version, VcfFlow flow) {
		super();
		this.version = version;
		this.flow = flow;
	}
	public FlowMessage() {
		
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public VcfFlow getFlow() {
		return flow;
	}
	public void setFlow(VcfFlow flow) {
		this.flow = flow;
	}
	
}





