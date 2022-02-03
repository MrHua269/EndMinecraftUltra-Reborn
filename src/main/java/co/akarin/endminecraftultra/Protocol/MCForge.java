package co.akarin.endminecraftultra.Protocol;
import co.akarin.endminecraftultra.utils.mainUtils;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundCustomPayloadPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundCustomPayloadPacket;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.*;
import com.github.steveice10.packetlib.packet.Packet;

import java.lang.reflect.Field;
import java.nio.charset.MalformedInputException;
import java.util.Arrays;
import java.util.HashMap;

public class MCForge {
    private MCForgeHandShake handshake;

    public HashMap<String,String> modList;
    public Session session;

    public MCForge(Session session,HashMap<String,String> modList) {
        this.modList=modList;
        this.session=session;
        this.handshake=new MCForgeHandShake(this);
    }

    public void init() {
        this.session.addListener(new SessionListener() {

            @Override
            public void packetReceived(Session session, Packet packet) {
                if (packet instanceof ServerboundCustomPayloadPacket) {
                    ServerboundCustomPayloadPacket packet1 = (ServerboundCustomPayloadPacket)packet;
                    handle(packet1);
                }
            }
            @Override
            public void packetSending(PacketSendingEvent e) {}

            @Override
            public void packetSent(Session session, Packet packet) {

            }

            @Override
            public void packetError(PacketErrorEvent packetErrorEvent) {

            }
            public void connected(ConnectedEvent e){
                modifyHost();
            }
            public void disconnecting(DisconnectingEvent e){}
            public void disconnected(DisconnectedEvent e){}
        });
    }

    public void handle(ServerboundCustomPayloadPacket packet) {
        switch(packet.getChannel()) {
            case "FML|HS":
                this.handshake.handle(packet);
                mainUtils.log("FML|HS"+ Arrays.toString(packet.getData()));
                break;
            case "REGISTER":
                this.session.send(new ClientboundCustomPayloadPacket("REGISTER",packet.getData()));
                mainUtils.log("Forge"+ Arrays.toString(packet.getData()));
                break;
            case "MC|Brand":
                this.session.send(new ClientboundCustomPayloadPacket("MC|Brand","fml,forge".getBytes()));
                mainUtils.log("MC|Brand"+ Arrays.toString(packet.getData()));
                break;
        }
    }

    public void modifyHost() {
        try {
            Class<?> cls=this.session.getClass().getSuperclass();

            Field field=cls.getDeclaredField("host");
            field.setAccessible(true);

            field.set(this.session, this.session.getHost()+"\0FML\0");
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static boolean isVersion1710() {
        try {
            Class<?> cls = Class.forName("org.spacehq.mc.protocol.ProtocolConstants");
            if (cls!=null) {
                Field field=cls.getDeclaredField("PROTOCOL_VERSION");
                int protocol=field.getInt(cls.newInstance());
                return (protocol==5);
            }else{
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
