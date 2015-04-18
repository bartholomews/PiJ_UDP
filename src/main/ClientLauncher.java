package main;

import java.io.IOException;

/**
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public class ClientLauncher {

    public static void main(String[] args) throws IOException {
        Client client = new ClientImpl();
        client.connect("localhost", 2046);
    }

}
