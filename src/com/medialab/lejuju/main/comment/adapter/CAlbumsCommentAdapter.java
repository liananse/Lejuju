package com.medialab.lejuju.main.comment.adapter;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.medialab.lejuju.R;
import com.medialab.lejuju.main.comment.model.CAlbumsCommentModel;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.views.RoundImageView;

public class CAlbumsCommentAdapter extends BaseAdapter{

	private List<CAlbumsCommentModel> mCAlbumsCommentModels;
	
	private Context mContext;
	private LayoutInflater mInflater;
	
	private FinalBitmap fb;
	
	private OnClickListener mCommentOnClickListener;
	
	public void setOnCommentClickListener(OnClickListener mCommentOnClickListener)
	{
		this.mCommentOnClickListener = mCommentOnClickListener;
	}
	
	public CAlbumsCommentAdapter(Context context)
	{
		this(context, null);
	}

	public CAlbumsCommentAdapter(Context context, List<CAlbumsCommentModel> infos)
	{
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		if (infos != null)
		{
			this.mCAlbumsCommentModels = infos;
		}
		else
		{
			this.mCAlbumsCommentModels = new ArrayList<CAlbumsCommentModel>();
		}

		fb = FinalBitmap.create(mContext);
	}
	
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (mCAlbumsCommentModels != null)
		{
			return mCAlbumsCommentModels.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (mCAlbumsCommentModels != null)
		{
			return mCAlbumsCommentModels.get(position);
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
            convertView = mInflater.inflate(R.layout.item_albums_comment, null);

            holder = new ViewHolder();
            
            holder.head_pic = (RoundImageView) convertView.findViewById(R.id.item_albums_comment_head_pic);
            holder.head_pic_bg = convertView.findViewById(R.id.item_albums_comment_head_pic_bg);
            holder.content = (TextView) convertView.findViewById(R.id.item_albums_comment_content);
            holder.time = (TextView) convertView.findViewById(R.id.item_albums_comment_time);
            holder.comment = (ImageView) convertView.findViewById(R.id.item_albums_comment);
    		holder.item = convertView.findViewById(R.id.item_albums_comment_item);
    		
    		holder.item.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// do nothing
				}
			});
    		
    		holder.comment.setOnClickListener(mCommentOnClickListener);
            UTools.UI.fitViewByWidth(mContext, holder.head_pic, 86, 86, 640);
            UTools.UI.fitViewByWidth(mContext, holder.head_pic_bg, 94, 94, 640);
            convertView.setTag(holder);
        } 
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        final CAlbumsCommentModel mCAlbumsCommentModel;
        mCAlbumsCommentModel = mCAlbumsCommentModels.get(position);
        
        holder.content.setText("");
        holder.time.setText("");
        if (mCAlbumsCommentModel != null)
        {
        	fb.display(holder.head_pic, mCAlbumsCommentModel.org_user.head_pic);
        	holder.content.setText(Html.fromHtml("<font color=\"#333333\">" + mCAlbumsCommentModel.org_user.nick_name + ":" + "</font>" + "<font color=\"#5b5c5f\">" + mCAlbumsCommentModel.content + "</font>"));
        	holder.time.setText(mCAlbumsCommentModel.add_time);
        	
        }
        holder.comment.setTag(mCAlbumsCommentModel);
        
        holder.head_pic_bg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				Intent intent = new Intent();
//				intent.setClass(mContext, UserInfoEntryActivity.class);
//				
//				Bundle bundle = new Bundle();
//				bundle.putSerializable(UConstants.FRIENDS_KEY, (Serializable) mCAlbumsCommentModel.org_user);
//				
//				intent.putExtras(bundle);
//				
//				
//				if (mContext instanceof CEventAlbumsCommentActivity)
//				{
//					((CEventAlbumsCommentActivity) mContext).startActivity(intent);
//				}
				
				// 头像不能点击
			}
		});
        
		return convertView;
	}
	
	static class ViewHolder
	{
		RoundImageView head_pic;
		TextView content;
		TextView time;
		ImageView comment;
		View item;
		View head_pic_bg;
	}
	
	public void refreshData(List list)
	{
		mCAlbumsCommentModels.clear();
		mCAlbumsCommentModels.addAll(list);
		notifyDataSetChanged();
	}
	
	public void addData(List list)
	{
		mCAlbumsCommentModels.addAll(list);
		notifyDataSetChanged();
	}

}
