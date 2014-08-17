package com.medialab.lejuju.main.comment;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.MMainTabActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.db.DDBOpenHelper;
import com.medialab.lejuju.fragment.FLoadingProgressBarFragment;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.http.HHttpDataLoader.HDataListener;
import com.medialab.lejuju.main.comment.adapter.CEventTrendsAdapter;
import com.medialab.lejuju.main.eventdetail.EventDetailActivity;
import com.medialab.lejuju.model.EventItemModel;
import com.medialab.lejuju.model.TrendItemModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UDisplayWidth;
import com.medialab.lejuju.util.UTimeShown;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.util.UUtils;
import com.medialab.lejuju.views.AutoPullDownRefreshView;
import com.medialab.lejuju.views.AutoPullDownRefreshView.OnListViewBottomListener;
import com.medialab.lejuju.views.AutoPullDownRefreshView.OnListViewTopListener;
import com.medialab.lejuju.views.AutoPullDownRefreshView.OnRefreshAdapterDataListener;
import com.medialab.lejuju.views.BlurImageView;

public class CEventDiscussActivity extends MBaseActivity implements OnClickListener{

	FinalBitmap fb;
	
	private boolean from_detail = false;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_event_discuss);
		fb = FinalBitmap.create(this);
		from_detail = getIntent().getBooleanExtra("from_detail", false);
		
		mCEventTrendsAdapter = new CEventTrendsAdapter(this);
		initHeaderBar();
		initContentView();
		animationIn();
		mPullDownView.setIsCloseTopAllowRefersh(false);
		fromPush = getIntent().getBooleanExtra(UConstants.FROM_PUSH, false);
		if (fromPush)
		{
			loadData();
		}
		else {
			mEventItemModel = (EventItemModel) getIntent().getSerializableExtra(UConstants.EVENT_DETAIL_KEY);  
			initData();
		}
	}
	
	private LinearLayout header_title_view;
	private View back_btn;
	private View eventdetail_btn;
	private ImageView mBackImageView;
	private ImageView mEventDetailView;
	private void initHeaderBar()
	{
		header_title_view = (LinearLayout) findViewById(R.id.header_title_view);
		back_btn = findViewById(R.id.back_btn);
		eventdetail_btn = findViewById(R.id.eventdetail_btn);
		mBackImageView = (ImageView) findViewById(R.id.back_btn_center);
		mEventDetailView = (ImageView) findViewById(R.id.eventdetail_btn_center);
		
		UTools.UI.fitViewByWidth(this, back_btn, 88, 88, 640);
		UTools.UI.fitViewByWidth(this, eventdetail_btn, 88, 88, 640);
		
		UTools.UI.fitViewByWidth(this, mBackImageView, 40, 44, 640);
		UTools.UI.fitViewByWidth(this, mEventDetailView, 40, 44, 640);
		
		if (from_detail)
		{
			eventdetail_btn.setVisibility(View.INVISIBLE);
		}
		else {
			eventdetail_btn.setVisibility(View.VISIBLE);
		}
		back_btn.setOnClickListener(this);
		eventdetail_btn.setOnClickListener(this);
	}
	
	private EventItemModel mEventItemModel = null;
	
	private LinearLayout event_detail_group_comment_bottom;
	private EditText event_detail_text_editText;
	private ImageView send_text_btn;
	
	////////////////////////////////////////
	private CEventTrendsAdapter mCEventTrendsAdapter;
	private ListView mListView;
	
	private int currentTrendsId = -1;
	
	//
	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	/////////////////////////////////////////
	
	private AutoPullDownRefreshView mPullDownView;
	
	private Boolean fromPush = false;
	
	private View listViewHeadView;
	
	private BlurImageView event_discuss_bg;
	
	private void initContentView()
	{
		event_discuss_bg = (BlurImageView) findViewById(R.id.event_discuss_bg);
		//////////////////////////
		mListView = (ListView) findViewById(R.id.group_comment_list);
		
		listViewHeadView = getLayoutInflater().inflate(R.layout.auto_pull_down_header_view, null);
		mListView.addHeaderView(listViewHeadView);
		
		mListView.setAdapter(mCEventTrendsAdapter);
		mListView.setOnScrollListener(mOnScrollListener );
		mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
		mListView.setKeepScreenOn(true);
		mListView.post(new Runnable() {

			@Override
			public void run() {
				mListView.setSelection(mListView.getCount());
			}
		});
		registerForContextMenu(mListView);
		
		mPullDownView = (AutoPullDownRefreshView) findViewById(R.id.chatting_pull_down_view);
		mPullDownView.setTopViewInitialize(true);
		mPullDownView.setIsCloseTopAllowRefersh(false);
		mPullDownView.setHasbottomViewWithoutscroll(false);
		mPullDownView.setOnRefreshAdapterDataListener(mOnRefreshAdapterDataListener);
		mPullDownView.setOnListViewTopListener(mOnListViewTopListener);
		mPullDownView.setOnListViewBottomListener(mOnListViewBottomListener);
		
		////////////////////////////////////////////
		event_detail_group_comment_bottom = (LinearLayout) findViewById(R.id.event_detail_group_comment_bottom_ll);
		event_detail_text_editText = (EditText) findViewById(R.id.event_detail_text_editText);
		send_text_btn = (ImageView) findViewById(R.id.send_text_btn);
		
		UTools.UI.fitViewByWidth(this, event_detail_group_comment_bottom, 640, 92, 640);
		UTools.UI.fitViewByWidth(this, event_detail_text_editText, 500, 69, 640);
		UTools.UI.fitViewByWidth(this, send_text_btn, 100, 68, 640);
		
		send_text_btn.setOnClickListener(this);
		//数据没有之前，先设置按钮不可用
		send_text_btn.setEnabled(false);
	}
	
	private OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

			if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
				View topView = mListView.getChildAt(mListView.getFirstVisiblePosition());
				if ((topView != null) && (topView.getTop() == 0)){
					mPullDownView.startTopScroll();
				}
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
		}
	};

	private OnRefreshAdapterDataListener mOnRefreshAdapterDataListener = new OnRefreshAdapterDataListener() {

		@Override
		public void refreshData() {
			
			DDBOpenHelper mDdbOpenHelper = DDBOpenHelper.getInstance(CEventDiscussActivity.this);
			List<TrendItemModel> mList = mDdbOpenHelper.getTrendsItemDiscussModelsByEventId(mEventItemModel.event_id, currentTrendsId);
			if (mList != null && mList.size() > 0)
			{
				mCEventTrendsAdapter.addHeadData(mList);
				
				currentTrendsId = mList.get(0).trends_id;
				mCEventTrendsAdapter.notifyDataSetChanged();
				mListView.setSelectionFromTop(mList.size()+1, listViewHeadView.getHeight() + mPullDownView.getTopViewHeight());
				if ( mList.size() < 10)
				{
					mPullDownView.setIsCloseTopAllowRefersh(true);
				}
			}
			else {
				mListView.setSelectionFromTop(1, mPullDownView.getTopViewHeight());
				mPullDownView.setIsCloseTopAllowRefersh(true);
			}
		}

	};

	private OnListViewBottomListener mOnListViewBottomListener = new OnListViewBottomListener() {

		@Override
		public boolean getIsListViewToBottom() {
			if((mListView.getChildAt(-1 + mListView.getChildCount()).getBottom() > mListView.getHeight()) 
					|| (mListView.getLastVisiblePosition() != -1 + mListView.getAdapter().getCount())){

				return false;
			}else{

				return true;
			}
		}
	};

	private OnListViewTopListener mOnListViewTopListener = new OnListViewTopListener() {

		@Override
		public boolean getIsListViewToTop() {
			View topListView =  mListView.getChildAt(mListView.getFirstVisiblePosition());
			if((topListView == null) || (topListView.getTop() != 0)){
				return false;
			}else{
				return true;
			}
		}
	};
	
	public void initData()
	{
		if (mEventItemModel != null)
		{
			SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(this, UConstants.BASE_PREFS_NAME);
			mEditor.putString(UConstants.GROUP_COMMENT_EVENT_ID, String.valueOf(mEventItemModel.event_id));
			mEditor.commit();
			
			registerReceiver(newCommentReceiver, new IntentFilter("com.medialab.lejuju.newtrends"));
			
			if (!from_detail)
			{
				if(mEventItemModel.event_pic==null || mEventItemModel.event_pic.equals(""))
		        {
					event_discuss_bg.setImageResource(getEventBlurTypeId(mEventItemModel.event_type_id));
		        }
		        else
		        {
		        	String event_pic_1 = mEventItemModel.event_pic;
		        	String replace_s = "/" + UDisplayWidth.getEventDetailPicWidth(this) +"/";
		        	String replace_with = "/" + UDisplayWidth.getPosterPicWidth(this) +"/";
		        	
		        	fb.display(event_discuss_bg, event_pic_1.replace(replace_with, replace_s));
		        }
				
				animationMove();
			}

		}
		
		if (mEventItemModel != null && mEventItemModel.event_id != -1)
		{
			DDBOpenHelper mDdbOpenHelper = DDBOpenHelper.getInstance(this);
			
			List<TrendItemModel> mList = mDdbOpenHelper.getTrendsItemDiscussModelsByEventId(mEventItemModel.event_id, currentTrendsId);
			
			if (mList != null && mList.size() > 0)
			{
				mCEventTrendsAdapter.refreshData(mList);
				
				currentTrendsId = mList.get(0).trends_id;

				if ( mList.size() < 10)
				{
					mPullDownView.setIsCloseTopAllowRefersh(true);
				}
			}
			else {
				mListView.setSelectionFromTop(1, mPullDownView.getTopViewHeight());
				mPullDownView.setIsCloseTopAllowRefersh(true);
			}
			
			mDdbOpenHelper.updateAttendsEventTrendsToZero(mEventItemModel.event_id);
		}
		//有数据之后设置为可以使用
		send_text_btn.setEnabled(true);
	}
	
	private int getEventBlurTypeId(int event_type_id)
	{
		if (event_type_id == 1)
		{
			return R.drawable.event_image_blur_1;
		}
		else if (event_type_id == 2)
		{
			return R.drawable.event_image_blur_2;
		}
		else if (event_type_id == 3)
		{
			return R.drawable.event_image_blur_3;
		}
		else if (event_type_id == 7)
		{
			return R.drawable.event_image_blur_7;
		}
		else if (event_type_id == 8)
		{
			return R.drawable.event_image_blur_8;
		}
		else if (event_type_id == 9)
		{
			return R.drawable.event_image_blur_9;
		}
		else {
			return R.drawable.event_image_blur_1;
		}
	}
	
	private void loadData()
	{
		String event_id = getIntent().getStringExtra("event_id");
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("event_id", event_id);
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
					TempModel temp = gson.fromJson(source, TempModel.class);
					
					if (temp != null && temp.result.equals("success"))
					{
						if (temp.event != null)
						{
							mEventItemModel = temp.event;
							
							initData();
						}
					}
					else {
						UUtils.showNetErrorToast(CEventDiscussActivity.this);
					}
				} catch (JsonSyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					UUtils.showNetErrorToast(CEventDiscussActivity.this);
				}
				mLoadingProgressBarFragment.dismiss();
			}
			
			@Override
			public void onFail(String msg)
			{
				UUtils.showNetErrorToast(CEventDiscussActivity.this);
				mLoadingProgressBarFragment.dismiss();
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == back_btn)
		{
			mBackViewClick();
		}
		else if (v == eventdetail_btn){
			if (mEventItemModel != null)
			{
				Intent intent = new Intent(this, EventDetailActivity.class);
				Bundle bundle = new Bundle();
				
				bundle.putBoolean("from_discuss", true);
				bundle.putSerializable(UConstants.EVENT_DETAIL_KEY, (Serializable) mEventItemModel);
				
				intent.putExtras(bundle);
				
				startActivity(intent);
			}
		}
		else if (v == send_text_btn){
			 
			if (!event_detail_text_editText.getText().toString().trim().isEmpty())
			{
				List<TrendItemModel> mList = new ArrayList<TrendItemModel>();
				TrendItemModel mTrendItemModel = new TrendItemModel();
				mTrendItemModel.org_user = UUtils.selfUserInfoModelToFriendsModel(this);
				mTrendItemModel.event_id = mEventItemModel.event_id;
				mTrendItemModel.type = 1;
				mTrendItemModel.content = event_detail_text_editText.getText().toString().trim();
				mTrendItemModel.show_time = 1;
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				mTrendItemModel.add_time = df.format(new Date());
				
				
				DDBOpenHelper mDdbOpenHelper = DDBOpenHelper.getInstance(this);
				TrendItemModel mTopTrendItemModel = mDdbOpenHelper.getTrendsMaxTimeByEventID(mEventItemModel.event_id);
				
				if (mTopTrendItemModel != null)
				{
					if (UTimeShown.isLargeThanThressMinute(mTrendItemModel.add_time, mTopTrendItemModel.add_time))
					{
						mTrendItemModel.show_time = 1;
					}
					else 
					{
						mTrendItemModel.show_time = 0;
					}
				}
				
				mDdbOpenHelper.insertTrendsModel(mTrendItemModel);
				
				mList.add(mTrendItemModel);
				
				mEventItemModel.trends = mList;
				mDdbOpenHelper.updateAttendEventModel(mEventItemModel);
				
				mCEventTrendsAdapter.addData(mList);
				mListView.setSelection(mListView.getCount() - 1);
				
				Map<String, String> params = new HashMap<String, String>();
				
				params.put("event_id", String.valueOf(mEventItemModel.event_id));
				params.put("content", event_detail_text_editText.getText().toString().trim());
				event_detail_text_editText.setText("");
				mDataLoader.postData(UConstants.EVENT_COMMENT_SEND_URL, params, this, new HDataListener() {
					
					@Override
					public void onFinish(String source) {
						// TODO Auto-generated method stub
					}
					
					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						
					}
				});
				
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			mBackViewClick();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void mBackViewClick()
	{
		int stackSize = UTools.activityhelper.getStackSize();

		// 历史栈中只有当前这一个activity
		if (stackSize == 1)
		{
			Intent intent = new Intent(this,MMainTabActivity.class);
			startActivity(intent);
		}
		else
		{
			this.setResult(RESULT_OK, getIntent());
		}
		
		animationOut();
		animationBack();
	}
	
	
	/**
	 * 执行出现动画
	 */
	private void animationIn()
	{
		// 从顶部出现动画
		Animation topInAnimation = new TranslateAnimation(0F,0F,-150, 0F);
		topInAnimation.setFillAfter(true);
		topInAnimation.setDuration(300);
		
		// 从底部出现动画
		Animation bottomInAnimation = new TranslateAnimation(0F,0F,150, 0F);
		bottomInAnimation.setFillAfter(true);
		bottomInAnimation.setDuration(300);
		
		header_title_view.startAnimation(topInAnimation);
		event_detail_group_comment_bottom.startAnimation(bottomInAnimation);
	}
	
	/**
	 * 执行消失动画并在消失
	 */
	private void animationOut()
	{
		// 从顶部消失动画
		Animation topOutAnimation = new TranslateAnimation(0F,0F,0F, -150);
		topOutAnimation.setFillAfter(true);
		topOutAnimation.setDuration(300);
		// 从底部消失动画
		Animation bottomOutAnimation = new TranslateAnimation(0F,0F,0F, 150);
		bottomOutAnimation.setFillAfter(true);
		bottomOutAnimation.setDuration(300);
		
		bottomOutAnimation.setAnimationListener(new Animation.AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				CEventDiscussActivity.this.finish();
			}
		});
		
		header_title_view.startAnimation(topOutAnimation);
		event_detail_group_comment_bottom.startAnimation(bottomOutAnimation);
	}
	
	class TempModel
	{
		String result;
		String message;
		EventItemModel event;
		String invite_code;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(this, UConstants.BASE_PREFS_NAME);
		mEditor.putString(UConstants.GROUP_COMMENT_EVENT_ID, String.valueOf(0));
		mEditor.commit();
		
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
				DDBOpenHelper mDdbOpenHelper = DDBOpenHelper.getInstance(CEventDiscussActivity.this);
				TrendItemModel mTopTrendItemModel = mDdbOpenHelper.getTrendsMaxTimeByEventID(mEventItemModel.event_id);
				
				if (mTopTrendItemModel != null)
				{
					List<TrendItemModel> mList = new ArrayList<TrendItemModel>();
					
					mList.add(mTopTrendItemModel);
					mCEventTrendsAdapter.addData(mList);
					mListView.setSelection(mListView.getCount() - 1);
				}
				
				mDdbOpenHelper.updateAttendsEventTrendsToZero(mEventItemModel.event_id);
				
			}
		}
	};
	
	private void animationMove()
	{
		//创建一个AnimationSet对象 
		AnimationSet animationSet = new AnimationSet(true);

		// 移动 
        Animation moveAnimation = new TranslateAnimation(0F,0F,0F, -getResources().getDimension(R.dimen.event_detail_pic_margin_top));
		
		animationSet.addAnimation(moveAnimation);
        // 放大
		float scale = getResources().getDisplayMetrics().heightPixels/getResources().getDimension(R.dimen.event_detail_pic_height);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, scale, 1f, scale,
                        Animation.RELATIVE_TO_SELF, 0.2f,      
                        Animation.RELATIVE_TO_SELF, 0.3f);      
        animationSet.addAnimation(scaleAnimation);        
        animationSet.setFillAfter(true);      
        animationSet.setDuration(300);
        event_discuss_bg.startAnimation(animationSet);
	}
	
	/**
	 * 将背景图移动会原位置
	 */
	private void animationBack()
	{
		AnimationSet animationSet = new AnimationSet(true);

		// 移动 
        Animation moveAnimation = new TranslateAnimation(0F,0F,-getResources().getDimension(R.dimen.event_detail_pic_margin_top), 0F);
		
		animationSet.addAnimation(moveAnimation);
        // 缩小
		float scale = getResources().getDisplayMetrics().heightPixels/getResources().getDimension(R.dimen.event_detail_pic_height);
        ScaleAnimation scaleAnimation = new ScaleAnimation(scale, 1f, scale, 1f,
                        Animation.RELATIVE_TO_SELF, 0.2f,      
                        Animation.RELATIVE_TO_SELF, 0.3f);      
        animationSet.addAnimation(scaleAnimation);        
        animationSet.setFillAfter(true);      
        animationSet.setDuration(300);
        event_discuss_bg.startAnimation(animationSet);
        
	}

}
