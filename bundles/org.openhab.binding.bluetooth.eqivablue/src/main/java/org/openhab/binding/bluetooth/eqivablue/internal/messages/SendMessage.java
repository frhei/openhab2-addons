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
package org.openhab.binding.bluetooth.eqivablue.internal.messages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.bluetooth.eqivablue.EqivaBlueBindingConstants;
import org.openhab.binding.bluetooth.eqivablue.internal.OperatingMode;
import org.openhab.binding.bluetooth.eqivablue.internal.PresetTemperature;

/**
 * @author Frank Heister - Initial contribution
 */
@NonNullByDefault
public class SendMessage {
    public enum MessageStatus {
        CREATED,
        QUEUED,
        SENT,
        SENDING_CONFIRMED,
        RESPONSE_RECEIVED
    }

    private MessageStatus status = MessageStatus.CREATED;

    protected List<Integer> sequence = new ArrayList<Integer>();
    protected static final int COMMAND_SET_DATETIME = 0x03;
    protected static final int COMMAND_SET_ECO_AND_COMFORT_TEMPERATURE = 0x11;
    protected static final int COMMAND_SET_OPERATING_MODE = 0x40;
    protected static final int COMMAND_SET_TARGET_TEMPERATURE = 0x41;
    protected static final int COMMAND_SWITCH_TO_COMFORT_TEMPERATURE = 0x43;
    protected static final int COMMAND_SWITCH_TO_ECO_TEMPERATURE = 0x44;
    protected static final int COMMAND_SET_BOOST_MODE = 0x45;

    protected static final int PRIMITIVE_OPERATING_MODE_MANUAL = 0x40;
    protected static final int PRIMITIVE_OPERATING_MODE_SCHEDULED = 0x00;
    protected static final int PRIMITIVE_BOOST_MODE_ON = 0xFF;
    protected static final int PRIMITIVE_BOOST_MODE_OFF = 0x00;

    // query status is done by updating the time on device
    public static SendMessage queryStatus() {
        return updateCurrentTime();
    }

    public static SendMessage updateCurrentTime() {
        return new UpdateCurrentTimeMessage();
    }

    public static SendMessage setTargetTemperature(float temperature) {
        return new SetTargetTemperatureMessage(temperature);
    }

    public static SendMessage setEcoAndComfortTemperature(float comfortTemperature, float ecoTemperature) {
        return new SetEcoAndComfortTemperatureMessage(comfortTemperature, ecoTemperature);
    }

    public static SendMessage setBoostMode(boolean boostMode) {
        return new SetBoostModeMessage(boostMode);
    }

    public static SendMessage setOperatingModeMode(OperatingMode operatingMode) {
        return new SetOperatingModeModeMessage(operatingMode);
    }

    public static SendMessage setPresetTemperature(PresetTemperature presetTemperature) {
        SendMessage command = null;
        switch (presetTemperature) {
            case On:
                command = new SetTargetTemperatureMessage(EqivaBlueBindingConstants.ALWAYS_ON_TEMPERATURE);
                break;
            case Off:
                command = new SetTargetTemperatureMessage(EqivaBlueBindingConstants.ALWAYS_OFF_TEMPERATURE);
                break;
            case Eco:
                command = new SwitchToEcoTemperature();
                break;
            case Comfort:
                command = new SwitchToComfortTemperature();
                break;
            default:
                command = new UpdateCurrentTimeMessage();
                break;
        }
        return command;
    }

    public int[] getEncodedContent() {
        return sequence.stream().mapToInt(Integer::valueOf).toArray();
    }

    public void setStatus(MessageStatus theStatus) {
        synchronized (this) {
            status = theStatus;
            switch (status) {
                case SENDING_CONFIRMED:
                case RESPONSE_RECEIVED:
                    notify();
                    break;
                default:
                    break;
            }
        }
    }

    public boolean hasStatus(MessageStatus theStatus) {
        synchronized (this) {
            return status == theStatus;
        }
    }

    public void blockOrTimeOut(long timeout) {
        try {
            synchronized (this) {
                wait(timeout);
            }
        } catch (InterruptedException e) {
        }
    }

}
