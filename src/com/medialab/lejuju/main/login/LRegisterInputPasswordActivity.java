package com.medialab.lejuju.main.login;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.db.DDBOpenHelper;
import com.medialab.lejuju.fragment.FLoadingProgressBarFragment;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.http.HHttpDataLoader.HDataListener;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.util.UUtils;

public class LRegisterInputPasswordActivity extends MBaseActivity implements OnClickListener{

	private String mobile = "";
	private String verify_code = "";
	private String from_type = "";
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_input_password);
		
		mobile = getIntent().getStringExtra("mobile");
		verify_code = getIntent().getStringExtra("verify_code");
		from_type = getIntent().getStringExtra("from_type");
		
		initHeaderBar();
		initContentView();
	}

	// 初始化header bar
	private View mBackView;
	private ImageView mBackImageView;
	
	private void initHeaderBar()
	{
		mBackView = findViewById(R.id.back_btn);
		mBackImageView = (ImageView) findViewById(R.id.back_btn_center);
		
		UTools.UI.fitViewByWidth(this, mBackView, 130, 62, 640);
		UTools.UI.fitViewByWidth(this, mBackImageView, 18, 32, 640);
		
		mBackView.setOnClickListener(this);
	}
	
	private EditText verifycodeEditText;
	private EditText passwordEditText;
	
	private Button next_click_button;
	private Button resend_code_button;
	
	private Handler handle;
	private Timer timer;
	private void initContentView()
	{
		verifycodeEditText = (EditText) findViewById(R.id.verifycodeEditText);
		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		
		next_click_button = (Button) findViewById(R.id.next_click_button);
		resend_code_button = (Button) findViewById(R.id.resend_code_button);
		
		next_click_button.setOnClickListener(this);
		resend_code_button.setOnClickListener(this);
		
		resend_code_button.setEnabled(false);
		// 倒计时
		timer = new Timer();
		TimerTask timerTask = new TimerTask() { 
            int i = 60; 

            @Override 
            public void run() { 
                Message msg = new Message(); 
                msg.what = i--; 
                handle.sendMessage(msg); 
            } 
        }; 
        timer.schedule(timerTask, 1000, 1000);
        
        handle = new Handler(){

			@Override
			public void handleMessage(Message msg)
			{
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what>0)
				{
					resend_code_button.setText("重新发送"+"("+msg.what+")");
				}
				else
				{
					resend_code_button.setText("重新发送");
					resend_code_button.setEnabled(true);
					timer.cancel();
				}
			}
			
		};
	}
	
	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if (v == mBackView)
		{
			this.finish();
		}
		else if (v == next_click_button)
		{
			boolean can_next = true;
			
			if (can_next && verifycodeEditText.getText().toString().trim().isEmpty())
			{
				can_next = false;
				Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
			}
			
			if (can_next && !verify_code.equals(verifycodeEditText.getText().toString().trim()))
			{
				can_next = false;
				Toast.makeText(this, "验证码不正确，请重新输入", Toast.LENGTH_SHORT);
			}
			
			if (can_next && passwordEditText.getText().toString().trim().isEmpty())
			{
				can_next = false;
				Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
			}
			
			if (can_next && (passwordEditText.getText().toString().trim().length() < 6 || passwordEditText.getText().toString().trim().length() > 20
					|| !passwordEditText.getText().toString().trim().matches("^[A-Za-z0-9]+$")))
			{
				can_next = false;
				Toast.makeText(this, "密码格式不正确", Toast.LENGTH_SHORT).show();
			}
			
			if (can_next)
			{
				if (from_type.equals("register"))
				{
					Intent intent = new Intent();
					intent.setClass(this, LRegisterActivity.class);
					
					Bundle bundle = new Bundle();
					bundle.putString("mobile", mobile);
					bundle.putString("password", passwordEditText.getText().toString().trim());
					
					intent.putExtras(bundle);
					
					this.startActivity(intent);
				}
				else if (from_type.equals("renren_login"))
				{
					final FLoadingProgressBarFragment mLoadingProgressBarFragment = new FLoadingProgressBarFragment();
					FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
					mLoadingProgressBarFragment.show(ft, "dialog");
					
					Map<String, String> params = new HashMap<String, String>();
					params.put("mobile", mobile);
					params.put("password", passwordEditText.getText().toString().trim());
					
					mDataLoader.postData(UConstants.RENREN_VERIFY_MOBILE_URL, params, this, new HDataListener() {
						
						@Override
						public void onFinish(String source) {
							// TODO Auto-generated method stub
							Gson gson = new Gson();
							try {
								TempVerifyMobile mTempVerifyMobile = gson.fromJson(source, new TypeToken<TempVerifyMobile>(){}.getType());
								
								if (mTempVerifyMobile != null && mTempVerifyMobile.result.equals("success"))
								{
									DDBOpenHelper mDdbOpenHelper = DDBOpenHelper.getInstance(LRegisterInputPasswordActivity.this);
									
									mDdbOpenHelper.updateSelfUserInfoModelMobile(mobile);
									
									Toast.makeText(LRegisterInputPasswordActivity.this, mTempVerifyMobile.message, Toast.LENGTH_SHORT).show();
									
									Intent intent = new Intent();
									
									UTools.activityhelper.clearAllBut(LRegisterInputPasswordActivity.this);
									
									intent.setClass(LRegisterInputPasswordActivity.this, LInputInviteCodeActivity.class);
									LRegisterInputPasswordActivity.this.startActivity(intent);
									LRegisterInputPasswordActivity.this.finish();
								}
								else if (mTempVerifyMobile != null && mTempVerifyMobile.result.equals("fail"))
								{
									Toast.makeText(LRegisterInputPasswordActivity.this, mTempVerifyMobile.message, Toast.LENGTH_SHORT).show();
								}
								mLoadingProgressBarFragment.dismiss();
							} catch (JsonSyntaxException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								UUtils.showNetErrorToast(LRegisterInputPasswordActivity.this);
								mLoadingProgressBarFragment.dismiss();
							}
						}
						
						@Override
						public void onFail(String msg) {
							// TODO Auto-generated method stub
							UUtils.showNetErrorToast(LRegisterInputPasswordActivity.this);
							mLoadingProgressBarFragment.dismiss();
						}
					});
				}
			}
		}
		else if (v == resend_code_button)
		{
			// 倒计时
			timer = new Timer();
			TimerTask timerTask = new TimerTask() { 
	            int i = 60; 

	            @Override 
	            public void run() { 
	                Message msg = new Message(); 
	                msg.what = i--; 
	                handle.sendMessage(msg); 
	            } 
	        }; 
	        timer.schedule(timerTask, 1000, 1000);
	        
	        resend_code_button.setEnabled(false);
	        
			final FLoadingProgressBarFragment mLoadingProgressBarFragment = new FLoadingProgressBarFragment();
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			mLoadingProgressBarFragment.show(ft, "dialog");
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("mobile", mobile);
			
			mDataLoader.getData(UConstants.JUDGE_MOBILE_URL, params, this, new HDataListener() {
				
				@Override
				public void onFinish(String source) {
					// TODO Auto-generated method stub
					Gson gson = new Gson();
					try {
						TempModel mTempModel = gson.fromJson(source, new TypeToken<TempModel>(){}.getType());
						
						if (mTempModel != null && mTempModel.result.equals("success"))
						{
							verify_code = mTempModel.verify_code;
							Toast.makeText(LRegisterInputPasswordActivity.this, mTempModel.message, Toast.LENGTH_SHORT).show();
						}
						else if (mTempModel != null && mTempModel.result.equals("fail"))
						{
							Toast.makeText(LRegisterInputPasswordActivity.this, mTempModel.message, Toast.LENGTH_SHORT).show();
						}
						mLoadingProgressBarFragment.dismiss();
					} catch (JsonSyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						UUtils.showNetErrorToast(LRegisterInputPasswordActivity.this);
						mLoadingProgressBarFragment.dismiss();
					}
					
				}
				
				@Override
				public void onFail(String msg) {
					// TODO Auto-generated method stub
					UUtils.showNetErrorToast(LRegisterInputPasswordActivity.this);
					mLoadingProgressBarFragment.dismiss();
				}
			});
		}
	}
	
	/**
	 * @author liananse
	 * 2013.07.07
	 */
	class TempModel
	{
		String result = "";
		String message = "";
		String verify_code = "";
		int error_code;
	}
	
	
	class TempVerifyMobile
	{
		String result = "";
		String message = "";
	}
	
}
