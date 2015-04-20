package main;

import java.net.Socket;
import java.util.List;

/**
 * Pool of threads to manage the Client requests. Each request is sent by the main server
 * via the method handleRequest(socket). The pool will translate the socket to a {@see Connection} instance,
 * which wraps the socket with useful information about that connection (ID, sender/receiver status).
 * It should keep a list of Connections to keep under control the first of the list (the sender).
 * The point-to-point TCP communication with each Client is dealt with working threads executed from the pool.
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public interface ThreadsPool {

    /**
     * Receive a socket and wraps it in a {@see Connection}. Add the connection on a List, and start a working
     * thread to keep the TCP communication with the Client connected with that socket.
     * This should be done on a separated Runnable thread.
     *
     * @param socket the socket connected with a Client.
     */
    public void handleRequest(Socket socket);

    /**
     * The list of active Connections. It might be empty.
     * The first Connection in the list identify a socket, ID and client_status of "sender",
     * all the others of "receiver".
     *
     * @return a List, maybe empty, of active Connections.
     */
    public List<Connection> getList();

}
