package com.medialab.lejuju.main.login;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.db.DDBOpenHelper;
import com.medialab.lejuju.fragment.FLoadingProgressBarFragment;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.http.HHttpDataLoader.HDataListener;
import com.medialab.lejuju.model.SelfUserInfoModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.views.XScrollLayout;
import com.medialab.lejuju.views.XScrollLayout.OnViewChangeListener;
import com.renn.rennsdk.RennClient;
import com.renn.rennsdk.RennClient.LoginListener;
import com.renn.rennsdk.RennExecutor.CallBack;
import com.renn.rennsdk.RennResponse;
import com.renn.rennsdk.exception.RennException;
import com.renn.rennsdk.param.GetUserParam;

public class LLoginEntryActivity extends MBaseActivity implements OnViewChangeListener, OnClickListener{

	private RennClient rennClient;
	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	private ImageView login_logo_iv;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		// 获取实例 
		rennClient = RennClient.getInstance(this);
		// 设置应用程序信息
		rennClient.init(UConstants.RENREN_APP_ID, UConstants.RENREN_APP_KEY, UConstants.RENREN_APP_SECRET_KEY);
		
		rennClient.setScope("read_user_album read_user_status"); 
		rennClient.setTokenType("bearer");
		
		setContentView(R.layout.activity_login_entry);
		initScrollView();
		initLoginContentView();
	}

	private View loginRenrenBtn;
	private View loginMobileBtn;
	private View loginRegisterBtn;
	
	private void initLoginContentView()
	{
		loginRenrenBtn = findViewById(R.id.login_renren_btn);
		loginMobileBtn = findViewById(R.id.login_mobile_btn);
		loginRegisterBtn = findViewById(R.id.login_register_btn);
		
		loginRenrenBtn.setOnClickListener(this);
		loginMobileBtn.setOnClickListener(this);
		loginRegisterBtn.setOnClickListener(this);
		
		login_logo_iv = (ImageView) findViewById(R.id.login_logo_iv);
		
		UTools.UI.fitViewByWidth(this, login_logo_iv, 378, 155, 640);
		
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, UTools.UI.fitPixels(this, 88, 640));
		
		lp.leftMargin = getResources().getDimensionPixelSize(R.dimen.padding_left_right);
		lp.rightMargin = getResources().getDimensionPixelSize(R.dimen.padding_left_right);
		loginRenrenBtn.setLayoutParams(lp);
		UTools.UI.fitViewByWidth(this, loginMobileBtn, 370, 88, 640);
		UTools.UI.fitViewByWidth(this, loginRegisterBtn, 210, 88, 640);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == loginRenrenBtn)
		{
			rennClient.setLoginListener(new LoginListener() {
				@Override
				public void onLoginSuccess() {
					// TODO Auto-generated method stub
					handler.sendMessage(handler.obtainMessage(UConstants.MESSAGE_DATA_OK));
				}

				@Override
				public void onLoginCanceled() {
					
				}
			});
			rennClient.login(this);
		}
		else if (v == loginMobileBtn)
		{
			Intent intent = new Intent();
			intent.setClass(LLoginEntryActivity.this, LLoginActivity.class);
			LLoginEntryActivity.this.startActivity(intent);
		}
		else if (v == loginRegisterBtn)
		{
			Intent intent = new Intent();
			intent.setClass(LLoginEntryActivity.this, LRegisterWithMobileActivity.class);
			
			LLoginEntryActivity.this.startActivity(intent);
		}
		
	}
	
	public Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			// TODO Auto-generated method stub
			switch (msg.what)
			{
				case UConstants.MESSAGE_DATA_OK:
					GetUserParam param3 = new GetUserParam();
	                param3.setUserId(rennClient.getUid());
	                try {
	                    rennClient.getRennService().sendAsynRequest(param3, new CallBack() {    
	                        
	                        @Override
	                        public void onSuccess(RennResponse response) {
	                        	
	                        	final FLoadingProgressBarFragment mLoadingProgressBarFragment = new FLoadingProgressBarFragment();
								FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
								
	                        	try {
									JSONObject json = response.getResponseObject();
									
									SharedPreferences sp = UTools.Storage.getSharedPreferences(LLoginEntryActivity.this, UConstants.BASE_PREFS_NAME);
									String baidu_uid = sp.getString(UConstants.BAIDU_USER_ID, "");
									String baidu_channel_id = sp.getString(UConstants.BAIDU_CHANNEL_ID, "");
									
									// params
									Map<String, String> params = new HashMap<String, String>();
									String head_pic = "";
									String sex = "1";
									String province = "";
									String city = "";
									String company = "";
									String school = "";
									String department = "";
									String year = "";
									JSONArray headArray = json.optJSONArray("avatar");
									if(headArray!=null)
									{
										for(int i=headArray.length()-1;i>=0;i--)
										{
											JSONObject tmp = headArray.getJSONObject(i);
											if(tmp!=null && tmp.getString("size").equals("LARGE"))
											{
												head_pic = tmp.getString("url");
												break;
											}
										}
									}
									
									JSONObject basicInfo = json.optJSONObject("basicInformation");
									if(basicInfo!=null)
									{
										if (basicInfo.optString("sex").equals("MALE"))
										{
											sex = "1";
										}
										else
										{
											sex = "0";
										}
										JSONObject homeTown = basicInfo.optJSONObject("homeTown");
										if(homeTown!=null)
										{
											province = homeTown.optString("province","");
											city = homeTown.optString("city","");
										}
									}
									JSONArray workArray = json.optJSONArray("work");
									if(workArray!=null)
									{
										for(int i=workArray.length()-1;i>=0;i--)
										{
											JSONObject tmp = workArray.getJSONObject(i);
											if(tmp!=null)
											{
												company = tmp.getString("name");
												break;
											}
										}
									}
									
									JSONArray educationArray = json.optJSONArray("education");
									if(educationArray!=null)
									{
										for(int i=0;i<educationArray.length();i++)
										{
											JSONObject tmp = educationArray.getJSONObject(i);
											if(tmp!=null)
											{
												school = tmp.getString("name");
												department = tmp.getString("department");
												year = tmp.getString("year");
												break;
											}
										}
									}
									
									mLoadingProgressBarFragment.show(ft, "dialog");
									
									params.put("nick_name", json.getString("name")); 
									params.put("renren_user_id", json.getLong("id")+""); 
									params.put("head_pic", head_pic); 
									params.put("sex", sex); 
									params.put("province", province);
									params.put("city", city);
									params.put("company", company);
									params.put("department", department);
									params.put("enter_school_year", year);
									String s1 = school.replaceAll("&amp;&#35;40;", "(");
									String s2 = s1.replaceAll("&amp;&#35;41;", ")");
									params.put("school", s2); 
									params.put("baidu_user_id", baidu_uid); 
									params.put("baidu_channel_id", baidu_channel_id); 
									
									mDataLoader.postData(UConstants.RENREN_LOGIN_URL, params, LLoginEntryActivity.this, new HDataListener() {
										
										@Override
										public void onFinish(String source) {
											Gson gson = new Gson();
											try {
												TempModel mTempModel = gson.fromJson(source, new TypeToken<TempModel>(){}.getType());
												
												if (mTempModel != null && mTempModel.result.equals("success"))
												{
													DDBOpenHelper mDdbOpenHelper = DDBOpenHelper.getInstance(LLoginEntryActivity.this);
													
													if(mTempModel.data != null)
													{
														mDdbOpenHelper.insertSelfUserInfoModel(mTempModel.data);
														
														// 将userid和accesstoken同时放到sharedpreferences中
														SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(LLoginEntryActivity.this, UConstants.BASE_PREFS_NAME);
														mEditor.putString(UConstants.SELF_USER_ID, String.valueOf(mTempModel.data.user_id));
														mEditor.putString(UConstants.SELF_ACCESS_TOKEN, mTempModel.data.access_token);
														mEditor.commit();
														
														if(!mTempModel.data.mobile.equals(""))
														{
															Intent intent = new Intent();
															
															UTools.activityhelper.clearAllBut(LLoginEntryActivity.this);
															
															intent.setClass(LLoginEntryActivity.this, LInputInviteCodeActivity.class);
															LLoginEntryActivity.this.startActivity(intent);
															LLoginEntryActivity.this.finish();
														}
														else
														{
															Intent intent = new Intent();
															intent.setClass(LLoginEntryActivity.this, LRegisterMobileActivity.class);
															LLoginEntryActivity.this.startActivity(intent);
															LLoginEntryActivity.this.finish();
														}
													}
													
													mLoadingProgressBarFragment.dismiss();
													// 跳转至打开通讯录页面
													
												}
												else if (mTempModel != null && mTempModel.result.equals("fail"))
												{
													mLoadingProgressBarFragment.dismiss();
													Toast toast = Toast.makeText(LLoginEntryActivity.this, mTempModel.message, Toast.LENGTH_SHORT);
													toast.setGravity(Gravity.TOP, 0, 0);
													toast.show();
												}
											} catch (JsonSyntaxException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
											
										}
										
										@Override
										public void onFail(String msg) {
											// TODO Auto-generated method stub
											mLoadingProgressBarFragment.dismiss();
										}
									});
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
	                        }
	                        
	                        @Override
	                        public void onFailed(String errorCode, String errorMessage) {
	                        	Toast toast = Toast.makeText(LLoginEntryActivity.this, "获取人人信息失败，请稍后再试！", Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.TOP, 0, 0);
								toast.show();
	                        }
	                    });
	                } catch (RennException e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                } 
					break;
			}
		}
	};
	
	
	private XScrollLayout mScrollLayout;
	private ImageView[] imgs;
	private int count;
	private int currentItem;
	private LinearLayout pointLLayout;
	
	@Override
	public void OnViewChange(int position) {
		// TODO Auto-generated method stub
		setcurrentPoint(position);
	}

	private void initScrollView()
	{
		mScrollLayout = (XScrollLayout) findViewById(R.id.ScrollLayout);
		pointLLayout = (LinearLayout) findViewById(R.id.llayout);

		count = mScrollLayout.getChildCount();
		imgs = new ImageView[count];
		for (int i = 0; i < count; i++)
		{
			imgs[i] = (ImageView) pointLLayout.getChildAt(i);
			imgs[i].setEnabled(true);
			imgs[i].setTag(i);
		}
		currentItem = 0;
		imgs[currentItem].setEnabled(false);
		mScrollLayout.SetOnViewChangeListener(this);
	}

	private void setcurrentPoint(int position)
	{
		if (position < 0 || position > count - 1 || currentItem == position)
		{
			return;
		}
		imgs[currentItem].setEnabled(true);
		imgs[position].setEnabled(false);
		currentItem = position;
	}

	/**
	 * @author liananse
	 * 2013.07.07
	 */
	class TempModel
	{
		public String result;
		public String message;
		public SelfUserInfoModel data = null;
	}
	
}
