package net.hoyoung.wfp.core.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author xufeng
 *         2014/11/8
 */
public class EncryptUtil {
	
	public static void main(String[] args) {
		try {
			System.out.println(EncryptUtil.encryptSha1("hello"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

    public static String encryptSha1(String password) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
        messageDigest.update(password.getBytes());
        byte[] b = messageDigest.digest();
        return byteArrayToHex(b);
    }

    private static String byteArrayToHex(byte[] bytes) {
        //字符数组，用来存放十六进制字符
        char[] hexReferChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        //一个字节占8位，一个十六进制字符占4位；十六进制字符数组的长度为字节数组长度的两倍
        char[] hexChars = new char[bytes.length * 2];
        int index = 0;
        for (byte b : bytes) {
            //取字节的高4位
            hexChars[index++] = hexReferChars[b >>> 4 & 0xf];
            //取字节的低4位
            hexChars[index++] = hexReferChars[b & 0xf];
        }
        return new String(hexChars);
    }
}
