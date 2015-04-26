package main;

import java.io.IOException;

/**
 * Runnable class which manage one Client requests as a new thread. A request is sent by the main {@see Server}
 * which should reference itself at construction time, in order to give access to its getter methods.
 * The ServerHandler should also be provided of the socket connected with the Client.
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public interface ServerHandler extends Runnable {
    /**
     * Get the socket provided by the caller class ({@see Server} at construction time and wraps it
     * in a {@see Connection} instance together with the Client unique-ID and {@see CientStatus}.
     * It will pass the Connection details to a worker thread of the Server's ThreadPool to keep a
     * TCP connection with the Client. Once that thread is returned with good result, this method can
     * initiate an UDP connection for data transfer with the Client, calling the Server's instance
     * of {@see UDPServer}.
     *
     * Receive a socket and wraps it in a {@see Connection}. Add the connection on a List, and start a working
     * thread to keep the TCP communication with the Client connected with that socket.
     * This should be done on a separated Runnable thread.
     *
     */
    @Override
    public void run();

    /**
     * Create a {@see Connection} which wraps a client socket, an unique id-number and a
     * Client_status (SENDER or RECEIVER). Adds it to the server list of connections and return it.
     * This method should be synchronized in order to have just one thread at the time accessing the
     * Server's Connections list.
     *
     * @return the newly create Connection
     */
    public Connection createConnection();

    /**
     * Close a connection which doesn't respond with the server. This method should be called
     * after a failed attempt to communicate via TCP. The connection should be removed from
     * the server's connections list and, if that was a SENDER connection, a new sender should be
     * selected.
     *
     * @param connection the connection to be closed
     * @throws IOException for an error during network communication
     */
    public void closeConnection(Connection connection) throws IOException;

}
