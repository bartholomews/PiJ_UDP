package main;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

/**
 * The Server class takes connection requests from multiple Clients over TCP
 * on a specified port number(s?). TODO should have one fixed final PORT field or each instance can choose a port?
 * For every request, it should acknowledge the connection and place it into a separate pool of worker threads,
 * then it should go back to receive new connections. It should accept an indefinite number of connections,
 * but at construction time it could be set the size of threads' pool (i.e. how many connections can be further
 * processed concurrently before starting queueing up new ones). Another constructor could have no parameters
 * and set a fixed value for the pool's size.
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public interface Server {
    /**
     * Initialise the main ServerSocket on a fixed port and start listening for connections.
     * The Server should concentrate on listening new connections and leave their handling to other threads.
     *
     * @throws IOException for a communication error.
     */
    public void init() throws IOException;

    /**
     * Initialise the main ServerSocket on a port selected as parameter, and start listening for connections.
     * The Server should concentrate on listening new connections and leave their handling to other threads.
     *
     * @param port the port to connect to.
     * @throws IOException for a communication error.
     */
    public void init(int port) throws IOException;

    /**
     * Getter for the list of active {@see Connections}. It might be empty.
     * Each Connection in the list wraps a Socket connected with a specific Client,
     * that Client's unique ID and {@see ClientStatus} of SENDER or RECEIVER.
     *
     * @return a List, maybe empty, of active Connections.
     */
    public List<Connection> getList();

    /**
     * Getter for the Pool of worker Threads which concurrently handle a TCP Connection with each Client.
     * This method is to be accessed by a separate thread, {@see ServerHandler}.
     *
     * @return the Pool of {@see WorkerThread} instances
     */
    public ExecutorService getPool();

    /**
     * Getter for the unique ID-number to assign to each Client.
     * This method is to be accessed by a separate thread, {@see ServerHandler}.
     *
     * @return an unique ID-number generated via a {@see IDGenerator} instance
     */
    public UUID generateID();

    /**
     * Getter for the instance of {@see UDPServer}, which listens for UDP Connections
     * and provide the transmission of audio chunks (receiving from the SENDER Client
     * packets and multicasting to RECEIVER Clients).
     *
     *
     * @return the instance of {@see UDPServer}
     */
    public UDPServer getUdpServer();

}
