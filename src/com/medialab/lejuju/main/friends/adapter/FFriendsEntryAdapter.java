package com.medialab.lejuju.main.friends.adapter;

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
import android.widget.TextView;

import com.medialab.lejuju.R;
import com.medialab.lejuju.adapter.BaseUserAdapter;
import com.medialab.lejuju.main.friends.FFriendsEntryActivity;
import com.medialab.lejuju.main.userinfo.UserInfoEntryActivity;
import com.medialab.lejuju.model.FriendsModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.views.RoundImageView;

public class FFriendsEntryAdapter extends BaseUserAdapter{

	private List<FriendsModel> mFriendsModels;
	private Context mContext;
	private LayoutInflater mInflater;
	
	FinalBitmap fb;
	
	public FFriendsEntryAdapter(Context context)
	{
		this(context, null);
	}

	public FFriendsEntryAdapter(Context context, List<FriendsModel> infos)
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
		fb = FinalBitmap.create(mContext);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (mFriendsModels != null)
		{
			return mFriendsModels.size();
		}
		else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (mFriendsModels != null)
		{
			return mFriendsModels.get(position);
		}
		else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return super.getItemId(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_entry_friends, null);

            holder = new ViewHolder();
            holder.headView = (RoundImageView) convertView.findViewById(R.id.header_icon);
            holder.nickName = (TextView) convertView.findViewById(R.id.nickname);
            holder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
            holder.item = convertView.findViewById(R.id.item_bg);
            holder.itemHeadPicBgView = convertView.findViewById(R.id.item_event_head_pic_bg);
            UTools.UI.fitViewByWidth(mContext, holder.itemHeadPicBgView, 94, 94, 640);
    		UTools.UI.fitViewByWidth(mContext, holder.headView, 86, 86, 640);
    		
            convertView.setTag(holder);
        } 
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        final FriendsModel mFriendsModel;
        mFriendsModel = mFriendsModels.get(position);
        
        holder.nickName.setText(mFriendsModel.nick_name);
        fb.display(holder.headView, mFriendsModel.head_pic);
        if (position == 0)
        {
			holder.tvLetter.setVisibility(View.VISIBLE);
			holder.tvLetter.setText(mFriendsModel.namePinYinFirst);
		}
        else 
		{
			String lastCatalog = mFriendsModels.get(position - 1).namePinYinFirst;
			if (mFriendsModel.namePinYinFirst.equals(lastCatalog))
			{
				holder.tvLetter.setVisibility(View.GONE);
			} 
			else 
			{
				holder.tvLetter.setVisibility(View.VISIBLE);
				holder.tvLetter.setText(mFriendsModel.namePinYinFirst);
			}
		}
        holder.item.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(mContext, UserInfoEntryActivity.class);
				
				Bundle bundle = new Bundle();
				bundle.putSerializable(UConstants.FRIENDS_KEY, (Serializable) mFriendsModel);
				
				intent.putExtras(bundle);
				
				
				if (mContext instanceof FFriendsEntryActivity)
				{
					((FFriendsEntryActivity) mContext).startActivity(intent);
				}
			}
		});
		return convertView;
	}

	@Override
	public int getPositionForSection(int section) {
		// TODO Auto-generated method stub
		FriendsModel mFriendsModel;
		String l;
		if (section == '!')
		{
			return 0;
		}
		else {
			for (int i = 0; i < getCount(); i++) {
				mFriendsModel = (FriendsModel) mFriendsModels.get(i);
				l = mFriendsModel.namePinYinFirst;
				char firstChar = l.toUpperCase().charAt(0);
				if (firstChar == section) {
					return i + 2;
				}

			}
		}
		
		mFriendsModel = null;
		l = null;
		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		return super.getSectionForPosition(position);
	}

	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return super.getSections();
	}

	
	static class ViewHolder
	{
		RoundImageView headView;
		TextView nickName;
		TextView tvLetter;
		View itemHeadPicBgView;
		View item;
	}

	public void refreshData(List list)
	{
		mFriendsModels.clear();
		mFriendsModels.addAll(list);
		notifyDataSetChanged();
	}
	
}
