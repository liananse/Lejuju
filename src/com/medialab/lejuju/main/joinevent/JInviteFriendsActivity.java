package com.medialab.lejuju.main.joinevent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.db.DDBOpenHelper;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.http.HHttpDataLoader.HDataListener;
import com.medialab.lejuju.main.joinevent.adapter.JInviteFriendsAdapter;
import com.medialab.lejuju.model.EventItemModel;
import com.medialab.lejuju.model.FriendsModel;
import com.medialab.lejuju.model.FriendsPinyinComparator;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.util.UUtils;
import com.medialab.lejuju.views.SideListBar;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class JInviteFriendsActivity extends MBaseActivity implements OnClickListener{

	private ListView mListView;
	private View mHeaderView;
	private View mContactsView;
	private View mWeixinView;
	private View mWeixinTimelineView;
	private View mSearchView;
	private EditText mSearchEditText;
	
	private JInviteFriendsAdapter mAdapter;
	
	private SideListBar indexBar;
	private WindowManager mWindowManager;
	private TextView mDialogText;
	
	private EventItemModel mEventItemModel = null;
	
	private boolean hasHeaderView = true;
	private List<FriendsModel> mFriendsModels = new ArrayList<FriendsModel>();
	private List<FriendsModel> mSearchResultList = new ArrayList<FriendsModel>();
	
	// DATALOAD
	HHttpDataLoader mDataLoader = new HHttpDataLoader();
	
	private String mFromWhere = "from_create_event";
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_invite_friends);
		mEventItemModel = (EventItemModel) getIntent().getSerializableExtra(UConstants.EVENT_DETAIL_KEY);
		
		mFromWhere = getIntent().getStringExtra("from_where");
		
		regToWx(this);
		
		initHeaderBar();
		initContentView();
	}
	
	// 初始化header bar
	private View mBackView;
	private View mOkView;
	
	private ImageView mBackImageView;
	private ImageView mOkImageView;
	
	private View mBackBtnRightLineView;
	
	private void initHeaderBar()
	{
		mBackView = findViewById(R.id.back_btn);
		mOkView = findViewById(R.id.ok_btn);
		
		mBackImageView = (ImageView) findViewById(R.id.back_btn_center);
		mOkImageView = (ImageView) findViewById(R.id.ok_btn_center);
		
		mBackBtnRightLineView = findViewById(R.id.back_btn_left_line);
		
		UTools.UI.fitViewByWidth(this, mBackView, 130, 62, 640);
		UTools.UI.fitViewByWidth(this, mOkView, 130, 62, 640);
		
		UTools.UI.fitViewByWidth(this, mBackImageView, 18, 32, 640);
		UTools.UI.fitViewByWidth(this, mOkImageView, 45, 28, 640);
		
		
		if (mFromWhere.equals("from_create_event"))
		{
			mBackView.setVisibility(View.GONE);
			mBackBtnRightLineView.setVisibility(View.GONE);
		}
		else if (mFromWhere.equals("from_event_detail"))
		{
			mBackView.setVisibility(View.VISIBLE);
			mBackBtnRightLineView.setVisibility(View.VISIBLE);
		}
		
		mBackView.setOnClickListener(this);
		mOkView.setOnClickListener(this);
	}

	private void initContentView()
	{
		mListView = (ListView) this.findViewById(R.id.content_list_view);
		mSearchView = LayoutInflater.from(this).inflate(R.layout.header_view_search, null);
		mSearchEditText = (EditText) mSearchView.findViewById(R.id.search_editText);
		
		mSearchEditText.addTextChangedListener(textWatcher);
		//
		indexBar = (SideListBar) findViewById(R.id.sideListBar);
		mDialogText = (TextView) LayoutInflater.from(this).inflate(R.layout.list_position, null);
		mDialogText.setVisibility(View.INVISIBLE);
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
						| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		mWindowManager.addView(mDialogText, lp);
		indexBar.setTextView(mDialogText);
		// header view
		mHeaderView = LayoutInflater.from(this).inflate(R.layout.header_view_sns_contacts, null);
		mContactsView = mHeaderView.findViewById(R.id.contacts_view);
		mWeixinView = mHeaderView.findViewById(R.id.weixin_view);
		mWeixinTimelineView = mHeaderView.findViewById(R.id.weixintimeline_view);
		
		mWeixinTimelineView.setOnClickListener(this);
		mContactsView.setOnClickListener(this);
		mWeixinView.setOnClickListener(this);
		// 
		mListView.addHeaderView(mSearchView);
		mListView.addHeaderView(mHeaderView);

		mAdapter = new JInviteFriendsAdapter(this);
		
		mListView.setAdapter(mAdapter);
		indexBar.setListView(mListView);
		
		DDBOpenHelper mDdbOpenHelper = DDBOpenHelper.getInstance(JInviteFriendsActivity.this);
		
		List<FriendsModel> list = mDdbOpenHelper.getFriendsModelsFromDB();
		if (list != null && list.size() > 0)
		{
			for (int i = 0; i < list.size(); i++) {
				FriendsModel temFriendsModel = new FriendsModel(list.get(i));
				mFriendsModels.add(temFriendsModel);
			}
			Collections.sort(mFriendsModels, new FriendsPinyinComparator());
			mAdapter.refreshData(mFriendsModels);
		}
	}

	TextWatcher textWatcher = new TextWatcher()
	{
		@Override
		public void afterTextChanged(Editable s)
		{
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			String newKeyword = s.toString();

			if (TextUtils.isEmpty(newKeyword))
			{
				if (!hasHeaderView)
				{
					mListView.addHeaderView(mHeaderView);
					hasHeaderView = true;
					mAdapter.refreshData(mFriendsModels);
				}
			}
			else
			{
				if (hasHeaderView)
				{
					mListView.removeHeaderView(mHeaderView);
					hasHeaderView = false;
				}
				
				mSearchResultList = searchFriends(mFriendsModels, s.toString());
				mAdapter.refreshData(mSearchResultList);
			}
		}
	};
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mWindowManager.removeView(mDialogText);
		super.onDestroy();
	}

	public List<FriendsModel> searchFriends(List<FriendsModel> list,String searchStr)
	{
		if (searchStr.trim().length() == 0)
		{
			return new ArrayList<FriendsModel>();
		}
		Vector<FriendsModel> result = new Vector<FriendsModel>();
		if (list != null && list.size() > 0)
		{
			for (FriendsModel info : list)
			{
				char i = searchStr.charAt(0);
				// 是汉字开头，只判断没有转拼音的字段就可以了
				if (java.lang.Character.toString(i).matches("[\\u4E00-\\u9FA5]+"))
				{
					if (info.nick_name.contains(searchStr.toLowerCase()))
					{
						result.add(info);
						continue;
					}
				}
				else
				{
					if (info.nick_name.toLowerCase().contains(searchStr.toLowerCase()) || info.namePinYin.toLowerCase().contains(searchStr.toLowerCase())
							|| info.namePinYinFirst.toLowerCase().contains(searchStr.toLowerCase()))
					{
						result.add(info);
						continue;
					}
				}
			}
		}
		return result;
		
	}
	private IWXAPI api;

	public void regToWx(Context context)
	{
		api = WXAPIFactory.createWXAPI(context, UConstants.WX_APP_ID);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mContactsView)
		{
			Intent intent = new Intent();
			intent.setClass(this, JInviteContactsFriendsActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(UConstants.EVENT_DETAIL_KEY, (Serializable) mEventItemModel);
			bundle.putString("invite_type", UConstants.EVENT_INVITE);
			intent.putExtras(bundle);
			this.startActivity(intent);
		}
		else if (v == mWeixinView)
		{
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = mEventItemModel.event_invite_url;
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = mEventItemModel.title;
			msg.description = mEventItemModel.wx_invite_content;
			Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.about_logo);
			msg.thumbData = UUtils.bmpToByteArray(thumb, true);
			
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("webpage");
			req.message = msg;
			api.sendReq(req);
			
		}
		else if (v == mWeixinTimelineView)
		{
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = mEventItemModel.event_invite_url;
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = mEventItemModel.title;
			msg.description = mEventItemModel.wx_invite_content;
			Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.about_logo);
			msg.thumbData = UUtils.bmpToByteArray(thumb, true);
			
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("webpage");
			req.message = msg;
			req.scene = req.WXSceneTimeline;
			api.sendReq(req);
		}
		else if (v == mOkView)
		{
			List<TempFriendModel> mTempFriendModels = new ArrayList<TempFriendModel>();
			// 发出邀请
			for (int i = 0; i < mFriendsModels.size(); i++) {
				
				if (mFriendsModels.get(i).isSelected == 1)
				{
					TempFriendModel tModel = new TempFriendModel(mFriendsModels.get(i).user_id);
					mTempFriendModels.add(tModel);
				}
			}
			
			if (mTempFriendModels.size() > 0 && mEventItemModel.event_id != -1)
			{
				Map<String, String> params = new HashMap<String, String>();
				params.put("method", "internal_friend");
				params.put("event_id", String.valueOf(mEventItemModel.event_id));
				
				String fString = "";
				for (int i = 0; i < mTempFriendModels.size(); i++) {
					if (i == 0)
					{
						fString = String.valueOf(mTempFriendModels.get(i).user_id);
					}
					fString = fString + "," + String.valueOf(mTempFriendModels.get(i).user_id);
					
				}
				params.put("friends", fString);
				
				mDataLoader.postData(UConstants.INVITE_INTERNAL_FRIENDS_URL, params, this, new HDataListener() {
					
					@Override
					public void onFinish(String source) {
						// TODO Auto-generated method stub
						Gson gson = new Gson();
						TempResultModel mTempResultModel = gson.fromJson(source, new TypeToken<TempResultModel>(){}.getType());
						
						if (mTempResultModel != null && mTempResultModel.result.equals("success"))
						{
							JInviteFriendsActivity.this.finish();
						}
						else if (mTempResultModel != null && mTempResultModel.result.equals("fail"))
						{
						}
						
					}
					
					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
					}
				});
			}
			else {
				this.finish();
			}
		}
		else if (v == mBackView)
		{
			this.finish();
		}
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	class TempModel
	{
		public List<FriendsModel> data = null;
	}
	
	class TempFriendModel
	{
		public int user_id;
		public TempFriendModel(int user_id)
		{
			this.user_id = user_id;
		}
	}
	
	class TempResultModel
	{
		public String result = "";
		public String message = "";
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (mFromWhere.equals("from_event_detail"))
		{
			overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
		}
	}

}
