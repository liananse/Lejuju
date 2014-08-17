package com.medialab.lejuju.main.userinfo;

import java.util.List;

import android.os.Bundle;
import android.widget.ListView;

import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.main.userinfo.adapter.UserInfoSelectRegionCityAdapter;
import com.medialab.lejuju.main.userinfo.model.UserInfoCitiesModel;
import com.medialab.lejuju.main.userinfo.model.UserInfoProvinceModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTools;

public class UserInfoSelectRegionCityActivity extends MBaseActivity{
	
	private List<UserInfoCitiesModel> mCityList = null;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		setContentView(R.layout.activity_userinfo_select_region);
		
		UserInfoProvinceModel mProvinceModel = (UserInfoProvinceModel) getIntent().getSerializableExtra(UConstants.SER_KEY);  
		
		mCityList = mProvinceModel.cities;
		
		String province = "";
		if(UTools.OS.getLanguage().equals("zh"))
        {
			province = mProvinceModel.province_cn;
        }
        else {
        	province = mProvinceModel.province_en;
		}
		
		((ListView) findViewById(R.id.region_listview)).setAdapter(new UserInfoSelectRegionCityAdapter(this, mCityList, province, getIntent()));
	}
	
}
