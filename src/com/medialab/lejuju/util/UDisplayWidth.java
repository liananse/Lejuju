package com.medialab.lejuju.util;

import android.content.Context;

public class UDisplayWidth {

	/**
	 * 
	 * 获取请求小头像的尺寸
	 * @author liananse
	 * @param context
	 * @return
	 * 2013-8-6
	 */
	public static int getSmallHeadPicWidth(Context context)
	{
		return 120;
	}
	
	/**
	 * 获取大头像的尺寸
	 * @author liananse
	 * @param context
	 * @return
	 * 2013-8-6
	 */
	public static int getLargeHeadPicWidth(Context context)
	{
		if (context.getResources().getDisplayMetrics().widthPixels <= 480)
		{
			return 120;
		}
		else if (context.getResources().getDisplayMetrics().widthPixels <= 720)
		{
			return 160;
		}
		else {
			return 220;
		}
	}
	
	/**
	 * 返回活动封面的图片请求尺寸
	 * @author liananse
	 * @param context
	 * @return
	 * 2013-8-6
	 */
	public static int getPosterPicWidth(Context context)
	{
		if (context.getResources().getDisplayMetrics().widthPixels <= 320)
		{
			return 320;
		}
		else if (context.getResources().getDisplayMetrics().widthPixels <= 480)
		{
			return 480;
		}
		else if (context.getResources().getDisplayMetrics().widthPixels <= 720)
		{
			return 680;
		}
		else 
		{
			return 680;
		}
	}
	
	/**
	 * 以往活动图片的尺寸
	 * @author liananse
	 * @param context
	 * @return
	 * 2013-8-6
	 */
	public static int getPastEventPicWidth(Context context)
	{
		if (context.getResources().getDisplayMetrics().widthPixels <= 480)
		{
			return 120;
		}
		else if (context.getResources().getDisplayMetrics().widthPixels <= 720)
		{
			return 160;
		}
		else
		{
			return 220;
		}
	}
	
	public static int getEventDetailPicWidth(Context context)
	{
		return 120;
	}
	
	public static int getPhotoPicWidth(Context context)
	{
		if (context.getResources().getDisplayMetrics().widthPixels <= 480)
		{
			return 160;
		}
		else if (context.getResources().getDisplayMetrics().widthPixels <= 720)
		{
			return 220;
		}
		else
		{
			return 320;
		}
	}
	
	
}
