/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.impl.controller.opendaylight;

public interface OpendaylightUrls {

    String GET_SWITCHES = "/restconf/operational/opendaylight-inventory:nodes";
    String GET_LINKS = "/restconf/operational/network-topology:network-topology";
    String GET_PORT = "/restconf/operational/opendaylight-inventory:nodes/node/{switch-id}";
    String GET_PORT_DESC = "/restconf/operational/opendaylight-inventory:nodes/node/{switch-id}";
    String GET_HEALTH = "";

    String GET_FLOW_TABLE = "/restconf/operational/opendaylight-inventory:nodes/node/{switch-id}/flow-node-inventory:table/0";
    String ADD_MODIFY_FLOW ="";
    String DELETE_ALL_FLOW = "";
    String GET_ACL = "";
    String ADD_ACL = "";
}
