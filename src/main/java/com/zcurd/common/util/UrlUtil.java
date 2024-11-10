package com.zcurd.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class UrlUtil {

	/**
	 * 获得url的基础地址（到action部分，用于权限管理）
	 */
	public static String formatBaseUrl(String url) {
		url = removeUrlParam(url).replaceAll("//+", "/").replaceAll("/$", "");
		if(url.split("/").length >= 2) {
			url = url.replaceAll("/\\w*$", "");
		}
		return url;
	}
	
	public static String formatUrl(String url) {
		url = url.replaceAll("//+", "/").replaceAll("/$", "");
		return url;
	}
	
	/**
	 * 去掉url参数
	 */
	public static String removeUrlParam(String url) {
		return url.replaceAll("[?#].*", "");
	}
	
	public static String getAsText(String url) {
		String urlString = "";
		try {
			URLConnection urlConnection = new URL(url).openConnection();
			urlConnection.setConnectTimeout(1000 * 30);	//30秒超时
			BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String current;
			while ((current = in.readLine()) != null) {
				urlString += current;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return urlString;
	}
}
