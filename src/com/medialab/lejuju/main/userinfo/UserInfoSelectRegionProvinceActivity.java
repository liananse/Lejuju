package com.medialab.lejuju.main.userinfo;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.main.userinfo.adapter.UserInfoSelectRegionProvinceAdapter;
import com.medialab.lejuju.main.userinfo.model.UserInfoProvinceModel;
import com.medialab.lejuju.util.UTools;

/**
 * 选择地区activity
 * 
 * @author liananse
 * 2012.07.03
 */
public class UserInfoSelectRegionProvinceActivity extends MBaseActivity{
	

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		setContentView(R.layout.activity_userinfo_select_region);
		
		String source = UTools.IO.getStringFromTxt(this, "region.txt");
		
		Gson gson = new Gson();
		
		List<UserInfoProvinceModel> list = gson.fromJson(source, new TypeToken<List<UserInfoProvinceModel>>(){}.getType());
		
		((ListView) findViewById(R.id.region_listview)).setAdapter(new UserInfoSelectRegionProvinceAdapter(this, list));
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) {
		
		case RESULT_OK:
			Bundle bundle = data.getExtras();
			Intent intent = getIntent();
			intent.putExtras(bundle);
			this.setResult(RESULT_OK, intent);
			
			this.finish();
			break;

		default:
			break;
		}
	}

}
