package com.fongmi.android.library.ltv.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class Item {

    @SerializedName("root")
    private Item root;
    @SerializedName("CH_LIST")
    private List channels;

    private static Item objectFrom(String str) {
        return new Gson().fromJson(str, Item.class);
    }

    private List getChannels() {
        return root.channels;
    }

    public static String getChannels(String result) {
        try {
            XmlToJson xml = new XmlToJson.Builder(result).build();
            String json = xml.toString().replace("\\/", "/");
            return new Gson().toJson(objectFrom(json).getChannels());
        } catch (Exception e) {
            return "";
        }
    }
}
