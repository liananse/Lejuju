package com.medialab.lejuju.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.medialab.lejuju.R;

public class EDListView extends ListView {

	private Context mContext;
	private Scroller mScroller;
	private View headerView = null;
	
	View headerImageView;
	EDTool tool;
	int top;
	float startY, currentY;
	int headerImageViewH;
	boolean scrollerType;
	static final int len = 0xc8;

	public EDListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public EDListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		mScroller = new Scroller(mContext);
	}

	public EDListView(Context context) {
		super(context);
	}
	
	public void setHeaderView(View headerView)
	{
		this.headerView = headerView;
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		int action = event.getAction();
		if (!mScroller.isFinished() || headerView == null)
		{
			return super.onTouchEvent(event);
		}
		
		headerImageView = headerView.findViewById(R.id.item_header_imageview);
		currentY = event.getY();
		switch (action)
		{
			case MotionEvent.ACTION_DOWN:
				top = headerImageView.getBottom();
				headerImageViewH = headerImageView.getHeight();
				startY = currentY;
				tool = new EDTool(headerImageView.getLeft(), headerImageView.getBottom(),
						headerImageView.getLeft(), headerImageView.getBottom() + len);
				break;
			case MotionEvent.ACTION_MOVE:
				if (headerView.isShown())
				{
					if (tool != null) {
						int t = tool.getScrollY(currentY - startY);
						if (t >= top && t <= headerView.getBottom() + len) {
							headerImageView.setLayoutParams(new RelativeLayout.LayoutParams(
									headerImageView.getWidth(), t));
						}
					}
					scrollerType = false;
				}
				break;
			case MotionEvent.ACTION_UP:
				scrollerType = true;
				mScroller.startScroll(headerImageView.getLeft(), headerImageView.getBottom(),
						0 - headerImageView.getLeft(), headerImageViewH - headerImageView.getBottom(), 200);
				invalidate();
				break;
		}

		return super.dispatchTouchEvent(event);
	}

	public void computeScroll() {
		if (mScroller.computeScrollOffset())
		{
			int x = mScroller.getCurrX();
			int y = mScroller.getCurrY();
			headerImageView.layout(0, 0, x + headerImageView.getWidth(), y);
			invalidate();
			if (!mScroller.isFinished() && scrollerType && y > 200)
			{
				headerImageView.setLayoutParams(new RelativeLayout.LayoutParams(headerImageView.getWidth(), y));
			}
		}
	}
}
