package com.medialab.lejuju.main.joinevent.model;

import java.io.Serializable;

public class JFriendsModel implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int user_id = -1;
	public String nick_name = "";
	public int sex = -1;
	public String mobile = "";
	public String area = "";
	public String country = "";
	public String account = "";
	public String school = "";
	public String company = "";
	public String introduce = "";
	public String headpic = "";
	public String memo = "";
	public String namePinYin = "";
	public String namePinYinFirst = "";
	
	public JFriendsModel(int user_id, String nick_name, int sex, String mobile, String area, String country, String account, String school, String company
			,String introduce, String headpic, String memo, String namePinYin, String namePinYinFirst)
	{
		this.user_id = user_id;
		this.nick_name = nick_name;
		this.mobile = mobile;
		this.area = area;
		this.country = country;
		this.account = account;
		this.school = school;
		this.company = company;
		this.introduce = introduce;
		this.headpic = headpic;
		this.memo = memo;
		this.namePinYin = namePinYin;
		this.namePinYinFirst = namePinYinFirst;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	
	

}
