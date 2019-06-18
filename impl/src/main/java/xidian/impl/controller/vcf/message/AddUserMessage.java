/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.impl.controller.vcf.message;

public class AddUserMessage {

	private class AddUser{
		private String user;
		private String password;
		private String role;
		public AddUser(String user, String password,  String role) {
			super();
			this.user = user;
			this.password = password;
			this.role = role;
		}
		
	}
	
	private AddUser adduser;

	public AddUserMessage(String user, String password,  String role) {
		super();
		AddUser addUser = new AddUser(user, password, role);
		this.adduser = addUser;
	}
	
}
