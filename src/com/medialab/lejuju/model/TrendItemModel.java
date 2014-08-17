package com.medialab.lejuju.model;

import java.io.Serializable;

/**
 * @author liananse
 *
 */
public class TrendItemModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id = -1;
	public FriendsModel org_user = null;
	public String content = "";
	public int event_id = -1;
	public int type = -1; // 1 表示普通内容 2表示活动的动态信息
	public String add_time;
	public int trends_id = -1;
	
	public int show_time = 1; // 1 表示显示时间 0 表示不显示时间
	
}
