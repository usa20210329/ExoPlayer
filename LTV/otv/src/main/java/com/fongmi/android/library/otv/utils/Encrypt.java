package com.fongmi.android.library.otv.utils;

import android.util.Base64;

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
}