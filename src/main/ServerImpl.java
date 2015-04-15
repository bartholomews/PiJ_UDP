package main;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Implementation of interface {@see Server}
 * The program runs in an infinite loop, so it should be closed manually from whathever prompt (e.g. crtl-c)
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public class ServerImpl implements Server {

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
                Socket sock = serverSocket.accept();
                System.out.println("Listening on port " + port + "...");
                sendString(sock, "Connection established with: " + sock.getRemoteSocketAddress());
                // handleRequest(socket);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param socket the socket connected with the Client.
     * @param message the String message to be sent via the socket.
     * @return true after the stream is flushed.
     * @throws IOException for a communication error.
     */
    @Override
    public boolean sendString(Socket socket, String message) throws IOException {
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            System.out.println(message);
            out.print(message);
            out.flush();
            return true;
        }
    }

    @Override
    public boolean handleRequest(Socket socket) {
        // pool.handleClient(socket); TODO
        return true;
    }

}
