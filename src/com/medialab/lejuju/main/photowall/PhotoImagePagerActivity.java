package com.medialab.lejuju.main.photowall;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import uk.co.senab.photoview.PhotoView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.db.DDBOpenHelper;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.http.HHttpDataLoader.HDataListener;
import com.medialab.lejuju.main.photowall.model.PPhotoWallPicModel;
import com.medialab.lejuju.main.photowall.views.DownloadImageView;
import com.medialab.lejuju.model.EventItemModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UDisplayWidth;

public class PhotoImagePagerActivity extends MBaseActivity{

	private List<PPhotoWallPicModel> mPPhotoWallPicModels;
	private FinalBitmap mFinalBitmap;
	private int position;
	private boolean isSelfCreate;
	private String big_pic_url;
	private long pic_id;
	private int event_id;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_photo_image_pager);
		
		getIntentData();
		findViewById();
		setListener();
		init();
	}
	
	private void getIntentData()
	{
		mPPhotoWallPicModels = (List<PPhotoWallPicModel>) getIntent().getSerializableExtra(UConstants.PHOTO_WALL_PIC_KEY); 
		pic_id = getIntent().getLongExtra("pic_id", 0);
		event_id = getIntent().getIntExtra("event_id", 0);
		isSelfCreate = getIntent().getBooleanExtra("isSelfCreate", false);
	}
	
	ViewPager pager;
	private DownloadImageView downloadImageView;
	private LinearLayout photosdetail_top;
	private LinearLayout photosdetail_bottom;
	private ImageView photosdetail_back;
	private ImageView photosdetail_menu;
	private TextView photosdetail_caption;
	private TextView photosdetail_viewcount;
	private void findViewById()
	{
		pager = (ViewPager) findViewById(R.id.pager);
		photosdetail_top = (LinearLayout) photosdetail_top;
		photosdetail_bottom = (LinearLayout) photosdetail_bottom;
		photosdetail_back = (ImageView) findViewById(R.id.photosdetail_back);
		photosdetail_menu = (ImageView) findViewById(R.id.photosdetail_menu);
		photosdetail_caption = (TextView) findViewById(R.id.photosdetail_caption);
		photosdetail_viewcount = (TextView) findViewById(R.id.photosdetail_viewcount);
		downloadImageView = (DownloadImageView) findViewById(R.id.downloadImageView);
	}
	
	private void init()
	{
		mFinalBitmap = FinalBitmap.create(this);
		pager.setAdapter(new ImagePagerAdapter(mPPhotoWallPicModels, this));
		
		for (int i = 0; i < mPPhotoWallPicModels.size(); i++) {
			if (mPPhotoWallPicModels.get(i).id == pic_id)
			{
				position = i;
				break;
			}
		}
		
		photosdetail_caption.setText("来自" + mPPhotoWallPicModels.get(position).user_nick_name);
    	photosdetail_viewcount.setText(mPPhotoWallPicModels.get(position).content);
    	
    	big_pic_url = mPPhotoWallPicModels.get(position).big_pic_url;
    	pic_id = mPPhotoWallPicModels.get(position).id;
    	
    	pager.setCurrentItem(position);
		pager.setOnPageChangeListener(new MyOnPageChangeListener());
	}
	
	private void setListener()
	{
		photosdetail_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PhotoImagePagerActivity.this.finish();
			}
		});
		photosdetail_menu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isSelfCreate)
				{
					showDialog(DIALOG_SELF);
				}
				else {
					showDialog(DIALOG_OTHER);
				}
			}
		});
	}
	
	public class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
        	photosdetail_caption.setText("来自" + mPPhotoWallPicModels.get(arg0).user_nick_name);
        	photosdetail_viewcount.setText(mPPhotoWallPicModels.get(arg0).content);
        	
        	big_pic_url = mPPhotoWallPicModels.get(arg0).big_pic_url;
        	pic_id = mPPhotoWallPicModels.get(arg0).id;
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

	private class ImagePagerAdapter extends PagerAdapter {

		private List<PPhotoWallPicModel> mPPhotoWallPicModels;
		private LayoutInflater inflater;

		ImagePagerAdapter(List<PPhotoWallPicModel> mPhotoWallPicModels, Context context) {
			this.mPPhotoWallPicModels = mPhotoWallPicModels;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {
			if (mPPhotoWallPicModels != null)
			{
				return mPPhotoWallPicModels.size();
			}
			return 0;
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
			
			PhotoView imageView = (PhotoView) imageLayout.findViewById(R.id.image);
			mFinalBitmap.display(imageView, mPPhotoWallPicModels.get(position).big_pic_url);
			((ViewPager) view).addView(imageLayout, 0);
			
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.gc();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
	}

	private static final int DIALOG_SELF = 1;
    private static final int DIALOG_OTHER = 2;
    
    
    private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
        case DIALOG_SELF:
        	return new AlertDialog.Builder(PhotoImagePagerActivity.this, AlertDialog.THEME_HOLO_LIGHT)
            .setTitle("选项")
            .setItems(R.array.dialog_self, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	if (which == 0)
                	{
                		// 设置活动封面
                		Map<String, String> params = new HashMap<String, String>();
                		params.put("event_id", String.valueOf(event_id));
                		params.put("event_pic_width", String.valueOf(UDisplayWidth.getPosterPicWidth(PhotoImagePagerActivity.this)));
                		params.put("pic_id", String.valueOf(pic_id));
                		
                		mDataLoader.postData(UConstants.SETTING_PIC_URL, params, PhotoImagePagerActivity.this, new HDataListener() {
							
							@Override
							public void onFinish(String source) {
								// TODO Auto-generated method stub
								Gson gson = new Gson();
								try {
									TempModel mTempModel = gson.fromJson(source, new TypeToken<TempModel>(){}.getType());
									
									if (mTempModel != null)
									{
										Toast.makeText(PhotoImagePagerActivity.this, mTempModel.message, Toast.LENGTH_SHORT).show();
										DDBOpenHelper mDdbOpenHelper = DDBOpenHelper.getInstance(PhotoImagePagerActivity.this);
										mDdbOpenHelper.updateLocalEventModel(mTempModel.event);
									}
									else {
										Toast.makeText(PhotoImagePagerActivity.this, "数据解析出错", Toast.LENGTH_SHORT).show();
									}
									
								} catch (JsonSyntaxException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									
									Toast.makeText(PhotoImagePagerActivity.this, "数据解析出错", Toast.LENGTH_SHORT).show();
								}
							}
							
							@Override
							public void onFail(String msg) {
								// TODO Auto-generated method stub
								Toast.makeText(PhotoImagePagerActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
							}
						});
                	}
                	else if (which == 1)
                	{
                		// 设置删除图片
                		Map<String, String> params = new HashMap<String, String>();
                		params.put("pic_id", String.valueOf(pic_id));
                		params.put("method", "delete");
                		
                		mDataLoader.postData(UConstants.DELETE_PIC_URL, params, PhotoImagePagerActivity.this, new HDataListener() {
							
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
                	else if (which == 2)
                	{
                		// 保存图片到本地
                		mFinalBitmap.display(downloadImageView, big_pic_url);
                	}
                }
            })
            .create();
        case DIALOG_OTHER:
        	return new AlertDialog.Builder(PhotoImagePagerActivity.this, AlertDialog.THEME_HOLO_LIGHT)
            .setTitle("选项")
            .setItems(R.array.dialog_other, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	if (which == 0)
                	{
                		mFinalBitmap.display(downloadImageView, big_pic_url);
                	}
                }
            })
            .create();
        }
        return null;
	}
	
	
	class TempModel
	{
		String result = "";
		String message = "";
		EventItemModel event = null;
	}
}
