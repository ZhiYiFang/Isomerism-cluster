/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.impl.controller.floodlight;

public interface FloodlightUrls {

	String GET_SWITCHES = "/wm/core/controller/switches/json";
	String GET_LINKS = "/wm/topology/links/json";
	String GET_PORT = "/wm/core/switch/{switch-id}/port/json";
	String GET_PORT_DESC = "/wm/core/switch/{switch-id}/port-desc/json";
	String GET_HEALTH = "/wm/core/health/json";
	String GET_FLOW_TABLE = "/wm/core/switch/{switch-id}/flow/json";
	String ADD_MODIFY_FLOW ="/wm/staticflowpusher/json";
	String DELETE_ALL_FLOW = "/wm/staticflowpusher/clear/{switch-id}/json";
	String GET_ACL = "/wm/acl/rules/json";
	String ADD_ACL = "/wm/acl/rules/json";
}
