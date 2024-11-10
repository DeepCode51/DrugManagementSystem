package com.zcurd.common.util;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * 密码工具类
 * @author 钟世云 2017年1月20日 上午11:40:52
 */
public class PasswordUtil {
	
	/**
	 * 默认密码
	 */
	public final static String defaultPassword = "123456";

	private String md5(String inputStr) {
		//System.out.println("=======加密前的数据:" + inputStr);
		BigInteger bigInteger = null;

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] inputData = inputStr.getBytes();
			md.update(inputData);
			bigInteger = new BigInteger(md.digest());
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println("MD5加密后:" + bigInteger.toString(16));
		return bigInteger.toString(16);
	}

	/**
	 * 密码加密
	 */
	public static String encodePassword(String password) {
		return new PasswordUtil().md5(password);
	}
	
	public static void main(String[] args) {
		System.out.println(encodePassword(defaultPassword));
	}

}
