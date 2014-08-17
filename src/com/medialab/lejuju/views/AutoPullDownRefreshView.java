package com.medialab.lejuju.views;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.medialab.lejuju.R;

public class AutoPullDownRefreshView extends FrameLayout implements OnGestureListener{

	// is move down
	private boolean isMoveDown = false;
	// is scroll far from top
	private boolean isScrollFarTop = false;
	// is scroll to top
	private boolean isScrollToTop = false;
	private int topViewHeight;
	private int bottomViewHeight;
	
	private Scroller mScroller;
	
	private boolean bAD = false;
	
	private Context context;
	private GestureDetector mGestureDetector;
	
	private View topView;
	private View bottomView;
	private int topViewHeightCurrentPotion = -2147483648;
	private boolean isHideTopView = false;
	// 滑动的类型
	private int scrollType;
	
	private OnRefreshAdapterDataListener mOnRefreshAdapterDataListener;
	// 是否启动，下拉顶部，刷新数据
	private boolean isCloseTopAllowRefersh = true;
	// 底部视图是否不需要滚动效果，不需要就直接显示
	private boolean hasbottomViewWithoutscroll = true;
	// 滚动是否结束
	private boolean isScrollStoped = false;
	// 是否第一次触摸
	private boolean isFristTouch = true;
	
	private boolean bAJ = false;
	private int bAL = this.bgColor;
	private static final int bAI = Color.parseColor("#00000000");
	private int bgColor = Color.parseColor("#ffffffff");
	
	private OnListViewTopListener mOnListViewTopListener;
	private OnListViewBottomListener mOnListViewBottomListener;
	
	private int topViewInitializeVisibility = View.INVISIBLE;
	private int bottomViewInitializeVisibility = View.INVISIBLE;
	private static int timeInterval = 400;
	public AutoPullDownRefreshView(Context context) {
		this(context,null);
	}

	public AutoPullDownRefreshView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public AutoPullDownRefreshView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mScroller = new Scroller(context, new AccelerateInterpolator());
		this.mGestureDetector = new GestureDetector(this);
		this.context = context;
	}
	
	
	@Override
	protected void onScrollChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		super.onScrollChanged(paramInt1, paramInt2, paramInt3, paramInt4);
		if (this.bAJ){
			if (this.topViewHeightCurrentPotion == -2147483648){
				this.topViewHeightCurrentPotion = this.topViewHeight;
			}
			if ((paramInt2 > this.topViewHeightCurrentPotion) || (this.bAL == bAI)){
				if ((paramInt2 > this.topViewHeightCurrentPotion) && (this.bAL != this.bgColor)){
					setBackgroundColor(this.bgColor);
					this.bAL = this.bgColor;
				}
			}else{
				setBackgroundResource(2130838685);
				this.bAL = bAI;
			}
		}
	}


	public final int getTopViewHeight(){
		return this.topViewHeight;
	}

	public final void setOnRefreshAdapterDataListener(OnRefreshAdapterDataListener paramcs)
	{
		this.mOnRefreshAdapterDataListener = paramcs;
	}

	public final void setOnListViewTopListener(OnListViewTopListener paramei)
	{
		this.mOnListViewTopListener = paramei;
	}

	public final void setOnListViewBottomListener(OnListViewBottomListener parames)
	{
		this.mOnListViewBottomListener = parames;
	}

	public final void setIsCloseTopAllowRefersh(boolean paramBoolean)
	{
		this.isCloseTopAllowRefersh = paramBoolean;
	}

	public final void setHasbottomViewWithoutscroll(boolean paramBoolean)
	{
		this.hasbottomViewWithoutscroll = paramBoolean;
	}

	public final void setTopViewInitialize(boolean paramBoolean)
	{
		int i;
		if (!paramBoolean)
			i = 4;
		else
			i = 0;
		this.topViewInitializeVisibility = i;
		if (this.topView != null)
			this.topView.setVisibility(this.topViewInitializeVisibility);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent paramMotionEvent) {
		boolean bool = true;
		if (this.isFristTouch )
		{
			if (this.mOnListViewTopListener != null){
				this.isScrollToTop = this.mOnListViewTopListener.getIsListViewToTop();
			}else{
				this.isScrollToTop = false;
			}
			if (this.mOnListViewBottomListener != null){
				this.isScrollFarTop = this.mOnListViewBottomListener.getIsListViewToBottom();
			}else{
				this.isScrollFarTop = false;
			}
			if (this.topViewInitializeVisibility == View.VISIBLE){
				if (!this.isCloseTopAllowRefersh){
					this.topView.setVisibility(View.VISIBLE);
				}else{
					this.topView.setVisibility(View.INVISIBLE);
				}
			}
			if (this.bottomViewInitializeVisibility == 0){
				if (!this.hasbottomViewWithoutscroll){
					this.bottomView.setVisibility(0);
				}else{
					this.bottomView.setVisibility(4);
				}
			}
			if (paramMotionEvent.getAction() != MotionEvent.ACTION_UP) {
				if (paramMotionEvent.getAction() != MotionEvent.ACTION_CANCEL){
					if (!this.mGestureDetector.onTouchEvent(paramMotionEvent)){
						bool = super.dispatchTouchEvent(paramMotionEvent);
					} else {
						paramMotionEvent.setAction(MotionEvent.ACTION_CANCEL);
						bool = super.dispatchTouchEvent(paramMotionEvent);
					}
				}else{
					startScroll();
				}
			}else{
				startScroll();
				bool = super.dispatchTouchEvent(paramMotionEvent);
			}
		}
		return bool;

	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (!this.mScroller.computeScrollOffset()){
			if (this.isScrollStoped){
				this.isScrollStoped = false;
				this.mHandler.sendEmptyMessageDelayed(0, timeInterval);
			}
		}else{
			scrollTo(this.mScroller.getCurrX(), this.mScroller.getCurrY());
			postInvalidate();
		}
		isFristTouch = this.mScroller.isFinished();
	}	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()){
		case MotionEvent.ACTION_UP:
			if (getScrollY() - this.topViewHeight < 0){
				this.isScrollToTop = true;
			}
			if (getScrollY() > this.bottomViewHeight){
				this.isScrollFarTop = true;
			}
			startScroll();
		}
		return true;
	}

	public final void startTopScroll(){
		if (!this.isCloseTopAllowRefersh){
			if (this.topView.getVisibility() == View.INVISIBLE){
				this.mScroller.startScroll(0, getScrollY(), 0, -getScrollY() + this.topViewHeight, 200);
			}
			if (this.topView.getVisibility() == View.VISIBLE){
				this.mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), 200);
			}
			this.scrollType = 0;
			this.isScrollStoped = true;
			this.isFristTouch = false;
		}else{
			//Log.e("MMPullDownView.java", "startTopScroll()--02************************");
			this.mScroller.startScroll(0, getScrollY(), 0, -getScrollY() + this.topViewHeight, 200);
		}
		postInvalidate();
	}

	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (scrollType) {
			case 0:
				if(mOnRefreshAdapterDataListener != null){
					mOnRefreshAdapterDataListener.refreshData();
				}
				if(topView.getVisibility() != View.VISIBLE){
					break;
				}
				scrollTo(0, topViewHeight);
				break;
			case 1:
				if(bottomView.getVisibility() != View.VISIBLE){
					break;
				}
				scrollTo(0, bottomViewHeight);
				break;

			}
			startScroll();
		};
	};
	
	private void startScroll() {
		if (getScrollY() - this.topViewHeight < 0)
		{
			if (!this.isCloseTopAllowRefersh)
			{
				if (this.topView.getVisibility() == View.INVISIBLE)
				{
					this.mScroller.startScroll(0, getScrollY(), 0, -getScrollY() + this.topViewHeight, 200);
				}
				if (this.topView.getVisibility() == View.VISIBLE)
				{
					this.mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), 200);
				}
				this.scrollType = 0;
				this.isScrollStoped = true;
				this.isFristTouch = false;
			}
			else
			{
				this.mScroller.startScroll(0, getScrollY(), 0, -getScrollY() + this.topViewHeight, 200);
			}
			postInvalidate();
		}
		if (getScrollY() > this.bottomViewHeight)
		{
			if (!this.hasbottomViewWithoutscroll)
			{
				if (this.bottomView.getVisibility() == View.INVISIBLE)
					this.mScroller.startScroll(0, getScrollY(), 0, this.bottomViewHeight - getScrollY(), 200);
				if (this.bottomView.getVisibility() == View.VISIBLE)
					this.mScroller.startScroll(0, getScrollY(), 0, this.bottomViewHeight - getScrollY() + this.bottomViewHeight, 200);
				this.scrollType = 1;
				this.isScrollStoped = true;
				this.isFristTouch = false;
			} else{
				this.mScroller.startScroll(0, getScrollY(), 0, this.bottomViewHeight - getScrollY(), 200);
			}
			postInvalidate();
		}
		this.isMoveDown = false;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		if (!this.bAD)
		{
			View localView2 = inflate(this.context, R.layout.auto_pull_down_loading_view, null);
			View localView1 = inflate(this.context, R.layout.auto_pull_down_loading_view, null);
			addView(localView2, 0, new FrameLayout.LayoutParams(-1, -2));
			addView(localView1, new FrameLayout.LayoutParams(-1, -2));
			this.bAD = true;
		}
		int m = getChildCount();
		int j = 0;
		int i = 0;
		while (true)
		{
			if (j >= m)
			{
				this.topView = getChildAt(0);
				this.bottomView = getChildAt(-1 + getChildCount());
				this.topView.setVisibility(View.INVISIBLE);
				this.bottomView.setVisibility(View.INVISIBLE);
				this.topViewHeight = this.topView.getHeight();
				this.bottomViewHeight = this.bottomView.getHeight();
				this.topViewHeightCurrentPotion = this.topViewHeight;
				if ((!this.isHideTopView) && (this.topViewHeight != 0)){
					this.isHideTopView = true;
					scrollTo(0, this.topViewHeight);
				}
				return;
			}
			View localView3 = getChildAt(j);
			int k = localView3.getMeasuredHeight();
			if (localView3.getVisibility() != 8){
				localView3.layout(0, i, localView3.getMeasuredWidth(), i + k);
				i += k;
			}
			j++;
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public boolean onDown(MotionEvent e)
	{
		// TODO Auto-generated method stub
		if (!this.mScroller.isFinished())
		{
			this.mScroller.abortAnimation();
		}
		
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		// distanceY > 0 means move down
		if (distanceY <= 0.0F)
		{
			this.isMoveDown = false;
		}
		else
		{
			this.isMoveDown = true;
		}
		
		int i = -1;
		int j = 1;
		int k;
		
		if ((!this.isMoveDown || !this.isScrollFarTop) && (this.isMoveDown || (getScrollY() - this.topViewHeight <= 0) || !this.isScrollFarTop))
		{
			if ((this.isMoveDown || !this.isScrollToTop) && (!this.isMoveDown || (getScrollY() - this.topViewHeight >= 0) || !this.isScrollToTop))
			{
				j = 0;
			}
			else
			{
				// 手势往下滑动，界面从顶部弹起
				k = (int)(0.5D * distanceY);
				if (k != 0){
					i = k;
				}else if (distanceY > 0.0F){
					i = j;
				}
				if (i + getScrollY() > this.topViewHeight)
					i = this.topViewHeight - getScrollY();
				scrollBy(0, i);
				return true;
			}
		}
		else{
			// 手势往上滑动，界面从底部弹起
			k = (int)(0.5D * distanceY);
			if (k != 0){
				i = k;
			}else if (distanceY > 0.0F){
				i = j;
			}
			if ((i + getScrollY() < this.topViewHeight) && (!this.isMoveDown))
				i = this.topViewHeight - getScrollY();
			scrollBy(0, i);
			return true;
		}
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public abstract interface OnRefreshAdapterDataListener
	{
	  public abstract void refreshData();
	}
	
	public abstract interface OnListViewBottomListener
	{
	  public abstract boolean getIsListViewToBottom();
	}
	
	public abstract interface OnListViewTopListener
	{
	  public abstract boolean getIsListViewToTop();
	}

}
