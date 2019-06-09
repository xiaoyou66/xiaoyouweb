package com.example.xiaoyouweb.network;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;


import static android.content.Context.MODE_PRIVATE;


public class Login {
    public static Bundle LoginByPost(String username, String passwd, Context context) throws IOException {
        //获取请求连接
        Connection con = Jsoup.connect("https://xiaoyou66.com/wp-login.php");
        //这里是我发送post的数据
        con.data("log", username);
        con.data("pwd", passwd);
        con.data("rememberme", "forever");
        Connection.Response resp = con.method(Connection.Method.POST).execute();
        //获取cookie数据
        Map cookieValue = resp.cookies();
        //把我们的cookie数据保存起来
        transMapToString(cookieValue,context);
        Connection myprofie = Jsoup.connect("https://xiaoyou66.com/wp-admin/profile.php");

        SharedPreferences date=context.getSharedPreferences("passwd" , MODE_PRIVATE);
        date.edit().clear().apply();
        date.edit().putString("name",username).putString("passwd",passwd).apply();

        //把cookie数据放进去，获取个人信息
        myprofie.cookies(cookieValue);
        //发送get请求
        Document doc = myprofie.get();
        //对获得的内容进行过滤
        Element name = doc.select("#nickname").first();
        Element email = doc.select("#email").first();
        Element sign = doc.select("#description").first();
        Element pic = doc.select("img.avatar-96").first();
        Bundle data = new Bundle();
//        把数据放到bundle里面

        data.putString("name", name.attr("value"));
        data.putString("email", email.attr("value"));
        data.putString("sign", sign.text());
        data.putString("pic", pic.attr("src"));
        data.putString("content", doc.toString());
        return data;

    }
    //map转换成字符串 然后保存到 cookie
    private static void transMapToString(Map map,Context context) {
        java.util.Map.Entry entry;
        StringBuffer sb = new StringBuffer();
        for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
            entry = (java.util.Map.Entry) iterator.next();
            sb.append(entry.getKey().toString()).append("=").append(null == entry.getValue() ? "" :
                    entry.getValue().toString()).append(iterator.hasNext() ? ";" : "");
        }
        SharedPreferences myprofie=context.getSharedPreferences("cookie" , MODE_PRIVATE);
        myprofie.edit().clear().apply();
        myprofie.edit().putString("cookie",sb.toString()).apply();
//        Log.d("-----------------",sb.toString());
    }
    //cookie转换成string 然后在转换成map
    private static Map transStringToMap(Context context){

        Map map = new HashMap();
        java.util.StringTokenizer items;
        String mapString;
        SharedPreferences myprofie=context.getSharedPreferences("cookie" , MODE_PRIVATE);
        mapString=myprofie.getString("cookie"," ");
        if(Objects.equals(mapString, " ")) {
            map.clear();
//            Log.d("^^^^^^^^","没有cookie数据");
        }
        else
        {
            for (StringTokenizer entrys = new StringTokenizer(mapString, ";"); entrys.hasMoreTokens();

                 map.put(items.nextToken(), items.hasMoreTokens() ? ((Object) (items.nextToken())) : null))

                items = new StringTokenizer(entrys.nextToken(), "=");
        }
        return map;
    }

    //使用cookie来更新个人信息
    public static void updatemessage(Context context) {
        Map cookieValue = transStringToMap(context);
        Connection myprofie = Jsoup.connect("https://xiaoyou66.com/wp-admin/profile.php");
        if (!cookieValue.isEmpty()) {
            //把cookie数据放进去，获取个人信息
            myprofie.cookies(cookieValue);
            //发送get请求
            Document doc = null;
            try {
                doc = myprofie.get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //对获得的内容进行过滤
            Element name = doc.select("#nickname").first();
            Element email = doc.select("#email").first();
            Element sign = doc.select("#description").first();
            Element pic = doc.select("img.avatar-96").first();
            Bundle data = new Bundle();
//      把数据放到bundle里面
            data.putString("name", name.attr("value"));
            data.putString("email", email.attr("value"));
            data.putString("sign", sign.text());
            data.putString("pic", pic.attr("src"));
            data.putString("content", doc.toString());
            //把数据打印出来，看一下效果
//            Log.d("$$$$$$$", data.getString("name"));
//            Log.d("$$$$$$$", data.getString("sign"));
            //把date转换成message，然后又转换成data（防止没有获取到数据后出现bug）
            Message a = new Message();
            a.setData(data);
            Bundle data1 = a.getData();//Bundle类似于Python的字典
            if (data1.getString("name") != null) {
                //这里我们把数据存储起来
                //fragment不能直接调用sharedpreference方法
                SharedPreferences myprofietext = context.getSharedPreferences("userInfo", MODE_PRIVATE);
                myprofietext.edit().clear().apply();
                myprofietext.edit().putString("name", data.getString("name"))
                        .putString("email", data.getString("email"))
                        .putString("sign", data.getString("sign"))
                        .putString("pic", data.getString("pic"))
                        .putString("islogin", "1").apply();
            }
        }
    }

}