package test;

import main.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public class TestServerHandler {
    private Server server;
    private ServerHandler test;

    @Before
    public void setUp() throws IOException {
        server = new ServerImpl();
        test = new ServerHandlerImpl(server, SocketMocks.getSocketMock("data".getBytes()));
    }

    @After
    public void tearDown() {
        server = null;
        test = null;
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void createConnectionFirstConnectionShouldSetSENDERClientStatus() throws IOException {
        assertTrue(server.getList().isEmpty());
        test.createConnection();
        assertEquals(server.getList().size(), 1);
        assertEquals(server.getList().get(0).getStatus(), ClientStatus.SENDER.name());
    }

    @Test
    public void createConnectionNotFirstConnectionShouldSetRECEIVERClientStatus() throws IOException {
        assertTrue(server.getList().isEmpty());
        test.createConnection();
        test.createConnection();
        assertEquals(server.getList().size(), 2);
        assertEquals(server.getList().get(0).getStatus(), ClientStatus.SENDER.name());
        assertEquals(server.getList().get(1).getStatus(), ClientStatus.RECEIVER.name());
    }

    @Test
    public void createConnectionRemoveItNewConnectionShouldBeSENDER() {
        test.createConnection();
        assertEquals(server.getList().get(0).getStatus(), ClientStatus.SENDER.name());
        server.getList().remove(0);
        test.createConnection();
        assertEquals(server.getList().get(0).getStatus(), ClientStatus.SENDER.name());
    }

    // TODO
    @Test
    public void createConnectionSecondConnectionFirstConnectionIsDeletedShouldBeSetAsSENDERClientStatus() {
        // TODO there should be a method from UDPServer to test here which gets an error during streaming
        // delete the first of the list and promote the second one.
    }

    @Test
    public void create1000ConnectionsIDsShouldBeAllUnique() throws IOException {
        for (int i = 0; i <= 1000; i++) {
            test.createConnection();
        }
        assertFalse(server.getList().isEmpty());
        for (int i = 0; i < server.getList().size(); i++) {
            for (int j = i + 1; j < server.getList().size(); j++) {
                assertNotEquals(server.getList().get(i).getID(), server.getList().get(j).getID());
            }
        }
    }



}
