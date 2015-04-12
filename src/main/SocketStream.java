package main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Wrapper class for a socket. It gets a socket at construction time, and provide the methods
 * to read and write to that socket. It can be implemented for production code to be used with
 * a BufferedReader/InputStreamReader and with a PrintWriter, and in unit testing to create a mock
 * implementation with a dummy socket and some byte[] testing data.
 */
public interface SocketStream {

    /**
     * Creates an InputStream on the underlying socket.
     *
     * @return an InputStream that read from the Socket specified with the constructor.
     * @throws IOException
     */
    public InputStream in() throws IOException;

    /**
     * Creates an OutputStream on the underlying socket.
     *
     * @return an OutputStream that writes to the Socket specified with the constructor.
     * @throws IOException
     */
    public OutputStream out() throws IOException;

    /**
     * Expose the underlying socket.
     *
     * @return the underlying socket.
     */
    public Socket getSocket();

}
