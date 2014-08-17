package com.medialab.lejuju.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundImageView extends ImageView{

	public RoundImageView(Context context) {
		super(context);
	}

	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        int roundPx = bitmap.getWidth() / 2;
        int roundPy = bitmap.getHeight() / 2;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        if (roundPx > roundPy)
        {
        	canvas.drawCircle(roundPx, roundPy, roundPy, paint);
        }
        else {
        	canvas.drawCircle(roundPx, roundPy, roundPx, paint);
		}
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

	@Override
	public void setImageBitmap(Bitmap bm) {
		// TODO Auto-generated method stub
		super.setImageBitmap(getRoundedCornerBitmap(bm));
	}

}