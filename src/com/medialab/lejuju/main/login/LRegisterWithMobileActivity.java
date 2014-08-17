package com.medialab.lejuju.main.login;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
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
import com.medialab.lejuju.fragment.FLoadingProgressBarFragment;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.http.HHttpDataLoader.HDataListener;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.util.UUtils;

/**
 * 当用户点击通过手机注册是跳到该界面
 * @author liananse
 *
 */
public class LRegisterWithMobileActivity extends MBaseActivity implements OnClickListener{

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_register_with_mobile);
		
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
	
	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	
	private Button next_click_button;
	private EditText registerCellPhoneEditText;
	private void initContentView()
	{
		registerCellPhoneEditText = (EditText) findViewById(R.id.registerCellPhoneEditText);
		
		next_click_button = (Button) findViewById(R.id.next_click_button);
		next_click_button.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mBackView)
		{
			this.finish();
		}
		else if (v == next_click_button)
		{
			if (registerCellPhoneEditText.getText().toString().trim().isEmpty())
			{
				Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
			}
			else if(registerCellPhoneEditText.getText().toString().trim().length()<11)
			{
				Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_LONG).show();
				return;
			}
			else if(!registerCellPhoneEditText.getText().toString().trim().matches("[0-9]+"))
			{
				Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_LONG).show();
				return;
			}
			else
			{
				final FLoadingProgressBarFragment mLoadingProgressBarFragment = new FLoadingProgressBarFragment();
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				mLoadingProgressBarFragment.show(ft, "dialog");
				
				Map<String, String> params = new HashMap<String, String>();
				params.put("mobile", registerCellPhoneEditText.getText().toString().trim());
				
				mDataLoader.getData(UConstants.JUDGE_MOBILE_URL, params, this, new HDataListener() {
					
					@Override
					public void onFinish(String source) {
						// TODO Auto-generated method stub
						Gson gson = new Gson();
						try {
							TempModel mTempModel = gson.fromJson(source, new TypeToken<TempModel>(){}.getType());
							
							if (mTempModel != null && mTempModel.result.equals("success"))
							{
								Intent intent = new Intent();
								intent.setClass(LRegisterWithMobileActivity.this, LRegisterInputPasswordActivity.class);
								
								Bundle bundle = new Bundle();
								bundle.putString("mobile", registerCellPhoneEditText.getText().toString().trim());
								bundle.putString("verify_code", mTempModel.verify_code);
								bundle.putString("from_type", "register");
								intent.putExtras(bundle);
								
								LRegisterWithMobileActivity.this.startActivity(intent);
							}
							else if (mTempModel != null && mTempModel.result.equals("fail"))
							{
								Toast.makeText(LRegisterWithMobileActivity.this, mTempModel.message, Toast.LENGTH_SHORT).show();
							}
							mLoadingProgressBarFragment.dismiss();
						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							UUtils.showNetErrorToast(LRegisterWithMobileActivity.this);
							mLoadingProgressBarFragment.dismiss();
						}
						
					}
					
					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						UUtils.showNetErrorToast(LRegisterWithMobileActivity.this);
						mLoadingProgressBarFragment.dismiss();
					}
				});
			}
		}
	}
	
	class TempModel
	{
		String result = "";
		String message = "";
		String verify_code = "";
		int error_code;
	}
}
