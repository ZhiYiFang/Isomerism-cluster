/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package xidian.impl.controller.ryu;

public class RyuAction {

	String type;
	String port;
	public RyuAction( String port) {
		super();
		type="OUTPUT";
		this.port = port;
	}
	
}
