/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package xidian.impl.controller.ryu;

public class RyuMatch {

	String dl_src;
	String dl_dst;
	String nw_src;
	String nw_dst;
	String tp_src;
	String tp_dst;
	String dl_type;
	String nw_type;
	
	public String getDl_type() {
		return dl_type;
	}
	public void setDl_type(String dl_type) {
		this.dl_type = dl_type;
	}
	public String getNw_type() {
		return nw_type;
	}
	public void setNw_type(String nw_type) {
		this.nw_type = nw_type;
	}
	public String getDl_src() {
		return dl_src;
	}
	public void setDl_src(String dl_src) {
		this.dl_src = dl_src;
	}
	public String getDl_dst() {
		return dl_dst;
	}
	public void setDl_dst(String dl_dst) {
		this.dl_dst = dl_dst;
	}
	public String getNw_src() {
		return nw_src;
	}
	public void setNw_src(String nw_src) {
		this.nw_src = nw_src;
	}
	public String getNw_dst() {
		return nw_dst;
	}
	public void setNw_dst(String nw_dst) {
		this.nw_dst = nw_dst;
	}
	public String getTp_src() {
		return tp_src;
	}
	public void setTp_src(String tp_src) {
		this.tp_src = tp_src;
	}
	public String getTp_dst() {
		return tp_dst;
	}
	public void setTp_dst(String tp_dst) {
		this.tp_dst = tp_dst;
	}

}
