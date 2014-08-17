package com.medialab.lejuju.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UTimeShown
{
	/**
	 * 具体怎么显示时间到时候再改
	 * @author liananse
	 * @param timeString
	 * @return
	 * 2013-7-12
	 */
	public static String getTimeTitle(String timeString)
	{
		Calendar calendar = Calendar.getInstance();
		try {
			String today = (calendar.getTime().getYear()+1900) + "-" + (calendar.getTime().getMonth()+1) + "-" + calendar.getTime().getDate();
			String tomorrow = (calendar.getTime().getYear()+1900) + "-" + (calendar.getTime().getMonth()+1) + "-" + (calendar.getTime().getDate()+1);
			String timeStringDay = timeString.split(" ")[0].split("-")[0] + "-" + deleteZeroString(timeString.split(" ")[0].split("-")[1])
					+ "-" + deleteZeroString(timeString.split(" ")[0].split("-")[2]);
			
			if(today.equals(timeStringDay))
			{
				int index = timeString.indexOf(" ");
				return "今天 " + timeString.substring(index + 1);
			}
			else if(tomorrow.equals(timeStringDay))
			{
				int index = timeString.indexOf(" ");
				return "明天 " + timeString.substring(index + 1);
			}
			else 
			{
				int index = timeString.indexOf("-");
				return timeString.substring(index + 1);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return timeString;
		}
		
	}
	
	/**
	 * 中间用\n 隔开
	 * @author liananse
	 * @param beginTime
	 * @param endTime
	 * @return
	 * 2013-7-23
	 */
	public static String getTimeString(String beginTime, String endTime)
	{
		String beginTimeString = getTimeTitle(beginTime);
		String endTimeString = getTimeTitle(endTime);
		if (beginTimeString.split(" ")[0].equals(endTimeString.split(" ")[0]))
		{
			return beginTimeString.split(" ")[0] + "\n" + beginTimeString.split(" ")[1] + "--" + endTimeString.split(" ")[1];
		}
		else {
			return beginTimeString + "\n" + endTimeString;
		}
		
	}
	
	/**
	 * 中间用 --隔开
	 * @author liananse
	 * @param beginTime
	 * @param endTime
	 * @return
	 * 2013-7-23
	 */
	public static String getTimeStringShownInEventItem(String beginTime, String endTime)
	{
		String beginTimeString = getTimeTitle(beginTime);
		String endTimeString = getTimeTitle(endTime);
		if (beginTimeString.split(" ")[0].equals(endTimeString.split(" ")[0]))
		{
			return beginTimeString.split(" ")[0] + " " + beginTimeString.split(" ")[1] + "--" + endTimeString.split(" ")[1];
		}
		else {
			return beginTimeString + "--" + endTimeString;
		}
	}
	
	/**
	 * 中间用年月日隔开
	 * @author liananse
	 * @param beginTime
	 * @param endTime
	 * @return
	 * 2013-7-23
	 */
	public static String getTimeStringShownInEventItem(String beginTime)
	{
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy年MM月dd日");
		Date date;
		try
		{
			date = df1.parse(beginTime);
			return df2.format(date);
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 计算时间差
	 * @author liananse
	 * @param now
	 * @param last
	 * @return
	 * 2013-8-12
	 */
	public static boolean isLargeThanThressMinute(String now, String last)
	{
		boolean result = false;
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
	    try {
	    	Date dNow = df.parse(now);
			Date dLast = df.parse(last);
			
			long between=(dNow.getTime()-dLast.getTime())/1000;
			
			if (between/60 > 3)
			{
				return true;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	public static String getTrendsTime(String timeString, String Type)
	{
		Calendar calendar = Calendar.getInstance();
		try {
			String today = (calendar.getTime().getYear()+1900) + "-" + (calendar.getTime().getMonth()+1) + "-" + calendar.getTime().getDate();
			String tomorrow = (calendar.getTime().getYear()+1900) + "-" + (calendar.getTime().getMonth()+1) + "-" + (calendar.getTime().getDate()-1);
			String timeStringDay = timeString.split(" ")[0].split("-")[0] + "-" + deleteZeroString(timeString.split(" ")[0].split("-")[1])
					+ "-" + deleteZeroString(timeString.split(" ")[0].split("-")[2]);
			if(today.equals(timeStringDay))
			{
				int index = timeString.indexOf(" ");
				return timeString.substring(index + 1).split(":")[0] + ":" + timeString.substring(index + 1).split(":")[1];
			}
			else if(tomorrow.equals(timeStringDay))
			{
				return "昨天";
			}
			else 
			{
				if (Type.equals("trends"))
				{
					int index = timeString.indexOf("-");
					String month = timeString.substring(index + 1).split(" ")[0].split("-")[0];
					String day = timeString.substring(index + 1).split(" ")[0].split("-")[1];
					
					return deleteZeroString(month) + "月" + deleteZeroString(day) + "日";
				}
				else {
					String month = timeString.split(" ")[0].split("-")[1];
					String day = timeString.split(" ")[0].split("-")[2];
					
					return timeString.split(" ")[0].split("-")[0] + "年" + deleteZeroString(month) + "月" + deleteZeroString(day) + "日";
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return timeString;
		}
		
	}
	
	public static String getMsgCenterTimeShown(String timeString)
	{
		Calendar calendar = Calendar.getInstance();
		try {
			String today = (calendar.getTime().getYear()+1900) + "-" + (calendar.getTime().getMonth()+1) + "-" + calendar.getTime().getDate();
			String tomorrow = (calendar.getTime().getYear()+1900) + "-" + (calendar.getTime().getMonth()+1) + "-" + (calendar.getTime().getDate()-1);
			String timeStringDay = timeString.split(" ")[0].split("-")[0] + "-" + deleteZeroString(timeString.split(" ")[0].split("-")[1])
					+ "-" + deleteZeroString(timeString.split(" ")[0].split("-")[2]);
			if(today.equals(timeStringDay))
			{
				int index = timeString.indexOf(" ");
				return timeString.substring(index + 1).split(":")[0] + ":" + timeString.substring(index + 1).split(":")[1];
			}
			else if(tomorrow.equals(timeStringDay))
			{
				int index = timeString.indexOf(" ");
				return "昨天" + " " + timeString.substring(index + 1).split(":")[0] + ":" + timeString.substring(index + 1).split(":")[1];
			}
			else 
			{
				int index = timeString.indexOf("-");
				String month = timeString.substring(index + 1).split(" ")[0].split("-")[0];
				String day = timeString.substring(index + 1).split(" ")[0].split("-")[1];
				
				int indexTime = timeString.indexOf(" ");
				
				return deleteZeroString(month) + "月" + deleteZeroString(day) + "日" + " " + timeString.substring(indexTime + 1).split(":")[0] + ":" + timeString.substring(indexTime + 1).split(":")[1];
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return timeString;
		}
	}
	
	public static String deleteZeroString(String str)
	{
		String newStr = str.replaceFirst("^0*", "");
		
		return newStr;
	}
	
	/**
	 * 中间用年月日隔开
	 * @author liananse
	 * @param beginTime
	 * @param endTime
	 * @return
	 * 2013-7-23
	 */
	public static String getTimeStringShownInInfo(String beginTime)
	{
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date;
		try
		{
			date = df1.parse(beginTime);
			return df2.format(date);
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
