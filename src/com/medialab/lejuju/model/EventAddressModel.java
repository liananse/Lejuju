package com.medialab.lejuju.model;

import java.io.Serializable;

public class EventAddressModel implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String address;
	public double x;
	public double y;
	
	public EventAddressModel(String address, double x, double y)
	{
		this.address = address;
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "EventAddressModel" + " address :" + address +
				" x : " + x + 
				" y : " + y;
	}

	
	
}
