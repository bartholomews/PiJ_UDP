package test;

import main.Client;
import main.ClientImpl;
import main.ClientStatus;
import main.Request;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

import static org.junit.Assert.*;
import static test.SocketMocks.*;

/**
 * JUnit tests for {@see ClientImpl} methods. Sockets mocks are retrieved from static methods
 * in {@see Mocks} class.
 */
public class TestClient {
    private final String MESSAGE = "Connection test message";
    private Client client;

    @Before
    public void setUp() {
        client = new ClientImpl();
    }

    @After
    public void tearDown() {
        client = null;
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void getStringOnEmptyInputStreamShouldReturnThrowIOException() throws IOException {
      //  exception.expect(IOException.class);
        Socket mock = getSocketMock("one-liner".getBytes());
        // consumes the readLine
        client.getString(mock);
        // getString() on the empty stream
        assertNull(client.getString(mock));
    }

    @Test
    public void getStringOnFailingSocketShouldThrowIOException() throws IOException {
        exception.expect(IOException.class);
        client.getString(getFailingSocketMock());
    }

    @Test
    public void getStringOnValidSocketShouldReturnIt() throws IOException {
        Socket mock = getSocketMock("one-liner".getBytes());
        assertEquals("one-liner", client.getString(mock));
    }

    @Test
    public void getStringOnValidInputShouldReturnIt() throws IOException {
        assertEquals(MESSAGE, client.getString(getSocketMock(MESSAGE.getBytes())));
    }

    @Test
    public void getIDInvalidUUIDShouldThrowIllegalArgumentException() throws IOException {
        exception.expect(IllegalArgumentException.class);
        UUID invalid = UUID.fromString("invalid");
        byte[] id = invalid.toString().getBytes();
        client.getID(getSocketMock(id));
    }

    @Test
    public void getIDValidUUIDShouldReturnIt() throws IOException {
        assertEquals(ID, client.getID(getSocketMock(ID.getBytes())));
    }

    @Test
    public void getStatusOnValidInputShouldReturnIt() throws IOException {
        assertEquals(ClientStatus.RECEIVER, client.getStatus(getSocketMock(ClientStatus.RECEIVER.name().getBytes())));
    }

    @Test
    public void getSENDERStatusOnValidInputShouldReturnIt() throws IOException {
        assertEquals(ClientStatus.SENDER, client.getStatus(getSocketMock(ClientStatus.SENDER.name().getBytes())));
    }

    @Test
    public void getStatusInvalidInputShouldThrowIllegalArgumentException() throws IOException {
        exception.expect(IllegalArgumentException.class);
        String invalid = "Not really a ClientStatus";
        client.getStatus(getSocketMock(invalid.getBytes()));
    }

    @Test
    public void sendValidRequestInvalidSocketShouldThrowAnIOException() throws IOException {
        exception.expect(IOException.class);
        Socket fail = getFailingSocketMock();
        client.sendRequest(fail, Request.CLIENT_STATUS);
    }

    @Test
    public void sendNullRequestOnValidSocketShouldThrowNullPointerException() throws IOException {
        exception.expect(NullPointerException.class);
        Socket mock = getSocketMock(MESSAGE.getBytes());
        client.sendRequest(mock, null);
    }

    @Test
    public void testValidSocketShouldSendACLIENT_STATUSRequestToTheServer() throws IOException {
        // a socket mock (with a String as inputStream not important here, as I'm testing outputStream)
        Socket mock = getSocketMock(MESSAGE.getBytes());
        // call method sendRequest(CLIENT_STATUS)
        client.sendRequest(mock, Request.CLIENT_STATUS);
        // get the content which has been written out in the outputStream
        OutputStream out = mock.getOutputStream();
        // and convert it back to byte[]
        byte[] sent = ((ByteArrayOutputStream) out).toByteArray();
        // the String which should have been sent
        String stringToBeSent = Request.CLIENT_STATUS.name();
        // converted to byte[]
        byte[] bytesToBeSent = (stringToBeSent + "\n").getBytes();
        // the two byte[] should be equal
        assertArrayEquals(sent, bytesToBeSent);
    }

    @Test
    public void testValidSocketShouldSendAnIDRequestToTheServer() throws IOException {
        // a socket mock (with a String as inputStream not important here, as I'm testing outputStream)
        Socket mock = getSocketMock(MESSAGE.getBytes());
        // call method sendRequest(CLIENT_STATUS)
        client.sendRequest(mock, Request.ID);
        // get the content which has been written out in the outputStream
        OutputStream out = mock.getOutputStream();
        // and convert it back to byte[]
        byte[] sent = ((ByteArrayOutputStream) out).toByteArray();
        // the String which should have been sent
        String stringToBeSent = Request.ID.name();
        // converted to byte[]
        byte[] bytesToBeSent = (stringToBeSent + "\n").getBytes();
        // the two byte[] should be equal
        assertArrayEquals(sent, bytesToBeSent);
    }

}
