package com.medialab.lejuju.util;

import android.R.integer;


/**
 * 系统中用到的常量
 * @author zenghui
 * 2013-07-01
 */
public class UConstants {
	
	/**
	 * config
	 */
	// weChat APP ID
	public static final String WX_APP_ID = "wxcd86f10d172b8e2d";
	
	//renren登录相关
	public static final String RENREN_APP_ID = "239093";
	public static final String RENREN_APP_KEY = "45852da417b64675a9aec7a3efc3da30";
	public static final String RENREN_APP_SECRET_KEY = "9d32d43052a642a987ac44cc539321c9";
	
	//baidu app key
	public static final String BAIDU_APP_KEY = "fj5lu2litPpxSGvojBXUr17d";
	
	public static final String BAIDU_MAP_KEY = "394913EBF515CFF13E810AE6287690BC8E9B189E";

	// 服务器请求地址
	public static final String SERVER_HOST = "http://www.yeskee.com.cn:10086/"; // 正式机
//	public static final String SERVER_HOST = "http://218.18.232.10:10086/"; // 测试机
//	public static final String SERVER_HOST = "http://10.10.151.45:10086/"; // 测试机
	
	// 图片服务器地址
	public static final String IMAGE_SERVER = "";
	
	// application name
	public static final String APP_NAME = "pengyouju";
	
	public static final int SELECT_REGION_REQUESTCODE = 0;
	public static final int SELECT_EVENT_TIME_REQUESTCODE = 1;
	public static final int SELECT_EVENT_LOCATION_REQUESTCODE = 2;
	public static final int GETIMAGE = 3;
	public static final int CAPUTRE = 4;
	public static final int PLAY_ANIM = 5;
	public static final int EVENT_DETAIL_REQUESTCODE = 6;
	public static final int DISSCUSS_REQUESTCODE = 7;
	public static final int EVENT_INFO_REQUESTCODE = 8;
	public static final int UPLOAD_PIC_REQUESTCODE = 9;
	// 
	public static final int MESSAGE_DATA_OK = 10;
	public static final int MESSAGE_DATA_ERROR = 11;
	
	public static final String FROM_PUSH = "from_push";
	
	
	public static final String EVENT_INVITE = "event_invite";
	public static final String FRIENDS_INVITE = "friends_invite";
	/**
	 * SharedPreferences name
	 */
	// base SharedPreference name if no others, use this
	public static final String BASE_PREFS_NAME = "base_prefs";
	public static final String TEMP_DATA = "temp_data";
	
	/**
	 * SharedPreferences item
	 */
	// first login
	public static final String FIRST_LOGIN = "first_login";
	// first loading friends data
	public static final String FIRST_LOADING_FRIENDS = "first_loading_friends";
	// local friends changed
	public static final String LOCAL_FRIENDS_CHANGED = "local_friends_changed";
	// userid
	public static final String SELF_USER_ID = "self_user_id";
	
	public static final String GROUP_COMMENT_EVENT_ID = "group_comment_event_id";
	// access Token
	public static final String SELF_ACCESS_TOKEN = "self_access_token";
	// first load my event 
	public static final String FIRST_LOAD_MY_EVENT = "first_load_my_event";
	// above sharedPreferences item
	public static final String SER_KEY = "com.medialab.lejuju.main.userinfo.ser";
	// 
	public static final String FRIENDS_KEY = "com.medialab.lejuju.main.friends.fri";
	//
	public static final String EVENT_DETAIL_KEY = "com.medialab.lejuju.main.eventdetail.ser";
	//
	public static final String PHOTO_WALL_PIC_KEY = "com.medialab.lejuju.main.photowall.key";
	// Latitude
	public static final String LOCATION_LATITUDE = "location_latitude";
	// Longitude
	public static final String LOCATION_LONGITUDE = "location_longitude";
	//user id of baidu push
	public static final String BAIDU_USER_ID = "baidu_uid";
	//channel id of baidu push 
	public static final String BAIDU_CHANNEL_ID = "baidu_channelID";
	//renren info json
	public static final String RENREN_INFO = "renren_info";
	
	// new friend num 
	public static final String NEW_FRIENDS_NUM = "new_friends_num";
	// unread news num
	public static final String UNREAD_NEWS_NUM = "unread_news_num";
	
	// local attend event version
	public static final String LOCAL_ATTEND_EVENT_VERSION = "local_attend_event_version";
	
	//统计接口
	public static final String STATIC_URL = SERVER_HOST + "juju/user/update_push_token";
	// register url
	public static final String LREGISTER_URL = SERVER_HOST + "juju/user/register";
	// login url
	public static final String LLOGIN_URL = SERVER_HOST + "juju/user/login";
	// open contacts 
	public static final String LOPENCONTACTS_URL = SERVER_HOST + "juju/mobilelist/match";
	// user info url
	public static final String USER_INFO_GET_URL = SERVER_HOST + "juju/user/main";
	// edit event url
	public static final String EDIT_EVENT_URL = SERVER_HOST + "juju/event/hold";
	// friends events or nearby events  
	public static final String FRIEND_NEARBY_EVENT_URL = SERVER_HOST + "juju/event/trends";
	// get friends list
	public static final String GET_FRIENDS_LIST_URL = SERVER_HOST + "juju/friends/list";
	// invite friends to join the event
	public static final String INVITE_INTERNAL_FRIENDS_URL = SERVER_HOST + "juju/event/invite";
	// i attend event
	public static final String MY_EVENT_ATTEND_EVENT_URL = SERVER_HOST + "juju/event/my_event_trends";
	// i interest event
	public static final String MY_EVENT_INTEREST_EVENT_URL = SERVER_HOST + "juju/event/my_interest_event";
	// i past event
	public static final String MY_EVENT_PAST_EVENT_URL = SERVER_HOST + "juju/event/foretime_event";
	//renren login upload info
	public static final String RENREN_LOGIN_URL = SERVER_HOST + "juju/user/renren_login";
	//mobile verify
	public static final String MOBILE_VERIFY = SERVER_HOST + "juju/user/verify_mobile";
	// register mobile verify 
	public static final String REGISTER_MOBILE_VERIFY = SERVER_HOST + "juju/user/register_verify_mobile";
	// event detail photo
	public static final String EVENT_DETAIL_PHOTO_URL = SERVER_HOST + "juju/event/photo";
	// event detail attend user 
	public static final String EVENT_DETAIL_USER_URL = SERVER_HOST + "juju/event/participant";
	
	// interest one event
	public static final String EVENT_DETAIL_INTEREST_URL = SERVER_HOST + "juju/event/interest_event";
	
	// join one event 
	public static final String EVENT_DETAIL_JOIN_EVENT_URL = SERVER_HOST + "juju/event/join_quit";
	//search certain friends
	public static final String SEARCH_FRIENDS_URL = SERVER_HOST + "juju/user/search";
	//add friends
	public static final String ADD_FRIENDS_URL = SERVER_HOST + "juju/friends/add";
	
	//audit apply
	public static final String AUDIT_APPLY_URL = SERVER_HOST + "juju/friends/audit";
	
	// follow friends 
	public static final String FOLLOW_URL = SERVER_HOST + "juju/friends/follow";
	
	// get vote info 
	public static final String GET_VOTE_INFO_URL = SERVER_HOST + "juju/vote/getvoteinfo";
	
	// send comment message
	public static final String EVENT_ALBUMS_COMMENT_URL = SERVER_HOST + "juju/event/comment";
	// get comment list message
	public static final String EVENT_GET_ALBUMS_COMMENT_LIST = SERVER_HOST + "juju/event/comment_list";
	
	// send discuss message
	public static final String EVENT_COMMENT_SEND_URL = SERVER_HOST + "juju/discuss/send";
	
	// zan
	public static final String ZAN_EVENT_URL = SERVER_HOST + "juju/event/operate"; 
	
	// vote 
	public static final String VOTE_URL = SERVER_HOST + "juju/vote/operation";
	
	//new friends
	public static final String NEW_FRIENDS_URL = SERVER_HOST + "juju/friends/new_friends";
	
	// 
	public static final String MSG_CENTER_URL = SERVER_HOST + "juju/user/news";
	
	//event detail content
	public static final String EVENT_DETAIL_INFO_URL = SERVER_HOST + "juju/event/info";
	
	// event detail send record
	public static final String EVENT_DETAIL_SEND_RECORD_URL = SERVER_HOST + "juju/event/send_record";
	
	// get check friends url
	public static final String GET_CHECK_FRIENDS_URL = SERVER_HOST + "juju/event/get_apply";
	
	// audit friends
	public static final String AUDIT_FRIENDS_URL = SERVER_HOST + "juju/event/audit_apply";
	
	// delete friends
	public static final String DELETE_FRIENDS_URL = SERVER_HOST + "juju/friends/delete";
	
	// logout
	public static final String LOGOUT_URL = SERVER_HOST + "juju/user/login_out";
	
	// set mobile password 
	public static final String SETTING_PASSWORD_URL = SERVER_HOST + "juju/user/setting_password";
	
	// event detail photo 2.0
	public static final String EVENT_DETAIL_PHOTO_URL_V2 = SERVER_HOST + "juju/event/piclist";
	
	// 判断手机号是否被绑定
	public static final String JUDGE_MOBILE_URL = SERVER_HOST + "juju/user/judge_mobile";
	// 登录输入邀请码
	public static final String SUBMIT_INVITE_CODE = SERVER_HOST + "juju/user/submit_invite_code";
	// 找回密码
	public static final String FIND_PASSWORD = SERVER_HOST + "juju/user/find_password";
	// 人人登录绑定手机号
	public static final String RENREN_VERIFY_MOBILE_URL = SERVER_HOST + "juju/user/renren_verify_mobile";
	
	//创建活动记录之后才能上传照片
	public static final String CREATE_RECORD_URL = SERVER_HOST + "juju/event/create_record";
	//上传活动照片
	public static final String UPLOAD_PHOTO_RECORD = SERVER_HOST + "juju/event/send_pic";
	// 重新获取邀请码
	public static final String REFRESH_INVITE_CODE_URL = SERVER_HOST + "juju/event/refresh_invite_code";
	// 举报活动
	public static final String REPORT_EVENT_URL = SERVER_HOST + "juju/event/report";
	// 修改活动信息
	public static final String SETTING_INFO_URL = SERVER_HOST + "juju/event/setting_info";
	
	// 设置活动封面
	public static final String SETTING_PIC_URL = SERVER_HOST + "juju/event/setting_pic";
	
	// 删除照片
	public static final String DELETE_PIC_URL = SERVER_HOST + "juju/pic/operate";
	
	// 意见反馈
	public static final String OPINION_URL = SERVER_HOST + "juju/user/opinion";
	
	// static 
	public static final String STATIC_URL_STRING = SERVER_HOST + "juju/static";
	
	// 获取未读的好友数及消息数
	public static final String UNREAD_UPDATE_URL = SERVER_HOST + "juju/user/unread_update";
	
	public static final String UPDATE_HEAD_PIC_URL = SERVER_HOST + "juju/setting/head_pic";

	
}
