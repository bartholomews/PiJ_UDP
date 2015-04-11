package main;

import java.net.Socket;

/**
 * A Connection is a Client-end socket associated with an unique ID given by the Server which is connected to,
 * and a boolean value which tells whether it is the first Client to be connected or not.
 * Every Client which establishes a connection with the Server should get an instance of this class.
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public interface Connection {

    /**
     * The connection socket between the Client and the Server.
     *
     * @return the connection socket of the Client with the Server.
     */
    public Socket getSocket();

    /**
     * The unique ID which has been assigned by the Server.
     *
     * @return the unique ID of the Client connected with the Server.
     */
    public String getID();  // todo implement UUID?

    /**
     * Boolean flag which checks whether the Connection is the "oldest" in communication with the Server.
     *
     * @return true if it is the first (i.e. "oldest") active Client connected with the Server, false otherwise.
     */
    public boolean isSender();

}
