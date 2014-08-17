package com.medialab.lejuju.main.friends;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.main.friends.adapter.FNewFriendsEntryAdapter;
import com.medialab.lejuju.model.FriendsModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.views.XListView;
import com.medialab.lejuju.views.XListView.IXListViewListener;

public class FHaveNewFriendsActivity extends MBaseActivity implements OnClickListener, IXListViewListener{

	private XListView mXListView;
	private FNewFriendsEntryAdapter adapter;
	private HHttpDataLoader loader = new HHttpDataLoader();
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_friends_entry_new_friends);
		adapter = new FNewFriendsEntryAdapter(this);
		initHeaderBar();
		initView();
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
		mXListView = (XListView) findViewById(R.id.new_friends_list);
		
		mXListView.setPullRefreshEnable(true);
		mXListView.setPullLoadEnable(false);
		mXListView.setXListViewListener(this);
		mXListView.setAdapter(adapter);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getData();
	}

	private void getData()
	{
		Map<String,String> params = new HashMap<String,String>();
		
		loader.getData(UConstants.NEW_FRIENDS_URL, params, this, new HHttpDataLoader.HDataListener()
		{
			@Override
			public void onFinish(String source)
			{
				Gson gson = new Gson();
				try {
					TempModel mTempModel = gson.fromJson(source, new TypeToken<TempModel>(){}.getType());
					
					if(mTempModel != null && mTempModel.result.equals("success"))
					{
						HashMap<String, Object> result = new HashMap<String, Object>();
						result.put("data", mTempModel.data);
						handler.sendMessage(handler.obtainMessage(UConstants.MESSAGE_DATA_OK, result));
					}
					else {
						Toast toast = Toast.makeText(FHaveNewFriendsActivity.this, mTempModel.message, Toast.LENGTH_SHORT);
						toast.show();
						onLoad();
					}
				} catch (JsonSyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast toast = Toast.makeText(FHaveNewFriendsActivity.this, "获取新的朋友出错！", Toast.LENGTH_SHORT);
					toast.show();
					onLoad();
				}
			}
			
			@Override
			public void onFail(String msg)
			{
				// TODO Auto-generated method stub
				Toast toast = Toast.makeText(FHaveNewFriendsActivity.this, "网络错误！", Toast.LENGTH_SHORT);
				toast.show();
				onLoad();
			}
		});
	}
	
	public Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
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
				adapter.refreshData(list);
			}
			onLoad();
		}
		SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(this, UConstants.BASE_PREFS_NAME);
		mEditor.putInt(UConstants.NEW_FRIENDS_NUM, 0);
		mEditor.commit();
	}
	
	@Override
	public void onClick(View v)
	{
		if(v == mBackView)
		{
			finish();
		}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch(resultCode)
		{
			case RESULT_OK:
				break;
			default:
				break;	
		}
	}

	class TempModel
	{
		public String result = "";
		public String message = "";
		public int new_friends_num = 0;
		public List<FriendsModel> data = null;
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		getData();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		
	}
	
	private void onLoad()
	{
		mXListView.stopLoadMore();
		mXListView.stopRefresh();
	}

}
