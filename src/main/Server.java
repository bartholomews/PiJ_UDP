package main;

import java.io.IOException;
import java.net.Socket;

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
     * Initialise the main ServerSocket on a fixed port and start listening for connections.
     * For each accepted connection a communication stream should be opened via openStream(Socket) method.
     * The Server should concentrate on listening new connections and leave their handling to other threads.
     *
     * @throws IOException for a communication error.
     */
    public void init() throws IOException;

    /**
     * Initialise the main ServerSocket on a port selected as parameter, and start listening for connections.
     * For each accepted connection a communication stream should be opened via openStream(Socket) method.
     * The Server should concentrate on listening new connections and leave their handling to other threads.
     *
     * @param port the port to connect to.
     * @throws IOException for a communication error.
     */
    public void init(int port) throws IOException;

    /**
     * Writes out a String message to a Client socket (wrapped inside a {@see SocketStream}).
     *
     * @param sock the SocketStream wrapping an InputStream/OutputStream that reads/writes to the Client socket.
     * @param message the String message to be sent via the SocketStream.
     * @return true after the Stream is flushed.
     * @throws IOException for a communication error.
     */
    public boolean sendString(SocketStream sock, String message) throws IOException;

    public boolean handleRequest(Socket socket);

    // TODO thread pool UDP class and here a method to send the connection details to that class (and to the Client)


}
