package com.medialab.lejuju.model;

import java.io.Serializable;

import com.medialab.lejuju.util.UTextUtils;

public class FriendsModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	public String namePinYin = "";
	public String namePinYinFirst = "";
	// 是否被选中 1表示被选中 0 表示未被选中
	public int isSelected = 0;
	// 区分参与人还是需要审核的好友 0 表示不需要审核 1 表示需要审核
	public int needCheck = 0;
	
	// 新的朋友页面需要
	public String memo_name = "";
	
	public FriendsModel(FriendsModel model)
	{
		this.sex = model.sex;
		this.head_pic = model.head_pic;
		this.relation = model.relation;
		this.introduce = model.introduce;
		this.identification_state = model.identification_state;
		this.country = model.country;
		this.nick_name = model.nick_name;
		this.area = model.area;
		this.school = model.school;
		this.background = model.background;
		this.company = model.company;
		this.account = model.account;
		this.user_id = model.user_id;
		this.allow_add_me = model.allow_add_me;
		this.mobile = model.mobile;
		this.namePinYin = UTextUtils.getPingYin(model.nick_name);
		this.namePinYinFirst = UTextUtils.getPinYinHeadChar(model.nick_name);
		this.isSelected = 0;
	}
	
	public FriendsModel()
	{
	}
	
	@Override
	public String toString() {
		return "FriendsModel [user_id=" + user_id + ", nick_name=" + nick_name
				+ ", sex=" + sex + ", mobile=" + mobile + ", area=" + area
				+ ", country=" + country + ", account=" + account + ", school="
				+ school + ", company=" + company + ", introduce=" + introduce
				+ ", head_pic=" + head_pic + ", namePinYin="
				+ namePinYin + ", namePinYinFirst=" + namePinYinFirst
				+ ", isSelected=" + isSelected + ", relation=" + relation + "]";
	}


	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		FriendsModel other = (FriendsModel) obj;
		if(user_id != -1 && other.user_id != -1 && user_id == other.user_id)
		{
			return true;
		}
		return false;
	}
	
}
