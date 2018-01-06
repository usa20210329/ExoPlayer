package com.fongmi.android.tv.utils;

import android.util.Base64;

import com.fongmi.android.tv.UserData;

import java.security.spec.AlgorithmParameterSpec;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encrypt {

    public static String aes(String iv, String key, String text) {
        try {
            AlgorithmParameterSpec mAlgorithmParameterSpec = new IvParameterSpec(iv.getBytes());
            SecretKeySpec mSecretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            mCipher.init(1, mSecretKeySpec, mAlgorithmParameterSpec);
            return getToken(Base64.encodeToString(mCipher.doFinal(text.getBytes()), 0));
        } catch (Exception e) {
            return "";
        }
    }

    private static String getToken(String text) {
        int i;
        text = Pattern.compile("\n").matcher(text).replaceAll("");
        String[] tokens = text.split("/");
        text = "";
        for (i = 0; i < tokens.length; i++) {
            if (i == 0) {
                text = text + tokens[i];
            } else {
                text = text + "_" + tokens[i];
            }
        }
        String[] tokens1 = text.split("\\+");
        text = "";
        for (i = 0; i < tokens1.length; i++) {
            if (i == 0) {
                text = text + tokens1[i];
            } else {
                text = text + "-" + tokens1[i];
            }
        }
        return text;
    }

    public static String getRealUrl(String url) {
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
        return domain + "/base/api/hls2/" + str + "/" + m3u8str + UserData.getInstance().getParam();
    }
}
