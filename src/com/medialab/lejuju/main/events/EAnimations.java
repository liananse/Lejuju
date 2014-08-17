package com.medialab.lejuju.main.events;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.medialab.lejuju.util.UConstants;

/**
 * 首页跳转动画
 * @author liananse
 * 2013.009.14
 */
public class EAnimations {

	private static Context mContext;
	
	public static void initEAnimation(Context context)
	{
		mContext = context;
	}
	
	public static void startItemAnimation(FrameLayout listItemView, ImageView frameImageView, final Intent intent)
	{
		// 将frameImageView 移动到listItemView 位置
		final ImageView img = frameImageView;
		
		final Animation moveAnimation = new TranslateAnimation(0F, 0F, listItemView.getTop(), 0F);
		moveAnimation.setFillAfter(true);
		moveAnimation.setDuration(350);
		moveAnimation.setStartOffset(0);
		
		img.setVisibility(View.VISIBLE);
		img.startAnimation(moveAnimation);
		
		moveAnimation.setAnimationListener(new Animation.AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				if (mContext instanceof EJoinEventEntryActivity)
				{
					((EJoinEventEntryActivity) mContext).content_view.setVisibility(View.GONE);
				}
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				if (mContext instanceof EJoinEventEntryActivity)
				{
					((EJoinEventEntryActivity) mContext).startActivityForResult(intent, UConstants.EVENT_DETAIL_REQUESTCODE);
				}
			}
		});

	}
	
	public static void endItemAnimation(int item_top, ImageView frameImageView)
	{
		final ImageView img = frameImageView;
		final Animation moveAnimation = new TranslateAnimation(0F, 0F, 0F, item_top);
		moveAnimation.setDuration(350);
		moveAnimation.setAnimationListener(new Animation.AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				img.clearAnimation();
				img.setVisibility(View.GONE);
				if (mContext instanceof EJoinEventEntryActivity)
				{
					((EJoinEventEntryActivity) mContext).content_view.setVisibility(View.VISIBLE);
				}
			}
			
		});
		
		img.startAnimation(moveAnimation);
	}
	
	public interface EAnimationsListener {

		public void onAnimationEnd();
	}
	
}
