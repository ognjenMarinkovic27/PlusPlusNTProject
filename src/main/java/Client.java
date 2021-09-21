import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;

public class Client {



    public static void main (String[] args) {

        final String SERVER_IP = "hermes.plusplus.rs";
        final int PORT = 4000;

        ShutdownHandler shutdownHandler = new ShutdownHandler();
        shutdownHandler.run();

        SavedPacketsHandler savedPacketsHandler = new SavedPacketsHandler(SERVER_IP, PORT, shutdownHandler);
        savedPacketsHandler.run();

        int packets = 0;
        while(true) {
            try {
                Socket s = new Socket(SERVER_IP, PORT);

                DataInputStream dis =  new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                System.out.println("Waiting for data...");
                while(dis.available()==0);

                Instant recievedAt = Instant.now();
                System.out.println("Packet received at " + recievedAt + ".");
                packets++;
                System.out.println("Assigning thread for received packet. Handler ID: " + (packets-1));

                Thread packetThread = new PacketHandler(s,packets-1, dis, dos, recievedAt, shutdownHandler);

                packetThread.start();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
