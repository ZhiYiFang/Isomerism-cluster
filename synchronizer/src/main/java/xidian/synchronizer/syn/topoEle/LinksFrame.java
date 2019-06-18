/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.synchronizer.syn.topoEle;

import java.util.List;

public class LinksFrame {
	private String controllerId;
	private List<Link> frames;
	public LinksFrame(String controllerId) {
		this.controllerId = controllerId;
	}
	public LinksFrame(String controllerId, List<Link> frames) {
		super();
		this.controllerId = controllerId;
		this.frames = frames;
	}
	public String getControllerId() {
		return controllerId;
	}
	public void setControllerId(String controllerId) {
		this.controllerId = controllerId;
	}
	public List<Link> getFrames() {
		return frames;
	}
	public void setFrames(List<Link> frames) {
		this.frames = frames;
	}
	
}
