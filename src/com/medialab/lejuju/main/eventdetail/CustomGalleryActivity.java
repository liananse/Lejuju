package com.medialab.lejuju.main.eventdetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.main.eventdetail.adapter.GalleryAdapter;
import com.medialab.lejuju.util.UTools;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class CustomGalleryActivity extends MBaseActivity implements OnClickListener {

	private boolean from_upload = false;
	
	GridView gridGallery;
	GalleryAdapter adapter;

	Handler handler;
	TextView title;
	Button btnGalleryOk;
	LinearLayout preView;
	Map<Integer,ImageView> cacheImageView = new HashMap<Integer,ImageView>();
	ArrayList<CustomGallery> dataList = null;
	String[] allPath;
	
	List<String> pathList;
	
	int eventId;

    private ImageLoader imageLoader;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery);
		from_upload = getIntent().getBooleanExtra("from_upload", false);
		allPath = getIntent().getStringArrayExtra("all_path");
		initHeaderBar();
        initImageLoader();
        findViewById();
		init();
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

    private void initImageLoader() {
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
    
    private void findViewById()
    {
    	eventId = getIntent().getIntExtra("event_id",0);
    	handler = new Handler();
    	title = (TextView) findViewById(R.id.title_num);
    	title.setText(String.format(getString(R.string.choose_upload_num),cacheImageView.size()));
    	
    	preView = (LinearLayout) findViewById(R.id.pre_pic);
    	gridGallery = (GridView) findViewById(R.id.gridGallery);
    	gridGallery.setFastScrollEnabled(true);
    	adapter = new GalleryAdapter(getApplicationContext(), imageLoader);
    	
    	findViewById(R.id.llBottomContainer).setVisibility(View.VISIBLE);
    	gridGallery.setOnItemClickListener(mItemMulClickListener);
    	
    	gridGallery.setAdapter(adapter);
    	
    	btnGalleryOk = (Button) findViewById(R.id.btnGalleryOk);
    	btnGalleryOk.setOnClickListener(mOkClickListener);
    	
    	UTools.UI.fitViewByWidth(this, btnGalleryOk, 110, 65, 640);
    }
    
	private void init() {
		new Thread() {

			@Override
			public void run() {
				Looper.prepare();
				handler.post(new Runnable() {

					@Override
					public void run() {
						dataList = getGalleryPhotos();
						
						if (from_upload)
						{
							for (String path:allPath)
							{
								for (int i = 0; i < dataList.size(); i++) {
									if (path.equals(dataList.get(i).sdcardPath))
									{
										dataList.get(i).isSeleted = true;
										
										ImageView view = new ImageView(CustomGalleryActivity.this);
										
										LayoutParams lp = new LayoutParams(getResources().getDimensionPixelSize(R.dimen.pre_view_pic),
												getResources().getDimensionPixelSize(R.dimen.pre_view_pic));
										lp.setMargins(getResources().getDimensionPixelSize(R.dimen.pre_view_padding),0,0,0);
										
										view.setLayoutParams(lp);
										view.setScaleType(ScaleType.CENTER_CROP);
										cacheImageView.put(i, view);
										imageLoader.displayImage("file://" + dataList.get(i).sdcardPath,view);
										preView.addView(view);
										
										title.setText(String.format(getString(R.string.choose_upload_num),cacheImageView.size()));
										break;
									}
								}
							}
						}
						adapter.addAll(dataList);
					}
				});
				Looper.loop();
			};

		}.start();

	}

	View.OnClickListener mOkClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			ArrayList<CustomGallery> selected = adapter.getSelected();

			String[] allPath = new String[selected.size()];
			for (int i = 0; i < allPath.length; i++) {
				allPath[i] = selected.get(i).sdcardPath;
			}

			Intent data = new Intent(CustomGalleryActivity.this,UploadPicActivity.class)
						.putExtra("all_path", allPath)
						.putExtra("event_id", eventId);
			
			if (from_upload)
			{
				setResult(RESULT_OK, data);
			}
			else
			{
				startActivity(data);
			}
			CustomGalleryActivity.this.finish();
		}
	};
	
	AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {
			
			
			if(dataList.get(position).isSeleted)
			{
				ImageView view = cacheImageView.get(position);
				preView.removeView(view);
				cacheImageView.remove(position);
				adapter.changeSelection(v, position);
			}
			else if(cacheImageView.size()<9)
			{
				ImageView view = new ImageView(CustomGalleryActivity.this);
				
				LayoutParams lp = new LayoutParams(getResources().getDimensionPixelSize(R.dimen.pre_view_pic),
						getResources().getDimensionPixelSize(R.dimen.pre_view_pic));
				lp.setMargins(getResources().getDimensionPixelSize(R.dimen.pre_view_padding),0,0,0);
				
				view.setLayoutParams(lp);
				view.setScaleType(ScaleType.CENTER_CROP);
				cacheImageView.put(position, view);
				imageLoader.displayImage("file://" + dataList.get(position).sdcardPath,view);
				preView.addView(view);
				adapter.changeSelection(v, position);
				
			}
			title.setText(String.format(getString(R.string.choose_upload_num),cacheImageView.size()));
			
		}
	};
	
	private ArrayList<CustomGallery> getGalleryPhotos() {
		ArrayList<CustomGallery> galleryList = new ArrayList<CustomGallery>();

		try {
			final String[] columns = { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media._ID };
			final String orderBy = MediaStore.Images.Media._ID;

			@SuppressWarnings("deprecation")
			Cursor imagecursor = managedQuery(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
					null, null, orderBy);
			if (imagecursor != null && imagecursor.getCount() > 0) {

				while (imagecursor.moveToNext()) {
					CustomGallery item = new CustomGallery();

					int dataColumnIndex = imagecursor
							.getColumnIndex(MediaStore.Images.Media.DATA);

					item.sdcardPath = imagecursor.getString(dataColumnIndex);
					galleryList.add(item);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

        // show newest photo at beginning of the list
		Collections.reverse(galleryList);
        return galleryList;
	}

	@Override
	public void onClick(View v)
	{
		if(v == mBackView)
		{
			finish();
		}
	}

}
