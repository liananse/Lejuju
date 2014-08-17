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
import com.medialab.lejuju.main.friends.FHaveNewFriendsActivity;
import com.medialab.lejuju.main.userinfo.UserInfoEntryActivity;
import com.medialab.lejuju.model.FriendsModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.views.RoundImageView;

public class FNewFriendsEntryAdapter extends BaseUserAdapter{

	private List<FriendsModel> mFriendsModels;
	private Context mContext;
	private LayoutInflater mInflater;
	
	FinalBitmap fb;
	public FNewFriendsEntryAdapter(Context context)
	{
		this(context, null);
	}

	public FNewFriendsEntryAdapter(Context context, List<FriendsModel> infos)
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
            convertView = mInflater.inflate(R.layout.item_new_friends, null);

            holder = new ViewHolder();
            holder.headView = (RoundImageView) convertView.findViewById(R.id.header_icon);
            holder.nickName = (TextView) convertView.findViewById(R.id.nickname);
            holder.item = convertView.findViewById(R.id.item_bg);
            holder.memo = (TextView) convertView.findViewById(R.id.memo_name);
            holder.itemHeadPicBgView = convertView.findViewById(R.id.item_event_head_pic_bg);
            holder.state_text = (TextView) convertView.findViewById(R.id.state_text);
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
        if(mFriendsModel!=null)
        {
        	holder.nickName.setText(mFriendsModel.nick_name);
        	
        	fb.display(holder.headView, mFriendsModel.head_pic);
        	holder.memo.setText(mFriendsModel.memo_name);
        	
        	if(mFriendsModel.relation == 1 || mFriendsModel.relation == 2 || mFriendsModel.relation == 4)
    		{
        		holder.state_text.setText("");
    		}
    		else if(mFriendsModel.relation == 3)
    		{
    			holder.state_text.setText("");
    		}
    		else if(mFriendsModel.relation == 5)
    		{
    			holder.state_text.setText("通过验证");
    		}
    		else if(mFriendsModel.relation == 0)
    		{
    			holder.state_text.setText("陌生人");
    		}
        	else if (mFriendsModel.relation == 6)
        	{
				holder.state_text.setText("等待验证");
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
        			
        			
        			if (mContext instanceof FHaveNewFriendsActivity)
        			{
        				((FHaveNewFriendsActivity) mContext).startActivityForResult(intent, 1);
        			}
        		}
        	});
        }
        
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
		TextView memo;
		View itemHeadPicBgView;
		View item;
		TextView state_text;
	}

	public void refreshData(List list)
	{
		mFriendsModels.clear();
		mFriendsModels.addAll(list);
		notifyDataSetChanged();
	}
	
}
