package com.medialab.lejuju.model;

import java.io.Serializable;

public class EventPicModel implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public long id;
	public String picurl;
	public String big_pic_url;
	public int lock_state;
	public String content;
}
