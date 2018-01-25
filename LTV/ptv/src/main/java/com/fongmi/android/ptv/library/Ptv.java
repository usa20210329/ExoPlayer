package com.fongmi.android.ptv.library;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class Ptv {

    private static final String ID = "DeviceId";
    private static final String JS = "http://120.24.179.81/tv/online.asp?v=3&u=" + ID;
    private static final String USER = "UserId";
    private DefaultHttpClient client;
    private String url, name;

    public static String getToken(String url, String name) {
        Ptv ptv = new Ptv();
        ptv.setUrl(url);
        ptv.setName(name);
        ptv.startHeart();
        return ptv.getToken();
    }

    public static String getJs() {
        try {
            HttpResponse response = new DefaultHttpClient().execute(new HttpGet(JS));
            String js = EntityUtils.toString(response.getEntity(), "UTF-8");
            return Item.objectFrom(js).getData();
        } catch (Exception e) {
            return "";
        }
    }

    private Ptv() {
        this.client = new DefaultHttpClient();
    }

    private void setUrl(String url) {
        this.url = url;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void startHeart() {
        try {
            HttpPost httpPost = new HttpPost("http://120.24.179.81:20189/tv/h.asp");
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            String param = "u=" + ID + "&s=127845196" + "&t=" + System.currentTimeMillis();
            httpPost.setEntity(new StringEntity(param));
            client.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getToken() {
        String mq = Utils.getMq(url);
        String tm = String.valueOf(System.currentTimeMillis());
        String boxTag = tm + "/" + Utils.md5(ID + tm + "a.b");
        String asp = "http://120.24.179.81:20189/tv/a.asp?c=" + Utils.md5(ID + "a*b.c." + tm) + "&t=" + tm;
        String c = Utils.md5(USER + "a*b.c." + mq + "." + tm);
        String data = "s=" + mq + "&u=" + Utils.urlEncode(name);
        return getData(asp, boxTag, c, data);
    }

    private String getData(String url, String boxTag, String c, String data) {
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.setHeader("b1", boxTag.split("/")[0]);
            httpPost.setHeader("b2", boxTag.split("/")[1]);
            httpPost.setHeader("c", c);
            httpPost.setEntity(new StringEntity(data));
            return EntityUtils.toString(client.execute(httpPost).getEntity(), "UTF-8");
        } catch (IOException e) {
            return null;
        }
    }
}
