package com.medialab.lejuju.model;

import java.io.Serializable;
import java.util.List;

import com.medialab.lejuju.main.myevent.model.MPicInfoModel;


public class EventItemModel implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String memo = "";
	public String introduce = "";
	public int event_id = -1;
	// 1 表示AA制,2 表示发起人请，3 其它类型
	public int spent_type = -1;
	// 是否同意朋友邀请朋友（1表示是，0表示否）
	public int forward_invite = -1;
	public String other_spent_type = "";
	// 是否公开活动信息（0表示私密活动，1表示只公开给朋友们，2完全公开）
	public int public_info = -1;
	public String event_type_name = "";
	public String title = "";
	public boolean interest = false;
	public int zan_num = -1;
	public String people_num = "";
	public String spent = "";
	public String share_content = "";
	// 1表示未开始，2表示进行中，3表示已结束
	public int event_state = -1;
	public String people_join = "";
	public int event_type_id = -1;
	// 时间状态    1表示已定    0表示未定
	public int time_state = -1;
	// 0 未加入，1，已申请参加，2，已被邀请，3，已参加
	public int join = 0;
	// 地址状态    1表示已定    0表示未定
	public int address_state = -1;
	public String event_pic = "";
	public boolean zan = false;
	public String address = "";
	public String end_time = "";
	public String start_time = "";
	public String add_time = "";
	public double x = 0;
	public double y = 0;
	public int need_audit = 0;
	public String invite_code = "";
	// 1邀请码可以看到 0不能看到
	public int invite_code_public = 0;
	
	public boolean allow_join = false;
	public String event_invite_url = "";
	public String event_share_url = "";
	public String sms_invite_content = "";
	public String wx_invite_content = "";
	// 附近的活动才有
	public double dist = 0;
	
	// 我参加的活动才有
	public int event_trends_num = 0;
	public List<TrendItemModel> trends = null;
	
	public String trends_content = "";
	public String trends_nick_name = "";
	public String trends_head_pic = "";
	public String trends_add_time = "";
	public int un_read_pic_num = 0;
	
	// 以往活动才有
	public List<MPicInfoModel> picurls = null;
	public int comment_num = -1;
	
	// 这个字段我参加的活动和以往活动都有
	public int pic_num = 0;
	
	public FriendsModel master = null;
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		EventItemModel other = (EventItemModel) obj;
		if(event_id != -1 && other.event_id != -1 && event_id == other.event_id)
		{
			return true;
		}
		return false;
	}
	
}
