package com.medialab.lejuju.main.friends;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TableRow;

import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.main.joinevent.JInviteContactsFriendsActivity;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.util.UUtils;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class FNewFriendsActivity extends MBaseActivity implements OnClickListener{

	private View search_certain_friends_layout;
	private TableRow invite_mobile_friends_layout;
	private TableRow invite_weixin_friends_layout;
	private TableRow invite_pengyouquan_friends_layout;
	
	private IWXAPI api;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_friends_entry_add_friends);
		regToWx(this);
		
		initHeaderBar();
		search_certain_friends_layout = findViewById(R.id.search_certain_friends_layout);
		search_certain_friends_layout.setOnClickListener(this);
		
		invite_mobile_friends_layout = (TableRow) findViewById(R.id.invite_mobile_friends_layout);
		invite_mobile_friends_layout.setOnClickListener(this);
		
		invite_weixin_friends_layout = (TableRow) findViewById(R.id.invite_weixin_friends_layout);
		invite_weixin_friends_layout.setOnClickListener(this);
		
		invite_pengyouquan_friends_layout = (TableRow) findViewById(R.id.invite_pengyouquan_friends_layout);
		invite_pengyouquan_friends_layout.setOnClickListener(this);
		
	}
	
	// 初始化header bar
	private View mBackView;
	
	private ImageView mBackImageView;
	
	private void initHeaderBar()
	{
		mBackView = findViewById(R.id.back_btn);
		
		mBackImageView = (ImageView) findViewById(R.id.back_btn_center);
		
		UTools.UI.fitViewByWidth(this, mBackView, 130, 62, 640);
		
		UTools.UI.fitViewByWidth(this, mBackImageView, 18, 32, 640);
		
		mBackView.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v)
	{
		if(v == mBackView)
		{
			finish();
		}
		else if(v == search_certain_friends_layout)
		{
			Intent intent = new Intent(this,FAddNewFriendsActivity.class);
			startActivity(intent);
		}
		else if(v == invite_mobile_friends_layout)
		{
			Intent intent = new Intent();
			intent.setClass(this, JInviteContactsFriendsActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("invite_type", UConstants.FRIENDS_INVITE);
			intent.putExtras(bundle);
			
			this.startActivity(intent);
		}
		else if(v == invite_weixin_friends_layout)
		{
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = UUtils.getSelfUserInfoModel(this).url;
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = "朋友聚";
			msg.description = UUtils.getSelfUserInfoModel(this).wx_content;
			Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.about_logo);
			msg.thumbData = UUtils.bmpToByteArray(thumb, true);
			
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("webpage");
			req.message = msg;
			api.sendReq(req);
		}
		else if(v == invite_pengyouquan_friends_layout)
		{
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = UUtils.getSelfUserInfoModel(this).url;
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = "朋友聚";
			msg.description = UUtils.getSelfUserInfoModel(this).wx_content;
			Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.about_logo);
			msg.thumbData = UUtils.bmpToByteArray(thumb, true);
			
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("webpage");
			req.message = msg;
			req.scene = req.WXSceneTimeline;
			api.sendReq(req);
		}
		
	}
	
	public void regToWx(Context context)
	{
		api = WXAPIFactory.createWXAPI(context, UConstants.WX_APP_ID);
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

}
