/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package xidian.synchronier.rediskey;

public class SCKey extends BasePrefix{

	private SCKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	
	public static SCKey getController = new SCKey(0,"sc");
}
