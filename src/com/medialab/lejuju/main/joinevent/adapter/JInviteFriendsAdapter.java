package com.medialab.lejuju.main.joinevent.adapter;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.medialab.lejuju.R;
import com.medialab.lejuju.adapter.BaseUserAdapter;
import com.medialab.lejuju.model.FriendsModel;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.views.RoundImageView;

public class JInviteFriendsAdapter extends BaseUserAdapter{

	private List<FriendsModel> mFriendsModels;
	private Context mContext;
	private LayoutInflater mInflater;
	
	private FinalBitmap fb;
	public JInviteFriendsAdapter(Context context)
	{
		this(context, null);
	}

	public JInviteFriendsAdapter(Context context, List<FriendsModel> infos)
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
            convertView = mInflater.inflate(R.layout.item_invite_friends, null);

            holder = new ViewHolder();
            holder.headView = (RoundImageView) convertView.findViewById(R.id.header_icon);
            holder.nickName = (TextView) convertView.findViewById(R.id.nickname);
            holder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
            holder.item = convertView.findViewById(R.id.item_bg);
            holder.itemHeadPicBgView = convertView.findViewById(R.id.item_event_head_pic_bg);
            holder.select_btn = (Button) convertView.findViewById(R.id.select_btn);
            
    		UTools.UI.fitViewByWidth(mContext, holder.headView, 86, 86, 640);
    		UTools.UI.fitViewByWidth(mContext, holder.itemHeadPicBgView, 94, 94, 640);
    		UTools.UI.fitViewByWidth(mContext, holder.select_btn, 42, 42, 640);
            convertView.setTag(holder);
        } 
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        final FriendsModel mFriendsModel;
        mFriendsModel = mFriendsModels.get(position);
        
        fb.display(holder.headView, mFriendsModel.head_pic);
        holder.nickName.setText(mFriendsModel.nick_name);
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
        if (mFriendsModel.isSelected == 0)
		{
			holder.select_btn.setBackgroundResource(R.drawable.btn_check_buttonless_off);
		}
		else
		{
			holder.select_btn.setBackgroundResource(R.drawable.btn_check_buttonless_on);
		}
        
        holder.item.setOnClickListener(new ControlClickListener(mFriendsModel,holder.select_btn));
		return convertView;
	}
	
	class ControlClickListener implements OnClickListener
	{
		FriendsModel fri;
		Button view;
		public ControlClickListener(FriendsModel friend, Button view)
		{
			super();
			this.fri = friend;
			this.view = view;
		}

		@Override
		public void onClick(View v)
		{
			final int isSelected = fri.isSelected;
			if (isSelected == 0)
			{
				fri.isSelected = 1;
				view.setBackgroundResource(R.drawable.btn_check_buttonless_on);
			}
			else
			{
				fri.isSelected = 0;
				view.setBackgroundResource(R.drawable.btn_check_buttonless_off);
			}
		}
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
		View item;
		View itemHeadPicBgView;
		Button select_btn;
	}
	
	public void refreshData(List list)
	{
		mFriendsModels.clear();
		mFriendsModels.addAll(list);
		notifyDataSetChanged();
	}

}
