package test;

import main.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.net.Socket;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class TestServer {
    private String MESSAGE = "Connection test message";
    private Server server;

    @Before
    public void setUp() throws IOException {
        server = new ServerImpl();
    }

    @After
    public void tearDown() {
        server = null;
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testServerUsingRealSocketStreamNotConnectedShouldThrowIOException() throws IOException {
        exception.expect(IOException.class);
        SocketStream notConnected = new SocketStreamImpl(new Socket());
        server.sendString(notConnected, MESSAGE);
    }

    @Test
    public void testServerUsingIOExceptionSocketStreamShouldThrowAnIOExceptionWhileWritingOut() throws IOException {
        exception.expect(IOException.class);
        // a byte[] of data for the SocketStreamMock constructor (it won't be used here)
        byte[] data = "Not_used_as_here_I'm_testing_OutputStream".getBytes();
        // a SocketStream which throws an IOException while its socket tries to get the outputStream
        SocketStream failing = new SocketStreamError(data);
        server.sendString(failing, MESSAGE);
    }

    @Test
    public void testServerUsingMockSocketStreamShouldWriteOutAStringMessage() throws IOException {
        // a byte[] of data for the SocketStreamMock constructor (it won't be used here)
        byte[] data = "Not_used_as_here_I'm_testing_OutputStream".getBytes();
        // a SocketStream mock to be used as OutputStream socket by the Server
        SocketStreamMock mock = new SocketStreamMock(data);
        assertTrue("Server wrote out on the socket mock", server.sendString(mock, MESSAGE));
        // get the byte[] in the socket outputStream
        byte[] outputSocket = mock.getOutput();
        // compare the bytes wrote out by the server to the socket with the outputStream of the socket
        assertArrayEquals(MESSAGE.getBytes(), outputSocket);
    }




}
