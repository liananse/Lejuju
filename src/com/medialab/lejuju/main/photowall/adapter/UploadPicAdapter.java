package com.medialab.lejuju.main.photowall.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.medialab.lejuju.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class UploadPicAdapter extends BaseAdapter
{
	List<String> allPath;
	Context mContext;
	LayoutInflater inflater;
	ImageLoader imageLoader;

	public UploadPicAdapter(Context mContext, List<String> allPath, ImageLoader imageLoader)
	{
		this.mContext = mContext;
		this.allPath = allPath;
		inflater = LayoutInflater.from(mContext);
		this.imageLoader = imageLoader;
	}

	@Override
	public int getCount()
	{
		if (allPath != null)
		{
			return allPath.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position)
	{
		if (allPath != null)
		{
			return allPath.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		if (convertView == null)
		{
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_gridview_image, null);
			holder.imageView = (ImageView) convertView.findViewById(R.id.pre_image);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		if (allPath.get(position).equals("add"))
		{
			holder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.add_pic));
		}
		else
		{
			imageLoader.displayImage("file://" + allPath.get(position), holder.imageView);
		}
		return convertView;
	}

	class ViewHolder
	{
		ImageView imageView;
	}

}
