package main;

import javax.sound.sampled.*;
import java.io.*;
import java.net.*;
import java.security.AccessControlException;
import java.util.UUID;

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

    public ClientImpl(String audioPath) {
        audioFile = new File(audioPath);
        format = new AudioFormat(44100, 16, 1, true, false);    // otherwise use getAudioFormat() at the bottom
    }

    // for testing
    public ClientImpl() {
        audioFile = new File("../Low-Conga-1.wav");
        if(!audioFile.exists()) {
            System.out.println(audioFile + " doesn't exist");
        }
        format = getAudioFormat();
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
            Socket socket = new Socket(hostname, port);
            System.out.println("From " + socket.getRemoteSocketAddress() + ": " + getString(socket));
            getID(socket);
            ClientStatus status = getStatus(socket);
            System.out.println("Status received: " + status.name());
            // THIS SHOULD BE A WHILE LOOP, CLIENTS SHOULD HAVE A QUICK CHECK BEFORE EVERY PACKET
            // TO SEE IF THEIR CLIENT_STATUS HAS CHANGED
            if (status == ClientStatus.SENDER) {
                sendAudio();
            } else {
                getAudioChunks(); // TODO
            }
        } catch (IOException ex) {
            throw new IOException("Cannot establish a connection with the Server");
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

    public void sendAudio() {
        int totalFramesRead = 0;
        try {
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
            try {
                int numBytesRead = 0;
                int numFramesRead = 0;
                // try to read numBytes from the file
                while ((numBytesRead = audioIn.read(audioBytes)) != -1) {
                    // calculate the number of frames actually read
                    numFramesRead = numBytesRead / bytesPerFrame;
                    totalFramesRead += numFramesRead;
                    sendAudioChunks(audioBytes);
                }
            } catch (Exception ex) {
                System.out.println("Error while packing audio");
            }
        } catch (Exception ex) {
            System.out.println("Error while preparing to pack audio");
        }
    }

    public void sendAudioChunks(byte[] audioBytes) {
        int count = 0; // just for testing;
        try (DatagramSocket senderSocket = new DatagramSocket(3333)) {
            // BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream("just-a-test".getBytes())))

            // boolean moreDataChunks = true;
            // while(moreDataChunks){
     //       while (true) {
                System.out.println("Client ready to send next audio chunk...");

           //     byte[] buffer = new byte[2048];
                byte[] buffer = audioBytes;

                // get the request from the server
                DatagramPacket serverPacket = new DatagramPacket(buffer, buffer.length);
                senderSocket.receive(serverPacket);

                // send the audio data to the server
                InetAddress address = serverPacket.getAddress();
                int port = serverPacket.getPort();
                serverPacket = new DatagramPacket(buffer, buffer.length, address, port);
                senderSocket.send(serverPacket);
                System.out.println("Packet " + ++count + " sent.");
                Thread.sleep(500); // avoid stackoverflow, testing
  //          }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            //
        }
    }

    // TODO THIS METHODS SHOULD RUN TOGETHER WITH A LISTENER THREAD IN CASE THE SENDER IS DISCONNECTED?
    public void getAudioChunks() throws IOException {
        System.out.println("Ready to receive via multicast");
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager == null) {
            System.setSecurityManager(new SecurityManager());
        }
        while (true) {
            try {
                MulticastSocket multicastSocket = new MulticastSocket(MULTICAST_PORT);
                InetAddress group = InetAddress.getByName(MULTICAST_INETADDRESS);
                multicastSocket.joinGroup(group);
                System.out.println("joined group");

                DatagramPacket packet;

                //  while(true) {   // while(SOMETHING ELSE?)

                byte[] buffer = new byte[2048];
                packet = new DatagramPacket(buffer, buffer.length);
                multicastSocket.receive(packet);

        //        String received = new String(packet.getData(), 0, packet.getLength());
        //        System.out.println("Received via multicasting: " + received);

            } catch (AccessControlException ex) {
                ex.printStackTrace();
                System.out.println("Please reboot the Client with a security.policy in runtime configuration.");
                System.exit(1);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }




    public void openPacketToAudioStream(DatagramPacket packet) {
        try {
            byte[] audioBytes = packet.getData();
            ByteArrayInputStream bytesIn = new ByteArrayInputStream(audioBytes);
            AudioInputStream audioIn = new AudioInputStream(bytesIn, format, packet.getLength());
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info);

            audioLine.open(format);
            audioLine.start();
            audioLine.write(audioBytes, 0, audioBytes.length);
            audioLine.drain();
            audioLine.close();

        } catch (Exception ex) {
            System.out.println("Some error during audio streaming");
        }
    }

    /**
     * TODO
     * Open the AudioInputStream
     *
     * @param packet
     */
    public void play(DatagramPacket packet) {
   //     File audioFile = new File(audioPath);

        try {

       //     AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            AudioInputStream audioIn = new AudioInputStream(new ByteArrayInputStream(packet.getData()), format, packet.getLength());
      //      AudioFormat format = audioIn.getFormat();
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

    //    } catch (UnsupportedAudioFileException ex) {
    //        System.out.println("Audio file not supported");
    //        ex.printStackTrace();
        } catch(LineUnavailableException ex) {
            System.out.println("Audio line unavailable");
            ex.printStackTrace();
        } catch(IOException ex) {
            System.out.println("There has been an error while playing the audio file");
            ex.printStackTrace();
        }
    }

    private AudioFormat getAudioFormat() {
        float sampleRate = 16000.0F;
        int sampleInbits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleInbits, channels, signed, bigEndian);
    }

}



