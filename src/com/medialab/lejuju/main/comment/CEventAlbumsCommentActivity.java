package com.medialab.lejuju.main.comment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.medialab.lejuju.main.comment.adapter.CAlbumsCommentAdapter;
import com.medialab.lejuju.main.comment.model.CAlbumsCommentModel;
import com.medialab.lejuju.model.EventItemModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UDisplayWidth;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.views.XListView;
import com.medialab.lejuju.views.XListView.IXListViewListener;

public class CEventAlbumsCommentActivity extends MBaseActivity implements OnClickListener, IXListViewListener{

    private View mBackView;
	private ImageView mBackImageView;
	private TextView mCommentBarTitleView;
	
	private View event_detail_group_comment_bottom;
	private EditText event_detail_text_editText;
	private ImageView send_text_btn;
	private XListView mXListView;
	
	private View mHeaderView;
	private TextView mHeaderTextView;
	private ImageView mHeaderImageView;
	
	// 从前一个页面传过来的EventItemModel
	private EventItemModel mEventItemModel = null;
	private String event_id = "0";
	private CAlbumsCommentAdapter mCAlbumsCommentAdapter;
	
	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	
	// 如果不是点对点回复则值是0，如果是点对点回复则值是被回复者的id
	private int opposite_id = 0;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		setContentView(R.layout.activity_event_albums);
		mCAlbumsCommentAdapter = new CAlbumsCommentAdapter(this);
		mCAlbumsCommentAdapter.setOnCommentClickListener(onCommentClickListener);
		
		mEventItemModel = (EventItemModel) getIntent().getSerializableExtra(UConstants.EVENT_DETAIL_KEY); 
		//如果是推送跳转过来的，就传event_id
		event_id = getIntent().getStringExtra("event_id");
		if(mEventItemModel!=null)
		{
			event_id = String.valueOf(mEventItemModel.event_id);
		}
		initHeaderBar();
		initView();
		
	}
	
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
		mCommentBarTitleView = (TextView) findViewById(R.id.event_comment_bar_title);
		event_detail_group_comment_bottom = findViewById(R.id.event_detail_group_comment_bottom_ll);
		event_detail_text_editText = (EditText) findViewById(R.id.event_detail_text_editText);
		send_text_btn = (ImageView) findViewById(R.id.send_text_btn);
		
		UTools.UI.fitViewByWidth(this, event_detail_group_comment_bottom, 640, 92, 640);
		UTools.UI.fitViewByWidth(this, event_detail_text_editText, 500, 69, 640);
		UTools.UI.fitViewByWidth(this, send_text_btn, 100, 68, 640);
		
		mHeaderView = LayoutInflater.from(this).inflate(R.layout.item_albums_comment_header, null);
		mHeaderImageView = (ImageView) mHeaderView.findViewById(R.id.item_albums_zan_iv);
		mHeaderTextView = (TextView) mHeaderView.findViewById(R.id.item_albums_zan_text);
		
		UTools.UI.fitViewByWidth(this, mHeaderImageView, 26, 22, 640);
		if (mEventItemModel != null)
		{
			mCommentBarTitleView.setText(mEventItemModel.title);
			mHeaderTextView.setText(mEventItemModel.zan_num + "个人觉得很赞！");
		}
		send_text_btn.setOnClickListener(this);
		
		mXListView = (XListView) findViewById(R.id.albums_comment_list);
		
		mXListView.setPullRefreshEnable(true);
		mXListView.setPullLoadEnable(true);
		mXListView.setXListViewListener(this);
		
		mXListView.addHeaderView(mHeaderView);
		mXListView.setAdapter(mCAlbumsCommentAdapter);
		
		getCommentData(0,true);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mBackView)
		{
			this.finish();
		}
		else if (v == send_text_btn)
		{
			if (!event_detail_text_editText.getText().toString().trim().isEmpty())
			{
				Map<String, String> params = new HashMap<String, String>();
				
				params.put("event_id", String.valueOf(event_id));
				params.put("opposite_id", String.valueOf(opposite_id));
				params.put("content", event_detail_text_editText.getText().toString().trim());
				
				event_detail_text_editText.setText("");
				
				mDataLoader.postData(UConstants.EVENT_ALBUMS_COMMENT_URL, params, this, new HDataListener() {
					
					@Override
					public void onFinish(String source) {
						// TODO Auto-generated method stub
						Gson gson = new Gson();
						
						try {
							TempModel mTempModel = gson.fromJson(source, new TypeToken<TempModel>(){}.getType());
							
							if(mTempModel != null && mTempModel.result.equals("success"))
							{
								getCommentData(0, true);
							}
							else {
								Toast toast = Toast.makeText(CEventAlbumsCommentActivity.this, mTempModel.message, Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.TOP, 0, 0);
								toast.show();
							}
						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast toast = Toast.makeText(CEventAlbumsCommentActivity.this, "评论出错", Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.TOP, 0, 0);
							toast.show();
						}
					}
					
					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						Toast toast = Toast.makeText(CEventAlbumsCommentActivity.this, "评论出错", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.TOP, 0, 0);
						toast.show();
					}
				});
				
				InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);  
                imm.hideSoftInputFromWindow(event_detail_text_editText.getWindowToken(), 0);
				opposite_id = 0;
			}
		}
	}
	
	OnClickListener onCommentClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			CAlbumsCommentModel mCAlbumsCommentModel = (CAlbumsCommentModel) v.getTag();
			if (mCAlbumsCommentModel != null)
			{
				
				event_detail_text_editText.setText("回复" + mCAlbumsCommentModel.org_user.nick_name + ":");
				event_detail_text_editText.requestFocus();
				event_detail_text_editText.setSelection(("回复" + mCAlbumsCommentModel.org_user.nick_name + ":").length());
				
				InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); 
                opposite_id = mCAlbumsCommentModel.org_user.user_id;
			}
		}
	};
	
	protected boolean hasNextPage;
	protected int last_id = 0;
	
	private void getCommentData(int last_id, boolean isRefresh)
	{
		Map<String, String> params = new HashMap<String, String>();
		
		params.put("event_id", String.valueOf(event_id));
		params.put("head_width", String.valueOf(UDisplayWidth.getSmallHeadPicWidth(this)));
		params.put("last_id", String.valueOf(last_id));
		params.put("page_size", "15");
		
		mDataLoader.getData(UConstants.EVENT_GET_ALBUMS_COMMENT_LIST, params, this, new EAlbumsCommentDataListener(isRefresh));
	}
	
	class EAlbumsCommentDataListener implements HDataListener
	{

		boolean isRefresh;

		public EAlbumsCommentDataListener(boolean isRefresh)
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
				Toast toast = Toast.makeText(CEventAlbumsCommentActivity.this, "获取评论列表信息出错！", Toast.LENGTH_SHORT);
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
    //	
	protected void updateData(HashMap<String, Object> result)
	{
		if (result != null)
		{
			List<CAlbumsCommentModel> list = (List<CAlbumsCommentModel>) result.get("data");
			Boolean isRefresh = (Boolean) result.get("isRefresh");
			Boolean hasNextPage = (Boolean) result.get("hasNextPage");
			int last_id = (Integer) result.get("last_id");
			if (list != null && list.size() > 0)
			{
				if (isRefresh)
				{
					mCAlbumsCommentAdapter.refreshData(list);
				}
				else
				{
					mCAlbumsCommentAdapter.addData(list);
				}
			}
			this.last_id = last_id;
			this.hasNextPage = hasNextPage;
			onLoad();
		}
	}
    //	
	class TempModel
	{
		public String result = "";
		public String message = "";
		public int last_id = 0;
		public boolean have_next_page = false;
		
		public List<CAlbumsCommentModel> data = null;
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		getCommentData(0, true);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		if (hasNextPage)
		{
			getCommentData(last_id, false);
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
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
	}
}
