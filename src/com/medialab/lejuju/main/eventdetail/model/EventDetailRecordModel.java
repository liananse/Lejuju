package com.medialab.lejuju.main.eventdetail.model;

import java.io.Serializable;
import java.util.List;

import com.medialab.lejuju.model.EventPicModel;
import com.medialab.lejuju.model.FriendsModel;

public class EventDetailRecordModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String add_time = "";
	public int record_id = 0;
	
	public FriendsModel user = null;
	public List<EventPicModel> piclist = null;
}
