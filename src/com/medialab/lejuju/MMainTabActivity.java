package com.medialab.lejuju;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TabHost;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.lejuju.db.DDBOpenHelper;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.http.HHttpDataLoader.HDataListener;
import com.medialab.lejuju.main.events.EJoinEventEntryActivity;
import com.medialab.lejuju.main.friends.FFriendsEntryActivity;
import com.medialab.lejuju.main.joinevent.JSelectEventTypeActivity;
import com.medialab.lejuju.main.more.MMoreEntryActivity;
import com.medialab.lejuju.main.trends.TTrendsEntryActivity;
import com.medialab.lejuju.model.FriendsModel;
import com.medialab.lejuju.model.FriendsPinyinComparator;
import com.medialab.lejuju.util.BaiduUtils;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UDisplayWidth;
import com.medialab.lejuju.util.UTools;
import com.umeng.analytics.MobclickAgent;

/**
 * @author liananse
 * 2013.06.27
 */
public class MMainTabActivity extends TabActivity implements OnClickListener{

	public TabHost tabHost;
	public RadioGroup radioGroup;

	public static final String TAB_JOIN_EVENT = "TabJoinEvent";
	public static final String TAB_PAST_EVENT = "TabPastEvent";
	public static final String TAB_EVENT_TYPE = "TabEventType";
	public static final String TAB_FRIENDS = "TabFriends";
	public static final String TAB_MORE = "TabMore";
	
	public ImageView new_friends_tv;
	public ImageView unread_news_num_tv;
	// 获取好友数据
	HHttpDataLoader mDataLoader = new HHttpDataLoader();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main_tab);
		
		new_friends_tv = (ImageView) findViewById(R.id.main_tab_friends);
		unread_news_num_tv = (ImageView) findViewById(R.id.main_tab_more);
		
		main_tab_bg_view = (RelativeLayout) findViewById(R.id.main_tab_bg_view);
		
		tabHost = this.getTabHost();

		tabHost.addTab(tabHost
				.newTabSpec(TAB_JOIN_EVENT)
				.setIndicator(TAB_JOIN_EVENT)
				.setContent(
						new Intent(MMainTabActivity.this, EJoinEventEntryActivity.class)));
		tabHost.addTab(tabHost
				.newTabSpec(TAB_PAST_EVENT)
				.setIndicator(TAB_PAST_EVENT)
				.setContent(
						new Intent(MMainTabActivity.this, TTrendsEntryActivity.class)));
		tabHost.addTab(tabHost
				.newTabSpec(TAB_EVENT_TYPE)
				.setIndicator(TAB_EVENT_TYPE)
				.setContent(
						new Intent(MMainTabActivity.this, JSelectEventTypeActivity.class)));
		tabHost.addTab(tabHost
				.newTabSpec(TAB_FRIENDS)
				.setIndicator(TAB_FRIENDS)
				.setContent(
						new Intent(MMainTabActivity.this, FFriendsEntryActivity.class)));
		tabHost.addTab(tabHost
				.newTabSpec(TAB_MORE)
				.setIndicator(TAB_MORE)
				.setContent(
						new Intent(MMainTabActivity.this, MMoreEntryActivity.class)));

		this.radioGroup = (RadioGroup) this.findViewById(R.id.main_tab_radio);

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.radio_join_event:
					tabHost.setCurrentTabByTag(TAB_JOIN_EVENT);
					break;
				case R.id.radio_past_event:
					tabHost.setCurrentTabByTag(TAB_PAST_EVENT);
					break;
				case R.id.radio_event_type:
					tabHost.setCurrentTabByTag(TAB_EVENT_TYPE);
					break;
				case R.id.radio_friends:
					tabHost.setCurrentTabByTag(TAB_FRIENDS);
					break;
				case R.id.radio_more:
					tabHost.setCurrentTabByTag(TAB_MORE);
					break;
				}
			}
		});
		
		initView();
		getUnReadNewsCount();
		
		if(UTools.OS.isNetworkAvailable(this))
		{
			SharedPreferences sp = UTools.Storage.getSharedPreferences(this, UConstants.BASE_PREFS_NAME);
			String baidu_uid = sp.getString(UConstants.BAIDU_USER_ID, "");
			String baidu_channel_id = sp.getString(UConstants.BAIDU_CHANNEL_ID, "");
			HHttpDataLoader mDataLoader = new HHttpDataLoader();
			if(baidu_uid.equals("") || baidu_channel_id.equals(""))
			{
				//初始化百度推送参数,uid,channelid
				PushManager.startWork(this,PushConstants.LOGIN_TYPE_API_KEY, BaiduUtils.getMetaValue(this, "api_key"));
			}
			else
			{
				Map<String,String> params = new HashMap<String,String>();
			    params.put("baidu_user_id", baidu_uid);
				params.put("baidu_channel_id", baidu_channel_id);
				mDataLoader.postData(UConstants.STATIC_URL, params, this, null);
			}
			mDataLoader.postData(UConstants.STATIC_URL_STRING, null, this, null);
		}
		
		MobclickAgent.onEvent(this, "maintab");
	}
	
	private void initView()
	{
		getData();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}
	
	private void getData()
	{
		Map<String, String> params = new HashMap<String, String>();
		
		params.put("head_width", String.valueOf(UDisplayWidth.getLargeHeadPicWidth(this)));
		mDataLoader.getData(UConstants.GET_FRIENDS_LIST_URL, params, this, new GetFriendsDataListener());
	}
	
	class GetFriendsDataListener implements HDataListener
	{
		@Override
		public void onFinish(String source) {
			// TODO Auto-generated method stub
			Gson gson = new Gson();
			
			TempModel mTempModel = gson.fromJson(source, new TypeToken<TempModel>(){}.getType());
			
			if(mTempModel != null)
			{	
				HashMap<String, Object> result = new HashMap<String, Object>();
				result.put("data", mTempModel.data);
				handler.sendMessage(handler.obtainMessage(UConstants.MESSAGE_DATA_OK, result));
			}
		}

		@Override
		public void onFail(String msg) {
			// TODO Auto-generated method stub
			
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
				{
					updateData((HashMap<String, Object>) msg.obj);
					break;
				}
				case UConstants.MESSAGE_DATA_ERROR:
				{
					break;
				}
			}
		}
	};
	
	protected void updateData(HashMap<String, Object> result)
	{
		if (result != null)
		{
			List<FriendsModel> list = (List<FriendsModel>) result.get("data");
			if (list != null && list.size() > 0)
			{
				List<FriendsModel> mList = new ArrayList<FriendsModel>();
				for (int i = 0; i < list.size(); i++) {
					FriendsModel temFriendsModel = new FriendsModel(list.get(i));
					mList.add(temFriendsModel);
				}
				Collections.sort(mList, new FriendsPinyinComparator());
				
				DDBOpenHelper mDdbOpenHelper = DDBOpenHelper.getInstance(MMainTabActivity.this);
				// 从数据库获取本地好友信息
				List<FriendsModel> mLocalFriendsModels = mDdbOpenHelper.getFriendsModelsFromDB();
				// 将相同的信息剔除掉，只保留未添加的
				mList.removeAll(mLocalFriendsModels);
				
				// 将未添加的加入数据库
				mDdbOpenHelper.insertFriendsInfoModel(mList);
				
				SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(MMainTabActivity.this, UConstants.BASE_PREFS_NAME);
				mEditor.putBoolean(UConstants.FIRST_LOADING_FRIENDS, false);
				mEditor.commit();
			}
		}
	}
	
	class TempModel
	{
		public List<FriendsModel> data = null;
	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		updateNotify();
		
	}
	
	public void updateNotify()
	{
		if (UTools.Storage.getSharedPreferences(this, UConstants.BASE_PREFS_NAME).getInt(UConstants.NEW_FRIENDS_NUM, 0) > 0)
		{
			new_friends_tv.setVisibility(View.VISIBLE);
		}
		else {
			new_friends_tv.setVisibility(View.INVISIBLE);
		}
		
		if (UTools.Storage.getSharedPreferences(this, UConstants.BASE_PREFS_NAME).getInt(UConstants.UNREAD_NEWS_NUM, 0) > 0)
		{
			unread_news_num_tv.setVisibility(View.VISIBLE);
		}
		else {
			unread_news_num_tv.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
	
	private RelativeLayout main_tab_bg_view;
	public void animationMoveMainTab()
	{
		// 从底部消失动画
		Animation bottomOutAnimation = new TranslateAnimation(0F,0F,0F,150);
		bottomOutAnimation.setFillAfter(true);
		bottomOutAnimation.setDuration(400);
		
		main_tab_bg_view.startAnimation(bottomOutAnimation);
	}
	
	public void animationBackMainTab()
	{
		// 从底部消失动画
		Animation bottomOutAnimation = new TranslateAnimation(0F,0F,150,0F);
		bottomOutAnimation.setFillAfter(true);
		bottomOutAnimation.setDuration(350);
		
		main_tab_bg_view.startAnimation(bottomOutAnimation);
	}

	// need improve 这个地方先去掉，后面想到更好的更新同步逻辑再改过来
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			
//			Intent intent = new Intent(Intent.ACTION_MAIN);  
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意  
//            intent.addCategory(Intent.CATEGORY_HOME);  
//            startActivity(intent);  
			
			showDialog(DIALOG_YES_NO_LONG_MESSAGE);
			return false;
		}
		else
		{
			return super.dispatchKeyEvent(event);
		}
	}
	
	
	private static final int DIALOG_YES_NO_LONG_MESSAGE = 1;
	
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case DIALOG_YES_NO_LONG_MESSAGE:
            return new AlertDialog.Builder(MMainTabActivity.this,AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("提示")
                .setMessage("确定退出朋友聚吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
    
                        /* User clicked OK so do some stuff */
                    	finish();
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
	
	private void getUnReadNewsCount()
	{
		HHttpDataLoader mDataLoader = new HHttpDataLoader();
		mDataLoader.getData(UConstants.UNREAD_UPDATE_URL, null, this, new HDataListener() {
			
			@Override
			public void onFinish(String source) {
				// TODO Auto-generated method stub
				Gson gson = new Gson();
				
				try {
					TempNumModel mTempModel = gson.fromJson(source, new TypeToken<TempNumModel>(){}.getType());
					
					if(mTempModel != null)
					{	
						SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(MMainTabActivity.this, UConstants.BASE_PREFS_NAME);
						mEditor.putInt(UConstants.NEW_FRIENDS_NUM, mTempModel.data.new_friend_num);
						mEditor.putInt(UConstants.UNREAD_NEWS_NUM, mTempModel.data.user_news_num);
						mEditor.commit();
					}
				} catch (JsonSyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFail(String msg) {
				// TODO Auto-generated method stub
			}
		});
		
	}
	
	class TempNumModel
	{
		String result = "";
		String message = "";
		TempDataModel data = null;
	}
	
	class TempDataModel
	{
		int new_friend_num = 0;
		int user_news_num = 0;
	}
}
