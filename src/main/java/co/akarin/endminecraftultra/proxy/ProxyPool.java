package co.akarin.endminecraftultra.proxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;

import co.akarin.endminecraftultra.utils.mainUtils;

public class ProxyPool {
    public static final List<String> proxys=new CopyOnWriteArrayList<>();

    public static void getProxysFromAPIs() {
        mainUtils.log("Proxy", "正在使用API获取代理..");

        getProxysFromAPI("http://www.66ip.cn/mo.php?tqsl=9999");
        getProxysFromAPI("http://www.89ip.cn/tqdl.html?api=1&num=9999");
        mainUtils.log("Proxy", "代理更新完成!数量:"+proxys.size());
    }

    public static void getProxysFromAPI(String url) {
        String ips=mainUtils.sendGet(url);
        Matcher matcher=mainUtils.matches(ips,"\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\:\\d{1,5}");
        synchronized (proxys) {
            while(matcher.find()) {
                String ip=matcher.group();
                if(!proxys.contains(ip)) {
                    proxys.add(ip);
                }
            }
        }
    }

    public static void getProxysFromFile() {
        try {
            File file=new File("http.txt");
            if(!file.exists()) return;
            BufferedReader reader=new BufferedReader(new FileReader(file));
            String tempString=null;
            while ((tempString=reader.readLine())!=null) {
                proxys.add(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mainUtils.log("Proxy", "从本地读取代理完成!数量:"+proxys.size());
    }

    public static void runUpdateProxysTask(final int time) {
        new Thread(()-> {
            while(true) {
                mainUtils.sleep(time*1000);
                synchronized (proxys) {
                    proxys.clear();
                }
                getProxysFromAPIs();
            }
        }).start();
    }
}
