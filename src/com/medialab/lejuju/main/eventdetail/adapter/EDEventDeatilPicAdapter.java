package com.medialab.lejuju.main.eventdetail.adapter;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.medialab.lejuju.R;
import com.medialab.lejuju.main.eventdetail.model.EDEventDetailPicModel;
import com.medialab.lejuju.util.UTools;

public class EDEventDeatilPicAdapter extends BaseAdapter{

	List<EDEventDetailPicModel> mEdEventDetailPicModels;
	private Context mContext;
	private LayoutInflater mInflater;

	private FinalBitmap fb;
	public EDEventDeatilPicAdapter(Context context)
	{
		this(context, null);
	}

	public EDEventDeatilPicAdapter(Context context, List<EDEventDetailPicModel> infos)
	{
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		if (infos != null)
		{
			this.mEdEventDetailPicModels = infos;
		}
		else
		{
			this.mEdEventDetailPicModels = new ArrayList<EDEventDetailPicModel>();
		}
		fb = FinalBitmap.create(mContext);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (mEdEventDetailPicModels != null)
		{
			return mEdEventDetailPicModels.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (mEdEventDetailPicModels != null)
		{
			return mEdEventDetailPicModels.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_event_detail_pics, null);

            holder = new ViewHolder();
            
            holder.event_detail_item = convertView.findViewById(R.id.event_detail_item);
            holder.event_detail_pic = (ImageView) convertView.findViewById(R.id.event_detail_pic);
            holder.event_detail_user = (TextView) convertView.findViewById(R.id.event_detail_user);
    		
            UTools.UI.fitViewByWidth(mContext, holder.event_detail_item, 100, 100, 640);
            UTools.UI.fitViewByWidth(mContext, holder.event_detail_pic, 100, 100, 640);
            convertView.setTag(holder);
        } 
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        final EDEventDetailPicModel mEdEventDetailPicModel;
        mEdEventDetailPicModel = mEdEventDetailPicModels.get(position);
    	holder.event_detail_user.setVisibility(View.VISIBLE);
		holder.event_detail_user.setText(mEdEventDetailPicModel.user.nick_name);
		
		fb.display(holder.event_detail_pic, mEdEventDetailPicModel.picurl);
		return convertView;
	}
	
	public void refreshData(List list)
	{
		mEdEventDetailPicModels.clear();
		mEdEventDetailPicModels.addAll(list);
		notifyDataSetChanged();
	}
	
	public void addData(List list)
	{
		mEdEventDetailPicModels.addAll(list);
		notifyDataSetChanged();
	}
	
	static class ViewHolder
	{
		ImageView event_detail_pic;
		TextView event_detail_user;
		View event_detail_item;
	}

}
