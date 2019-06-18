/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package xidian.impl.util.rediskey;

public class CSKey extends BasePrefix{

	private CSKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
		// TODO Auto-generated constructor stub
	}

	public static CSKey getSwitches = new CSKey(0,"cs");
	public static CSKey getEdgeSwitches = new CSKey(0, "ces");
}
