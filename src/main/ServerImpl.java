package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implementation of interface {@see Server}
 * The program runs in an infinite loop, so it should be closed manually from whatever prompt (e.g. ctrl-c)
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public class ServerImpl implements Server {
    private ExecutorService threadsPool;
    private IdGenerator idGenerator;
    private List<Connection> connections;
    private Runnable udpServer;


    public ServerImpl() throws IOException {
        threadsPool = Executors.newFixedThreadPool(10);
        idGenerator = new IdGeneratorImpl();
        connections = new LinkedList<>();
    //    udpServer = new UDPServerImpl(this); TODO
    }

    /**
     * {@inheritDoc}
     *
     * Start the Server at port 2046 and start listening for connections.
     * For any accepted connection it sends an acknowledgement message
     * and leave further handling to another thread.
     *
     * @throws IOException for a communication error.
     */
    @Override
    public void init() throws IOException {
        init(2046);
    }

    /**
     * {@inheritDoc}
     *
     * Start the Server at the port specified and start listening for connections.
     * For any accepted connection it sends an acknowledgement message
     * and leave further handling to an handler thread ({@see ServerHandler}).
     *
     * @param port the port number for the Server.
     * @throws IOException for a communication error.
     */
    @Override
    public void init(int port) throws IOException {
        Thread multicast = new Thread(udpServer);
        multicast.start();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while(true) {
                System.out.println("Listening on port " + port + "...");
                Socket sock = serverSocket.accept();
                Runnable handler = new ServerHandlerImpl(this, sock);
                Thread handleRequest = new Thread(handler);
                handleRequest.start();
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return a List, maybe empty, of active Connections.
     */
    @Override
    public List<Connection> getList() {
        return connections;
    }


    /**
     * {@inheritDoc}
     *
     * @return the Pool of {@see WorkerThread} instances
     */
    public ExecutorService getPool() {
        return threadsPool;
    }

    /**
     * {@inheritDoc}
     *
     * @return an unique ID-number generated via a {@see IDGenerator} instance
     */
    @Override
    public UUID generateID() {
        return idGenerator.generateID();
    }

    /**
     * {@inheritDoc}
     *
     * @return the instance of {@see UDPServer}
     */
    @Override
    public UDPServerImpl getUdpServer() {
        return null; // (UDPServerImpl) udpServer; TODO
    }

}
