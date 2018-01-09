package com.fongmi.android.tv.model;

import com.fongmi.android.tv.R;
import com.fongmi.android.tv.utils.Notify;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class Root {

    @SerializedName("root")
    private Root root;
    @SerializedName("CH_LIST")
    private List<Channel> channels;

    private static Root objectFrom(String str) {
        return new Gson().fromJson(str, Root.class);
    }

    private List<Channel> getChannels() {
        return root.channels;
    }

    public static List<Channel> getChannels(String result) {
        try {
            XmlToJson xmlToJson = new XmlToJson.Builder(result).build();
            String jsonString = xmlToJson.toString().replace("\\/", "/");
            return Root.objectFrom(jsonString).getChannels();
        } catch (Exception e) {
            Notify.show(R.string.channel_error);
            return new ArrayList<>();
        }
    }
}

