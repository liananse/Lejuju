package com.medialab.lejuju.main.eventdetail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.MMainTabActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.fragment.FLoadingProgressBarFragment;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.http.HHttpDataLoader.HDataListener;
import com.medialab.lejuju.main.comment.CEventAlbumsCommentActivity;
import com.medialab.lejuju.main.comment.CEventDiscussActivity;
import com.medialab.lejuju.main.eventdetail.adapter.EDEventDetailUserAdapter;
import com.medialab.lejuju.main.eventdetail.adapter.EventDetailAdapter;
import com.medialab.lejuju.main.eventdetail.model.EventDetailRecordModel;
import com.medialab.lejuju.main.photowall.views.PShareAlertDialog;
import com.medialab.lejuju.main.photowall.views.PShareAlertDialog.OnAlertSelectId;
import com.medialab.lejuju.model.EventItemModel;
import com.medialab.lejuju.model.FriendsModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UDisplayWidth;
import com.medialab.lejuju.util.UTimeShown;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.util.UUtils;
import com.medialab.lejuju.views.BlurImageView;
import com.medialab.lejuju.views.EDListView;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class EventDetailActivity extends MBaseActivity implements OnClickListener{

	private Boolean fromPush = false;
	private EventItemModel mEventItemModel = null;
	
	private boolean from_discuss = false;
	private FinalBitmap fb;
	private static int alphaHeight = 200;
	
	private EventDetailAdapter mEventDetailAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_event_detail);
		
		// we chat register
		regToWx(this);
		fb = FinalBitmap.create(this);
		mEventDetailAdapter = new EventDetailAdapter(this);
		fromPush = getIntent().getBooleanExtra(UConstants.FROM_PUSH, false);
		from_discuss = getIntent().getBooleanExtra("from_discuss", false);
		
		// findViewById()
		initHeaderBar();
		initTopEventContentView();
		initContentView();
		initBottomBtnView();
		
		animationIn();
		if (fromPush)
		{
			loadData(getIntent().getStringExtra("event_id"));
		}
		else 
		{
			mEventItemModel = (EventItemModel) getIntent().getSerializableExtra(UConstants.EVENT_DETAIL_KEY);
			
			if (mEventItemModel != null)
			{
				if(mEventItemModel.event_pic==null || mEventItemModel.event_pic.equals(""))
		        {
					bottom_bg_image.setImageResource(getPosterId(mEventItemModel.event_type_id));
		        }
		        else
		        {
		        	fb.display(bottom_bg_image, mEventItemModel.event_pic);
		        }
				
				if (mEventItemModel.master.user_id == UUtils.getSelfUserInfoModel(this).user_id)
				{
					mEventDetailAdapter.setIsSelfCreate(true);
				}
				else {
					mEventDetailAdapter.setIsSelfCreate(false);
				}
				
				mEventDetailAdapter.setEventId(mEventItemModel.event_id);
			}
			
			loadData(String.valueOf(mEventItemModel.event_id));
		}
		
//		if (!event_id.equals("-1") && !event_id.isEmpty())
//		{
//			loadData(event_id);
//			getEventDetailPicData("load_success");
//		}
//		else {
//			String wapRequestUrl = getIntent().getDataString();
//			if (!TextUtils.isEmpty(wapRequestUrl))
//			{
//				int index = wapRequestUrl.lastIndexOf("/");
//				if (index != -1)
//				{
//					String invite_code = wapRequestUrl.substring(index + 1);
//					
//					loadDataByInviteCode(invite_code);
//				}
//			}
//		}
	}
	
	private int getPosterId(int event_type_id)
	{
		if (event_type_id == 1)
		{
			return R.drawable.event_image_1;
		}
		else if (event_type_id == 2)
		{
			return R.drawable.event_image_2;
		}
		else if (event_type_id == 3)
		{
			return R.drawable.event_image_3;
		}
		else if (event_type_id == 7)
		{
			return R.drawable.event_image_7;
		}
		else if (event_type_id == 8)
		{
			return R.drawable.event_image_8;
		}
		else if (event_type_id == 9)
		{
			return R.drawable.event_image_9;
		}
		else {
			return R.drawable.event_image_1;
		}
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
	
	private void displayEventContent()
	{
		if (mEventItemModel != null)
		{
			top_content_title.setText(mEventItemModel.title);
			top_content_address.setText(mEventItemModel.address);
			top_content_time.setText(UTimeShown.getMsgCenterTimeShown(mEventItemModel.start_time));
			
			header_middle_title.setText(mEventItemModel.title);
			header_middle_address.setText(mEventItemModel.address);
			
			header_event_introduce.setText(mEventItemModel.introduce);
			
			if (!from_discuss)
			{
				if (mEventItemModel.join == 3)
				{
					event_i.setVisibility(View.VISIBLE);
					header_middle_btn.setVisibility(View.VISIBLE);
					photo_btn.setVisibility(View.VISIBLE);
				}
				else {
					event_i.setVisibility(View.GONE);
					header_middle_btn.setVisibility(View.INVISIBLE);
					photo_btn.setVisibility(View.INVISIBLE);
				}
			}
			else {
				if (mEventItemModel.join == 3)
				{
					event_i.setVisibility(View.VISIBLE);
					header_middle_btn.setVisibility(View.INVISIBLE);
					photo_btn.setVisibility(View.VISIBLE);
				}
				else {
					event_i.setVisibility(View.GONE);
					header_middle_btn.setVisibility(View.INVISIBLE);
					photo_btn.setVisibility(View.INVISIBLE);
				}
			}
			
			if(mEventItemModel.event_pic==null || mEventItemModel.event_pic.equals(""))
	        {
				blurred_image.setImageResource(getEventBlurTypeId(mEventItemModel.event_type_id));
				blurred_middle_image.setImageResource(getEventBlurTypeId(mEventItemModel.event_type_id));
	        }
	        else
	        {
	        	System.out.println(mEventItemModel.event_pic);
	        	fb.display(blurred_image, mEventItemModel.event_pic);
	        	fb.display(blurred_middle_image, mEventItemModel.event_pic);
	        }
		}
	}
	
	private View back_btn;
	private View photo_btn;
	private View header_middle_btn;
	private View header_middle_ll;
	
	private ImageView back_btn_center;
	private ImageView photo_btn_center;
	private ImageView header_middle_btn_center;
	
	private LinearLayout header_title_view;
	
	// 顶部活动title
	private TextView header_middle_title;
	// 顶部活动地址
	private TextView header_middle_address;
	/**
	 * 初始化header bar
	 * @author liananse
	 * 2013-9-16
	 */
	private void initHeaderBar()
	{
		back_btn = findViewById(R.id.back_btn);
		photo_btn = findViewById(R.id.photo_btn);
		header_middle_btn = findViewById(R.id.header_middle_btn);
		header_middle_ll = findViewById(R.id.header_middle_ll);
		
		back_btn_center = (ImageView) findViewById(R.id.back_btn_center);
		photo_btn_center = (ImageView) findViewById(R.id.photo_btn_center);
		header_middle_btn_center = (ImageView) findViewById(R.id.header_middle_btn_center);
		header_title_view = (LinearLayout) findViewById(R.id.header_title_view);
		
		header_middle_title = (TextView) findViewById(R.id.header_middle_title);
		header_middle_address = (TextView) findViewById(R.id.header_middle_address);
		
		UTools.UI.fitViewByWidth(this, back_btn, 88, 88, 640);
		UTools.UI.fitViewByWidth(this, photo_btn, 88, 88, 640);
		UTools.UI.fitViewByWidth(this, header_middle_btn, 88, 88, 640);
		
		UTools.UI.fitViewByWidth(this, back_btn_center, 18, 32, 640);
		UTools.UI.fitViewByWidth(this, photo_btn_center, 46, 36, 640);
		UTools.UI.fitViewByWidth(this, header_middle_btn_center, 39, 39, 640);
		
		back_btn.setOnClickListener(this);
		photo_btn.setOnClickListener(this);
		header_middle_btn.setOnClickListener(this);
		header_middle_btn.setClickable(false);// 初始化为不可点击，动画执行完以后可以点击
	}
	
	private LinearLayout bottom_content_view;
	private LinearLayout top_content_view;
	private TextView top_content_title;
	private TextView top_content_address;
	private TextView top_content_time;
	
	private ImageView event_i;
	/**
	 * 初始化 活动content view
	 * @author liananse
	 * 2013-9-16
	 */
	private void initTopEventContentView()
	{
		bottom_content_view = (LinearLayout) findViewById(R.id.bottom_content_view);
		top_content_view = (LinearLayout) findViewById(R.id.top_content_view);
		
		top_content_title = (TextView) findViewById(R.id.top_content_title);
		top_content_address = (TextView) findViewById(R.id.top_content_address);
		top_content_time = (TextView) findViewById(R.id.top_content_time);
		
		event_i = (ImageView) findViewById(R.id.event_i);
		
		UTools.UI.fitViewByWidth(this, event_i, 40, 40, 640);
		
		event_i.setOnClickListener(this);
	}
	
	EDListView lv;
	
	private View top_listview;
	private ImageView bottom_bg_image;
	private BlurImageView blurred_middle_image;
	
	private BlurImageView blurred_image;
	
	private View blurred_image_ll;
	
	public static View mHeaderView;
	private LinearLayout mEmptyHeaderView;
	private TextView header_event_introduce;
	
	
	private int lastVisibileItem;
	
	/**
	 * 初始化content view
	 * @author liananse
	 * 2013-9-16
	 */
	private void initContentView()
	{
		top_listview = findViewById(R.id.top_listview);
		bottom_bg_image = (ImageView) findViewById(R.id.bottom_bg_image);
		blurred_middle_image = (BlurImageView) findViewById(R.id.blurred_middle_image);
		blurred_image = (BlurImageView) findViewById(R.id.blurred_image);
		
		blurred_image_ll = findViewById(R.id.blurred_image_ll);
		
		lv = (EDListView) findViewById(R.id.lv);
		mHeaderView = LayoutInflater.from(this).inflate(
				R.layout.item_event_detail_header, null);
		
		mEmptyHeaderView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.event_detail_header_view, null);
		header_event_introduce = (TextView) mEmptyHeaderView.findViewById(R.id.header_event_introduce);
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		mEmptyHeaderView.setLayoutParams(lp);
		lv.addHeaderView(mHeaderView);
		lv.setHeaderView(mHeaderView);
		
		mFooterView = LayoutInflater.from(this).inflate(R.layout.comman_footer_view, null);
		
		mH1TextView = (TextView) mFooterView.findViewById(R.id.h1_tv);
		mH2TextView = (TextView) mFooterView.findViewById(R.id.h2_tv);
		
		mH1TextView.setVisibility(View.VISIBLE);
		mH2TextView.setVisibility(View.GONE);
		
		lv.addFooterView(mFooterView);
		lv.setAdapter(mEventDetailAdapter);
		
		lv.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE && (lastVisibileItem+1) == lv.getCount()){
					if (hasNextPage && !loadingState)
					{
						initPicData(false);
					}
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				lastVisibileItem = firstVisibleItem + visibleItemCount -1;
				int dy = mHeaderView.getBottom() + header_title_view.getHeight() - bottom_content_view.getBottom() + 1;
				
				if (dy >= 0)
				{
					if (bottom_bg_image.getTop() < 0)
					{
						bottom_bg_image.setBottom(bottom_bg_image.getBottom() + dy/2);
						bottom_bg_image.setTop(bottom_bg_image.getTop() + dy/2);
						
						bottom_content_view.setTop(bottom_content_view.getTop() + dy);
						bottom_content_view.setBottom(bottom_content_view.getBottom() + dy);
						
						FrameLayout.LayoutParams lp;
						lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, EventDetailActivity.this.getResources().getDimensionPixelSize(R.dimen.event_detail_pic_shown_height));
						
						lp.topMargin = (EventDetailActivity.this.getResources().getDimensionPixelSize(R.dimen.event_detail_pic_margin_top) + dy);
						
						top_content_view.setLayoutParams(lp);
					}
				}
				
				if (dy >= 0)
				{
					top_content_view.clearAnimation();
					top_content_view.bringToFront();
				}
				else
				{
					top_listview.clearAnimation();
					top_listview.bringToFront();
				}
				
				if (mHeaderView.getBottom() > alphaHeight)
				{
					// 跳转到讨论的Button显示，同时设置为可点击
					header_middle_btn.setAlpha(1);
					header_middle_btn.setClickable(true);
					
					header_middle_ll.setAlpha(0);
					blurred_image_ll.setAlpha(0);
				}
				else 
				{
					header_middle_btn.setAlpha((float)mHeaderView.getBottom()/alphaHeight);
					header_middle_btn.setClickable(true);
					
					blurred_image_ll.setAlpha((float)(alphaHeight-mHeaderView.getBottom())/alphaHeight);
					header_middle_ll.setAlpha((float)(alphaHeight-mHeaderView.getBottom())/alphaHeight);
				}
				
				if (firstVisibleItem > 0)
				{
					// 跳转到讨论的Button完全隐藏,同时设置为不可点击
					header_middle_btn.setAlpha(0);
					header_middle_btn.setClickable(false);
					
					blurred_image_ll.setAlpha(1);
					header_middle_ll.setAlpha(1);
				}
			}
		});
	}

	private View bottom_btn_view;
	private TextView text_btn_comment;
	private TextView text_btn_share;
	private TextView text_btn_zan;
	private TextView text_btn_join;
	private TextView text_btn_like;
	private TextView text_btn_has_sign_up;
	private void initBottomBtnView()
	{
		bottom_btn_view = findViewById(R.id.bottom_btn_view);
		text_btn_comment = (TextView) findViewById(R.id.text_btn_comment);
		text_btn_share = (TextView) findViewById(R.id.text_btn_share);
		text_btn_zan = (TextView) findViewById(R.id.text_btn_zan);
		text_btn_join = (TextView) findViewById(R.id.text_btn_join);
		text_btn_like = (TextView) findViewById(R.id.text_btn_like);
		text_btn_has_sign_up = (TextView) findViewById(R.id.text_btn_has_sign_up);
		
		text_btn_comment.setOnClickListener(this);
		text_btn_share.setOnClickListener(this);
		text_btn_zan.setOnClickListener(this);
		text_btn_join.setOnClickListener(this);
		text_btn_like.setOnClickListener(this);
	}
	
	private void displayBottomBtnView()
	{
		if (mEventItemModel != null)
		{
			if (mEventItemModel.allow_join)
			{
				if (mEventItemModel.join == 3)
				{
					text_btn_comment.setVisibility(View.VISIBLE);
					text_btn_share.setVisibility(View.VISIBLE);
					
					text_btn_zan.setVisibility(View.GONE);
					text_btn_join.setVisibility(View.GONE);
					text_btn_like.setVisibility(View.GONE);
				}
				else if (mEventItemModel.join == 1)
				{
					text_btn_zan.setVisibility(View.GONE);
					text_btn_join.setVisibility(View.GONE);
					text_btn_comment.setVisibility(View.GONE);
					text_btn_share.setVisibility(View.GONE);
					text_btn_like.setVisibility(View.GONE);
					
					text_btn_has_sign_up.setVisibility(View.VISIBLE);
				}
				else
				{
					text_btn_zan.setVisibility(View.VISIBLE);
					text_btn_join.setVisibility(View.VISIBLE);
					
					text_btn_comment.setVisibility(View.GONE);
					text_btn_share.setVisibility(View.GONE);
					text_btn_like.setVisibility(View.GONE);
					
					if (mEventItemModel.zan)
					{
						text_btn_zan.setText("已赞");
					}
					else {
						text_btn_zan.setText("赞");
					}
				}
			}
			else {
				text_btn_like.setVisibility(View.VISIBLE);
				
				if (mEventItemModel.interest)
				{
					text_btn_like.setClickable(false);
					text_btn_like.setTextColor(Color.parseColor("#fb1359"));
				}
				else {
					text_btn_like.setClickable(true);
					text_btn_like.setTextColor(Color.parseColor("#000000"));
				}
				text_btn_comment.setVisibility(View.GONE);
				text_btn_join.setVisibility(View.GONE);
				text_btn_share.setVisibility(View.GONE);
				text_btn_zan.setVisibility(View.GONE);
			}
		}
	}
	private boolean loadingState = false;
	private View mFooterView;
	private TextView mH1TextView;
	private TextView mH2TextView;
	private void initPicData(boolean refresh)
	{
		final boolean isRefresh = refresh;
		if (mEventItemModel != null)
		{
			loadingState = true;
			Map<String, String> params = new HashMap<String, String>();
			
			params.put("head_width", String.valueOf(UDisplayWidth.getSmallHeadPicWidth(this)));
			params.put("event_id", String.valueOf(mEventItemModel.event_id));
			params.put("last_id", String.valueOf(last_id));
			params.put("page_size", "5");
			params.put("sort", "desc");
			
			mDataLoader.getData(UConstants.EVENT_DETAIL_PHOTO_URL_V2, params, this, new HDataListener() {
				
				@Override
				public void onFinish(String source) {
					// TODO Auto-generated method stub
					Gson gson = new Gson();
					
					try {
						TempRecordModel mTempRecordModel = gson.fromJson(source, new TypeToken<TempRecordModel>(){}.getType());
						
						if(mTempRecordModel != null && mTempRecordModel.result.equals("success"))
						{
							HashMap<String, Object> result = new HashMap<String, Object>();
							result.put("data", mTempRecordModel.records);
							result.put("isRefresh", isRefresh);
							result.put("last_id", mTempRecordModel.last_id);
							result.put("hasNextPage", mTempRecordModel.have_next_page);
							handler.sendMessage(handler.obtainMessage(UConstants.MESSAGE_DATA_OK, result));
						}
						else {
							UUtils.showNetErrorToast(EventDetailActivity.this);
							loadingState = false;
						}
					} catch (JsonSyntaxException e) {
						// TODO Auto-generated catch block
						UUtils.showNetErrorToast(EventDetailActivity.this);
						loadingState = false;
					}
				}
				
				@Override
				public void onFail(String msg) {
					// TODO Auto-generated method stub
					loadingState = false;
				}
			});
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
	
	protected boolean hasNextPage;
	protected int last_id = 0;
	
	protected void updateData(HashMap<String, Object> result)
	{
		if (result != null)
		{
			List<EventDetailRecordModel> list = (List<EventDetailRecordModel>) result.get("data");
			
			Boolean isRefresh = (Boolean) result.get("isRefresh");
			Boolean hasNextPage = (Boolean) result.get("hasNextPage");
			int lastId = (Integer) result.get("last_id");
			if (list != null && list.size() > 0)
			{
				if (isRefresh)
				{
					mEventDetailAdapter.refreshData(list);
					bottomAnimationIn();
				}
				else
				{
					mEventDetailAdapter.addData(list);
				}
			}
			else {
				if (isRefresh)
				{
					lv.addHeaderView(mEmptyHeaderView);
					bottomAnimationIn();
				}
				else
				{
					mEventDetailAdapter.addData(list);
				}
			}
			this.last_id = lastId;
			this.hasNextPage = hasNextPage;
		}
		loadingState = false;
		
	}
	
	class TempRecordModel
	{
		public String result = "";
		public String message = "";
		public boolean zan = false;
		public boolean have_next_page = false;
		public int last_id = 0;
		public List<EventDetailRecordModel> records = null;
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
		finish();
	}
	
	// 进入信息的动画切换
	public boolean event_i_from = false;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == back_btn)
		{
			mBackViewClick();
		}
		else if (v == photo_btn)
		{
			showDialog(DIALOG_YES_NO_LONG_MESSAGE);
		}
		else if (v == event_i)
		{
			event_i_from = true;
			Intent intent = new Intent();
			intent.setClass(this, EDEventInfoActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(UConstants.EVENT_DETAIL_KEY, (Serializable) mEventItemModel);
			intent.putExtras(bundle);
			
			this.startActivityForResult(intent, UConstants.EVENT_INFO_REQUESTCODE);
		}
		else if (v == header_middle_btn)
		{
			// 初始化跳转Intent 
			Intent intent = new Intent();
			intent.setClass(this, CEventDiscussActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(UConstants.EVENT_DETAIL_KEY, (Serializable) mEventItemModel);
			bundle.putBoolean("from_detail", true);
			intent.putExtras(bundle);
			
			// 执行动画
			animationOut();
			animationMove(intent);
		}
		else if (v == text_btn_comment)
		{
			event_i_from = true;
			Bundle bundle = new Bundle();
			
			bundle.putSerializable(UConstants.EVENT_DETAIL_KEY, (Serializable) mEventItemModel);
			
			Intent intent = new Intent();
			intent.setClass(this, CEventAlbumsCommentActivity.class);
			intent.putExtras(bundle);
			this.startActivity(intent);
		}
		else if (v == text_btn_share)
		{
			PShareAlertDialog.showAlert(this, new OnAlertSelectId() {
				
				@Override
				public void onClick(int whichButton) {
					
					// TODO Auto-generated method stub
					// email
					if (whichButton == 1)
					{
						Intent sharingIntent = new Intent(Intent.ACTION_SEND);
						sharingIntent.setType("text/html");

						sharingIntent.putExtra(Intent.EXTRA_TEXT, mEventItemModel.share_content + mEventItemModel.event_share_url);
						startActivity(sharingIntent);
					}// message
					else if (whichButton == 2)
					{
						Uri smsToUri = Uri.parse("smsto:");
						Intent mIntent = new Intent(android.content.Intent.ACTION_SENDTO, smsToUri);
						// 加内容进去
						if (mEventItemModel != null)
							mIntent.putExtra("sms_body", mEventItemModel.share_content + mEventItemModel.event_share_url);
						startActivity(mIntent);
					}// weixin
					else if (whichButton == 3)
					{
						WXWebpageObject webpage = new WXWebpageObject();
						webpage.webpageUrl = mEventItemModel.event_share_url;
						WXMediaMessage msg = new WXMediaMessage(webpage);
						msg.title = mEventItemModel.title;
						msg.description = mEventItemModel.share_content;
						Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.about_logo);
						msg.thumbData = UUtils.bmpToByteArray(thumb, true);
						
						SendMessageToWX.Req req = new SendMessageToWX.Req();
						req.transaction = buildTransaction("webpage");
						req.message = msg;
						api.sendReq(req);
					}// timeline
					else if (whichButton == 4)
					{
						WXWebpageObject webpage = new WXWebpageObject();
						webpage.webpageUrl = mEventItemModel.event_share_url;
						WXMediaMessage msg = new WXMediaMessage(webpage);
						msg.title = mEventItemModel.title;
						msg.description = mEventItemModel.share_content;
						Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.about_logo);
						msg.thumbData = UUtils.bmpToByteArray(thumb, true);
						
						SendMessageToWX.Req req = new SendMessageToWX.Req();
						req.transaction = buildTransaction("webpage");
						req.message = msg;
						req.scene = req.WXSceneTimeline;
						api.sendReq(req);
					}
				}
			});
		}
		else if (v == text_btn_zan)
		{
			if (mEventItemModel != null)
			{
				Map<String , String> params = new HashMap<String, String>();
				params.put("event_id", String.valueOf(mEventItemModel.event_id));
				if (mEventItemModel.zan)
				{
					params.put("method", "cancel_zan");
				}
				else
				{
					params.put("method", "zan");
				}
				
				mDataLoader.postData(UConstants.ZAN_EVENT_URL, params, this, new HDataListener() {
					
					@Override
					public void onFinish(String source) {
						// TODO Auto-generated method stub
						Gson gson = new Gson();
						
						try {
							TempLikeModel mTempLikeModel = gson.fromJson(source, new TypeToken<TempLikeModel>(){}.getType());
							
							if(mTempLikeModel != null && mTempLikeModel.result.equals("success"))
							{
								if (mEventItemModel.zan)
								{
									mEventItemModel.zan = false;
									mEventItemModel.zan_num--;
								}
								else {
									mEventItemModel.zan = true;
									mEventItemModel.zan_num++;
								}
							}
						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
						}
						
						displayBottomBtnView();
					}
					
					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						displayBottomBtnView();
					}
				});
			}
		}
		else if (v == text_btn_join)
		{
			if (mEventItemModel != null)
			{
				final FLoadingProgressBarFragment mLoadingProgressBarFragment = new FLoadingProgressBarFragment();
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				mLoadingProgressBarFragment.show(ft, "dialog");
				
				Map<String, String> params = new HashMap<String, String>();
				params.put("event_id", String.valueOf(mEventItemModel.event_id));
				params.put("method", "apply_join");
				
				mDataLoader.postData(UConstants.EVENT_DETAIL_JOIN_EVENT_URL, params, this, new HDataListener() {
					
					@Override
					public void onFinish(String source) {
						// TODO Auto-generated method stub
						mLoadingProgressBarFragment.dismiss();
						Gson gson = new Gson();
						try {
							TempJoinResultModel mTempJoinResultModel = gson.fromJson(source, new TypeToken<TempJoinResultModel>(){}.getType());
							
							if (mTempJoinResultModel != null && mTempJoinResultModel.result.equals("success"))
							{
								mEventItemModel.join = mTempJoinResultModel.join;
								displayBottomBtnView();
								initPicView(mEventItemModel);
								displayEventContent();
							}
							else if (mTempJoinResultModel != null) {
								Toast toast = Toast.makeText(EventDetailActivity.this, mTempJoinResultModel.message, Toast.LENGTH_SHORT);
								toast.show();
							}
						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast toast = Toast.makeText(EventDetailActivity.this, "服务器错误", Toast.LENGTH_SHORT);
							toast.show();
						}
					}
					
					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						mLoadingProgressBarFragment.dismiss();
						Toast toast = Toast.makeText(EventDetailActivity.this, "服务器错误", Toast.LENGTH_SHORT);
						toast.show();
					}
				});
			}
		}
		else if (v == text_btn_like)
		{
			if (mEventItemModel != null)
			{
				Map<String, String> params = new HashMap<String, String>();
				
				params.put("event_id", String.valueOf(mEventItemModel.event_id));
				params.put("method", "interest");
				mDataLoader.postData(UConstants.EVENT_DETAIL_INTEREST_URL, params, this, new HDataListener() {
					
					@Override
					public void onFinish(String source) {
						// TODO Auto-generated method stub
						
						try
						{
							JSONObject json = new JSONObject(source);
							if (json.optString("result", "").equals("success"))
							{
								if (!mEventItemModel.interest)
								{
									mEventItemModel.interest = true;
								}
								else {
									mEventItemModel.interest = false;
								}
							}
						}
						catch (JSONException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						displayBottomBtnView();
					}
					
					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						displayBottomBtnView();
					}
				});
			}
		}
	}
	
	class TempLikeModel
	{
		public String result = "";
		public String message = "";
	}
	
	
	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	private GridView mTopUserGridView;
	private EDEventDetailUserAdapter mEdEventDetailUserAdapter;
	
	private void initPicView(EventItemModel model)
	{
		mTopUserGridView = (GridView) findViewById(R.id.top_user_gridview);
		
		mEdEventDetailUserAdapter = new EDEventDetailUserAdapter(this, model);
		mTopUserGridView.setAdapter(mEdEventDetailUserAdapter);
		if (model.join == 3)
		{
			if (model.master != null)
			{
				if (model.master.user_id == UUtils.getSelfUserInfoModel(this).user_id || model.forward_invite == 1)
				{
					initEventUserList();
				}
			}
			else if (model.forward_invite == 1)
			{
				initEventUserList();
			}
		}
		getEventDetailUserData();
	}
	
	private void initEventUserList()
	{
		int fitWidth = UTools.UI.fitPixels(this, 94, 640);
		LayoutParams params = new LayoutParams(fitWidth, fitWidth);
		
		mTopUserGridView.setLayoutParams(params);
		mTopUserGridView.setColumnWidth(fitWidth);
		mTopUserGridView.setNumColumns(1);
		
		List<FriendsModel> mList = new ArrayList<FriendsModel>();
		
		FriendsModel model = new FriendsModel();
		
		mList.add(model);
		
		mEdEventDetailUserAdapter.refreshData(mList);
	}
	
	private void getEventDetailUserData()
	{
		if (mEventItemModel != null)
		{
			Map<String, String> params = new HashMap<String, String>();
			
			params.put("head_width", String.valueOf(UDisplayWidth.getSmallHeadPicWidth(this)));
			params.put("event_id", String.valueOf(mEventItemModel.event_id));
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
				else {
					UUtils.showNetErrorToast(EventDetailActivity.this);
				}
			} catch (JsonSyntaxException e) {
				// TODO Auto-generated catch block
				UUtils.showNetErrorToast(EventDetailActivity.this);
			}
		}

		@Override
		public void onFail(String msg) {
			// TODO Auto-generated method stub
			UUtils.showNetErrorToast(EventDetailActivity.this);
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
				final int emotionSize = list.size() + 1;
				
				int fitWidth = UTools.UI.fitPixels(this, 94, 640);
				LayoutParams params = new LayoutParams(emotionSize * fitWidth + emotionSize, fitWidth);
				
				mTopUserGridView.setLayoutParams(params);
				mTopUserGridView.setColumnWidth(fitWidth);
				mTopUserGridView.setNumColumns(emotionSize);
				
				mEdEventDetailUserAdapter.addData(list);
			}
		}
		
		rightAnimationIn();
	}
	
	class TempUserModel
	{
		public String result = "";
		public String message = "";
		
		public List<FriendsModel> userlist = null;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (event_i_from)
		{
			event_i_from = false;
			overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out_no);
		}
		else {
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) {
		case RESULT_OK:
			if (requestCode == UConstants.DISSCUSS_REQUESTCODE)
			{
				animationBack();
			}
			else if (requestCode == UConstants.EVENT_INFO_REQUESTCODE)
			{
				
				Bundle bundle = data.getExtras();
				
				if (bundle.getString("from_where").equals("quit"))
				{
					mEventItemModel.join = bundle.getInt("join", 0);
				}
				else {
					mEventItemModel = (EventItemModel) bundle.getSerializable(UConstants.EVENT_DETAIL_KEY);
				}
				displayEventContent();
				displayBottomBtnView();
				initPicView(mEventItemModel);
				
			}
			else if (requestCode == UConstants.CAPUTRE)
			{
				// Load up the image's dimensions not the image itself
				
				String[] allPath = new String[1];

				allPath[0] = urlTempPic;
				Intent i = new Intent(EventDetailActivity.this,UploadPicActivity.class)
							.putExtra("all_path", allPath)
							.putExtra("event_id", mEventItemModel.event_id);
				
				startActivity(i);
			}
			break;

		default:
			break;
		}
	}
	
	class TempJoinResultModel
	{
		String result = "";
		String message = "";
		int join = 0;
	}
	
	// 获取活动信息，和发起人信息
	private void loadData(String eventId)
	{
		Map<String,String> params = new HashMap<String,String>();
		params.put("event_id", eventId);
		params.put("head_width", String.valueOf(UDisplayWidth.getLargeHeadPicWidth(this)));
		params.put("event_pic_width", String.valueOf(UDisplayWidth.getEventDetailPicWidth(this)));
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
							if (mEventItemModel == null)
							{
								mEventItemModel = temp.event;
								displayEventContent();
								initPicView(mEventItemModel);
								displayBottomBtnView();
								if(mEventItemModel.event_pic==null || mEventItemModel.event_pic.equals(""))
						        {
									bottom_bg_image.setImageResource(getPosterId(mEventItemModel.event_type_id));
						        }
						        else
						        {
						        	String event_pic_1 = mEventItemModel.event_pic;
						        	String replace_s = "/" + UDisplayWidth.getEventDetailPicWidth(EventDetailActivity.this) +"/";
						        	String replace_with = "/" + UDisplayWidth.getPosterPicWidth(EventDetailActivity.this) +"/";
						        	
						        	fb.display(bottom_bg_image, event_pic_1.replace(replace_s, replace_with));
						        }
								
								if (mEventItemModel.master.user_id == UUtils.getSelfUserInfoModel(EventDetailActivity.this).user_id)
								{
									mEventDetailAdapter.setIsSelfCreate(true);
								}
								else {
									mEventDetailAdapter.setIsSelfCreate(false);
								}
							}
							else {
								mEventItemModel = temp.event;
								displayEventContent();
								initPicView(mEventItemModel);
								displayBottomBtnView();
							}
						}
					}
					else if (temp != null)
					{
						Toast toast = Toast.makeText(EventDetailActivity.this, temp.message, Toast.LENGTH_SHORT);
						toast.show();
					}
					else {
						UUtils.showNetErrorToast(EventDetailActivity.this);
					}
				} catch (JsonSyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					UUtils.showNetErrorToast(EventDetailActivity.this);
				}
			}
			
			@Override
			public void onFail(String msg)
			{
				UUtils.showNetErrorToast(EventDetailActivity.this);
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
	
	
	private IWXAPI api;

	public void regToWx(Context context)
	{
		api = WXAPIFactory.createWXAPI(context, UConstants.WX_APP_ID);
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	
	// 选择照片上传
	
	private static final int DIALOG_YES_NO_LONG_MESSAGE = 1;
	
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case DIALOG_YES_NO_LONG_MESSAGE:
            return new AlertDialog.Builder(EventDetailActivity.this,AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("提示")
                .setMessage("选择需要上传的照片")
                .setPositiveButton("从相册", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
    
                        /* User clicked OK so do some stuff */
                    	startPhotoAlbum();
                    }
                })
                .setNeutralButton("从相机", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked Something so do some stuff */
                    	startCamera();
                    }
                })
                .create();
		}
		
		return null;
	}
	
	/**
	 * @author liananse
	 * 2013-7-21
	 */
	private void startPhotoAlbum()
	{
		Intent i = new Intent(this,CustomGalleryActivity.class).putExtra("event_id", mEventItemModel.event_id);
		startActivity(i);
	}
	
	private String urlTempPic = "";
	
	private void startCamera()
	{
		Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);  
        
        //这里我们插入一条数据，ContentValues是我们希望这条记录被创建时包含的数据信息  
        //这些数据的名称已经作为常量在MediaStore.Images.Media中,有的存储在MediaStore.MediaColumn中了  
        //ContentValues values = new ContentValues();  
        ContentValues values = new ContentValues(3);  
        values.put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, "");  
        values.put(android.provider.MediaStore.Images.Media.DESCRIPTION, "");  
        values.put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/jpeg");  
        Uri imageFilePath = EventDetailActivity.this.getContentResolver().insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);  
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFilePath); //这样就将文件的存储方式和uri指定到了Camera应用中  
        
        urlTempPic = getFilePathByContentResolver(EventDetailActivity.this, imageFilePath);
        //由于我们需要调用完Camera后，可以返回Camera获取到的图片，  
        //所以，我们使用startActivityForResult来启动Camera                      
        startActivityForResult(intent, UConstants.CAPUTRE);  
	}
	
	private String getFilePathByContentResolver(Context context, Uri uri)
	{ 
		if (null == uri)
		{ 
			return null; 
		} 
		Cursor c = context.getContentResolver().query(uri, null, null, null, null); 
		String filePath = null; 
		if (null == c) { 
		throw new IllegalArgumentException( 
				"Query on " + uri + " returns null result."); 
		} 
		try { 
			if ((c.getCount() != 1) || !c.moveToFirst()) { 
			} else { 
				filePath = c.getString( 
				c.getColumnIndexOrThrow(MediaColumns.DATA)); 
			} 
		} 
		finally { 
			c.close(); 
		} 
		return filePath; 
	} 
	
	
	////////////////////////////////////////////////
	//动画
	////////////////////////////////////////////////
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.gc();
	}

	private boolean oncreate = true; // 每次进入时为true，后变为false
	/**
	 * 执行出现动画
	 */
	private void animationIn()
	{
		// 从顶部出现动画
		Animation topInAnimation = new TranslateAnimation(0F,0F,-200, 0F);
		topInAnimation.setFillAfter(true);
		topInAnimation.setDuration(300);
		
		header_title_view.startAnimation(topInAnimation);
	}
	
	private void bottomAnimationIn()
	{
		// 从底部出现动画
		Animation bottomInAnimation = new TranslateAnimation(0F,0F,this.getResources().getDisplayMetrics().heightPixels, 0F);
		bottomInAnimation.setFillAfter(true);
		bottomInAnimation.setDuration(300);
		
		bottomInAnimation.setAnimationListener(new Animation.AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				bottom_content_view.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				header_middle_btn.clearAnimation();
				header_middle_btn.setClickable(true);
			}
		});

		lv.startAnimation(bottomInAnimation);
		bottom_content_view.startAnimation(bottomInAnimation);
		bottom_btn_view.startAnimation(bottomInAnimation);
	}
	
	// 右侧进入动画
	private void rightAnimationIn()
	{
		top_content_view.setVisibility(View.VISIBLE);
		Animation rightInAnimation = new TranslateAnimation(this.getResources().getDisplayMetrics().widthPixels,0F,0F, 0F);
		rightInAnimation.setFillAfter(true);
		rightInAnimation.setDuration(300);
		top_content_view.startAnimation(rightInAnimation);
		
		rightInAnimation.setAnimationListener(new Animation.AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				top_content_view.setAlpha(1);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				if (oncreate)
				{
					oncreate = false;
					initPicData(true);
				}
			}
		});
	}
	
	private void animationOut()
	{
		// 从顶部消失动画
		Animation topOutAnimation = new TranslateAnimation(0F,0F,0F, -200);
		topOutAnimation.setFillAfter(true);
		topOutAnimation.setDuration(300);
		// 从底部消失动画
		Animation bottomOutAnimation = new TranslateAnimation(0F,0F,0F,this.getResources().getDisplayMetrics().heightPixels);
		bottomOutAnimation.setFillAfter(true);
		bottomOutAnimation.setDuration(300);
		// 从右侧消失动画
		Animation rightOutAnimation = new TranslateAnimation(0F,this.getResources().getDisplayMetrics().widthPixels,0F, 0F);
		rightOutAnimation.setFillAfter(true);
		rightOutAnimation.setDuration(300);
		
		rightOutAnimation.setAnimationListener(new Animation.AnimationListener() {
			
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
				top_content_view.clearAnimation();
				top_content_view.setAlpha(0);
			}
		});
		
		header_title_view.startAnimation(topOutAnimation);
		top_content_view.startAnimation(rightOutAnimation);
		lv.startAnimation(bottomOutAnimation);
		bottom_content_view.startAnimation(bottomOutAnimation);
		bottom_btn_view.startAnimation(bottomOutAnimation);
	}
	
	private void animationMove(final Intent intent)
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
        scaleAnimation.setAnimationListener(new Animation.AnimationListener(){
                public void onAnimationEnd(Animation arg0) {
                	EventDetailActivity.this.startActivityForResult(intent, UConstants.DISSCUSS_REQUESTCODE);
                	overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                public void onAnimationRepeat(Animation arg0) {
                }
                public void onAnimationStart(Animation arg0) {
                	blurred_middle_image.setVisibility(View.VISIBLE);
                	blurred_image_ll.setVisibility(View.GONE);
                }
        });
        bottom_bg_image.startAnimation(animationSet);
        blurred_middle_image.startAnimation(animationSet);
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
        scaleAnimation.setAnimationListener(new Animation.AnimationListener(){
                public void onAnimationEnd(Animation arg0) {
                	animationIn();
                	rightAnimationIn();
                	bottomAnimationIn();
                	blurred_middle_image.clearAnimation();
                	blurred_middle_image.setVisibility(View.INVISIBLE);
                	blurred_image_ll.setVisibility(View.VISIBLE);
                }
                public void onAnimationRepeat(Animation arg0) {
                }
                public void onAnimationStart(Animation arg0) {
                }
        });
        bottom_bg_image.startAnimation(animationSet);
        blurred_middle_image.startAnimation(animationSet);
        
	}
}
