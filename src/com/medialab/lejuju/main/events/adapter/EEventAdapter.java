package com.medialab.lejuju.main.events.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.medialab.lejuju.MMainTabActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.main.eventdetail.EventDetailActivity;
import com.medialab.lejuju.main.events.EAnimations;
import com.medialab.lejuju.main.events.EJoinEventEntryActivity;
import com.medialab.lejuju.main.userinfo.UserInfoEntryActivity;
import com.medialab.lejuju.model.EventItemModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTimeShown;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.views.RoundImageView;

public class EEventAdapter extends BaseAdapter{
	
	private List<EventItemModel> mEventItemModels;
	private Context mContext;
	private LayoutInflater mInflater;
	
	private FinalBitmap fb;
	
	public EEventAdapter(Context context)
	{
		this(context, null);
		//初始化FinalBitmap模块
		fb = FinalBitmap.create(context);
	}

	public EEventAdapter(Context context, List<EventItemModel> infos)
	{
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		if (infos != null)
		{
			this.mEventItemModels = infos;
		}
		else
		{
			this.mEventItemModels = new ArrayList<EventItemModel>();
		}
		
		EAnimations.initEAnimation(mContext);
	}

	public int getCount() {
		// TODO Auto-generated method stub
		int count = 0;
		if (mEventItemModels != null)
		{
			count = mEventItemModels.size();
		}
		return count;
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (mEventItemModels != null)
		{
			return mEventItemModels.get(position);
		}
		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_friends_events, null);

            holder = new ViewHolder();
            
            holder.itemPoster = (ImageView) convertView.findViewById(R.id.item_event_poster);
            holder.itemTitle = (TextView) convertView.findViewById(R.id.item_event_title);
            holder.itemTime = (TextView) convertView.findViewById(R.id.item_event_time);
            holder.itemTotalPeopleNum = (TextView) convertView.findViewById(R.id.item_event_total_people_num);
            holder.itemTotalPeopleNumImg = (ImageView) convertView.findViewById(R.id.item_event_total_people_num_img);
            
            holder.itemHeadPicBgView = convertView.findViewById(R.id.item_event_head_pic_bg);
            holder.itemHeadPic = (RoundImageView) convertView.findViewById(R.id.item_event_head_pic);
            
            holder.itemEventItem = convertView.findViewById(R.id.item_event_item);
            
    		UTools.UI.fitViewByWidth(mContext, holder.itemHeadPic, 98, 98, 640);
    		UTools.UI.fitViewByWidth(mContext, holder.itemHeadPicBgView, 108, 108, 640);
    		
    		UTools.UI.fitViewByWidth(mContext, holder.itemTotalPeopleNumImg, 28, 31, 640);
            convertView.setTag(holder);
        } 
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        final EventItemModel mEventItemModel;
        mEventItemModel = mEventItemModels.get(position);
        fb.display(holder.itemHeadPic, mEventItemModel.master.head_pic);
        
        // need improve
        // 这种方式要优化，存在列表滑动缓慢的问题
        if(mEventItemModel.event_pic==null || mEventItemModel.event_pic.equals(""))
        {
        	holder.itemPoster.setImageResource(getEventTypeId(mEventItemModel.event_type_id));
        }
        else
        {
        	fb.display(holder.itemPoster, mEventItemModel.event_pic);
        }
        holder.itemTitle.setText(mEventItemModel.title);
        holder.itemTotalPeopleNum.setText(mEventItemModel.people_join);
        holder.itemTime.setText(UTimeShown.getTimeStringShownInEventItem(mEventItemModel.start_time));
        holder.itemEventItem.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mEventItemModel != null)
				{
					Intent intent = new Intent(mContext, EventDetailActivity.class);
					Bundle bundle = new Bundle();
					
					bundle.putSerializable(UConstants.EVENT_DETAIL_KEY, (Serializable) mEventItemModel);
					
					intent.putExtras(bundle);
					if (mContext instanceof EJoinEventEntryActivity)
					{
						if(mEventItemModel.event_pic==null || mEventItemModel.event_pic.equals(""))
				        {
							((EJoinEventEntryActivity) mContext).top_image_poster.setImageResource(getEventTypeId(mEventItemModel.event_type_id));
				        }
				        else
				        {
				        	fb.display(((EJoinEventEntryActivity) mContext).top_image_poster, mEventItemModel.event_pic);
				        }
						int height = ((EJoinEventEntryActivity) mContext).header_barView.getHeight();
						((EJoinEventEntryActivity) mContext).top_image_poster.layout(v.getLeft(), v.getTop() + height, v.getRight(), v.getBottom());
						
						EAnimations.startItemAnimation((FrameLayout) v, ((EJoinEventEntryActivity) mContext).top_image_poster, intent);
						((EJoinEventEntryActivity) mContext).item_top = v.getTop() + height;
						
						if (((EJoinEventEntryActivity) mContext).getParent() instanceof MMainTabActivity)
						{
							((MMainTabActivity) ((EJoinEventEntryActivity) mContext).getParent()).animationMoveMainTab();
						}
					}
				}
			}
		});
        
        holder.itemHeadPicBgView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(mContext, UserInfoEntryActivity.class);
				
				Bundle bundle = new Bundle();
				bundle.putSerializable(UConstants.FRIENDS_KEY, (Serializable) mEventItemModel.master);
				
				intent.putExtras(bundle);
				
				mContext.startActivity(intent);
			}
		});
        
		return convertView;
		
	}
	
	public void refreshData(List list)
	{
		mEventItemModels.clear();
		mEventItemModels.addAll(list);
		this.notifyDataSetChanged();
	}
	
	public void addData(List list)
	{
		mEventItemModels.addAll(list);
		this.notifyDataSetChanged();
	}
	
	static class ViewHolder
	{
		ImageView itemPoster;
		TextView itemTitle;
		TextView itemTime;
		TextView itemTotalPeopleNum;
		ImageView itemTotalPeopleNumImg;
		
		View itemHeadPicBgView;
		RoundImageView itemHeadPic;
		
		View itemEventItem;
	}
	
	private int getEventTypeId(int event_type_id)
	{
		if (event_type_id == 1)
		{
			return R.drawable.event_image_1;
		}
		else if (event_type_id == 2)
		{
			return R.drawable.event_image_2;
		}
		else if (event_type_id == 3)
		{
			return R.drawable.event_image_3;
		}
		else if (event_type_id == 7)
		{
			return R.drawable.event_image_7;
		}
		else if (event_type_id == 8)
		{
			return R.drawable.event_image_8;
		}
		else if (event_type_id == 9)
		{
			return R.drawable.event_image_9;
		}
		else {
			return R.drawable.event_image_1;
		}
	}
	

}
