/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package xidian.impl.util;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.guard.rev150105.DatapathId;

public class DpidUtils {
	
	
	
	public static String getIntStringValueFromDpid(DatapathId dpidRaw) {
		String dpid = dpidRaw.getValue();
		return Integer.valueOf(dpid.replace(":", ""), 16).toString();
	}
	
	public static int getIntValueFromDpid(DatapathId dpidRaw) {
		String dpid = dpidRaw.getValue();
		return Integer.valueOf(dpid.replace(":", ""), 16);
	}
	
	public static String getIntStringValueFromDpid(String dpid) {
		return Integer.valueOf(dpid.replace(":", ""), 16).toString();
	}
	
	public static int getIntValueFromDpid(String dpid) {
		return Integer.valueOf(dpid.replace(":", ""), 16);
	}
	
	public static String getDpidFromOdlSw(String odlSw) {
		String[] sourceStrs = odlSw.split(":");
		char[] chars = String.format("%016x", Integer.valueOf(sourceStrs[1])).toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			sb.append(chars[i]);
			if (i % 2 == 1 && i != chars.length - 1) {
				sb.append(":");
			}
		}
		return sb.toString();
	}
	
	public static String getDpidFromInt(int dpid) {
		char[] chars = String.format("%016x", Integer.valueOf(dpid)).toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			sb.append(chars[i]);
			if (i % 2 == 1 && i != chars.length - 1) {
				sb.append(":");
			}
		}
		return sb.toString();
	}
	
	public static String getDpidFromString(String dpid) {
		StringBuffer sb = new StringBuffer();
		char[] chars = dpid.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			sb.append(chars[i]);
			if (i % 2 == 1 && i != chars.length - 1) {
				sb.append(":");
			}
		}
		return sb.toString();
	}
}
