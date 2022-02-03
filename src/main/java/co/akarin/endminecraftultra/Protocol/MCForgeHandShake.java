package co.akarin.endminecraftultra.Protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundCustomPayloadPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundCustomPayloadPacket;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.io.stream.StreamNetOutput;

public class MCForgeHandShake {
    private MCForge forge;

    public MCForgeHandShake(MCForge forge) {
        this.forge=forge;
    }

    public void handle(ServerboundCustomPayloadPacket packet) {
        Session session=forge.session;

        byte[] data=packet.getData();
        int packetID=data[0];

        switch(packetID) {
            case 0: //Hello
                sendPluginMessage(session,"FML|HS",new byte[]{0x01, 0x02});

                //ModList
                ByteArrayOutputStream buf=new ByteArrayOutputStream();
                StreamNetOutput out=new StreamNetOutput(buf);
                try {
                    out.writeVarInt(2);
                    out.writeByte(forge.modList.size());
                    forge.modList.forEach((k, v) -> {
                        try {
                            out.writeString(k);
                            out.writeString(v);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendPluginMessage(session,"FML|HS",buf.toByteArray());
                break;
            case 2: //ModList
                sendPluginMessage(session,"FML|HS",new byte[]{-0x1, 0x02}); //ACK(WAITING SERVER DATA)
                break;
            case 3: //RegistryData
                sendPluginMessage(session,"FML|HS",new byte[]{-0x1, 0x03}); //ACK(WAITING SERVER COMPLETE)
                break;
            case -1: //HandshakeAck
                int ackID=data[1];
                switch(ackID) {
                    case 2: //WAITING CACK
                        sendPluginMessage(session,"FML|HS",new byte[]{-0x1, 0x04}); //PENDING COMPLETE
                        break;
                    case 3: //COMPLETE
                        sendPluginMessage(session,"FML|HS",new byte[]{-0x1, 0x05}); //COMPLETE
                        break;
                    default:
                }
            default:
        }
    }

    private void sendPluginMessage(Session session,String channel,byte[] data) {
        session.send(new ClientboundCustomPayloadPacket(channel,data));
    }
}
