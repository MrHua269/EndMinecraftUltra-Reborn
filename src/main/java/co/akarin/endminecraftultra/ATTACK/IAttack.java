package co.akarin.endminecraftultra.ATTACK;

import java.util.HashMap;

public abstract class IAttack {
    public int attack_mode;
    public int attack_time;
    public int attack_maxconnect;
    public int attack_joinsleep;
    public boolean attack_motdbefore;
    public boolean attack_tab;

    public HashMap<String,String> modList;

    public IAttack(int time,int maxconnect,int joinsleep,boolean motdbefore,boolean tab,HashMap<String,String> modList) {
        this.attack_time=time;
        this.attack_maxconnect=maxconnect;
        this.attack_joinsleep=joinsleep;
        this.attack_motdbefore=motdbefore;
        this.attack_tab=tab;
        this.modList=modList;
    }

    public abstract void start(String ip,int port);
    public abstract void stop();
}
