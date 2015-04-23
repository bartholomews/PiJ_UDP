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
    private UDPServerImpl udpServer; // TODO
    private Socket socket;
 //   private boolean moreData;


    public ServerHandlerImpl(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        udpServer = server.getUdpServer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        System.out.println("From THREAD POOL: Connected to " + socket.getRemoteSocketAddress());
        try {
            Connection connection = createConnection();
            Future<Boolean> task = server.getPool().submit(new WorkerThreadImpl(connection));
            while (!task.isDone()) {
                Thread.sleep(500); // wait for the client to get the id and client_status
                //      listenForRequests();    // start a listener for possible further requests?
                // or keep workerthreadimpl alive?
            }
            if (connection.isSender()) {
                System.out.println("Sending audio request to " + connection.getStatus());
          //      udpServer.getSenderAudio(connection); TODO
            } else {
                // broadcastAudio(); // UDPServer should always multicast if possible
            }
  /*      } catch (IOException ex) {
            System.out.println("There has been an error during connection");
            ex.printStackTrace();
  */      } catch (InterruptedException ex) {
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
        ClientStatus status = server.getList().isEmpty() ? ClientStatus.SENDER : ClientStatus.RECEIVER;
        Connection connection = new ConnectionImpl(socket, server.generateID(), status);
        server.getList().add(connection);
        return connection;
    }

}

