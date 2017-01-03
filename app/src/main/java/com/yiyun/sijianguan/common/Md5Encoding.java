package com.yiyun.sijianguan.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Encoding {
	
	// MD5加密，16位
	public static String md5(String plainText) {
		String reslut="";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}

//			reslut=buf.toString();// 32位的加密
			reslut=buf.toString().substring(8, 24);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reslut;
	}

}
