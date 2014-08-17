package com.medialab.lejuju.main.userinfo.adapter;

import java.io.Serializable;
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
import com.medialab.lejuju.main.userinfo.UserInfoSelectRegionCityActivity;
import com.medialab.lejuju.main.userinfo.UserInfoSelectRegionProvinceActivity;
import com.medialab.lejuju.main.userinfo.model.UserInfoProvinceModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTools;

public class UserInfoSelectRegionProvinceAdapter extends BaseAdapter{
	
	private List<UserInfoProvinceModel> mList;
	private Context mContext;
	private LayoutInflater mInflater;
	
	public UserInfoSelectRegionProvinceAdapter(Context context)
	{
		this(context, null);
	}

	public UserInfoSelectRegionProvinceAdapter(Context context, List<UserInfoProvinceModel> infos)
	{
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		if (infos != null)
		{
			this.mList = infos;
		}
		else
		{
			this.mList = new ArrayList<UserInfoProvinceModel>();
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
            convertView = mInflater.inflate(R.layout.item_region_select, null);

            holder = new ViewHolder();
            holder.regionName = (TextView) convertView.findViewById(R.id.region_name);
            holder.regionItemView = convertView.findViewById(R.id.region_item_view);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final UserInfoProvinceModel mProvinceModel;
        mProvinceModel = mList.get(position);
        
        if(UTools.OS.getLanguage().equals("zh"))
        {
        	holder.regionName.setText(mProvinceModel.province_cn);
        }
        else {
        	holder.regionName.setText(mProvinceModel.province_en);
		}
        
        
        holder.regionItemView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(mContext, UserInfoSelectRegionCityActivity.class);
				
				Bundle bundle = new Bundle();
				bundle.putSerializable(UConstants.SER_KEY, (Serializable) mProvinceModel);
				
				intent.putExtras(bundle);
				
				
				if (mContext instanceof UserInfoSelectRegionProvinceActivity)
				{
					((UserInfoSelectRegionProvinceActivity) mContext).startActivityForResult(intent, UConstants.SELECT_REGION_REQUESTCODE);
				}
			}
		});
        
		return convertView;
	}
	
	static class ViewHolder
	{
		TextView regionName;
		View regionItemView;
	}

}
