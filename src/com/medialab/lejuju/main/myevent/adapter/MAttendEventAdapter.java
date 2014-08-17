package com.medialab.lejuju.main.myevent.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
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
import com.medialab.lejuju.model.EventItemModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTimeShown;
import com.medialab.lejuju.util.UTools;

public class MAttendEventAdapter extends BaseAdapter{

	private List<EventItemModel> mEventItemModels;
	private Context mContext;
	private LayoutInflater mInflater;
	
	private FinalBitmap fb;
	
	public MAttendEventAdapter(Context context)
	{
		this(context, null);
	}

	public MAttendEventAdapter(Context context, List<EventItemModel> infos)
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
		fb = FinalBitmap.create(mContext);
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
            convertView = mInflater.inflate(R.layout.item_my_event, null);

            holder = new ViewHolder();

            holder.itemPoster = (ImageView) convertView.findViewById(R.id.attend_event_poster);
            holder.itemTime = (TextView) convertView.findViewById(R.id.attend_event_time);
            holder.itemTitle = (TextView) convertView.findViewById(R.id.attend_event_title);
            holder.itemTotalPicNum = (TextView) convertView.findViewById(R.id.attend_event_total_picnum);
            holder.itemTotalPicNumImg = (ImageView) convertView.findViewById(R.id.attend_event_total_picnum_img);
            
            holder.itemNewPhotoLL = convertView.findViewById(R.id.new_photo_ll);
            holder.itemNewPhotoNum = (TextView) convertView.findViewById(R.id.new_photo_num);
            holder.itemNewPhotoContent = (TextView)convertView.findViewById(R.id.new_photo_content);
            holder.itemEventItem = convertView.findViewById(R.id.item_event_item);
            
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    UTools.UI.fitPixels(mContext, 42, 640), Gravity.CENTER_VERTICAL);
            layoutParams.setMargins(20, 0, 0, 0);
            holder.itemNewPhotoContent.setLayoutParams(layoutParams);
    		UTools.UI.fitViewByWidth(mContext, holder.itemPoster, 640, 240, 640);
    		UTools.UI.fitViewByWidth(mContext, holder.itemTotalPicNumImg, 22, 26, 640);
    		UTools.UI.fitViewByWidth(mContext, holder.itemNewPhotoNum, 52, 54, 640);
    		
            convertView.setTag(holder);
        } 
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        final EventItemModel mEventItemModel;
        mEventItemModel = mEventItemModels.get(position);
        
        if(mEventItemModel.event_pic==null || mEventItemModel.event_pic.equals(""))
        {
        	holder.itemPoster.setImageResource(getEventTypeId(mEventItemModel.event_type_id));
        }
        else
        {
        	fb.display(holder.itemPoster, mEventItemModel.event_pic);
        }
        
        holder.itemTitle.setText(mEventItemModel.title);
        holder.itemTime.setText(UTimeShown.getTrendsTime(mEventItemModel.start_time,"attend_event"));
        if (mEventItemModel.pic_num != 0)
        {
        	holder.itemTotalPicNum.setText(mEventItemModel.pic_num + "");
        	holder.itemTotalPicNum.setVisibility(View.VISIBLE);
        	holder.itemTotalPicNumImg.setVisibility(View.VISIBLE);
        }
        else 
        {
			holder.itemTotalPicNum.setVisibility(View.GONE);
			holder.itemTotalPicNumImg.setVisibility(View.GONE);
		}
        
        if (mEventItemModel.un_read_pic_num != 0)
        {
        	holder.itemNewPhotoLL.setVisibility(View.VISIBLE);
        	holder.itemNewPhotoNum.setText("" + mEventItemModel.un_read_pic_num);
        	holder.itemNewPhotoContent.setText("该活动上传了" + mEventItemModel.un_read_pic_num + "张新照片");
        }
        else
        {
			holder.itemNewPhotoLL.setVisibility(View.GONE);
		}
        
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
		return convertView;
		
	}
	
	public void refreshData(List list)
	{
		mEventItemModels.clear();
		mEventItemModels.addAll(list);
		notifyDataSetChanged();
	}
	
	public void addData(List list)
	{
		mEventItemModels.addAll(list);
	}
	
	static class ViewHolder
	{
		ImageView itemPoster;
		TextView itemTitle;
		TextView itemTime;
		TextView itemTotalPicNum;
		ImageView itemTotalPicNumImg;
		
		View itemNewPhotoLL;
		TextView itemNewPhotoNum;
		TextView itemNewPhotoContent;
		
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
