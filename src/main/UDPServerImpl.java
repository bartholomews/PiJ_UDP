package main;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Implementation of {@see UDPServer}.
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public class UDPServerImpl implements UDPServer {
    private Server server;
    private int FIVE_SECONDS = 5000;
//    private BufferedReader in = null;
//    private boolean moreData = true;
//    private File audioFile;
    private DatagramSocket socketToMulticast;
    private InetAddress group;
    private byte[] data;    // TO SORT OUT

    public UDPServerImpl(Server server) throws IOException {
        this.server = server;
        socketToMulticast = new MulticastSocket(3332);
        group = InetAddress.getByName("230.0.0.1");

     //   audioFile = file;
        /*
        try {
            in = new BufferedReader(new FileReader(audioFile));
        } catch (FileNotFoundException ex) {
            System.out.println("Cannot open file");
        }
        */
    }

    @Override
    public void run() {
        // TODO
    }

    //    AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioFile);

    /**
     *
     *
     * @param connection
     * @throws IOException
     */
    @Override
    public void getSenderAudio(Connection connection) throws IOException {
        while (true) {
            System.out.println("Server requesting audio data from sender client " + connection.getID());
            // get a datagram socket (try-with-resources)
            try (DatagramSocket senderSocket = new DatagramSocket()) {
                InetAddress address = connection.getSocket().getInetAddress();
                // send request
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 3333);
                senderSocket.send(packet);
                System.out.println("Request sent.");
                senderSocket.setSoTimeout(FIVE_SECONDS); // 5 sec timeout before closing the connection with the client
                // get response
                packet = new DatagramPacket(buffer, buffer.length);
                // ByteArrayInputStream byteIn = new ByteArrayInputStream(received.getData());
                senderSocket.receive(packet);
                // display response
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Packet received: " + received);
                data = buffer;
            } catch (IOException ex) {
                System.out.println(connection.getID() + " (" + connection.getStatus() + ") disconnected");
                // SYNCHRONIZED
                server.getList().remove(connection);
                connection.getSocket().close();
                if (!server.getList().isEmpty()) {
                    // THIS SHOULD BE SYNCHRONIZED WITH CREATECONNECTION() IN SERVERHANDLER
                    System.out.println("Getting a new sender..");
                    Connection newSender = server.getList().get(0);
                    newSender.setStatus(ClientStatus.SENDER);
                    getSenderAudio(newSender);
                }
                // TODO let the new sender know and open a new UDP
            }
        }
    }

    @Override
    public void multicastAudio(byte[] data) throws IOException {
        return;    // TODO
    }


}
