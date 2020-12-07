package cn.las.utils;

import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.security.*;

public class MD5Utils {
    private static final Logger logger = Logger.getLogger(MD5Utils.class);

    private static final  String  hexDigits [] = {
            "0", "1", "2", "3", "4", "5", "6", "7",
            "8", "9", "a", "b", "c", "d", "e", "f"
    };

    /**
     * @param pswd : 需要加密的字符串
     * @return : 返回加密的MD5字符串
     * @throws NoSuchAlgorithmException : 没有这样的算法
     */
    public static String MD5Encode(String pswd) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
            String s = bytesToHex(digest.digest(pswd.getBytes("utf-8")));
            return s;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return null;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuffer md5str = new StringBuffer();
        //把数组每一字节换成16进制连成md5字符串
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];
            if (digital < 0) { digital += 256; }
            if (digital < 16) { md5str.append("0"); }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString().toUpperCase();
    }

    //生成十六位加密字符串
    private static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }

}
