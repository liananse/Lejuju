package com.medialab.lejuju.main.trends.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.medialab.lejuju.R;
import com.medialab.lejuju.main.comment.CEventDiscussActivity;
import com.medialab.lejuju.main.trends.TTrendsEntryActivity;
import com.medialab.lejuju.model.EventItemModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTimeShown;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.views.CircleImageView;

public class TTrendsEventAdapter extends BaseAdapter{

	private List<EventItemModel> mEventItemModels;
	private Context mContext;
	private LayoutInflater mInflater;
	
	private FinalBitmap fb;
	
	public TTrendsEventAdapter(Context context)
	{
		this(context, null);
	}

	public TTrendsEventAdapter(Context context, List<EventItemModel> infos)
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
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (mEventItemModels != null)
		{
			return mEventItemModels.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		if (mEventItemModels != null)
		{
			return mEventItemModels.get(arg0);
		}
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_trends, null);

            holder = new ViewHolder();

            holder.item_trends_image = (CircleImageView) convertView.findViewById(R.id.item_trends_image);
    		holder.item_trends_title = (TextView) convertView.findViewById(R.id.item_trends_title);
    		holder.item_trends_time = (TextView) convertView.findViewById(R.id.item_trends_time);
    		holder.item_trends_content = (TextView) convertView.findViewById(R.id.item_trends_content);
    		holder.item_trends_item = (LinearLayout) convertView.findViewById(R.id.item_trends_item);
    		holder.item_trends_num = (TextView) convertView.findViewById(R.id.item_trends_num);
    		holder.item_trends_bg = (LinearLayout) convertView.findViewById(R.id.item_trends_bg);
    		
    		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, UTools.UI.fitPixels(mContext, 120, 640));
    		
    		holder.item_trends_item.setLayoutParams(layoutParams);
    		UTools.UI.fitViewByWidth(mContext, holder.item_trends_image, 92, 92, 640);
    		UTools.UI.fitViewByWidth(mContext, holder.item_trends_num, 42, 42, 640);
            convertView.setTag(holder);
        } 
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        final EventItemModel mEventItemModel;
        mEventItemModel = mEventItemModels.get(position);
        
        holder.item_trends_title.setText(mEventItemModel.title);
        holder.item_trends_time.setText(UTimeShown.getTrendsTime(mEventItemModel.trends_add_time, "trends"));
        holder.item_trends_content.setText(mEventItemModel.trends_nick_name + ":" + mEventItemModel.trends_content);
        
        if(mEventItemModel.event_pic==null || mEventItemModel.event_pic.equals(""))
        {
        	Bitmap thumb = BitmapFactory.decodeResource(mContext.getResources(), getEventTypeId(mEventItemModel.event_type_id));
        	holder.item_trends_image.setImageBitmap(thumb);
        }
        else
        {
        	fb.display(holder.item_trends_image, mEventItemModel.event_pic);
        }
        
        if (mEventItemModel.event_trends_num > 0)
        {
        	holder.item_trends_num.setVisibility(View.VISIBLE);
        	holder.item_trends_num.setText(mEventItemModel.event_trends_num+"");
        }
        else {
			holder.item_trends_num.setVisibility(View.INVISIBLE);
		}
        
        holder.item_trends_bg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(mContext, CEventDiscussActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(UConstants.EVENT_DETAIL_KEY, (Serializable) mEventItemModel);
				intent.putExtras(bundle);
				
				mContext.startActivity(intent);
				((TTrendsEntryActivity) mContext).overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out_no);
			}
		});
		return convertView;
	}
	
	
	static class ViewHolder
	{
		CircleImageView item_trends_image;
		TextView item_trends_title;
		TextView item_trends_time;
		TextView item_trends_content;
		LinearLayout item_trends_item;
		TextView item_trends_num;
		
		LinearLayout item_trends_bg;
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
	
	private int getEventBlurTypeId(int event_type_id)
	{
		if (event_type_id == 1)
		{
			return R.drawable.event_image_blur_1;
		}
		else if (event_type_id == 2)
		{
			return R.drawable.event_image_blur_2;
		}
		else if (event_type_id == 3)
		{
			return R.drawable.event_image_blur_3;
		}
		else if (event_type_id == 7)
		{
			return R.drawable.event_image_blur_7;
		}
		else if (event_type_id == 8)
		{
			return R.drawable.event_image_blur_8;
		}
		else if (event_type_id == 9)
		{
			return R.drawable.event_image_blur_9;
		}
		else {
			return R.drawable.event_image_blur_1;
		}
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
