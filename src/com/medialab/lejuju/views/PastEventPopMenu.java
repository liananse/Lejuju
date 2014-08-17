package com.medialab.lejuju.views;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.lejuju.R;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.http.HHttpDataLoader.HDataListener;
import com.medialab.lejuju.main.comment.CEventAlbumsCommentActivity;
import com.medialab.lejuju.model.EventItemModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTools;

public class PastEventPopMenu extends PopupWindow {
	
	private Context mContext;
	private ImageView mCommemtImageView;
	private ImageView mLikeImageView;
	private View mCommentView;
	private EventItemModel mEventItemModel = null;
	
	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	
	public void setEventItemModel(EventItemModel mEventItemModel)
	{
		this.mEventItemModel = mEventItemModel;
		
		if (mEventItemModel != null)
		{
			if (mEventItemModel.zan)
			{
				mLikeImageView.setImageResource(R.drawable.comment_pop_cancel_like_bg);
			}
			else {
				mLikeImageView.setImageResource(R.drawable.comment_pop_like_bg);
			}
		}
	}
	//实例化一个矩形
	private Rect mRect = new Rect();
	private int popupGravity = Gravity.NO_GRAVITY;
	
	//坐标的位置（x、y）
	private final int[] mLocation = new int[2];
	
	public PastEventPopMenu(Context context){
		//设置布局的参数
		this(context, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}
	
	public PastEventPopMenu(Context context, int width, int height){
		this.mContext = context;
		
		//设置可以获得焦点
		setFocusable(true);
		//设置弹窗内可点击
		setTouchable(true);	
		//设置弹窗外可点击
		setOutsideTouchable(false);
		
		//设置弹窗的宽度和高度
		setBackgroundDrawable(new BitmapDrawable());
		
		//设置弹窗的布局界面
		setContentView(LayoutInflater.from(mContext).inflate(R.layout.past_event_popmenu, null));
		mCommentView = getContentView().findViewById(R.id.comment_pop);
		mCommemtImageView = (ImageView) getContentView().findViewById(R.id.comment_pop_com);
		mLikeImageView = (ImageView) getContentView().findViewById(R.id.comment_pop_like);
		
		UTools.UI.fitViewByWidth(mContext, mCommentView, 338, 90, 640);
		UTools.UI.fitViewByWidth(mContext, mCommemtImageView, 154, 60, 640);
		UTools.UI.fitViewByWidth(mContext, mLikeImageView, 154, 60, 640);
		
		setWidth(UTools.UI.fitPixels(mContext, 340, 640));
		setHeight(UTools.UI.fitPixels(mContext, 90, 640));
		
		mCommemtImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
				if (mEventItemModel != null)
				{
					Bundle bundle = new Bundle();
					
					bundle.putSerializable(UConstants.EVENT_DETAIL_KEY, (Serializable) mEventItemModel);
					bundle.putSerializable(UConstants.FRIENDS_KEY, (Serializable) mEventItemModel.master);
					
					Intent intent = new Intent();
					intent.setClass(mContext, CEventAlbumsCommentActivity.class);
					intent.putExtras(bundle);
					mContext.startActivity(intent);
				}
			}
		});
		
		mLikeImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
				if (mEventItemModel != null)
				{
					Map<String , String> params = new HashMap<String, String>();
					params.put("event_id", String.valueOf(mEventItemModel.event_id));
					if (mEventItemModel.zan)
					{
						params.put("method", "cancel_zan");
					}
					else
					{
						params.put("method", "zan");
					}
					
					mDataLoader.postData(UConstants.ZAN_EVENT_URL, params, mContext, new HDataListener() {
						
						@Override
						public void onFinish(String source) {
							// TODO Auto-generated method stub
							
							Gson gson = new Gson();
							
							try {
								TempModel mTempModel = gson.fromJson(source, new TypeToken<TempModel>(){}.getType());
								
								if(mTempModel != null && mTempModel.result.equals("success"))
								{
									if (mEventItemModel.zan)
									{
										mEventItemModel.zan = false;
										mEventItemModel.zan_num--;
									}
									else {
										mEventItemModel.zan = true;
										mEventItemModel.zan_num++;
									}
								}
							} catch (JsonSyntaxException e) {
								// TODO Auto-generated catch block
							}
						}
						
						@Override
						public void onFail(String msg) {
							// TODO Auto-generated method stub
						}
					});
				}
			}
		});
	}
	
	/**
	 * 显示弹窗列表界面
	 */
	public void show(View view){
		//获得点击屏幕的位置坐标
		view.getLocationOnScreen(mLocation);
		
		//设置矩形的大小
		mRect.set(mLocation[0], mLocation[1], mLocation[0] + view.getWidth(),mLocation[1] + view.getHeight());
		setAnimationStyle(R.style.PopupAnimation);
		//显示弹窗的位置
		showAtLocation(view, popupGravity, mRect.left - 10 - getWidth(), mRect.top - getHeight()/2+view.getHeight()/2);
	}
	
	class TempModel
	{
		public String result = "";
		public String message = "";
	}

}
