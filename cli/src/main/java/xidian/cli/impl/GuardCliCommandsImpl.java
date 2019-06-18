/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package xidian.cli.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xidian.cli.api.GuardCliCommands;

public class GuardCliCommandsImpl implements GuardCliCommands {

    private static final Logger LOG = LoggerFactory.getLogger(GuardCliCommandsImpl.class);
    private final DataBroker dataBroker;

    public GuardCliCommandsImpl(final DataBroker db) {
        this.dataBroker = db;
        LOG.info("GuardCliCommandImpl initialized");
    }

    @Override
    public Object testCommand(Object testArgument) {
        return "This is a test implementation of test-command";
    }
}