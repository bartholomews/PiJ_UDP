package main;

import java.io.*;
import java.net.Socket;

/**
 * Implementation of interface {@see SocketStream} for production code.
 * It should be further wrapped into a BufferedReader and a PrintWriter.
 *
 * @author federico.bartolomei (BBK-PiJ-2014)
 */
public class SocketStreamImpl implements SocketStream {
    private Socket socket;

    public SocketStreamImpl(Socket socket) {
        this.socket = socket;
    }

    /**
     * {@inheritDoc}
     *
     * @return an InputStream connected to the Socket specified with the constructor.
     * @throws IOException if a problem is encountered during the connection.
     */
    @Override
    public InputStream in() throws IOException {
        return socket.getInputStream();
    }

    /**
     * {@inheritDoc}
     *
     * @return an OutputStream connected to the Socket specified with the constructor.
     * @throws IOException if a problem is encountered during the connection.
     */
    @Override
    public OutputStream out() throws IOException {
        return socket.getOutputStream();
    }

    /**
     * {@inheritDoc}
     *
     * @return the underlying socket.
     */
    @Override
    public Socket getSocket() {
        return socket;
    }

}
