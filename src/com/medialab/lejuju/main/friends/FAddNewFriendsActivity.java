package com.medialab.lejuju.main.friends;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.medialab.lejuju.main.userinfo.UserInfoEntryActivity;
import com.medialab.lejuju.model.FriendsModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UDisplayWidth;
import com.medialab.lejuju.util.UTools;

public class FAddNewFriendsActivity extends MBaseActivity implements OnClickListener{

	private EditText search_text;
	
	private HHttpDataLoader loader = new HHttpDataLoader();
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_friends_entry_search_friend);
		initHeaderBar();
		search_text = (EditText) findViewById(R.id.search_friends_text);
	}
	
	// 初始化header bar
	private View mBackView;
	private View mOkView;
	
	private ImageView mBackImageView;
	private ImageView mOkImageView;
	
	private void initHeaderBar()
	{
		mBackView = findViewById(R.id.back_btn);
		mOkView = findViewById(R.id.ok_btn);
		
		mBackImageView = (ImageView) findViewById(R.id.back_btn_center);
		mOkImageView = (ImageView) findViewById(R.id.ok_btn_center);
		
		UTools.UI.fitViewByWidth(this, mBackView, 130, 62, 640);
		UTools.UI.fitViewByWidth(this, mOkView, 130, 62, 640);
		
		UTools.UI.fitViewByWidth(this, mBackImageView, 18, 32, 640);
		UTools.UI.fitViewByWidth(this, mOkImageView, 45, 28, 640);
		
		mBackView.setOnClickListener(this);
		mOkView.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v)
	{
		if(v == mBackView)
		{
			this.finish();
		}
		else if(v == mOkView)
		{
			if (!search_text.getText().toString().trim().equals(""))
			{
				final FLoadingProgressBarFragment mLoadingProgressBarFragment = new FLoadingProgressBarFragment();
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				mLoadingProgressBarFragment.show(ft, "dialog");
				
				Map<String,String> params = new HashMap<String,String>();
				params.put("keyword", search_text.getText().toString());
				params.put("head_width", UDisplayWidth.getLargeHeadPicWidth(this)+"");
				loader.postData(UConstants.SEARCH_FRIENDS_URL, params, this, new HDataListener(){
	
					@Override
					public void onFinish(String source)
					{
						mLoadingProgressBarFragment.dismiss();
						Gson gson = new Gson();
						
						try {
							TempModel mTempModel = gson.fromJson(source, new TypeToken<TempModel>(){}.getType());
							
							if(mTempModel != null && mTempModel.result.equals("success"))
							{
								Intent intent = new Intent();
								intent.setClass(FAddNewFriendsActivity.this, UserInfoEntryActivity.class);
								
								Bundle bundle = new Bundle();
								bundle.putSerializable(UConstants.FRIENDS_KEY, (Serializable) mTempModel.user);
								
								intent.putExtras(bundle);
								
								FAddNewFriendsActivity.this.startActivity(intent);
							}
							else {
								Toast.makeText(FAddNewFriendsActivity.this, "搜索失败", Toast.LENGTH_LONG).show();
							}
						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(FAddNewFriendsActivity.this, "搜索失败", Toast.LENGTH_LONG).show();
							mLoadingProgressBarFragment.dismiss();
						}
					}
	
					@Override
					public void onFail(String msg)
					{
						// TODO Auto-generated method stub
						Toast.makeText(FAddNewFriendsActivity.this, "搜索失败", Toast.LENGTH_LONG).show();
						mLoadingProgressBarFragment.dismiss();
					}
					
				});
			}
			else {
				Toast.makeText(FAddNewFriendsActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	
	class TempModel
	{
		public String result = "";
		public String message = "";
		public FriendsModel user = null;
	}

}
