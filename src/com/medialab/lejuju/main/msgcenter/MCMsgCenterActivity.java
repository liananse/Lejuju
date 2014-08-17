package com.medialab.lejuju.main.msgcenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.http.HHttpDataLoader.HDataListener;
import com.medialab.lejuju.main.msgcenter.adapter.MCMsgAdapter;
import com.medialab.lejuju.main.msgcenter.model.MCMsgModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UDisplayWidth;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.util.UUtils;
import com.medialab.lejuju.views.XListView;
import com.medialab.lejuju.views.XListView.IXListViewListener;

public class MCMsgCenterActivity extends MBaseActivity implements OnClickListener, IXListViewListener{

	private XListView mXListView;
	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	private MCMsgAdapter mcMsgAdapter;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		setContentView(R.layout.activity_msg_center);
		mcMsgAdapter = new MCMsgAdapter(this);
		initHeaderBar();
		initView();
		
		mXListView.setRefreshState();
		getMsgData(0,true);
	}

	// 初始化header bar
	private View mBackView;
	
	private ImageView mBackImageView;
	
	private void initHeaderBar()
	{
		mBackView = findViewById(R.id.back_btn);
		
		mBackImageView = (ImageView) findViewById(R.id.back_btn_center);
		
		UTools.UI.fitViewByWidth(this, mBackView, 130, 62, 640);
		
		UTools.UI.fitViewByWidth(this, mBackImageView, 18, 32, 640);
		
		mBackView.setOnClickListener(this);
	}
	
	private void initView()
	{
		mXListView = (XListView) findViewById(R.id.msg_center_list);
		
		mXListView.setPullRefreshEnable(true);
		mXListView.setPullLoadEnable(false);
		mXListView.setXListViewListener(this);
		mXListView.setAdapter(mcMsgAdapter);
	}
	
	protected boolean hasNextPage;
	protected int last_id = 0;
	
	private void getMsgData(int last_id, boolean isRefresh)
	{
		Map<String, String> params = new HashMap<String, String>();
		
		params.put("head_width", String.valueOf(UDisplayWidth.getSmallHeadPicWidth(this)));
		params.put("width", String.valueOf(UDisplayWidth.getPhotoPicWidth(this)));
		params.put("last_id", String.valueOf(last_id));
		params.put("page_size", "20");
		
		mDataLoader.getData(UConstants.MSG_CENTER_URL, params, this, new EMsgDataListener(isRefresh));
	}
	
	class EMsgDataListener implements HDataListener
	{

		boolean isRefresh;

		public EMsgDataListener(boolean isRefresh)
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
					UUtils.showNetErrorToast(MCMsgCenterActivity.this);
					onLoad();
				}
			} catch (JsonSyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				UUtils.showNetErrorToast(MCMsgCenterActivity.this);
				onLoad();
			}
		}

		@Override
		public void onFail(String msg) {
			// TODO Auto-generated method stub
			UUtils.showNetErrorToast(MCMsgCenterActivity.this);
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
    //	
	protected void updateData(HashMap<String, Object> result)
	{
		if (result != null)
		{
			List<MCMsgModel> list = (List<MCMsgModel>) result.get("data");
			Boolean isRefresh = (Boolean) result.get("isRefresh");
			Boolean hasNextPage = (Boolean) result.get("hasNextPage");
			int last_id = (Integer) result.get("last_id");
			if (list != null && list.size() > 0)
			{
				if (isRefresh)
				{
					mcMsgAdapter.refreshData(list);
				}
				else
				{
					mcMsgAdapter.addData(list);
				}
			}
			this.last_id = last_id;
			this.hasNextPage = hasNextPage;
			onLoad();
		}
		
		SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(this, UConstants.BASE_PREFS_NAME);
		mEditor.putInt(UConstants.UNREAD_NEWS_NUM, 0);
		mEditor.commit();
	}
    //	
	class TempModel
	{
		public String result = "";
		public String message = "";
		public int last_id = 0;
		public boolean have_next_page = false;
		
		public List<MCMsgModel> data = null;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mBackView)
		{
			this.finish();
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		getMsgData(0, true);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		if (hasNextPage)
		{
			getMsgData(last_id, false);
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
