package com.example.ccqzy.androidutils;


import android.util.Base64;

import com.lidroid.xutils.util.LogUtils;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import Decoder.BASE64Encoder;

/**
 * Created by Administrator on 2018/6/21.
 * md5加密
 */

public class EncryptManager {
    public static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";
    private String AES_KEY = "F5222E27-DB01-4691-864E-117C2989E595";

    public String MD5Encrypt(String content) {
        MessageDigest md5 = null;
        BASE64Encoder base64en = new BASE64Encoder();
        //加密后的字符串
        String encrypt = content+AES_KEY;
        String newstr = "";
        try {
            md5 = MessageDigest.getInstance("MD5");
            newstr = base64en.encode(md5.digest(encrypt.getBytes("utf-8")));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        LogUtils.d("加密后的数据是"+newstr.substring(0,16));
        return newstr.substring(0,16);

    }

    public String encode(String key, byte[] data) throws Exception {

        try {

            byte[] ivbyte = {1, 2, 3, 4, 5, 6, 7, 8};

            DESKeySpec dks = new DESKeySpec(key.getBytes());


            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

// key的长度不能够小于8位字节

            Key secretKey = keyFactory.generateSecret(dks);

            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);

            IvParameterSpec iv = new IvParameterSpec(ivbyte);

            AlgorithmParameterSpec paramSpec = iv;

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);


            byte[] bytes = cipher.doFinal(data);


            return Base64.encodeToString(bytes, Base64.NO_WRAP);

        } catch (Exception e) {

            throw new Exception(e);

        }

    }

}
