package main;

import java.io.*;

/**
 * Worker thread for a {@see ThreadPool} which keeps a TCP point-to-point communication with a Client.
 * Its constructor should have a {@see Connection} to communicate with.
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public class WorkerThreadImpl implements WorkerThread {
    private Connection connection;

    public WorkerThreadImpl(Connection connection) {
        this.connection = connection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean call() {
        try {
            String message = "Connection established with " + connection.getSocket().getRemoteSocketAddress();
            sendString(message);
            sendRequest(getRequest());  // send the ID
            try {
                Thread.sleep(1000);    // to disconnect (testing)
            } catch (InterruptedException ex) {
                // do nothing
            }
            sendRequest(getRequest());  // send client_status
            return true;
        } catch (IOException ex) {
            System.out.println(connection.getID() + " disconnected!!");
            return false;
        }
    }


    /**
     * {@inheritDoc}
     *
     * @param request the request from the Client
     * @throws IOException for an error during communication
     */
    @Override
    public void sendRequest(Request request) throws IOException {
        String toSend = "";
        if(request == Request.ID) {
            toSend = connection.getID();
        } else if(request == Request.CLIENT_STATUS) {
            toSend = connection.getStatus();
        } else {
            throw new IOException("Invalid request: " + request.toString());
        }
        System.out.println(" (" + toSend + ")");
        sendString(toSend);
    }

    /**
     * {@inheritDoc}
     *
     * @return the request received
     * @throws IOException for a communication error
     * @throws IllegalArgumentException if the String is not a value of a valid {@see Request}
     */
    @Override
    public Request getRequest() throws IOException {
        Request request = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getSocket().getInputStream()));
        String line = in.readLine();
        boolean received = false;
        do {
            if (line == null) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    // do nothing
                }
            } else {
                received = true;
                request = Request.valueOf(line);
                System.out.print(request.toString() + " request from " + connection.getSocket().getRemoteSocketAddress());
            }
        } while (!received);
        return request;
    }

    /**
     * {@inheritDoc}
     *
     * @return the connection assigned to the thread.
     */
    @Override
    public Connection getConnection() {
        return connection;
    }

    /**
     * {@inheritDoc}
     *
     * @param toSend the String message to be sent via the socket.
     * @return true after the stream is flushed.
     * @throws IOException for a communication error.
     */
    @Override
    public boolean sendString(String toSend) throws IOException {
        try {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(connection.getSocket().getOutputStream()));
            out.println(toSend);
            out.flush();
            return true;
        } catch (IOException ex) {
            System.out.println("Error");
            throw new IOException();
        }
    }

}
