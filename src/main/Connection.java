package main;

import java.net.Socket;

/**
 * A Connection is a Client-end socket associated with an unique ID given by the Server which is connected to,
 * and a constant of enum class {@see ClientStatus} which tells whether it is the first Client to be connected or not.
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
     * Get info about whether the Connection is the "oldest" in communication with the Server.
     * Will return SENDER Client status if it is the first (i.e. "sender") active client connected
     * with the server, RECEIVER status otherwise. Both constants refer to the enum class {@see ClientStatus}
     * and are converted to String to be communicated via the network.
     *
     * @return a SENDER Client status converted to String if it is the first (i.e. "sender") active Client
     * connected with the Server, a RECEIVER status converted to String otherwise.
     */
    public String getStatus();

    /**
     * Boolean flag which returns true if the connection client_status is currently set to SENDER,
     * false otherwise.
     *
     * @return true if the connection client_status is set to SENDER, false otherwise
     */
    public boolean isSender();

    /**
     * Change the client status. This might happen when the first Connection disconnects,
     * and the second one is promoted to SENDER.
     *
     * @param status the new constant status of the Client connection, true if "sender", false if "receiver"
     */
    public void setStatus(ClientStatus status);
}
