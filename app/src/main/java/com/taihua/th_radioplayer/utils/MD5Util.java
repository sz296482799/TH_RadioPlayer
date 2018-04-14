package com.taihua.th_radioplayer.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

	public static String MD5(String str) {
		String md5Str = null;
		if (str != null && str.length() != 0) {
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update(str.getBytes());
				byte b[] = md.digest();

				StringBuffer buf = new StringBuffer("");
				for (int offset = 0; offset < b.length; offset++) {
					int i = b[offset];
					if (i < 0)
						i += 256;
					if (i < 16)
						buf.append("0");
					buf.append(Integer.toHexString(i));
				}

				md5Str = buf.toString();

			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		return md5Str;
	}
}