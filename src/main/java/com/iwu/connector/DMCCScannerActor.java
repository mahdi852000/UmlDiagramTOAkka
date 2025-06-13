package com.iwu.connector;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

public class DMCCScannerActor {

    private static Behavior<ConnectorCommand> build(String name, ActorRef<ConnectorCommand> replyTo) {
        return Behaviors.withTimers(
                timers -> Behaviors.setup(ctx -> new AbstractConnectorActor(ctx, timers, name, replyTo)
                )
        );
    }
    public static Behavior<ConnectorCommand> create() {
        return build("DMCCScannerConnector", null); // یا از ctx.getSelf() داخل کلاس استفاده کن
    }
    public static Behavior<ConnectorCommand> create(String name, ActorRef<ConnectorCommand> replyTo) {
        return build(name, replyTo);
    }


}
