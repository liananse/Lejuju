
package com.medialab.lejuju.push;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.lejuju.R;
import com.medialab.lejuju.db.DDBOpenHelper;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.http.HHttpDataLoader.HDataListener;
import com.medialab.lejuju.main.comment.CEventAlbumsCommentActivity;
import com.medialab.lejuju.main.comment.CEventDiscussActivity;
import com.medialab.lejuju.main.eventdetail.EDInviteFriendsActivity;
import com.medialab.lejuju.main.eventdetail.EventDetailActivity;
import com.medialab.lejuju.main.friends.FHaveNewFriendsActivity;
import com.medialab.lejuju.main.userinfo.UserInfoEntryActivity;
import com.medialab.lejuju.main.userinfo.model.UserInfoReloadModel;
import com.medialab.lejuju.model.EventItemModel;
import com.medialab.lejuju.model.FriendsModel;
import com.medialab.lejuju.model.TrendItemModel;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UDisplayWidth;
import com.medialab.lejuju.util.UTimeShown;
import com.medialab.lejuju.util.UTools;
import com.medialab.lejuju.util.UUtils;

public class AndroidNotify implements Notify
{
	private Context context;
	
	private Map<String,String> separateUrl(String url)
	{
		Map<String,String> map = null;
		if(url!=null && !url.equals(""))
		{
			map = new HashMap<String,String>();
			String[] all = url.split("\\?");
			map.put("url", all[0]);
			String[] params = all[1].split("\\&");
			for(int i=0;i<params.length;i++)
			{
				String[] pa = params[i].split("=");
				map.put(pa[0], pa[1]);
			}
		}
		return map;
	}

	/**
	 * 消息跳转改过很多次了
	 * 
	 * @param mes
	 * @return
	 */
	Intent getIntent(Message mes)
	{
		if (mes != null)
		{
			Intent intent = new Intent();
			MessageType type = mes.getType();
			intent.putExtra("id", mes.getId());
			intent.putExtra("type", type);
			intent.putExtra("head", mes.getHead());
			Map<String,String> map = null;
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
			if (type != null && mes.getUrl() != null)
			{
				switch (type)
				{
				    // XX 邀请参加活动 done
					case INVITE_JOIN_EVENT:
						map = separateUrl(mes.getUrl());
						Bundle bundle_INVITE_JOIN_EVENT = new Bundle();
						bundle_INVITE_JOIN_EVENT.putString("event_id", map.get("event_id"));
						bundle_INVITE_JOIN_EVENT.putBoolean(UConstants.FROM_PUSH, true);
						
						intent.putExtras(bundle_INVITE_JOIN_EVENT);
						intent.setClass(context, EventDetailActivity.class);
						break;
					// 讨论信息 done
					case DISCUSS_GROUP_TRENDS:
						map = separateUrl(mes.getUrl());
						
						Bundle bundle_DISCUSS_GROUP_TRENDS = new Bundle();
						bundle_DISCUSS_GROUP_TRENDS.putString("event_id", map.get("event_id"));
						bundle_DISCUSS_GROUP_TRENDS.putBoolean(UConstants.FROM_PUSH, true);
						
						intent.putExtras(bundle_DISCUSS_GROUP_TRENDS);
						intent.setClass(context, CEventDiscussActivity.class);
						
						String event_id = UTools.Storage.getSharedPreferences(context, UConstants.BASE_PREFS_NAME).getString(UConstants.GROUP_COMMENT_EVENT_ID, "0");
						if (event_id.equals(map.get("event_id")) || event_id.equals("0"))
						{
							getTrendsInfo(context,true);
						}
						else {
							getTrendsInfo(context, false);
						}
						break;
					// XX 发起了公共活动 done
					case FRIEND_HOLD_PUBLIC_EVENT:
						map = separateUrl(mes.getUrl());
						Bundle bundle_FRIEND_HOLD_PUBLIC_EVENT = new Bundle();
						bundle_FRIEND_HOLD_PUBLIC_EVENT.putString("event_id", map.get("event_id"));
						bundle_FRIEND_HOLD_PUBLIC_EVENT.putBoolean(UConstants.FROM_PUSH, true);
						
						intent.putExtras(bundle_FRIEND_HOLD_PUBLIC_EVENT);
						intent.setClass(context, EventDetailActivity.class);
						break;
					case EVENT_COMMENT:
						map = separateUrl(mes.getUrl());
						intent.putExtra("url", mes.getUrl());
						intent.putExtra("event_id", map.get("event_id"));
						intent.setClass(context, CEventAlbumsCommentActivity.class);
						break;
					case P2P_COMMENT:
						map = separateUrl(mes.getUrl());
						intent.putExtra("url", mes.getUrl());
						intent.putExtra("event_id", map.get("event_id"));
						intent.setClass(context, CEventAlbumsCommentActivity.class);
						break;
					// XX 申请加你为好友 done
					case ADD_FRIEND_APPLY:
						map = separateUrl(mes.getUrl());
						
						Bundle bundle_ADD_FRIEND_APPLY = new Bundle();
						bundle_ADD_FRIEND_APPLY.putString("user_id", map.get("user_id"));
						bundle_ADD_FRIEND_APPLY.putBoolean(UConstants.FROM_PUSH, true);
						
						intent.putExtras(bundle_ADD_FRIEND_APPLY);
						intent.setClass(context, UserInfoEntryActivity.class);
						
						break;
					// 你和XX都愿意加彼此位好友 done
					case ALLOW_ADD_FRIEND_SUCCESS:
						map = separateUrl(mes.getUrl());
						Bundle bundle_ALLOW_ADD_FRIEND_SUCCESS = new Bundle();
						bundle_ALLOW_ADD_FRIEND_SUCCESS.putString("user_id", map.get("user_id"));
						bundle_ALLOW_ADD_FRIEND_SUCCESS.putBoolean(UConstants.FROM_PUSH, true);
						
						intent.putExtras(bundle_ALLOW_ADD_FRIEND_SUCCESS);
						loadUserInfo(map.get("user_id"),context);
						intent.setClass(context, UserInfoEntryActivity.class);
						break;
					// XX通过了你参加活动的申请 done
					case JOIN_EVENT_AUDIT_PASS:
						map = separateUrl(mes.getUrl());
						Bundle bundle_JOIN_EVENT_AUDIT_PASS = new Bundle();
						bundle_JOIN_EVENT_AUDIT_PASS.putString("event_id", map.get("event_id"));
						bundle_JOIN_EVENT_AUDIT_PASS.putBoolean(UConstants.FROM_PUSH, true);
						
						intent.putExtras(bundle_JOIN_EVENT_AUDIT_PASS);
						intent.setClass(context, EventDetailActivity.class);
						break;
				    // XX 通过了你的好友申请 done
					case ADD_FRIEND_AUDIT_PASS:
						map = separateUrl(mes.getUrl());
						
						Bundle bundle_ADD_FRIEND_AUDIT_PASS = new Bundle();
						bundle_ADD_FRIEND_AUDIT_PASS.putString("user_id", map.get("user_id"));
						bundle_ADD_FRIEND_AUDIT_PASS.putBoolean(UConstants.FROM_PUSH, true);
						
						intent.putExtras(bundle_ADD_FRIEND_AUDIT_PASS);
						loadUserInfo(map.get("user_id"),context);
						intent.setClass(context, UserInfoEntryActivity.class);
						break;
					// 好友申请拒绝 done
					case ADD_FRIEND_AUDIT_REFUSE:
						map = separateUrl(mes.getUrl());
						Bundle bundle_ADD_FRIEND_AUDIT_REFUSE = new Bundle();
						bundle_ADD_FRIEND_AUDIT_REFUSE.putString("user_id", map.get("user_id"));
						bundle_ADD_FRIEND_AUDIT_REFUSE.putBoolean(UConstants.FROM_PUSH, true);
						
						intent.putExtras(bundle_ADD_FRIEND_AUDIT_REFUSE);
						intent.setClass(context, UserInfoEntryActivity.class);
						break;
					// XX 报名参加了活动
					case APPLY_JOIN_EVENT:// done
						map = separateUrl(mes.getUrl());
						Bundle bundle_APPLY_JOIN_EVENT = new Bundle();
						bundle_APPLY_JOIN_EVENT.putString("event_id", map.get("event_id"));
						bundle_APPLY_JOIN_EVENT.putBoolean(UConstants.FROM_PUSH, true);
						
						intent.putExtras(bundle_APPLY_JOIN_EVENT);
						intent.setClass(context, EDInviteFriendsActivity.class);
						break;
					// XX 加入了活动 done
					case JOIN_EVENT:
						map = separateUrl(mes.getUrl());
						
						Bundle bundle_JOIN_EVENT = new Bundle();
						bundle_JOIN_EVENT.putString("event_id", map.get("event_id"));
						bundle_JOIN_EVENT.putBoolean(UConstants.FROM_PUSH, true);
						
						intent.putExtras(bundle_JOIN_EVENT);
						intent.setClass(context, EventDetailActivity.class);
						
						String event_id2 = UTools.Storage.getSharedPreferences(context, UConstants.BASE_PREFS_NAME).getString(UConstants.GROUP_COMMENT_EVENT_ID, "0");
						if (event_id2.equals(map.get("event_id")) || event_id2.equals("0"))
						{
							getTrendsInfo(context,true);
						}
						else {
							getTrendsInfo(context, false);
						}
						break;
					//  修改活动信息 done
					case CHANGE_EVENT_INFO:
						map = separateUrl(mes.getUrl());
						Bundle bundle_CHANGE_EVENT_INFO = new Bundle();
						bundle_CHANGE_EVENT_INFO.putString("event_id", map.get("event_id"));
						bundle_CHANGE_EVENT_INFO.putBoolean(UConstants.FROM_PUSH, true);
						
						intent.putExtras(bundle_CHANGE_EVENT_INFO);
						intent.setClass(context, EventDetailActivity.class);
						break;
					// 发布活动记录 done
					case SEND_EVENT_RECORD:
						map = separateUrl(mes.getUrl());
						Bundle bundle_SEND_EVENT_RECORD = new Bundle();
						bundle_SEND_EVENT_RECORD.putString("event_id", map.get("event_id"));
						bundle_SEND_EVENT_RECORD.putBoolean(UConstants.FROM_PUSH, true);
						
						intent.putExtras(bundle_SEND_EVENT_RECORD);
						intent.setClass(context, EventDetailActivity.class);
						break;
					// 退出活动推送 done
					case PUSH_TYPE_QUIT_EVENT:
						map = separateUrl(mes.getUrl());
						Bundle bundle_PUSH_TYPE_QUIT_EVENT = new Bundle();
						bundle_PUSH_TYPE_QUIT_EVENT.putString("event_id", map.get("event_id"));
						bundle_PUSH_TYPE_QUIT_EVENT.putBoolean(UConstants.FROM_PUSH, true);
						
						intent.putExtras(bundle_PUSH_TYPE_QUIT_EVENT);
						intent.setClass(context, EventDetailActivity.class);
						break;
					// 新朋友推送
					case PUSH_TYPE_NEW_FRIENDS:
						intent.setClass(context,FHaveNewFriendsActivity.class);
						break;
					default:
						break;
				}
			}
			return intent;
		}
		else
		{
			return null;
		}
	}


	Notification getNotification(Message mes)
	{
		if (mes != null)
		{
			Notification messageNotification = new Notification();
			messageNotification.icon = R.drawable.ic_launcher;

			messageNotification.tickerText = mes.getHead();
			messageNotification.defaults = Notification.DEFAULT_SOUND;
			return messageNotification;
		}
		else
		{
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.huotu.textgram.message.Notify#notify(android.content.Context)
	 */
	@Override
	public void notify(Context context, Message mes)
	{
		this.context = context;
		
		Notification notification = getNotification(mes);
		
		Intent intent = getIntent(mes);
		PendingIntent messagePendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		notification.setLatestEventInfo(context, mes.getHead(), mes.getContent(), messagePendingIntent);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		
		MessageType type = mes.getType();
		if (type != MessageType.PUSH_TYPE_NEW_FRIENDS)
		{
			String event_id = UTools.Storage.getSharedPreferences(context, UConstants.BASE_PREFS_NAME).getString(UConstants.GROUP_COMMENT_EVENT_ID, "0");
			if (!event_id.equals(separateUrl(mes.getUrl()).get("event_id")))
			{
				((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(mes.getType().getCode(), notification);
			}
		}
		else {
			((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(mes.getType().getCode(), notification);
		}
	}
	
	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	
	
	///////////////////////////////////////////////////////////////////
	
	// 获取个人信息
	///////////////////////////////////////////////////////////////////
	private void loadUserInfo(String user_id, Context context)
	{
		final Context tempContext = context;
		if (!user_id.equals("-1") && !user_id.isEmpty())
		{
			Map<String, String> params = new HashMap<String, String>();
			
			params.put("user_id", user_id);
			params.put("head_pic", String.valueOf(UDisplayWidth.getLargeHeadPicWidth(context)));
			params.put("width", String.valueOf(UDisplayWidth.getEventDetailPicWidth(context)));
			mDataLoader.getData(UConstants.USER_INFO_GET_URL, params, context, new HDataListener() {
				
				@Override
				public void onFinish(String source) {
					// TODO Auto-generated method stub
					
					Gson gson = new Gson();
					
					try {
						TempUserInfoModel mTempModel = gson.fromJson(source, new TypeToken<TempUserInfoModel>(){}.getType());
						
						if(mTempModel != null && mTempModel.result.equals("success"))
						{
							FriendsModel mFriendsModel = UUtils.initMFriendsModel(mTempModel.data);
							
							List<FriendsModel> tempList = new ArrayList<FriendsModel>();
							tempList.add(mFriendsModel);
							if (mFriendsModel.relation == 1)
							{
								if (UUtils.getFriendsModelByUserId(tempContext, String.valueOf(mFriendsModel.user_id)) == null)
								{
									DDBOpenHelper mDdbOpenHelper = DDBOpenHelper.getInstance(tempContext);
									mDdbOpenHelper.insertFriendsInfoModel(tempList);
								}
							}
						}
					} catch (JsonSyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				@Override
				public void onFail(String msg) {
					// TODO Auto-generated method stub
					
				}
			});
		}
	}
	
	class TempUserInfoModel
	{
		public String result = "";
		public String message = "";
		public UserInfoReloadModel data = null;
	}
	///////////////////////////////////////////////////////////////////////
	// 获取推送信息
	//////////////////////////////////////////////////////////////////////
	
	private void getTrendsInfo(Context context, boolean isSend)
	{
		final boolean isSendB;
		final Context tempContext;
		tempContext = context;
		isSendB = isSend;
		int version = UTools.Storage.getSharedPreferences(context, UConstants.BASE_PREFS_NAME).getInt(UConstants.LOCAL_ATTEND_EVENT_VERSION, 0);
		
		Map<String, String> params = new HashMap<String, String>();
		
		params.put("head_width", String.valueOf(UDisplayWidth.getSmallHeadPicWidth(context)));
		params.put("event_pic_width", String.valueOf(UDisplayWidth.getPosterPicWidth(context)));
		params.put("version", String.valueOf(version));
		
		mDataLoader.getData(UConstants.MY_EVENT_ATTEND_EVENT_URL, params, context, new HDataListener() {
			
			@Override
			public void onFinish(String source) {
				// TODO Auto-generated method stub
				Gson gson = new Gson();
				try {
					TempModel mTempModel = gson.fromJson(source, new TypeToken<TempModel>(){}.getType());
					
					if(mTempModel != null && mTempModel.result.equals("success"))
					{
						
						//////////////////////////////////////////////////
						
						DDBOpenHelper mDdbOpenHelper = DDBOpenHelper.getInstance(tempContext);
						List<EventItemModel> list = mTempModel.data;
						
						if (list != null && list.size() > 0)
						{
							// 将动态统一放入动态list插入数据库
							// step1 声明需要插入数据库的
							List<TrendItemModel> mTrendItemModels = new ArrayList<TrendItemModel>();
							for (int i = 0; i < list.size(); i++) {
								if (list.get(i).trends != null && list.get(i).trends.size() > 0)
								{
									mTrendItemModels.addAll(list.get(i).trends);
								}
							}
							
							if (mTrendItemModels != null && mTrendItemModels.size() > 0)
							{
								for (int i = 0; i < mTrendItemModels.size(); i++) {
									
									TrendItemModel mTopTrendItemModel = mDdbOpenHelper.getTrendsMaxTimeByEventID(mTrendItemModels.get(i).event_id);
									
									if (mTopTrendItemModel != null)
									{
										if (UTimeShown.isLargeThanThressMinute(mTrendItemModels.get(i).add_time, mTopTrendItemModel.add_time))
										{
											mTrendItemModels.get(i).show_time = 1;
										}
										else 
										{
											mTrendItemModels.get(i).show_time = 0;
										}
									}
									
									mDdbOpenHelper.insertTrendsModel(mTrendItemModels.get(i));
								}
							}
							
							// 将活动插入数据库
							// step1 从数据库中获取本地存储的活动
							List<EventItemModel> listFromDB = mDdbOpenHelper.getAttendEventModels();
							// step2声明共同活动的list
							List<EventItemModel> comList = new ArrayList<EventItemModel>();
							// setp3找出共同的活动
							for (int j = 0; j < list.size(); j++) {
								
								for (int i = 0; i < listFromDB.size(); i++) {
									
									if (list.get(j).event_id == listFromDB.get(i).event_id)
									{
										list.get(j).event_trends_num = list.get(j).event_trends_num + listFromDB.get(i).event_trends_num;
										list.get(j).un_read_pic_num = (list.get(j).pic_num - listFromDB.get(i).pic_num) + listFromDB.get(i).un_read_pic_num;
										comList.add(list.get(j));
										break;
									}
								}
							}
							// step4将共同的活动从list中剔除
							list.removeAll(comList);
							mDdbOpenHelper.updateAttendEventModels(comList);
							// step5将本地数据库没有的list插入数据库
							mDdbOpenHelper.insertAttendEventModel(list);
							
							if (isSendB)
								tempContext.sendBroadcast(new Intent("com.medialab.lejuju.newtrends"));
							
						}
						
						SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(tempContext, UConstants.BASE_PREFS_NAME);
						mEditor.putInt(UConstants.LOCAL_ATTEND_EVENT_VERSION, mTempModel.version);
						mEditor.commit();
						
					}
				} catch (JsonSyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFail(String msg) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	class TempModel
	{
		public String result = "";
		public String message = "";
		public int version = 0;
		public List<EventItemModel> data = null;
	}
	
}
