package com.medialab.lejuju.main.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.MMainTabActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.http.HHttpDataLoader.HDataListener;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTools;

public class LOpenContactsActivity extends MBaseActivity implements
		OnClickListener {

	private Button contactsEnableButton;
	
	private HHttpDataLoader mDataLoader = new HHttpDataLoader();

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_open_contacts);
		initHeaderBar();
		initView();
	}
	
	// 初始化header bar
	private View mOkView;
	
	private ImageView mOkImageView;
	
	private void initHeaderBar()
	{
		mOkView = findViewById(R.id.ok_btn);
		
		mOkImageView = (ImageView) findViewById(R.id.ok_btn_center);
		
		UTools.UI.fitViewByWidth(this, mOkView, 130, 62, 640);
		
		UTools.UI.fitViewByWidth(this, mOkImageView, 45, 28, 640);
		
		mOkView.setOnClickListener(this);
	}

	private void initView() {
		contactsEnableButton = (Button) findViewById(R.id.contacts_enable_btn);
		contactsEnableButton.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == contactsEnableButton) {

			Map<String, String> params = new HashMap<String, String>();
			Gson gson = new Gson();
			List<Contact> Contacts = getContacts();
			
			String persString = gson.toJson(Contacts);
			params.put("mobile_list", persString);
			
			mDataLoader.postData(UConstants.LOPENCONTACTS_URL, params, this, new HDataListener() {
				
				@Override
				public void onFinish(String source) {
					// TODO Auto-generated method stub
				}
				
				@Override
				public void onFail(String msg) {
					// TODO Auto-generated method stub
				}
			});
			
			// 跳转至主页面
			Intent intent = new Intent(LOpenContactsActivity.this,MMainTabActivity.class);
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			LOpenContactsActivity.this.finish();
		}
		else if (v == mOkView)
		{
			Intent intent = new Intent(LOpenContactsActivity.this,MMainTabActivity.class);
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			LOpenContactsActivity.this.finish();
		}
	}


	public List<Contact> getContacts()
	{
		List<Contact> list = new ArrayList<Contact>();
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER };
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
		Cursor cursor = managedQuery(uri, projection, null, null, sortOrder);

		while (cursor.moveToNext())
		{
			String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			String phoneNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			Contact man = new Contact();
			man.setName(name);
			man.setMobile(String.valueOf(phoneNum.trim().replace(" ", "").replace("+", "")));

			list.add(man);
		}
		cursor.close();
		return list;
	}
	
	class Contact
	{
		private String name;
		private String mobile;

		@Override
		public String toString()
		{
			return "Contact [name=" + name + ", mobile=" + mobile + "]";
		}

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public String getMobile()
		{
			return mobile;
		}

		public void setMobile(String mobile)
		{
			this.mobile = mobile;
		}
	}
}
