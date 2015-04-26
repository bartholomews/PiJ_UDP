package main;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of interface {@see ServerHandler}
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public class ServerHandlerImpl implements ServerHandler {
    private Server server;
    private Socket socket;

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
            Connection connection = createConnection();
            while (!connection.getSocket().isClosed()) {
                System.out.println("SIZE: " + server.getUdpServer().getList().size());
                Future<Boolean> task = server.getPool().submit(new WorkerThreadImpl(connection));
                while (!task.isDone()) {
                    Thread.sleep(500); // wait for the client to get the id and client_status
                }
                if (!task.get()) {  // connection is disconnected
                    closeConnection(connection);
                    return;
                } else {
                    if (connection.isSender()) {
                        server.getUdpServer().needNewSender(false);   // allow the server to get audio chunks from SENDER
                        System.out.println("Sending audio request to " + connection.getStatus());
                        server.getUdpServer().getSenderAudio(connection);
                    } else {
                        if(!server.getPool().schedule(new WorkerThreadImpl(connection), 3, TimeUnit.SECONDS).get()) {
                            closeConnection(connection);
                        }
                    }
                    // UDPServer should always be ready to multicast if possible;
                    // this thread will go back to submit its task that is getting further requests from the client
                }
            }
        } catch(ExecutionException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("There has been an error during connection");
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            // do nothing
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return the newly create Connection
     */
    @Override
    public synchronized Connection createConnection() {
        ClientStatus status = server.getUdpServer().getList().isEmpty() ? ClientStatus.SENDER : ClientStatus.RECEIVER;
        Connection connection = new ConnectionImpl(socket, server.generateID(), status);
        server.getUdpServer().getList().add(connection);
        return connection;
    }

    /**
     * {@inheritDoc}
     *
     * @param connection the connection to be closed
     * @throws IOException for an error during network communication
     */
    @Override
    public synchronized void closeConnection(Connection connection) throws IOException {
        System.out.println(connection.getID() + " (" + connection.getStatus() + ") disconnected");
        server.getUdpServer().getList().remove(connection);
        System.out.println("Size: " + server.getUdpServer().getList().size());
        if(connection.getStatus().equals(ClientStatus.SENDER.name())) {
            if (!server.getUdpServer().getList().isEmpty()) {
                System.out.println("Getting a new sender..");
                Connection newSender = server.getUdpServer().getList().get(0);
                newSender.setStatus(ClientStatus.SENDER);
            } else {
                System.out.println("No other Client is connected so far. Listening on port 2046...");
                server.getUdpServer().needNewSender(true);
            }
        }
    }

}

