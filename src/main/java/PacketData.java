import java.time.Instant;

public class PacketData {
    public byte[] data;
    public int delay;
    public Instant receivedAt;

    public PacketData(byte[] data, int delay, Instant receivedAt) {
        this.data = data;
        this.delay = delay;
        this.receivedAt = receivedAt;
    }
}
