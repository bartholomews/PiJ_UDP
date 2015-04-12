package main;

import java.io.IOException;

/**
 * The Server class takes connection requests from multiple Clients over TCP
 * on a specified port number(s?). TODO should have one fixed final PORT field or each instance can choose a port?
 * For every request, it should acknowledge the connection and place it into a separate pool of worker threads,
 * then it should go back to receive new connections. It should accept an indefinite number of connections,
 * but at construction time it could be set the size of threads' pool
 * (i.e. how many connections can be further processed concurrently before starting queueing up new ones).
 * Another constructor could have no parameters and set a fixed value.
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public interface Server {
    /**
     * Initialise the main ServerSocket on a specified port and start listening for connections.
     * For each accepted connection a communication stream should be opened via openStream(Socket) method.
     * The Server should concentrate on listening new connections and leave their handling to other threads.
     *
     * @throws java.io.IOException
     */
    public void init() throws IOException;

    public void init(int port) throws IOException;

    /**
     * Open a communication stream with a Client socket (wrapped in a {@see SocketStream})
     * and send a message through it.
     */
    public String sendString(SocketStream sock, String message) throws IOException ;

    // TODO thread pool UDP class and here a method to send the connection details to that class (and to the Client)


}
