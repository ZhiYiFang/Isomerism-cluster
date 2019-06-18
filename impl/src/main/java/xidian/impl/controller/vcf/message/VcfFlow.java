/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.impl.controller.vcf.message;

import java.util.List;

public class VcfFlow {

	private String priority;
	private String flow_mod_cmd;
	private String table_id;
	public String getTable_id() {
		return table_id;
	}
	public void setTable_id(String table_id) {
		this.table_id = table_id;
	}
	private String idle_timeout;
	private String hard_timeout;
	private List<String> flow_mod_flags;
	private List<VcfMatch> match;
	private List<VcfInstruction> instructions;
	public VcfFlow() {
		
	}
	public VcfFlow(String priority, String flow_mod_cmd, String idle_timeout, String hard_timeout,
			List<String> flow_mod_flags, List<VcfMatch> match, List<VcfInstruction> instructions) {
		super();
		this.priority = priority;
		this.flow_mod_cmd = flow_mod_cmd;
		this.idle_timeout = idle_timeout;
		this.hard_timeout = hard_timeout;
		this.flow_mod_flags = flow_mod_flags;
		this.match = match;
		this.instructions = instructions;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getFlow_mod_cmd() {
		return flow_mod_cmd;
	}
	public void setFlow_mod_cmd(String flow_mod_cmd) {
		this.flow_mod_cmd = flow_mod_cmd;
	}
	public String getIdle_timeout() {
		return idle_timeout;
	}
	public void setIdle_timeout(String idle_timeout) {
		this.idle_timeout = idle_timeout;
	}
	public String getHard_timeout() {
		return hard_timeout;
	}
	public void setHard_timeout(String hard_timeout) {
		this.hard_timeout = hard_timeout;
	}
	public List<String> getFlow_mod_flags() {
		return flow_mod_flags;
	}
	public void setFlow_mod_flags(List<String> flow_mod_flags) {
		this.flow_mod_flags = flow_mod_flags;
	}
	public List<VcfMatch> getMatch() {
		return match;
	}
	public void setMatch(List<VcfMatch> match) {
		this.match = match;
	}
	public List<VcfInstruction> getInstructions() {
		return instructions;
	}
	public void setInstructions(List<VcfInstruction> instructions) {
		this.instructions = instructions;
	}
	
}
