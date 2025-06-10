import akka.actor.testkit.typed.javadsl.ActorTestKit;
import akka.actor.typed.ActorRef;
import com.iwu.connector.*;
import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.Test;

public class DMCCScannerActorTest {

    static final ActorTestKit testKit = ActorTestKit.create();


    @AfterAll
    static void cleanup() {
        testKit.shutdownTestKit();
    }

    @Test
    public void testReceiveInstruction() {
        ActorRef<ConnectorCommand> actorRef = testKit.spawn(DMCCScannerActor.create(),"dmccScanner");

                ConnectorCommand scanCommand = new ScannerBarcodeCommand("Scan barcode");
                actorRef.tell(scanCommand);
        }
    }
