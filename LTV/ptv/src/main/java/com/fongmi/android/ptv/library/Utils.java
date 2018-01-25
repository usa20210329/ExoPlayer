package com.fongmi.android.ptv.library;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;

public class Utils {

    static String getEncryption(String data) {
        byte[] e = Base64.decode(data.getBytes(), 0);
        int size = e.length;
        for (int i = 0; i < size; i++) {
            e[i] = (byte) (e[i] ^ 108);
        }
        String js = null;
        try {
            js = new String(e, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        return js.substring(16, js.length());
    }

    static String getMq(String url) {
        return url.startsWith("http://59.125.210.231") ? "1" : "2";
    }

    static String md5(String text) {
        MessageDigest md = getMd();
        md.update(text.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : md.digest()) {
            int bt = b & 255;
            if (bt < 16) sb.append(0);
            sb.append(Integer.toHexString(bt));
        }
        return sb.toString();
    }

    static String urlEncode(String text) {
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (Exception e) {
            return text;
        }
    }

    private static MessageDigest getMd() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            return null;
        }
    }
}
