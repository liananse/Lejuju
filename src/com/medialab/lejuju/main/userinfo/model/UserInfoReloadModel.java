package com.medialab.lejuju.main.userinfo.model;

import java.util.List;


public class UserInfoReloadModel {

	public int sex = -1;
	public String head_pic = "";
	// 0陌生人，1朋友    2已关注 3未关注  4自己  5申请加我为好友的朋友 6.等待验证    2,3为公共账号专用
	public int relation = 0;
	public String introduce = "";
	// 0是普通帐号，1是公共帐号
	public int identification_state = 0;
	public String country = "";
	public String nick_name = "";
	public String area = "";
	public String school = "";
	public String background = "";
	public String company = "";
	public String account = "";
	public int user_id = -1;
	public String department = "";
	public String enter_school_year = "";
	
	// 是否允许加我为好友
	public boolean allow_add_me = false;
	public String mobile = "";
	
	public List<UserInfoPicModel> picurls = null;
	
}
