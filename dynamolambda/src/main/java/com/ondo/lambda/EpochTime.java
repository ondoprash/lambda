package com.ondo.lambda;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class EpochTime {
	public static final long TTL_IN_MILI_SECOND = 1000l * 60 * 60;
//	public static final long TTL_IN_MILI_SECOND = 1000l * 60 * 5;
	static SimpleDateFormat crunchifyFormat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");

	public static Long epochTTL(int ttlValue) {
		Calendar c=	Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date today = c.getTime();

		String currentTime = crunchifyFormat.format(today);

		Date date = new Date();
		try {
			date = crunchifyFormat.parse(currentTime);
		} catch (ParseException e) {

		}

		Long epochTime = date.getTime() + TTL_IN_MILI_SECOND*ttlValue;

		return epochTime/1000;

		
	}
	

}
