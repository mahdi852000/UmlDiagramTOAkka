package com.iwu.connector;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import akka.actor.typed.javadsl.ActorContext;


import java.time.Duration;

public class AbstractConnectorActor extends AbstractBehavior<ConnectorCommand> {
    private final String name;

    public static Behavior<ConnectorCommand> create(String name) {
        return Behaviors.withTimers(
                timers-> Behaviors.setup(ctx-> new
                        AbstractConnectorActor(ctx,timers,name)));
    }

    private AbstractConnectorActor( ActorContext<ConnectorCommand>
                                            context,TimerScheduler<ConnectorCommand> timers, String name) {
        super(context);
        this.name = name;

        context.getLog().info("{} started", name);

       timers.startTimerAtFixedRate(new Heartbeat(name), Duration.ofSeconds(10));
    }
    @Override
    public Receive<ConnectorCommand> createReceive(){
        return newReceiveBuilder()
                .onMessage(InstructionCommand.class, this::onInstruction)
                .onMessage(Heartbeat.class, this::onHeartbeat)
                .onMessage(ScannerBarcodeCommand.class, this::onScanBarcodeCommand)
                .build();
    }

    private Behavior<ConnectorCommand> onScanBarcodeCommand(ScannerBarcodeCommand cmd){
        getContext().getLog().info("[{}] Received scan barcode command: {}", name, cmd.barcode());
        return this;
    }

    private Behavior<ConnectorCommand> onInstruction(InstructionCommand cmd){
        getContext().getLog().info("[{}] Received instruction: {}", name, cmd.command());
        return this;
    }

    private Behavior<ConnectorCommand> onHeartbeat (Heartbeat tick) {
        sendHeartbeat(tick.source(),getContext().getSelf());
        return this;
    }
    public static void sendHeartbeat(String name,  ActorRef<ConnectorCommand>target) {
        target.tell(new Heartbeat(name));
    }

}
