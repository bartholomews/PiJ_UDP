package main;

import java.io.IOException;
import java.net.Socket;

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
     * @param port the port number to connect to.
     * @return the Client socket opened with the Server.
     * @throws java.io.IOException for a communication error with the Server.
     */
    public void connect(String hostname, int port) throws IOException;

    /**
     * Submit a request to the Server via the connected socket.
     * This should be used to request an unique ID number
     * and whether it is the first Client to connect or not
     *
     * @param socket the socket connected with the Server
     * @param request the Client request to be sent to the Server
     * @throws IOException for an error during connection
     * @throws NullPointerException for a null Request
     */
    public void sendRequest(Socket socket, Request request) throws IOException;

    /**
     * Get a String from the inputStream opened on the socket connected with the Server.
     *
     * @param socket the socket connected with the Server.
     * @return the String read from the InputStream.
     * @throws IOException for an error during connection or if a null String is received.
     */
    public String getString(Socket socket) throws IOException;

    /**
     * Get the current client_status, either SENDER or RECEIVER.
     *
     * @param socket the socket connected with the Server
     * @return the current status flag of the client
     * @throws IOException for a communication error
     * @throws IllegalArgumentException if an invalid ClientStatus is received
     */
    public ClientStatus getStatus(Socket socket) throws IOException;

    /**
     * Get the unique-id assigned by the Server.
     *
     * @param socket the socket connected with the Server
     * @return a String with the unique ID requested
     * @throws IOException for a communication error
     * @throws IllegalArgumentException if an invalid UUID is received
     */
    public String getID(Socket socket) throws IOException;

}
