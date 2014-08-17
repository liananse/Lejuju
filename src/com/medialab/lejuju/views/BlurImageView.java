package com.medialab.lejuju.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.ImageView;

public class BlurImageView extends ImageView{

	private Context mContext;
	public BlurImageView(Context context) {
		super(context);
		this.mContext = context;
	}

	public BlurImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public BlurImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

	public Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		
		Bitmap newImg = Blur.fastblur(mContext, bitmap, 12);

        return newImg;
    }
	

	@Override
	public void setImageBitmap(Bitmap bm) {
		// TODO Auto-generated method stub
		// need improve 将bitmap高斯模糊的过程缓慢
		
		super.setImageBitmap(getRoundedCornerBitmap(bm));
//		super.setImageBitmap(bm);
	}

}