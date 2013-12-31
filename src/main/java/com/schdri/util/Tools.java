package com.schdri.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Tools {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static String DateToString(Date d){
		if (d==null)
			return null;
		else
			return sdf.format(d);
	}
}
