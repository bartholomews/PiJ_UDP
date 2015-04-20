package main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public class ThreadsPoolImpl implements ThreadsPool {
    private ExecutorService threadsPool;
    private IdGenerator IdGenerator;
    private List<Connection> connections;
    private byte[] data;    // TO SORT OUT

    public ThreadsPoolImpl(int n) {
        threadsPool = Executors.newFixedThreadPool(n);
        IdGenerator = new IdGeneratorImpl();
        connections = new LinkedList<>();
    }

    /**
     * Acknowledge connection and pass the socket details to a separate thread (so the
     * Server can keep receiving new requests) of class ConnectionToClient which is nested
     * so it can easily have access to the fields of ThreadsPoolImpl, and whose run() method
     * calls a WorkerThreadImpl which is a thread member of threadsPool.
     *
     * @param socket the client socket to open a communication with.
     */
    @Override
    public void handleRequest(Socket socket) {
        System.out.println("From THREAD POOL: Connected to " + socket.getRemoteSocketAddress());
        new Thread(new ConnectionToClient(socket)).start();
    }

    /**
     *
     *
     * @return
     */
    @Override
    public List<Connection> getList() {
        return connections;
    }

    /**
     * Nested Runnable class to have easy access to the main class fields.
     * It gets a client socket and wraps it to create a Connection (socket, id and client_status).
     * Then it submits a WorkerThreadImpl (with the Connection) to the pool and wait for it to return.
     */
     class ConnectionToClient implements Runnable {
        private Socket socket;

        public ConnectionToClient(Socket socket) {
            this.socket = socket;
        }

        /**
         *
         */
        @Override
        public void run() {
            try {
                Connection connection = createConnection();
                Future<Boolean> task = threadsPool.submit(new WorkerThreadImpl(connection));
                while (!task.isDone()) {
                    try {
                        Thread.sleep(1000); // wait for the client to get the id and client_status
                    } catch (InterruptedException ex) {
                        // do nothing
                    }
                }
                //      listenForRequests();    // start a listener for possible further requests?
                if (connection.isSender()) {
                    getSenderAudio(connection);
                } else {
                    System.out.println("Server will soon stream audio to receiver client " + connection.getID());
                    // broadcastAudio(); TODO
                }
            } catch (IOException ex) {
                System.out.println("There has been an error during connection");
            }
        }

        /**
         *
         * @return
         */
        public synchronized Connection createConnection() {
            ClientStatus status = connections.isEmpty() ? ClientStatus.SENDER : ClientStatus.RECEIVER;
            Connection connection = new ConnectionImpl(socket, IdGenerator.generateID(), status);
            connections.add(connection);
            return connection;
        }

        /**
         *
         *
         * @param connection
         * @throws IOException
         */
        public void getSenderAudio(Connection connection) throws IOException {
            System.out.println("Server requesting audio data from sender client " + connection.getID());
            // get a datagram socket (try-with-resources)
            try (DatagramSocket senderSocket = new DatagramSocket()) {
                InetAddress address = connection.getSocket().getInetAddress();
                // send request
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 3333);
                senderSocket.send(packet);
                senderSocket.setSoTimeout(5000);    // 5 sec timeout
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
                if (connection.isSender() && connections.size() > 1) {
                    Connection newSender = connections.get(1);
                    newSender.setStatus(ClientStatus.SENDER);
                } // else ?
                connections.remove(connection);
                connection.getSocket().close();
                // TODO let the new sender know and open a new UDP
            }
        }

    }

}