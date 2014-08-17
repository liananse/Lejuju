package com.medialab.lejuju.main.eventdetail.adapter;

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
import android.widget.Toast;

import com.medialab.lejuju.R;
import com.medialab.lejuju.main.eventdetail.EDInviteFriendsActivity;
import com.medialab.lejuju.main.eventdetail.EventDetailActivity;
import com.medialab.lejuju.main.joinevent.JInviteFriendsActivity;
import com.medialab.lejuju.model.EventItemModel;
import com.medialab.lejuju.model.FriendsModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.views.RoundImageView;

public class EDEventDetailUserAdapter extends BaseAdapter{

	List<FriendsModel> mFriendsModels;
	private Context mContext;
	private LayoutInflater mInflater;
	
	private EventItemModel mEventItemModel;
	
	private FinalBitmap fb;
	public EDEventDetailUserAdapter(Context context, EventItemModel model)
	{
		this(context, null, model);
	}

	public EDEventDetailUserAdapter(Context context, List<FriendsModel> infos, EventItemModel model)
	{
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		if (infos != null)
		{
			this.mFriendsModels = infos;
		}
		else
		{
			this.mFriendsModels = new ArrayList<FriendsModel>();
		}
		this.mEventItemModel = model;
		fb = FinalBitmap.create(mContext);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (mFriendsModels != null) {
			return mFriendsModels.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		if (mFriendsModels != null)
		{
			return mFriendsModels.get(arg0);
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
            convertView = mInflater.inflate(R.layout.item_event_detail_users, null);

            holder = new ViewHolder();
            
            holder.item_event_detail_head_pic_bg = convertView.findViewById(R.id.item_event_detail_head_pic_bg);
            holder.item_event_detail_head_pic = (RoundImageView) convertView.findViewById(R.id.item_event_detail_head_pic);
    		
            UTools.UI.fitViewByWidth(mContext, holder.item_event_detail_head_pic_bg, 94, 94, 640);
            UTools.UI.fitViewByWidth(mContext, holder.item_event_detail_head_pic, 86, 86, 640);
            convertView.setTag(holder);
        } 
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        final FriendsModel mFriendsModel;
        mFriendsModel = mFriendsModels.get(position);
        if (mFriendsModel.user_id == -1)
        {
        	holder.item_event_detail_head_pic_bg.setBackgroundResource(R.drawable.event_detail_add_user);
        	holder.item_event_detail_head_pic.setVisibility(View.GONE);
        	holder.item_event_detail_head_pic_bg.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(mContext, JInviteFriendsActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable(UConstants.EVENT_DETAIL_KEY, (Serializable) mEventItemModel);
					bundle.putString("from_where", "from_event_detail");
					intent.putExtras(bundle);
					
					if (mContext instanceof EventDetailActivity)
					{
						((EventDetailActivity) mContext).event_i_from = true;
					}
					mContext.startActivity(intent);
				}
			});
        }
        else {
        	holder.item_event_detail_head_pic.setVisibility(View.VISIBLE);
        	fb.display(holder.item_event_detail_head_pic, mFriendsModel.head_pic);
			
			holder.item_event_detail_head_pic_bg.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (mEventItemModel.join == 3)
					{
						Intent intent = new Intent();
						Bundle bundle_APPLY_JOIN_EVENT = new Bundle();
						bundle_APPLY_JOIN_EVENT.putSerializable(UConstants.EVENT_DETAIL_KEY, (Serializable) mEventItemModel);
						bundle_APPLY_JOIN_EVENT.putBoolean(UConstants.FROM_PUSH, false);
						
						intent.putExtras(bundle_APPLY_JOIN_EVENT);
						intent.setClass(mContext, EDInviteFriendsActivity.class);
						
						if (mContext instanceof EventDetailActivity)
						{
							((EventDetailActivity) mContext).event_i_from = true;
						}
						mContext.startActivity(intent);
					}
					else {
						Toast.makeText(mContext, "你还没有参加该活动", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
		return convertView;
	}
	
	public void refreshData(List list)
	{
		mFriendsModels.clear();
		mFriendsModels.addAll(list);
		notifyDataSetChanged();
	}
	
	public void addData(List list)
	{
		mFriendsModels.addAll(list);
		notifyDataSetChanged();
	}
	
	static class ViewHolder
	{
		View item_event_detail_head_pic_bg;
		RoundImageView item_event_detail_head_pic;
	}

}
