package com.medialab.lejuju.main.joinevent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.fragment.FLoadingProgressBarFragment;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.main.eventdetail.EventDetailActivity;
import com.medialab.lejuju.model.EventItemModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UDisplayWidth;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.util.UUtils;

public class JSelectEventByCodeActivity extends MBaseActivity implements OnClickListener{

	private EditText invite_code_et;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_select_event_by_code);
		
		initHeaderBar();
		invite_code_et = (EditText) findViewById(R.id.invite_code_et);
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

	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mBackView)
		{
			this.finish();
		}
		else if (v == mOkView)
		{
			if (!invite_code_et.getText().toString().trim().equals(""))
			{
				Map<String,String> params = new HashMap<String,String>();
				params.put("invite_code", invite_code_et.getText().toString().trim());
				params.put("head_width", String.valueOf(UDisplayWidth.getLargeHeadPicWidth(this)));
				params.put("event_pic_width", String.valueOf(UDisplayWidth.getEventDetailPicWidth(this)));
				
				final FLoadingProgressBarFragment mLoadingProgressBarFragment = new FLoadingProgressBarFragment();
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				mLoadingProgressBarFragment.show(ft, "dialog");
				
				mDataLoader.getData(UConstants.EVENT_DETAIL_INFO_URL, params, this, new HHttpDataLoader.HDataListener()
				{
					@Override
					public void onFinish(String source)
					{
						Gson gson = new Gson();
						try {
							DetailModel temp = gson.fromJson(source, DetailModel.class);
							
							if (temp != null && temp.result.equals("success"))
							{
								if (temp.event != null)
								{
									Intent intent = new Intent(JSelectEventByCodeActivity.this, EventDetailActivity.class);
									Bundle bundle = new Bundle();
									
									bundle.putSerializable(UConstants.EVENT_DETAIL_KEY, (Serializable) temp.event);
									
									intent.putExtras(bundle);
									
									JSelectEventByCodeActivity.this.startActivity(intent);
								}
							}
							else if (temp != null && temp.result.equals("fail"))
							{
								Toast.makeText(JSelectEventByCodeActivity.this, temp.message, Toast.LENGTH_SHORT).show();
							}
							else {
								UUtils.showNetErrorToast(JSelectEventByCodeActivity.this);
							}
						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							UUtils.showNetErrorToast(JSelectEventByCodeActivity.this);
						}
						mLoadingProgressBarFragment.dismiss();
					}
					
					@Override
					public void onFail(String msg)
					{
						UUtils.showNetErrorToast(JSelectEventByCodeActivity.this);
						mLoadingProgressBarFragment.dismiss();
					}
				});
			}
			else {
				Toast.makeText(JSelectEventByCodeActivity.this, "请输入邀请码", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	class DetailModel
	{
		String result;
		String message;
		EventItemModel event;
		String invite_code;
	}

}
