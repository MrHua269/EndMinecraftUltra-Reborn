package co.akarin.endminecraftultra.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class mainUtils {
    public static void log(String from,Object msg) {
        System.out.println("["+from+"] "+msg);
    }
    public static void log(Object msg) {
        System.out.println(msg);
    }
    public static void log(Object... msg) {
        for (Object o : msg) {
            System.out.println(o);
        }
    }
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Matcher matches(String str,String regex) {
        Pattern mPattern=Pattern.compile(regex);
        Matcher mMatcher=mPattern.matcher(str);
        return mMatcher;
    }

    public static String sendGet(String url) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url;
            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();

            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            log("HTTP","HTTP请求失败: "+e.getMessage());
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                log("HTTP","IO异常: "+e.getMessage());
            }
        }
        return result;
    }

    public static String getRandomString(int minLength,int maxLength) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        int length=random.nextInt(maxLength) % (maxLength-minLength+1)+minLength;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public static <T> T getCo(String date, T def) {
        if (date.equals("")) {
            return def;
        }
        return (T) date;
    }

    public static int getCo(String date, int def) {
        if (date.equals("")) {
            return def;
        }
        return Integer.parseInt(date);
    }
}
