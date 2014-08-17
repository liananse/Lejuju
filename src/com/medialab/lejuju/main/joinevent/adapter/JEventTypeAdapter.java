package com.medialab.lejuju.main.joinevent.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.medialab.lejuju.R;
import com.medialab.lejuju.main.joinevent.JCreateEventActivity;
import com.medialab.lejuju.main.joinevent.JSelectEventTypeActivity;
import com.medialab.lejuju.main.joinevent.model.JEventTypeModel;
import com.medialab.lejuju.util.UTools;

public class JEventTypeAdapter extends BaseAdapter{
	
	private List<JEventTypeModel> mList;
	private Context mContext;
	private LayoutInflater mInflater;
	
	public JEventTypeAdapter(Context context)
	{
		this(context, null);
	}

	public JEventTypeAdapter(Context context, List<JEventTypeModel> infos)
	{
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		if (infos != null)
		{
			this.mList = infos;
		}
		else
		{
			this.mList = new ArrayList<JEventTypeModel>();
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mList.get(arg0);
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
            convertView = mInflater.inflate(R.layout.item_event_type, null);

            holder = new ViewHolder();
            holder.eventNameTextView = (TextView) convertView.findViewById(R.id.event_name);
            holder.eventPicView = convertView.findViewById(R.id.event_pic);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final JEventTypeModel mJEventTypeModel;
        mJEventTypeModel = mList.get(position);
        
        holder.eventNameTextView.setText(mJEventTypeModel.name);
        
        holder.eventPicView.setBackgroundDrawable(UTools.IO.getDrawableFromAssets(mContext, mJEventTypeModel.picurl));
        UTools.UI.fitViewByWidth(mContext, holder.eventPicView, 307, 193, 640);
        holder.eventPicView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (mContext instanceof JSelectEventTypeActivity)
				{
					((JSelectEventTypeActivity) mContext).event_type_id = mJEventTypeModel.id;
					((JSelectEventTypeActivity) mContext).showDialog(1);
				}
				
			}
		});
        
		return convertView;
	}

	static class ViewHolder
	{
		TextView eventNameTextView;
		View eventPicView;
	}
	
}
