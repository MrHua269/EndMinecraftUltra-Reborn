package co.akarin.endminecraftultra.Protocol;

import java.io.IOException;

import com.github.steveice10.packetlib.io.NetInput;

public class MCForgeUtils {
    public static int readVarShort(NetInput in) throws IOException {
        int low = in.readUnsignedShort();
        int high = 0;
        if ((low & 0x8000) != 0) {
            low = low & 0x7FFF;
            high = in.readUnsignedByte();
        }
        return ((high & 0xFF) << 15) | low;
    }

    public static UnknowPacket createUnknowPacket() {
        try {
            return UnknowPacket.class.newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
