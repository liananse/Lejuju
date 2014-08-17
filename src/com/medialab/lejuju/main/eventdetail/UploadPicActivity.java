package com.medialab.lejuju.main.eventdetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.core.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.db.DDBOpenHelper;
import com.medialab.lejuju.fragment.FLoadingProgressBarFragment;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.main.photowall.adapter.UploadPicAdapter;
import com.medialab.lejuju.main.photowall.model.PhotoUploadModel;
import com.medialab.lejuju.service.UploadPicService;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.util.UUtils;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class UploadPicActivity extends MBaseActivity implements OnClickListener
{

	EditText commentText;
	GridView preGridview;
	String[] allPath;
	List<ImageView> list;
	ImageLoader imageLoader;
	LayoutInflater inflater;
	List<String> listImage;
	String comment;
	String key = "tempComment";
	int eventId;
	
	HHttpDataLoader http;
	/*
	 * (non-Javadoc)
	 * @see com.medialab.lejuju.MBaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle arg0)
	{
		super.onCreate(arg0);
		setContentView(R.layout.activity_upload_pics);
		initHeaderBar();
		initView();
		initData();
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

	private void initView()
	{

		commentText = (EditText) findViewById(R.id.comment_text);
		preGridview = (GridView) findViewById(R.id.pre_gridview);
	}

	private void initImageLoader()
	{
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
				this).defaultDisplayImageOptions(defaultOptions).memoryCache(
				new WeakMemoryCache());

		ImageLoaderConfiguration config = builder.build();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
	}

	private void initData()
	{
		initImageLoader();
		http = new HHttpDataLoader();
		
		inflater = LayoutInflater.from(this);
		allPath = getIntent().getStringArrayExtra("all_path");
		listImage = new ArrayList<String>(Arrays.asList(allPath));
		if (listImage.size() < 9)
		{
			listImage.add("add");
		}
		
		eventId = getIntent().getIntExtra("event_id",0);
		
		SharedPreferences sp = UTools.Storage.getSharedPreferences(this, UConstants.BASE_PREFS_NAME);
		comment = sp.getString(key, null);
		if(comment!=null)
		{
			commentText.setText(comment);
		}
		preGridview.setAdapter(new UploadPicAdapter(this, listImage, imageLoader));
		preGridview.setOnItemClickListener(itemListener);
	}

	AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id)
		{
			if (position == (listImage.size() - 1) && position < 8)
			{
				showDialog(DIALOG_YES_NO_LONG_MESSAGE);
			}
		}

	};
	
	
	// 选择照片上传
	
		private static final int DIALOG_YES_NO_LONG_MESSAGE = 1;
		
		@Override
		protected Dialog onCreateDialog(int id) {
			// TODO Auto-generated method stub
			switch (id) {
			case DIALOG_YES_NO_LONG_MESSAGE:
	            return new AlertDialog.Builder(UploadPicActivity.this,AlertDialog.THEME_HOLO_LIGHT)
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
			backOperate();
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
	        Uri imageFilePath = UploadPicActivity.this.getContentResolver().insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);  
	        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFilePath); //这样就将文件的存储方式和uri指定到了Camera应用中  
	        
	        urlTempPic = getFilePathByContentResolver(UploadPicActivity.this, imageFilePath);
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

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (resultCode) {
		case RESULT_OK:
			if (requestCode == UConstants.CAPUTRE)
			{
				// Load up the image's dimensions not the image itself
				
				listImage.remove(listImage.size() - 1);
				listImage.add(urlTempPic);
				
				if (listImage.size() < 9)
				{
					listImage.add("add");
				}
				
				preGridview.setAdapter(new UploadPicAdapter(this, listImage, imageLoader));
			}
			else if (requestCode == UConstants.UPLOAD_PIC_REQUESTCODE)
			{
				allPath = data.getStringArrayExtra("all_path");
				listImage = new ArrayList<String>(Arrays.asList(allPath));
				if (listImage.size() < 9)
				{
					listImage.add("add");
				}
				
				eventId = getIntent().getIntExtra("event_id",0);
				
				SharedPreferences sp = UTools.Storage.getSharedPreferences(this, UConstants.BASE_PREFS_NAME);
				comment = sp.getString(key, null);
				if(comment!=null)
				{
					commentText.setText(comment);
				}
				preGridview.setAdapter(new UploadPicAdapter(this, listImage, imageLoader));
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v)
	{
		if (v == mBackView)
		{
			finish();
		}
		else if (v == mOkView)
		{
			
			final FLoadingProgressBarFragment mLoadingProgressBarFragment = new FLoadingProgressBarFragment();
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			mLoadingProgressBarFragment.show(ft, "dialog");
			comment = commentText.getText().toString();
			Map<String,String> params = new HashMap<String,String>(); 
			params.put("event_id", String.valueOf(eventId));
			params.put("content", comment);
			
			http.postData(UConstants.CREATE_RECORD_URL, params, this, new HHttpDataLoader.HDataListener(){

				@Override
				public void onFinish(String source)
				{
					JSONObject json;
					try
					{
						json = new JSONObject(source);
						if(json.optString("result","").equals("success"))
						{
							int recordId = json.optInt("record_id",0);
							if(recordId>0)
							{
								List<PhotoUploadModel> list = new ArrayList<PhotoUploadModel>();
								PhotoUploadModel model;
								for(String path:listImage)
								{
									model = new PhotoUploadModel();
									model.event_id = eventId;
									model.content = comment;
									model.path = path;
									model.pic_num = listImage.size();
									model.record_id = recordId;
									list.add(model);
								}
								DDBOpenHelper db = DDBOpenHelper.getInstance(UploadPicActivity.this);
								db.insertUploadPicModel(list);
								
								Intent intent = new Intent(UploadPicActivity.this,UploadPicService.class);
						        startService(intent);
							}
							else
							{
								UUtils.showNetErrorToast(UploadPicActivity.this);
							}
						}
						else
						{
							UUtils.showNetErrorToast(UploadPicActivity.this);
						}
					}
					catch (JSONException e)
					{
						UUtils.showNetErrorToast(UploadPicActivity.this);
						e.printStackTrace();
					}
					mLoadingProgressBarFragment.dismiss();
					finish();
				}

				@Override
				public void onFail(String msg)
				{
					mLoadingProgressBarFragment.dismiss();
					UUtils.showNetErrorToast(UploadPicActivity.this);
					
				}
				
			});
		}

	}
	
	private void backOperate()
	{
		String[] paths = new String[listImage.size()];
		for (int i = 0; i < listImage.size(); i++) {
			paths[i] = listImage.get(i);
		}
		String comment = commentText.getText().toString();
		Intent i = new Intent(this,CustomGalleryActivity.class)
					.putExtra("comment", comment)
					.putExtra("all_path", paths)
					.putExtra("from_upload", true);
//		startActivity(i);
		startActivityForResult(i, UConstants.UPLOAD_PIC_REQUESTCODE);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#finish()
	 */
	@Override
	public void finish()
	{
		comment = commentText.getText().toString();
		SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(this, UConstants.TEMP_DATA);
		mEditor.putString(key, comment);
		mEditor.commit();
		super.finish();
	}
	
	
	
	

}
