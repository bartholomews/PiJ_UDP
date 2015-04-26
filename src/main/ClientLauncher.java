package main;

import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public class ClientLauncher {

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        String path = "";
        if(args.length != 1) {
            System.out.println("USAGE:");
            System.out.println("java -Djava.security.policy=../client.policy main.ClientLauncher <String audiofile path>)");
        } else {
            path = args[0];
        }
        Client client = new ClientImpl(path);
        client.connect("localhost", 2046);
    }

}
