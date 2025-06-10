package com.iwu.connector;
import akka.actor.typed.ActorSystem;
import java.time.Duration;

public class Main {
    public static void main(String[] args) {

        ActorSystem<ConnectorCommand> system =
                ActorSystem.create(DMCCScannerActor.create(),"dmcc-system");


        system.scheduler().scheduleOnce(
                Duration.ofSeconds(5),
                ()-> system.tell(new InstructionCommand("Scan Barcode")),
                system.executionContext()
        );

        try {Thread.sleep(30_000);}
        catch (InterruptedException ignored) {
            system.terminate();
        }
    }
}