package main;

import java.io.*;
import java.net.Socket;

/**
 * Implementation of interface {@see SocketStream} for unit testing.
 * It simulates a connected socket streaming the byte[] of data set at construction time.
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public class SocketStreamMock implements SocketStream {
    private InputStream in;
    private OutputStream out;

    public SocketStreamMock(byte[] data) {
        in = new ByteArrayInputStream(data);
        out = new ByteArrayOutputStream();
    }

    /**
     *  An InputStream which pretends to be connected to a socket, to be used for unit testing.
     *  In reality it contains the data set at construction time
     *
     * @return an InputStream with the data set at construction time.
     */
    @Override
    public InputStream in() {
        return in;
    }

    /**
     * An OutputStream which pretends to be connected to a socket, to be used for unit testing
     *
     * @return an OutputStream which pretends to be connected to a socket.
     */
    @Override
    public OutputStream out() {
        return out;
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

    /**
     * Retrieve the content of the OutputStream, to be used for unit testing
     *
     * @return the content of the OutputStream.
     */
    public byte[] getOutput() {
        return ((ByteArrayOutputStream) out).toByteArray();
    }

}
