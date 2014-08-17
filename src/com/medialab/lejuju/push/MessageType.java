
package com.medialab.lejuju.push;

public enum MessageType
{
	INVITE_JOIN_EVENT(1, "邀请通知"),
	DISCUSS_GROUP_TRENDS(2, "活动动态"),
	FRIEND_HOLD_PUBLIC_EVENT(3, "活动通知"),
	EVENT_COMMENT(4, "活动评论"),
	P2P_COMMENT(5, "回复评论"),
	ADD_FRIEND_APPLY(6, "好友通知"),
	ALLOW_ADD_FRIEND_SUCCESS(7, "互加好友"),
	APPLY_JOIN_EVENT(8, "参加活动"),
	JOIN_EVENT_AUDIT_PASS(9, "活动通过"),
	ADD_FRIEND_AUDIT_PASS(11, "验证通过"),
	ADD_FRIEND_AUDIT_REFUSE(12, "拒绝添加"),
	SEND_EVENT_RECORD(13, "新的活动记录"),
	CHANGE_EVENT_INFO(14, "活动信息更新"),
	JOIN_EVENT(15, "加入活动"),
	PUSH_TYPE_QUIT_EVENT(16, "退出活动推送"),
	PUSH_TYPE_NEW_FRIENDS(18, "新朋友推送");
	/**
	 * 根据code 获取消息类型
	 */
	public static MessageType getMessageTypeByCode(int code)
	{
		if (INVITE_JOIN_EVENT.getCode() == code)
		{
			return INVITE_JOIN_EVENT;
		}
		else if (DISCUSS_GROUP_TRENDS.getCode() == code)
		{
			return DISCUSS_GROUP_TRENDS;
		}
		else if (FRIEND_HOLD_PUBLIC_EVENT.getCode() == code)
		{
			return FRIEND_HOLD_PUBLIC_EVENT;
		}
		else if (EVENT_COMMENT.getCode() == code)
		{
			return EVENT_COMMENT;
		}
		else if (P2P_COMMENT.getCode() == code)
		{
			return P2P_COMMENT;
		}
		else if (ADD_FRIEND_APPLY.getCode() == code)
		{
			return ADD_FRIEND_APPLY;
		}
		else if (ALLOW_ADD_FRIEND_SUCCESS.getCode() == code)
		{
			return ALLOW_ADD_FRIEND_SUCCESS;
		}
		else if (APPLY_JOIN_EVENT.getCode() == code)
		{
			return APPLY_JOIN_EVENT;
		}
		else if (JOIN_EVENT_AUDIT_PASS.getCode() == code)
		{
			return JOIN_EVENT_AUDIT_PASS;
		}
		else if (ADD_FRIEND_AUDIT_PASS.getCode() == code)
		{
			return ADD_FRIEND_AUDIT_PASS;
		}
		else if (ADD_FRIEND_AUDIT_REFUSE.getCode() == code)
		{
			return ADD_FRIEND_AUDIT_REFUSE;
		}
		else if (SEND_EVENT_RECORD.getCode() == code)
		{
			return SEND_EVENT_RECORD;
		}
		else if (CHANGE_EVENT_INFO.getCode() == code)
		{
			return CHANGE_EVENT_INFO;
		}
		else if (JOIN_EVENT.getCode() == code)
		{
			return JOIN_EVENT;
		}
		else if (PUSH_TYPE_QUIT_EVENT.getCode() == code)
		{
			return PUSH_TYPE_QUIT_EVENT;
		}
		else if (PUSH_TYPE_NEW_FRIENDS.getCode() == code)
		{
			return PUSH_TYPE_NEW_FRIENDS;
		}
		else
		{
			return null;
		}
	}

	private int code;

	private String name;


	private MessageType(int code, String name)
	{
		this.code = code;
		this.name = name;
	}

	/**
	 * @return the code
	 */
	public int getCode()
	{
		return code;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("MessageType [code=");
		builder.append(code);
		builder.append(", name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}
}
