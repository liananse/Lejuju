package com.medialab.lejuju.main.trends;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.db.DDBOpenHelper;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.http.HHttpDataLoader.HDataListener;
import com.medialab.lejuju.main.trends.adapter.TTrendsEventAdapter;
import com.medialab.lejuju.model.EventItemModel;
import com.medialab.lejuju.model.TrendItemModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UDisplayWidth;
import com.medialab.lejuju.util.UTimeShown;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.views.XListView;
import com.medialab.lejuju.views.XListView.IXListViewListener;

public class TTrendsEntryActivity extends MBaseActivity implements IXListViewListener{

	// DATALOAD
	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	// ADAPTER
	private TTrendsEventAdapter mAttendEventAdapter;
	
	public View header_barView;
	
	public float item_top = 0;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_trends_entry);
		mAttendEventAdapter = new TTrendsEventAdapter(this);
		
		header_barView = findViewById(R.id.header_bar);
		
		registerReceiver(newCommentReceiver, new IntentFilter("com.medialab.lejuju.newtrends"));
		initContentView();
		
		mXListView.setRefreshState();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getData();
	}

	// init content view
	private XListView mXListView;
	private View mFooterView;
	private TextView mH1TextView;
	private TextView mH2TextView;
	private void initContentView()
	{
		mXListView = (XListView) findViewById(R.id.event_list_view);
		
        
		mXListView.setAdapter(mAttendEventAdapter);
		
		mXListView.setPullRefreshEnable(true);
		mXListView.setPullLoadEnable(false);
		mXListView.setXListViewListener(this);
		
		mFooterView = LayoutInflater.from(this).inflate(R.layout.comman_footer_view, null);
		mH1TextView = (TextView) mFooterView.findViewById(R.id.h1_tv);
		mH2TextView = (TextView) mFooterView.findViewById(R.id.h2_tv);
		
		mH1TextView.setVisibility(View.GONE);
		mH2TextView.setVisibility(View.VISIBLE);
		mXListView.addFooterView(mFooterView);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		unregisterReceiver(newCommentReceiver);
	}
	
	// 用来及时刷新评论的
	BroadcastReceiver newCommentReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			if (action.equals("com.medialab.lejuju.newtrends"))
			{
				DDBOpenHelper mDdbOpenHelper = DDBOpenHelper.getInstance(TTrendsEntryActivity.this);
				// 从数据库返回需要显示的list
				List<EventItemModel> displayList = mDdbOpenHelper.getAttendEventHasTrendsModels();
				
				mAttendEventAdapter.refreshData(displayList);
			}
		}
	};
	
	private void getData()
	{
		int version = UTools.Storage.getSharedPreferences(this, UConstants.BASE_PREFS_NAME).getInt(UConstants.LOCAL_ATTEND_EVENT_VERSION, 0);
		
		Map<String, String> params = new HashMap<String, String>();
		
		params.put("head_width", String.valueOf(UDisplayWidth.getSmallHeadPicWidth(this)));
		params.put("event_pic_width", String.valueOf(UDisplayWidth.getPosterPicWidth(this)));
		params.put("version", String.valueOf(version));
		
		mDataLoader.getData(UConstants.MY_EVENT_ATTEND_EVENT_URL, params, this, new EFriendsDataListener());
	}
	
	class EFriendsDataListener implements HDataListener
	{
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
					
					SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(TTrendsEntryActivity.this, UConstants.BASE_PREFS_NAME);
					mEditor.putInt(UConstants.LOCAL_ATTEND_EVENT_VERSION, mTempModel.version);
					mEditor.commit();
					
					handler.sendMessage(handler.obtainMessage(UConstants.MESSAGE_DATA_OK, result));
				}
				else
				{
					DDBOpenHelper mDdbOpenHelper = DDBOpenHelper.getInstance(TTrendsEntryActivity.this);
					// 从数据库返回需要显示的list
					List<EventItemModel> displayList = mDdbOpenHelper.getAttendEventHasTrendsModels();
					
					mAttendEventAdapter.refreshData(displayList);
					onLoad();
				}
			} catch (JsonSyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				DDBOpenHelper mDdbOpenHelper = DDBOpenHelper.getInstance(TTrendsEntryActivity.this);
				// 从数据库返回需要显示的list
				List<EventItemModel> displayList = mDdbOpenHelper.getAttendEventHasTrendsModels();
				
				mAttendEventAdapter.refreshData(displayList);
				onLoad();
			}
		}

		@Override
		public void onFail(String msg) {
			// TODO Auto-generated method stub
			DDBOpenHelper mDdbOpenHelper = DDBOpenHelper.getInstance(TTrendsEntryActivity.this);
			// 从数据库返回需要显示的list
			List<EventItemModel> displayList = mDdbOpenHelper.getAttendEventHasTrendsModels();
			
			mAttendEventAdapter.refreshData(displayList);
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
			DDBOpenHelper mDdbOpenHelper = DDBOpenHelper.getInstance(this);
			List<EventItemModel> list = (List<EventItemModel>) result.get("data");
			if (list != null && list.size() > 0)
			{
				// 将动态统一放入动态list插入数据库
				// step1 声明需要插入数据库的
				List<TrendItemModel> mTrendItemModels = new ArrayList<TrendItemModel>();
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).trends != null && list.get(i).trends.size() > 0)
					{
						mTrendItemModels.addAll(list.get(i).trends);
					}
				}
				
				if (mTrendItemModels != null && mTrendItemModels.size() > 0)
				{
					for (int i = 0; i < mTrendItemModels.size(); i++) {
						
						TrendItemModel mTopTrendItemModel = mDdbOpenHelper.getTrendsMaxTimeByEventID(mTrendItemModels.get(i).event_id);
						
						if (mTopTrendItemModel != null)
						{
							if (UTimeShown.isLargeThanThressMinute(mTrendItemModels.get(i).add_time, mTopTrendItemModel.add_time))
							{
								mTrendItemModels.get(i).show_time = 1;
							}
							else 
							{
								mTrendItemModels.get(i).show_time = 0;
							}
						}
						
						mDdbOpenHelper.insertTrendsModel(mTrendItemModels.get(i));
					}
				}
				
				// 将活动插入数据库
				// step1 从数据库中获取本地存储的活动
				List<EventItemModel> listFromDB = mDdbOpenHelper.getAttendEventModels();
				// step2声明共同活动的list
				List<EventItemModel> comList = new ArrayList<EventItemModel>();
				// setp3找出共同的活动
				for (int j = 0; j < list.size(); j++) {
					
					for (int i = 0; i < listFromDB.size(); i++) {
						
						if (list.get(j).event_id == listFromDB.get(i).event_id)
						{
							list.get(j).event_trends_num = list.get(j).event_trends_num + listFromDB.get(i).event_trends_num;
							list.get(j).un_read_pic_num = (list.get(j).pic_num - listFromDB.get(i).pic_num) + listFromDB.get(i).un_read_pic_num;
							comList.add(list.get(j));
							break;
						}
					}
				}
				// step4将共同的活动从list中剔除
				list.removeAll(comList);
				mDdbOpenHelper.updateAttendEventModels(comList);
				// step5将本地数据库没有的list插入数据库
				mDdbOpenHelper.insertAttendEventModel(list);
				
			}
			// 从数据库返回需要显示的list
			List<EventItemModel> displayList = mDdbOpenHelper.getAttendEventHasTrendsModels();
			
			mAttendEventAdapter.refreshData(displayList);
		}
		onLoad();
	}
	
	class TempModel
	{
		public String result = "";
		public String message = "";
		public int version = 0;
		public List<EventItemModel> data = null;
	}


	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		getData();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		// do nothing
	}
	
	private void onLoad()
	{
		mXListView.stopLoadMore();
		mXListView.stopRefresh();
	}
}
