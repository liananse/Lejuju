package com.medialab.lejuju.main.comment.adapter;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.medialab.lejuju.R;
import com.medialab.lejuju.model.TrendItemModel;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.views.RoundImageView;

public class CEventTrendsAdapter extends BaseAdapter{

	private List<TrendItemModel> mTrendItemModels;
	
	private Context mContext;
	private LayoutInflater mInflater;
	
	FinalBitmap fb;
	public CEventTrendsAdapter(Context context)
	{
		this(context, null);
	}

	public CEventTrendsAdapter(Context context, List<TrendItemModel> infos)
	{
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		if (infos != null)
		{
			this.mTrendItemModels = infos;
		}
		else
		{
			this.mTrendItemModels = new ArrayList<TrendItemModel>();
		}

		fb = FinalBitmap.create(mContext);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (mTrendItemModels != null)
		{
			return mTrendItemModels.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		if (mTrendItemModels != null)
		{
			return mTrendItemModels.get(arg0);
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
            convertView = mInflater.inflate(R.layout.group_comment_text, null);

            holder = new ViewHolder();
            
            holder.group_comment_head_left = (RoundImageView) convertView.findViewById(R.id.group_comment_head_left);
            holder.group_comment_head_right = (RoundImageView) convertView.findViewById(R.id.group_comment_head_right);
            holder.group_comment_content = (TextView) convertView.findViewById(R.id.group_comment_content);
    		holder.group_comment_time = (TextView) convertView.findViewById(R.id.group_comment_time);
    		holder.group_comment_other_content = (TextView) convertView.findViewById(R.id.group_comment_other_content);
    		
    		holder.group_comment_head_left_bg = convertView.findViewById(R.id.group_comment_head_left_bg);
    		holder.group_comment_head_right_bg = convertView.findViewById(R.id.group_comment_head_right_bg);
    		
            UTools.UI.fitViewByWidth(mContext, holder.group_comment_head_left, 86, 86, 640);
            UTools.UI.fitViewByWidth(mContext, holder.group_comment_head_right, 86, 86, 640);
            
            UTools.UI.fitViewByWidth(mContext, holder.group_comment_head_left_bg, 94, 94, 640);
            UTools.UI.fitViewByWidth(mContext, holder.group_comment_head_right_bg, 94, 94, 640);
            convertView.setTag(holder);
        } 
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        final TrendItemModel mTrendItemModel;
        mTrendItemModel = mTrendItemModels.get(position);
        
        holder.group_comment_content.setVisibility(View.VISIBLE);
        holder.group_comment_head_left.setVisibility(View.VISIBLE);
        holder.group_comment_head_right.setVisibility(View.VISIBLE);
        holder.group_comment_other_content.setVisibility(View.VISIBLE);
        holder.group_comment_time.setVisibility(View.VISIBLE);
        
        // 如果是活动的动态信息
        if (mTrendItemModel.type == 2 || mTrendItemModel.type == 3 || mTrendItemModel.type == 5)
        {
        	holder.group_comment_head_left.setVisibility(View.GONE);
        	holder.group_comment_head_right.setVisibility(View.GONE);
        	holder.group_comment_head_left_bg.setVisibility(View.GONE);
        	holder.group_comment_head_right_bg.setVisibility(View.GONE);
        	
        	holder.group_comment_content.setVisibility(View.GONE);
        	
        	holder.group_comment_other_content.setText(mTrendItemModel.content);
        	holder.group_comment_other_content.setVisibility(View.VISIBLE);
        }
        else if (mTrendItemModel.type == 1)
        {
        	holder.group_comment_other_content.setVisibility(View.GONE);
        	if (String.valueOf(mTrendItemModel.org_user.user_id).equals(UTools.OS.getUserId(mContext)))
        	{
        		holder.group_comment_head_left.setVisibility(View.GONE);
        		holder.group_comment_head_left_bg.setVisibility(View.GONE);
        		holder.group_comment_content.setBackgroundResource(R.drawable.group_comment_text_right);
        		holder.group_comment_head_right.setVisibility(View.VISIBLE);
        		holder.group_comment_head_right_bg.setVisibility(View.VISIBLE);
        		
        		holder.group_comment_content.setText(mTrendItemModel.content);
        		
        		if (mTrendItemModel.org_user != null)
        		{
        			fb.display(holder.group_comment_head_right, mTrendItemModel.org_user.head_pic);
        		}
        	}
        	else
        	{
        		holder.group_comment_head_left_bg.setVisibility(View.VISIBLE);
        		holder.group_comment_head_left.setVisibility(View.VISIBLE);
        		holder.group_comment_content.setBackgroundResource(R.drawable.group_comment_text_left);
        		holder.group_comment_head_right.setVisibility(View.GONE);
        		holder.group_comment_head_right_bg.setVisibility(View.GONE);
        		holder.group_comment_content.setText(mTrendItemModel.content);
        		
        		if (mTrendItemModel.org_user != null)
        		{
        			fb.display(holder.group_comment_head_left, mTrendItemModel.org_user.head_pic);
        		}
        	}
		}
        // 如果是自己发的
        
        if (mTrendItemModel.show_time == 1)
        {
        	holder.group_comment_time.setText(mTrendItemModel.add_time);
        	holder.group_comment_time.setVisibility(View.VISIBLE);
        }
        else {
			holder.group_comment_time.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	
	static class ViewHolder
	{
		RoundImageView group_comment_head_left;
		RoundImageView group_comment_head_right;
		TextView group_comment_content;
		TextView group_comment_time;
		TextView group_comment_other_content;
		
		View group_comment_head_left_bg;
		View group_comment_head_right_bg;
	}
	
	public void refreshData(List<TrendItemModel> list)
	{
		mTrendItemModels.clear();
		mTrendItemModels.addAll(list);
		notifyDataSetChanged();
	}
	
	public void addData(List<TrendItemModel> list)
	{
		mTrendItemModels.addAll(list);
		
		notifyDataSetChanged();
	}
	
	public void addHeadData(List<TrendItemModel> list)
	{
		for (int i = 0; i < list.size(); i++) {
			mTrendItemModels.add(i, (TrendItemModel) list.get(i));
		}
	}


}
