/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.bluetooth.eqivablue.internal.communication.states;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * @author Frank Heister - Initial contribution
 */
@NonNullByDefault
class WaitingForDisconnectState extends ConnectedState {

    WaitingForDisconnectState(DeviceHandler theHandler) {
        super(theHandler);
    }

    @Override
    void onEntry() {
        deviceHandler.requestDisconnect();
    }

}
