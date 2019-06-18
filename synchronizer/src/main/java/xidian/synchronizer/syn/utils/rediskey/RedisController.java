/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package xidian.synchronizer.syn.utils.rediskey;

import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.controllers.rev181125.ControllerTypes.TypeName;

public class RedisController {

	private Ipv4Address ip;
	private TypeName type;
	private PortNumber port;
	
	public RedisController(Ipv4Address ip, TypeName type, PortNumber port) {
		super();
		this.ip = ip;
		this.type = type;
		this.port = port;
	}
	public PortNumber getPort() {
		return port;
	}
	public void setPort(PortNumber port) {
		this.port = port;
	}

	public Ipv4Address getIp() {
		return ip;
	}
	public void setIp(Ipv4Address ip) {
		this.ip = ip;
	}
	public TypeName getType() {
		return type;
	}
	public void setType(TypeName type) {
		this.type = type;
	}
	
}
