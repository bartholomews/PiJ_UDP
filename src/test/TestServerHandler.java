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
        assertTrue(server.getUdpServer().getList().isEmpty());
        test.createConnection();
        assertEquals(server.getUdpServer().getList().size(), 1);
        assertEquals(server.getUdpServer().getList().get(0).getStatus(), ClientStatus.SENDER.name());
    }

    @Test
    public void createConnectionNotFirstConnectionShouldSetRECEIVERClientStatus() throws IOException {
        assertTrue(server.getUdpServer().getList().isEmpty());
        test.createConnection();
        test.createConnection();
        assertEquals(server.getUdpServer().getList().size(), 2);
        assertEquals(server.getUdpServer().getList().get(0).getStatus(), ClientStatus.SENDER.name());
        assertEquals(server.getUdpServer().getList().get(1).getStatus(), ClientStatus.RECEIVER.name());
    }

    @Test
    public void createConnectionRemoveItNewConnectionShouldBeSENDER() {
        test.createConnection();
        assertEquals(server.getUdpServer().getList().get(0).getStatus(), ClientStatus.SENDER.name());
        server.getUdpServer().getList().remove(0);
        test.createConnection();
        assertEquals(server.getUdpServer().getList().get(0).getStatus(), ClientStatus.SENDER.name());
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
        assertFalse(server.getUdpServer().getList().isEmpty());
        for (int i = 0; i < server.getUdpServer().getList().size(); i++) {
            for (int j = i + 1; j < server.getUdpServer().getList().size(); j++) {
                assertNotEquals(server.getUdpServer().getList().get(i).getID(), server.getUdpServer().getList().get(j).getID());
            }
        }
    }



}
