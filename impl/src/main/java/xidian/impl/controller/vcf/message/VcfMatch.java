/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package xidian.impl.controller.vcf.message;

public class VcfMatch {

	private String eth_type;
	private String eth_src;
	private String eth_dst;
	private String ipv4_src;
	private String ipv4_dst;
	private String ip_proto;
	private String tcp_src;
	private String tcp_dst;
	
	public VcfMatch(String eth_type, String eth_src, String eth_dst, String ipv4_src, String ipv4_dst, String ip_proto,
			String tcp_src, String tcp_dst) {
		super();
		this.eth_type = eth_type;
		this.eth_src = eth_src;
		this.eth_dst = eth_dst;
		this.ipv4_src = ipv4_src;
		this.ipv4_dst = ipv4_dst;
		this.ip_proto = ip_proto;
		this.tcp_src = tcp_src;
		this.tcp_dst = tcp_dst;
	}		
	
}
