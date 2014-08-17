package com.medialab.lejuju.main.eventdetail;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.anim;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.fragment.FLoadingProgressBarFragment;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.http.HHttpDataLoader.HDataListener;
import com.medialab.lejuju.main.eventdetail.adapter.EDInviteFriendsAdapter;
import com.medialab.lejuju.main.joinevent.JInviteFriendsActivity;
import com.medialab.lejuju.model.EventItemModel;
import com.medialab.lejuju.model.FriendsModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UDisplayWidth;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.util.UUtils;

public class EDInviteFriendsActivity extends MBaseActivity implements OnClickListener{

	private EventItemModel mEventItemModel = null;
	private EDInviteFriendsAdapter mEdInviteFriendsAdapter;
	
	private String event_id = "-1";
	public Boolean bottomInOut = true;
	private Boolean fromPush = false;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_ed_invite_friends);
		
		fromPush = getIntent().getBooleanExtra(UConstants.FROM_PUSH, false);
		
		if (fromPush)
		{
			event_id = getIntent().getStringExtra("event_id");
			loadData(event_id);
		}
		else 
		{
			mEventItemModel = (EventItemModel) getIntent().getSerializableExtra(UConstants.EVENT_DETAIL_KEY); 
			event_id = String.valueOf(mEventItemModel.event_id);
		}
		
		initHeaderBar();
		initContentView();
		getEventDetailUserData();
	}
	
	// 初始化header bar
	private View mBackView;
	private View mOkView;
	
	private ImageView mBackImageView;
	private ImageView mOkImageView;
	
	private View back_btn_right_line;
	
	private void initHeaderBar()
	{
		mBackView = findViewById(R.id.back_btn);
		mOkView = findViewById(R.id.ok_btn);
		
		mBackImageView = (ImageView) findViewById(R.id.back_btn_center);
		mOkImageView = (ImageView) findViewById(R.id.ok_btn_center);
		
		back_btn_right_line = findViewById(R.id.back_btn_right_line);
		
		UTools.UI.fitViewByWidth(this, mBackView, 130, 62, 640);
		UTools.UI.fitViewByWidth(this, mOkView, 130, 62, 640);
		
		UTools.UI.fitViewByWidth(this, mBackImageView, 18, 32, 640);
		UTools.UI.fitViewByWidth(this, mOkImageView, 40, 36, 640);
		
		mBackView.setOnClickListener(this);
		mOkView.setOnClickListener(this);
	}
	
	
	private ListView attend_friends_list;
	private void initContentView()
	{
		mEdInviteFriendsAdapter = new EDInviteFriendsAdapter(this,event_id);
		
		attend_friends_list = (ListView) findViewById(R.id.attend_friends_list);
		
		attend_friends_list.setAdapter(mEdInviteFriendsAdapter);
		
		// 逻辑上只要能进入该页面的人，都是可以继续邀请好友的，所以不做权限判断了
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mBackView)
		{
			this.finish();
		}
		else if (v == mOkView)
		{
			if (mEventItemModel != null)
			{
				Intent intent = new Intent();
				intent.setClass(this, JInviteFriendsActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(UConstants.EVENT_DETAIL_KEY, (Serializable) mEventItemModel);
				bundle.putString("from_where", "from_event_detail");
				intent.putExtras(bundle);
				startActivity(intent);
			}
		}
	}
	
	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	
	private void getEventDetailUserData()
	{
		if (!event_id.equals("-1") && !event_id.isEmpty())
		{
			Map<String, String> params = new HashMap<String, String>();
			
			params.put("head_width", String.valueOf(UDisplayWidth.getSmallHeadPicWidth(this)));
			params.put("event_id", event_id);
			mDataLoader.getData(UConstants.EVENT_DETAIL_USER_URL, params, this, new EventDetailUserListener());
		}
	}
	
	class EventDetailUserListener implements HDataListener
	{

		@Override
		public void onFinish(String source) {
			// TODO Auto-generated method stub
			Gson gson = new Gson();
			
			try {
				TempUserModel mTempModel = gson.fromJson(source, new TypeToken<TempUserModel>(){}.getType());
				
				if(mTempModel != null && mTempModel.result.equals("success"))
				{
					HashMap<String, Object> result = new HashMap<String, Object>();
					result.put("data", mTempModel.userlist);
					handleruser.sendMessage(handleruser.obtainMessage(UConstants.MESSAGE_DATA_OK, result));
				}
				else
				{
					UUtils.showNetErrorToast(EDInviteFriendsActivity.this);
					getCheckFriendsList();
				}
			} catch (JsonSyntaxException e) {
				// TODO Auto-generated catch block
				UUtils.showNetErrorToast(EDInviteFriendsActivity.this);
				getCheckFriendsList();
			}
		}

		@Override
		public void onFail(String msg) {
			// TODO Auto-generated method stub
			UUtils.showNetErrorToast(EDInviteFriendsActivity.this);
			getCheckFriendsList();
		}
		
	}
	
	public Handler handleruser = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			// TODO Auto-generated method stub
			switch (msg.what)
			{
				case UConstants.MESSAGE_DATA_OK:
				{
					updateUserData((HashMap<String, Object>) msg.obj);
					break;
				}
				case UConstants.MESSAGE_DATA_ERROR:
				{
					break;
				}
			}
		}
	};
	
	protected void updateUserData(HashMap<String, Object> result)
	{
		if (result != null)
		{
			List<FriendsModel> list = (List<FriendsModel>) result.get("data");
			
			if (list != null && list.size() > 0)
			{
				mEdInviteFriendsAdapter.addData(list);
			}
		}
		getCheckFriendsList();
	}
	
	// 获取参加活动的好友列表
	
	class TempUserModel
	{
		public String result = "";
		public String message = "";
		
		public List<FriendsModel> userlist = null;
	}
	
	
	private void getCheckFriendsList()
	{
		if (!event_id.equals("-1") && !event_id.isEmpty())
		{
			Map<String, String> params = new HashMap<String, String>();
			
			params.put("head_width", String.valueOf(UDisplayWidth.getSmallHeadPicWidth(this)));
			params.put("event_id", event_id);
			mDataLoader.getData(UConstants.GET_CHECK_FRIENDS_URL, params, this, new CheckUserListener());
		}
	}
	
	class CheckUserListener implements HDataListener
	{

		@Override
		public void onFinish(String source) {
			// TODO Auto-generated method stub
			Gson gson = new Gson();
			
			try {
				TempCheckModel mTempModel = gson.fromJson(source, new TypeToken<TempCheckModel>(){}.getType());
				
				if(mTempModel != null && mTempModel.result.equals("success"))
				{
					HashMap<String, Object> result = new HashMap<String, Object>();
					result.put("data", mTempModel.data);
					handlercheckuser.sendMessage(handlercheckuser.obtainMessage(UConstants.MESSAGE_DATA_OK, result));
				}
				else
				{
					UUtils.showNetErrorToast(EDInviteFriendsActivity.this);
				}
			} catch (JsonSyntaxException e) {
				// TODO Auto-generated catch block
				UUtils.showNetErrorToast(EDInviteFriendsActivity.this);
			}
		}

		@Override
		public void onFail(String msg) {
			// TODO Auto-generated method stub
			UUtils.showNetErrorToast(EDInviteFriendsActivity.this);
		}
		
	}
	
	public Handler handlercheckuser = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			// TODO Auto-generated method stub
			switch (msg.what)
			{
				case UConstants.MESSAGE_DATA_OK:
				{
					updateCheckUserData((HashMap<String, Object>) msg.obj);
					break;
				}
				case UConstants.MESSAGE_DATA_ERROR:
				{
					break;
				}
			}
		}
	};
	
	protected void updateCheckUserData(HashMap<String, Object> result)
	{
		if (result != null)
		{
			List<FriendsModel> list = (List<FriendsModel>) result.get("data");
			
			if (list != null && list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++) {
					list.get(i).needCheck = 1;
				}
				mEdInviteFriendsAdapter.addData(list);
			}
		}
	}
	
	class TempCheckModel
	{
		public String result = "";
		public String message = "";
		
		public List<FriendsModel> data = null;
	}
	
	// 获取活动信息，和发起人信息
	private void loadData(String eventId)
	{
		Map<String,String> params = new HashMap<String,String>();
		params.put("event_id", eventId);
		params.put("head_width", String.valueOf(UDisplayWidth.getLargeHeadPicWidth(this)));
		params.put("event_pic_width", String.valueOf(UDisplayWidth.getEventDetailPicWidth(this)));
		
		final FLoadingProgressBarFragment mLoadingProgressBarFragment = new FLoadingProgressBarFragment();
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		mLoadingProgressBarFragment.show(ft, "dialog");
		
		mDataLoader.getData(UConstants.EVENT_DETAIL_INFO_URL, params, this, new HHttpDataLoader.HDataListener()
		{
			@Override
			public void onFinish(String source)
			{
				Gson gson = new Gson();
				try {
					DetailModel temp = gson.fromJson(source, DetailModel.class);
					
					if (temp != null && temp.result.equals("success"))
					{
						if (temp.event != null)
						{
							mEventItemModel = temp.event;
							event_id = String.valueOf(mEventItemModel.event_id);
						}
					}
					else {
						UUtils.showNetErrorToast(EDInviteFriendsActivity.this);
					}
				} catch (JsonSyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					UUtils.showNetErrorToast(EDInviteFriendsActivity.this);
				}
				mLoadingProgressBarFragment.dismiss();
			}
			
			@Override
			public void onFail(String msg)
			{
				UUtils.showNetErrorToast(EDInviteFriendsActivity.this);
				mLoadingProgressBarFragment.dismiss();
			}
		});
	}
	
	class DetailModel
	{
		String result;
		String message;
		EventItemModel event;
		String invite_code;
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (!fromPush)
		{
			if (bottomInOut)
			{
				overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
			}
			else {
				
				bottomInOut = true;
			}
		}
	}
}
