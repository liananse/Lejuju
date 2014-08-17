package com.medialab.lejuju.main.joinevent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.medialab.lejuju.R;
import com.medialab.lejuju.main.joinevent.adapter.JInviteContactsFriendsAdapter;
import com.medialab.lejuju.main.joinevent.model.JContactsModel;
import com.medialab.lejuju.model.EventItemModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTextUtils;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.util.UUtils;
import com.medialab.lejuju.views.SideListBar;

public class JInviteContactsFriendsActivity extends Activity implements OnClickListener{

	private ListView mListView;
	private SideListBar indexBar;
	private WindowManager mWindowManager;
	private TextView mDialogText;
	
	private View mSearchView;
	private EditText mSearchEditText;
	private JInviteContactsFriendsAdapter mAdapter;

	private List<JContactsModel> mContactsModels = new ArrayList<JContactsModel>();
	private List<JContactsModel> mSearchResultModels = new ArrayList<JContactsModel>();
	
	private EventItemModel mEventItemModel = null;
	private String inviteType = "";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite_contacts_friends);
		
		inviteType = getIntent().getStringExtra("invite_type");
		
		if (inviteType.equals(UConstants.EVENT_INVITE))
		{
			mEventItemModel = (EventItemModel) getIntent().getSerializableExtra(UConstants.EVENT_DETAIL_KEY);  
		}
		initHeaderBar();
		initView();
	}
	
	// 初始化header bar
	private View mBackView;
	private View mOkView;
	
	private ImageView mBackImageView;
	private ImageView mOkImageView;
	
	private void initHeaderBar()
	{
		mBackView = findViewById(R.id.back_btn);
		mOkView = findViewById(R.id.ok_btn);
		
		mBackImageView = (ImageView) findViewById(R.id.back_btn_center);
		mOkImageView = (ImageView) findViewById(R.id.ok_btn_center);
		
		UTools.UI.fitViewByWidth(this, mBackView, 130, 62, 640);
		UTools.UI.fitViewByWidth(this, mOkView, 130, 62, 640);
		
		UTools.UI.fitViewByWidth(this, mBackImageView, 18, 32, 640);
		UTools.UI.fitViewByWidth(this, mOkImageView, 45, 28, 640);
		
		mBackView.setOnClickListener(this);
		mOkView.setOnClickListener(this);
	}
	
	private void initView()
	{
		mListView = (ListView) this.findViewById(R.id.content_list_view);
		mSearchView = LayoutInflater.from(this).inflate(R.layout.header_view_search, null);
		
		mSearchEditText = (EditText) mSearchView.findViewById(R.id.search_editText);
		
		mSearchEditText.addTextChangedListener(textWatcher);
		
		indexBar = (SideListBar) findViewById(R.id.sideListBar);
		mDialogText = (TextView) LayoutInflater.from(this).inflate(R.layout.list_position, null);
		mListView.addHeaderView(mSearchView);
		mAdapter = new JInviteContactsFriendsAdapter(this);
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
		mContactsModels = getContacts();
		//根据a-z进行排序
		Collections.sort(mContactsModels, new PinyinComparator());
		mListView.setAdapter(mAdapter);
		indexBar.setListView(mListView);
		mAdapter.refreshData(mContactsModels);
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mWindowManager.removeView(mDialogText);
		super.onDestroy();
	}


	TextWatcher textWatcher = new TextWatcher()
	{
		@Override
		public void afterTextChanged(Editable s)
		{
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			String newKeyword = s.toString();

			if (TextUtils.isEmpty(newKeyword))
			{
					mAdapter.refreshData(mContactsModels);
			}
			else
			{
				
				mSearchResultModels = searchFriends(mContactsModels, s.toString());
				mAdapter.refreshData(mSearchResultModels);
			}
		}
	};
	
	public List<JContactsModel> searchFriends(List<JContactsModel> list,String searchStr)
	{
		if (searchStr.trim().length() == 0)
		{
			return new ArrayList<JContactsModel>();
		}
		Vector<JContactsModel> result = new Vector<JContactsModel>();
		if (list != null && list.size() > 0)
		{
			for (JContactsModel info : list)
			{
				char i = searchStr.charAt(0);
				// 是汉字开头，只判断没有转拼音的字段就可以了
				if (java.lang.Character.toString(i).matches("[\\u4E00-\\u9FA5]+"))
				{
					if (info.name.contains(searchStr.toLowerCase()))
					{
						result.add(info);
						continue;
					}
				}
				else
				{
					if (info.name.toLowerCase().contains(searchStr.toLowerCase()) || info.namePinYin.toLowerCase().contains(searchStr.toLowerCase()) || info.namePinYinFirst.toLowerCase().contains(searchStr.toLowerCase()))
					{
						result.add(info);
						continue;
					}
				}
			}
		}
		return result;
		
	}
	
	public List<JContactsModel> getContacts()
	{
		List<JContactsModel> list = new ArrayList<JContactsModel>();
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER };
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
		
		ContentResolver resolver = getContentResolver();
		Cursor cursor = resolver.query(uri, projection, null, null, sortOrder);

		while (cursor.moveToNext())
		{
			String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			String phoneNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			JContactsModel man = new JContactsModel(name,String.valueOf(phoneNum.trim().replace(" ", "").replace("+", "")),0, UTextUtils.getPingYin(name), UTextUtils.getPinYinHeadChar(name));

			list.add(man);
		}
		cursor.close();
		return list;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mOkView)
		{
			List<JContactsModel> mList = new ArrayList<JContactsModel>();
			for (int i = 0; i < mContactsModels.size(); i++) {
				if (mContactsModels.get(i).isSelected == 1)
				{
					mList.add(mContactsModels.get(i));
				}
			}
			if (mList.size() > 0)
			{
				List<String> mobile = new ArrayList<String>();
				for (int i = 0; i < mList.size(); i++) {
					mobile.add(mList.get(i).mobile);
				}
				
				Uri smsToUri = Uri.parse("smsto:" + TextUtils.join(";", mobile));
				Intent mIntent = new Intent(android.content.Intent.ACTION_SENDTO, smsToUri);
				// 加内容进去
				if (inviteType.equals(UConstants.EVENT_INVITE))
				{
					if (mEventItemModel != null)
						mIntent.putExtra("sms_body", mEventItemModel.sms_invite_content);
				}
				else if (inviteType.equals(UConstants.FRIENDS_INVITE))
				{
					mIntent.putExtra("sms_body", UUtils.getSelfUserInfoModel(this).sms_content);
				}
				startActivity(mIntent);
			}
		}
		else if(v == mBackView)
		{
			finish();
		}
	}

}
