package com.common.util;

import com.common.entity.PageData;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * <pre>
 * 对象功能:日期处理类
 * 开发人员:rdexpense
 * 创建时间:2018-01-15
 * </pre>
 */
public class DateUtil {

	public final static SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
	public final static SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
	public final static SimpleDateFormat sdfDays = new SimpleDateFormat("yyyyMMdd");
	public final static SimpleDateFormat sdfSecond = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final static SimpleDateFormat sdfSeconds = new SimpleDateFormat("yyyyMMddHHmmss");
	public final static SimpleDateFormat sdfMill = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	public final static SimpleDateFormat sdfMills = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	public final static SimpleDateFormat sdfMinute = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final String DEFAULT_DAY_FORMAT_PATTERN = "yyyy-MM-dd";
	public static final String SIMPLE_PATTERN = "yyyyMMdd";
	public static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

	public static String getDateFormat(Date date) {
		return getDateFormat(date, DEFAULT_DAY_FORMAT_PATTERN);
	}

	public static String getDayFormat(Date date) {
		return getDateFormat(date, SIMPLE_PATTERN);
	}

	public static String getDateFormat(Date date, String format) {
		return new DateTime(date).toString(format);
	}


	public static String parseDateTime(Date date) {
		return new DateTime().toString(DEFAULT_DATE_FORMAT_PATTERN);
	}

	public static Date parseDate(String dateStr) {
		return parseDate(dateStr, DEFAULT_DAY_FORMAT_PATTERN);
	}

	public static Date parseDate(String dateStr, String format) {
		return DateTimeFormat.forPattern(format).parseDateTime(dateStr).toDate();
	}

	/**
	 * 根据日期获取SimpleDateFormat类型信息
	 * 
	 * @param sdf:SimpleDateFormat对象
	 * @param date:日期
	 */
	public static String getFormatDate(SimpleDateFormat sdf, Object date) {
		try {
			if (date != null && !"".equals(date)) {
				if (date instanceof Date) {
					return sdf.format(date);
				} else if (date instanceof String) {
					return sdf.format(sdfMill.parse(date.toString()));
				} else {
					return "";
				}
			} else {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 将毫秒数转成日期格式
	 * 
	 * @param timeInMillis:毫秒数
	 */
	public static String getCalendar(long timeInMillis) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);
		Date date = c.getTime();
		return sdfSecond.format(date);
	}

	/**
	 * 根据日期获取yyyy格式
	 * 
	 * @param date:日期
	 */
	public static String getYear(Object date) {
		return getFormatDate(sdfYear, date);
	}

	/**
	 * 根据当前日期获取yyyy格式
	 */
	public static String getCurrentYear() {
		return sdfYear.format(new Date());
	}

	/**
	 * 根据日期获取yyyy-MM-dd格式
	 * 
	 * @param date:日期
	 */
	public static String getDay(Object date) {
		return getFormatDate(sdfDay, date);
	}

	/**
	 * 根据当前日期获取yyyy-MM-dd格式
	 */
	public static String getCurrentDay() {
		return sdfDay.format(new Date());
	}

	/**
	 * 根据日期获取yyyyMMdd格式
	 * 
	 * @param date:日期
	 */
	public static String getDays(Object date) {
		return getFormatDate(sdfDays, date);
	}

	/**
	 * 根据当前日期获取yyyyMMdd格式
	 */
	public static String getCurrentDays() {
		return sdfDays.format(new Date());
	}

	/**
	 * 根据日期获取yyyy-MM-dd HH:mm:ss格式
	 * 
	 * @param date:日期
	 */
	public static String getSecond(Object date) {
		return getFormatDate(sdfSecond, date);
	}

	/**
	 * 根据当前日期获取yyyy-MM-dd HH:mm:ss格式
	 */
	public static String getCurrentSecond() {
		return sdfSecond.format(new Date());
	}

	/**
	 * 根据日期获取yyyyMMddHHmmss格式
	 * 
	 * @param date:日期
	 */
	public static String getSeconds(Object date) {
		return getFormatDate(sdfSeconds, date);
	}

	/**
	 * 根据当前日期获取yyyyMMddHHmmss格式
	 */
	public static String getCurrentSeconds() {
		return sdfSeconds.format(new Date());
	}

	/**
	 * 根据日期获取yyyy-MM-dd HH:mm:ss.SSS格式
	 * 
	 * @param date:日期
	 */
	public static String getMill(Object date) {
		return getFormatDate(sdfMill, date);
	}

	/**
	 * 根据当前日期获取yyyy-MM-dd HH:mm:ss.SSS格式
	 */
	public static String getCurrentMill() {
		return sdfMill.format(new Date());
	}

	/**
	 * 根据日期获取yyyyMMddHHmmssSSS格式
	 * 
	 * @param date:日期
	 */
	public static String getMills(Object date) {
		return getFormatDate(sdfMills, date);
	}

	/**
	 * 根据当前日期获取yyyyMMddHHmmssSSS格式
	 */
	public static String getCurrentMills() {
		return sdfMills.format(new Date());
	}

	/**
	 * 将时间yyyy-MM-dd HH:mm:ss转化成毫秒数
	 * 
	 * @param conversionTime:字符日期
	 * @throws ParseException
	 */
	public static long getTimeInMillis(String conversionTime) {
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(conversionTime));
			long currentSecondInMillis = calendar.getTimeInMillis();
			return currentSecondInMillis;
		} catch (Exception e) {
			return 0;
		}
	}

    /**
     * 时间段的条件查询 【进行比较的时间（精确到时分秒）】
     * @param strartTime 开始时间
     * @param endTime 结束时间
     *
     * @return
     */
    public static PageData period(String strartTime, String endTime) {

        PageData pageData = new PageData();
        StringBuilder stringBuilderStrat = new StringBuilder();
        StringBuilder stringBuilderEnd = new StringBuilder();
        try {
            String sTime = " 00:00:00";
            stringBuilderStrat.append(strartTime);
            stringBuilderStrat.append(sTime);
            String resultStartTime = stringBuilderStrat.toString();
            Date startDate = sdfSecond.parse(resultStartTime);
            pageData.put("startTime", sdfSecond.format(startDate));
        } catch (ParseException e) {
        }
        try {
            String eTime = " 23:59:59";
            stringBuilderEnd.append(endTime);
            stringBuilderEnd.append(eTime);
            String resultEndTime = stringBuilderEnd.toString();
            Date endDate = sdfSecond.parse(resultEndTime);
            pageData.put("endTime", sdfSecond.format(endDate));
        } catch (ParseException e) {
        }
        return pageData;
    }

	/**
	 * 日期比较大小
	 * @param strartDate 查询条件的开始日期
	 * @param endDate 查询条件的结束时间
	 * @param settStartDate 库中周期的开始时间
	 * @param settEndDate 库中周期的结束时间
	 * @return
	 */
	public static boolean periodDate(String strartDate, String endDate, String settStartDate, String settEndDate) {
		boolean flag = false;
		try {
			//查询条件的开始日期
			Date sDate = sdfDay.parse(strartDate);
			//查询条件的结束时间
			Date eDate = sdfDay.parse(endDate);
			//库中周期的开始时间
			Date settleStart = sdfDay.parse(settStartDate);
			//库中周期的结束时间
			Date settleEnd = sdfDay.parse(settEndDate);
			//结算周期符合查询条件，返回TRUE
			if (!(settleEnd.before(sDate) || settleStart.after(eDate))) {
				flag = true;
				return flag;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 日期转换为字符串 格式自定义
	 *
	 * @param date
	 * @param destFormat
	 * @return String
	 */
	public static String dateStr(Date date, String destFormat) {
		if (date == null){
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat(destFormat);
		return format.format(date);
	}

	/**
	 * 计算两个日期之间相差的天数
	 * @param date1 开始日期
	 * @param date2 结束日期
	 * @return  date1>date2时结果<0,  date1=date2时结果=0, date2>date1时结果>0
	 */
	public static int daysBetween(Date date1, Date date2, String format){
		DateFormat sdf=new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance();
		try {
			Date d1 = sdf.parse(DateUtil.dateStr(date1, format));
			Date d2 = sdf.parse(DateUtil.dateStr(date2, format));
			cal.setTime(d1);
			long time1 = cal.getTimeInMillis();
			cal.setTime(d2);
			long time2 = cal.getTimeInMillis();
			return Integer.parseInt(String.valueOf((time2 - time1) / 86400000L));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}


	public static int timeBetween(String date1, String date2, String format){
		DateFormat sdf=new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance();
		try {
			Date d1 = sdf.parse(date1);
			Date d2 = sdf.parse(date2);
			cal.setTime(d1);
			long time1 = cal.getTimeInMillis();
			cal.setTime(d2);
			long time2 = cal.getTimeInMillis();
			return Integer.parseInt(String.valueOf((time2 - time1)));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	// public static void main(String[] args) {
	// String date = "2015-10-10 11:21:00.310";
	// // Date date = new Date();
	// System.out.println("getYear------------->" + getYear(date));
	// System.out.println("getDay-------------->" + getDay(date));
	// System.out.println("getDays------------->" + getDays(date));
	// System.out.println("getSecond----------->" + getSecond(date));
	// System.out.println("getSeconds---------->" + getSeconds(date));
	// System.out.println("getMill------------->" + getMill(date));
	// System.out.println("getMills------------>" + getMills(date));
	//
	// System.out.println("getCurrentYear------>" + getCurrentYear());
	// System.out.println("getCurrentDay------->" + getCurrentDay());
	// System.out.println("getCurrentDays------>" + getCurrentDays());
	// System.out.println("getCurrentSecond---->" + getCurrentSecond());
	// System.out.println("getCurrentSeconds--->" + getCurrentSeconds());
	// System.out.println("getCurrentMill------>" + getCurrentMill());
	// System.out.println("getCurrentMills----->" + getCurrentMills());
	// }

}
