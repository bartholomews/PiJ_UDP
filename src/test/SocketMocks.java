package test;

import main.ClientStatus;
import main.Connection;
import main.ConnectionImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Class of static methods which are used from both {@see TestWorkerThread} and {@see TestClient} unit testing,
 * to create mock instances of Socket and Connection.
 */
public class SocketMocks {
    // a valid UUID to String to be used for testing
    static final String ID = "3fb4fa6e-2899-4429-b818-d34fe8df5dd0";

    /**
     * Return the UUID test id number
     *
     * @return the UUID test id number
     */
    public static String getTestID() {
        return ID;
    }

    /**
     * A mock of Socket.class with an OutputStream opened on it to write data to,
     * and an InputStream which pretends to read data but in reality is reading
     * the byte[] set at construction time.
     *
     * @return a valid socket's mock
     * @throws java.io.IOException
     */
    public static Socket getSocketMock(byte[] data) throws IOException {
        Socket mock = mock(Socket.class);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        when(mock.getOutputStream()).thenReturn(baos);
        when(mock.getInputStream()).thenReturn(new ByteArrayInputStream(data));
        when(mock.getRemoteSocketAddress()).thenReturn(new InetSocketAddress("some IP address", 2046));
        return mock;
    }

    /**
     * A mock of Socket.class which throws an IOException, simulating an error during reading/writing.
     *
     * @return a failing socket's mock
     * @throws IOException
     */
    public static Socket getFailingSocketMock() throws IOException {
        Socket mock = mock(Socket.class);
        when(mock.getOutputStream()).thenThrow(new IOException());
        when(mock.getInputStream()).thenThrow(new IOException());
        return mock;
    }

    /**
     * A mock of Connection.class which wraps a Socket mock with an inputStream containing the data set
     * as parameter, an ID and a the boolean status flag set as parameter.
     *
     * @param message a String converted to byte[] and placed into the underlying Socket mock inputStream.
     * @param status the client_status
     * @return the Connection mock just created
     * @throws IOException
     */
    public static Connection getConnectionMock(String message, ClientStatus status) throws IOException {
        byte[] data = message.getBytes();
        // just to check that the UUID is valid
        UUID id = UUID.fromString(ID);
        return new ConnectionImpl(getSocketMock(data), id, status);
    }

    /**
     * A mock of Connection.class which throws an IOException, having a failing Socket mock as underlying socket.
     *
     * @param status the client_status
     * @return a Connection ready to throw an IOException
     * @throws IOException
     */
    public static Connection getFailingConnectionMock(ClientStatus status) throws IOException {
        UUID id = UUID.randomUUID();
        return new ConnectionImpl(getFailingSocketMock(), id, status);
    }


}
