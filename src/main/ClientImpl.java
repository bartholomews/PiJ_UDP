package main;

import java.io.*;
import java.net.*;
import java.util.UUID;

/**
 *  // TODO try with resources bufferedreader and printwriter as fields bound at construction time?
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public class ClientImpl implements Client {
    // private MulticastSocket multicastSocket TODO

    /**
     * {@inheritDoc}
     *
     * @param hostname the hostname (or IP address) of the Server to connect to.
     * @param port     the port number to connect to.
     * @return the Client socket opened with the Server.
     * @throws java.io.IOException for a communication error with the Server.
     */
    @Override
    public void connect(String hostname, int port) throws IOException {
        try {
            Socket socket = new Socket(hostname, port);
            System.out.println("From " + socket.getRemoteSocketAddress() + ": " + getString(socket));
            getID(socket);
            ClientStatus status = getStatus(socket);
            System.out.println("Status received: " + status.name());

            if (status == ClientStatus.SENDER) {
                sendAudioChunks();
            } else {
            //   getAudioChunks(); TODO
            }
        } catch (IOException ex) {
            throw new IOException("Cannot establish a connection with the Server");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param socket the socket connected with the Server
     * @return the String read from the InputStream
     * @throws IOException for an error during connection or if a null String is received
     */
    public String getString(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String received = in.readLine();
        if (received == null) {
            throw new IOException("Connection opened, but Server failed to respond");
        } else {
            return received;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param socket  the socket connected with the Server
     * @param request the Client request to be sent to the Server
     * @throws IOException for an error during connection
     * @throws NullPointerException for a null Request
     */
    @Override
    public void sendRequest(Socket socket, Request request) throws IOException {
        if (request == null) {
            throw new NullPointerException("Cannot have a null request");
        }
        try {
            PrintWriter out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));
            String toSend = request.name();
            out.println(toSend);
            out.flush();
        } catch (IOException ex) {
            throw new IOException("Error while sending a Request to the Server");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param socket the socket connected with the Server
     * @return the current status flag of the client (either RECEIVER or SENDER)
     * @throws IOException for a communication error
     * @throws IllegalArgumentException if an invalid ClientStatus is received
     */
    public ClientStatus getStatus(Socket socket) throws IOException {
        sendRequest(socket, Request.CLIENT_STATUS);
        return ClientStatus.valueOf(getString(socket));
    }

    /**
     * {@inheritDoc}
     *
     * @param socket the socket connected with the Server
     * @return a String with the unique ID requested
     * @throws IOException for a communication error
     * @throws IllegalArgumentException if an invalid UUID is received
     */
    @Override
    public String getID(Socket socket) throws IOException {
        sendRequest(socket, Request.ID);
        String id = getString(socket);
        UUID.fromString(id);
        System.out.println("ID received: " + id + " from " + socket.getRemoteSocketAddress());
        return id;
    }

    public void sendAudioChunks() throws IOException {
        System.out.println("Client ready to send audio...");
        try (DatagramSocket senderSocket = new DatagramSocket(3333);
 // BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream("just-a-test".getBytes())))
        ) {
            // boolean moreDataChunks = true;
            // while(moreDataChunks){

            System.out.println("Packet sent.");
            byte[] buffer = new byte[1024];
            // get the request from the server
            DatagramPacket serverPacket = new DatagramPacket(buffer, buffer.length);
            senderSocket.receive(serverPacket);
            // pack the audio data
            buffer = "some audio data".getBytes();

            // if(in == null) {
            // moreDataChunks = false;
            // }

            // send the audio data to the server
            InetAddress address = serverPacket.getAddress();
            int port = serverPacket.getPort();
            serverPacket = new DatagramPacket(buffer, buffer.length, address, port);
            senderSocket.send(serverPacket);
        }
    }

}



