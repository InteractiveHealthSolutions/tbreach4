package com.ihsinformatics.minetbdashboard.server.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTimeUtil {

	public static final String FE_FORMAT = "dd/MM/yyyy HH:mm:ss";
	public static final String SQL_DATETIME = "yyyy-MM-dd HH:mm:ss";
	public static final String DOB_FORMAT = "dd/MM/yyyy";
	public static final String SQL_DATE = "yyyy-MM-dd";

	public static final int BAD_TX_START = -2;
	public static final int DATE_CALC_ERROR = -1;

	public static final int DAYS_IN_MONTH = 30;

	public static String getSQLDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(SQL_DATETIME);
		return sdf.format(date);
	}

	public static Date getDateFromString(String string, String format)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.parse(string);
	}

	public static String convertFromSlashFormatToSQL(String data) {

		System.out.println(data);

		String[] array = data.split("/");
		String date = array[0];
		String month = array[1];
		String year = array[2];

		return year + "-" + month + "-" + date;

	}

	public static int calculateMonthOfTreatment(String treatmentStartDate) {
		int monthOfTreatment = -1;
		System.out.println("TX START:------>" + treatmentStartDate);
		Date txStart = null;

		try {
			txStart = getDateFromString(treatmentStartDate, SQL_DATETIME);
		}

		catch (Exception e) {
			return DATE_CALC_ERROR;
		}

		GregorianCalendar gcTx = (GregorianCalendar) GregorianCalendar
				.getInstance();
		gcTx.setTime(txStart);

		GregorianCalendar gcNow = (GregorianCalendar) GregorianCalendar
				.getInstance();
		gcNow.setTimeInMillis(System.currentTimeMillis());

		long diff = gcNow.getTimeInMillis() - gcTx.getTimeInMillis();
		if (diff < 0)
			return BAD_TX_START;

		double diffInSeconds = diff / 1000;
		System.out.println(diffInSeconds);
		double diffInMinutes = diffInSeconds / 60;
		System.out.println(diffInMinutes);
		double diffInHours = diffInMinutes / 60;
		System.out.println(diffInHours);
		double diffInDays = diffInHours / 24;
		System.out.println(diffInDays);

		if (diffInDays >= 0 && diffInDays <= 23)
			return 0;
		if (diffInDays >= 24 && diffInDays <= 53)
			return 1;
		if (diffInDays >= 54 && diffInDays <= 83)
			return 2;
		if (diffInDays >= 84 && diffInDays <= 113)
			return 3;
		if (diffInDays >= 114 && diffInDays <= 143)
			return 4;
		if (diffInDays >= 144 && diffInDays <= 173)
			return 5;
		if (diffInDays >= 174 && diffInDays <= 203)
			return 6;
		if (diffInDays >= 204 && diffInDays <= 233)
			return 7;
		if (diffInDays >= 234 && diffInDays <= 263)
			return 8;
		if (diffInDays >= 264 && diffInDays <= 293)
			return 9;
		if (diffInDays >= 294 && diffInDays <= 323)
			return 10;

		/*
		 * 0 - 23 days = Month 0 24 - 53 days = Month 1 54 - 83 days = Month 2
		 * 84 - 113 days = Month 3 114 - 143 days = Month 4 144 - 173 days =
		 * Month 5 174 - 203 days = Month 6 204 - 233 days = Month 7 234 - 263
		 * days = Month 8 264- 293 days = Month 9 294- 323 days = Month 10
		 */
		/*
		 * double diffInMonths = diffInDays/DAYS_IN_MONTH;
		 * System.out.println(diffInMonths);
		 * 
		 * Double mnthDbl = new Double(diffInMonths);
		 * 
		 * monthOfTreatment = mnthDbl.intValue();
		 */

		return monthOfTreatment;
	}

}
