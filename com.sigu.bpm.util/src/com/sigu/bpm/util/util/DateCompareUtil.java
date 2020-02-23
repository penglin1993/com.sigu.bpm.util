package com.sigu.bpm.util.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateCompareUtil {

	public DateCompareUtil() {

	}

	/**
	 * 比较传入的两个日期字符串的大小 0等于/1大于/2小于/-1异常 传入的大小字符串格式必须为yyyy-MM-dd hh:mm:ss
	 * 比如2018-08-15 09:00:00
	 * 
	 * @param kssj
	 * @param jssj
	 * @return
	 */
	public static int compareDateText(String kssj, String jssj) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			Date dt1 = df.parse(kssj);
			Date dt2 = df.parse(jssj);
			if (dt1.getTime() > dt2.getTime()) {
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				return 2;
			} else {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 通过时间秒毫秒数判断两个时间的间隔天数
	 * 
	 * @param txt1
	 *            传入的日期字符串
	 * @param txt2
	 *            传入的日期字符串
	 * @return
	 */
	public static int calcuDaysByMillisecond(String txt1, String txt2) {
		int days = 0;
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date1 = format.parse(txt1);
			Date date2 = format.parse(txt2);
			days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return days;
	}

	/**
	 * 将传入的日期加上指定的天数后返回
	 * 
	 * @param txt
	 *            传入的日期字符串
	 * @param days
	 *            需要添加的天数
	 * @return 返回计算后的日期字符串 格式为yyyy-MM-dd
	 */
	public static String calcuDateAddDays(String txt, int days) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date = format.parse(txt);

			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, days);
			Date time = cal.getTime();
			String newDate = new SimpleDateFormat("yyyy-MM-dd").format(time);
			return newDate;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
