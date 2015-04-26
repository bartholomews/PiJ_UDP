package main;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of {@see UDPServer}.
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public class UDPServerImpl implements UDPServer {
    private List<Connection> connections;
    private int FIVE_SECONDS = 5000;
//    private BufferedReader in = null;
//    private boolean moreData = true;
//    private File audioFile;
    private DatagramSocket socketToMulticast;
    private InetAddress group;
    private byte[] data;    // TO SORT OUT

    public UDPServerImpl() throws IOException {
        connections = new LinkedList<>();
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        while (true) {
            try {
                // SYNCHRONIZED?
                while (connections.size() < 2 || data == null) {
                    Thread.sleep(1000);
                }
                multicastAudio(data);
            } catch (InterruptedException ex) {
                // do nothing
            } catch (IOException ex) {
                System.out.println("There has been an error while multicasting audio data");
                ex.printStackTrace();
            }
        }
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
            try (DatagramSocket senderSocket = new DatagramSocket()) {
                InetAddress address = connection.getSocket().getInetAddress();
                // send request
                byte[] buffer = new byte[2048];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 3333);
                senderSocket.send(packet);
                // get response
                packet = new DatagramPacket(buffer, buffer.length);
                senderSocket.setSoTimeout(FIVE_SECONDS); // 5 sec timeout before closing the connection with the client
                senderSocket.receive(packet);
                // display response
                System.out.println("Packet received from " + connection.getID() + " (" + connection.getStatus() + ")");
                data = buffer;
            } catch (IOException ex) {
                getNewSender(connection);
            }
        }
    }

    public synchronized void getNewSender(Connection connection) throws IOException {
        System.out.println(connection.getID() + " (" + connection.getStatus() + ") disconnected");
        // SYNCHRONIZED
        connections.remove(connection);
        connection.getSocket().close();
        if (!connections.isEmpty()) {
            // THIS SHOULD BE SYNCHRONIZED WITH CREATECONNECTION() IN SERVERHANDLER
            System.out.println("Getting a new sender..");
            Connection newSender = connections.get(0);
            newSender.setStatus(ClientStatus.SENDER);
     //       getSenderAudio(newSender);// TODO let the new sender know and open a new UDP
        } else {
            System.out.println("No other Client is connected so far. Listening on port 2046...");
        }
    }

    /**
     * Return the packed of byte[] received from the SENDER Client via UDP;
     * that is, after the method getSenderAudio(Connection).
     *
     * @return the last packet received from the SENDER Client
     */
    public byte[] getData() {
        return data;
    }

    /**
     * {@inheritDoc}
     *
     * @param data
     * @throws java.io.IOException
     */
    @Override
    public void multicastAudio(byte[] data) throws IOException {
        //       while (true) {
        try {
            //      byte[] chunk = data;
            DatagramPacket packet = new DatagramPacket(data, data.length, group, 4446);
            socketToMulticast.send(packet);
            System.out.println(packet.toString() + "sent via multicasting");
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            //
        } catch (IOException ex) {
            System.out.println("There has been an error while multicasting");
            // TODO should set a timer and catch the IOException with new getSenderAudio() ?
        }

        //    DatagramPacket packet = new DatagramPacket(buffer, buffer.length)
    }
//   }

    /**
     * {@inheritDoc}
     *
     * @return a List, maybe empty, of active Connections.
     */
    @Override
    public List<Connection> getList() {
        return connections;
    }

}
