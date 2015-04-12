package main;

/**
 * Enum class for the types of requests a Client can submit to the Server.
 *
 * @author federico.bartolomei (BBK-Pij-2014-21)
 */
public enum Request {
    /**
     * A request for an unique ID number provided by the Server to a new connection
     */
    ID,

    /**
     * A request about the status of a connected Client, that is whether it is the first to be connected
     * (in which case it would be a sender Client) or not (in which case it is a receiver Client).
     * When the first connection drops, the next oldest connection is promoted to sender status.
     */
    CLIENT_STATUS
}
