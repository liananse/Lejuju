package com.medialab.lejuju.main.login;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.http.HHttpDataLoader.HDataListener;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTools;

public class LInputInviteCodeActivity extends MBaseActivity implements OnClickListener{

	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_input_verify_code);
		initHeaderBar();
		initContentView();
	}
	
	// 初始化header bar
	private View mOkView;
	
	private void initHeaderBar()
	{
		mOkView = findViewById(R.id.ok_btn);
		
		UTools.UI.fitViewByWidth(this, mOkView, 130, 62, 640);
		
		mOkView.setOnClickListener(this);
	}

	private Button next_click_button;
	private EditText invite_code_et;
	private void initContentView()
	{
		invite_code_et = (EditText) findViewById(R.id.invite_code_et);
		next_click_button = (Button) findViewById(R.id.next_click_button);
		next_click_button.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mOkView)
		{
			Intent intent = new Intent();
			intent.setClass(this, LOpenContactsActivity.class);
			this.startActivity(intent);
			this.finish();
		}
		else if (v == next_click_button)
		{
			if (invite_code_et.getText().toString().trim().isEmpty())
			{
				Toast.makeText(this, "请输入邀请码，或者点击跳过", Toast.LENGTH_SHORT).show();
			}
			else {
				Map<String, String> params = new HashMap<String, String>();
				params.put("invite_code", invite_code_et.getText().toString().trim());
				
				mDataLoader.postData(UConstants.SUBMIT_INVITE_CODE, params, this, new HDataListener() {
					
					@Override
					public void onFinish(String source) {
						// TODO Auto-generated method stub
						// 暂时先不解析，因为不管提交成功与否都要跳转到开启通讯录页面
//						Gson gson = new Gson();
//						
//						TempModel mTempModel = gson.fromJson(source, new TypeToken<TempModel>(){}.getType());
//						if (mTempModel != null && mTempModel.result.equals("success"))
//						{
//						}
//						else {
//						}
						
						Intent intent = new Intent();
						intent.setClass(LInputInviteCodeActivity.this, LOpenContactsActivity.class);
						LInputInviteCodeActivity.this.startActivity(intent);
						LInputInviteCodeActivity.this.finish();
					}
					
					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.setClass(LInputInviteCodeActivity.this, LOpenContactsActivity.class);
						LInputInviteCodeActivity.this.startActivity(intent);
						LInputInviteCodeActivity.this.finish();
					}
				});
			}
		}
	}

	class TempModel
	{
		public String result;
		public String message;
	}
}
