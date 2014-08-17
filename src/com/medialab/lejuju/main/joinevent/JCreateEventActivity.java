package com.medialab.lejuju.main.joinevent;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.db.DDBOpenHelper;
import com.medialab.lejuju.fragment.FLoadingProgressBarFragment;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.http.HHttpDataLoader.HDataListener;
import com.medialab.lejuju.model.EventAddressModel;
import com.medialab.lejuju.model.EventItemModel;
import com.medialab.lejuju.model.EventTimeModel;
import com.medialab.lejuju.model.TrendItemModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UDisplayWidth;
import com.medialab.lejuju.util.UTimeShown;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.util.UUtils;
import com.medialab.lejuju.views.NumberPickerPopWindow;
import com.medialab.lejuju.views.XScrollLayout;

public class JCreateEventActivity extends MBaseActivity implements OnClickListener{

	private int currentScreenIndex = 0;
	FinalBitmap fb;
	
	// 从上个页面传来的活动类型id
	private int event_type_id = 0;
	private String event_poster_path = "";
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_create_event);
		fb = FinalBitmap.create(this);
		
		event_type_id = getIntent().getIntExtra("event_type_id", 0);
		event_poster_path = getIntent().getStringExtra("event_poster_path");
		
		initHeaderBar();
		initScrollLayout();
		initStepOneScreen();
		initStepTwoScreen();
		initStepThreeScreen();
	}
	
	// 初始化header bar
	private View mBackView;
	private View mOkView;
	
	private ImageView mBackImageView;
	private ImageView mOkImageView;
	
	private void initHeaderBar()
	{
		mBackView = findViewById(R.id.back_btn);
		mOkView = findViewById(R.id.ok_btn);
		
		mBackImageView = (ImageView) findViewById(R.id.back_btn_center);
		mOkImageView = (ImageView) findViewById(R.id.ok_btn_center);
		
		UTools.UI.fitViewByWidth(this, mBackView, 130, 62, 640);
		UTools.UI.fitViewByWidth(this, mOkView, 130, 62, 640);
		
		UTools.UI.fitViewByWidth(this, mBackImageView, 18, 32, 640);
		
		mOkImageView.setImageResource(R.drawable.action_bar_next);
		
		UTools.UI.fitViewByWidth(this, mOkImageView, 42, 32, 640);
		
		mBackView.setOnClickListener(this);
		mOkView.setOnClickListener(this);
	}

	private XScrollLayout mXScrollLayout;
	
	private void initScrollLayout()
	{
		mXScrollLayout = (XScrollLayout) findViewById(R.id.ScrollLayout);
		mXScrollLayout.setMoveType(false);
	}
	
	// 初始化Step 1
	
	private EditText edit_event_title_et;
	private ToggleButton allow_invite_friends_toggle_btn;
	private ToggleButton can_join_event_free_toggle_btn;
	private View event_open_state_view;
	
	private TextView event_open_state_tv;
	
	private int open_state = 1; // 是否公开活动信息（0表示私密活动，1表示只公开给朋友们，2完全公开） 默认公开给朋友们
	private int forward_invite = 1; // 是否同意朋友邀请朋友（1表示是，0表示否）
	private int need_audit = 1; // 是否需要审核  （1表示是，0表示否）注意与客户端界面显示文字的意思相反
	
	private TextView event_open_state_introduce_tv;
	private void initStepOneScreen()
	{
		edit_event_title_et = (EditText) findViewById(R.id.edit_event_title_et);
		allow_invite_friends_toggle_btn = (ToggleButton) findViewById(R.id.allow_invite_friends_toggle_btn);
		can_join_event_free_toggle_btn = (ToggleButton) findViewById(R.id.can_join_event_free_toggle_btn);
		event_open_state_view = findViewById(R.id.event_open_state_view);
		event_open_state_introduce_tv = (TextView) findViewById(R.id.event_open_state_introduce_tv);
		event_open_state_tv = (TextView) findViewById(R.id.event_open_state_tv);
		UTools.UI.fitViewByWidth(this, allow_invite_friends_toggle_btn, 120, 40, 640);
		UTools.UI.fitViewByWidth(this, can_join_event_free_toggle_btn, 120, 40, 640);
		
		event_open_state_view.setOnClickListener(this);
		
		event_open_state_tv.setText("朋友公开"); // 默认公开给朋友们
		event_open_state_introduce_tv.setText("朋友公开，你的朋友都可以看到并参加该活动");
	}
	
	// 初始化 Step 2
	
	private View event_start_time_row;
	private TextView event_start_time_tv;
	
	private View event_end_time_row;
	private TextView event_end_time_tv;
	
	private EditText edit_event_address_et;
	
	private ImageView map_image_view;
	
	private View default_display_view;
	
	private AddressTextChange tempChange = new AddressTextChange();
	
	private void initStepTwoScreen()
	{
		event_start_time_row = findViewById(R.id.event_start_time_row);
		event_start_time_tv = (TextView) findViewById(R.id.event_start_time_tv);
		
		event_end_time_row = findViewById(R.id.event_end_time_row);
		event_end_time_tv = (TextView) findViewById(R.id.event_end_time_tv);
		
		edit_event_address_et = (EditText) findViewById(R.id.edit_event_address_et);
		
		map_image_view = (ImageView) findViewById(R.id.map_image_view);
		
		default_display_view = findViewById(R.id.default_display_view);
		
		edit_event_address_et.addTextChangedListener(tempChange);
		
		map_image_view.setOnClickListener(this);
		default_display_view.setOnClickListener(this);
		
		event_end_time_row.setOnClickListener(this);
		event_start_time_row.setOnClickListener(this);
	}
	
	// 初始化 Step 3
	private EditText edit_event_introduce;
	
	private void initStepThreeScreen()
	{
		edit_event_introduce = (EditText) findViewById(R.id.edit_event_introduce);
	}
	
	NumberPickerPopWindow mNumberPickerPopWindow;
	
	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mOkView)
		{
			// 如果在编辑活动Step 1 点击下一步按键
			if (currentScreenIndex == 0)
			{
				// 是否可以跳转到step2
				boolean canStep = true;
				
				if (canStep && edit_event_title_et.getText().toString().trim().isEmpty())
				{
					canStep = false;
					// toast
					Toast toast = Toast.makeText(this, "活动主题不能为空", Toast.LENGTH_SHORT);
					toast.show();
				}
				
				if (canStep && event_open_state_tv.getText().toString().trim().isEmpty())
				{
					canStep = false;
					Toast toast = Toast.makeText(this, "隐私设置不能为空", Toast.LENGTH_SHORT);
					toast.show();
				}
				
				// 跳转到第二步
				if (canStep)
				{
					if (allow_invite_friends_toggle_btn.isChecked())
					{
						forward_invite = 1;
					}
					else {
						forward_invite = 0;
					}
					
					if (can_join_event_free_toggle_btn.isChecked())
					{
						need_audit = 0;
					}
					else {
						need_audit = 1;
					}
					
					// 跳转到Step 2 同时设置current screen index 为Step 2
					mXScrollLayout.snapToScreen(1);
					currentScreenIndex = 1;
					
					mOkImageView.setImageResource(R.drawable.action_bar_next);
					UTools.UI.fitViewByWidth(JCreateEventActivity.this, mOkImageView, 42, 32, 640);
				}
				// 
				
			}
			// 如果在编辑活动Step 2 点击下一步按键
			else if (currentScreenIndex == 1)
			{
				boolean canStep = true;
				
				if (canStep && event_start_time_tv.getText().toString().trim().isEmpty())
				{
					canStep = false;
					// toast
					Toast toast = Toast.makeText(this, "开始时间不能为空", Toast.LENGTH_SHORT);
					toast.show();
				}
				
				if (canStep && event_end_time_tv.getText().toString().trim().isEmpty())
				{
					canStep = false;
					Toast.makeText(this, "结束时间不能为空", Toast.LENGTH_SHORT).show();
				}
				
				if (canStep && !logicalBeginEndTime(event_start_time_tv.getText().toString().trim(), event_end_time_tv.getText().toString().trim()))
				{
					canStep = false;
					Toast.makeText(this, "时间不合法，请重新选择", Toast.LENGTH_SHORT).show();
				}
				
				if (canStep && mEventAddressModel == null)
				{
					canStep = false;
					Toast.makeText(this, "地点不能为空", Toast.LENGTH_SHORT).show();
				}
				
				if (canStep)
				{
					// 跳转到Step 3 同时设置current screen index 为Step 3
					mXScrollLayout.snapToScreen(2);
					currentScreenIndex = 2;
					
					mOkImageView.setImageResource(R.drawable.action_bar_ok);
					UTools.UI.fitViewByWidth(JCreateEventActivity.this, mOkImageView, 45, 28, 640);
				}
				//
			}
			// 如果在编辑活动Step 3 点击完成按键 则表示活动编辑完成 请求服务器完成活动编辑
			else if (currentScreenIndex == 2)
			{
				boolean canCreate = true;
				
				if (canCreate && edit_event_introduce.getText().toString().trim().isEmpty())
				{
					canCreate = false;
					Toast.makeText(this, "活动简介不能为空", Toast.LENGTH_SHORT).show();
				}
				
				if (canCreate)
				{
					final FLoadingProgressBarFragment mLoadingProgressBarFragment = new FLoadingProgressBarFragment();
					FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
					mLoadingProgressBarFragment.show(ft, "dialog");
					
					List<EventTimeModel> mEventTimeModels = new ArrayList<EventTimeModel>();
					
					EventTimeModel mEventTimeModel = new EventTimeModel(event_start_time_tv.getText().toString().trim(), event_end_time_tv.getText().toString().trim());
					
					mEventTimeModels.add(mEventTimeModel);
					
					List<EventAddressModel> mEventAddressModels = new ArrayList<EventAddressModel>();
					
					mEventAddressModels.add(mEventAddressModel);
					
					// params
					Map<String, String> params = new HashMap<String, String>();
					
					// 模拟数据
					
					Gson gson = new Gson();
					String allTimeString = gson.toJson(mEventTimeModels);
					
					String allAddresString = gson.toJson(mEventAddressModels);
					
					
					params.put("head_width", String.valueOf(UDisplayWidth.getSmallHeadPicWidth(JCreateEventActivity.this)));
					params.put("event_pic_width", String.valueOf(UDisplayWidth.getPosterPicWidth(JCreateEventActivity.this)));
					
					params.put("title", edit_event_title_et.getText().toString().trim());
					params.put("introduce", edit_event_introduce.getText().toString().trim());
					params.put("all_time", allTimeString);
					params.put("all_address",  allAddresString);
					params.put("public_info", String.valueOf(open_state));
					params.put("forward_invite", String.valueOf(forward_invite));
					params.put("event_type_id", String.valueOf(event_type_id));
					params.put("need_audit", String.valueOf(need_audit));
					
					if (!event_poster_path.equals(""))
					{
						params.put("image", "image:" + event_poster_path);
					}
					else
					{
						params.put("image", "");
					}
					//
					mDataLoader.postData(UConstants.EDIT_EVENT_URL, params, this, new HDataListener() {
						
						@Override
						public void onFinish(String source) {
							// TODO Auto-generated method stub
							Gson gson = new Gson();
							TempModel mTempModel = gson.fromJson(source, new TypeToken<TempModel>(){}.getType());
							
							if (mTempModel != null && mTempModel.result.equals("success"))
							{
								// 将新创建的活动插入数据库
								List<EventItemModel> list = new ArrayList<EventItemModel>();
								
								mTempModel.event.master = UUtils.selfUserInfoModelToFriendsModel(JCreateEventActivity.this);
								
								List<TrendItemModel> trendItemModels  = new ArrayList<TrendItemModel>();
								
								TrendItemModel mTrendItemModel = new TrendItemModel();
								mTrendItemModel.org_user = UUtils.selfUserInfoModelToFriendsModel(JCreateEventActivity.this);
								mTrendItemModel.event_id = mTempModel.event.event_id;
								mTrendItemModel.type = 2;
								mTrendItemModel.content = UUtils.selfUserInfoModelToFriendsModel(JCreateEventActivity.this).nick_name + "创建了" + mTempModel.event.title + "活动";
								mTrendItemModel.show_time = 1;
								SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								mTrendItemModel.add_time = df.format(new Date());
								
								DDBOpenHelper mDdbOpenHelper = DDBOpenHelper.getInstance(JCreateEventActivity.this);
								TrendItemModel mTopTrendItemModel = mDdbOpenHelper.getTrendsMaxTimeByEventID(mTempModel.event.event_id);
								
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
								trendItemModels.add(mTrendItemModel);
								
								mDdbOpenHelper.insertTrendsModel(mTrendItemModel);
								
								mTempModel.event.trends = trendItemModels;
								list.add(mTempModel.event);
								mDdbOpenHelper.insertAttendEventModel(list);
								
								// dismiss the progressbar
								mLoadingProgressBarFragment.dismiss();
								
								// 跳转至打开通讯录页面
								Intent intent = new Intent();
								intent.setClass(JCreateEventActivity.this, JInviteFriendsActivity.class);
								Bundle bundle = new Bundle();
								bundle.putSerializable(UConstants.EVENT_DETAIL_KEY, (Serializable) mTempModel.event);
								bundle.putString("from_where", "from_create_event");
								intent.putExtras(bundle);
								JCreateEventActivity.this.startActivity(intent);
								JCreateEventActivity.this.finish();
							}
							else if (mTempModel != null && mTempModel.result.equals("fail"))
							{
								// dismiss the progressbar
								mLoadingProgressBarFragment.dismiss();
								
								Toast toast = Toast.makeText(JCreateEventActivity.this, mTempModel.message, Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.TOP, 0, 0);
								toast.show();
							}
							
							
						}
						
						@Override
						public void onFail(String msg) {
							// TODO Auto-generated method stub
							mLoadingProgressBarFragment.dismiss();
						}
					});
				}
			}
			
		}
		else if (v == mBackView)
		{
			// 如果在编辑活动Step 1 点击返回按键 则表示退出当前编辑活动页面
			if (currentScreenIndex == 0)
			{
				showDialog(DIALOG_YES_NO_LONG_MESSAGE);
			}
			// 如果在编辑活动Step 2 点击返回按键
			else if (currentScreenIndex == 1)
			{
				// 跳转到Step 1 同时设置current screen index 为Step 1
				mXScrollLayout.snapToScreen(0);
				currentScreenIndex = 0;
				
				mOkImageView.setImageResource(R.drawable.action_bar_next);
				UTools.UI.fitViewByWidth(JCreateEventActivity.this, mOkImageView, 42, 32, 640);
			}
			// 如果在编辑活动Step 3 点击返回按键
			else if (currentScreenIndex == 2)
			{
				// 跳转到Step 2 同时设置current screen index 为Step 2
				mXScrollLayout.snapToScreen(1);
				currentScreenIndex = 1;
				
				mOkImageView.setImageResource(R.drawable.action_bar_next);
				UTools.UI.fitViewByWidth(JCreateEventActivity.this, mOkImageView, 42, 32, 640);
			}
		}
		// 选择隐私类型
		else if (v == event_open_state_view)
		{
			openStateChoose();
		}
		// 点击地图图片
		else if (v == map_image_view) {
			
			Intent intent = new Intent();
			intent.setClass(this, JSelectLocationFromMapActivity.class);
			
			this.startActivityForResult(intent, UConstants.SELECT_EVENT_LOCATION_REQUESTCODE);
		}
		// 点击地图选择默认view
		else if (v == default_display_view)
		{
			Intent intent = new Intent();
			intent.setClass(this, JSelectLocationFromMapActivity.class);
			
			this.startActivityForResult(intent, UConstants.SELECT_EVENT_LOCATION_REQUESTCODE);
		}
		// 点击选择结束时间
		else if (v == event_end_time_row)
		{
			mNumberPickerPopWindow = new NumberPickerPopWindow(this,endTimeClickListener);
			mNumberPickerPopWindow.setAnimationStyle(R.style.AnimBottom);
			mNumberPickerPopWindow.showAtLocation(this.findViewById(R.id.root),Gravity.BOTTOM, 0, 0);
		}
		// 点击选择开始时间
		else if (v == event_start_time_row)
		{
			mNumberPickerPopWindow = new NumberPickerPopWindow(this,beginTimeClickListener);
			mNumberPickerPopWindow.setAnimationStyle(R.style.AnimBottom);
			mNumberPickerPopWindow.showAtLocation(this.findViewById(R.id.root),Gravity.BOTTOM, 0, 0);
		}
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			// 如果在编辑活动Step 1 点击返回按键 则表示退出当前编辑活动页面
			if (currentScreenIndex == 0)
			{
				showDialog(DIALOG_YES_NO_LONG_MESSAGE);
			}
			// 如果在编辑活动Step 2 点击返回按键
			else if (currentScreenIndex == 1)
			{
				// 跳转到Step 1 同时设置current screen index 为Step 1
				mXScrollLayout.snapToScreen(0);
				currentScreenIndex = 0;
				mOkImageView.setImageResource(R.drawable.action_bar_next);
				UTools.UI.fitViewByWidth(JCreateEventActivity.this, mOkImageView, 42, 32, 640);
			}
			// 如果在编辑活动Step 3 点击返回按键
			else if (currentScreenIndex == 2)
			{
				// 跳转到Step 2 同时设置current screen index 为Step 2
				mXScrollLayout.snapToScreen(1);
				currentScreenIndex = 1;
				mOkImageView.setImageResource(R.drawable.action_bar_next);
				UTools.UI.fitViewByWidth(JCreateEventActivity.this, mOkImageView, 42, 32, 640);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) {
		case RESULT_OK:
			if (requestCode == UConstants.SELECT_EVENT_LOCATION_REQUESTCODE)
			{
				Bundle bundle = data.getExtras();
				String locationName = bundle.getString("location_name");
				// 维度
				int location_latitude = bundle.getInt("location_latitude");
				// 经度
				int location_longitude = bundle.getInt("location_longitude");
				
				double x = location_latitude / 1e6;
				double y = location_longitude / 1e6;
				
				edit_event_address_et.setText(locationName);
				if (mEventAddressModel == null)
				{
					EventAddressModel tempAddressModel = new EventAddressModel(locationName, x, y);
					
					mEventAddressModel = tempAddressModel;
				}
				else {
					mEventAddressModel.address = locationName;
					mEventAddressModel.x = x;
					mEventAddressModel.y = y;
				}
				
				
				String map_image_url = "http://api.map.baidu.com/staticimage?center="+y+","+x+"&width=680&height=400&zoom=18&markers="+y+","+x;
				
				fb.display(map_image_view, map_image_url);
				
				map_image_view.setVisibility(View.VISIBLE);
				default_display_view.setVisibility(View.GONE);
			}
			break;

		default:
			break;
		}
	}
	
	private EventAddressModel mEventAddressModel = null;
	
	class AddressTextChange implements TextWatcher
	{
		public AddressTextChange()
		{
			
		}
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			String newKeyword = s.toString();

			if (newKeyword.isEmpty())
			{
				mEventAddressModel = null;
			}
			else
			{
				if (mEventAddressModel == null)
				{
					EventAddressModel tempAddressModel = new EventAddressModel(newKeyword, 0, 0);
					
					mEventAddressModel = tempAddressModel;
				}
				else {
					mEventAddressModel.address = newKeyword;
					mEventAddressModel.x = Double.parseDouble(UTools.Storage.getSharedPreferences(JCreateEventActivity.this, UConstants.BASE_PREFS_NAME).getString(UConstants.LOCATION_LATITUDE, "0.0"));
					mEventAddressModel.y = Double.parseDouble(UTools.Storage.getSharedPreferences(JCreateEventActivity.this, UConstants.BASE_PREFS_NAME).getString(UConstants.LOCATION_LONGITUDE, "0.0"));
				}
			}
		}
		
	}
	
	
	/**
	 * 判断开始时间和结束时间是否合法
	 * @author liananse
	 * @param beginTimeString
	 * @param endTimeString
	 * @return
	 * 2013-7-19
	 */
	private boolean logicalBeginEndTime(String beginTimeString, String endTimeString)
	{
		int bYear = Integer.parseInt(beginTimeString.split("-")[0]);
		int bMonth = Integer.parseInt(beginTimeString.split("-")[1]);
		int bDay = Integer.parseInt(beginTimeString.split("-")[2].split(" ")[0]);
		int bHour = Integer.parseInt(beginTimeString.split("-")[2].split(" ")[1].split(":")[0]);
		int bMinute = Integer.parseInt(beginTimeString.split("-")[2].split(" ")[1].split(":")[1]);
		
		int eYear = Integer.parseInt(endTimeString.split("-")[0]);
		int eMonth = Integer.parseInt(endTimeString.split("-")[1]);
		int eDay = Integer.parseInt(endTimeString.split("-")[2].split(" ")[0]);
		int eHour = Integer.parseInt(endTimeString.split("-")[2].split(" ")[1].split(":")[0]);
		int eMinute = Integer.parseInt(endTimeString.split("-")[2].split(" ")[1].split(":")[1]);
		
		if (eYear > bYear)
		{
			return true;
		}
		else if (eYear == bYear)
		{
			if (eMonth > bMonth)
			{
				return true;
			}
			else if (eMonth == bMonth)
			{
				if (eDay > bDay)
				{
					return true;
				}
				else if (eDay == bDay)
				{
					if (eHour > bHour)
					{
						return true;
					}
					else if(eHour == bHour)
					{
						if(eMinute > bMinute)
						{
							return true;
						}
						else {
							return false;
						}
					}
					else {
						return false;
					}
				}
				else {
					return false;
				}
			}
			else
			{
				return false;
			}
		}
		else 
		{
			return false;
		}
	}
	
	public void openStateChoose()
	{
		new AlertDialog.Builder(this).setItems(R.array.open_state_choose, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				switch (which)
				{
					case 0:
						event_open_state_tv.setText("邀请参加");
						event_open_state_introduce_tv.setText("邀请参加，只能通过邀请才可以看到并参加该活动");
						open_state = 0;
						break;
					case 1:
						event_open_state_tv.setText("朋友公开");
						event_open_state_introduce_tv.setText("朋友公开，你的朋友都可以看到并参加该活动");
						open_state = 1;
						break;
					case 2:
						event_open_state_tv.setText("完全公开");
						event_open_state_introduce_tv.setText("完全公开，附近的人能看到你的活动信息");
						open_state = 2;
						break;
				}
			}

		}).show();
	}
	
	OnClickListener beginTimeClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			mNumberPickerPopWindow.dismiss();
			String xString = (String) v.getTag();
			event_start_time_tv.setText(xString);
		}
	};
	
	OnClickListener endTimeClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			mNumberPickerPopWindow.dismiss();
			String xString = (String) v.getTag();
			event_end_time_tv.setText(xString);
		}
	};
	
	/**
	 * @author liananse
	 * 2013.07.07
	 */
	class TempModel
	{
		public String result;
		public String message;
		public String invite_code;
		public String event_url;
		public EventItemModel event;
	}


	private static final int DIALOG_YES_NO_LONG_MESSAGE = 1;
	
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case DIALOG_YES_NO_LONG_MESSAGE:
            return new AlertDialog.Builder(JCreateEventActivity.this,AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("提示")
                .setMessage("确定要取消编辑活动吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
    
                        /* User clicked OK so do some stuff */
                    	finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked Cancel so do some stuff */
                    	dialog.cancel();
                    }
                })
                .create();
		}
		
		return null;
	}
	
	
}
