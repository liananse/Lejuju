package com.medialab.lejuju.main.more;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.MMainTabActivity;
import com.medialab.lejuju.MWelcomeActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.http.HHttpDataLoader.HDataListener;
import com.medialab.lejuju.main.msgcenter.MCMsgCenterActivity;
import com.medialab.lejuju.main.userinfo.UserInfoEditSignatureActivity;
import com.medialab.lejuju.main.userinfo.UserInfoEntryActivity;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UDataCleanManager;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.util.UUtils;

public class MMoreEntryActivity extends MBaseActivity implements OnClickListener{

	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_more_entry);
		
		initContentView();
	}
	
	private View mMessageCenterView;
	private View mCleanCache;
	private View mUserInfoView;
	private Button exitBtn;
	private View about_row;
	private View feedback_row;
	
	private TextView unReadNewsTV;
	private void initContentView()
	{
		mMessageCenterView = findViewById(R.id.message_center_row);
		mCleanCache = findViewById(R.id.remove_cache);
		exitBtn = (Button) findViewById(R.id.exitBtn);
		mUserInfoView = findViewById(R.id.user_info_row);
		about_row = findViewById(R.id.about_row);
		feedback_row = findViewById(R.id.feed_back);
		unReadNewsTV = (TextView) findViewById(R.id.unread_news_count);
		
		UTools.UI.fitViewByWidth(this, unReadNewsTV, 42, 42, 640);
		
		mMessageCenterView.setOnClickListener(this);
		mCleanCache.setOnClickListener(this);
		exitBtn.setOnClickListener(this);
		mUserInfoView.setOnClickListener(this);
		about_row.setOnClickListener(this);
		feedback_row.setOnClickListener(this);
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		int un_read_news_count = UTools.Storage.getSharedPreferences(this, UConstants.BASE_PREFS_NAME).getInt(UConstants.UNREAD_NEWS_NUM, 0);
		if (un_read_news_count > 0)
		{
			unReadNewsTV.setVisibility(View.VISIBLE);
			unReadNewsTV.setText(un_read_news_count+"");
		}
		else {
			unReadNewsTV.setVisibility(View.INVISIBLE);
		}
		
		if (getParent() instanceof MMainTabActivity)
		{
			((MMainTabActivity) getParent()).updateNotify();
		}
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if (v == mMessageCenterView)
		{
			Intent intent = new Intent();
			intent.setClass(MMoreEntryActivity.this, MCMsgCenterActivity.class);
			MMoreEntryActivity.this.startActivity(intent);
		}
		else if (v == mCleanCache)
		{
			UDataCleanManager.cleanInternalCache(this);
			UDataCleanManager.cleanExternalCache(this);
			
			Toast.makeText(this, "清除缓存成功", Toast.LENGTH_SHORT).show();
		}
		else if (v == exitBtn)
		{
			showDialog(DIALOG_YES_NO_LONG_MESSAGE);
		}
		else if (v == about_row)
		{
			// 关于
			Intent intent = new Intent();
			intent.setClass(this, MAboutPyjActivity.class);
			this.startActivity(intent);
		}
		else if (v == feedback_row)
		{
			Intent intent = new Intent();
			intent.setClass(this, UserInfoEditSignatureActivity.class);
			this.startActivity(intent);
		}
		else if (v == mUserInfoView)
		{
			Intent intent = new Intent();
			intent.setClass(this, UserInfoEntryActivity.class);
			
			Bundle bundle = new Bundle();
			bundle.putSerializable(UConstants.FRIENDS_KEY, (Serializable) UUtils.selfUserInfoModelToFriendsModel(this));
			
			intent.putExtras(bundle);
			
			this.startActivity(intent);
		}
	}
	
	class TempModel{
		String result = "";
		String message = "";
	}

	private static final int DIALOG_YES_NO_LONG_MESSAGE = 1;
	
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case DIALOG_YES_NO_LONG_MESSAGE:
            return new AlertDialog.Builder(MMoreEntryActivity.this,AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("提示")
                .setMessage("确定退出朋友聚吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
    
                    	Map<String, String> params = new HashMap<String, String>();
						
						params.put("user_id", UTools.OS.getUserId(MMoreEntryActivity.this));
						
						mDataLoader.postData(UConstants.LOGOUT_URL, params, MMoreEntryActivity.this, new HDataListener() {
							
							@Override
							public void onFinish(String source) {
								// TODO Auto-generated method stub
								Gson gson = new Gson();
								
								try {
									TempModel temp = gson.fromJson(source, TempModel.class);
									
									if (temp != null && temp.result.equals("success"))
									{
										UDataCleanManager.cleanApplicationData(MMoreEntryActivity.this);
										UTools.activityhelper.clearAllBut(MMoreEntryActivity.this);
										Intent intent = new Intent();
										intent.setClass(MMoreEntryActivity.this, MWelcomeActivity.class);
										MMoreEntryActivity.this.startActivity(intent);
										
										MMoreEntryActivity.this.finish();
									}
									else {
										UUtils.showNetErrorToast(MMoreEntryActivity.this);
									}
								} catch (JsonSyntaxException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									UUtils.showNetErrorToast(MMoreEntryActivity.this);
								}
							}
							
							@Override
							public void onFail(String msg) {
								// TODO Auto-generated method stub
								UUtils.showNetErrorToast(MMoreEntryActivity.this);
							}
						});
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked Cancel so do some stuff */
                    	dialog.cancel();
                    }
                })
                .create();
		}
		
		return null;
	}
	
	
	
}
