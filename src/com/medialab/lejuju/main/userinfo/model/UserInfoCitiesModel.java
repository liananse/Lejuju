package com.medialab.lejuju.main.userinfo.model;

import java.io.Serializable;

/**
 * 解析城市的model
 * @author liananse
 * 2013.07.03
 */
public class UserInfoCitiesModel implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String city_en = "";
	public String city_cn = "";
	
	public UserInfoCitiesModel(String city_en, String city_cn)
	{
		this.city_cn = city_cn;
		this.city_en = city_en;
	}

}
