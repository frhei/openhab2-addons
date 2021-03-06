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
package org.openhab.binding.bluetooth.eqivablue.communication.states.stages;

import java.time.Duration;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.openhab.binding.bluetooth.eqivablue.handler.ThermostatUpdateListener;
import org.openhab.binding.bluetooth.eqivablue.internal.communication.CommandHandler;
import org.openhab.binding.bluetooth.eqivablue.internal.communication.EqivablueDeviceAdapter;
import org.openhab.binding.bluetooth.eqivablue.internal.communication.states.DeviceContext;
import org.openhab.binding.bluetooth.eqivablue.internal.communication.states.DeviceHandler;
import org.openhab.binding.bluetooth.eqivablue.internal.messages.EncodedReceiveMessage;
import org.openhab.binding.bluetooth.eqivablue.internal.messages.SendMessage;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;

/**
 * @author Frank Heister - Initial contribution
 */
@NonNullByDefault
@SuppressWarnings("null")
public class WhenStage extends Stage<WhenStage> {

    @ExpectedScenarioState
    @Mock
    private @Nullable EqivablueDeviceAdapter deviceAdapter;

    @ExpectedScenarioState
    @Mock
    private @Nullable DeviceContext context;

    @ExpectedScenarioState
    @Spy
    private @Nullable FakeScheduledExecutorService executorService;

    @ExpectedScenarioState
    @Spy
    private @Nullable FakeClock clock;

    @ExpectedScenarioState
    // instance will be overridden from given stage
    // instantiation just for satisfying null check analysis
    private TestContext testContext = new TestContext();

    @ExpectedScenarioState
    private @Nullable DeviceHandler deviceHandler;

    @ExpectedScenarioState
    @Mock
    private @Nullable CommandHandler commandHandler;

    @ExpectedScenarioState
    @Mock
    private @Nullable ThermostatUpdateListener thingListener;

    @ExpectedScenarioState
    @Mock
    private @Nullable EncodedReceiveMessage receivedMessage;

    @BeforeStage
    public void beforeStage() {
        // Mockito.clearInvocations(deviceAdapter);
    }

    public WhenStage adapter_received_signal_with_strength(int rssi) {
        deviceHandler.notifyReceivedSignalStrength(rssi);
        return this;
    }

    public WhenStage a_connection_is_established() {
        deviceHandler.notifyConnectionEstablished();
        return this;
    }

    public WhenStage a_connection_request_for_service_discovery_is_issued() {
        int arbitraryRssi = -45;
        Mockito.when(context.getMinimalSignalStrengthForAcceptingCommunicationToDevice()).thenReturn(arbitraryRssi);
        executorService.addListener(testContext);
        deviceHandler.notifyReceivedSignalStrength(arbitraryRssi);
        executorService.removeListener(testContext);
        return this;
    }

    public WhenStage a_service_discovery_request_is_issued() {
        int arbitraryRssi = -55;
        Mockito.when(context.getMinimalSignalStrengthForAcceptingCommunicationToDevice()).thenReturn(arbitraryRssi);
        Mockito.when(deviceAdapter.requestConnection()).thenReturn(true);
        Mockito.when(commandHandler.peekCommand()).thenReturn(SendMessage.queryStatus());
        deviceHandler.notifyReceivedSignalStrength(arbitraryRssi);
        executorService.addListener(testContext);
        deviceHandler.notifyConnectionEstablished();
        executorService.removeListener(testContext);
        return this;
    }

    public WhenStage characteristics_are_retrieved() {
        int arbitraryRssi = -55;
        Mockito.when(context.getMinimalSignalStrengthForAcceptingCommunicationToDevice()).thenReturn(arbitraryRssi);
        Mockito.when(deviceAdapter.requestConnection()).thenReturn(true);
        Mockito.when(deviceAdapter.requestDiscoverServices()).thenReturn(true);
        Mockito.when(deviceAdapter.getCharacteristics()).thenReturn(true);
        deviceHandler.notifyReceivedSignalStrength(arbitraryRssi);
        deviceHandler.notifyConnectionEstablished();
        deviceHandler.notifyServicesDiscovered();
        return this;
    }

    public WhenStage going_idle() {
        int arbitraryRssi = -55;
        Mockito.when(context.getMinimalSignalStrengthForAcceptingCommunicationToDevice()).thenReturn(arbitraryRssi);
        Mockito.when(deviceAdapter.characteristicsAreAvailable()).thenReturn(true);
        deviceHandler.notifyReceivedSignalStrength(arbitraryRssi);
        return this;
    }

    public WhenStage a_connection_request_for_command_processing_is_issued() {
        int arbitraryRssi = -45;
        Mockito.when(context.getMinimalSignalStrengthForAcceptingCommunicationToDevice()).thenReturn(arbitraryRssi);
        Mockito.when(deviceAdapter.characteristicsAreAvailable()).thenReturn(true);
        Mockito.when(commandHandler.peekCommand()).thenReturn(SendMessage.queryStatus());
        deviceHandler.notifyReceivedSignalStrength(arbitraryRssi);
        executorService.addListener(testContext);
        deviceHandler.notifyCommandProcessingRequest();
        executorService.removeListener(testContext);
        return this;
    }

    public WhenStage a_transmission_request_is_issued() {
        int arbitraryRssi = -45;
        Mockito.when(context.getMinimalSignalStrengthForAcceptingCommunicationToDevice()).thenReturn(arbitraryRssi);
        Mockito.when(deviceAdapter.characteristicsAreAvailable()).thenReturn(true);
        Mockito.when(deviceAdapter.requestConnection()).thenReturn(true);
        Mockito.when(commandHandler.peekCommand()).thenReturn(SendMessage.queryStatus());
        deviceHandler.notifyReceivedSignalStrength(arbitraryRssi);
        deviceHandler.notifyCommandProcessingRequest();
        executorService.addListener(testContext);
        deviceHandler.notifyConnectionEstablished();
        executorService.removeListener(testContext);
        return this;
    }

    public WhenStage a_transmission_request_is_sent_to_device() {
        int arbitraryRssi = -45;
        Mockito.when(context.getMinimalSignalStrengthForAcceptingCommunicationToDevice()).thenReturn(arbitraryRssi);
        Mockito.when(deviceAdapter.characteristicsAreAvailable()).thenReturn(true);
        Mockito.when(deviceAdapter.requestConnection()).thenReturn(true);
        Mockito.when(deviceAdapter.writeCharacteristic(ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(commandHandler.peekCommand()).thenReturn(SendMessage.queryStatus());
        deviceHandler.notifyReceivedSignalStrength(arbitraryRssi);
        deviceHandler.notifyCommandProcessingRequest();
        deviceHandler.notifyConnectionEstablished();
        executorService.addListener(testContext);
        deviceHandler.notifyCharacteristicWritten();
        executorService.removeListener(testContext);
        return this;
    }

    public WhenStage the_device_indicated_a_disconnect_$_consecutive_times(int maxNumberOfDisconnects) {
        int actualNumberOfDisconnects = 0;
        while (actualNumberOfDisconnects < maxNumberOfDisconnects) {
            deviceHandler.notifyConnectionClosed();
            deviceHandler.notifyConnectionEstablished();
            actualNumberOfDisconnects++;
        }
        return this;
    }

    public WhenStage the_device_indicates_a_disconnect() {
        deviceHandler.notifyConnectionClosed();
        return this;
    }

    public WhenStage service_discovery_requests_time_out_$_consecutive_times(int maxNumberOfTimeouts) {
        int actualNumberOfTimeouts = 0;
        while (actualNumberOfTimeouts < maxNumberOfTimeouts) {
            actualNumberOfTimeouts++;
            clock.elapse(Duration.ofMillis(context.getServiceDiscoveryTimeoutInMilliseconds()));
            executorService.run();
            deviceHandler.notifyConnectionEstablished();
        }
        return this;
    }

    public WhenStage connection_requests_time_out_$_consecutive_times(int maxNumberOfTimeouts) {
        int actualNumberOfTimeouts = 0;
        while (actualNumberOfTimeouts < maxNumberOfTimeouts) {
            actualNumberOfTimeouts++;
            clock.elapse(Duration.ofMillis(context.getConnectionRequestTimeoutInMilliseconds()));
            executorService.run();
        }
        return this;
    }

    public WhenStage transmission_requests_time_out_$_consecutive_times(int maxNumberOfTimeouts) {
        int actualNumberOfTimeouts = 0;
        while (actualNumberOfTimeouts < maxNumberOfTimeouts) {
            actualNumberOfTimeouts++;
            clock.elapse(Duration.ofMillis(context.getTransmissionRequestTimeoutInMilliseconds()));
            executorService.run();
        }
        return this;
    }

    public WhenStage response_times_out_$_consecutive_times(int maxNumberOfTimeouts) {
        int actualNumberOfTimeouts = 0;
        while (actualNumberOfTimeouts < maxNumberOfTimeouts) {
            actualNumberOfTimeouts++;
            clock.elapse(Duration.ofMillis(context.getResponseTimeoutInMilliseconds()));
            executorService.run();
            deviceHandler.notifyCharacteristicWritten();
        }
        return this;
    }

    public WhenStage time_elapses_by(long timeStep) {
        clock.elapse(Duration.ofMillis(timeStep));
        executorService.run();
        return this;
    }

    public WhenStage services_are_discovered() {
        deviceHandler.notifyServicesDiscovered();
        return this;
    }

    public WhenStage a_command_processing_is_requested() {
        deviceHandler.notifyCommandProcessingRequest();
        return this;
    }

    public WhenStage the_transmission_request_is_acknowledged() {
        deviceHandler.notifyCharacteristicWritten();
        return this;
    }

    public WhenStage a_response_is_received() {
        EncodedReceiveMessage message = receivedMessage;
        if (message != null) {
            deviceHandler.notifyCharacteristicUpdate(message);
        }
        return this;
    }

}
