package main;

import java.io.IOException;

/**
 * Class which deals with UDP Connection with a Client (identified as a {@see Connection} instance).
 * Its Runnable thread should be launched by the main {@see Server}, and the Server itself should reference
 * itself at construction time in order to give access to its getter methods.
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public interface UDPServer extends Runnable {
// TODO best practice: extending Runnable and overriding run() in this interface or implementing in UDPServerImpl?

    /**
     * Periodically check the Server's list of active Connection. If at least one RECEIVER client is in the list,
     * it will start multicasting the audio data which should be already being streamed by the SENDER client.
     *
     */
    @Override
    public void run();

    /**
     * Send a UDP request to the Client identified by the Connection taken as parameter, start receiving
     * chunks of audio data from it and store it to be used by the multicasting routine.
     * If the SENDER Client disconnects during the streaming, its Connection should be removed from the
     * Server's list of Connections and the next oldest active Client should be promoted to new SENDER.
     *
     * @param connection the Connection with the SENDER Client
     * @throws IOException for an error during connection. If the error happens during the UDP transmission
     * the exception should be caught and recovered with the deletion of the old and selection of a new SENDER.
     * The exception will be thrown only if an IO Error occurs during this second recovery phase.
     */
    public void getSenderAudio(Connection connection) throws IOException;

    /**
     * Send packets of data (retrieved by the byte[] given as parameter) via multicast to any RECEIVER Client
     * connected to its InetAddress group and port.
     * TODO IOException to catch a timeout (i.e. the SENDER is disconnected?)
     *
     * @param data the data to be sent via multicasting
     * @throws IOException for an error during connection.
     */
    public void multicastAudio(byte[] data) throws IOException;



}
