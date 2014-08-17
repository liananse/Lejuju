package com.medialab.lejuju.main.msgcenter.model;

import java.util.List;

import com.medialab.lejuju.model.EventItemModel;
import com.medialab.lejuju.model.FriendsModel;

public class MCMsgModel {

	public int id = 0;
	public String content = "";
	public FriendsModel org_user = null;
	public EventItemModel event = null;
	public String add_time = "";
	public int type = 0;
	public int user_id = 0;
	public List<MCPicModel> picurls = null;
}
