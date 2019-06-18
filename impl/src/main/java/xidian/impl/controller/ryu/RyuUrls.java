/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.impl.controller.ryu;

public interface RyuUrls {

	String GET_SWITCHES = "/v1.0/topology/switches";
	String GET_LINKS = "/v1.0/topology/links";
	String GET_HEALTH = "";
	String GET_SWITCHES_LIST="/stats/switches";
	
	String GET_PORT = "/stats/port/{dpidInt}";
	String GET_PORT_DESC = "/stats/portdesc/{dpidInt}";
	
	String GET_FLOW_TABLE= "/stats/flow/{dpidInt}";
}
