import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;

public class SavedPacketsHandler extends Thread {
    final String SERVER_IP;
    final int PORT;
    final ShutdownHandler shutdownHandler;


    public SavedPacketsHandler(String SERVER_IP, int PORT, ShutdownHandler shutdownHandler) {
        this.SERVER_IP = SERVER_IP;
        this.PORT = PORT;
        this.shutdownHandler = shutdownHandler;
    }

    @Override
    public void run() {
        int packets = 0;

        System.out.println("Checking for saved packets...");

        try {
            File savedPacketsFile = new File("./savedPacketData/savedPackets.txt");
            savedPacketsFile.createNewFile();
            FileReader fileReader = new FileReader("./savedPacketData/savedPackets.txt");

            String line = "";
            int data;

            while ((data=fileReader.read()) != -1) {
                if(data == '\n') {
                    String[] packetDataStringArray = line.split(" ");

                    for( String s : packetDataStringArray) {
                        //System.out.println(s);
                    }

                    Instant expirationTime = Instant.parse(packetDataStringArray[0]);

                    boolean cancel = false;
                    int responseSize;
                    int delay;

                    if(expirationTime.isBefore(Instant.now())) {
                        cancel = true;
                        responseSize = 12;
                        delay = 0;
                        System.out.println("PACKET EXPIRED: " + line);
                    }
                    else {
                        responseSize = 16;
                        delay = (int) Duration.between(Instant.now(), expirationTime).getSeconds();
                        System.out.println("FOUND VALID PACKET: " + line);
                    }

                    byte[] response = new byte[responseSize];

                    for(int i=1;i<responseSize;i++) {
                        response[i-1] = Byte.parseByte(packetDataStringArray[i]);
                    }

                    if(cancel) {
                        //Veoma lep kod
                        response[0] = 2;
                        response[1] = response[2] = response[3] = 0;
                    }

                    Socket s = new Socket(SERVER_IP, PORT);

                    DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                    int handlerId = -1-packets;
                    shutdownHandler.addPacket(handlerId, new PacketData(response, delay, expirationTime));
                    ResponseSender responseSender = new ResponseSender(response, s, dos, delay, handlerId, shutdownHandler);
                    responseSender.sendMessage();

                    packets++;
                    line = "";
                }
                else {
                    line+=(char)data;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
