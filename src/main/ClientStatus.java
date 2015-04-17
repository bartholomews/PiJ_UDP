package main;

/**
 * Enum class for the two possible Client status flags: the first (i.e. "oldest") client currently connected
 * with the Server has a SENDER status, all the others a RECEIVER status. When a SENDER Client is disconnected,
 * the next oldest connection will be promoted to new SENDER.
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public enum ClientStatus {
    /**
     *
     */
    SENDER,

    /**
     *
     */
    RECEIVER

}
