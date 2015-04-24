package main;

import java.io.*;
import java.net.*;
import java.util.UUID;

/**
 * // TODO so far when a sender is disconnected, the receivers are blocked waiting to get the multicast,
 * // TODO the new sender is the first new client to connect. Do tests and cleanup before fixing this.
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
            // THIS SHOULD BE A WHILE LOOP, CLIENTS SHOULD HAVE A QUICK CHECK BEFORE EVERY PACKET
            // TO SEE IF THEIR CLIENT_STATUS HAS CHANGED
            if (status == ClientStatus.SENDER) {
                sendAudioChunks();
            } else {
                getAudioChunks(); // TODO
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
     * @throws IOException          for an error during connection
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
     * @throws IOException              for a communication error
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
     * @throws IOException              for a communication error
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

    public void sendAudioChunks() {
        int n = 0; // just for testing;
        try (DatagramSocket senderSocket = new DatagramSocket(3333)) {
             // BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream("just-a-test".getBytes())))

            // boolean moreDataChunks = true;
            // while(moreDataChunks){
            while (true) {
                System.out.println("Client ready to send next audio chunk...");
                byte[] buffer = new byte[1024];
                // get the request from the server
                DatagramPacket serverPacket = new DatagramPacket(buffer, buffer.length);
                senderSocket.receive(serverPacket);
                // pack the audio data
                buffer = ("PACKET " + ++n).getBytes();

                // if(in == null) {
                // moreDataChunks = false;
                // }

                // send the audio data to the server
                InetAddress address = serverPacket.getAddress();
                int port = serverPacket.getPort();
                serverPacket = new DatagramPacket(buffer, buffer.length, address, port);
                senderSocket.send(serverPacket);
                System.out.println("Packet " + n + " sent.");
                Thread.sleep(2000); // avoid stackoverflow, testing
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            //
        }
    }

    // TODO THIS METHODS SHOULD RUN TOGETHER WITH A LISTENER THREAD IN CASE THE SENDER IS DISCONNECTED?
    public void getAudioChunks() throws IOException {
        System.out.println("Ready to receive via multicast");

        /*
        SecurityManager security = System.getSecurityManager();
        if(security==null) {

        }
        security.checkListen(MULTICAST_PORT);   // will throw exception :(
        */
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager == null) {
            System.setSecurityManager(new SecurityManager());
        }
        while (true) {
            try {
                MulticastSocket multicastSocket = new MulticastSocket(4446);
                InetAddress group = InetAddress.getByName("230.0.0.1");
                multicastSocket.joinGroup(group);
                System.out.println("joined group");

                DatagramPacket packet;

                //  while(true) {   // while(SOMETHING ELSE?)

                byte[] buffer = new byte[1024];
                packet = new DatagramPacket(buffer, buffer.length);
                multicastSocket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received via multicasting: " + received);

            } catch (SecurityException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}



