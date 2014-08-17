package com.medialab.lejuju.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import com.medialab.lejuju.R;
import com.medialab.lejuju.db.DDBOpenHelper;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.main.photowall.model.PhotoUploadModel;
import com.medialab.lejuju.push.MessageType;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UImageManager;
import com.medialab.lejuju.util.UTools;

public class UploadPicService extends Service
{
	
	class UploadThread extends Thread
	{
		Context mContext;
		List<PhotoUploadModel> list;
		
		public UploadThread(Context mContext)
		{
			this.mContext = mContext;
		}

		@Override
		public void run()
		{
			Looper.prepare();
			System.out.println("run thread ");
			DDBOpenHelper db = DDBOpenHelper.getInstance(mContext);
			list = db.getNotUploadPhoto();
			if(list!=null && list.size()>0)
			{
				showNotification("开始上传图片");
				HHttpDataLoader http = new HHttpDataLoader();
				System.out.println("upload size="+list.size());
				for(PhotoUploadModel m:list)
				{
					final PhotoUploadModel model = m;
					File file = new File(model.path);
					if(file.exists())
					{
						
						try {
							BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
							bmpFactoryOptions.inJustDecodeBounds = true;
							
							Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(file), null, bmpFactoryOptions);
							
							int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth
									/ (float) 1080);
							if (widthRatio > 1) {
								bmpFactoryOptions.inSampleSize = widthRatio;
							}
							
							bmpFactoryOptions.inJustDecodeBounds = false;
							bmp = BitmapFactory.decodeStream(new FileInputStream(file),null, bmpFactoryOptions);
				
							String imagePath = UTools.Storage.getEventDetailSmallImagePath();
							
							if(UImageManager.saveBtimapToFile(bmp, imagePath))
							{
								Map<String,String> params = new HashMap<String,String>();
								params.put("event_id", String.valueOf(model.event_id));
								params.put("content", model.content);
								params.put("record_id", String.valueOf(model.record_id));
								params.put("pic_num", String.valueOf(model.pic_num));
								params.put("image", "image:"+imagePath);
								http.postData(UConstants.UPLOAD_PHOTO_RECORD, params, mContext, new HHttpDataLoader.HDataListener()
								{
									
									@Override
									public void onFinish(String source)
									{
										System.out.println("upload source="+source);
										System.out.println("upload "+model.path);
										JSONObject json;
										try
										{
											json = new JSONObject(source);
											if(json.optString("result","").equals("success"))
											{
												DDBOpenHelper db = DDBOpenHelper.getInstance(mContext);
												db.updatePhotoUploadModel(model.id);
												System.out.println("update id="+model.id);
											}
										}
										catch (JSONException e)
										{
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
									}
									
									@Override
									public void onFail(String msg)
									{
										// TODO Auto-generated method stub
										
									}
								});
							}
							
						} catch (Exception e) {
							// TODO: handle exception
						}
						
					}
					else
					{
						db.updatePhotoUploadModel(model.id);
					}
				}
				handler.sendEmptyMessage(0);
			}
			Looper.loop();
			
		}
		
	}
	
	Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			System.out.println("stop service ");
			UploadPicService.this.stopSelf();
			showNotification("上传图片成功");
		}
		
	};
	
	private NotificationManager mNM;
	/* (non-Javadoc)
	 * @see android.app.Service#onStart(android.content.Intent, int)
	 */
	@Override
	public void onStart(Intent intent, int startId)
	{
		System.out.println("start service ");
		UploadThread t = new UploadThread(this);
		t.start();
		super.onStart(intent, startId);
		
	}
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}


	private void showNotification(String text) {
        // In this sample, we'll use the same text for the ticker and the expanded notification
		
		Notification messageNotification = new Notification();
		messageNotification.icon = R.drawable.ic_launcher;

		messageNotification.tickerText = text;
		messageNotification.defaults = Notification.DEFAULT_SOUND;
		messageNotification.setLatestEventInfo(getApplicationContext(), text, "", null);  
		mNM.notify(23, messageNotification);
    }

	/* (non-Javadoc)
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		return super.onStartCommand(intent, flags, startId);
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onUnbind(android.content.Intent)
	 */
	@Override
	public boolean onUnbind(Intent intent)
	{
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
