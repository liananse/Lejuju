package com.medialab.lejuju.main.joinevent.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.medialab.lejuju.R;
import com.medialab.lejuju.adapter.BaseUserAdapter;
import com.medialab.lejuju.main.joinevent.model.JContactsModel;
import com.medialab.lejuju.util.UTools;

public class JInviteContactsFriendsAdapter extends BaseUserAdapter{

	private List<JContactsModel> contactsList;
	private Context mContext;
	private LayoutInflater mInflater;
	
	public JInviteContactsFriendsAdapter(Context context)
	{
		this(context, null);
	}

	public JInviteContactsFriendsAdapter(Context context, List<JContactsModel> infos)
	{
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		if (infos != null)
		{
			this.contactsList = infos;
		}
		else
		{
			this.contactsList = new ArrayList<JContactsModel>();
		}
	}

	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int count = 0;
		if (contactsList != null)
		{
			count = contactsList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (contactsList != null)
		{
			return contactsList.get(position);
		}
		return null;
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

        if (convertView == null) 
        {
            convertView = mInflater.inflate(R.layout.item_friends, null);

            holder = new ViewHolder();
            holder.selectButton = (Button) convertView.findViewById(R.id.select_btn);
            holder.headView = (ImageView) convertView.findViewById(R.id.header_icon);
            holder.nickName = (TextView) convertView.findViewById(R.id.nickname);
            holder.content = (TextView) convertView.findViewById(R.id.content);
            holder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
            holder.item = convertView.findViewById(R.id.item_bg);
            UTools.UI.fitViewByWidth(mContext, holder.headView, 88, 88, 640);
            UTools.UI.fitViewByWidth(mContext, holder.selectButton, 42, 42, 640);
            convertView.setTag(holder);
        } 
        else 
        {
            holder = (ViewHolder) convertView.getTag();
        }

        final JContactsModel mJContactsModel;
        mJContactsModel = contactsList.get(position);
        
        holder.nickName.setText(mJContactsModel.name);
        holder.content.setText(mJContactsModel.mobile);
        
        if (position == 0)
        {
			holder.tvLetter.setVisibility(View.VISIBLE);
			holder.tvLetter.setText(mJContactsModel.namePinYinFirst);
		}
        else 
		{
			String lastCatalog = contactsList.get(position - 1).namePinYinFirst;
			if (mJContactsModel.namePinYinFirst.equals(lastCatalog))
			{
				holder.tvLetter.setVisibility(View.GONE);
			} 
			else 
			{
				holder.tvLetter.setVisibility(View.VISIBLE);
				holder.tvLetter.setText(mJContactsModel.namePinYinFirst);
			}
		}
        if (mJContactsModel.isSelected == 0)
		{
			holder.selectButton.setBackgroundResource(R.drawable.btn_check_buttonless_off);
		}
		else
		{
			holder.selectButton.setBackgroundResource(R.drawable.btn_check_buttonless_on);
		}
        holder.item.setOnClickListener(new ControlClickListener(mJContactsModel,holder.selectButton));
        
		return convertView;
	}
	
	class ControlClickListener implements OnClickListener
	{
		JContactsModel fri;
		Button view;
		public ControlClickListener(JContactsModel friend, Button view)
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
		JContactsModel mJContactsModel;
		String l;
		if (section == '!')
		{
			return 0;
		}
		else {
			for (int i = 0; i < getCount(); i++) {
				mJContactsModel = (JContactsModel) contactsList.get(i);
				l = mJContactsModel.namePinYinFirst;
				char firstChar = l.toUpperCase().charAt(0);
				if (firstChar == section) {
					return i + 1;
				}

			}
		}
		
		mJContactsModel = null;
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
	
	final static class ViewHolder {
		ImageView headView;
		TextView nickName;
		TextView content;
		Button selectButton;
		TextView tvLetter;
		View item;
	}

	public void refreshData(List list)
	{
		contactsList.clear();
		contactsList.addAll(list);
		notifyDataSetChanged();
	}
}
