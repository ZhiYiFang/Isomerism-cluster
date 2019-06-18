/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.synchronizer.syn.topoEle;

import java.util.List;

public class SwitchWithPortFrame {

	private String controllerId;
	private List<SwitchWithPorts> frames;
	public String getControllerId() {
		return controllerId;
	}
	public void setControllerId(String controllerId) {
		this.controllerId = controllerId;
	}
	public List<SwitchWithPorts> getFrames() {
		return frames;
	}
	public void setFrames(List<SwitchWithPorts> frames) {
		this.frames = frames;
	}
	
	public SwitchWithPortFrame(String controllerId) {
		super();
		this.controllerId = controllerId;
	}
	public SwitchWithPortFrame(String controllerId, List<SwitchWithPorts> frames) {
		super();
		this.controllerId = controllerId;
		this.frames = frames;
	}
}
