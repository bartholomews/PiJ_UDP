package main;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Future;

/**
 * TODO should implement Runnable?
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public class ServerHandlerImpl implements ServerHandler {
    private Server server;
    private Socket socket;
 //   private boolean moreData;


    public ServerHandlerImpl(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        System.out.println("From THREAD POOL: Connected to " + socket.getRemoteSocketAddress());
        try {
            while(true) {
                Connection connection = createConnection();
                System.out.println("SIZE: " + server.getUdpServer().getList().size());
                Future<Boolean> task = server.getPool().submit(new WorkerThreadImpl(connection));
                while (!task.isDone()) {
                    Thread.sleep(500); // wait for the client to get the id and client_status
                }
                if (connection.isSender()) {
                    System.out.println("Sending audio request to " + connection.getStatus());
                    server.getUdpServer().getSenderAudio(connection);
                }   // else
                // UDPServer should always be ready to multicast if possible;
                // this thread will go back to submit its task that is getting further requests from the client
            }
        } catch (IOException ex) {
            System.out.println("There has been an error during connection");
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            // do nothing
        }
    }


    /**
     * Create a {@see ConnectionImpl} which wraps a client socket, an unique id-number and a
     * Client_status (SENDER or RECEIVER). Adds it to the server list of connections and return it.
     *
     * @return
     */
    @Override
    public synchronized Connection createConnection() {
        ClientStatus status = server.getUdpServer().getList().isEmpty() ? ClientStatus.SENDER : ClientStatus.RECEIVER;
        Connection connection = new ConnectionImpl(socket, server.generateID(), status);
        server.getUdpServer().getList().add(connection);
        return connection;
    }

}

