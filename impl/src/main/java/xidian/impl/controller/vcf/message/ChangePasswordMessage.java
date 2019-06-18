/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.impl.controller.vcf.message;

public class ChangePasswordMessage {

	private class ChangePassword{
		private String user;
		private String oldpassword;
		private String newpassword;
		public ChangePassword(String user, String oldpassword, String newpassword) {
			super();
			this.user = user;
			this.oldpassword = oldpassword;
			this.newpassword = newpassword;
		}
		
	}
	
	private ChangePassword changepassword;
	
	public ChangePasswordMessage(String user, String oldpassword, String newpassword) {
		super();
		this.changepassword = new ChangePassword(user, oldpassword, newpassword);
	}
}
