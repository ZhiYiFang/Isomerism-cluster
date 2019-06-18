/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package xidian.impl.controller.vcf.message;

import java.util.List;

import xidian.impl.controller.vcf.message.Actions.ApplyAction;


public class VcfInstruction {

	private List<ApplyAction> apply_actions;

	public List<ApplyAction> getApply_actions() {
		return apply_actions;
	}

	public void setApply_actions(List<ApplyAction> apply_actions) {
		this.apply_actions = apply_actions;
	}
	
}
