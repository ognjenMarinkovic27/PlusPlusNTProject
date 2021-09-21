import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class ShutdownHandler extends Thread {

    Map<Integer, PacketData> activePackets = new HashMap<>();

    void addPacket (int handlerId, PacketData packetData) {
        activePackets.put(handlerId, packetData);
    }

    void removePacket (int handlerId) {
        activePackets.remove(handlerId);
    }

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                try {
                    FileWriter savedPacketsWriter = new FileWriter( "./savedPacketData/savedPackets.txt");

                    for (int handlerId : activePackets.keySet()) {
                        String packetDataString = "";

                        PacketData packetData = activePackets.get(handlerId);

                        packetDataString += packetData.receivedAt.plusSeconds(packetData.delay) + " ";

                        for(byte b : packetData.data) {
                            packetDataString += b + " ";
                        }
                        packetDataString += "\n";
                        savedPacketsWriter.write(packetDataString);
                    }

                    savedPacketsWriter.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

}
