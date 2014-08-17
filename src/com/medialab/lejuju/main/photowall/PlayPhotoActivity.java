package com.medialab.lejuju.main.photowall;


import java.net.ContentHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.fragment.FLoadingProgressBarFragment;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.imageload.ImageLoader;
import com.medialab.lejuju.main.photowall.model.PPhotoWallPicModel;
import com.medialab.lejuju.model.EventItemModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UDisplayWidth;
import com.medialab.lejuju.util.UUtils;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.TextView;

public class PlayPhotoActivity extends MBaseActivity implements AnimationListener
{
	private ImageView show_image;
	private TextView show_text;
	private View show_text_bg;
	private int playPosition=0;
	private int loadPosition=0;
	private Animation anim_image;
	private Animation anim_text;
	private Animation anim_text_bg;
	public MediaPlayer  mMediaPlayer;
	private EventItemModel mEventItemModel = null;
	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	private ImageLoader imageLoader = null;
	private int last_id = 0;
	private boolean hasNextPage = false;
	private List<PPhotoWallPicModel> list = new ArrayList<PPhotoWallPicModel>();
//	private FLoadingProgressBarFragment mLoadingProgressBarFragment;
	private boolean start = false;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		setContentView(R.layout.activity_play_photo);
		mEventItemModel = (EventItemModel) getIntent().getSerializableExtra(UConstants.EVENT_DETAIL_KEY);
		imageLoader = new ImageLoader(this,10);
		initView();
		getPhotosData(last_id);
	}
	
	private void initView()
	{
		show_image = (ImageView) findViewById(R.id.show_image);
		show_text = (TextView) findViewById(R.id.show_text);
		show_text_bg = findViewById(R.id.show_text_bg);
		
		anim_image = AnimationUtils.loadAnimation(this, R.anim.anim_image);
		anim_image.setRepeatCount(Animation.INFINITE);  
		anim_image.setRepeatMode(Animation.REVERSE);  
		anim_image.setAnimationListener(this);
		
		anim_text = AnimationUtils.loadAnimation(this, R.anim.anim_text);
		anim_text_bg = AnimationUtils.loadAnimation(this, R.anim.push_right_in);
		
//		mMediaPlayer = MediaPlayer.create(this, R.raw.play);
//		mMediaPlayer.setLooping(true);
		
	}
	
	ImageLoader.Callback callBack = new ImageLoader.Callback()
	{

		@Override
		public void onImageLoaded(ImageView view, String url)
		{
			view.startAnimation(anim_image);
			show_text.startAnimation(anim_text);
			show_text_bg.startAnimation(anim_text_bg);
		}

		@Override
		public void onImageError(ImageView view, String url, Throwable error)
		{
			// TODO Auto-generated method stub
			
		}
		
	};

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		
		try
		{
			mMediaPlayer.start();
		}
		catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalStateException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void getPhotosData(int last_id)
	{
		if (mEventItemModel != null)
		{
			Map<String, String> params = new HashMap<String, String>();
			
			params.put("head_width", String.valueOf(UDisplayWidth.getSmallHeadPicWidth(this)));
			params.put("width", String.valueOf(UDisplayWidth.getPhotoPicWidth(this)));
			params.put("event_id", String.valueOf(mEventItemModel.event_id));
			params.put("last_id", String.valueOf(last_id));
			params.put("page_size", "10");
			params.put("sort", "asc");
			mDataLoader.getData(UConstants.EVENT_DETAIL_PHOTO_URL, params, this, new HHttpDataLoader.HDataListener()
			{
				@Override
				public void onFinish(String source)
				{
					Gson gson = new Gson();
					
					try {
						TempModel mTempModel = gson.fromJson(source, new TypeToken<TempModel>(){}.getType());
						
						if(mTempModel != null && mTempModel.result.equals("success"))
						{
							HashMap<String, Object> result = new HashMap<String, Object>();
							result.put("data", mTempModel.picurls);
							result.put("last_id", mTempModel.last_id);
							result.put("hasNextPage", mTempModel.have_next_page);
							handler.sendMessage(handler.obtainMessage(UConstants.MESSAGE_DATA_OK, result));
						}
						else {
							UUtils.showNetErrorToast(PlayPhotoActivity.this);
						}
					} catch (JsonSyntaxException e) {
						// TODO Auto-generated catch block
						UUtils.showNetErrorToast(PlayPhotoActivity.this);
					}
					
				}

				@Override
				public void onFail(String msg)
				{
					UUtils.showNetErrorToast(PlayPhotoActivity.this);
				}
			});
		}
	}
	
	class TempModel
	{
		public String result = "";
		public String message = "";
		public int last_id = 0;
		public boolean have_next_page = false;
		
		public List<PPhotoWallPicModel> picurls = null;
	}
	
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		if(mMediaPlayer.isPlaying())
		{
			mMediaPlayer.stop();
		}
	}
	

	Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case UConstants.PLAY_ANIM:
					showPic();
					break;
				case UConstants.MESSAGE_DATA_OK:
					Log.i("BitmapCache","MESSAGE_DATA_OK");
					updateData((HashMap<String, Object>) msg.obj);
					break;
//				case UConstants.DOWN_IMAGE_OK:
//					String uri = (String) msg.obj;
//					Log.i("juju","DOWN_IMAGE_OK "+uri);
//					if(uri.equals(list.get(0).big_pic_url))
//					{
//						Log.i("juju","show "+uri);
//						mLoadingProgressBarFragment.dismiss();
//						show_image.setImageBitmap(imageLoader.getBitmap(list.get(0).big_pic_url));
//						show_image.startAnimation(anim_image);
//						show_text.startAnimation(anim_text);
//						show_text_bg.startAnimation(anim_text_bg);
//					}
//					else if(finalBitmap.isCache(list.get(playPosition).big_pic_url) && stop)
//					{
//						showPic();
//					}
//					break;
				default:
					break;
			}
			
		}
		
	};
	
	private void showPic()
	{
		Log.i("BitmapCache","UPDATE_UI");
		if(playPosition<list.size()-1)
		{
			playPosition++;
			Bitmap pic = imageLoader.getBitmap(list.get(playPosition).big_pic_url);
			if(pic!=null)
			{
//				imageLoader.removeBitmap(list.get(playPosition-1).big_pic_url);
				Log.i("BitmapCache","show "+list.get(playPosition).big_pic_url);
				imageLoader.bind(show_image, list.get(playPosition).big_pic_url, callBack, 0);
				if(list.size()-1>loadPosition)
				{
					loadPosition++;
					Log.i("BitmapCache","loadPosition="+loadPosition);
					imageLoader.preload(list.get(loadPosition).big_pic_url,true);
				}
			}
			else
			{
				Log.i("BitmapCache","bind "+list.get(playPosition).big_pic_url);
				imageLoader.bind(show_image, list.get(playPosition).big_pic_url, callBack, 0);
				if(list.size()-1>loadPosition)
				{
					loadPosition++;
					Log.i("BitmapCache","loadPosition="+loadPosition);
					imageLoader.preload(list.get(loadPosition).big_pic_url,true);
				}
			}
		}
	}
	
	protected void updateData(HashMap<String, Object> result)
	{
		if (result != null)
		{
			List<PPhotoWallPicModel> data = (List<PPhotoWallPicModel>) result.get("data");
			List<String> uriList = null;
			if(data!=null)
			{
				uriList = new ArrayList<String>();
				for(int i=0;i<data.size();i++)
				{
					uriList.add(data.get(i).big_pic_url);
//					imageLoader.preload(data.get(i).big_pic_url);
//					Log.i("juju",data.get(i).big_pic_url);
				}
			}
			list.addAll(data);
			Boolean hasNextPage = (Boolean) result.get("hasNextPage");
			this.last_id = (Integer) result.get("last_id");
			this.hasNextPage = hasNextPage;
			if(hasNextPage)
			{
				getPhotosData(last_id);
			}
			if(!start)
			{
				start = true;
				imageLoader.bind(show_image, list.get(0).big_pic_url, callBack, 0);
				while(list.size()>loadPosition && loadPosition<3)
				{
					//预先读取三张
					loadPosition++;
					Log.i("BitmapCache","loadPosition="+loadPosition);
					imageLoader.preload(list.get(loadPosition).big_pic_url,true);
				}
			}
		}
	}

	@Override
	public void onAnimationEnd(Animation animation)
	{
		handler.sendMessage(handler.obtainMessage(UConstants.PLAY_ANIM));
		
	}

	@Override
	public void onAnimationRepeat(Animation animation)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation animation)
	{
		// TODO Auto-generated method stub
		
	}
	
	
	
}
