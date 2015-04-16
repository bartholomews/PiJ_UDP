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
            String message = "Connection " + connection.getID() + " established with " + connection.getSocket().getRemoteSocketAddress();
            sendString(message);
            getRequest();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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

    /**
     * {@inheritDoc}
     *
     * @return the request received.
     * @throws IOException for an error during communication.
     */
    @Override
    public Request getRequest() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getSocket().getInputStream()));
            String received = in.readLine();
            Request request = Request.valueOf(received);
            System.out.println(request.toString() + " request from " + connection.getSocket().getRemoteSocketAddress());
            return request;
        } catch (IOException ex) {
            System.out.println("Error");
            return null;    // TODO ???
        }
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

}
