package test;

import main.Server;
import main.ServerImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class TestServer {
    private final String MESSAGE = "Connection test message";
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

    /**
     * A mock of Socket.class with an OutputStream opened on it to write data to.
     *
     * @return a valid socket's mock
     * @throws IOException
     */
    public Socket getSocketMock() throws IOException {
        Socket mock = mock(Socket.class);
        when(mock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        return mock;
    }

    /**
     * A mock of Socket.class which throws an IOException during the getOutputStream(),
     * simulating an error during writing out to stream.
     *
     * @return a failing socket's mock
     * @throws IOException
     */
    public Socket getFailingSocketMock() throws IOException {
        Socket mock = mock(Socket.class);
        when(mock.getOutputStream()).thenThrow(new IOException());
        return mock;
    }

    @Test
    public void testServerUsingRealSocketStreamNotConnectedShouldThrowIOException() throws IOException {
        exception.expect(IOException.class);
        Socket notConnected = new Socket();
        server.sendString(notConnected, MESSAGE);
    }

    @Test
    public void testIOExceptionDuringGetOutputStreamShouldThrowItWhileWritingOut() throws IOException {
        exception.expect(IOException.class);
        Socket failing = getFailingSocketMock();
        server.sendString(failing, MESSAGE);
    }

    @Test
    public void testValidSocketShouldWriteOutStringMessageCompareContent() throws IOException {
        Socket mock = mock(Socket.class);
        when(mock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        assertTrue(server.sendString(mock, MESSAGE));
        byte[] data = ((ByteArrayOutputStream) mock.getOutputStream()).toByteArray();
        assertArrayEquals(MESSAGE.getBytes(), data);
    }

}
