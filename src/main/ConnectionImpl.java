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
    private ClientStatus status;

    /**
     * Constructor for a new instance of ConnectionImpl.
     *
     * @param socket the socket underlying the connection
     * @param ID the unique-ID of the Client whose connection is assigned to
     * @param status constant client_status flag (SENDER ot RECEIVER client)
     */
    public ConnectionImpl(Socket socket, UUID ID, ClientStatus status) {
        this.socket = socket;
        this.ID = ID;
        this.status = status;
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
     * @return a SENDER Client status converted to String if it is the first (i.e. "sender") active Client
     * connected with the Server, a RECEIVER status converted to String otherwise.
     */
    @Override
    public String getStatus() {
        return status.toString();
    }

    /**
     * {@inheritDoc}
     *
     * @param status the new constant status of the Client connection, SENDER or RECEIVER
     */
    @Override
    public void setStatus(ClientStatus status) {
        this.status = status;
    }


}
