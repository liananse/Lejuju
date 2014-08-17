package com.medialab.lejuju.util;

import java.io.ByteArrayOutputStream;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.widget.Toast;

import com.medialab.lejuju.db.DDBOpenHelper;
import com.medialab.lejuju.main.userinfo.model.UserInfoReloadModel;
import com.medialab.lejuju.model.FriendsModel;
import com.medialab.lejuju.model.SelfUserInfoModel;

public class UUtils
{

	/**
	 * 
	 * @author zenghui
	 * @create 2013-7-7
	 * @param context
	 * @return SelfUserInfoModel
	 */
	public static SelfUserInfoModel getSelfUserInfoModel(Context context)
	{
		DDBOpenHelper mdDdbOpenHelper = DDBOpenHelper.getInstance(context);

		return mdDdbOpenHelper.selectSelfUserInfoModelFromDB();
	}
	
	/**
	 * selfUserInfoModel To FriendsModel
	 * @author liananse
	 * @param mSelfUserInfoModel
	 * @return
	 * 2013-8-9
	 */
	public static FriendsModel selfUserInfoModelToFriendsModel(Context context)
	{
		DDBOpenHelper mdDdbOpenHelper = DDBOpenHelper.getInstance(context);

		SelfUserInfoModel mSelfUserInfoModel = mdDdbOpenHelper.selectSelfUserInfoModelFromDB();
		if (mSelfUserInfoModel != null)
		{
			FriendsModel resultModel = new FriendsModel();
			
			resultModel.user_id = mSelfUserInfoModel.user_id;
			resultModel.nick_name = mSelfUserInfoModel.nick_name;
			resultModel.sex = mSelfUserInfoModel.sex;
			resultModel.mobile = mSelfUserInfoModel.mobile;
			resultModel.area = mSelfUserInfoModel.area;
			resultModel.country = mSelfUserInfoModel.country;
			resultModel.account = mSelfUserInfoModel.account;
			resultModel.school = mSelfUserInfoModel.school;
			resultModel.company = mSelfUserInfoModel.company;
			resultModel.introduce = mSelfUserInfoModel.introduce;
			resultModel.head_pic = mSelfUserInfoModel.head_pic;
			resultModel.namePinYin = UTextUtils.getPingYin(mSelfUserInfoModel.nick_name);
			resultModel.namePinYinFirst = UTextUtils.getPinYinHeadChar(mSelfUserInfoModel.nick_name);
			resultModel.isSelected = 0;
			resultModel.relation = 4;
			resultModel.department = mSelfUserInfoModel.department;
			resultModel.enter_school_year = mSelfUserInfoModel.enter_school_year;
			
			return resultModel;
		}
		return null;
	}
	
	/**
	 * 将UserInfoReloadModel 转为FriendsModel
	 * @author liananse
	 * @param model
	 * @return
	 * 2013-8-27
	 */
	public static FriendsModel initMFriendsModel(UserInfoReloadModel model)
	{
		FriendsModel tempModel = new FriendsModel();
		
		tempModel.sex = model.sex;
		tempModel.head_pic = model.head_pic;
		tempModel.relation = model.relation;
		tempModel.introduce = model.introduce;
		tempModel.identification_state = model.identification_state;
		tempModel.country = model.country;
		tempModel.nick_name = model.nick_name;
		tempModel.area = model.area;
		tempModel.school = model.school;
		tempModel.background = model.background;
		tempModel.company = model.company;
		tempModel.account = model.account;
		tempModel.user_id = model.user_id;
		tempModel.allow_add_me = model.allow_add_me;
		tempModel.mobile = model.mobile;
		tempModel.namePinYin = UTextUtils.getPingYin(model.nick_name);
		tempModel.namePinYinFirst = UTextUtils.getPinYinHeadChar(model.nick_name);
		tempModel.department = model.department;
		tempModel.enter_school_year = model.enter_school_year;
		return tempModel;
	}
	
	/**
	 * @author liananse
	 * @param context
	 * @param user_id
	 * @return
	 * 2013-8-28
	 */
	public static FriendsModel getFriendsModelByUserId(Context context, String user_id)
	{
		DDBOpenHelper mdDdbOpenHelper = DDBOpenHelper.getInstance(context);

		return mdDdbOpenHelper.selectFriendsModelFromDBByID(user_id);
	}

	/**
	 * 软件是否处于后台
	 * @author Kin
	 * @create 2013-8-7 上午10:35:25
	 * @param context
	 * @return boolean
	 */
	public static boolean isBackground(Context context)
	{

		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcesses)
		{
			if (appProcess.processName.equals(context.getPackageName()))
			{
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND)
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		return false;
	}
	
	/**
	 * 是否处于锁屏状态
	 * @author Kin
	 * @create 2013-8-7 上午10:56:07
	 * @param context
	 * @return boolean
	 */
	public static boolean inKeyguardRestrictedInputMode(Context context)
	{
		 KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);   
         
	        if (mKeyguardManager.inKeyguardRestrictedInputMode()) {  
	        	return true;
	        }  
	        else
	        {
	        	return false;
	        }
	}
	
	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}
		
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static void showNetErrorToast(Context context)
	{
		try {
			Toast.makeText(context, "网络出错！", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
