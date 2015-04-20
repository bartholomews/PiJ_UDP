package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Implementation of interface {@see Server}
 * The program runs in an infinite loop, so it should be closed manually from whatever prompt (e.g. ctrl-c)
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public class ServerImpl implements Server {
    private ThreadsPool pool;// TODO

    public ServerImpl() {
        pool = new ThreadsPoolImpl(10);
    }

    public ServerImpl(int n) {
        pool = new ThreadsPoolImpl(n);
    }

    /**
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
     * Start the Server at the port specified and start listening for connections.
     * For any accepted connection it sends an acknowledgement message
     * and leave further handling to another thread.
     *
     * @param port the port number for the Server.
     * @throws IOException for a communication error.
     */
    @Override
    public void init(int port) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while(true) {
                System.out.println("Listening on port " + port + "...");
                Socket sock = serverSocket.accept();
                pool.handleRequest(sock); //TODO
            }
        }
    }


}
