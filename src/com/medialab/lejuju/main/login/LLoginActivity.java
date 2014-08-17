package com.medialab.lejuju.main.login;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.medialab.lejuju.model.SelfUserInfoModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UDisplayWidth;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.util.UUtils;

public class LLoginActivity extends MBaseActivity implements OnClickListener {

	private TextView forgetpasswordTextView;
	private Button loginBtn;
	
	private EditText mCellPhoneEditText;
	private EditText mPasswordEditText;

	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	
	private String from_mobile = "";
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_login);
		from_mobile = getIntent().getStringExtra("mobile");
		
		initView();
	}

	private void initView() {
		// forget password statement textview
		forgetpasswordTextView = (TextView) this
				.findViewById(R.id.forget_password_textview);
		String forgetpassword = this.getResources().getString(
				R.string.login_activity_forgetpassword);
		forgetpasswordTextView.setText(Html.fromHtml("<u>" + forgetpassword
				+ "<u>"));
		
		forgetpasswordTextView.setOnClickListener(this);

		// login button
		loginBtn = (Button) this.findViewById(R.id.loginBtn);
		loginBtn.setOnClickListener(this);

		// Parameter EditText
		mCellPhoneEditText = (EditText) findViewById(R.id.cellPhonenumEditText);
		mCellPhoneEditText.setText(from_mobile);
		mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == forgetpasswordTextView)
		{
			if (mCellPhoneEditText.getText().toString().trim().isEmpty())
			{
				Toast toast = Toast.makeText(this, R.string.input_cellphonenum_tips, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 0, 0);
				toast.show();
			}
			else {
				final FLoadingProgressBarFragment mLoadingProgressBarFragment = new FLoadingProgressBarFragment();
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				mLoadingProgressBarFragment.show(ft, "dialog");
				Map<String, String> params = new HashMap<String, String>();
				
				params.put("mobile", mCellPhoneEditText.getText().toString().trim());
				
				mDataLoader.getData(UConstants.FIND_PASSWORD, params, this, new HDataListener() {
					
					@Override
					public void onFinish(String source) {
						// TODO Auto-generated method stub
						mLoadingProgressBarFragment.dismiss();
						Gson gson = new Gson();
						try {
							TempGetPassModel mTempGetPassModel = gson.fromJson(source, new TypeToken<TempGetPassModel>(){}.getType());
							
							Toast toast = Toast.makeText(LLoginActivity.this, mTempGetPassModel.message, Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.TOP, 0, 0);
							toast.show();
						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							
							Toast toast = Toast.makeText(LLoginActivity.this, "服务器错误", Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.TOP, 0, 0);
							toast.show();
						}
						
					}
					
					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						mLoadingProgressBarFragment.dismiss();
						Toast toast = Toast.makeText(LLoginActivity.this, "服务器错误", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.TOP, 0, 0);
						toast.show();
					}
				});
			}
			
		}
		if (v == loginBtn) {
			
			boolean canLogin = true;
			
			if (mCellPhoneEditText.getText().toString().trim().isEmpty())
			{
				canLogin = false;
				// toast
				Toast toast = Toast.makeText(this, R.string.input_cellphonenum_tips, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 0, 0);
				toast.show();
			}
			
			if (canLogin && ( mCellPhoneEditText.getText().toString().trim().length() != 11 || !mCellPhoneEditText.getText().toString().trim().startsWith("1")))
			{
				canLogin = false;
				// toast
				Toast toast = Toast.makeText(this, R.string.input_logical_cellphonenum_tips, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 0, 0);
				toast.show();
			}
			
			if (canLogin && mPasswordEditText.getText().toString().trim().isEmpty())
			{
				canLogin = false;
				// toast
				Toast toast = Toast.makeText(this, R.string.input_password_tips, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 0, 0);
				toast.show();
			}
			
			if (canLogin)
			{
				// dialog progressBar
				final FLoadingProgressBarFragment mLoadingProgressBarFragment = new FLoadingProgressBarFragment();
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				mLoadingProgressBarFragment.show(ft, "dialog");
				
				SharedPreferences sp = UTools.Storage.getSharedPreferences(LLoginActivity.this, UConstants.BASE_PREFS_NAME);
				String baidu_uid = sp.getString(UConstants.BAIDU_USER_ID, "");
				String baidu_channel_id = sp.getString(UConstants.BAIDU_CHANNEL_ID, "");
				// params
				Map<String, String> params = new HashMap<String, String>();
				
				params.put("mobile", mCellPhoneEditText.getText().toString().trim()); 
				params.put("password", mPasswordEditText.getText().toString().trim().toLowerCase());
				params.put("head_width", String.valueOf(UDisplayWidth.getLargeHeadPicWidth(LLoginActivity.this)));
				
				params.put("baidu_user_id", baidu_uid); 
				params.put("baidu_channel_id", baidu_channel_id); 
				
				mDataLoader.postData(UConstants.LLOGIN_URL, params, this, new HDataListener() {
					
					@Override
					public void onFinish(String source) {
						// TODO Auto-generated method stub
						Gson gson = new Gson();
						
						try {
							TempModel mTempModel = gson.fromJson(source, new TypeToken<TempModel>(){}.getType());
							
							if (mTempModel != null && mTempModel.result.equals("success"))
							{
								DDBOpenHelper mDdbOpenHelper = DDBOpenHelper.getInstance(LLoginActivity.this);
								
								if(mTempModel.data != null)
								{
									mDdbOpenHelper.insertSelfUserInfoModel(mTempModel.data);
									
									// 将userid和accesstoken同时放到sharedpreferences中
									SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(LLoginActivity.this, UConstants.BASE_PREFS_NAME);
									mEditor.putString(UConstants.SELF_USER_ID, String.valueOf(mTempModel.data.user_id));
									mEditor.putString(UConstants.SELF_ACCESS_TOKEN, mTempModel.data.access_token);
									mEditor.commit();
								}
								
								// dismiss the progressbar
								mLoadingProgressBarFragment.dismiss();
								
								// 跳转至输入邀请码页面
								Intent intent = new Intent();
								UTools.activityhelper.clearAllBut(LLoginActivity.this);
								
								intent.setClass(LLoginActivity.this, LInputInviteCodeActivity.class);
								LLoginActivity.this.startActivity(intent);
								LLoginActivity.this.finish();
							}
							else if (mTempModel != null && mTempModel.result.equals("fail"))
							{
								// dismiss the progressbar
								mLoadingProgressBarFragment.dismiss();
								
								Toast toast = Toast.makeText(LLoginActivity.this, mTempModel.message, Toast.LENGTH_SHORT);
								toast.show();
							}
						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							UUtils.showNetErrorToast(LLoginActivity.this);
						}
						
					}
					
					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						mLoadingProgressBarFragment.dismiss();
					}
				});
				
			}
		} 
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) {
		
		case RESULT_OK:
			Intent intent = new Intent();
			intent.setClass(LLoginActivity.this, LOpenContactsActivity.class);
			LLoginActivity.this.startActivity(intent);
			LLoginActivity.this.finish();
			break;

		default:
			break;
		}
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
		public int error_code;
	}

	class TempGetPassModel
	{
		public String result;
		public String message;
		public int error_code;
	}
}
