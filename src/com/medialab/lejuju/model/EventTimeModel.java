package com.medialab.lejuju.model;

import java.io.Serializable;

public class EventTimeModel implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String start_time;
	public String end_time;
	
	
	public EventTimeModel(String startTime, String endTime)
	{
		this.start_time = startTime;
		this.end_time = endTime;
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "EventTimeModel" + " start_time :" + start_time +
				" end_time : " + end_time;
	}
	
	

}
