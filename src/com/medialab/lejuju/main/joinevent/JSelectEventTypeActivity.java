package com.medialab.lejuju.main.joinevent;

import java.io.File;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.main.joinevent.adapter.JEventTypeAdapter;
import com.medialab.lejuju.main.joinevent.model.JEventTypeModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UImageManager;
import com.medialab.lejuju.util.UTools;

public class JSelectEventTypeActivity extends MBaseActivity implements OnClickListener{
	
	private LinearLayout linearLayoutLeft;
	private LinearLayout linearLayoutRight;

	public int event_type_id = 0;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_select_event_type);
		
		initView();
	}
	
	private View join_event_code_by_code;
	private void initView()
	{
		linearLayoutLeft = (LinearLayout) findViewById(R.id.linear_layout_left);
		linearLayoutRight = (LinearLayout) findViewById(R.id.linear_layout_right);
		
		String source = UTools.IO.getStringFromTxt(this, "new_event_type.txt");
		
		Gson gson = new Gson();
		
		List<JEventTypeModel> list = gson.fromJson(source, new TypeToken<List<JEventTypeModel>>(){}.getType());
		
		JEventTypeAdapter mJEventTypeAdapter = new JEventTypeAdapter(this, list);
		
		for (int i = 0; i < list.size(); i++) {
			
			if (0 == i % 2) {
				linearLayoutLeft.addView(mJEventTypeAdapter.getView(i, null, null));
			} else if(1== i % 2){
				linearLayoutRight.addView(mJEventTypeAdapter.getView(i, null, null));
			}
		}
		
		join_event_code_by_code = findViewById(R.id.join_event_code_by_code);
		join_event_code_by_code.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == join_event_code_by_code)
		{
			Intent intent = new Intent();
			intent.setClass(this, JSelectEventByCodeActivity.class);
			this.startActivity(intent);
		}
	}
	
	private static final int DIALOG_YES_NO_LONG_MESSAGE = 1;
	
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case DIALOG_YES_NO_LONG_MESSAGE:
            return new AlertDialog.Builder(JSelectEventTypeActivity.this,AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("提示")
                .setMessage("需要自己定义活动封面吗？")
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
                .setNegativeButton("不需要", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked Cancel so do some stuff */
                    	
                    	Intent intent = new Intent();
						intent.setClass(JSelectEventTypeActivity.this, JCreateEventActivity.class);
						Bundle bundle = new Bundle();
						bundle.putInt("event_type_id", event_type_id);
						bundle.putString("event_poster_path", "");
						intent.putExtras(bundle);
						JSelectEventTypeActivity.this.startActivity(intent);
                    }
                })
                .create();
		}
		
		return null;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) {
		case RESULT_OK:
			if (requestCode == UConstants.CAPUTRE)
			{
				// Load up the image's dimensions not the image itself
				try {
					BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
					bmpFactoryOptions.inJustDecodeBounds = true;
					Bitmap bmp = BitmapFactory.decodeFile(urlTempPic,
							bmpFactoryOptions);
					
					int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) 1080);

					if (widthRatio > 1)
					{
						bmpFactoryOptions.inSampleSize = widthRatio;
					}

					bmpFactoryOptions.inJustDecodeBounds = false;
					bmp = BitmapFactory.decodeFile(urlTempPic, bmpFactoryOptions);

					String imagePath = UTools.Storage.getEventDetailSmallImagePath();
					
					if (UImageManager.saveBtimapToFile(bmp, imagePath))
					{
						Intent intent = new Intent();
						intent.setClass(JSelectEventTypeActivity.this, JCreateEventActivity.class);
						Bundle bundle = new Bundle();
						bundle.putInt("event_type_id", event_type_id);
						bundle.putString("event_poster_path", imagePath);
						intent.putExtras(bundle);
						JSelectEventTypeActivity.this.startActivity(intent);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
		
					String imagePath = UTools.Storage.getEventDetailSmallImagePath();
					
					if (UImageManager.saveBtimapToFile(bmp, imagePath))
					{
						Intent intent = new Intent();
						intent.setClass(JSelectEventTypeActivity.this, JCreateEventActivity.class);
						Bundle bundle = new Bundle();
						bundle.putInt("event_type_id", event_type_id);
						bundle.putString("event_poster_path", imagePath);
						intent.putExtras(bundle);
						JSelectEventTypeActivity.this.startActivity(intent);
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
	
	/**
	 * @author liananse
	 * 2013-7-21
	 */
	private void startPhotoAlbum()
	{
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		
		startActivityForResult(intent, UConstants.GETIMAGE);
	}
	
	private String urlTempPic = "";
	
	private void startCamera()
	{
		Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		urlTempPic = UTools.Storage.getEventDetailImagePath();
		
		i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(urlTempPic)));
		startActivityForResult(i, UConstants.CAPUTRE);
	}
	

}
