package main;

import java.io.*;
import java.net.Socket;

/**
 * Implementation of interface {@see SocketStream} for unit testing.
 * As in {@see SocketStreamMock}, this implementation doesn't really have a socket.
 * It simulates a connection error throwing an IOException while trying to open
 * the InputStream or the OutputStream on a socket.
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public class SocketStreamError implements SocketStream {
    private InputStream in;
    private OutputStream out;

    public SocketStreamError(byte[] data) throws IOException {
        in = new ByteArrayInputStream(data);
        out = new ByteArrayOutputStream();
    }

    /**
     * Simulates a connection error while trying to open an InputStream on the socket.
     *
     * @return nothing, always throws an IOException
     * @throws IOException
     */
    @Override
    public InputStream in() throws IOException {
        throw new IOException();
    }

    /**
     * Simulates a connection error while trying to open an OutputStream on the socket.
     *
     * @return nothing, always throws an IOException
     * @throws IOException
     */
    @Override
    public OutputStream out() throws IOException {
        throw new IOException();
    }

    /**
     * {@inheritDoc}
     *
     * @return null, as it doesn't really have a socket. This method should not be used.
     */
    @Override
    public Socket getSocket() {
        return null;
    }

}
