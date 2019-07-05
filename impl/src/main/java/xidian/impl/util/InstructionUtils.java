/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package xidian.impl.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class InstructionUtils {

	public static String mininetIp;
	public static String passwd;
	
	public static void moveSwitch(String mininetIp, MoveOrder order) {
		String ip = order.getDestinationIP();
		String port = order.getDestinationPort();
		String switchDpid = order.getSwitchDpid();
		int index = Integer.valueOf(switchDpid.replace(":", ""), 16);
		String switchName = "s"+index;
//		String cmd = "sshpass "+"-p "+passwd+" ssh root@"+mininetIp+" ovs-vsctl"+" set-controller "+switchName+" tcp:"+ip+":"+port;
		String cmd = "ssh root@"+mininetIp+" ovs-vsctl"+" set-controller "+switchName+" tcp:"+ip+":"+port;
		exec(cmd);
	}
	
	public static void moveSwitch( MoveOrder order) {
		String ip = order.getDestinationIP();
		String port = order.getDestinationPort();
		String switchDpid = order.getSwitchDpid();
		int index = Integer.valueOf(switchDpid.replace(":", ""), 16);
		String switchName = "s"+index;
//		String cmd = "sshpass "+"-p "+passwd+" ssh root@"+mininetIp+" ovs-vsctl"+" set-controller "+switchName+" tcp:"+ip+":"+port;
		String cmd = "ssh root@"+mininetIp+" ovs-vsctl"+" set-controller "+switchName+" tcp:"+ip+":"+port;
		exec(cmd);
	}
	
    public static String exec(String command) {
        String returnString = "";
        Process pro = null;
        Runtime runTime = Runtime.getRuntime();
        if (runTime == null) {
            System.err.println("Create runtime false!");
        }
        try {
            pro = runTime.exec(command);
            BufferedReader input = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            PrintWriter output = new PrintWriter(new OutputStreamWriter(pro.getOutputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                returnString = returnString + line + "\n";
            }
            input.close();
            output.close();
            pro.destroy();
        } catch (IOException ex) {
        }
        return returnString;
    }
}
