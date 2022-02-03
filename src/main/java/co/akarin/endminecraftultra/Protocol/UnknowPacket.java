package co.akarin.endminecraftultra.Protocol;

import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.packet.Packet;

import java.io.IOException;


public class UnknowPacket implements Packet {
    public boolean isPriority() {
        return false;
    }

    public void read(NetInput in) throws IOException {
        in.readBytes(in.available());
    }

    public void write(NetOutput out) throws IOException {}
}
