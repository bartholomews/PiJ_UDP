package main;

import java.io.*;

/**
 * Worker thread for a {@see ThreadPool} which keeps a TCP point-to-point communication with a Client.
 * Its constructor should have a {@see Connection} to communicate with.
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public class WorkerThreadImpl implements WorkerThread, Runnable {
    private Connection connection;

    public WorkerThreadImpl(Connection connection) {
        this.connection = connection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        try {
            String message = "Connection established with " + connection.getSocket().getRemoteSocketAddress();
            message += "\n + Listening for requests...";
            sendString(message);
            sendRequest(getRequest());
        } catch (IOException ex) {
            ex.printStackTrace();
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
                System.out.println(request.toString() + " request from " + connection.getSocket().getRemoteSocketAddress());
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
            System.out.println(toSend);
            return true;
        } catch (IOException ex) {
            System.out.println("Error");
            throw new IOException();
        }
    }

}
