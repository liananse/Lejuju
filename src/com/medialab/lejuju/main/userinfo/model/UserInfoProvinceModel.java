package com.medialab.lejuju.main.userinfo.model;

import java.io.Serializable;
import java.util.List;


/**
 * 解析省份的model
 * @author liananse
 *
 */
public class UserInfoProvinceModel implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String province_cn = "";
	public String province_en = "";
	public int sum = 0;
	public List<UserInfoCitiesModel> cities = null;
	
	public UserInfoProvinceModel(String province_cn, String province_en, int sum, List<UserInfoCitiesModel> cities)
	{
		this.province_cn = province_cn;
		this.province_en = province_en;
		this.sum = sum;
		this.cities = cities;
	}
	
}
