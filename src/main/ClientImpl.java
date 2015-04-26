package main;

import javax.sound.sampled.*;
import java.io.*;
import java.net.*;
import java.security.AccessControlException;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * // TODO so far when a sender is disconnected, the receivers are blocked waiting to get the multicast,
 * // TODO the new sender is the first new client to connect. Do tests and cleanup before fixing this.
 *  // TODO try with resources bufferedreader and printwriter as fields bound at construction time?
 *
 * @author federico.bartolomei (BBK-PiJ-2014-21)
 */
public class ClientImpl implements Client {
    private final int MULTICAST_PORT = 4446;
    private final String MULTICAST_INETADDRESS = "230.0.0.1";
    private File audioFile;
    private final AudioFormat format;
    private Socket socket;
    private ClientStatus status;
    private ScheduledExecutorService listener;
    private DatagramSocket senderSocket;

    public ClientImpl(String audioPath) {
        listener = Executors.newSingleThreadScheduledExecutor();
        audioFile = new File(audioPath);
        format = new AudioFormat(44100, 16, 1, true, false);
    }

    // for testing
    public ClientImpl() {
        listener = Executors.newSingleThreadScheduledExecutor();
        audioFile = new File("../Low-Conga-1.wav");
        if (!audioFile.exists()) {
            System.out.println(audioFile + " doesn't exist");
        }
        format = new AudioFormat(44100, 16, 1, true, false);
    }

    /**
     * {@inheritDoc}
     *
     * @param hostname the hostname (or IP address) of the Server to connect to.
     * @param port     the port number to connect to.
     * @return the Client socket opened with the Server.
     * @throws java.io.IOException for a communication error with the Server.
     */
    @Override
    public void connect(String hostname, int port) throws IOException {
        try {
            socket = new Socket(hostname, port);
            init();
        } catch (IOException ex) {
//            senderSocket.close();
//            socket.close();
            System.out.println("Cannot establish a connection with the Server");
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public void init() throws IOException {
        try {
            Future<ClientStatus> task = listener.submit(new TCPClient());
            listener.schedule(new TCPClient(), 3, TimeUnit.SECONDS);
            while (!task.isDone()) {
                Thread.sleep(500);
            }
            System.out.println("Status received: " + status.name());
            if (status == ClientStatus.SENDER) {
                sendAudio();
            } else {
                getAudio();
            }
        } catch (InterruptedException ex) {
            //
        } catch (IOException ex) {
            throw new IOException("Error while connecting via UDP");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param socket the socket connected with the Server
     * @return the String read from the InputStream
     * @throws IOException for an error during connection or if a null String is received
     */
    public String getString(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String received = in.readLine();
        if (received == null) {
            throw new IOException("Connection opened, but Server failed to respond");
        } else {
            return received;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param socket  the socket connected with the Server
     * @param request the Client request to be sent to the Server
     * @throws IOException          for an error during connection
     * @throws NullPointerException for a null Request
     */
    @Override
    public void sendRequest(Socket socket, Request request) throws IOException {
        if (request == null) {
            throw new NullPointerException("Cannot have a null request");
        }
        try {
            PrintWriter out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));
            String toSend = request.name();
            out.println(toSend);
            out.flush();
        } catch (IOException ex) {
            throw new IOException("Error while sending a Request to the Server");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param socket the socket connected with the Server
     * @return the current status flag of the client (either RECEIVER or SENDER)
     * @throws IOException              for a communication error
     * @throws IllegalArgumentException if an invalid ClientStatus is received
     */
    public ClientStatus getStatus(Socket socket) throws IOException {
        sendRequest(socket, Request.CLIENT_STATUS);
        return ClientStatus.valueOf(getString(socket));
    }

    /**
     * {@inheritDoc}
     *
     * @param socket the socket connected with the Server
     * @return a String with the unique ID requested
     * @throws IOException              for a communication error
     * @throws IllegalArgumentException if an invalid UUID is received
     */
    @Override
    public String getID(Socket socket) throws IOException {
        sendRequest(socket, Request.ID);
        String id = getString(socket);
        UUID.fromString(id);
        System.out.println("ID received: " + id + " from " + socket.getRemoteSocketAddress());
        return id;
    }

    /**
     *
     */
    @Override
    public void sendAudio() {
        try {
            senderSocket = new DatagramSocket(3333);
            while (true) {
                int count = 0;
                int totalFramesRead = 0;
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioFile);
                int bytesPerFrame = audioIn.getFormat().getFrameSize();
                if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
                    // some audio formats may have unspecified frame size
                    // in that case we may read any amount of bytes
                    bytesPerFrame = 1;
                }
                // set buffer size
                int bufferSize = 2048 * bytesPerFrame;
                byte[] audioBytes = new byte[bufferSize];

                int numBytesRead = 0;
                int numFramesRead = 0;
                // try to read numBytes from the file
                while ((numBytesRead = audioIn.read(audioBytes)) != -1) {
                    // calculate the number of frames actually read
                    numFramesRead = numBytesRead / bytesPerFrame;
                    totalFramesRead += numFramesRead;

                    System.out.println("Client ready to send next audio chunk...");

                    // get the request from the server
                    DatagramPacket serverPacket = new DatagramPacket(audioBytes, audioBytes.length);
                    senderSocket.receive(serverPacket);

                    // send the audio data to the server
                    InetAddress address = serverPacket.getAddress();
                    int port = serverPacket.getPort();
                    serverPacket = new DatagramPacket(audioBytes, audioBytes.length, address, port);
                    senderSocket.send(serverPacket);
                    Thread.sleep(500);  // to make it more "readable"

                    System.out.println("Packet " + ++count + " sent.");
                }
            }
        } catch(UnsupportedAudioFileException ex) {
            System.out.println("Unsupported audio");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Error while packing audio");
            ex.printStackTrace();
        } catch(InterruptedException ex) {
            //
        }
    }

    /**
     *
     * @throws IOException
     */
    @Override
    public void getAudio() throws IOException {
        while (status == ClientStatus.RECEIVER) {
            System.out.println("Ready to receive via multicast");
            SecurityManager securityManager = System.getSecurityManager();
            if (securityManager == null) {
                System.setSecurityManager(new SecurityManager());
            }
            try {
                MulticastSocket multicastSocket = new MulticastSocket(MULTICAST_PORT);
                InetAddress group = InetAddress.getByName(MULTICAST_INETADDRESS);
                multicastSocket.joinGroup(group);
                System.out.println("joined group");

                while (!status.equals(ClientStatus.SENDER)) {
                    DatagramPacket packet;

                    byte[] buffer = new byte[2048];
                    packet = new DatagramPacket(buffer, buffer.length);
                    multicastSocket.receive(packet);

                    play(packet);
                }
            } catch (AccessControlException ex) {
                ex.printStackTrace();
                System.out.println("Please reboot the Client with a security.policy in runtime configuration.");
                System.exit(1);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        sendAudio();
    }

    /**
     * Open an AudioInputStream on the Byte[] data within a DatagramPacket and start a SourceDataLine on it.
     *
     * @param packet the DatagramPacket to play
     */
    private void play(DatagramPacket packet) {
        try {
            AudioInputStream audioIn = new AudioInputStream(new ByteArrayInputStream(packet.getData()), format, packet.getLength());
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info);
            audioLine.open(format);
            audioLine.start();
            System.out.println("Playback started");
            byte[] buffer = new byte[1024];
            int bytesRead = -1;
            while((bytesRead = audioIn.read(buffer)) != -1) {
                audioLine.write(buffer, 0, bytesRead);
            }
            audioLine.drain();
            audioLine.close();
            audioIn.close();
            System.out.println("Playback completed.");
        } catch(LineUnavailableException ex) {
            System.out.println("Audio line unavailable");
            ex.printStackTrace();
        } catch(IOException ex) {
            System.out.println("There has been an error while playing the audio file");
            ex.printStackTrace();
        }
    }

    /**
     * Inner Callable class which should be scheduled to run periodically maintaining
     * a TCP communication with the server. Most importantly it should update the
     * Client_Status in order to make the Client change its routine if necessary.
     */
    public class TCPClient implements Callable<ClientStatus> {

        public ClientStatus call() throws IOException {
            try {
                socket.setSoTimeout(5000);
                System.out.println("From server: " + getString(socket));
                getID(socket);
                status = getStatus(socket);
                return status;
            } catch (IOException ex) {
                throw new IOException("Error while communicating via TCP");
            }
        }

    }

}



