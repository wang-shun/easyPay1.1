package com.acewill.ordermachine.util_new;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {

	private static final String dayPattern = "yyyy-MM-dd";


	public static boolean inHourTime(String startTime, String endTime) {
		Date date        = new Date();
		int  currentTime = date.getHours() * 100 + date.getMinutes();
		if (!TextUtils.isEmpty(startTime)) {
			Integer stime = Integer.valueOf(startTime.replace(":", ""));
			if (stime > currentTime) {
				return false;
			}
		}
		if (!TextUtils.isEmpty(endTime)) {
			Integer etime = Integer.valueOf(endTime.replace(":", ""));
			if (etime < currentTime) {
				return false;
			}
		}
		return true;
	}


	public static String getHourStr() {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		Date             date   = new Date();
		return format.format(date);
	}

	//获取小时分钟字符串
	public static String getHour() {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		Date             date   = new Date();
		return format.format(date);
	}

	//比较两个时间，d1>d2返回1，d1<d2返回-1，d1=d2返回0
	public static int compareData(String d1, String d2) {
		int i = 0;
		try {
			DateFormat dateFormat = new SimpleDateFormat("HH:mm");
			Date       dateTime1  = dateFormat.parse(d1);
			Date       dateTime2  = dateFormat.parse(d2);
			i = dateTime1.compareTo(dateTime2);
			System.out.println(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return i;
	}

	/**\
	 * 判断两个日期是否为同一天
	 *
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameDate(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);

		boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
				.get(Calendar.YEAR);
		boolean isSameMonth = isSameYear
				&& cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
		boolean isSameDate = isSameMonth
				&& cal1.get(Calendar.DAY_OF_MONTH) == cal2
				.get(Calendar.DAY_OF_MONTH);

		return isSameDate;
	}

	//获取当天星期
	public static String getWeekOfDate() {
		Date     dt       = new Date();
		String[] weekDays = {"7", "1", "2", "3", "4", "5", "6"};
		Calendar cal      = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return weekDays[w];
	}

	public static String getHours(String time) {
		try {
			if (TextUtils.isEmpty(time)) {
				return time;
			}
			SimpleDateFormat format  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat format2 = new SimpleDateFormat("HH:mm:ss");
			Date             data    = format.parse(time);
			return format2.format(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	//时间戳转字符串
	public static Integer getStringData() {
		SimpleDateFormat format = new SimpleDateFormat("MMddmmss");
		return Integer.valueOf(format.format(System.currentTimeMillis()));
	}


	public static boolean isToday(Date date) {
		Date begin = DateUtils.getDayBegin();
		Date end   = DateUtils.getDayEnd();
		if (begin.getTime() < date.getTime() && end.getTime() > date.getTime())
			return true;
		return false;
	}

	/**
	 * 计算两个日期之间相差的天数
	 *
	 * @param smdate 较小的时间
	 * @param bdate  较大的时间
	 * @return 相差天数
	 * @throws ParseException
	 */
	public static int daysBetween(Date smdate, Date bdate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd 00:00:00");
		smdate = sdf.parse(sdf.format(smdate));
		bdate = sdf.parse(sdf.format(bdate));
		Calendar cal = Calendar.getInstance();
		cal.setTime(smdate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bdate);
		long time2        = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * 判断两个时间相差是否大于一天
	 *
	 * @param smdate 较小的时间
	 * @param bdate  较大的时间
	 * @throws ParseException
	 */
	public static boolean moreThanOneDay(Date smdate, Date bdate) throws ParseException {
		//
		long time1         = smdate.getTime();
		long time2         = bdate.getTime();
		long between_days  = (time2 - time1) / (1000 * 3600 * 24);
		long between_days2 = (time2 - time1) % (1000 * 3600 * 24);

		if (between_days == 1) {
			if (between_days2 > 1) {
				return true;
			} else {
				return false;
			}
		} else if (between_days < 1) {
			return false;
		} else {
			return true;
		}
	}


	/**
	 * 计算两个时间的小时差
	 *
	 * @param eDate
	 * @param sDate
	 * @return
	 */
	public static long getDateBetweenHour(Date eDate, Date sDate) {

		long nd = 1000 * 24 * 60 * 60;
		long nh = 1000 * 60 * 60;
		long nm = 1000 * 60;
		// long ns = 1000;
		// 获得两个时间的毫秒时间差异
		long diff = eDate.getTime() - sDate.getTime();
		// 计算差多少天
		long day = diff / nd;
		// 计算差多少小时
		long hour = diff % nd / nh + day * 24;
		// 计算差多少分钟
		return hour;
	}

	/**
	 * 计算两个时间的分差
	 *
	 * @param eDate
	 * @param sDate
	 * @return
	 */
	public static long getDateBetweenMinute(Date eDate, Date sDate) {

		long nd = 1000 * 24 * 60 * 60;
		long nh = 1000 * 60 * 60;
		long nm = 1000 * 60;
		// 获得两个时间的毫秒时间差异
		long diff = eDate.getTime() - sDate.getTime();
		// 计算差多少分钟
		long min = diff % nd % nh / nm;
		return min;
	}


	public static String getDatePoor(Date endDate, Date nowDate) {

		long nd = 1000 * 24 * 60 * 60;
		long nh = 1000 * 60 * 60;
		long nm = 1000 * 60;
		// long ns = 1000;
		// 获得两个时间的毫秒时间差异
		long diff = endDate.getTime() - nowDate.getTime();
		// 计算差多少天
		long day = diff / nd;
		// 计算差多少小时
		long hour = diff % nd / nh;
		// 计算差多少分钟
		long min = diff % nd % nh / nm;
		// 计算差多少秒//输出结果
		// long sec = diff % nd % nh % nm / ns;
		return day + "天" + hour + "小时" + min + "分钟";
	}


	/**
	 * 计算两个日期之间
	 */
	public static int calculteTime(Date smdate, Date bdate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		smdate = sdf.parse(sdf.format(smdate));
		bdate = sdf.parse(sdf.format(bdate));
		Calendar cal = Calendar.getInstance();
		cal.setTime(smdate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bdate);
		long time2        = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));
	}
}
