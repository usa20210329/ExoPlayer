package com.fongmi.android.library.otv.utils;

import com.fongmi.android.library.otv.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static String getResult(String url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setReadTimeout(30000);
            conn.setConnectTimeout(30000);
            StringBuilder sb = new StringBuilder();
            int count;
            char[] buf = new char[1024];
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            while ((count = isr.read(buf)) != -1) {
                sb.append(new String(buf, 0, count));
            }
            is.close();
            isr.close();
            return sb.toString();
        } catch (IOException e) {
            return "";
        }
    }

    public static String getRealUrl(String url, User user) {
        int firstNum = url.lastIndexOf("/");
        int secNum = url.lastIndexOf("/", firstNum - 1);
        char[] chars1 = new char[((firstNum - secNum) - 1)];
        url.getChars(secNum + 1, firstNum, chars1, 0);
        String str = "";
        for (char c : chars1) {
            str = str + c;
        }
        char[] chars2 = new char[secNum];
        url.getChars(0, secNum, chars2, 0);
        String domain = "";
        for (char c2 : chars2) {
            domain = domain + c2;
        }
        char[] chars3 = new char[((url.length() - firstNum) - 1)];
        url.getChars(firstNum + 1, url.length(), chars3, 0);
        String m3u8str = "";
        for (char c22 : chars3) {
            m3u8str = m3u8str + c22;
        }
        m3u8str = m3u8str.replace("playlist", "index3");
        return domain + "/base/api/hls2/" + str + "/" + m3u8str + "?ts=" + user.getTs() + "&hx=" + user.getHx();
    }

    public static void getAccount(final User user) {
        FirebaseDatabase.getInstance().getReference().child("account").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                List<String> items = new ArrayList<>();
                for (DataSnapshot item : data.getChildren()) items.add(item.getValue(String.class));
                user.setItems(items);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
