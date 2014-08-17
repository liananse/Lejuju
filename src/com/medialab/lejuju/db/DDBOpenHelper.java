package com.medialab.lejuju.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.medialab.lejuju.main.photowall.model.PhotoUploadModel;
import com.medialab.lejuju.model.EventItemModel;
import com.medialab.lejuju.model.FriendsModel;
import com.medialab.lejuju.model.SelfUserInfoModel;
import com.medialab.lejuju.model.TrendItemModel;

public class DDBOpenHelper extends SQLiteOpenHelper
{
	public static final String DATABASE_NAME = "Lejuju.db";
	public static final int DATABASE_VERSION = 1;
	public static final String USER_TABLE_NAME = "user_info";
	public static final String FRIENDS_TABLE_NAME = "friends_info";
	public static final String MYEVENT_TABLE_NAME = "my_event_info";
	public static final String TRENDS_TABLE_NAME = "trends_info";
	public static final String UPLOAD_PIC = "upload_pic";
	
	public DDBOpenHelper(Context context) 
	{
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	private static DDBOpenHelper instance;

	public final static DDBOpenHelper getInstance(Context context)
	{
		return (instance == null) ? new DDBOpenHelper(context) : instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		// TODO Auto-generated method stub
		
		db.execSQL(SQL_CREATE_USER_ENTRIES);
		db.execSQL(SQL_CREATE_FRIENDS_ENTRIES);
		db.execSQL(SQL_CREATE_MYEVENT_TREND_ENTRIES);
		db.execSQL(SQL_CREATE_TRENDS_ENTRIES);
		db.execSQL(SQL_CREATE_UPLOAD_PIC_TABLE);
	}
	
	// self user info model
	/////////////////////////////
	// 创建用户数据表
	private static final String SQL_CREATE_USER_ENTRIES =
		    "CREATE TABLE IF NOT EXISTS " + USER_TABLE_NAME + " (" +
		    "Id" + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		    "access_token" + " TEXT," +
		    "user_id" + " INTEGER," +
		    "nick_name" + " TEXT," + 
		    "sex" + " integer," + 
		    "mobile" + " TEXT," + 
		    "area" + " TEXT," + 
		    "account" + " TEXT," +
		    "country" + " TEXT," + 
		    "school" + " TEXT," + 
		    "company" + " TEXT," + 
		    "introduce" + " TEXT," +
		    "head_pic" + " TEXT," +
		    "background" + " TEXT," +
		    "identification_state" + " INTEGER," +
		    "relation" + " INTEGER," +
		    "sms_content" + " TEXT," +
		    "url" + " TEXT," +
		    "department" + " TEXT," +
		    "enter_school_year" + " TEXT," +
		    "wx_content" + " TEXT" +
		    " )";
	
	/**
	 * 将个人信息存入数据库
	 * @author liananse
	 * @param mUserInfoModel
	 * 2013-8-30
	 */
	public void insertSelfUserInfoModel(SelfUserInfoModel mUserInfoModel)
	{
		// Gets the data repository in write mode
		SQLiteDatabase db = getWritableDatabase();

		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		
		values.put("access_token", mUserInfoModel.access_token);
		values.put("user_id", mUserInfoModel.user_id);
		values.put("nick_name", mUserInfoModel.nick_name);
		values.put("sex", mUserInfoModel.sex);
		values.put("mobile", mUserInfoModel.mobile);
		values.put("area", mUserInfoModel.area);
		values.put("account", mUserInfoModel.account);
		values.put("country", mUserInfoModel.country);
		values.put("school", mUserInfoModel.school);
		values.put("company", mUserInfoModel.company);
		values.put("introduce", mUserInfoModel.introduce);
		values.put("head_pic", mUserInfoModel.head_pic);
		values.put("background", mUserInfoModel.background);
		values.put("identification_state", mUserInfoModel.identification_state);
		values.put("relation", mUserInfoModel.relation);
		values.put("sms_content", mUserInfoModel.sms_content);
		values.put("url", mUserInfoModel.url);
		values.put("department", mUserInfoModel.department);
		values.put("enter_school_year", mUserInfoModel.enter_school_year);
		values.put("wx_content", mUserInfoModel.wx_content);
		// Insert the new row, returning the primary key value of the new row
		long newRowId;
		newRowId = db.insert(
		         USER_TABLE_NAME,
		         null,
		         values);
		db.close();
	}
	
	/**
	 * 设置手机
	 * @author liananse
	 * @param event_id
	 * 2013-9-2
	 */
	public void updateSelfUserInfoModelMobile(String mobile)
	{
		
		// Gets the data repository in write mode
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		
		
		ContentValues values = new ContentValues();
		
		values.put("mobile", mobile);
			
		db.update(USER_TABLE_NAME, values, null, null);

		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}
	
	/**
	 * 设置头像
	 * @author liananse
	 * @param event_id
	 * 2013-9-2
	 */
	public void updateSelfUserInfoHeadModelMobile(String url)
	{
		
		// Gets the data repository in write mode
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		
		
		ContentValues values = new ContentValues();
		
		values.put("head_pic", url);
			
		db.update(USER_TABLE_NAME, values, null, null);

		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}
	
	/**
	 * @author liananse
	 * @return null if no user info
	 * 2013-7-7
	 */
	public SelfUserInfoModel selectSelfUserInfoModelFromDB()
	{
		
		SelfUserInfoModel mUserInfoModel = null;
		SQLiteDatabase db = getReadableDatabase();

		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = {
				"access_token",
			    "user_id",
			    "nick_name",
			    "sex",
			    "mobile",
			    "area",
			    "account",
			    "country",
			    "school",
			    "company",
			    "introduce",
			    "head_pic",
			    "background",
			    "identification_state",
			    "relation",
			    "sms_content",
			    "url",
			    "department",
			    "enter_school_year",
			    "wx_content"
		    };


		Cursor c = db.query(
		    USER_TABLE_NAME,  // The table to query
		    projection,                               // The columns to return
		    null,                                     // The columns for the WHERE clause
		    null,                                     // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                      // The sort order
		    );
		
		if (c != null && c.getCount() > 0)
		{
			c.moveToFirst();

			mUserInfoModel = new SelfUserInfoModel();
			
			mUserInfoModel.access_token = c.getString(0);
			mUserInfoModel.user_id = c.getInt(1);
			mUserInfoModel.nick_name = c.getString(2);
			mUserInfoModel.sex = c.getInt(3);
			mUserInfoModel.mobile = c.getString(4);
			mUserInfoModel.area = c.getString(5);
			mUserInfoModel.account = c.getString(6);
			mUserInfoModel.country = c.getString(7);
			mUserInfoModel.school = c.getString(8);
			mUserInfoModel.company = c.getString(9);
			mUserInfoModel.introduce = c.getString(10);
			mUserInfoModel.head_pic = c.getString(11);
			mUserInfoModel.background = c.getString(12);
			mUserInfoModel.identification_state = c.getInt(13);
			mUserInfoModel.relation = c.getInt(14);
			mUserInfoModel.sms_content = c.getString(15);
			mUserInfoModel.url = c.getString(16);
			mUserInfoModel.department = c.getString(17);
			mUserInfoModel.enter_school_year = c.getString(18);
			mUserInfoModel.wx_content = c.getString(19);
		}
		c.close();
		db.close();
		return mUserInfoModel;
	}
	
	
	
	/////////////////////////////
	
	// friends model
	/////////////////////////////
	// 创建好友数据表
	private static final String SQL_CREATE_FRIENDS_ENTRIES = 
			"CREATE TABLE IF NOT EXISTS " + FRIENDS_TABLE_NAME + " (" +
		    "Id" + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		    "sex" + " INTEGER," + 
		    "head_pic" + " TEXT," +
		    "relation" + " INTEGER," + 
		    "introduce" + " TEXT," + 
		    "identification_state" + " INTEGER," +
		    "country" + " TEXT," + 
		    "nick_name" + " TEXT," + 
		    "area" + " TEXT," + 
		    "school" + " TEXT," + 
		    "background" + " TEXT," + 
		    "company" + " TEXT," + 
		    "account" + " TEXT," +
		    "user_id" + " INTEGER," +
		    "allow_add_me" + " INTEGER," + 
		    "mobile" + " TEXT," + 
		    "namePinYin" + " TEXT," +
		    "namePinYinFirst" + " TEXT," +
		    "department" + " TEXT," + 
		    "enter_school_year" + " TEXT" +
		    " )";
	
	/**
	 * 将好友信息插入数据库
	 * @author liananse
	 * @param mFriendsModels
	 * 2013-8-30
	 */
	public void insertFriendsInfoModel(List<FriendsModel> mFriendsModels)
	{
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		
		//
		for (int i = 0; i < mFriendsModels.size(); i++) {
			
			FriendsModel tModel = mFriendsModels.get(i);
			ContentValues values = new ContentValues();
			values.put("sex", tModel.sex);
			values.put("head_pic", tModel.head_pic);
			values.put("relation", tModel.relation);
			values.put("introduce", tModel.introduce);
			values.put("identification_state", tModel.identification_state);
			values.put("country", tModel.country);
			values.put("nick_name", tModel.nick_name);
			values.put("area", tModel.area);
			values.put("school", tModel.school);
			values.put("background", tModel.background);
			values.put("company", tModel.company);
			values.put("account", tModel.account);
			values.put("user_id", tModel.user_id);
			if (tModel.allow_add_me)
			{
				values.put("allow_add_me", 1);
			}
			else {
				values.put("allow_add_me", 0);
			}
			values.put("mobile", tModel.mobile);
			values.put("namePinYin", tModel.namePinYin);
			values.put("namePinYinFirst", tModel.namePinYinFirst);
			values.put("department", tModel.department);
			values.put("enter_school_year", tModel.enter_school_year);
			// Insert the new row, returning the primary key value of the new row
			long newRowId;
			newRowId = db.insert(
			         FRIENDS_TABLE_NAME,
			         null,
			         values);
		}
		
		//
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}
	
	/**
	 * @author liananse
	 * @return null if no user info
	 * 2013-7-7
	 */
	public FriendsModel selectFriendsModelFromDBByID(String user_id)
	{
		
		FriendsModel mFriendsModel = null;
		SQLiteDatabase db = getReadableDatabase();

		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = {
				"sex",
				"head_pic",
				"relation",
				"introduce",
				"identification_state",
				"country",
				"nick_name",
				"area",
				"school",
				"background",
				"company",
				"account",
			    "user_id",
			    "allow_add_me",
			    "mobile",
			    "namePinYin",
			    "namePinYinFirst",
			    "department",
			    "enter_school_year"
		    };


		String whereClause = "";
		
		whereClause = "user_id=?";
		String[] valuesClause = {
				user_id
		};
		
		Cursor c = db.query(
			FRIENDS_TABLE_NAME,  // The table to query
		    projection,                               // The columns to return
		    whereClause,                                     // The columns for the WHERE clause
		    valuesClause,                                     // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                      // The sort order
		    );
		
		if (c != null && c.getCount() > 0)
		{
			c.moveToFirst();

			mFriendsModel = new FriendsModel();
			mFriendsModel.sex = c.getInt(0);
			mFriendsModel.head_pic = c.getString(1);
			mFriendsModel.relation = c.getInt(2);
			mFriendsModel.introduce = c.getString(3);
			mFriendsModel.identification_state = c.getInt(4);
			mFriendsModel.country = c.getString(5);
			mFriendsModel.nick_name = c.getString(6);
			mFriendsModel.area = c.getString(7);
			mFriendsModel.school = c.getString(8);
			mFriendsModel.background = c.getString(9);
			mFriendsModel.company = c.getString(10);
			mFriendsModel.account = c.getString(11);
			mFriendsModel.user_id = c.getInt(12);
			if (c.getInt(13) == 1)
			{
				mFriendsModel.allow_add_me = true;
			}
			else {
				mFriendsModel.allow_add_me = false;
			}
			mFriendsModel.mobile = c.getString(14);
			mFriendsModel.namePinYin = c.getString(15);
			mFriendsModel.namePinYinFirst = c.getString(16);
			mFriendsModel.department = c.getString(17);
			mFriendsModel.enter_school_year = c.getString(18);
			
		}
		c.close();
		db.close();
		return mFriendsModel;
	}
	
	/**
	 * 从数据库获取好友列表
	 * @author liananse
	 * @return
	 * 2013-8-28
	 */
	public List<FriendsModel> getFriendsModelsFromDB()
	{
		List<FriendsModel> list = new ArrayList<FriendsModel>();
		SQLiteDatabase db = getReadableDatabase();
		
		String[] projection = {
				"sex",
				"head_pic",
				"relation",
				"introduce",
				"identification_state",
				"country",
				"nick_name",
				"area",
				"school",
				"background",
				"company",
				"account",
			    "user_id",
			    "allow_add_me",
			    "mobile",
			    "namePinYin",
			    "namePinYinFirst",
			    "department",
			    "enter_school_year"
		    };

		Cursor c = db.query(
		    FRIENDS_TABLE_NAME,  // The table to query
		    projection,                               // The columns to return
		    null,                                     // The columns for the WHERE clause
		    null,                                     // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                      // The sort order
		    );
		
		if (c != null && c.getCount() > 0)
		{
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			{
				FriendsModel tModel = new FriendsModel();
				tModel.sex = c.getInt(0);
				tModel.head_pic = c.getString(1);
				tModel.relation = c.getInt(2);
				tModel.introduce = c.getString(3);
				tModel.identification_state = c.getInt(4);
				tModel.country = c.getString(5);
				tModel.nick_name = c.getString(6);
				tModel.area = c.getString(7);
				tModel.school = c.getString(8);
				tModel.background = c.getString(9);
				tModel.company = c.getString(10);
				tModel.account = c.getString(11);
				tModel.user_id = c.getInt(12);
				if (c.getInt(13) == 1)
				{
					tModel.allow_add_me = true;
				}
				else {
					tModel.allow_add_me = false;
				}
				tModel.mobile = c.getString(14);
				tModel.namePinYin = c.getString(15);
				tModel.namePinYinFirst = c.getString(16);
				tModel.department = c.getString(17);
				tModel.enter_school_year = c.getString(18);
				list.add(tModel);
			}
			c.close();
		}
		db.close();
		return list;
	}
	
	/**
	 * 将好友从数据库删除
	 * @author liananse
	 * @param userId
	 * 2013-8-30
	 */
	public void deleteFriendsModelFromDB(int userId)
	{
		SQLiteDatabase db = getWritableDatabase();
		String where = "user_id = " + userId;
		String whereArgs[] = null;
		db.delete(FRIENDS_TABLE_NAME, where, whereArgs);
		
		db.close();
	}
	
	/////////////////////////////
	
	
	// 活动动态
	/////////////////////////////
	
	// 创建我的活动动态表
	private static final String SQL_CREATE_MYEVENT_TREND_ENTRIES = 
			"CREATE TABLE IF NOT EXISTS " + MYEVENT_TABLE_NAME + " (" +
		    "Id" + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		    "memo" + " TEXT," +
		    "introduce" + " TEXT," + 
		    "event_id" + " INTEGER," +
		    "spent_type" + " INTEGER," +
		    "forward_invite" + " INTEGER," +
		    "other_spent_type" + " TEXT," +
		    "public_info" + " INTEGER," +
		    "event_type_name" + " TEXT," +
		    "title" + " TEXT," +
		    "interest" + " INTEGER," +
		    "zan_num" + " INTEGER," +
		    "people_num" + " INTEGER," +
		    "spent" + " INTEGER," +
		    "share_content" + " TEXT," +
		    "event_state" + " INTEGER," +
		    "people_join" + " INTEGER," +
		    "event_type_id" + " INTEGER," +
		    "time_state" + " INTEGER," +
		    "join_state" + " INTEGER," +
		    "address_state" + " INTEGER," +
		    "event_pic" + " TEXT," +
		    "event_invite_url" + " TEXT," +
		    "zan" + " INTEGER," +
		    "address" + " TEXT," +
		    "end_time" + " TIMESTAMP," +
		    "start_time" + " TIMESTAMP," +
		    "add_time" + " TIMESTAMP," +
		    "x" + " TEXT," +
		    "y" + " TEXT," +
		    "event_trends_num" + " INTEGER," +
		    
		    // master
		    "sex" + " INTEGER," + 
		    "head_pic" + " TEXT," +
		    "relation" + " INTEGER," +
		    "master_introduce" + " TEXT," +
		    "identification_state" + " INTEGER," +
		    "country" + " TEXT," + 
		    "nick_name" + " TEXT," + 
		    "area" + " TEXT," + 
		    "school" + " TEXT," + 
		    "background" + " TEXT," +
		    "company" + " TEXT," + 
		    "account" + " TEXT," +
		    "user_id" + " INTEGER," +
		    "allow_add_me" + " INTEGER," +
		    "mobile" + " TEXT," +
		    
		    "trends_content" + " TEXT," +
		    "trends_nick_name" + " TEXT," + 
		    "trends_head_pic" + " TEXT," + 
		    "trends_add_time" + " TEXT," +
		    "pic_num" + " INTEGER," + 
		    "un_read_pic_num" + " INTEGER," +
		    "need_audit" + " INTEGER," +
		    "event_share_url" + " TEXT," +
		    "sms_invite_content" + " TEXT," +
		    "wx_invite_content" + " TEXT" +
		    
		    " )";
	
	// 创建trends
	private static final String SQL_CREATE_TRENDS_ENTRIES = 
			"CREATE TABLE IF NOT EXISTS " + TRENDS_TABLE_NAME + " (" +
		    "trends_id" + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		    "user_id" + " INTEGER," +
		    "head_pic" + " TEXT," +
		    "nick_name" + " TEXT," + 
		    "sex" + " INTEGER," + 
		    "mobile" + " TEXT," + 
		    "area" + " TEXT," + 
		    "country" + " TEXT," + 
		    "account" + " TEXT," +
		    "school" + " TEXT," + 
		    "company" + " TEXT," + 
		    "introduce" + " TEXT," + 
		    "relation" + " INTEGER," +
		    "content" + " TEXT," + 
		    "event_id" + " INTEGER," + 
		    "add_time" + " TIMESTAMP," + 
		    "type" + " INTEGER," +
		    "id" + " INTEGER," +
		    "show_time" + " INTEGER" +  
		    " )";
	
	/////////////////////////////
	
	
	
	/**
	 * 插入我的活动动态
	 * @author liananse
	 * @param mAttendEventModels
	 * 2013-8-1
	 */
	public void insertAttendEventModel(List<EventItemModel> mEventItemModels)
	{
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		
		for (int i = 0; i < mEventItemModels.size(); i++) {
			
			EventItemModel mEventItemModel = mEventItemModels.get(i);
			
			ContentValues values = new ContentValues();
			values.put("memo", mEventItemModel.memo);
			values.put("introduce", mEventItemModel.introduce);
			values.put("event_id", mEventItemModel.event_id);
			values.put("spent_type", mEventItemModel.spent_type);
			values.put("forward_invite", mEventItemModel.forward_invite);
			values.put("other_spent_type", mEventItemModel.other_spent_type);
			values.put("public_info", mEventItemModel.public_info);
			values.put("event_type_name", mEventItemModel.event_type_name);
			values.put("title", mEventItemModel.title);
			
			if (mEventItemModel.interest)
			{
				values.put("interest", 1);
			}
			else {
				values.put("interest", 0);
			}
			values.put("zan_num", mEventItemModel.zan_num);
			values.put("people_num", mEventItemModel.people_num);
			values.put("spent", mEventItemModel.spent);
			values.put("share_content", mEventItemModel.share_content);
			values.put("event_state", mEventItemModel.event_state);
			values.put("people_join", mEventItemModel.people_join);
			values.put("event_type_id", mEventItemModel.event_type_id);
			values.put("time_state", mEventItemModel.time_state);
			values.put("join_state", mEventItemModel.join);
			values.put("address_state", mEventItemModel.address_state);
			values.put("event_pic", mEventItemModel.event_pic);
			values.put("event_invite_url", mEventItemModel.event_invite_url);
			if (mEventItemModel.zan)
			{
				values.put("zan", 1);
			}
			else {
				values.put("zan", 0);
			}
			values.put("address", mEventItemModel.address);
			values.put("end_time", mEventItemModel.end_time);
			values.put("start_time", mEventItemModel.start_time);
			values.put("add_time", mEventItemModel.add_time);
			values.put("x", mEventItemModel.x);
			values.put("y", mEventItemModel.y);
			values.put("event_trends_num", mEventItemModel.event_trends_num);
			
			values.put("sex", mEventItemModel.master.sex);
			values.put("head_pic", mEventItemModel.master.head_pic);
			values.put("relation", mEventItemModel.master.relation);
			values.put("master_introduce", mEventItemModel.master.introduce);
			values.put("identification_state", mEventItemModel.master.identification_state);
			values.put("country", mEventItemModel.master.country);
			values.put("nick_name", mEventItemModel.master.nick_name);
			values.put("area", mEventItemModel.master.area);
			values.put("school", mEventItemModel.master.school);
			values.put("background", mEventItemModel.master.background);
			values.put("company", mEventItemModel.master.company);
			values.put("account", mEventItemModel.master.account);
			values.put("user_id", mEventItemModel.master.user_id);
			if (mEventItemModel.master.allow_add_me)
			{
				values.put("allow_add_me", 1);
			}
			else {
				values.put("allow_add_me", 0);
			}
			values.put("mobile", mEventItemModel.master.mobile);
			
			if (mEventItemModel.trends != null && mEventItemModel.trends.size() > 0)
			{
				values.put("trends_content", mEventItemModel.trends.get(0).content);
				values.put("trends_nick_name", mEventItemModel.trends.get(0).org_user.nick_name);
				values.put("trends_head_pic", mEventItemModel.trends.get(0).org_user.head_pic);
				values.put("trends_add_time", mEventItemModel.trends.get(0).add_time);
			}
			else
			{
				values.put("trends_content", "");
				values.put("trends_nick_name", "");
				values.put("trends_head_pic", "");
				values.put("trends_add_time", "");
			}
			
			values.put("pic_num", mEventItemModel.pic_num);
			values.put("un_read_pic_num", mEventItemModel.un_read_pic_num);
			values.put("need_audit", mEventItemModel.need_audit);
		    values.put("event_share_url", mEventItemModel.event_share_url);
			values.put("sms_invite_content", mEventItemModel.sms_invite_content);
			values.put("wx_invite_content", mEventItemModel.wx_invite_content);
			long newRowId;
			newRowId = db.insert(
					 MYEVENT_TABLE_NAME,
			         null,
			         values);
		}
		
		//
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}
	
	/**
	 * 从数据库获取活动动态信息
	 * @author liananse
	 * @return
	 * 2013-8-1
	 */
	public List<EventItemModel> getAttendEventModels()
	{
		List<EventItemModel> list = new ArrayList<EventItemModel>();
		SQLiteDatabase db = getReadableDatabase();
		
		String[] projection = {
				"memo",
				"introduce",
				"event_id",
				"spent_type",
				"forward_invite",
				"other_spent_type",
				"public_info",
				"event_type_name",
				"title",
				"interest",
				"zan_num",
				"people_num",
				"spent",
				"share_content",
				"event_state",
				"people_join",
				"event_type_id",
				"time_state",
				"join_state",
				"address_state",
				"event_pic",
				"event_invite_url",
				"zan",
				"address",
				"end_time",
				"start_time",
				"add_time",
				"x",
				"y",
				"event_trends_num",
				
				"sex",
				"head_pic",
				"relation",
				"master_introduce",
				"identification_state",
				"country",
				"nick_name",
				"area",
				"school",
				"background",
				"company",
				"account",
				"user_id",
				"allow_add_me",
				"mobile",
				
				"trends_content",
				"trends_nick_name",
				"trends_head_pic",
				"trends_add_time",
				"pic_num",
				"un_read_pic_num",
				"need_audit",
				"event_share_url",
				"sms_invite_content",
				"wx_invite_content"
		};
		
		Cursor c = db.query(
				MYEVENT_TABLE_NAME,  // The table to query
				projection,                               // The columns to return
				null,                                     // The columns for the WHERE clause
				null,                                     // The values for the WHERE clause
				null,                                     // don't group the rows
				null,                                     // don't filter by row groups
				"start_time desc"                                      // The sort order
				);
		
		if (c != null && c.getCount() > 0)
		{
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			{
				EventItemModel tModel = new EventItemModel();
				FriendsModel tMaster = new FriendsModel();
				
				tModel.master = tMaster;
				
				tModel.memo = c.getString(0);
				tModel.introduce = c.getString(1);
				tModel.event_id = c.getInt(2);
				tModel.spent_type = c.getInt(3);
				tModel.forward_invite = c.getInt(4);
				tModel.other_spent_type = c.getString(5);
				tModel.public_info = c.getInt(6);
				tModel.event_type_name = c.getString(7);
				tModel.title = c.getString(8);
				
				if (c.getInt(9) == 1)
				{
					tModel.interest = true;
				}
				else {
					tModel.interest = false;
				}
				
				tModel.zan_num = c.getInt(10);
				tModel.people_num = c.getString(11);
				tModel.spent = c.getString(12);
				tModel.share_content = c.getString(13);
				tModel.event_state = c.getInt(14);
				tModel.people_join = c.getString(15);
				tModel.event_type_id = c.getInt(16);
				tModel.time_state = c.getInt(17);
				
				tModel.join = c.getInt(18);
				tModel.address_state = c.getInt(19);
				tModel.event_pic = c.getString(20);
				tModel.event_invite_url = c.getString(21);
				
				if (c.getInt(22) == 1)
				{
					tModel.zan = true;
				}
				else {
					tModel.zan = false;
				}
				
				tModel.address = c.getString(23);
				tModel.end_time = c.getString(24);
				tModel.start_time = c.getString(25);
				tModel.add_time = c.getString(26);

				tModel.x = c.getDouble(27);
				tModel.y = c.getDouble(28);
				tModel.event_trends_num = c.getInt(29);
				
				
				tMaster.sex = c.getInt(30);
				tMaster.head_pic = c.getString(31);
				tMaster.relation = c.getInt(32);
				tMaster.introduce = c.getString(33);
				tMaster.identification_state = c.getInt(34);
				tMaster.country = c.getString(35);
				tMaster.nick_name = c.getString(36);
				tMaster.area = c.getString(37);
				tMaster.school = c.getString(38);
				tMaster.background = c.getString(39);
				tMaster.company = c.getString(40);
				tMaster.account = c.getString(41);
				tMaster.user_id = c.getInt(42);
				
				if (c.getInt(43) == 1)
				{
					tMaster.allow_add_me = true;
				}
				else {
					tMaster.allow_add_me = false;
				}
				
				tMaster.mobile = c.getString(44);
				
				tModel.trends_content = c.getString(45);
				tModel.trends_nick_name = c.getString(46);
				tModel.trends_head_pic = c.getString(47);
				tModel.trends_add_time = c.getString(48);
				tModel.pic_num = c.getInt(49);
				tModel.un_read_pic_num = c.getInt(50);
				tModel.need_audit = c.getInt(51);
				tModel.event_share_url = c.getString(52);
				tModel.sms_invite_content = c.getString(53);
				tModel.wx_invite_content = c.getString(54);
				
				list.add(tModel);
			}
			c.close();
		}
		db.close();
		return list;
	}
	
	/**
	 * 从数据库获取活动动态信息
	 * @author liananse
	 * @return
	 * 2013-8-1
	 */
	public List<EventItemModel> getAttendEventHasTrendsModels()
	{
		List<EventItemModel> list = new ArrayList<EventItemModel>();
		SQLiteDatabase db = getReadableDatabase();
		
		String[] projection = {
				"memo",
				"introduce",
				"event_id",
				"spent_type",
				"forward_invite",
				"other_spent_type",
				"public_info",
				"event_type_name",
				"title",
				"interest",
				"zan_num",
				"people_num",
				"spent",
				"share_content",
				"event_state",
				"people_join",
				"event_type_id",
				"time_state",
				"join_state",
				"address_state",
				"event_pic",
				"event_invite_url",
				"zan",
				"address",
				"end_time",
				"start_time",
				"add_time",
				"x",
				"y",
				"event_trends_num",
				
				"sex",
				"head_pic",
				"relation",
				"master_introduce",
				"identification_state",
				"country",
				"nick_name",
				"area",
				"school",
				"background",
				"company",
				"account",
				"user_id",
				"allow_add_me",
				"mobile",
				
				"trends_content",
				"trends_nick_name",
				"trends_head_pic",
				"trends_add_time",
				"need_audit",
				"event_share_url",
				"sms_invite_content",
				"wx_invite_content"
		};
		
		String whereClause = "trends_content != ''";
		
		
		Cursor c = db.query(
				MYEVENT_TABLE_NAME,  // The table to query
				projection,                               // The columns to return
				whereClause,                                     // The columns for the WHERE clause
				null,                                     // The values for the WHERE clause
				null,                                     // don't group the rows
				null,                                     // don't filter by row groups
				"trends_add_time desc"                                      // The sort order
				);
		
		if (c != null && c.getCount() > 0)
		{
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			{
				EventItemModel tModel = new EventItemModel();
				FriendsModel tMaster = new FriendsModel();
				
				tModel.master = tMaster;
				
				tModel.memo = c.getString(0);
				tModel.introduce = c.getString(1);
				tModel.event_id = c.getInt(2);
				tModel.spent_type = c.getInt(3);
				tModel.forward_invite = c.getInt(4);
				tModel.other_spent_type = c.getString(5);
				tModel.public_info = c.getInt(6);
				tModel.event_type_name = c.getString(7);
				tModel.title = c.getString(8);
				
				if (c.getInt(9) == 1)
				{
					tModel.interest = true;
				}
				else {
					tModel.interest = false;
				}
				
				tModel.zan_num = c.getInt(10);
				tModel.people_num = c.getString(11);
				tModel.spent = c.getString(12);
				tModel.share_content = c.getString(13);
				tModel.event_state = c.getInt(14);
				tModel.people_join = c.getString(15);
				tModel.event_type_id = c.getInt(16);
				tModel.time_state = c.getInt(17);
				
				tModel.join = c.getInt(18);
				tModel.address_state = c.getInt(19);
				tModel.event_pic = c.getString(20);
				tModel.event_invite_url = c.getString(21);
				
				if (c.getInt(22) == 1)
				{
					tModel.zan = true;
				}
				else {
					tModel.zan = false;
				}
				
				tModel.address = c.getString(23);
				tModel.end_time = c.getString(24);
				tModel.start_time = c.getString(25);
				tModel.add_time = c.getString(26);

				tModel.x = c.getDouble(27);
				tModel.y = c.getDouble(28);
				tModel.event_trends_num = c.getInt(29);
				
				
				tMaster.sex = c.getInt(30);
				tMaster.head_pic = c.getString(31);
				tMaster.relation = c.getInt(32);
				tMaster.introduce = c.getString(33);
				tMaster.identification_state = c.getInt(34);
				tMaster.country = c.getString(35);
				tMaster.nick_name = c.getString(36);
				tMaster.area = c.getString(37);
				tMaster.school = c.getString(38);
				tMaster.background = c.getString(39);
				tMaster.company = c.getString(40);
				tMaster.account = c.getString(41);
				tMaster.user_id = c.getInt(42);
				
				if (c.getInt(43) == 1)
				{
					tMaster.allow_add_me = true;
				}
				else {
					tMaster.allow_add_me = false;
				}
				
				tMaster.mobile = c.getString(44);
				
				tModel.trends_content = c.getString(45);
				tModel.trends_nick_name = c.getString(46);
				tModel.trends_head_pic = c.getString(47);
				tModel.trends_add_time = c.getString(48);
				tModel.need_audit = c.getInt(49);
				tModel.event_share_url = c.getString(50);
				tModel.sms_invite_content = c.getString(51);
				tModel.wx_invite_content = c.getString(52);
				list.add(tModel);
			}
			c.close();
		}
		db.close();
		return list;
	}

	
	/**
	 * insert trends model
	 * @author liananse
	 * @param mTrendItemModel
	 * 2013-8-10
	 */
	public void insertTrendsModel(TrendItemModel mTrendItemModel)
	{
		// Gets the data repository in write mode
		SQLiteDatabase db = getWritableDatabase();

		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put("user_id", mTrendItemModel.org_user.user_id);
		values.put("head_pic", mTrendItemModel.org_user.head_pic);
		values.put("nick_name", mTrendItemModel.org_user.nick_name);
		values.put("sex", mTrendItemModel.org_user.sex);
		values.put("mobile", mTrendItemModel.org_user.mobile);
		values.put("area", mTrendItemModel.org_user.area);
		values.put("country", mTrendItemModel.org_user.country);
		values.put("account", mTrendItemModel.org_user.account);
		values.put("school", mTrendItemModel.org_user.school);
		values.put("company", mTrendItemModel.org_user.company);
		values.put("introduce", mTrendItemModel.org_user.introduce);
		values.put("relation", mTrendItemModel.org_user.relation);
		values.put("content", mTrendItemModel.content);
		values.put("event_id", mTrendItemModel.event_id);
		values.put("add_time", mTrendItemModel.add_time);
		values.put("type", mTrendItemModel.type);
		values.put("id", mTrendItemModel.id);
		values.put("show_time", mTrendItemModel.show_time);

		// Insert the new row, returning the primary key value of the new row
		long newRowId;
		newRowId = db.insert(
		         TRENDS_TABLE_NAME,
		         null,
		         values);
		db.close();
	}
	
	public void insertTrendsModelList(List<TrendItemModel> mTrendItemModels)
	{
		// Gets the data repository in write mode
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();

		// Create a new map of values, where column names are the keys
		for (int i = 0; i < mTrendItemModels.size(); i++) {
			TrendItemModel mTrendItemModel = mTrendItemModels.get(i);
			
			ContentValues values = new ContentValues();
			values.put("user_id", mTrendItemModel.org_user.user_id);
			values.put("head_pic", mTrendItemModel.org_user.head_pic);
			values.put("nick_name", mTrendItemModel.org_user.nick_name);
			values.put("sex", mTrendItemModel.org_user.sex);
			values.put("mobile", mTrendItemModel.org_user.mobile);
			values.put("area", mTrendItemModel.org_user.area);
			values.put("country", mTrendItemModel.org_user.country);
			values.put("account", mTrendItemModel.org_user.account);
			values.put("school", mTrendItemModel.org_user.school);
			values.put("company", mTrendItemModel.org_user.company);
			values.put("introduce", mTrendItemModel.org_user.introduce);
			values.put("relation", mTrendItemModel.org_user.relation);
			values.put("content", mTrendItemModel.content);
			values.put("event_id", mTrendItemModel.event_id);
			values.put("add_time", mTrendItemModel.add_time);
			values.put("type", mTrendItemModel.type);
			values.put("id", mTrendItemModel.id);
			values.put("show_time", mTrendItemModel.show_time);
			
			// Insert the new row, returning the primary key value of the new row
			long newRowId;
			newRowId = db.insert(
					TRENDS_TABLE_NAME,
					null,
					values);
			
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}
	
	public List<TrendItemModel> getTrendsItemDiscussModelsByEventId(int event_id, int trend_id)
	{
		List<TrendItemModel> list = new ArrayList<TrendItemModel>();
		SQLiteDatabase db = getReadableDatabase();
		
		String[] projection = {
			    "user_id",
			    "head_pic",
			    "nick_name",
			    "sex",
			    "mobile",
			    "area",
			    "country",
			    "account",
			    "school",
			    "company",
			    "introduce",
			    "content",
			    "event_id",
			    "add_time",
			    "type",
			    "trends_id",
			    "id",
			    "show_time",
			    "relation"
		    };
		String whereClause = "";
		
		if (trend_id == -1)
		{
			whereClause = "event_id=? and trends_id>?";
		}
		else {
			whereClause = "event_id=? and trends_id<?";
			
		}
		String[] valuesClause = {
				String.valueOf(event_id),
				String.valueOf(trend_id)
		};
		
		

		Cursor c = db.query(
			TRENDS_TABLE_NAME,  // The table to query
		    projection,                               // The columns to return
		    whereClause,                                     // The columns for the WHERE clause
		    valuesClause,                                     // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    "trends_id desc limit 10"                                      // The sort order
		    );
		
		if (c != null && c.getCount() > 0)
		{
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			{
				TrendItemModel tModel = new TrendItemModel();
				
				FriendsModel tMaster = new FriendsModel();
				
				tModel.org_user = tMaster;
				
				tMaster.user_id = c.getInt(0);
				tMaster.head_pic = c.getString(1);
				tMaster.nick_name = c.getString(2);
				tMaster.sex = c.getInt(3);
				tMaster.mobile = c.getString(4);
				tMaster.area = c.getString(5);
				tMaster.country = c.getString(6);
				tMaster.account = c.getString(7);
				tMaster.school = c.getString(8);
				tMaster.company = c.getString(9);
				tMaster.introduce = c.getString(10);
				
				tModel.content = c.getString(11);
				tModel.event_id = c.getInt(12);
				tModel.add_time = c.getString(13);
				tModel.type = c.getInt(14);
				tModel.trends_id = c.getInt(15);
				tModel.id = c.getInt(16);
				tModel.show_time = c.getInt(17);
				tMaster.relation = c.getInt(18);
				list.add(tModel);
			}
			c.close();
		}
		db.close();
		List<TrendItemModel> ResultList = new ArrayList<TrendItemModel>();
		
		if (list.size() > 0)
		{
			for (int i = list.size() - 1; i >= 0; i--) {
				ResultList.add(list.get(i));
			}
		}
		
		return ResultList;
	}
	
	
	/**
	 * 返回最新的trendsitem
	 * @author liananse
	 * @param event_id
	 * @return
	 * 2013-8-12
	 */
	public TrendItemModel getTrendsMaxTimeByEventID(int event_id)
	{
		List<TrendItemModel> list = new ArrayList<TrendItemModel>();
		SQLiteDatabase db = getReadableDatabase();
		
		String[] projection = {
			    "add_time",
			    "user_id",
			    "head_pic",
			    "nick_name",
			    "sex",
			    "mobile",
			    "area",
			    "country",
			    "account",
			    "school",
			    "company",
			    "introduce",
			    "content",
			    "event_id",
			    "type",
			    "trends_id",
			    "id",
			    "show_time",
			    "relation"
		    };
		String whereClause = "";
		
		whereClause = "event_id=? and type=?";
		String[] valuesClause = {
				String.valueOf(event_id),
				"1"
		};
		
		

		Cursor c = db.query(
			TRENDS_TABLE_NAME,  // The table to query
		    projection,                               // The columns to return
		    whereClause,                                     // The columns for the WHERE clause
		    valuesClause,                                     // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    "trends_id desc limit 1"                                      // The sort order
		    );
		
		if (c != null && c.getCount() > 0)
		{
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			{
				TrendItemModel tModel = new TrendItemModel();
				
				tModel.add_time = c.getString(0);
				FriendsModel tMaster = new FriendsModel();
				
				tModel.org_user = tMaster;
				
				tMaster.user_id = c.getInt(1);
				tMaster.head_pic = c.getString(2);
				tMaster.nick_name = c.getString(3);
				tMaster.sex = c.getInt(4);
				tMaster.mobile = c.getString(5);
				tMaster.area = c.getString(6);
				tMaster.country = c.getString(7);
				tMaster.account = c.getString(8);
				tMaster.school = c.getString(9);
				tMaster.company = c.getString(10);
				tMaster.introduce = c.getString(11);
				
				tModel.content = c.getString(12);
				tModel.event_id = c.getInt(13);
				tModel.type = c.getInt(14);
				tModel.trends_id = c.getInt(15);
				tModel.id = c.getInt(16);
				tModel.show_time = c.getInt(17);
				tMaster.relation = c.getInt(18);
				list.add(tModel);
			}
			c.close();
		}
		db.close();
		
		if (list.size() > 0)
		{
			return list.get(0);
		}
		
		
		return null;
	}
	
	
	// db.update(String table, Contentvalues values, String whereClause, String whereArgs);
	
	/**
	 * 请求动态接口后批量更新数据库中已经存在的数据
	 * @author liananse
	 * @param mEventItemModels
	 * 2013-9-2
	 */
	public void updateAttendEventModels(List<EventItemModel> mEventItemModels)
	{
		int event_trends_num = 0;
		// 动态数目减去 “XX上传了几张照片”这种类型的数目
		int other_trends_type_num = 0;
		
		boolean hasDiscussTrends = false;
		
		String trends_add_time = "";
		String trends_content = "";
		String trends_head_pic = "";
		String trends_nick_name = "";
		
		// Gets the data repository in write mode
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		
		
		for (int i = 0; i < mEventItemModels.size(); i++) {
			
			EventItemModel mEventItemModel = mEventItemModels.get(i);
			
			event_trends_num = mEventItemModel.event_trends_num;
			
			hasDiscussTrends = false;
			
			other_trends_type_num = 0;
			
			if (mEventItemModel.trends != null && mEventItemModel.trends.size() > 0)
			{
				for (int j = 0; j < mEventItemModel.trends.size(); j++) {
					
					if (mEventItemModel.trends.get(j).type == 5)
					{
						other_trends_type_num++;
					}
				}
			}
			
			if (mEventItemModel.trends != null && mEventItemModel.trends.size() > 0)
			{
				for (int j = 0; j < mEventItemModel.trends.size(); j++) {
					
					if (mEventItemModel.trends.get(j).type != 5)
					{
						trends_add_time = mEventItemModel.trends.get(j).add_time;
						trends_content = mEventItemModel.trends.get(j).content;
						trends_head_pic = mEventItemModel.trends.get(j).org_user.head_pic;
						trends_nick_name = mEventItemModel.trends.get(j).org_user.nick_name;
						hasDiscussTrends = true;
						break;
					}
				}
			}
			else {
				trends_add_time = "";
				trends_content = "";
				trends_head_pic = "";
				trends_nick_name = "";
			}
			
			event_trends_num = event_trends_num - other_trends_type_num;
			
			ContentValues values = new ContentValues();
			
			
			values.put("event_trends_num", event_trends_num);
			if (hasDiscussTrends)
			{
				values.put("trends_content", trends_content);
				values.put("trends_nick_name", trends_nick_name);
				values.put("trends_head_pic", trends_head_pic);
				values.put("trends_add_time", trends_add_time);
			}
			values.put("pic_num", mEventItemModel.pic_num);
			values.put("un_read_pic_num", mEventItemModel.un_read_pic_num);
			
			db.update(MYEVENT_TABLE_NAME, values, "event_id = ?", new String[]{String.valueOf(mEventItemModel.event_id)});
		}

		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		
	}
	
	/**
	 * 更新本地活动，修改了封面等信息时更新本地的model
	 * @param mEventItemModel
	 */
	public void updateLocalEventModel(EventItemModel mEventItemModel)
	{
		if (hasEvent(mEventItemModel.event_id))
		{
			SQLiteDatabase db = getWritableDatabase();
			db.beginTransaction();
			
			ContentValues values = new ContentValues();
			
			values.put("memo", mEventItemModel.memo);
			values.put("introduce", mEventItemModel.introduce);
			values.put("event_id", mEventItemModel.event_id);
			values.put("spent_type", mEventItemModel.spent_type);
			values.put("forward_invite", mEventItemModel.forward_invite);
			values.put("other_spent_type", mEventItemModel.other_spent_type);
			values.put("public_info", mEventItemModel.public_info);
			values.put("event_type_name", mEventItemModel.event_type_name);
			values.put("title", mEventItemModel.title);
			
			if (mEventItemModel.interest)
			{
				values.put("interest", 1);
			}
			else {
				values.put("interest", 0);
			}
			values.put("zan_num", mEventItemModel.zan_num);
			values.put("people_num", mEventItemModel.people_num);
			values.put("spent", mEventItemModel.spent);
			values.put("share_content", mEventItemModel.share_content);
			values.put("event_state", mEventItemModel.event_state);
			values.put("people_join", mEventItemModel.people_join);
			values.put("event_type_id", mEventItemModel.event_type_id);
			values.put("time_state", mEventItemModel.time_state);
			values.put("join_state", mEventItemModel.join);
			values.put("address_state", mEventItemModel.address_state);
			values.put("event_pic", mEventItemModel.event_pic);
			values.put("event_invite_url", mEventItemModel.event_invite_url);
			if (mEventItemModel.zan)
			{
				values.put("zan", 1);
			}
			else {
				values.put("zan", 0);
			}
			values.put("address", mEventItemModel.address);
			values.put("end_time", mEventItemModel.end_time);
			values.put("start_time", mEventItemModel.start_time);
			values.put("add_time", mEventItemModel.add_time);
			values.put("x", mEventItemModel.x);
			values.put("y", mEventItemModel.y);
			values.put("event_trends_num", mEventItemModel.event_trends_num);
			
			values.put("pic_num", mEventItemModel.pic_num);
			values.put("un_read_pic_num", mEventItemModel.un_read_pic_num);
			values.put("need_audit", mEventItemModel.need_audit);
		    values.put("event_share_url", mEventItemModel.event_share_url);
			values.put("sms_invite_content", mEventItemModel.sms_invite_content);
			values.put("wx_invite_content", mEventItemModel.wx_invite_content);
			
			db.update(MYEVENT_TABLE_NAME, values, "event_id = ?", new String[]{String.valueOf(mEventItemModel.event_id)});

			db.setTransactionSuccessful();
			db.endTransaction();
			db.close();
		}
	}
	
	/**
	 * 将好友从数据库删除
	 * @author liananse
	 * @param userId
	 * 2013-8-30
	 */
	public void deleteEventItemModelFromDB(int event_id)
	{
		SQLiteDatabase db = getWritableDatabase();
		String where = "event_id = " + event_id;
		String whereArgs[] = null;
		db.delete(MYEVENT_TABLE_NAME, where, whereArgs);
		
		db.close();
	}
	
	/**
	 * 更新我参加的活动活动动态 讨论组使用
	 * @author liananse
	 * @param mEventItemModels
	 * 2013-9-2
	 */
	public void updateAttendEventModel(EventItemModel mEventItemModel)
	{
		int event_trends_num = 0;
		
		String trends_add_time = "";
		String trends_content = "";
		String trends_head_pic = "";
		String trends_nick_name = "";
		
		if (hasEvent(mEventItemModel.event_id))
		{
			// Gets the data repository in write mode
			SQLiteDatabase db = getWritableDatabase();
			db.beginTransaction();
			
			event_trends_num = mEventItemModel.event_trends_num;
			
			if (mEventItemModel.trends != null && mEventItemModel.trends.size() > 0)
			{
				trends_add_time = mEventItemModel.trends.get(0).add_time;
				trends_content = mEventItemModel.trends.get(0).content;
				trends_head_pic = mEventItemModel.trends.get(0).org_user.head_pic;
				trends_nick_name = mEventItemModel.trends.get(0).org_user.nick_name;
			}
			else {
				trends_add_time = "";
				trends_content = "";
				trends_head_pic = "";
				trends_nick_name = "";
			}
			
			ContentValues values = new ContentValues();
			
			values.put("event_trends_num", event_trends_num);
			values.put("trends_content", trends_content);
			values.put("trends_nick_name", trends_nick_name);
			values.put("trends_head_pic", trends_head_pic);
			values.put("trends_add_time", trends_add_time);
			
			db.update(MYEVENT_TABLE_NAME, values, "event_id = ?", new String[]{String.valueOf(mEventItemModel.event_id)});

			db.setTransactionSuccessful();
			db.endTransaction();
			db.close();
		}
		else {
			List<EventItemModel> mlist = new ArrayList<EventItemModel>();
			mlist.add(mEventItemModel);
			
			insertAttendEventModel(mlist);
		}
		
		
	}
	
	/**
	 * 将event_trends_num 设置为0
	 * @author liananse
	 * @param event_id
	 * 2013-9-2
	 */
	public void updateAttendsEventTrendsToZero(int event_id)
	{
		int event_trends_num = 0;
		
		
		// Gets the data repository in write mode
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		
		
		ContentValues values = new ContentValues();
		
		values.put("event_trends_num", event_trends_num);
			
		db.update(MYEVENT_TABLE_NAME, values, "event_id = ?", new String[]{String.valueOf(event_id)});

		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}
	
	/**
	 * 将un_read_pic_num 设置为0
	 * @author liananse
	 * @param event_id
	 * 2013-9-2
	 */
	public void updateAttendsEventUnReadPicNumToZero(int event_id)
	{
		int un_read_pic_num = 0;
		
		
		// Gets the data repository in write mode
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		
		
		ContentValues values = new ContentValues();
		
		values.put("un_read_pic_num", un_read_pic_num);
			
		db.update(MYEVENT_TABLE_NAME, values, "event_id = ?", new String[]{String.valueOf(event_id)});

		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}
	
	/**
	 * 返回最新的trendsitem
	 * @author liananse
	 * @param event_id
	 * @return
	 * 2013-8-12
	 */
	public boolean hasEvent(int event_id)
	{
		boolean result = false;
		SQLiteDatabase db = getReadableDatabase();
		
		String[] projection = {
			    "event_id"
		    };
		String whereClause = "";
		
		whereClause = "event_id=?";
		String[] valuesClause = {
				String.valueOf(event_id)
		};
		
		Cursor c = db.query(
			MYEVENT_TABLE_NAME,  // The table to query
		    projection,                               // The columns to return
		    whereClause,                                     // The columns for the WHERE clause
		    valuesClause,                                     // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                      // The sort order
		    );
		
		if (c != null && c.getCount() > 0)
		{
			result = true;
			c.close();
		}
		db.close();
		
		return result;
	}
	
	private static final String SQL_CREATE_UPLOAD_PIC_TABLE = 
			"CREATE TABLE IF NOT EXISTS " + UPLOAD_PIC + " (" +
		    "Id" + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		    "event_id" + " INTEGER," + 
		    "content" + " TEXT," +
		    "path" + " TEXT," +
		    "record_id" + " INTEGER," + 
		    "pic_num" + " INTEGER," +
		    "status" + " INTEGER" + 
		    " )";
	
	public void insertUploadPicModel(List<PhotoUploadModel> list)
	{
		if(list!=null && list.size()>0)
		{
			SQLiteDatabase db = getWritableDatabase();
			for(PhotoUploadModel model:list)
			{
				ContentValues values = new ContentValues();
				values.put("event_id", model.event_id);
				values.put("content", model.content);
				values.put("path", model.path);
				values.put("record_id", model.record_id);
				values.put("pic_num", model.pic_num);
				values.put("status", 0);
				
				db.insert(UPLOAD_PIC, null, values);
			}
			
			db.close();
		}
	}
	
	public List<PhotoUploadModel> getNotUploadPhoto()
	{
		List<PhotoUploadModel> list = new ArrayList<PhotoUploadModel>();
		PhotoUploadModel model = null;
		SQLiteDatabase db = getReadableDatabase();
		String projection = "id,event_id,content,path,record_id,pic_num";
		Cursor c = db.rawQuery("select "+projection+" from "+UPLOAD_PIC+" where status=0",null);
		if (c != null && c.getCount() > 0)
		{
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			{
				model = new PhotoUploadModel();
				model.id = c.getInt(0);
				model.event_id = c.getInt(1);
				model.content = c.getString(2);
				model.path = c.getString(3);
				model.record_id = c.getInt(4);
				model.pic_num = c.getInt(5);
				list.add(model);
			}
		}
		return list;
	}
	
	public void updatePhotoUploadModel(int id)
	{
		
		// Gets the data repository in write mode
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		ContentValues v = new ContentValues();
		v.put("status", 1);
//		String[] args = {String.valueOf(id+"")};
		db.update(UPLOAD_PIC, v, "id="+id, null);
//		db.rawQuery("update "+UPLOAD_PIC+" set status=1 where id=?", new String[]{String.valueOf(id)});

		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2)
	{
		// TODO Auto-generated method stub
		
	}

}