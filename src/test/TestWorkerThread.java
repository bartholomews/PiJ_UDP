package test;

import main.*;
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
 * JUnit tests for {@see WorkerThreadImpl} methods. Sockets and Connections mocks are retrieved from static methods
 * in {@see Mocks} class.
 */
public class TestWorkerThread {
    // a valid String message to be used for testing
    private final String MESSAGE = "Connection test " + "message";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testUsingRealSocketStreamNotConnectedShouldThrowIOException() throws IOException {
        exception.expect(IOException.class);
        // create a "real" connection, i.e. not with a mock but with a real socket, not connected
        // (and a SENDER status, not important here)
        Connection notConnected = new ConnectionImpl(new Socket(), UUID.randomUUID(), ClientStatus.SENDER);
        // and a WorkerThread with that connection
        WorkerThread fail = new WorkerThreadImpl(notConnected);
        // try to send a String through its outputStream, should throw IOException
        fail.sendString(MESSAGE);
    }

    @Test
    public void testIOExceptionDuringGetOutputStreamShouldThrowItWhileWritingOut() throws IOException {
        exception.expect(IOException.class);
        // create a mock connection (with a SENDER status, not important here)
        WorkerThread fail = new WorkerThreadImpl(getFailingConnectionMock(ClientStatus.SENDER));
        // try to send a String through its outputStream, should throw IOException
        fail.sendString(MESSAGE);
    }

    @Test
    public void testValidSocketShouldGetARequestFromTheClient() throws IOException {
        // pack a Request.ID in a String
        String request = Request.ID.name();
        // create a mock connection with that request (and a RECEIVER status, not important here)
        // i.e. mock a client which sends an ID request to the server
        WorkerThread test = new WorkerThreadImpl(getConnectionMock(request, ClientStatus.RECEIVER));
        // should receive the Request and return it
        assertEquals(test.getRequest(), Request.ID);
    }

    @Test
    public void testValidSocketShouldSendAStringToTheClientCompareValues() throws IOException {
        // a new thread with a mock connection (with not important parameters, as I'm testing outputStream here)
        WorkerThread test = new WorkerThreadImpl(getConnectionMock(MESSAGE, ClientStatus.SENDER));
        // the String to be written out to the outputStream
        String message = "A String to send to the Client";
        // call method sendString(), should return true
        assertTrue(test.sendString(message));
        // get the content which has been written out in the outputStream
        OutputStream out = test.getConnection().getSocket().getOutputStream();
        // and convert it back to byte[]
        byte[] sent = ((ByteArrayOutputStream) out).toByteArray();
        // convert to byte[] the original String, too (with newline, as println() is used with the PrintWriter)
        byte[] original = (message + "\n").getBytes();
        // compare the two byte[], should be equals
        assertArrayEquals(original, sent);
    }

    @Test
    public void testValidSocketShouldSendARequestToTheClient() throws IOException {
        // a new thread with a mock connection (with a RECEIVER status)
        WorkerThread test = new WorkerThreadImpl(getConnectionMock(MESSAGE, ClientStatus.RECEIVER));
        // call method sendRequest(CLIENT_STATUS)
        test.sendRequest(Request.CLIENT_STATUS);
        // get the content which has been written out in the outputStream
        OutputStream out = test.getConnection().getSocket().getOutputStream();
        // and convert it back to byte[]
        byte[] sent = ((ByteArrayOutputStream) out).toByteArray();
        // the String which should have been sent
        String stringToBeSent = ClientStatus.RECEIVER.name();
        // converted to byte[]
        byte[] bytesToBeSent = (stringToBeSent + "\n").getBytes();
        // the two byte[] should be equal
        assertArrayEquals(sent, bytesToBeSent);
    }

    @Test
    public void testValidSocketShouldSendAnIdToTheClient() throws IOException {
        // a new thread with a mock connection (with a SENDER status, not important here)
        WorkerThread test = new WorkerThreadImpl(getConnectionMock(MESSAGE, ClientStatus.SENDER));
        // call method sendRequest(ID)
        test.sendRequest(Request.ID);
        // get the content which has been written out in the outputStream
        OutputStream out = test.getConnection().getSocket().getOutputStream();
        // and convert it back to byte[]
        byte[] sent = ((ByteArrayOutputStream) out).toByteArray();
        // the String which should have been sent
        String stringToBeSent = getTestID();
        // converted to byte[]
        byte[] bytesToBeSent = (stringToBeSent + "\n").getBytes();
        // the two byte[] should be equal
        assertArrayEquals(sent, bytesToBeSent);
    }

}
