package com.iwu.connector;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import akka.actor.typed.javadsl.ActorContext;


import java.time.Duration;


public class AbstractConnectorActor extends AbstractBehavior<ConnectorCommand> {
    private final String name;
    private int heartbeatCount=0;
    protected final ActorRef<ConnectorCommand> replyTo;
    private final TimerScheduler<ConnectorCommand> timers;

   /* public static Behavior<ConnectorCommand> create(String name, ActorRef<ConnectorCommand> replyTo) {
        return Behaviors.withTimers(
                timers-> Behaviors.setup(ctx-> new
                        AbstractConnectorActor(ctx,timers,name,replyTo)));
    }*/


    AbstractConnectorActor(
            ActorContext<ConnectorCommand>
                    context, TimerScheduler<ConnectorCommand> timers, String name, ActorRef<ConnectorCommand> replyTo) {
        super(context);
        this.name = name;
        this.timers = timers;
        this.replyTo= replyTo;



        context.getLog().info("{} started", name);

       timers.startTimerAtFixedRate("heartbeat", new Heartbeat(name), Duration.ofSeconds(0), Duration.ofSeconds(2));
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
        if (cmd.command().equalsIgnoreCase("SendHeartbeatManually")) {
            sendHeartbeat(name, replyTo);
        }
        return this;
    }

    private Behavior<ConnectorCommand> onHeartbeat (Heartbeat tick) {
        heartbeatCount++;
        if(heartbeatCount >= 10) {
            getContext().getLog().info("[{}] Reached max heartbeat count, stopping timer.", name);
            timers.cancel("heartbeat");
            return this;
        }

        getContext().getLog().info("[{}] Heartbeat {} received from: {}", name, heartbeatCount, tick.source());
        sendHeartbeat(tick.source(), replyTo);  // فقط به probe ارسال کن

        return this;
    }

    public static void sendHeartbeat(String name, ActorRef<ConnectorCommand> target) {
        if(target != null) {
            target.tell(new Heartbeat(name));
        }

    }

}
