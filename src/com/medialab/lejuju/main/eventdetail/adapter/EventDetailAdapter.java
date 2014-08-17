package com.medialab.lejuju.main.eventdetail.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.medialab.lejuju.R;
import com.medialab.lejuju.main.eventdetail.EventDetailActivity;
import com.medialab.lejuju.main.eventdetail.model.EventDetailRecordModel;
import com.medialab.lejuju.main.photowall.PhotoImagePagerActivity;
import com.medialab.lejuju.main.photowall.model.PPhotoWallPicModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTimeShown;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.views.CircleImageView;

public class EventDetailAdapter extends BaseAdapter{

	List<EventDetailRecordModel> mEventDetailRecordModels;
	
	List<PPhotoWallPicModel> mPhotoWallPicModels;
	Bitmap thumb;
	private Context mContext;
	private LayoutInflater mInflater;

	private FinalBitmap fb;
	
	private boolean isSelfCreate = false;
	private int event_id;
	
	public void setIsSelfCreate(boolean isSelf)
	{
		this.isSelfCreate = isSelf;
	}
	
	public void setEventId(int event_id)
	{
		this.event_id = event_id;
	}
	public EventDetailAdapter(Context context)
	{
		this(context, null);
	}

	public EventDetailAdapter(Context context, List<EventDetailRecordModel> infos)
	{
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		if (infos != null)
		{
			this.mEventDetailRecordModels = infos;
			
			this.mPhotoWallPicModels = new ArrayList<PPhotoWallPicModel>();
			
			for (int i = 0; i < infos.size(); i++) {
				if (infos.get(i).piclist != null)
				{
					for (int j = 0; j < infos.get(i).piclist.size(); j++) {
						PPhotoWallPicModel tModel = new PPhotoWallPicModel();
						tModel.big_pic_url = infos.get(i).piclist.get(j).big_pic_url;
						tModel.content = infos.get(i).piclist.get(j).content;
						tModel.id = infos.get(i).piclist.get(j).id;
						tModel.picurl = infos.get(i).piclist.get(j).picurl;
						tModel.user_nick_name = infos.get(i).user.nick_name;
						
						this.mPhotoWallPicModels.add(tModel);
					}
				}
			}
		}
		else
		{
			this.mEventDetailRecordModels = new ArrayList<EventDetailRecordModel>();
			this.mPhotoWallPicModels = new ArrayList<PPhotoWallPicModel>();
		}
		fb = FinalBitmap.create(mContext);
		thumb = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.pyj_potu);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (mEventDetailRecordModels != null)
		{
			return mEventDetailRecordModels.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (mEventDetailRecordModels != null)
		{
			return mEventDetailRecordModels.get(position);
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
            convertView = mInflater.inflate(R.layout.item_event_detail_pic, null);

            holder = new ViewHolder();
            
            holder.userImageView = (CircleImageView) convertView.findViewById(R.id.item_user_imageview);
            holder.userTitleView = (TextView) convertView.findViewById(R.id.item_user_title);
            holder.line1 = convertView.findViewById(R.id.line1);
            holder.line2 = convertView.findViewById(R.id.line2);
            holder.leftLL = (LinearLayout) convertView.findViewById(R.id.left_ll);
            holder.middleLL = (LinearLayout) convertView.findViewById(R.id.middle_ll);
            holder.rightLL = (LinearLayout) convertView.findViewById(R.id.right_ll);
            
            UTools.UI.fitViewByWidth(mContext, holder.userImageView, 50, 50, 640);
            holder.itemPics = new ImageView[9];
            holder.itemPics[0] = (ImageView) convertView.findViewById(R.id.pic0);
            holder.itemPics[1] = (ImageView) convertView.findViewById(R.id.pic1);
            holder.itemPics[2] = (ImageView) convertView.findViewById(R.id.pic2);
            holder.itemPics[3] = (ImageView) convertView.findViewById(R.id.pic3);
            holder.itemPics[4] = (ImageView) convertView.findViewById(R.id.pic4);
            holder.itemPics[5] = (ImageView) convertView.findViewById(R.id.pic5);
            holder.itemPics[6] = (ImageView) convertView.findViewById(R.id.pic6);
            holder.itemPics[7] = (ImageView) convertView.findViewById(R.id.pic7);
            holder.itemPics[8] = (ImageView) convertView.findViewById(R.id.pic8);
            
            for (int i = 0; i < 9; i++) {
            	UTools.UI.fitViewByWidth(mContext, holder.itemPics[i], 200, 200, 640);
			}
            convertView.setTag(holder);
        } 
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        for (int i = 0; i < 9; i++) {
			holder.itemPics[i].setVisibility(View.GONE);
		}
        
        holder.line1.setVisibility(View.VISIBLE);
        holder.line2.setVisibility(View.VISIBLE);
        holder.leftLL.setVisibility(View.VISIBLE);
        holder.rightLL.setVisibility(View.VISIBLE);
        holder.middleLL.setVisibility(View.VISIBLE);
        
        final EventDetailRecordModel mEventDetailRecordModel;
        mEventDetailRecordModel = mEventDetailRecordModels.get(position);
		
		fb.display(holder.userImageView, mEventDetailRecordModel.user.head_pic);
		holder.userTitleView.setText(mEventDetailRecordModel.user.nick_name + "-" + UTimeShown.getTrendsTime(mEventDetailRecordModel.add_time, "trends"));
		
		if (mEventDetailRecordModel.piclist.size() > 0)
		{
			if (mEventDetailRecordModel.piclist.size() == 1)
			{
				fb.display(holder.itemPics[0], mEventDetailRecordModel.piclist.get(0).picurl, thumb);
				
				holder.itemPics[0].setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(mContext, PhotoImagePagerActivity.class);
						intent.putExtra(UConstants.PHOTO_WALL_PIC_KEY, (Serializable) mPhotoWallPicModels);
						intent.putExtra("pic_id", mEventDetailRecordModel.piclist.get(0).id);
						intent.putExtra("isSelfCreate", isSelfCreate);
						intent.putExtra("event_id", event_id);
						mContext.startActivity(intent);
						
						if (mContext instanceof EventDetailActivity)
						{
							((EventDetailActivity) mContext).event_i_from = true;
						}
					}
				});
				UTools.UI.fitViewByWidth(mContext, holder.itemPics[0], 620, 620, 640);
				holder.itemPics[0].setVisibility(View.VISIBLE);
				for (int i = 1; i < 9; i++) {
					holder.itemPics[i].setVisibility(View.GONE);
				}
				
				holder.line1.setVisibility(View.GONE);
				holder.line2.setVisibility(View.GONE);
				holder.middleLL.setVisibility(View.GONE);
				holder.rightLL.setVisibility(View.GONE);
				
				
			}
			else if (mEventDetailRecordModel.piclist.size() == 2)
			{
				fb.display(holder.itemPics[0], mEventDetailRecordModel.piclist.get(0).picurl, thumb);
				fb.display(holder.itemPics[1], mEventDetailRecordModel.piclist.get(1).picurl, thumb);
				
				holder.itemPics[0].setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(mContext, PhotoImagePagerActivity.class);
						intent.putExtra(UConstants.PHOTO_WALL_PIC_KEY, (Serializable) mPhotoWallPicModels);
						intent.putExtra("pic_id", mEventDetailRecordModel.piclist.get(0).id);
						intent.putExtra("isSelfCreate", isSelfCreate);
						intent.putExtra("event_id", event_id);
						mContext.startActivity(intent);
						
						if (mContext instanceof EventDetailActivity)
						{
							((EventDetailActivity) mContext).event_i_from = true;
						}
					}
				});
				
				holder.itemPics[1].setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(mContext, PhotoImagePagerActivity.class);
						intent.putExtra(UConstants.PHOTO_WALL_PIC_KEY, (Serializable) mPhotoWallPicModels);
						intent.putExtra("pic_id", mEventDetailRecordModel.piclist.get(1).id);
						intent.putExtra("isSelfCreate", isSelfCreate);
						intent.putExtra("event_id", event_id);
						mContext.startActivity(intent);
						
						if (mContext instanceof EventDetailActivity)
						{
							((EventDetailActivity) mContext).event_i_from = true;
						}
					}
				});

				UTools.UI.fitViewByWidth(mContext, holder.itemPics[0], 305, 305, 640);
				UTools.UI.fitViewByWidth(mContext, holder.itemPics[1], 305, 305, 640);
				
				
				holder.itemPics[0].setVisibility(View.VISIBLE);
				holder.itemPics[1].setVisibility(View.VISIBLE);
				for (int i = 2; i < 9; i++) {
					holder.itemPics[i].setVisibility(View.GONE);
				}
				
				holder.line2.setVisibility(View.GONE);
				holder.rightLL.setVisibility(View.GONE);
			}
			else {
				for (int i = 0; i < mEventDetailRecordModel.piclist.size(); i++) {
					fb.display(holder.itemPics[i], mEventDetailRecordModel.piclist.get(i).picurl, thumb);
					final Long id = mEventDetailRecordModel.piclist.get(i).id;
					holder.itemPics[i].setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(mContext, PhotoImagePagerActivity.class);
							intent.putExtra(UConstants.PHOTO_WALL_PIC_KEY, (Serializable) mPhotoWallPicModels);
							intent.putExtra("pic_id", id);
							intent.putExtra("isSelfCreate", isSelfCreate);
							intent.putExtra("event_id", event_id);
							mContext.startActivity(intent);
							
							if (mContext instanceof EventDetailActivity)
							{
								((EventDetailActivity) mContext).event_i_from = true;
							}
						}
					});
					
					UTools.UI.fitViewByWidth(mContext, holder.itemPics[i], 200, 200, 640);
					holder.itemPics[i].setVisibility(View.VISIBLE);
				}
				
			    for (int i = mEventDetailRecordModel.piclist.size(); i < 9; i++) {
			    	holder.itemPics[i].setVisibility(View.GONE);
				}
			}
		}
		return convertView;
	}
	
	public void refreshData(List<EventDetailRecordModel> list)
	{
		mEventDetailRecordModels.clear();
		mEventDetailRecordModels.addAll(list);
		
		mPhotoWallPicModels.clear();
		
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).piclist != null)
			{
				for (int j = 0; j < list.get(i).piclist.size(); j++) {
					PPhotoWallPicModel tModel = new PPhotoWallPicModel();
					tModel.big_pic_url = list.get(i).piclist.get(j).big_pic_url;
					tModel.content = list.get(i).piclist.get(j).content;
					tModel.id = list.get(i).piclist.get(j).id;
					tModel.picurl = list.get(i).piclist.get(j).picurl;
					tModel.user_nick_name = list.get(i).user.nick_name;
					
					this.mPhotoWallPicModels.add(tModel);
				}
			}
		}
		
		notifyDataSetChanged();
	}
	
	public void addData(List<EventDetailRecordModel> list)
	{
		mEventDetailRecordModels.addAll(list);
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).piclist != null)
			{
				for (int j = 0; j < list.get(i).piclist.size(); j++) {
					PPhotoWallPicModel tModel = new PPhotoWallPicModel();
					tModel.big_pic_url = list.get(i).piclist.get(j).big_pic_url;
					tModel.content = list.get(i).piclist.get(j).content;
					tModel.id = list.get(i).piclist.get(j).id;
					tModel.picurl = list.get(i).piclist.get(j).picurl;
					tModel.user_nick_name = list.get(i).user.nick_name;
					
					this.mPhotoWallPicModels.add(tModel);
				}
			}
		}
		notifyDataSetChanged();
	}
	
	class ViewHolder
	{
		CircleImageView userImageView;
		TextView userTitleView;
		LinearLayout leftLL;
		LinearLayout middleLL;
		LinearLayout rightLL;
		
		View line1;
		View line2;
		ImageView[] itemPics;
	}

}
