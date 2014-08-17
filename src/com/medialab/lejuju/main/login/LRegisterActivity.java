package com.medialab.lejuju.main.login;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.db.DDBOpenHelper;
import com.medialab.lejuju.fragment.FLoadingProgressBarFragment;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.main.userinfo.UserInfoSelectRegionProvinceActivity;
import com.medialab.lejuju.model.SelfUserInfoModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UDisplayWidth;
import com.medialab.lejuju.util.UImageManager;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.views.RoundImageView;

public class LRegisterActivity extends MBaseActivity implements OnClickListener {

	private TextView statement_part2;
	private View regionSelectView;

	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	
	// register item
	private EditText mNicknamEditText;
	// 学校
	private EditText mCollegeEditText;
	// 学院
	private EditText mSchoolEditText;
	// 入学年份
	private TextView mYearTextView;
	private View yearSelectView;
	
	private TextView mRegionTextView;
	private RadioGroup mRadioGroup;
	private RoundImageView upload_head_pic;
	private CheckBox mCheckBox;
	
	private String mobile = "";
	private String password = "";
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_register);
		
		mobile = getIntent().getStringExtra("mobile");
		password = getIntent().getStringExtra("password");
		initYears();
		initHeaderBar();
		initView();
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
		UTools.UI.fitViewByWidth(this, mOkImageView, 45, 28, 640);
		
		mBackView.setOnClickListener(this);
		mOkView.setOnClickListener(this);
	}

	
	public void initView() {
		statement_part2 = (TextView) findViewById(R.id.statement_part2_textview);
		String forgetpassword = this.getResources().getString(
				R.string.register_activity_statement_part2);
		statement_part2.setText(Html.fromHtml("<u>" + forgetpassword + "<u>"));

		// region select view
		regionSelectView = findViewById(R.id.regionSelectView);
		regionSelectView.setOnClickListener(this);

		// register item view
		mNicknamEditText = (EditText) findViewById(R.id.nickNameEditText);
		mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		mRegionTextView = (TextView) findViewById(R.id.regionTextView);
		
		mCheckBox = (CheckBox) findViewById(R.id.statement_checkbox);
		
		upload_head_pic = (RoundImageView) findViewById(R.id.upload_head_pic);
		
		UTools.UI.fitViewByWidth(this, upload_head_pic, 183, 183, 640);
		
		upload_head_pic.setOnClickListener(this);
		
		mCollegeEditText = (EditText) findViewById(R.id.collegeEditText);
		mSchoolEditText = (EditText) findViewById(R.id.schoolEditText);
		mYearTextView = (TextView) findViewById(R.id.time_tv);
		yearSelectView = findViewById(R.id.timeSelectView);
		
		Calendar c = Calendar.getInstance();
		mYearTextView.setText(c.get(Calendar.YEAR) + "年");
		yearSelectView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == regionSelectView) {
			Intent intent = new Intent();
			intent.setClass(this, UserInfoSelectRegionProvinceActivity.class);
			this.startActivityForResult(intent, UConstants.SELECT_REGION_REQUESTCODE);
			
		} 
		else if (v == mBackView)
		{
			this.finish();
		}
		else if (v == upload_head_pic)
		{
			showDialog(DIALOG_YES_NO_LONG_MESSAGE);
		}
		else if (v == yearSelectView)
		{
			showDialog(DATE_ID);
		}
		else if (v == mOkView)
		{
			boolean canRegister = true;
			
			if (!isHeadPicSet)
			{
				canRegister = false;
				Toast toast = Toast.makeText(this, "请上传头像!", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 0, 0);
				toast.show();
			}
			if (canRegister && mNicknamEditText.getText().toString().trim().isEmpty())
			{
				canRegister = false;
				Toast toast = Toast.makeText(this, "请输入你的真实姓名", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 0, 0);
				toast.show();
			}
			
			if (canRegister && mCollegeEditText.getText().toString().trim().isEmpty())
			{
				canRegister = false;
				Toast toast = Toast.makeText(this, "请输入你所在的大学", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 0, 0);
				toast.show();
			}
			
			if (canRegister && mSchoolEditText.getText().toString().trim().isEmpty())
			{
				canRegister = false;
				Toast toast = Toast.makeText(this, "请输入你所在的学院", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 0, 0);
				toast.show();
			}
			
			if (canRegister && !mCheckBox.isChecked())
			{
				canRegister = false;
				Toast toast = Toast.makeText(this, R.string.input_statement_tips, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 0, 0);
				toast.show();
			}
			
			if (canRegister) {
				
				SharedPreferences sp = UTools.Storage.getSharedPreferences(LRegisterActivity.this, UConstants.BASE_PREFS_NAME);
				String baidu_uid = sp.getString(UConstants.BAIDU_USER_ID, "");
				String baidu_channel_id = sp.getString(UConstants.BAIDU_CHANNEL_ID, "");
				
				// dialog progressBar
				final FLoadingProgressBarFragment mLoadingProgressBarFragment = new FLoadingProgressBarFragment();
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				mLoadingProgressBarFragment.show(ft, "dialog");
				
				Map<String, String> params = new HashMap<String, String>();
				if (mRadioGroup.getCheckedRadioButtonId() == R.id.radio1)
				{
					params.put("sex", "1");
				}
				else {
					params.put("sex", "0");
				}
				params.put("nick_name", mNicknamEditText.getText().toString().trim());
				params.put("area", mRegionTextView.getText().toString().trim());
				params.put("password", password);
				params.put("mobile", mobile);
				params.put("image", "image:" + UTools.Storage.getHeadPicSmallImagePath());
				params.put("head_width", String.valueOf(UDisplayWidth.getLargeHeadPicWidth(this)));
				params.put("baidu_user_id", baidu_uid); 
				params.put("baidu_channel_id", baidu_channel_id); 
		        params.put("school", mCollegeEditText.getText().toString().trim());
				params.put("department", mSchoolEditText.getText().toString().trim());
				params.put("enter_school_year", mYearTextView.getText().toString().trim().replace("年", ""));
				
				mDataLoader.postData(UConstants.LREGISTER_URL, params, this, new HHttpDataLoader.HDataListener() {
					
					@Override
					public void onFinish(String source) {
						// TODO Auto-generated method stub
						Gson gson = new Gson();
						TempModel mTempModel = gson.fromJson(source, new TypeToken<TempModel>(){}.getType());
						
						if (mTempModel != null && mTempModel.result.equals("success"))
						{
							DDBOpenHelper mDdbOpenHelper = DDBOpenHelper.getInstance(LRegisterActivity.this);
							
							if(mTempModel.data != null)
							{
								
								mDdbOpenHelper.insertSelfUserInfoModel(mTempModel.data);
								
								// 将userid和accesstoken同时放到sharedpreferences中
								SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(LRegisterActivity.this, UConstants.BASE_PREFS_NAME);
								mEditor.putString(UConstants.SELF_USER_ID, String.valueOf(mTempModel.data.user_id));
								mEditor.putString(UConstants.SELF_ACCESS_TOKEN, mTempModel.data.access_token);
								mEditor.commit();
							}
							
							// dismiss the progressbar
							mLoadingProgressBarFragment.dismiss();
							
							Intent intent = new Intent();
							
							UTools.activityhelper.clearAllBut(LRegisterActivity.this);
							
							intent.setClass(LRegisterActivity.this, LInputInviteCodeActivity.class);
							LRegisterActivity.this.startActivity(intent);
							LRegisterActivity.this.finish();
						}
						else if (mTempModel != null && mTempModel.result.equals("fail"))
						{
							// dismiss the progressbar
							mLoadingProgressBarFragment.dismiss();
							
							Toast toast = Toast.makeText(LRegisterActivity.this, mTempModel.message, Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.TOP, 0, 0);
							toast.show();
						}
					}
					
					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						
					}
				});
				
				
			}
		}
	}
	
	private boolean isHeadPicSet = false;
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
				
				UImageManager.saveBtimapToFile(bmp, imagePath);
				
				UTools.UI.fitViewByWidth(this, upload_head_pic, 183, 183, 640);
				upload_head_pic.setImageBitmap(bmp);
				isHeadPicSet = true;
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
						
					}
					
					UTools.UI.fitViewByWidth(this, upload_head_pic, 183, 183, 640);
					upload_head_pic.setImageBitmap(bmp);
					isHeadPicSet = true;
					
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			else {
				Bundle bundle = data.getExtras();
				String regionStr = bundle.getString("region_str");
				mRegionTextView.setText(regionStr);
			}
			break;

		default:
			break;
		}
	}
	
	/**
	 * @author liananse
	 * 2013.07.07
	 */
	class TempModel
	{
		public String result = "";
		public String message = "";
		public SelfUserInfoModel data = null;
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
	
	private static final int DIALOG_YES_NO_LONG_MESSAGE = 1;
	private static final int DATE_ID = 2;
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case DIALOG_YES_NO_LONG_MESSAGE:
            return new AlertDialog.Builder(LRegisterActivity.this,AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("提示")
                .setMessage("选择活动头像")
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
         case DATE_ID:
        	 return new AlertDialog.Builder(LRegisterActivity.this,AlertDialog.THEME_HOLO_LIGHT)
             .setTitle("入学年份")
             .setItems(years, new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int which) {

                     /* User clicked so do some stuff */
                	 mYearTextView.setText(years[which]);
                 }
             })
             .create();
		}
		
		return null;
	}
	
	private String[] years;
	
	private void initYears()
	{
		final Calendar c = Calendar.getInstance();
		
		years = new String[c.get(Calendar.YEAR) - 1970 + 1];
		
		for (int i = 0; i < years.length; i++) {
			years[i] = (c.get(Calendar.YEAR) - i) + "年";
		}
	}

}
