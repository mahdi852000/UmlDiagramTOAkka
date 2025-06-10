package com.iwu.connector;
import akka.actor.typed.Behavior;

public class DMCCScannerActor {

    public static Behavior<ConnectorCommand> create(){
        return AbstractConnectorActor.create("DMCCScannerConnector");
    }

}
