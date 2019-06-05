package com.shanjin.manager.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

import com.shanjin.manager.time.StatisticalUtil;

public class DateUtil {
	
	private  static final Logger log = Logger.getLogger(DateUtil.class);
	public static final String TIME_PATTERN = "HH:mm:ss";
	public static final String DATE_TIME_MS_PATTERN = "yyyy-MM-dd HH:mm:ss.S";
	public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_YYYYMMDD_PATTERN = "yyyyMMdd";
	public static final String DATE_YYYY_MM_DD_PATTERN = "yyyy-MM-dd";
	public static final String TIME_HHMM_PATTERN = "HH:mm";
	public static final String TIME_HHMM_PATTERN2 = "HHmm";
	public static final String DATE_TIME_NO_HORI_PATTERN = "yyyyMMdd HH:mm:ss";
	public static final String DATE_TIME_NO_SPACE_PATTERN = "yyyyMMddHHmmss";
	public static final String DATE_TIME_PLAYBILL_PATTERN = "yyyyMMdd HH:mm";
	public static final String DATE_TIME_INDEX_PLAYBILL_PATTERN = "yyyy-MM-dd HH:mm";
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_ENGLISH_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";



    public final static String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
    public final static String getDate(String date) {
      String newDate=date.replace("T", " ");
       return newDate;
    }
    public static String formatDate(String pattern, Date adate) {
		if(adate == null){
			return "";
		}
		

		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(adate);
	}

	public static Date parseDate(String pattern, String dateStr) {

		if (dateStr == null || "".equals(dateStr)) {
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(pattern);

		try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 计算时间的起始时间
	 * */
	private final static String BASIC_DATE = "2000-01-01 00:00:00";

	/**
	 * Checkstyle rule: utility classes should not have public constructor
	 */
	private DateUtil() {
	}

	/**
	 * 把日期字符串yyyy-MM-dd HH:mm:ss转换成HH:mm形式
	 */
	public static String strToString(String date) {
		if (date == null || "".equals(date)) {
			return date;
		}
		String temp = "";
		try {
			Date dateStr = DateUtil.parseDate(DATE_TIME_PATTERN ,date);
			temp = DateUtil.formatDate(TIME_HHMM_PATTERN,dateStr);
		} catch (Exception ex) {
			log.debug(ex.getStackTrace());
		}
		return temp;
	}

	/**
	 * 把日期字符串yyyy-MM-dd HH:mm:ss转换成yyyy-MM-dd HH:mm形式
	 */
	public static String strToStr(String date) {
		if (date == null || "".equals(date)) {
			return date;
		}
		String temp = "";
		try {
			Date dateStr = DateUtil.parseDate(DATE_TIME_PATTERN,date);
			temp = DateUtil.formatDate(DATE_TIME_INDEX_PLAYBILL_PATTERN,dateStr);
		} catch (Exception ex) {
			log.debug(ex.getStackTrace());
		}
		return temp;
	}

	public static String dateToString(Date date) {
		SimpleDateFormat df;
		String returnValue = "";
		if (date != null) {
			df = new SimpleDateFormat(DATE_FORMAT);
			returnValue = df.format(date);
		}

		return (returnValue);
	}





	/**
	 * This method generates a string representation of a date/time in the
	 * format you specify on input
	 * 
	 * @param aMask
	 *            the date pattern the string is in
	 * @param strDate
	 *            a string representation of a date
	 * @return a converted Date object
	 * @see java.text.SimpleDateFormat
	 * @throws ParseException
	 *             when String doesn't match the expected format
	 */
	public static Date convertStringToDate(String aMask, String strDate)
			throws ParseException {
		SimpleDateFormat df;
		Date date;
		df = new SimpleDateFormat(aMask);


		try {
			date = df.parse(strDate);
		} catch (ParseException pe) {
			// log.error("ParseException: " + pe);
			throw new ParseException(pe.getMessage(), pe.getErrorOffset());
		}

		return (date);
	}

	/**
	 * This method returns the current date time in the format: MM/dd/yyyy HH:MM
	 * a
	 * 
	 * @param theTime
	 *            the current time
	 * @return the current date/time
	 */
	public static String getTimeNow(Date theTime) {
		return getDateTime(TIME_PATTERN, theTime);
	}

	

	/**
	 * This method generates a string representation of a date's date/time in
	 * the format you specify on input
	 * 
	 * @param aMask
	 *            the date pattern the string is in
	 * @param aDate
	 *            a date object
	 * @return a formatted string representation of the date
	 * 
	 * @see java.text.SimpleDateFormat
	 */
	public static String getDateTime(String aMask, Date aDate) {
		SimpleDateFormat df = null;
		String returnValue = "";

		if (aDate == null) {
			log.error("aDate is null!");
		} else {
			df = new SimpleDateFormat(aMask);
			returnValue = df.format(aDate);
		}

		return (returnValue);
	}



	public static java.sql.Date convertDateToSqlDate(Date date) {
		return new java.sql.Date(date.getTime());
	}

	public static java.sql.Timestamp convertDateToTimestamp(Date date) {
		return new java.sql.Timestamp(date.getTime());
	}

	public static String getNowTime(Date date) {
		if (date == null) {
			return "";
		}
		return DateUtil.formatDate(DATE_TIME_MS_PATTERN,date);
	}

	public static String getDateTime(String sdate) {
		try {
			java.sql.Timestamp date = stringToTimestamp(sdate);
			return DateUtil.formatDate(DATE_TIME_PATTERN,date);
		} catch (Exception e) {
			return sdate;
		}
	}

	public static java.sql.Timestamp stringToTimestamp(String timestampStr) {
		if (timestampStr == null || timestampStr.length() < 1)
			return null;
		return java.sql.Timestamp.valueOf(timestampStr);
	}

	/**
	 * 根据日期计算出所在周的日期，并返回大小为7的数组
	 * 
	 * @param date
	 * @return
	 */
	public static String[] getWholeWeekByDate(Date date) {
		String[] ss = new String[7];
		Calendar calendar = Calendar.getInstance();
		for (int i = 0, j = 2; i < 6 && j < 8; i++, j++) {
			calendar.setTime(date);
			calendar.setFirstDayOfWeek(Calendar.MONDAY);
			calendar.set(Calendar.DAY_OF_WEEK, j);
			ss[i] = getFormatDate(calendar.getTime());
		}
		calendar.setTime(date);
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + 6);
		ss[6] = getFormatDate(calendar.getTime());
		return ss;
	}

	/**
	 * 返回格式 yyyyMMdd的日期格式
	 * 
	 * @param d
	 * @return
	 */
	public static String getFormatDate(Date d) {
		return DateUtil.formatDate(DATE_YYYYMMDD_PATTERN,d);
	}

	public static String getHHmm2(Date d) {
		return DateUtil.formatDate(TIME_HHMM_PATTERN2,d);
	}

	public static Date getDateByString(String pattern) throws ParseException {
		return DateUtil.parseDate(DATE_YYYYMMDD_PATTERN,pattern);
	}

	public static Date getPlayBillTimeByPattern(String date)
			throws ParseException {
		return DateUtil.parseDate(DATE_TIME_PLAYBILL_PATTERN,date);
	}

	public static String getNowTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String nowTime = df.format(date);
		return nowTime;
	}

	/**
	 * @return 当前标准日期yyyyMMddHHmmss
	 */
	public static String getNowTimeNumber() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		String nowTime = df.format(date);
		return nowTime;
	}

	/**
	 * 获取从2000年1月1日 00:00:00开始到指定日期的秒数
	 * 
	 * @param 日期
	 * @return long
	 */
	public static Long getSeconds(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		Date basicDate = formatter.parse(BASIC_DATE, new ParsePosition(0));
		long secLong = (date.getTime() - basicDate.getTime()) / 1000L;
		return secLong;
	}

	/**
	 * 获取从2000年1月1日 00:00:00开始到指定日期的秒数
	 * 
	 * @param 日期
	 * @param 日期格式
	 *            例如：yyyy-MM-dd HH:mm:ss
	 * @return long
	 */
	public static Long getSeconds(String dateStr, String df) {
		if (dateStr == null || "".equals(dateStr)) {
			return null;
		}
		if (df == null || "".equals(df)) {
			df = DATE_FORMAT;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(df);
		Date date = formatter.parse(dateStr, new ParsePosition(0));
		return getSeconds(date);
	}

	/**
	 * 返回格式 yyyyMMdd的日期格式
	 * 
	 * @param d
	 * @return
	 */

	public static Date getDateByStringyyyyMMddHHmmss(String pattern)
			throws ParseException {
		return DateUtil.parseDate(DATE_TIME_NO_SPACE_PATTERN,pattern);
	}

	public static Date getFormatDateByEnglishSDF(String s) {
			return DateUtil.parseDate(DATE_ENGLISH_FORMAT,s);
	}

	public static String getFormatDateByyyyyMMddHHmmssFile(Date d) {
		return DateUtil.formatDate(DATE_TIME_NO_SPACE_PATTERN,d);
	}

	public static String formateStrDate(String d) {
		Date formateDate = DateUtil.parseDate(DATE_TIME_PATTERN,d);
		String dateStr = getFormatDateByyyyyMMddHHmmssFile(formateDate);
		return dateStr;
	}

	/**
	 * 将格式为yyyyMMddhhmmss的字符串 格式为yyyy-MM-dd
	 * 
	 * @param yyyyMMddhhmmss
	 * @return
	 */
	public static String formatDate(String d) {
		Date formateDate = DateUtil.parseDate(DATE_TIME_NO_SPACE_PATTERN,d);
		String dateStr = DateUtil.formatDate(DATE_YYYY_MM_DD_PATTERN,formateDate);
		return dateStr;
	}


	public static String formatLongToTimeStr(Long msl) {
		String str = "";
		Integer day = 0;
		Integer hour = 0;
		Integer minute = 0;
		Integer second = 0;
		Integer ms = 0;

		second = msl.intValue() / 1000;
		ms = msl.intValue() % 1000;

		if (second > 60) {
			minute = second / 60;
			second = second % 60;
		}
		if (minute > 60) {
			hour = minute / 60;
			minute = minute % 60;
		}
		if (hour > 24) {
			day = hour / 24;
			hour = hour % 24;
		}
		
		if (day > 0)
			str = day.toString() + "天";
		if (hour > 0)
			str += hour.toString() + "小时";
		if (minute > 0)
			str += minute.toString() + "分钟";
		if (second > 0)
			str += second.toString() + "秒";
		if (ms > 0)
			str += ms.toString() + "毫秒";

		return str;
	}
	public static String formatLongToTxt(Long msl) {
		String str = "";
		Integer day = 0;
		Integer hour = 0;
		Integer minute = 0;
		Integer second = 0;

		second = msl.intValue() / 1000;

		if (second > 60) {
			minute = second / 60;
			second = second % 60;
		}
		if (minute > 60) {
			hour = minute / 60;
			minute = minute % 60;
		}
		if (hour > 24) {
			day = hour / 24;
			hour = hour % 24;
		}
		
		if (day > 0)
			str = day.toString() + "天";
		if (hour > 0)
			str += hour.toString() + "小时";
		if (minute > 0)
			str += minute.toString() + "分钟";

		return str;
	}

	public static long getBetweenDays(Date t1, Date t2) throws ParseException {
		return ((t2.getTime()) - t1.getTime()) / 1000 / 60 / 60 / 24;

	}

	public static int getIntervalDaysOfExitDate2(Date exitDateFrom,
			Date exitDateTo) {
		Calendar aCalendar = Calendar.getInstance();
		Calendar bCalendar = Calendar.getInstance();
		aCalendar.setTime(exitDateFrom);
		bCalendar.setTime(exitDateTo);
		int days = 0;
		while (aCalendar.before(bCalendar)) {
			days++;
			aCalendar.add(Calendar.DAY_OF_YEAR, 1);
		}
		return days;
	}

	
	
	/****
	 * 
	 * 
	 * @param currentTimes
	 *            当前时间的毫秒数
	 * @param times
	 *            输入判断的毫秒数
	 * @return yesteday today tomorrow
	 */
	public static String getMarkByTime(long currentTimes, long times) {
		String result = "";

		long rate = 24 * 3600 * 1000;

		long today = currentTimes / rate;
		long yesterday = today - 1;
		long beforeYesterday = yesterday - 1;
		long tomorrow = today + 1;

		long input = times / rate;
		if (input == yesterday) {
			result = "yesterday";
		} else if (input == today) {
			result = "today";
		} else if (input == beforeYesterday) {
			result = "beforeYesterday";
		} else if (input == tomorrow) {
			result = "tomorrow";
		}
		return result;
	}
	/**
	 * 获取下月的第一天
	 * @param curMonth yyyy-mm : 2011-09
	 * @return
	 */
	public static String getNextMonth(String curMonth) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		String startDate = ""; // 当前月的第一天
		String endDate = ""; // 下个月的第一天
		String[] ta = curMonth.split("-");
		c.setTime(java.sql.Date.valueOf(curMonth + "-01"));
		c.add(Calendar.MONTH, 0);// 当前月
		c.set(Calendar.DAY_OF_MONTH, 1);// 设置月份的第一天
		startDate = sdf.format(c.getTime());
		c.set(Calendar.MONTH, Integer.parseInt(ta[1]));// 回到当前月
		c.set(Calendar.DAY_OF_MONTH, 1);// 设置当前月第一天
		c.add(Calendar.DAY_OF_YEAR, 0); //
		endDate = sdf.format(c.getTime());
		return endDate;
	}
	/**
	 * 获取日期范围内的月份明细
	 * @param startMonth 2012-07
	 * @param endMonth 2012-09
	 * @return	{2012-07,2012-08,2012-09}
	 * @throws Exception
	 */
	public static List<String> getMonthList(String startMonth,String endMonth) throws Exception{
		List<String> monthList = new ArrayList<String>();
		monthList.add(startMonth);
		while(!startMonth.equals(endMonth)){
			startMonth = DateUtil.getNextMonth(startMonth);
			if(startMonth != null&&!startMonth.equals("")){
				startMonth = startMonth.substring(0,7);
				monthList.add(startMonth);
			}
		}
			
		return monthList;
	}
	
	/**
	 * 得到某年某月的第一天
	 * 
	 * @param yearMonth 2012-09
	 * @return
	 */
	public static String getFirstDayOfMonth(String yearMonth) {
		String[] tmp = yearMonth.split("-");
		int year = Integer.parseInt(tmp[0]);
		int month = Integer.parseInt(tmp[1]);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DATE));
		return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
	}

	/**
	 * 得到某年某月的最后一天
	 * 
	 * @param yearMonth 2012-09
	 * @return
	 */
	public static String getLastDayOfMonth(String yearMonth) {
		String[] tmp = yearMonth.split("-");
		int year = Integer.parseInt(tmp[0]);
		int month = Integer.parseInt(tmp[1]);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		int value = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.DAY_OF_MONTH, value);
		return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
	}
	
	
	public static Date getFirstDayOfWeek() {  
	    Calendar monday = Calendar.getInstance();  
	    return getADayOfWeek(monday, Calendar.MONDAY).getTime();  
	}  
	  
	public static Calendar getFirstDayOfWeek(Calendar day) {  
	    Calendar monday = (Calendar) day.clone();  
	    return getADayOfWeek(monday, Calendar.MONDAY);  
	}  
	  
	public static Date getLastDayOfWeek() {  
	    Calendar sunday = Calendar.getInstance();  
	    return getADayOfWeek(sunday, Calendar.SUNDAY).getTime();  
	}  
	  
	public static Calendar getLastDayOfWeek(Calendar day) {  
	    Calendar sunday = (Calendar) day.clone();  
	    return getADayOfWeek(sunday, Calendar.SUNDAY);  
	}  
	  
	private static Calendar getADayOfWeek(Calendar day, int dayOfWeek) {  
	    int week = day.get(Calendar.DAY_OF_WEEK);  
	    if (week == dayOfWeek)  
	        return day;  
	    int diffDay = dayOfWeek - week;  
	    if (week == Calendar.SUNDAY) {  
	        diffDay -= 7;  
	    } else if (dayOfWeek == Calendar.SUNDAY) {  
	        diffDay += 7;  
	    }  
	    day.add(Calendar.DATE, diffDay);  
	    return day;  
	} 
	/**
	 * 获取该日期对应的周几的下一周的日期
	 * @author 陈银东
	 * 下午1:27:07
	 * String
	 */
	public static String getDayOfNextWeek(String dayString,String pattern){
		String dayOfNextWeekString = null;
		try {
			Date date = convertStringToDate(pattern, dayString);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.DAY_OF_YEAR, 7);
			dayOfNextWeekString = getFormatDate(c.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dayOfNextWeekString;
	}
	
	public static Date getDayOfNextWeekDate(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, 7);
		return c.getTime();
	}
	
	/**
	 *  返回date1与date2相差天数
	 */
	public static int daysBetweenDate(Date date1,Date date2){
		Calendar cal = Calendar.getInstance();     
        cal.setTime(date1);     
        long time1 = cal.getTimeInMillis();                  
        cal.setTime(date2);     
        long time2 = cal.getTimeInMillis();          
        long between_days=(time1-time2)/(1000*3600*24);     
            
       return Integer.parseInt(String.valueOf(between_days)); 
		
	}
	
	public static Date getNextDay(Date date,int next){
		Calendar cal = Calendar.getInstance();     
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, next);
        return cal.getTime();
	}
    
    public static void main(String[] args){
    	String path="/ExtJS4.2/icons/100.png";
    	String filename = path.substring(path.lastIndexOf("/")+1);
    	System.out.println(filename);
    }
	public static String getDateparse(String str) {
		DateFormat format=new SimpleDateFormat("yyyy-MM");
		Date da;
		String head_date="";
		
		try {
			da = format.parse(str);
		    head_date=format.format(da); 
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return head_date.replace("-0", "_").replace("-", "_");
	}
	public static boolean compareTime(String str) {
		DateFormat format=new SimpleDateFormat("yyyy-MM");
		Date da;
		try {
			da = format.parse(str);
		    Date dastart=format.parse("2016-07");
			Date daend=format.parse(format.format(new Date()));
			if(da.getTime()<dastart.getTime()||da.getTime()>=daend.getTime()){
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static String getDateMerOrderparse(String str) {
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Date da;
		String head_date="";
		
		try {
			da = format.parse(str);
		    head_date=format.format(da); 
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return head_date.replace("-", "_");
	}
	
	public static boolean compareMerOrderTime(String str) {
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Date da;
		try {
			da = format.parse(str);
		    Date dastart=format.parse("2016-07-31");
			Date daend=format.parse(format.format(new Date()));
			if(da.getTime()<dastart.getTime()||da.getTime()>=daend.getTime()){
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return false;
	}
}

