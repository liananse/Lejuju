package com.medialab.lejuju.main.myevent.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.lejuju.R;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.http.HHttpDataLoader.HDataListener;
import com.medialab.lejuju.main.myevent.adapter.MPastEventAdapter;
import com.medialab.lejuju.model.EventItemModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UDisplayWidth;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.util.UUtils;
import com.medialab.lejuju.views.PastEventPopMenu;
import com.medialab.lejuju.views.RoundImageView;
import com.medialab.lejuju.views.XListView;
import com.medialab.lejuju.views.XListView.IXListViewListener;

public class MPastEventFragment extends Fragment implements IXListViewListener
{
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
	
	private FinalBitmap fb;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mPastEventAdapter = new MPastEventAdapter(getActivity());
		mPastEventAdapter.setOnCommentClickListener(onCommentClickListener);
		mPastEventPopMenu = new PastEventPopMenu(getActivity());
		fb = FinalBitmap.create(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_my_events, container, false);
		mXListView = (XListView) view.findViewById(R.id.event_list_view);
		// header view
		mHeaderView = inflater.inflate(R.layout.header_view_past_event, null);
		mHeaderPicView = (RoundImageView) mHeaderView.findViewById(R.id.past_event_header_pic);
		mHeaderNickNameTextView = (TextView) mHeaderView.findViewById(R.id.item_event_nick_name);
		mHeaderPostView = (ImageView) mHeaderView.findViewById(R.id.past_event_header_poster);
		
		mHeaderPicBgView = mHeaderView.findViewById(R.id.item_event_head_pic_bg);
		UTools.UI.fitViewByWidth(getActivity(), mHeaderPicBgView, 150, 150, 640);
		UTools.UI.fitViewByWidth(getActivity(), mHeaderPicView, 140, 140, 640);
		
		mHeaderView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// do nothing
			}
		});
		mXListView.addHeaderView(mHeaderView);
		
		mXListView.setPullRefreshEnable(true);
		mXListView.setPullLoadEnable(false);
		mXListView.setXListViewListener(this);
		
		mXListView.setAdapter(mPastEventAdapter);
		if (UUtils.getSelfUserInfoModel(getActivity()) != null)
		{
			fb.display(mHeaderPicView, UUtils.getSelfUserInfoModel(getActivity()).head_pic);
			mHeaderNickNameTextView.setText(UUtils.getSelfUserInfoModel(getActivity()).nick_name);
		}
		getData(0, true);
		
		return view;
	}
	
	
	private void getData(int last_id, boolean isRefresh)
	{
		Map<String, String> params = new HashMap<String, String>();
		
		params.put("width", String.valueOf(UDisplayWidth.getPastEventPicWidth(getActivity())));
		params.put("last_id", String.valueOf(last_id));
		params.put("page_size", "15");
		
		mDataLoader.getData(UConstants.MY_EVENT_PAST_EVENT_URL, params, getActivity(), new EFriendsDataListener(isRefresh));
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
