package com.medialab.lejuju;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.baidu.android.pushservice.PushManager;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.medialab.lejuju.main.login.LLoginEntryActivity;
import com.medialab.lejuju.model.SelfUserInfoModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UDataCleanManager;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.util.UUtils;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * @author liananse
 * 2013.06.27
 */
public class MWelcomeActivity extends MBaseActivity implements AnimationListener{

	private ImageView login_logo_iv;
	
	LocationClient locationClient;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		setContentView(R.layout.activity_main);
		
		initView();
		
//		if(UTools.OS.isNetworkAvailable(this))
//		{
//			SharedPreferences sp = UTools.Storage.getSharedPreferences(this, UConstants.BASE_PREFS_NAME);
//			String baidu_uid = sp.getString(UConstants.BAIDU_USER_ID, "");
//			String baidu_channel_id = sp.getString(UConstants.BAIDU_CHANNEL_ID, "");
//			HHttpDataLoader mDataLoader = new HHttpDataLoader();
//			if(baidu_uid.equals("") || baidu_channel_id.equals(""))
//			{
//				//初始化百度推送参数,uid,channelid
//				PushManager.startWork(this,PushConstants.LOGIN_TYPE_API_KEY, BaiduUtils.getMetaValue(this, "api_key"));
//			}
//			else
//			{
//				Map<String,String> params = new HashMap<String,String>();
//			    params.put("baidu_user_id", baidu_uid);
//				params.put("baidu_channel_id", baidu_channel_id);
//				mDataLoader.postData(UConstants.STATIC_URL, params, this, null);
//			}
//			mDataLoader.postData(UConstants.STATIC_URL_STRING, null, this, null);
//		}
		
		locationClient = new LocationClient(this);
        //设置定位条件
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);								//是否打开GPS
        option.setCoorType("bd09ll");							//设置返回值的坐标类型。
        option.setPriority(LocationClientOption.NetWorkFirst);	//设置定位优先级
        option.setScanSpan(1000);						//设置定时定位的时间间隔。单位毫秒
        locationClient.setLocOption(option);
        locationClient.start();
        locationClient.registerLocationListener(new BDLocationListener() {
			
			@Override
			public void onReceiveLocation(BDLocation location) {
				// TODO Auto-generated method stub
				if (location == null) {
					return;
				}
				
				SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(MWelcomeActivity.this, UConstants.BASE_PREFS_NAME);
				mEditor.putString(UConstants.LOCATION_LATITUDE, String.valueOf(location.getLatitude()));
				mEditor.putString(UConstants.LOCATION_LONGITUDE, String.valueOf(location.getLongitude()));
				mEditor.commit();
				locationClient.stop();
			}
			
			@Override
			public void onReceivePoi(BDLocation location) {
			}
			
		});
	}
	
	private IWXAPI api;
	
	private void initView()
	{
		login_logo_iv = (ImageView) findViewById(R.id.login_logo_iv);
		
		UTools.UI.fitViewByWidth(this, login_logo_iv, 378, 155, 640);
		
		AlphaAnimation aa=new AlphaAnimation(0.1f,1.0f);
		aa.setDuration(1500);
		login_logo_iv.startAnimation(aa);
		aa.setAnimationListener(this);
		
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		api = WXAPIFactory.createWXAPI(this, UConstants.WX_APP_ID, false);

		api.registerApp(UConstants.WX_APP_ID);    	
	}
	

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		gotoActivity();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}
	
	private void gotoActivity()
	{
		SelfUserInfoModel mSelfUserInfoModel;
		mSelfUserInfoModel = UUtils.getSelfUserInfoModel(this);
		if (mSelfUserInfoModel == null)
		{
			Intent intent = new Intent(MWelcomeActivity.this,LLoginEntryActivity.class);
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			finish();
		}
		else
		{
			if (mSelfUserInfoModel.user_id == -1)
			{
				Intent intent = new Intent(MWelcomeActivity.this,LLoginEntryActivity.class);
				startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				finish();
			}
			else
			{
				if (mSelfUserInfoModel.mobile.equals(""))
				{
					UDataCleanManager.cleanApplicationData(MWelcomeActivity.this);
					Intent intent = new Intent();
					intent.setClass(MWelcomeActivity.this, LLoginEntryActivity.class);
					MWelcomeActivity.this.startActivity(intent);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					MWelcomeActivity.this.finish();
				}
				else {
					Intent intent = new Intent(MWelcomeActivity.this,MMainTabActivity.class);
					startActivity(intent);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					finish();
				}
			}
		}
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		PushManager.resumeWork(this);
	}
	
}
