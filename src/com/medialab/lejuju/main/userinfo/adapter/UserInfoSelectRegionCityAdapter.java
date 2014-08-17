package com.medialab.lejuju.main.userinfo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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
import com.medialab.lejuju.main.userinfo.UserInfoSelectRegionCityActivity;
import com.medialab.lejuju.main.userinfo.model.UserInfoCitiesModel;
import com.medialab.lejuju.util.UTools;

public class UserInfoSelectRegionCityAdapter extends BaseAdapter{
	
	private List<UserInfoCitiesModel> mList;
	private Context mContext;
	private LayoutInflater mInflater;
	private String province;
	private Intent intent;
	
	public UserInfoSelectRegionCityAdapter(Context context)
	{
		this(context, null, "", null);
	}

	public UserInfoSelectRegionCityAdapter(Context context, List<UserInfoCitiesModel> infos, String province, Intent intent)
	{
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		if (infos != null)
		{
			this.mList = infos;
		}
		else
		{
			this.mList = new ArrayList<UserInfoCitiesModel>();
		}
		this.province = province;
		this.intent = intent;
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
            holder.regionArrow = (ImageView) convertView.findViewById(R.id.region_arrow);
            holder.regionItemView = convertView.findViewById(R.id.region_item_view);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final UserInfoCitiesModel mCityModel = mList.get(position);
        
        if(UTools.OS.getLanguage().equals("zh"))
        {
        	holder.regionName.setText(mCityModel.city_cn);
        }
        else {
			holder.regionName.setText(mCityModel.city_en);
		}
        holder.regionArrow.setVisibility(View.GONE);
        
        holder.regionItemView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				
				if(UTools.OS.getLanguage().equals("zh"))
		        {
					bundle.putString("region_str", province + "-" + mCityModel.city_cn);
		        }
		        else {
		        	bundle.putString("region_str", province + "-" + mCityModel.city_en);
				}
				
				intent.putExtras(bundle);
				
				if (mContext instanceof UserInfoSelectRegionCityActivity)
				{
					((UserInfoSelectRegionCityActivity) mContext).setResult(Activity.RESULT_OK, intent);
					((UserInfoSelectRegionCityActivity) mContext).finish();
				}
				
			}
		});

		return convertView;
	}
	
	static class ViewHolder
	{
		TextView regionName;
		ImageView regionArrow;
		View regionItemView;
	}

}