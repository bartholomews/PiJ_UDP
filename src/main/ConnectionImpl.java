package main;

import java.net.Socket;
import java.util.UUID;

/**
 * Implementation of interface {@see Connection}
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public class ConnectionImpl implements Connection {
    private final Socket socket;
    private final UUID ID;
    private boolean isSender;

    /**
     * Constructor for a new instance of ConnectionImpl.
     *
     * @param socket the socket underlying the connection
     * @param ID the unique-ID of the Client whose connection is assigned to
     * @param isSender boolean client_status flag (true for a "sender" client, false for a "receiver"
     */
    public ConnectionImpl(Socket socket, UUID ID, boolean isSender) {
        this.socket = socket;
        this.ID = ID;
        this.isSender = isSender;
    }

    /**
     * {@inheritDoc}
     *
     * @return the Client socket opened with the Server
     */
    @Override
    public Socket getSocket() {
        return socket;
    }

    /**
     * {@inheritDoc}
     *
     * @return the unique ID of the Client connected with the Server
     */
    @Override
    public String getID() {
        return ID.toString();
    }

    /**
     * {@inheritDoc}
     *
     * @return true if it is the first (i.e. "sender") active Client connected with the Server, false otherwise
     */
    @Override
    public boolean isSender() {
        return isSender;
    }

    /**
     * {@inheritDoc}
     *
     * @param isSender the new flag status of the Client connection, true if "sender", false if "receiver"
     */
    @Override
    public void setStatus(boolean isSender) {
        this.isSender = isSender;
    }


}
