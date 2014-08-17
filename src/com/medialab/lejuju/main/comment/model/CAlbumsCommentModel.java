package com.medialab.lejuju.main.comment.model;

import java.io.Serializable;

import com.medialab.lejuju.model.FriendsModel;

public class CAlbumsCommentModel implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String content = "";
	public int id = -1;
	public FriendsModel org_user = null;
	public FriendsModel opposite_user = null;
	public String add_time = "";

}
