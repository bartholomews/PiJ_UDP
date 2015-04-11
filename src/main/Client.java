package main;

/**
 * The Client class connects over TCP to the Server specified by a hostname field.
 * TODO it might choose to specify the hostname from command-line, or chosen via a prompt? Should choose the port, too?
 * Once connected, the Client asks for an unique ID and whether is the first to connect.
 * Then it opens an UDP connection to the Server, and sends the audio chunks if it's the first Client,
 * or start receiving audio chunks if it's not. If the sender client gets disconnected,
 * the process which connected earlier in the pool of active ones will be the new sender.
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public interface Client {
    /**
     * Connects to the Server specified by the hostname (or IP address) at the specified port number
     *
     * @param hostname the hostname (or IP address) of the Server to connect to.
     * @throws java.io.IOException for a communication error with the Server.
     */
    public void connect(String hostname);

    /**
     * Asks the Server for an unique ID number and whether is the first Client to connect or not.
     * It could wrap such information (together with the socket) in a Connection class to be passed
     * to the UDP sendAudio() or receiveAudio() routines?
     *
     * @return a Connection which wraps the socket and the information retrieved from the Server.
     */
    public Connection getInfo();
// TODO create Connection class which wraps a Socket, ID and boolean value(first client or not)?

    // TODO UDPMethods()


}
