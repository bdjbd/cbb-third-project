package com.wd.tools;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;

/**
 * 基础加密组件
 * 
 * @author zhouxn
 * @version 1.0
 * @since 1.0
 */
public class Hmac {

	/**
	 * MAC算法可选以下多种算法
	 * 
	 * <pre>
	 * HmacMD5
	 * HmacSHA1
	 * HmacSHA256
	 * HmacSHA384
	 * HmacSHA512
	 * </pre>
	 */
	private static final String KEY_MAC = "HmacMD5";
	private static final String KEY = "wisdeem";

	/**
	 * BASE64解密
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private static byte[] decryptBASE64(String key) throws Exception {
		return (new BASE64Decoder()).decodeBuffer(key);
	}

	/**
	 * HMAC加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encryptHMAC(String pStr) throws Exception {
		SecretKey secretKey = new SecretKeySpec(decryptBASE64(KEY), KEY_MAC);
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		mac.init(secretKey);
		return byte2hex(mac.doFinal(pStr.getBytes()));
	}

	/**
	 * 二行制转字符串
	 * 
	 * @param data
	 * @return
	 */
	private static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}

}