package com.medialab.lejuju.views;

public class EDTool {

	private int startX, startY;
	public EDTool(int startX, int startY, int endX, int endY)
	{
		super();
		this.startX = startX;
		this.startY = startY;
	}

	public int getScrollX(float dx)
	{
		int xx = (int) (startX + dx / 2.5F);
		return xx;
	}

	public int getScrollY(float dy)
	{
		return (int) (startY + dy / 2.5F);
	}
}