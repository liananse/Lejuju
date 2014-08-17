package com.medialab.lejuju.main.eventdetail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.db.DDBOpenHelper;
import com.medialab.lejuju.fragment.FLoadingProgressBarFragment;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.http.HHttpDataLoader.HDataListener;
import com.medialab.lejuju.main.joinevent.JSelectLocationFromMapActivity;
import com.medialab.lejuju.model.EventAddressModel;
import com.medialab.lejuju.model.EventItemModel;
import com.medialab.lejuju.model.EventTimeModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTimeShown;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.util.UUtils;
import com.medialab.lejuju.views.NumberPickerPopWindow;

public class EDEventInfoActivity extends MBaseActivity implements OnClickListener{

	FinalBitmap fb;
	private Intent mIntent;
	private EventItemModel mEventItemModel = null;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_event_info);
		mIntent = getIntent();
		mEventItemModel = (EventItemModel) getIntent().getSerializableExtra(UConstants.EVENT_DETAIL_KEY);
		fb = FinalBitmap.create(this);
		initHeaderBar();
		initContentView();
		initData();
	}
	
	// 初始化header bar
	private View mBackView;
	private View mOkView;
	
	private ImageView mBackImageView;
	private ImageView mOkImageView;
	
	private View mOkLine;
	private void initHeaderBar()
	{
		mBackView = findViewById(R.id.back_btn);
		mOkView = findViewById(R.id.ok_btn);
		
		mBackImageView = (ImageView) findViewById(R.id.back_btn_center);
		mOkImageView = (ImageView) findViewById(R.id.ok_btn_center);
		
		UTools.UI.fitViewByWidth(this, mBackView, 130, 62, 640);
		UTools.UI.fitViewByWidth(this, mOkView, 130, 62, 640);
		
		UTools.UI.fitViewByWidth(this, mBackImageView, 18, 32, 640);
		UTools.UI.fitViewByWidth(this, mOkImageView, 45, 28, 640);
		
		mOkLine = findViewById(R.id.ok_left_line);
		mBackView.setOnClickListener(this);
		mOkView.setOnClickListener(this);
	}
	
	private ToggleButton allow_invite_friends_toggle_btn;
	private ToggleButton can_join_event_free_toggle_btn;
	private ToggleButton open_invite_code_toggle_btn;
	
	private EditText event_info_title_tv;
	private ImageView map_imageImageView;
	private TextView event_open_state_introduce_tv;
	private TextView event_open_state_tv;
	private TextView event_start_time_tv;
	private TextView event_end_time_tv;
	private TextView invite_code_tv;
	private EditText event_introduce;
	
	private Button report_event_btn;
	private Button exit_event_btn;
	private View event_open_state_view;
	
	private int open_state = 1; // 是否公开活动信息（0表示私密活动，1表示只公开给朋友们，2完全公开） 默认公开给朋友们
	private int forward_invite = 1; // 是否同意朋友邀请朋友（1表示是，0表示否）
	private int need_audit = 1; // 是否需要审核  （1表示是，0表示否）注意与客户端界面显示文字的意思相反
	
	private View event_start_time_row;
	private View event_end_time_row;
	
	private EditText edit_event_address_et;
	
	private AddressTextChange tempChange = new AddressTextChange();
	
	private View get_new_invite_code;
	
	private void initContentView()
	{
		allow_invite_friends_toggle_btn = (ToggleButton) findViewById(R.id.allow_invite_friends_toggle_btn);
		can_join_event_free_toggle_btn = (ToggleButton) findViewById(R.id.can_join_event_free_toggle_btn);
		open_invite_code_toggle_btn = (ToggleButton) findViewById(R.id.open_invite_code_toggle_btn);
		event_open_state_view = findViewById(R.id.event_open_state_view);
		
		UTools.UI.fitViewByWidth(this, allow_invite_friends_toggle_btn, 120, 40, 640);
		UTools.UI.fitViewByWidth(this, can_join_event_free_toggle_btn, 120, 40, 640);
		UTools.UI.fitViewByWidth(this, open_invite_code_toggle_btn, 120, 40, 640);
		
		map_imageImageView = (ImageView) findViewById(R.id.map_image_view);
		event_open_state_introduce_tv = (TextView) findViewById(R.id.event_open_state_introduce_tv);
		event_open_state_tv = (TextView) findViewById(R.id.event_open_state_tv);
		event_info_title_tv = (EditText) findViewById(R.id.event_info_title_tv);
		
		event_end_time_tv = (TextView) findViewById(R.id.event_end_time_tv);
		event_start_time_tv = (TextView) findViewById(R.id.event_start_time_tv);
		
		invite_code_tv = (TextView) findViewById(R.id.invite_code_tv);
		
		event_introduce = (EditText) findViewById(R.id.event_introduce);
		
		report_event_btn = (Button) findViewById(R.id.report_event_btn);
		exit_event_btn = (Button) findViewById(R.id.exit_event_btn);
		
		report_event_btn.setOnClickListener(this);
		exit_event_btn.setOnClickListener(this);
		
		event_open_state_view.setOnClickListener(this);
		
		event_start_time_row = findViewById(R.id.event_start_time_row);
		event_end_time_row = findViewById(R.id.event_end_time_row);
		
		event_end_time_row.setOnClickListener(this);
		event_start_time_row.setOnClickListener(this);
		
		edit_event_address_et = (EditText) findViewById(R.id.edit_event_address_et);
		
		edit_event_address_et.addTextChangedListener(tempChange);
		
		map_imageImageView.setOnClickListener(this);
		
		get_new_invite_code = findViewById(R.id.get_new_invite_code);
		
		get_new_invite_code.setOnClickListener(this);
	}
	
	private void initData()
	{
		if (mEventItemModel != null)
		{
			event_info_title_tv.setText(mEventItemModel.title);
			String map_image_url = "http://api.map.baidu.com/staticimage?center="+mEventItemModel.y+","+mEventItemModel.x+"&width=680&height=400&zoom=18&markers="+mEventItemModel.y+","+mEventItemModel.x;
			fb.display(map_imageImageView, map_image_url);
			
			if (mEventItemModel.public_info == 0)
			{
				event_open_state_tv.setText("邀请参加");
				event_open_state_introduce_tv.setText("邀请参加，只能通过邀请才可以看到并参加该活动");
				open_state = 0;
			}
			else if (mEventItemModel.public_info == 1)
			{
				event_open_state_tv.setText("朋友公开");
				event_open_state_introduce_tv.setText("朋友公开，你的朋友都可以看到并参加该活动");
				open_state = 1;
			}
			else if (mEventItemModel.public_info == 2)
			{
				event_open_state_tv.setText("完全公开");
				event_open_state_introduce_tv.setText("完全公开，附近的人能看到你的活动信息");
				open_state = 2;
			}
			
			if (mEventItemModel.forward_invite == 1)
			{
				allow_invite_friends_toggle_btn.setChecked(true);
				forward_invite = 1;
			}
			else {
				allow_invite_friends_toggle_btn.setChecked(false);
				forward_invite = 0;
			}
			
			if (mEventItemModel.need_audit == 1)
			{
				can_join_event_free_toggle_btn.setChecked(false);
				need_audit = 1;
			}
			else {
				can_join_event_free_toggle_btn.setChecked(true);
				need_audit = 0;
			}
			
			event_end_time_tv.setText(UTimeShown.getTimeStringShownInInfo(mEventItemModel.end_time));
			event_start_time_tv.setText(UTimeShown.getTimeStringShownInInfo(mEventItemModel.start_time));
			
			if (mEventItemModel.invite_code_public == 1)
			{
				open_invite_code_toggle_btn.setChecked(true);
				
				if (mEventItemModel.master.user_id == UUtils.getSelfUserInfoModel(EDEventInfoActivity.this).user_id)
				{
					invite_code_tv.setText(mEventItemModel.invite_code);
				}
				else {
					if (mEventItemModel.forward_invite == 1)
					{
						invite_code_tv.setText(mEventItemModel.invite_code);
					}
					else 
						invite_code_tv.setText("");
				}
			}
			else {
				open_invite_code_toggle_btn.setChecked(false);
				if (mEventItemModel.master.user_id == UUtils.getSelfUserInfoModel(EDEventInfoActivity.this).user_id)
				{
					invite_code_tv.setText(mEventItemModel.invite_code);
				}
				else {
					invite_code_tv.setText("");
				}
			}
			
			event_introduce.setText(mEventItemModel.introduce);
			
			if (mEventItemModel.join == 3)
			{
				if (mEventItemModel.master.user_id == UUtils.getSelfUserInfoModel(EDEventInfoActivity.this).user_id)
				{
					setAllEnable();
				}
				else {
					setAllDisable();
				}
			}
			
			edit_event_address_et.setText(mEventItemModel.address);
		}
	}
	
	NumberPickerPopWindow mNumberPickerPopWindow;
	
	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if (v == mBackView)
		{
			this.finish();
		}
		else if (v == event_open_state_view)
		{
			openStateChoose();
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
		else if (v == map_imageImageView){
			Intent intent = new Intent();
			intent.setClass(this, JSelectLocationFromMapActivity.class);
			
			this.startActivityForResult(intent, UConstants.SELECT_EVENT_LOCATION_REQUESTCODE);
		}
		else if (v == mOkView)
		{
			boolean canStep = true;
			
			if (canStep && event_info_title_tv.getText().toString().trim().isEmpty())
			{
				canStep = false;
				// toast
				Toast toast = Toast.makeText(this, "活动主题不能为空", Toast.LENGTH_SHORT);
				toast.show();
			}
			
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
				final FLoadingProgressBarFragment mLoadingProgressBarFragment = new FLoadingProgressBarFragment();
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				mLoadingProgressBarFragment.show(ft, "dialog");
				
				List<EventTimeModel> mEventTimeModels = new ArrayList<EventTimeModel>();
				
				EventTimeModel mEventTimeModel = new EventTimeModel(event_start_time_tv.getText().toString().trim(), event_end_time_tv.getText().toString().trim());
				
				mEventTimeModels.add(mEventTimeModel);
				
				List<EventAddressModel> mEventAddressModels = new ArrayList<EventAddressModel>();
				
				mEventAddressModels.add(mEventAddressModel);
				
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
				
				Map<String , String> params = new HashMap<String, String>();
				
				Gson gson = new Gson();
				String allTimeString = gson.toJson(mEventTimeModels);
				
				String allAddresString = gson.toJson(mEventAddressModels);
				
				params.put("event_id", String.valueOf(mEventItemModel.event_id));
				params.put("title", event_info_title_tv.getText().toString().trim());
				params.put("introduce", event_introduce.getText().toString().trim());
				params.put("all_time", allTimeString);
				params.put("all_address", allAddresString);
				params.put("public_info", String.valueOf(open_state));
				params.put("forward_invite", String.valueOf(forward_invite));
				params.put("need_audit", String.valueOf(need_audit));
				
				mDataLoader.postData(UConstants.SETTING_INFO_URL, params, this, new HDataListener() {
					
					@Override
					public void onFinish(String source) {
						// TODO Auto-generated method stub
						Gson gson = new Gson();
						try {
							DetailModel temp = gson.fromJson(source, DetailModel.class);
							
							if (temp != null && temp.result.equals("success"))
							{
								if (temp.event != null)
								{
									mEventItemModel = temp.event;
									Bundle bundle = new Bundle();
									bundle.putString("from_where", "setting");
									bundle.putSerializable(UConstants.EVENT_DETAIL_KEY, (Serializable) mEventItemModel);
									mIntent.putExtras(bundle);
									EDEventInfoActivity.this.setResult(RESULT_OK, mIntent);
									EDEventInfoActivity.this.finish();
								}
								
							}
							else if (temp != null){
								Toast toast = Toast.makeText(EDEventInfoActivity.this, temp.message, Toast.LENGTH_SHORT);
								toast.show();
							}
							else {
								Toast toast = Toast.makeText(EDEventInfoActivity.this, "服务器错误", Toast.LENGTH_SHORT);
								toast.show();
							}
						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast toast = Toast.makeText(EDEventInfoActivity.this, "服务器错误", Toast.LENGTH_SHORT);
							toast.show();
						}
						mLoadingProgressBarFragment.dismiss();
					}
					
					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						mLoadingProgressBarFragment.dismiss();
						Toast toast = Toast.makeText(EDEventInfoActivity.this, "服务器错误", Toast.LENGTH_SHORT);
						toast.show();
					}
				});
			}
			
		}
		else if (v == report_event_btn)
		{
			if (mEventItemModel != null)
			{
				final FLoadingProgressBarFragment mLoadingProgressBarFragment = new FLoadingProgressBarFragment();
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				mLoadingProgressBarFragment.show(ft, "dialog");
				Map<String , String> params = new HashMap<String, String>();
				
				params.put("event_id", String.valueOf(mEventItemModel.event_id));
				
				mDataLoader.postData(UConstants.REPORT_EVENT_URL, params, this, new HDataListener() {
					
					@Override
					public void onFinish(String source) {
						// TODO Auto-generated method stub
						mLoadingProgressBarFragment.dismiss();
						Gson gson = new Gson();
						try {
							TempInviteCodeModel mTempInviteCodeModel = gson.fromJson(source, new TypeToken<TempInviteCodeModel>(){}.getType());
							
							if (mTempInviteCodeModel != null)
							{
								Toast toast = Toast.makeText(EDEventInfoActivity.this, mTempInviteCodeModel.message, Toast.LENGTH_SHORT);
								toast.show();
							}
						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast toast = Toast.makeText(EDEventInfoActivity.this, "服务器错误", Toast.LENGTH_SHORT);
							toast.show();
						}
					}
					
					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						mLoadingProgressBarFragment.dismiss();
						Toast toast = Toast.makeText(EDEventInfoActivity.this, "服务器错误", Toast.LENGTH_SHORT);
						toast.show();
					}
				});
			}
		}
		else if (v == exit_event_btn)
		{
			if (mEventItemModel != null)
			{
				final FLoadingProgressBarFragment mLoadingProgressBarFragment = new FLoadingProgressBarFragment();
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				mLoadingProgressBarFragment.show(ft, "dialog");
				
				Map<String, String> params = new HashMap<String, String>();
				params.put("event_id", String.valueOf(mEventItemModel.event_id));
				params.put("method", "apply_quit");
				
				final int exit_event_id = mEventItemModel.event_id;
				mDataLoader.postData(UConstants.EVENT_DETAIL_JOIN_EVENT_URL, params, this, new HDataListener() {
					
					@Override
					public void onFinish(String source) {
						// TODO Auto-generated method stub
						mLoadingProgressBarFragment.dismiss();
						Gson gson = new Gson();
						try {
							TempInviteCodeModel mTempInviteCodeModel = gson.fromJson(source, new TypeToken<TempInviteCodeModel>(){}.getType());
							
							if (mTempInviteCodeModel != null && mTempInviteCodeModel.result.equals("success"))
							{
								Bundle bundle = new Bundle();
								bundle.putString("from_where", "quit");
								bundle.putInt("join", mTempInviteCodeModel.join);
								mIntent.putExtras(bundle);
								EDEventInfoActivity.this.setResult(RESULT_OK, mIntent);
								
								DDBOpenHelper mDdbOpenHelper = DDBOpenHelper.getInstance(EDEventInfoActivity.this);
								mDdbOpenHelper.deleteEventItemModelFromDB(exit_event_id);
								
								EDEventInfoActivity.this.finish();
							}
							else if (mTempInviteCodeModel != null) {
								Toast toast = Toast.makeText(EDEventInfoActivity.this, mTempInviteCodeModel.message, Toast.LENGTH_SHORT);
								toast.show();
							}
						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast toast = Toast.makeText(EDEventInfoActivity.this, "服务器错误", Toast.LENGTH_SHORT);
							toast.show();
						}
					}
					
					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						mLoadingProgressBarFragment.dismiss();
						Toast toast = Toast.makeText(EDEventInfoActivity.this, "服务器错误", Toast.LENGTH_SHORT);
						toast.show();
					}
				});
			}
		}
		else if (v == get_new_invite_code)
		{
			if (mEventItemModel != null)
			{
				final FLoadingProgressBarFragment mLoadingProgressBarFragment = new FLoadingProgressBarFragment();
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				mLoadingProgressBarFragment.show(ft, "dialog");
				Map<String , String> params = new HashMap<String, String>();
				
				params.put("event_id", String.valueOf(mEventItemModel.event_id));
				
				mDataLoader.postData(UConstants.REFRESH_INVITE_CODE_URL, params, this, new HDataListener() {
					
					@Override
					public void onFinish(String source) {
						// TODO Auto-generated method stub
						mLoadingProgressBarFragment.dismiss();
						Gson gson = new Gson();
						try {
							TempInviteCodeModel mTempInviteCodeModel = gson.fromJson(source, new TypeToken<TempInviteCodeModel>(){}.getType());
							
							if (mTempInviteCodeModel != null && mTempInviteCodeModel.result.equals("success"))
							{
								invite_code_tv.setText(mTempInviteCodeModel.invite_code);
							}
							else if (mTempInviteCodeModel != null) {
								Toast toast = Toast.makeText(EDEventInfoActivity.this, mTempInviteCodeModel.message, Toast.LENGTH_SHORT);
								toast.show();
							}
						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast toast = Toast.makeText(EDEventInfoActivity.this, "服务器错误", Toast.LENGTH_SHORT);
							toast.show();
						}
					}
					
					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						mLoadingProgressBarFragment.dismiss();
						Toast toast = Toast.makeText(EDEventInfoActivity.this, "服务器错误", Toast.LENGTH_SHORT);
						toast.show();
					}
				});
				
			}
		}
				
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
	}
	
	private void setAllDisable()
	{
		allow_invite_friends_toggle_btn.setEnabled( false);
		can_join_event_free_toggle_btn.setEnabled(false);
		open_invite_code_toggle_btn.setEnabled(false);
		
		event_info_title_tv.setEnabled(false);
		map_imageImageView.setEnabled(false);
		event_open_state_introduce_tv.setEnabled(false);
		event_open_state_tv.setEnabled(false);
		event_start_time_tv.setEnabled(false);
		event_end_time_tv.setEnabled(false);
		invite_code_tv.setEnabled(false);
		event_introduce.setEnabled(false);
		
		mOkView.setVisibility(View.INVISIBLE);
		mOkLine.setVisibility(View.INVISIBLE);
		
		event_open_state_view.setEnabled(false);
		event_end_time_row.setEnabled(false);
		event_start_time_row.setEnabled(false);
		edit_event_address_et.setEnabled(false);
		get_new_invite_code.setEnabled(false);
	}
	
	private void setAllEnable()
	{
		allow_invite_friends_toggle_btn.setEnabled(true);
		can_join_event_free_toggle_btn.setEnabled(true);
		open_invite_code_toggle_btn.setEnabled(true);
		
		event_info_title_tv.setEnabled(true);
		map_imageImageView.setEnabled(true);
		event_open_state_introduce_tv.setEnabled(true);
		event_open_state_tv.setEnabled(true);
		event_start_time_tv.setEnabled(true);
		event_end_time_tv.setEnabled(true);
		invite_code_tv.setEnabled(true);
		event_introduce.setEnabled(true);
		
		mOkLine.setVisibility(View.VISIBLE);
		mOkView.setVisibility(View.VISIBLE);
		
		event_open_state_view.setEnabled(true);
		event_end_time_row.setEnabled(true);
		event_start_time_row.setEnabled(true);
		edit_event_address_et.setEnabled(true);
		get_new_invite_code.setEnabled(true);
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
				
				fb.display(map_imageImageView, map_image_url);
				
				map_imageImageView.setVisibility(View.VISIBLE);
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
					mEventAddressModel.x = Double.parseDouble(UTools.Storage.getSharedPreferences(EDEventInfoActivity.this, UConstants.BASE_PREFS_NAME).getString(UConstants.LOCATION_LATITUDE, "0.0"));
					mEventAddressModel.y = Double.parseDouble(UTools.Storage.getSharedPreferences(EDEventInfoActivity.this, UConstants.BASE_PREFS_NAME).getString(UConstants.LOCATION_LONGITUDE, "0.0"));
				}
			}
		}
		
	}
	
	class TempInviteCodeModel
	{
		String result = "";
		String message = "";
		String invite_code = "";
		int join = 0;
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
	
	class DetailModel
	{
		String result;
		String message;
		EventItemModel event;
	}
}
