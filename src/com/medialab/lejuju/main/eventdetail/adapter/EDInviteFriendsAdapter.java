package com.medialab.lejuju.main.eventdetail.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.medialab.lejuju.R;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.http.HHttpDataLoader.HDataListener;
import com.medialab.lejuju.main.eventdetail.EDInviteFriendsActivity;
import com.medialab.lejuju.main.userinfo.UserInfoEntryActivity;
import com.medialab.lejuju.model.FriendsModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.views.RoundImageView;

public class EDInviteFriendsAdapter extends BaseAdapter{

	private List<FriendsModel> mFriendsModels;
	private Context mContext;
	private LayoutInflater mInflater;
	
	private String event_id = "-1";
	private FinalBitmap fb;
	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	public EDInviteFriendsAdapter(Context context, String event_id)
	{
		this(context, null, event_id);
	}

	public EDInviteFriendsAdapter(Context context, List<FriendsModel> infos, String event_id)
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
		this.event_id = event_id;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (mFriendsModels != null)
		{
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
    		
    		UTools.UI.fitViewByWidth(mContext, holder.select_btn, 130, 52, 640);
    		
            convertView.setTag(holder);
        } 
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        final FriendsModel mFriendsModel;
        mFriendsModel = mFriendsModels.get(position);
        
        final int pos = position;
        fb.display(holder.headView, mFriendsModel.head_pic);
        holder.nickName.setText(mFriendsModel.nick_name);
        holder.tvLetter.setVisibility(View.GONE);
        
        holder.select_btn.setBackgroundResource(R.drawable.check_friends_pass_bg);
        if (mFriendsModel.needCheck == 1)
        {
        	holder.select_btn.setVisibility(View.VISIBLE);
        	
        	if (position == 0)
            {
    			holder.tvLetter.setVisibility(View.VISIBLE);
    			holder.tvLetter.setText("待通过");
    		}
            else 
    		{
    			int needtoCheck = mFriendsModels.get(position - 1).needCheck;
    			if (mFriendsModel.needCheck == needtoCheck)
    			{
    				holder.tvLetter.setVisibility(View.GONE);
    			} 
    			else 
    			{
    				holder.tvLetter.setVisibility(View.VISIBLE);
    				holder.tvLetter.setText("待通过");
    			}
    		}
        }
        else {
			holder.select_btn.setVisibility(View.GONE);
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
				if (mContext instanceof EDInviteFriendsActivity)
				{
					((EDInviteFriendsActivity) mContext).bottomInOut = false;
				}
				mContext.startActivity(intent);
			}
		});
        
        holder.select_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if (!event_id.equals("-1") && !event_id.isEmpty())
				{
					Map<String, String> params = new HashMap<String, String>();
					params.put("event_id", event_id);
					
					String fString = String.valueOf(mFriendsModel.user_id);
					params.put("user_id_list", fString);
					
					mDataLoader.postData(UConstants.AUDIT_FRIENDS_URL, params, mContext, new HDataListener() {
						
						@Override
						public void onFinish(String source) {
							// TODO Auto-generated method stub
							try
							{
								JSONObject json = new JSONObject(source);
								if (json.optString("result", "").equals("success"))
								{
									mFriendsModels.remove(pos);
									mFriendsModel.needCheck = 0;
									mFriendsModels.add(0, mFriendsModel);
									notifyDataSetChanged();
								}
								else
								{
								}
							}
							catch (JSONException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						@Override
						public void onFail(String msg) {
							// TODO Auto-generated method stub
						}
					});
					
					
					
				}
			}
		});
        
        return convertView;
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
	
	public void addData(List list)
	{
		mFriendsModels.addAll(list);
		notifyDataSetChanged();
	}

}
