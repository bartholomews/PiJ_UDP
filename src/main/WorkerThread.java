package main;

import java.io.IOException;

/**
 * Worker thread for a {@see ThreadPool} which keeps a TCP point-to-point communication with a Client.
 * Its constructor should have a {@see Connection} to communicate with.
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public interface WorkerThread extends Runnable {

    /**
     * Main runnable method of the thread to communicate with the Client: should send an
     * acknowledge of connection, receive requests about the connection_status and ID,
     * reply to the requests providing information to the Client.
     */
     public void run();

     /**
     * Writes out a String message to a Client.
     *
     * @param toSend the String message to be sent via the socket.
     * @return true after the stream is flushed.
     * @throws IOException for a communication error.
     */
    public boolean sendString(String toSend) throws IOException;

    /**
     * Reads a {@see Request} from a Client.
     *
     * @return the request received.
     * @throws IOException for an error during communication.
     */
    public Request getRequest() throws IOException;

    /**
     * Returns the {@see Connection} the thread is working with.
     *
     * @return the connection assigned to the thread.
     */
    public Connection getConnection();

}
