package com.iwu.connector;

import akka.actor.testkit.typed.javadsl.ActorTestKit;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class DMCCScannerActorTest {

    static final ActorTestKit testKit = ActorTestKit.create();



    @Test
    public void testManualHeartbeatSendsMessage() {
        TestProbe<ConnectorCommand> manualProbe = testKit.createTestProbe();

        ActorRef<ConnectorCommand> manualActor = testKit.spawn(
                DMCCScannerActor.create("DMCCScannerConnector_manual", manualProbe.getRef()),
                "dmccScannerManual"
        );

        manualActor.tell(new InstructionCommand("SendHeartbeatManually"));

        ConnectorCommand manualMsg = manualProbe.receiveMessage(Duration.ofSeconds(3));
        Assertions.assertInstanceOf(Heartbeat.class, manualMsg, "Expected a Heartbeat message");

        Heartbeat manualHb = (Heartbeat) manualMsg;
        Assertions.assertEquals("DMCCScannerConnector_manual", manualHb.source(), "Unexpected heartbeat source");
    }

    @Test
   public void testAutomaticHeartbeatStopsAfterMax() {
        TestProbe<ConnectorCommand> autoProbe = testKit.createTestProbe();

        ActorRef<ConnectorCommand> autoActor = testKit.spawn(
                DMCCScannerActor.create("DMCCScannerConnector_auto", autoProbe.getRef()),
                "dmccScannerAuto"
        );

        int expectedHeartbeats = 10;
        for (int i = 1; i < expectedHeartbeats; i++) {
            ConnectorCommand autoMsg = autoProbe.receiveMessage(Duration.ofSeconds(3));
            Assertions.assertInstanceOf(Heartbeat.class, autoMsg, "Expected Heartbeat message");
            Heartbeat autoHb = (Heartbeat) autoMsg;
            Assertions.assertEquals("DMCCScannerConnector_auto", autoHb.source(), "heartbeat #"+i);
            System.out.println("Current i value: " + i);
        }

        // بعد از 10 بار، نباید پیام دیگه‌ای دریافت بشه
        autoProbe.expectNoMessage(Duration.ofSeconds(3));
    }
    @AfterAll
    static void cleanup() {
        testKit.shutdownTestKit();
    }
}
