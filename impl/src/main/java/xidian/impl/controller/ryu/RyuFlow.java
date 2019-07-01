/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.impl.controller.ryu;

import java.util.List;

public class RyuFlow {
	String dpid;
	String table_id;
	String idle_timeout;
	String hard_timeout;
	String priority;
	RyuMatch match;
	List<RyuAction> actions;
	public String getDpid() {
		return dpid;
	}
	public void setDpid(String dpid) {
		this.dpid = dpid;
	}
	public String getTable_id() {
		return table_id;
	}
	public void setTable_id(String table_id) {
		this.table_id = table_id;
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
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public RyuMatch getMatch() {
		return match;
	}
	public void setMatch(RyuMatch match) {
		this.match = match;
	}
	public List<RyuAction> getActions() {
		return actions;
	}
	public void setActions(List<RyuAction> actions) {
		this.actions = actions;
	}
	
}
