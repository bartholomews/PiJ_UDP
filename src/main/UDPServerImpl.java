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
    private DatagramSocket socketToMulticast;
    private InetAddress group;
    private byte[] data;
    private boolean needNewSender;

    public UDPServerImpl() throws IOException {
        connections = new LinkedList<>();
        socketToMulticast = new MulticastSocket(3332);
        group = InetAddress.getByName("230.0.0.1");
        needNewSender = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        while (true) {
            try {
                while (connections.size() < 2 || data == null || needNewSender) {
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

    /**
     * {@inheritDoc}
     *
     * @param connection the Connection with the SENDER Client
     * @throws IOException for an error during connection. If the error happens during the UDP transmission
     * the method will return, and the caller class should at that point deal with removing it from the
     * list and getting a new sender.
     */
    @Override
    public void getSenderAudio(Connection connection) throws IOException {
        while (!needNewSender) {
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
                return;
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param data the data to be sent via multicasting
     * @throws IOException for an error during connection.
     */
    @Override
    public void multicastAudio(byte[] data) throws IOException {
        while (!needNewSender) {
            try {
                DatagramPacket packet = new DatagramPacket(data, data.length, group, 4446);
                socketToMulticast.send(packet);
                System.out.println(packet.toString() + "sent via multicasting");
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                //
            } catch (IOException ex) {
                System.out.println("There has been an error while multicasting");
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return a List, maybe empty, of active Connections.
     */
    @Override
    public List<Connection> getList() {
        return connections;
    }

    /**
     * Change the status of the boolean flag which checks if a SENDER client is currently selected.
     *
     * @param flag true if a SENDER client is not assigned, false otherwise
     */
    public void needNewSender(boolean flag) {
        needNewSender = flag;
    }

}
