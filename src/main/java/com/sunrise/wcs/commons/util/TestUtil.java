package com.sunrise.wcs.commons.util;

public class TestUtil {
    public static void main(String[] args) {
        String clientPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC4rFhsjsX3pW23Zw4VPJQzHulv8cqL7CWJEle+kKBaTIZUGywTjnQPLPLZzZPGWUd2KhysoL1/sYp/wEwQ+1YgcCs3vG0Bb2fqWV0+ijBLBOlI0OxSeN0ZwJ8hIcan9yuJo5a6rw7yZaWypVY6LtIJzB9CKICgJhspW5ugtlbxHQIDAQAB";
        String result = "{\"sourceSystem\": \"100002\",   \"channelAccount\": \"51b3z13\" }";
        //获取随机的aes秘钥
        String aesKey = AESUtils.getRandomString(16);
        System.out.println("随机的key:"+aesKey);
        try {
            // rsa加密
            String key = RSAUtils.encryptByPublicKey(aesKey, clientPublicKey);
            // aes加密
            String data = AESUtils.encrypt(result, aesKey);
            System.out.println("key:"+key);
            System.out.println("data:"+data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
