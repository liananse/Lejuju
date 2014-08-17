package com.medialab.lejuju.main.friends;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.MMainTabActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.db.DDBOpenHelper;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.main.friends.adapter.FFriendsEntryAdapter;
import com.medialab.lejuju.model.FriendsModel;
import com.medialab.lejuju.model.FriendsPinyinComparator;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.views.SideListBar;

public class FFriendsEntryActivity extends MBaseActivity implements OnClickListener{

	private FFriendsEntryAdapter mAdapter;
	
	private boolean hasHeaderView = true;
	private List<FriendsModel> mFriendsModels = new ArrayList<FriendsModel>();
	private List<FriendsModel> mSearchResultList = new ArrayList<FriendsModel>();
	// DATALOAD
	HHttpDataLoader mDataLoader = new HHttpDataLoader();
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_friends_entry);
		initContentView();
	}
	
	private ListView mListView;
	private View mSearchView;
	private EditText mSearchEditText;
	private View mHeaderView;
	private TextView mNewFriendsCount;
	private View mFooterView;
	private View mAddFriendsView;
	private View mNewFriendsView;
	
	private SideListBar indexBar;
	private WindowManager mWindowManager;
	private TextView mDialogText;
	
	private void initContentView()
	{
		mListView = (ListView) this.findViewById(R.id.content_list_view);
		
		mSearchView = LayoutInflater.from(this).inflate(R.layout.header_view_search, null);
		mSearchEditText = (EditText) mSearchView.findViewById(R.id.search_editText);
		
		mSearchEditText.addTextChangedListener(textWatcher);
		//
		indexBar = (SideListBar) findViewById(R.id.sideListBar);
		mDialogText = (TextView) LayoutInflater.from(this).inflate(R.layout.list_position, null);
		mDialogText.setVisibility(View.INVISIBLE);
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		mWindowManager.addView(mDialogText, lp);
		indexBar.setTextView(mDialogText);
		
		// HEADER VIEW
		mHeaderView = LayoutInflater.from(this).inflate(R.layout.header_view_friends, null);
		mAddFriendsView = mHeaderView.findViewById(R.id.add_friends_view);
		mNewFriendsView = mHeaderView.findViewById(R.id.new_friends_view);
		mNewFriendsCount = (TextView) mHeaderView.findViewById(R.id.new_friends_count);
		UTools.UI.fitViewByWidth(this, mNewFriendsCount, 42, 42, 640);
		mAddFriendsView.setOnClickListener(this);
		mNewFriendsView.setOnClickListener(this);
		// FOOTER VIEW
		mFooterView = LayoutInflater.from(this).inflate(R.layout.footer_view_friends, null);
		
		mListView.addHeaderView(mSearchView);
		mListView.addHeaderView(mHeaderView);
		
		mListView.addFooterView(mFooterView);
		mAdapter = new FFriendsEntryAdapter(this);
		
		mListView.setAdapter(mAdapter);
		indexBar.setListView(mListView);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		DDBOpenHelper mDdbOpenHelper = DDBOpenHelper.getInstance(FFriendsEntryActivity.this);
		
		mFriendsModels = mDdbOpenHelper.getFriendsModelsFromDB();
		Collections.sort(mFriendsModels, new FriendsPinyinComparator());
		mAdapter.refreshData(mFriendsModels);
		
		int un_read_news_count = UTools.Storage.getSharedPreferences(this, UConstants.BASE_PREFS_NAME).getInt(UConstants.NEW_FRIENDS_NUM, 0);
		if (un_read_news_count > 0)
		{
			mNewFriendsCount.setVisibility(View.VISIBLE);
			mNewFriendsCount.setText(un_read_news_count+"");
		}
		else {
			mNewFriendsCount.setVisibility(View.INVISIBLE);
		}
		
		if (getParent() instanceof MMainTabActivity)
		{
			((MMainTabActivity) getParent()).updateNotify();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if (v == mAddFriendsView)
		{
			// 跳转到添加朋友的页面
			Intent intent = new Intent();
			intent.setClass(this,FNewFriendsActivity.class);
			this.startActivity(intent);
		}
		else if (v == mNewFriendsView)
		{
			// 跳转到新朋友页面
			Intent intent = new Intent();
			intent.setClass(this,FHaveNewFriendsActivity.class);
			this.startActivity(intent);
		}
	}
	
	TextWatcher textWatcher = new TextWatcher()
	{
		@Override
		public void afterTextChanged(Editable s)
		{
			// DO NOTHING
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
			// DO NOTHING
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			String newKeyword = s.toString();

			if (TextUtils.isEmpty(newKeyword))
			{
				if (!hasHeaderView)
				{
					mListView.addHeaderView(mHeaderView);
					hasHeaderView = true;
					mAdapter.refreshData(mFriendsModels);
				}
			}
			else
			{
				if (hasHeaderView)
				{
					mListView.removeHeaderView(mHeaderView);
					hasHeaderView = false;
				}
				
				mSearchResultList = searchFriends(mFriendsModels, s.toString());
				mAdapter.refreshData(mSearchResultList);
			}
		}
	};
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mWindowManager.removeView(mDialogText);
		super.onDestroy();
	}

	public List<FriendsModel> searchFriends(List<FriendsModel> list,String searchStr)
	{
		if (searchStr.trim().length() == 0)
		{
			return new ArrayList<FriendsModel>();
		}
		Vector<FriendsModel> result = new Vector<FriendsModel>();
		if (list != null && list.size() > 0)
		{
			for (FriendsModel info : list)
			{
				char i = searchStr.charAt(0);
				// 是汉字开头，只判断没有转拼音的字段就可以了
				if (java.lang.Character.toString(i).matches("[\\u4E00-\\u9FA5]+"))
				{
					if (info.nick_name.contains(searchStr.toLowerCase()))
					{
						result.add(info);
						continue;
					}
				}
				else
				{
					if (info.nick_name.toLowerCase().contains(searchStr.toLowerCase()) || 
							info.namePinYin.toLowerCase().contains(searchStr.toLowerCase()) || 
							info.namePinYinFirst.toLowerCase().contains(searchStr.toLowerCase()))
					{
						result.add(info);
						continue;
					}
				}
			}
		}
		return result;
		
	}

}
