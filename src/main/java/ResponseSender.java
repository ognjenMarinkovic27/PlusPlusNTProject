import java.io.DataOutputStream;
import java.net.Socket;

public class ResponseSender {
    final byte[] response;
    final Socket s;
    final DataOutputStream dos;
    final int delay;
    final int handlerId;
    final ShutdownHandler shutdownHandler;

    public ResponseSender(byte[] response, Socket s, DataOutputStream dos, int delay, int handlerId, ShutdownHandler shutdownHandler) {
        this.response = response;
        this.s = s;
        this.dos = dos;
        this.delay = delay;
        this.handlerId = handlerId;
        this.shutdownHandler = shutdownHandler;
    }

    public void sendMessage() {
        try {
            Thread.sleep(delay * 1000);

            dos.write(response);
            dos.close();
            s.close();

            System.out.print(handlerId + ": Sent ");
            for(byte b : response) {
                System.out.print(b + " ");
            }
            System.out.print("to server.\n");

            shutdownHandler.removePacket(handlerId);


        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
