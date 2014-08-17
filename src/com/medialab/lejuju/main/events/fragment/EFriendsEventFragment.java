package com.medialab.lejuju.main.events.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.lejuju.R;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.http.HHttpDataLoader.HDataListener;
import com.medialab.lejuju.main.events.adapter.EEventAdapter;
import com.medialab.lejuju.model.EventItemModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UDisplayWidth;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.util.UUtils;
import com.medialab.lejuju.views.XListView;
import com.medialab.lejuju.views.XListView.IXListViewListener;

public class EFriendsEventFragment extends Fragment implements IXListViewListener
{
	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	private XListView mXListView;
	private EEventAdapter mEEventAdapter;
	
	protected boolean hasNextPage;
	protected int last_id = 0;
	
	private View mFooterView;
	private TextView mH1TextView;
	private TextView mH2TextView;
	
	private View mEmptyHeaderView;
	private TextView mTipsTitle;
	private TextView mTipsContent;
	private ImageView empty_logo;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mEEventAdapter = new EEventAdapter(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View view = inflater.inflate(R.layout.fragment_friends_nearby_events, container, false);
		mXListView = (XListView) view.findViewById(R.id.event_list_view);
		
		mXListView.setPullRefreshEnable(true);
		mXListView.setPullLoadEnable(false);
		mXListView.setXListViewListener(this);
		
		mXListView.setAdapter(mEEventAdapter);
		
		mFooterView = LayoutInflater.from(getActivity()).inflate(R.layout.comman_footer_view, null);
		mH1TextView = (TextView) mFooterView.findViewById(R.id.h1_tv);
		mH2TextView = (TextView) mFooterView.findViewById(R.id.h2_tv);
		
		mH1TextView.setVisibility(View.GONE);
		mH2TextView.setVisibility(View.VISIBLE);
		
		mXListView.addFooterView(mFooterView);
		mXListView.setRefreshState();
		
		mEmptyHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.empty_event_view, null);
		
		empty_logo = (ImageView) mEmptyHeaderView.findViewById(R.id.empty_logo_iv);
		
		UTools.UI.fitViewByWidth(getActivity(), empty_logo, 178, 182, 640);
		mTipsContent = (TextView) mEmptyHeaderView.findViewById(R.id.tips_content);
		mTipsTitle = (TextView) mEmptyHeaderView.findViewById(R.id.tips_title);
		
		mTipsContent.setText("Tips:你可以通过活动来收集和分享以往活动的照片");
		mTipsTitle.setText("你和你的好友都还没参加活动哦！\n马上创建一个活动，邀请朋友参加吧！");
		getData(0, true);
		
		return view;
	}
	
	private void getData(int last_id, boolean isRefresh)
	{
		Map<String, String> params = new HashMap<String, String>();
		
		params.put("head_width", String.valueOf(UDisplayWidth.getSmallHeadPicWidth(getActivity())));
		params.put("event_pic_width", String.valueOf(UDisplayWidth.getPosterPicWidth(getActivity())));
		params.put("type", "friend");
		params.put("last_id", String.valueOf(last_id));
		params.put("page_size", "10");
		
		mDataLoader.getData(UConstants.FRIEND_NEARBY_EVENT_URL, params, getActivity(), new EFriendsDataListener(isRefresh));
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
					UUtils.showNetErrorToast(getActivity());
					onLoad();
				}
			} catch (JsonSyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				UUtils.showNetErrorToast(getActivity());
				onLoad();
			}
		}

		@Override
		public void onFail(String msg) {
			// TODO Auto-generated method stub
			UUtils.showNetErrorToast(getActivity());
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
						mH1TextView.setVisibility(View.VISIBLE);
						mH2TextView.setVisibility(View.GONE);
					}
					else
					{
						mXListView.setPullLoadEnable(false);
						mH1TextView.setVisibility(View.GONE);
						mH2TextView.setVisibility(View.VISIBLE);
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
					mXListView.removeHeaderView(mEmptyHeaderView);
					mEEventAdapter.refreshData(list);
				}
				else
				{
					mEEventAdapter.addData(list);
				}
				
			}
			else {
				if (isRefresh)
				{
					mXListView.removeHeaderView(mEmptyHeaderView);
					mXListView.addHeaderView(mEmptyHeaderView);
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
	
	private void onLoad()
	{
		mXListView.stopLoadMore();
		mXListView.stopRefresh();
	}
	
}
