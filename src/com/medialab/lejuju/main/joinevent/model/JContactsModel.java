package com.medialab.lejuju.main.joinevent.model;

import java.io.Serializable;

public class JContactsModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String name = "";
	public String mobile = "";
	public int isSelected = -1;
	public String namePinYin = "";
	public String namePinYinFirst = "";
	@Override
	public String toString()
	{
		return "Contact [name=" + name + ", mobile=" + mobile + ", namePinYin=" + namePinYin + ", isSelected=" + isSelected +"]";
	}

	public JContactsModel(String name, String mobile, int isSelected, String namePinYin, String namePinYinFirst)
	{
		this.name = name;
		this.mobile = mobile;
		this.isSelected = isSelected;
		this.namePinYin = namePinYin;
		this.namePinYinFirst = namePinYinFirst;
	}
	
	
}
