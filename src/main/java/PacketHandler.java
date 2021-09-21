import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;

public class PacketHandler extends Thread {
    final Socket s;
    final DataInputStream dis;
    final DataOutputStream dos;
    int handlerId;
    final Instant receivedAt;
    final ShutdownHandler shutdownHandler;

    public PacketHandler(Socket s, int handlerId, DataInputStream dis, DataOutputStream dos, Instant receivedAt, ShutdownHandler shutdownHandler)
    {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        this.handlerId = handlerId;
        this.receivedAt = receivedAt;
        this.shutdownHandler = shutdownHandler;

    }

    int getIntFromByteArray (byte byteArray[], int offset) {
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray, offset, 4);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            return byteBuffer.getInt();
        }
        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    byte[] getByteArrayFromInt(int value) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4).putInt(value);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return byteBuffer.array();
    }



    byte[] generateResponse(byte[] header, byte[] message) {
        byte[] response = new byte[16];
        System.arraycopy(header, 0, response, 0, 4);
        System.arraycopy(message,0, response, 4, 12);

        return response;
    }

    @Override
    public void run() {
        try {
            byte[] headerBuffer = new byte[4];
            dis.readFully(headerBuffer, 0, 4);
            int headerValue = getIntFromByteArray(headerBuffer, 0);

            if(headerValue == 1) {
                byte[] messageBuffer = new byte[12];
                dis.readFully(messageBuffer);
                int delay = getIntFromByteArray(messageBuffer, 8);

                byte[] response = generateResponse(headerBuffer, messageBuffer);
                shutdownHandler.addPacket(handlerId, new PacketData(response, delay, receivedAt));
                ResponseSender responseSender = new ResponseSender(response, s, dos, delay, handlerId, shutdownHandler);
                responseSender.sendMessage();
            }
            else {

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
