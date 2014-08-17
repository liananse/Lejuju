
package com.medialab.lejuju.push;

import org.json.JSONObject;

public class Message
{

	String content;
	String head;
	Long id;
	JSONObject json;
	MessageType type;
	String url;
	boolean notified = true;

	public Message()
	{
		
	}

	public Message(JSONObject json)
	{
		this.json = json;
		if(json!=null)
		{
			id = json.optLong("id",0);
			content = json.optString("content","content");
			url = json.optString("url","url");
			type = MessageType.getMessageTypeByCode(json.optInt("type", 0));
			head = type.getName();
		}
		notified = false;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * @return the content
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * @return the head
	 */
	public String getHead()
	{
		return head;
	}

	/**
	 * @return the id
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * @return the json
	 */
	public JSONObject getJson()
	{
		return json;
	}

	/**
	 * @return the type
	 */
	public MessageType getType()
	{
		return type;
	}

	/**
	 * @return the url
	 */
	public String getUrl()
	{
		return url;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content)
	{
		this.content = content;
	}

	/**
	 * @param head
	 *            the head to set
	 */
	public void setHead(String head)
	{
		this.head = head;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * @param json
	 *            the json to set
	 */
	public void setJson(JSONObject json)
	{
		this.json = json;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(MessageType type)
	{
		this.type = type;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}

	/**
	 * @return the notified
	 */
	public boolean isNotified()
	{
		return notified;
	}

	/**
	 * @param notified
	 *            the notified to set
	 */
	public void setNotified(boolean notified)
	{
		this.notified = notified;
	}

	@Override
	public String toString()
	{
		return "Message [content=" + content + ", head=" + head + ", id=" + id + ", json=" + json  + ", type=" + type + ", url=" + url + ", notified=" + notified + "]";
	}

}
