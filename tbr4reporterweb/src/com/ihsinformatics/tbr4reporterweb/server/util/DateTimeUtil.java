package com.ihsinformatics.tbr4reporterweb.server.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
}
