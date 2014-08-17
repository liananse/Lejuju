package com.medialab.lejuju.main.msgcenter.adapter;

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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.medialab.lejuju.R;
import com.medialab.lejuju.main.comment.CEventAlbumsCommentActivity;
import com.medialab.lejuju.main.eventdetail.EDInviteFriendsActivity;
import com.medialab.lejuju.main.eventdetail.EventDetailActivity;
import com.medialab.lejuju.main.msgcenter.model.MCMsgModel;
import com.medialab.lejuju.main.userinfo.UserInfoEntryActivity;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTimeShown;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.views.RoundImageView;

public class MCMsgAdapter extends BaseAdapter{

	List<MCMsgModel> mcMsgModels;
	
	private Context mContext;
	private LayoutInflater mInflater;
	
	private FinalBitmap fb;
	public MCMsgAdapter(Context context)
	{
		this(context, null);
	}

	public MCMsgAdapter(Context context, List<MCMsgModel> infos)
	{
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		if (infos != null)
		{
			this.mcMsgModels = infos;
		}
		else
		{
			this.mcMsgModels = new ArrayList<MCMsgModel>();
		}
		fb = FinalBitmap.create(mContext);
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		
		if (mcMsgModels != null)
		{
			return mcMsgModels.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		if (mcMsgModels != null)
		{
			return mcMsgModels.get(arg0);
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
            convertView = mInflater.inflate(R.layout.item_msg_center, null);

            holder = new ViewHolder();
            
            holder.head_pic = (RoundImageView) convertView.findViewById(R.id.item_msg_center_head_pic);
            holder.head_pic_bg = convertView.findViewById(R.id.item_msg_center_head_pic_bg);
            holder.content = (TextView) convertView.findViewById(R.id.item_msg_center_content);
            holder.time = (TextView) convertView.findViewById(R.id.item_msg_center_time);
    		holder.item = convertView.findViewById(R.id.item_msg_center_item);
    		holder.imageViews = new ImageView[3];
    		
    		holder.imageViews[0] = (ImageView) convertView.findViewById(R.id.news_image1);
    		holder.imageViews[1] = (ImageView) convertView.findViewById(R.id.news_image2);
    		holder.imageViews[2] = (ImageView) convertView.findViewById(R.id.news_image3);
    		
            UTools.UI.fitViewByWidth(mContext, holder.head_pic, 86, 86, 640);
            UTools.UI.fitViewByWidth(mContext, holder.head_pic_bg, 94, 94, 640);
            
            for (int i = 0; i < 3; i++) {
            	UTools.UI.fitViewByWidth(mContext, holder.imageViews[i], 120, 120, 640);
			}
            
            convertView.setTag(holder);
        } 
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        final MCMsgModel mcMsgModel;
        mcMsgModel = mcMsgModels.get(position);
        
        for (int i = 0; i < 3; i++) {
			holder.imageViews[i].setVisibility(View.GONE);
		}
        holder.content.setText("");
        holder.time.setText("");
        if (mcMsgModel != null)
        {
        	if (mcMsgModel.org_user != null)
        	{
        		fb.display(holder.head_pic, mcMsgModel.org_user.head_pic);
        	}
        	holder.content.setText(mcMsgModel.content);
        	holder.time.setText(UTimeShown.getMsgCenterTimeShown(mcMsgModel.add_time));
        	
        	if (mcMsgModel.picurls != null && mcMsgModel.picurls.size() > 0)
        	{
        		int size = 0;
        		if (mcMsgModel.picurls.size() >= 3)
        		{
        			size = 3;
        		}
        		else {
					size = mcMsgModel.picurls.size();
				}
        		for (int i = 0; i < size; i++) {
					holder.imageViews[i].setVisibility(View.VISIBLE);
					fb.display(holder.imageViews[i], mcMsgModel.picurls.get(i).picurl);
				}
        	}
        	
        	
        	/////////////////////////根据不同的消息类型跳转到不同的界面///////////////////////////
//        	NEWS_TYPE_INVITE_JOIN_EVENT = 1;                                                               //邀请朋友参加活动
//          NEWS_TYPE_ADD_FRIEND_APPLY = 2;                                                                //添加好友（直接添加好友时推送好友申请）
//          NEWS_TYPE_SYSTEM_NOTIFY = 3;                                                                   //系统消息
//          NEWS_TYPE_P2P_COMMENT = 4;                                                                     //点对点活动评论（回复评论）
//          NEWS_TYPE_ALLOW_ADD_FRIEND_SUCCESS = 5;                                                        //互相都愿意加好友时
//          NEWS_TYPE_APPLY_JOIN_EVENT = 6;                                                                //申请加入活动
//          NEWS_TYPE_JOIN_EVENT_AUDIT_PASS = 7;                                                           //加入活动审核通过
//          NEWS_TYPE_JOIN_EVENT_AUDIT_REFUSE = 8;                                                         //加入活动审核拒绝
//          NEWS_TYPE_ADD_FRIEND_AUDIT_PASS = 9;                                                           //添加朋友审核通过
//          NEWS_TYPE_ADD_FRIEND_AUDIT_REFUSE = 10;                                                        //添加朋友审核拒绝
//          NEWS_TYPE_SEND_EVENT_RECORD = 11;                                                              //发送活动记录
//          NEWS_TYPE_CHANGE_EVENT_INFO = 12;                                                              //修改活动信息
//          NEWS_TYPE_COMMENT = 13;                                                                        //活动评论
        	
        	switch (mcMsgModel.type) {
			case 1:
				holder.item.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (mcMsgModel.event != null)
						{
							Intent intent = new Intent(mContext, EventDetailActivity.class);
							Bundle bundle = new Bundle();
							
							bundle.putSerializable(UConstants.EVENT_DETAIL_KEY, (Serializable) mcMsgModel.event);
							intent.putExtras(bundle);
							mContext.startActivity(intent);
						}
					}
				});
				break;
			case 2:
				holder.item.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (mcMsgModel.org_user != null)
						{
							Intent intent = new Intent();
							intent.setClass(mContext, UserInfoEntryActivity.class);
							
							Bundle bundle = new Bundle();
							bundle.putSerializable(UConstants.FRIENDS_KEY, (Serializable) mcMsgModel.org_user);
							
							intent.putExtras(bundle);
							
							
							mContext.startActivity(intent);
						}
					}
				});
				break;
			case 3:
				holder.item.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// do nothing
					}
				});
				break;
			case 4:
				holder.item.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (mcMsgModel.event != null)
						{
							Bundle bundle = new Bundle();
							
							bundle.putSerializable(UConstants.EVENT_DETAIL_KEY, (Serializable) mcMsgModel.event);
							bundle.putSerializable(UConstants.FRIENDS_KEY, (Serializable) mcMsgModel.event.master);
							
							Intent intent = new Intent();
							intent.setClass(mContext, CEventAlbumsCommentActivity.class);
							intent.putExtras(bundle);
							mContext.startActivity(intent);
						}
					}
				});
				break;
			case 5:
				holder.item.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (mcMsgModel.org_user != null)
						{
							Intent intent = new Intent();
							intent.setClass(mContext, UserInfoEntryActivity.class);
							
							Bundle bundle = new Bundle();
							bundle.putSerializable(UConstants.FRIENDS_KEY, (Serializable) mcMsgModel.org_user);
							
							intent.putExtras(bundle);
							
							
							mContext.startActivity(intent);
						}
					}
				});
				break;
			case 6:
				holder.item.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (mcMsgModel.event != null)
						{
//							Intent intent = new Intent(mContext, EventDetailActivity.class);
//							Bundle bundle = new Bundle();
//							
//							bundle.putSerializable(UConstants.EVENT_DETAIL_KEY, (Serializable) mcMsgModel.event);
//							intent.putExtras(bundle);
//							mContext.startActivity(intent);
							
							// 从消息中心直接跳转到审核页面 改：2013.12.04
							Intent intent = new Intent();
							Bundle bundle_APPLY_JOIN_EVENT = new Bundle();
							bundle_APPLY_JOIN_EVENT.putSerializable(UConstants.EVENT_DETAIL_KEY, (Serializable) mcMsgModel.event);
							bundle_APPLY_JOIN_EVENT.putBoolean(UConstants.FROM_PUSH, false);
							
							intent.putExtras(bundle_APPLY_JOIN_EVENT);
							intent.setClass(mContext, EDInviteFriendsActivity.class);
							
							mContext.startActivity(intent);
						}
					}
				});
				break;
			case 7:
				holder.item.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (mcMsgModel.event != null)
						{
							Intent intent = new Intent(mContext, EventDetailActivity.class);
							Bundle bundle = new Bundle();
							
							bundle.putSerializable(UConstants.EVENT_DETAIL_KEY, (Serializable) mcMsgModel.event);
							intent.putExtras(bundle);
							mContext.startActivity(intent);
						}
					}
				});
				break;
			case 8:
				holder.item.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (mcMsgModel.event != null)
						{
							Intent intent = new Intent(mContext, EventDetailActivity.class);
							Bundle bundle = new Bundle();
							
							bundle.putSerializable(UConstants.EVENT_DETAIL_KEY, (Serializable) mcMsgModel.event);
							intent.putExtras(bundle);
							mContext.startActivity(intent);
						}
					}
				});
				break;
				
			case 9:
				holder.item.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (mcMsgModel.org_user != null)
						{
							Intent intent = new Intent();
							intent.setClass(mContext, UserInfoEntryActivity.class);
							
							Bundle bundle = new Bundle();
							bundle.putSerializable(UConstants.FRIENDS_KEY, (Serializable) mcMsgModel.org_user);
							
							intent.putExtras(bundle);
							
							
							mContext.startActivity(intent);
						}
					}
				});
				break;
			case 10:
				holder.item.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (mcMsgModel.org_user != null)
						{
							Intent intent = new Intent();
							intent.setClass(mContext, UserInfoEntryActivity.class);
							
							Bundle bundle = new Bundle();
							bundle.putSerializable(UConstants.FRIENDS_KEY, (Serializable) mcMsgModel.org_user);
							
							intent.putExtras(bundle);
							
							
							mContext.startActivity(intent);
						}
					}
				});
				break;
			case 11:
				holder.item.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (mcMsgModel.event != null)
						{
							
							Intent intent = new Intent(mContext, EventDetailActivity.class);
							Bundle bundle = new Bundle();
							
							bundle.putSerializable(UConstants.EVENT_DETAIL_KEY, (Serializable) mcMsgModel.event);
							intent.putExtras(bundle);
							mContext.startActivity(intent);
						}
					}
				});
				break;
			case 12:
				holder.item.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (mcMsgModel.event != null)
						{
							Intent intent = new Intent(mContext, EventDetailActivity.class);
							Bundle bundle = new Bundle();
							
							bundle.putSerializable(UConstants.EVENT_DETAIL_KEY, (Serializable) mcMsgModel.event);
							intent.putExtras(bundle);
							mContext.startActivity(intent);
						}
					}
				});
				break;
			case 13:
				holder.item.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (mcMsgModel.event != null)
						{
							Bundle bundle = new Bundle();
							
							bundle.putSerializable(UConstants.EVENT_DETAIL_KEY, (Serializable) mcMsgModel.event);
							bundle.putSerializable(UConstants.FRIENDS_KEY, (Serializable) mcMsgModel.event.master);
							
							Intent intent = new Intent();
							intent.setClass(mContext, CEventAlbumsCommentActivity.class);
							intent.putExtras(bundle);
							mContext.startActivity(intent);
						}
					}
				});
				break;
			default:
				holder.item.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// do nothing
					}
				});
				break;
			}

        }
        
		return convertView;
	}
	
	
	static class ViewHolder
	{
		RoundImageView head_pic;
		TextView content;
		TextView time;
		View item;
		View head_pic_bg;
		ImageView[] imageViews;
	}
	
	public void refreshData(List list)
	{
		mcMsgModels.clear();
		mcMsgModels.addAll(list);
		notifyDataSetChanged();
	}
	
	public void addData(List list)
	{
		mcMsgModels.addAll(list);
		notifyDataSetChanged();
	}

}
