package com.medialab.lejuju.main.more;

import android.os.Bundle;
import android.widget.ImageView;

import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.util.UTools;

public class MAboutPyjActivity extends MBaseActivity{

	private ImageView login_logo_iv;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		setContentView(R.layout.activity_about_pyj);
		
		login_logo_iv = (ImageView) findViewById(R.id.login_logo_iv);
		
		UTools.UI.fitViewByWidth(this, login_logo_iv, 169, 169, 640);
		
	}

}
