/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.impl.controller.vcf;

public interface VcfUrls {

	String LOGIN = "/sdn/v2.0/auth";
	String CONTROLLER_STATIC = "/sdn/v2.0/of/stats";
	String PORT_STATIC = "/sdn/v2.0/of/stats/ports";
	String METER_STATIC = "/sdn/v2.0/of/stats/meters";
	String WIHOUR_AUTH = "/sdn/v2.0/ipauth/addAuth/";
	String IS_CONNECTED = "/sdn/v2.0/net/interconnect/";
	String TUNNEL_STATUS = "/sdn/v2.0/net/links";
	String LINK_STATUS = "/sdn/v2.0/net/links/Undelay";
	String GET_USER = "/sdn/v2.0/userconfig";
	String ADD_USER = "/sdn/v2.0/userconfig/adduser";
	String CHANGE_USER = "/sdn/v2.0/userconfig/changepassword";
	String DELETE_USER = "/sdn/v2.0/userconfig/deleteuser/";
	String GET_OPENFLOW_DEVICES = "/sdn/v2.0/of/datapaths/device";
	String GET_GATEWAYS = "/sdn/v2.0/of/datapaths/";
	String GET_FLOWS = "/sdn/v2.0/of/datapaths/flows";
	String GET_DEVICE_MATCH_ABILITY = "/sdn/v2.0/of/datapaths/";
	
}
