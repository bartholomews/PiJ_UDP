package main;

import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public class ThreadsPoolImpl implements ThreadsPool {
    private ExecutorService pool;
    private IdGenerator IdGenerator;
    private List<Connection> connections;

    public ThreadsPoolImpl(int n) {
        pool = Executors.newFixedThreadPool(n);
        IdGenerator = new IdGeneratorImpl();
        connections = new LinkedList<>();
    }

    /**
     * {@inheritDoc}
     *
     * @param socket
     */
    @Override
    public void submit(Socket socket) {
        System.out.println("Connected to " + socket.getRemoteSocketAddress());
        ClientStatus status = connections.isEmpty()? ClientStatus.SENDER : ClientStatus.RECEIVER;
        Connection connection = new ConnectionImpl(socket, IdGenerator.generateID(), status);
        connections.add(connection);
        pool.submit(new WorkerThreadImpl(connection));
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public List<Connection> getList() {
        return connections;
    }


}
