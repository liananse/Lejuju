package com.medialab.lejuju.main.userinfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.tsz.afinal.FinalBitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.http.HHttpDataLoader.HDataListener;
import com.medialab.lejuju.main.myevent.adapter.MPastEventAdapter;
import com.medialab.lejuju.model.EventItemModel;
import com.medialab.lejuju.model.FriendsModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UDisplayWidth;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.views.PastEventPopMenu;
import com.medialab.lejuju.views.RoundImageView;
import com.medialab.lejuju.views.XListView;
import com.medialab.lejuju.views.XListView.IXListViewListener;

public class UserInfoPastEventActivity extends MBaseActivity implements IXListViewListener{

	private FriendsModel mFriendsModel;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_user_info_past_event);
		mFriendsModel = (FriendsModel) getIntent().getSerializableExtra(UConstants.FRIENDS_KEY);
		initHeaderBar();
		initView();
	}
	
	private TextView user_info_past_event_title;
	private View mBackView;
	private ImageView mBackImageView;
	
	private void initHeaderBar()
	{
		mBackView = findViewById(R.id.back_btn);
		
		mBackImageView = (ImageView) findViewById(R.id.back_btn_center);
		
		user_info_past_event_title = (TextView) findViewById(R.id.user_info_past_event_title);
		
		user_info_past_event_title.setText(mFriendsModel.nick_name);
		UTools.UI.fitViewByWidth(this, mBackView, 130, 62, 640);
		
		UTools.UI.fitViewByWidth(this, mBackImageView, 18, 32, 640);
		
		mBackView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UserInfoPastEventActivity.this.finish();
			}
		});
	}
	
	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	private XListView mXListView;
	private MPastEventAdapter mPastEventAdapter;
	
	protected boolean hasNextPage;
	protected int last_id = 0;
	
	private View mHeaderView;
	private ImageView mHeaderPostView;
	private View mHeaderPicBgView;
	private RoundImageView mHeaderPicView;
	private TextView mHeaderNickNameTextView;
	
	private PastEventPopMenu mPastEventPopMenu;
	private Random mRandom = new Random();
	
	FinalBitmap fb;
	private void initView()
	{
		mPastEventAdapter = new MPastEventAdapter(this);
		mPastEventAdapter.setOnCommentClickListener(onCommentClickListener);
		mPastEventPopMenu = new PastEventPopMenu(this);
		
		fb = FinalBitmap.create(this);
		mXListView = (XListView) findViewById(R.id.event_list_view);
		// header view
		mHeaderView = LayoutInflater.from(this).inflate(R.layout.header_view_past_event, null);
		mHeaderPicView = (RoundImageView) mHeaderView.findViewById(R.id.past_event_header_pic);
		mHeaderNickNameTextView = (TextView) mHeaderView.findViewById(R.id.item_event_nick_name);
		mHeaderPostView = (ImageView) mHeaderView.findViewById(R.id.past_event_header_poster);
		mHeaderPicBgView = mHeaderView.findViewById(R.id.item_event_head_pic_bg);
		UTools.UI.fitViewByWidth(this, mHeaderPicBgView, 150, 150, 640);
		UTools.UI.fitViewByWidth(this, mHeaderPicView, 140, 140, 640);
		
		int posterId = mRandom.nextInt(3);
		if (posterId == 0)
		{
			mHeaderPostView.setImageResource(R.drawable.past_event_poster1);
		}
		else if (posterId == 1)
		{
			mHeaderPostView.setImageResource(R.drawable.past_event_poster2);
		}
		else {
			mHeaderPostView.setImageResource(R.drawable.past_event_poster3);
		}
		
		mHeaderView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// do nothing
			}
		});
		mXListView.addHeaderView(mHeaderView);
		
		mXListView.setPullRefreshEnable(true);
		mXListView.setPullLoadEnable(true);
		mXListView.setXListViewListener(this);
		
		mXListView.setAdapter(mPastEventAdapter);
		if (mFriendsModel != null)
		{
			fb.display(mHeaderPicView, mFriendsModel.head_pic);
			mHeaderNickNameTextView.setText(mFriendsModel.nick_name);
		}
		getData(0, true);
		
	}
	
	private void getData(int last_id, boolean isRefresh)
	{
		if (mFriendsModel != null)
		{
			Map<String, String> params = new HashMap<String, String>();
			
			params.put("user_id", String.valueOf(mFriendsModel.user_id));
			params.put("width", String.valueOf(UDisplayWidth.getPastEventPicWidth(this)));
			params.put("last_id", String.valueOf(last_id));
			params.put("page_size", "15");
			
			mDataLoader.getData(UConstants.MY_EVENT_PAST_EVENT_URL, params, this, new EFriendsDataListener(isRefresh));
		}
	}
	
	class EFriendsDataListener implements HDataListener
	{

		boolean isRefresh;

		public EFriendsDataListener(boolean isRefresh)
		{
			this.isRefresh = isRefresh;
		}
		
		@Override
		public void onFinish(String source) {
			// TODO Auto-generated method stub
			Gson gson = new Gson();
			try {
				TempModel mTempModel = gson.fromJson(source, new TypeToken<TempModel>(){}.getType());
				
				if(mTempModel != null && mTempModel.result.equals("success"))
				{
					HashMap<String, Object> result = new HashMap<String, Object>();
					result.put("data", mTempModel.data);
					result.put("isRefresh", isRefresh);
					result.put("last_id", mTempModel.last_id);
					result.put("hasNextPage", mTempModel.have_next_page);
					handler.sendMessage(handler.obtainMessage(UConstants.MESSAGE_DATA_OK, result));
				}
				else {
					onLoad();
				}
			} catch (JsonSyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast toast = Toast.makeText(UserInfoPastEventActivity.this, "获取好友活动信息出错！", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 0, 0);
				toast.show();
				onLoad();
			}
		}

		@Override
		public void onFail(String msg) {
			// TODO Auto-generated method stub
			onLoad();
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
					if (hasNextPage)
					{
						mXListView.setPullLoadEnable(true);
					}
					else
					{
						mXListView.setPullLoadEnable(false);
					}
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
			List<EventItemModel> list = (List<EventItemModel>) result.get("data");
			Boolean isRefresh = (Boolean) result.get("isRefresh");
			Boolean hasNextPage = (Boolean) result.get("hasNextPage");
			int lastId = (Integer) result.get("last_id");
			if (list != null && list.size() > 0)
			{
				if (isRefresh)
				{
					mPastEventAdapter.refreshData(list);
				}
				else
				{
					mPastEventAdapter.addData(list);
				}
			}
			this.last_id = lastId;
			this.hasNextPage = hasNextPage;
			onLoad();
		}
	}
	
	class TempModel
	{
		public String result = "";
		public String message = "";
		public int last_id = 0;
		public boolean have_next_page = false;
		
		public List<EventItemModel> data = null;
	}


	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
			getData(0, true);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		if (hasNextPage)
		{
			getData(last_id, false);
		}
		else
		{
			onLoad();
		}
	}
	
	OnClickListener onCommentClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			EventItemModel mEventItemModel = (EventItemModel) v.getTag();
			mPastEventPopMenu.setEventItemModel(mEventItemModel);
			mPastEventPopMenu.show(v);
			// item click
		}
	};
	
	private void onLoad()
	{
		mXListView.stopLoadMore();
		mXListView.stopRefresh();
	}


}
