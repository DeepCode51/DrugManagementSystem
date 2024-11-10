package com.zcurd.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	
	public static String getDateYDMHMS() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	public static void main(String[] args) {
		System.out.println(getDateYDMHMS());
	}
}
