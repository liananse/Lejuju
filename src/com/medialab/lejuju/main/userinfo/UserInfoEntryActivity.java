package com.medialab.lejuju.main.userinfo;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.MMainTabActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.db.DDBOpenHelper;
import com.medialab.lejuju.fragment.FLoadingProgressBarFragment;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.http.HHttpDataLoader.HDataListener;
import com.medialab.lejuju.main.photowall.BigPictureActivity;
import com.medialab.lejuju.main.userinfo.model.UserInfoPicModel;
import com.medialab.lejuju.main.userinfo.model.UserInfoReloadModel;
import com.medialab.lejuju.model.FriendsModel;
import com.medialab.lejuju.model.SelfUserInfoModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UDisplayWidth;
import com.medialab.lejuju.util.UImageManager;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.util.UUtils;
import com.medialab.lejuju.views.RoundImageView;

public class UserInfoEntryActivity extends MBaseActivity implements OnClickListener{

	// 头像
	private RoundImageView mCircleImageView;
	//
	private View mHeadViewBgView;
	
	private FriendsModel mFriendsModel;
	
	private Button confirmBtn;
	
	private HHttpDataLoader http = new HHttpDataLoader();
	
	private View userinfo_albums;
	
	///////////////////////////////
	// new added 2013.08.27
	///////////////////////////////
	private String user_id = "-1";
	
	private Boolean fromPush = false;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_userinfo_entry);
		fb = FinalBitmap.create(this);
		
		initHeaderBar();
		initContentView();
		
		fromPush = getIntent().getBooleanExtra(UConstants.FROM_PUSH, false);
		
		if (fromPush)
		{
			user_id = getIntent().getStringExtra("user_id");
		}
		else 
		{
			mFriendsModel = (FriendsModel) getIntent().getSerializableExtra(UConstants.FRIENDS_KEY);
			
			if (mFriendsModel != null)
			{
				user_id = String.valueOf(mFriendsModel.user_id);
			}
			initUserInfo();
		}
		
		reloadUserInfo();
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
		UTools.UI.fitViewByWidth(this, mOkImageView, 46, 10, 640);
		
		mBackView.setOnClickListener(this);
		mOkView.setOnClickListener(this);
		
		mOkView.setVisibility(View.GONE);
	}
	private FinalBitmap fb;
	
	private LinearLayout pics_ll_view;
	
	
	private TextView nameTextView;
	private TextView sexTextView;
	private TextView school;
	private TextView departmentTextView;
	private TextView yearTextView;
	private TextView areaTextView;
	
	private void initContentView()
	{
		mCircleImageView = (RoundImageView) findViewById(R.id.header_icon);
		mHeadViewBgView = findViewById(R.id.item_event_head_pic_bg);
		
		confirmBtn = (Button) findViewById(R.id.confirmBtn);
		confirmBtn.setOnClickListener(this);
		
		UTools.UI.fitViewByWidth(this, mCircleImageView, 140, 140, 640);
		UTools.UI.fitViewByWidth(this, mHeadViewBgView, 150, 150, 640);
		
		mHeadViewBgView.setOnClickListener(this);
		userinfo_albums = findViewById(R.id.userinfo_albums);
		pics_ll_view = (LinearLayout) findViewById(R.id.pics_ll_view);
		userinfo_albums.setOnClickListener(this);
		
		userinfo_albums.setVisibility(View.GONE);
		
		confirmBtn.setVisibility(View.GONE);
		
		nameTextView = (TextView) findViewById(R.id.nameTextView);
		sexTextView = (TextView) findViewById(R.id.sexTextView);
		school = (TextView) findViewById(R.id.school);
		departmentTextView = (TextView) findViewById(R.id.departmentTextView);
		yearTextView = (TextView) findViewById(R.id.yearTextView);
		areaTextView = (TextView) findViewById(R.id.areaTextView);
		
	}
	
	private void initUserInfo()
	{
		if (mFriendsModel != null)
		{
			fb.display(mCircleImageView, mFriendsModel.head_pic);
			
			nameTextView.setText(mFriendsModel.nick_name);
			if (mFriendsModel.sex == 1)
			{
				sexTextView.setText("男");
			}
			else {
				sexTextView.setText("女");
			}
			school.setText(mFriendsModel.school);
			departmentTextView.setText(mFriendsModel.department);
			yearTextView.setText(mFriendsModel.enter_school_year + "年");
			areaTextView.setText(mFriendsModel.area);
		}
	}
	
	private void reInitUserInfo()
	{
		if (mFriendsModel != null)
		{
			fb.display(mCircleImageView, mFriendsModel.head_pic);
			
			 //0陌生人，1朋友    2已关注 3未关注  4自己  5申请加我为好友的朋友 6.等待验证    2,3为公共账号专用
			if (mFriendsModel.relation == 0 || mFriendsModel.relation == 3 || mFriendsModel.relation == 5)
			{
				confirmBtn.setVisibility(View.VISIBLE);
				mOkView.setVisibility(View.GONE);
				if (mFriendsModel.relation == 0)
				{
					confirmBtn.setText(R.string.userinfo_activity_add_friend);
				}
				else if (mFriendsModel.relation == 3) {
					confirmBtn.setText(R.string.userinfo_activity_focus_friend);
				}
				else if (mFriendsModel.relation == 5) {
					confirmBtn.setText(R.string.userinfo_activity_pass_friend);
				}
				
				// 点击查看大图
			}
			else
			{
				confirmBtn.setVisibility(View.GONE);
				
				if (mFriendsModel.relation == 1 || mFriendsModel.relation == 2 || mFriendsModel.relation == 4)
				{
				}
				
				if (mFriendsModel.relation == 1 || mFriendsModel.relation == 2)
				{
					mOkView.setVisibility(View.VISIBLE);
				}
				else {
					mOkView.setVisibility(View.GONE);
				}
				// 如果是自己
				if (mFriendsModel.relation == 4)
				{
					// 修改头像
				}
				else {
					// 查看大图
				}
			}
			
			initUserInfo();
		}
	}
	
	
	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	
	private void reloadUserInfo()
	{
		if (!user_id.equals("-1") && !user_id.isEmpty())
		{
			Map<String, String> params = new HashMap<String, String>();
			
			params.put("user_id", user_id);
			params.put("head_pic", String.valueOf(UDisplayWidth.getLargeHeadPicWidth(this)));
			params.put("width", String.valueOf(UDisplayWidth.getEventDetailPicWidth(this)));
			mDataLoader.getData(UConstants.USER_INFO_GET_URL, params, this, new HDataListener() {
				
				@Override
				public void onFinish(String source) {
					// TODO Auto-generated method stub
					Gson gson = new Gson();
					
					try {
						TempModel mTempModel = gson.fromJson(source, new TypeToken<TempModel>(){}.getType());
						
						if(mTempModel != null && mTempModel.result.equals("success"))
						{
							mFriendsModel = UUtils.initMFriendsModel(mTempModel.data);
							reInitUserInfo();
							displayPics(mTempModel.data.picurls);
							
							if (mFriendsModel.relation == 1)
							{
								if (UUtils.getFriendsModelByUserId(UserInfoEntryActivity.this, String.valueOf(mFriendsModel.user_id)) == null)
								{
									List<FriendsModel> tempList = new ArrayList<FriendsModel>();
									tempList.add(mFriendsModel);
									
									DDBOpenHelper mDdbOpenHelper = DDBOpenHelper.getInstance(UserInfoEntryActivity.this);
									mDdbOpenHelper.insertFriendsInfoModel(tempList);
								}
							}
						}
					} catch (JsonSyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				@Override
				public void onFail(String msg) {
					// TODO Auto-generated method stub
					
				}
			});
		}
	}
	
	
	class TempModel
	{
		public String result = "";
		public String message = "";
		public UserInfoReloadModel data = null;
	}
	
	private void deleteFriends()
	{
		if (mFriendsModel != null)
		{
			Map<String, String> params = new HashMap<String, String>();
			params.put("fans_id", String.valueOf(mFriendsModel.user_id));
			
			mDataLoader.postData(UConstants.DELETE_FRIENDS_URL, params, this, new HDataListener() {
				
				@Override
				public void onFinish(String source) {
					// TODO Auto-generated method stub
					DDBOpenHelper mDdbOpenHelper = DDBOpenHelper.getInstance(UserInfoEntryActivity.this);
					mDdbOpenHelper.deleteFriendsModelFromDB(mFriendsModel.user_id);
					UserInfoEntryActivity.this.finish();
				}
				
				@Override
				public void onFail(String msg) {
					// TODO Auto-generated method stub
					
				}
			});
		}
	}
	
	private void displayPics(List<UserInfoPicModel> picList)
	{
		if (mFriendsModel != null)
		{
			if (mFriendsModel.relation == 1 || mFriendsModel.relation == 2 || mFriendsModel.relation == 4)
			{
				if (picList != null && picList.size() > 0)
				{
					userinfo_albums.setVisibility(View.VISIBLE);
					
					for (int i = 0; i < picList.size(); i++) {
						
						ImageView imageView = new ImageView(UserInfoEntryActivity.this);

						LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						
						imageView.setScaleType(ScaleType.CENTER_CROP);
						imageView.setLayoutParams(layoutParams);
						
						fb.display(imageView, picList.get(i).picurl);
						
						UTools.UI.fitViewByWidth(UserInfoEntryActivity.this, imageView, 100, 100, 640);
						
						LinearLayout spaceView = new LinearLayout(UserInfoEntryActivity.this);
						LayoutParams mLayoutParams = new LayoutParams(15, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
						spaceView.setLayoutParams(mLayoutParams);
						
						pics_ll_view.addView(spaceView);
						pics_ll_view.addView(imageView);
					}
				}
			}
		}
	}
	

	@Override
	public void onClick(View v) {
		if (v == mBackView)
		{
			mBackViewClick();
		}
		else if (v == mOkView)
		{
			new AlertDialog.Builder(this).setItems(R.array.user_info_more, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					switch (which)
					{
						case 0:
							deleteFriends();
							break;
					}
				}

			}).show();
		}
		else if (v == confirmBtn)
		{
			Map<String,String> params = new HashMap<String,String>();
			String url = "";
			
			if (mFriendsModel != null)
			{
				// 0 3 5
				//0陌生人，1朋友    2已关注 3未关注  4自己  5申请加我为好友的朋友 6.等待验证    2,3为公共账号专用
				if (mFriendsModel.relation == 0)
				{
					url = UConstants.ADD_FRIENDS_URL;
					
					params.put("fans_id", String.valueOf(mFriendsModel.user_id));
					params.put("head_width", String.valueOf(UDisplayWidth.getLargeHeadPicWidth(this)));
					params.put("verify_info", UUtils.getSelfUserInfoModel(this).nick_name);
					params.put("width", String.valueOf(UDisplayWidth.getEventDetailPicWidth(this)));
				}
				
				else if (mFriendsModel.relation == 3)
				{
					url = UConstants.FOLLOW_URL;
					
					params.put("follow_id", String.valueOf(mFriendsModel.user_id));
					params.put("method", "follow");
				}
				else if (mFriendsModel.relation == 5)
				{
					url = UConstants.AUDIT_APPLY_URL;
					
					params.put("head_width", String.valueOf(UDisplayWidth.getLargeHeadPicWidth(this)));
					params.put("method", "pass");
					params.put("fans_id", String.valueOf(mFriendsModel.user_id));
					params.put("width", String.valueOf(UDisplayWidth.getEventDetailPicWidth(this)));
				}
				
				
				final FLoadingProgressBarFragment mLoadingProgressBarFragment = new FLoadingProgressBarFragment();
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				mLoadingProgressBarFragment.show(ft, "dialog");
				confirmBtn.setClickable(false);
				
				http.postData(url, params, this, new HHttpDataLoader.HDataListener()
				{
					
					@Override
					public void onFinish(String source)
					{
						Gson gson = new Gson();
						
						try {
							TempResultModel mTempResultModel = gson.fromJson(source, new TypeToken<TempResultModel>(){}.getType());
							
							if(mTempResultModel != null && mTempResultModel.result.equals("success"))
							{
								mFriendsModel = UUtils.initMFriendsModel(mTempResultModel.user);
								user_id = String.valueOf(mFriendsModel.user_id);
								reInitUserInfo();
								displayPics(mTempResultModel.user.picurls);
								
								List<FriendsModel> tempList = new ArrayList<FriendsModel>();
								tempList.add(mFriendsModel);
								
								if (mFriendsModel.relation == 1)
								{
									if (UUtils.getFriendsModelByUserId(UserInfoEntryActivity.this, user_id) == null)
									{
										DDBOpenHelper mDdbOpenHelper = DDBOpenHelper.getInstance(UserInfoEntryActivity.this);
										mDdbOpenHelper.insertFriendsInfoModel(tempList);
									}
								}
								
								Toast.makeText(UserInfoEntryActivity.this, mTempResultModel.message, Toast.LENGTH_SHORT).show();
							}
						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						mLoadingProgressBarFragment.dismiss();
						confirmBtn.setClickable(true);
					}
					
					@Override
					public void onFail(String msg)
					{
						mLoadingProgressBarFragment.dismiss();
						confirmBtn.setClickable(true);
						Toast.makeText(UserInfoEntryActivity.this, "服务器出错", Toast.LENGTH_LONG).show();
					}
				});
				
				
			}
			
		}
		else if (v == userinfo_albums)
		{
			if (mFriendsModel != null)
			{
				Intent intent = new Intent();
				intent.setClass(this, UserInfoPastEventActivity.class);
				
				Bundle bundle = new Bundle();
				bundle.putSerializable(UConstants.FRIENDS_KEY, (Serializable) mFriendsModel);
				
				intent.putExtras(bundle);
				
				this.startActivity(intent);
			}
		}
		else if (v == mHeadViewBgView)
		{
			if (mFriendsModel != null)
			{
				SelfUserInfoModel mSelfUserInfoModel;
				mSelfUserInfoModel = UUtils.getSelfUserInfoModel(this);
				
				if (mSelfUserInfoModel != null)
				{
					if (mFriendsModel.user_id == mSelfUserInfoModel.user_id)
					{
						showDialog(DIALOG_SELF);
					}
					else {
						Intent intent = new Intent();
						intent.setClass(this, BigPictureActivity.class);
						
						Bundle bundle = new Bundle();
						bundle.putSerializable(UConstants.FRIENDS_KEY, (Serializable) mFriendsModel);
						
						intent.putExtras(bundle);
						
						this.startActivity(intent);
					}
				}
			}
		}
			
	}

	class TempResultModel
	{
		public String result = "";
		public String message = "";
		public UserInfoReloadModel user = null;
	}
	
	private void mBackViewClick()
	{
		int stackSize = UTools.activityhelper.getStackSize();

		// 历史栈中只有当前这一个activity
		if (stackSize == 1)
		{
			Intent intent = new Intent(this,MMainTabActivity.class);
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		}
		
		finish();
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
	
	private static final int DIALOG_SELF = 1;
    
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
        case DIALOG_SELF:
        	return new AlertDialog.Builder(UserInfoEntryActivity.this,AlertDialog.THEME_HOLO_LIGHT)
            .setTitle("提示")
            .setMessage("修改头像")
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
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		
		startActivityForResult(intent, UConstants.GETIMAGE);
	}
	
	private void startCamera()
	{
		Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(UTools.Storage.getHeadPicImagePath())));
		startActivityForResult(i, UConstants.CAPUTRE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) {
		
		case RESULT_OK:
			
			if (requestCode == UConstants.CAPUTRE)
			{
				// Load up the image's dimensions not the image itself
				BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
				bmpFactoryOptions.inJustDecodeBounds = true;
				Bitmap bmp = BitmapFactory.decodeFile(UTools.Storage.getHeadPicImagePath(),
						bmpFactoryOptions);
				
				int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) 1080);
	
				if (widthRatio > 1)
				{
					bmpFactoryOptions.inSampleSize = widthRatio;
				}
	
				bmpFactoryOptions.inJustDecodeBounds = false;
				bmp = BitmapFactory.decodeFile(UTools.Storage.getHeadPicImagePath(), bmpFactoryOptions);
	
				String imagePath = UTools.Storage.getHeadPicSmallImagePath();
				
				if (UImageManager.saveBtimapToFile(bmp, imagePath))
				{
//					changeHeader();
					android.os.Message msg = new android.os.Message();
					msg.what = 1;
					handler.sendMessage(msg);
				}
				
			}
			else if (requestCode == UConstants.GETIMAGE)
			{
				Uri imageFileUri = data.getData();
				
				try {
					BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
					bmpFactoryOptions.inJustDecodeBounds = true;
					
					Bitmap bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, bmpFactoryOptions);
					
					int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth
							/ (float) 1080);
					if (widthRatio > 1) {
						bmpFactoryOptions.inSampleSize = widthRatio;
					}
					
					bmpFactoryOptions.inJustDecodeBounds = false;
					bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri),null, bmpFactoryOptions);
		
					String imagePath = UTools.Storage.getHeadPicSmallImagePath();
					
					if (UImageManager.saveBtimapToFile(bmp, imagePath))
					{
//						changeHeader();
						android.os.Message msg = new android.os.Message();
						msg.what = 1;
						handler.sendMessage(msg);
					}
					
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			break;

		default:
			break;
		}
	}
	
	private void changeHeader()
	{
		final FLoadingProgressBarFragment mLoadingProgressBarFragment = new FLoadingProgressBarFragment();
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		mLoadingProgressBarFragment.show(ft, "dialog");
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("image", "image:" + UTools.Storage.getHeadPicSmallImagePath());
		params.put("head_width", String.valueOf(UDisplayWidth.getLargeHeadPicWidth(this)));
		
		mDataLoader.postData(UConstants.UPDATE_HEAD_PIC_URL, params, this, new HDataListener() {
			
			@Override
			public void onFinish(String source) {
				// TODO Auto-generated method stub
				Gson gson = new Gson();
				
				try {
					TempUpdatePicModel mTempUpdatePicModel = gson.fromJson(source, new TypeToken<TempUpdatePicModel>(){}.getType());
					
					if(mTempUpdatePicModel != null && mTempUpdatePicModel.result.equals("success"))
					{
						fb.display(mCircleImageView, mTempUpdatePicModel.head_pic);
						DDBOpenHelper mDdbOpenHelper = DDBOpenHelper.getInstance(UserInfoEntryActivity.this);
						
						mDdbOpenHelper.updateSelfUserInfoHeadModelMobile(mTempUpdatePicModel.head_pic);
						
						Toast.makeText(UserInfoEntryActivity.this, mTempUpdatePicModel.message, Toast.LENGTH_SHORT).show();
					}
					else if(mTempUpdatePicModel != null)
					{
						Toast.makeText(UserInfoEntryActivity.this, mTempUpdatePicModel.message, Toast.LENGTH_SHORT).show();
					}
				} catch (JsonSyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				mLoadingProgressBarFragment.dismiss();
			}
			
			@Override
			public void onFail(String msg) {
				// TODO Auto-generated method stub
				Toast.makeText(UserInfoEntryActivity.this, "服务器出错", Toast.LENGTH_LONG).show();
				mLoadingProgressBarFragment.dismiss();
			}
		});
	}
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 1:
				changeHeader();
				break;
			default:
				break;
			}
		}
	};
	
	class TempUpdatePicModel
	{
		String result;
		String message;
		String head_pic;
	}
	
}
