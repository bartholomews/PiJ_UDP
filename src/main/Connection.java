package main;

import java.net.Socket;

/**
 * A Connection is a Client-end socket associated with an unique ID given by the Server which is connected to,
 * and a boolean value which tells whether it is the first Client to be connected or not.
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public interface Connection {

    /**
     * The Client socket opened with the Server
     *
     * @return the Client socket opened with the Server
     */
    public Socket getSocket();

    /**
     * The unique ID which has been assigned by the Server.
     *
     * @return the unique ID of the Client connected with the Server
     */
    public String getID();

    /**
     * Boolean flag to check whether the Connection is the "oldest" in communication with the Server.
     *
     * @return true if it is the first (i.e. "sender") active Client connected with the Server, false otherwise
     */
    public boolean isSender();

    /**
     * Change the state of isSender the boolean flag. This might happen when the first Connection disconnects,
     * and the second one is promoted to sender_status.
     *
     * @param isSender the new flag status of the Client connection, true if "sender", false if "receiver"
     */
    public void setStatus(boolean isSender);
}
