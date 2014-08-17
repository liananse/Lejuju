package com.medialab.lejuju.main.photowall.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.Toast;

import com.medialab.lejuju.util.UImageManager;
import com.medialab.lejuju.util.UTools;

public class DownloadImageView extends ImageView{

	public DownloadImageView(Context context) {
		super(context);
	}

	public DownloadImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DownloadImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		// TODO Auto-generated method stub
		String imagePath = UTools.Storage.getEventPicSaveImagePath();
		
		if (UImageManager.saveBtimapToFile(bm, imagePath))
		{
			Toast.makeText(getContext(), "图片已保存到"+imagePath, Toast.LENGTH_LONG).show();
		}
		
		super.setImageBitmap(bm);
	}
}