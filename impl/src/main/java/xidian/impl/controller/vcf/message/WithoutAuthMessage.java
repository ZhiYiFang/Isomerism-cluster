/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.impl.controller.vcf.message;

public class WithoutAuthMessage {

	private String Description;
	private String Ip;
	private String Role;
	public WithoutAuthMessage(String description, String ip, String role) {
		super();
		Description = description;
		Ip = ip;
		Role = role;
	}
	
}
