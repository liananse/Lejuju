package com.medialab.lejuju.main.myevent.adapter;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.medialab.lejuju.R;
import com.medialab.lejuju.main.eventdetail.EventDetailActivity;
import com.medialab.lejuju.model.EventItemModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTools;

public class MPastEventAdapter extends BaseAdapter{

	private List<EventItemModel> mEventItemModels;
	private Context mContext;
	private LayoutInflater mInflater;
	
	
	private FinalBitmap fb;
	
	protected OnClickListener onCommentClickListener;
	
	public void setOnCommentClickListener(OnClickListener onCommentClickListener)
	{
		this.onCommentClickListener = onCommentClickListener;
	}
	public MPastEventAdapter(Context context)
	{
		this(context, null);
	}

	public MPastEventAdapter(Context context, List<EventItemModel> infos)
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
            convertView = mInflater.inflate(R.layout.item_past_event, null);

            holder = new ViewHolder();
            
            holder.itemDay = (TextView) convertView.findViewById(R.id.item_past_event_day);
            holder.itemMonth = (TextView) convertView.findViewById(R.id.item_past_event_month);
            holder.itemTitle = (TextView) convertView.findViewById(R.id.item_past_event_title);
            holder.itemMemo = (TextView) convertView.findViewById(R.id.item_past_event_memo);
            holder.itemTotalPic = (TextView) convertView.findViewById(R.id.item_past_event_total_pics);
            holder.itemComment = (ImageView) convertView.findViewById(R.id.item_past_event_comment);
            holder.itemPicLL = convertView.findViewById(R.id.item_past_event_pic_ll);
            holder.item = convertView.findViewById(R.id.item);
            holder.itemPics = new ImageView[4];
            holder.itemPics[0] = (ImageView) convertView.findViewById(R.id.item_past_event_pic1);
            holder.itemPics[1] = (ImageView) convertView.findViewById(R.id.item_past_event_pic2);
            holder.itemPics[2] = (ImageView) convertView.findViewById(R.id.item_past_event_pic3);
            holder.itemPics[3] = (ImageView) convertView.findViewById(R.id.item_past_event_pic4);
            
            UTools.UI.fitViewByWidth(mContext, holder.itemComment, 39, 30, 640);
            convertView.setTag(holder);
        } 
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        final EventItemModel mEventItemModel;
        mEventItemModel = mEventItemModels.get(position);
        
        //holder reset
        for (int i = 0; i < 4; i++) {
			holder.itemPics[i].setVisibility(View.GONE);
			UTools.UI.fitViewByWidth(mContext, holder.itemPics[i], 75, 75, 640);
		}
        holder.itemPicLL.setVisibility(View.GONE);
        holder.itemTotalPic.setVisibility(View.GONE);
        
        if (mEventItemModel != null)
        {
        	if (position == 0) {
        		holder.itemDay.setVisibility(View.VISIBLE);
				holder.itemMonth.setVisibility(View.VISIBLE);
				holder.itemDay.setText(mEventItemModel.start_time.split(" ")[0].split("-")[2]);
	        	holder.itemMonth.setText(mEventItemModel.start_time.split(" ")[0].split("-")[1] + "月");
			}
        	else
        	{
        		String lastTime = mEventItemModels.get(position - 1).start_time.split(" ")[0];
        		if (mEventItemModel.start_time.split(" ")[0].equals(lastTime))
        		{
        			holder.itemDay.setVisibility(View.INVISIBLE);
        			holder.itemMonth.setVisibility(View.INVISIBLE);
        			holder.itemDay.setText(mEventItemModel.start_time.split(" ")[0].split("-")[2]);
        			holder.itemMonth.setText(mEventItemModel.start_time.split(" ")[0].split("-")[1] + "月");
        		} 
        		else 
        		{
        			holder.itemDay.setVisibility(View.VISIBLE);
        			holder.itemMonth.setVisibility(View.VISIBLE);
        			holder.itemDay.setText(mEventItemModel.start_time.split(" ")[0].split("-")[2]);
        			holder.itemMonth.setText(mEventItemModel.start_time.split(" ")[0].split("-")[1] + "月");
        		}
			}
        	
        	holder.itemTitle.setText(mEventItemModel.title);
        	holder.itemMemo.setText(mEventItemModel.memo);
        	
        	holder.itemTotalPic.setText("共有"+mEventItemModel.pic_num+"张");
            if (mEventItemModel.picurls != null && mEventItemModel.picurls.size() > 0)
            {
            	if (mEventItemModel.picurls.size() == 1)
            	{
            		UTools.UI.fitViewByWidth(mContext, holder.itemPics[0], 150, 150, 640);
            	}
            	else if (mEventItemModel.picurls.size() == 2)
            	{
            		UTools.UI.fitViewByWidth(mContext, holder.itemPics[0], 75, 150, 640);
            		UTools.UI.fitViewByWidth(mContext, holder.itemPics[1], 75, 150, 640);
    			}
            	else if (mEventItemModel.picurls.size() == 3)
            	{
            		UTools.UI.fitViewByWidth(mContext, holder.itemPics[0], 75, 150, 640);
    			}
            	holder.itemPicLL.setVisibility(View.VISIBLE);
            	holder.itemTotalPic.setVisibility(View.VISIBLE);
            	for (int i = 0; i < mEventItemModel.picurls.size(); i++) {
    				holder.itemPics[i].setVisibility(View.VISIBLE);
    				fb.display(holder.itemPics[i], mEventItemModel.picurls.get(i).picurl);
    			}
            }
            holder.itemComment.setTag(mEventItemModel);
            holder.itemComment.setOnClickListener(onCommentClickListener);
            holder.item.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View arg0) {
    				// TODO Auto-generated method stub
    				// do nothing
    			}
    		});
            
            holder.itemPicLL.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View arg0) {
    				// TODO Auto-generated method stub
    				if (mEventItemModel != null)
    				{
    					Intent intent = new Intent(mContext, EventDetailActivity.class);
    					Bundle bundle = new Bundle();
    					
    					bundle.putSerializable(UConstants.EVENT_DETAIL_KEY, (Serializable) mEventItemModel);
    					intent.putExtras(bundle);
    					mContext.startActivity(intent);
    				}
    			}
    		});
            
            holder.itemTitle.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(mContext, EventDetailActivity.class);
					Bundle bundle = new Bundle();
					
					bundle.putSerializable(UConstants.EVENT_DETAIL_KEY, (Serializable) mEventItemModel);
					intent.putExtras(bundle);
					mContext.startActivity(intent);
				}
			});
        }
        
        
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
		notifyDataSetChanged();
	}
	
	static class ViewHolder
	{
		TextView itemDay;
		TextView itemMonth;
		ImageView[] itemPics;
		TextView itemTitle;
		TextView itemMemo;
		TextView itemTotalPic;
		ImageView itemComment;
		View itemPicLL;
		View item;
	}
}
