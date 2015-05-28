package com.app.Common.SelectMultiImg;

public class SelectMultiImgDirItem
{
	/**
	 * 圖片的文件夾路徑
	 */
	private String dir;

	/**
	 * 第一張圖片的路徑
	 */
	private String firstImagePath;

	/**
	 * 文件夾的名稱
	 */
	private String name;

	/**
	 * 圖片的數量
	 */
	private int count;

	public String getDir()
	{
		return dir;
	}

	public void setDir(String dir)
	{
		this.dir = dir;
		int lastIndexOf = this.dir.lastIndexOf("/");
		this.name = this.dir.substring(lastIndexOf);
	}

	public String getFirstImagePath()
	{
		return firstImagePath;
	}

	public void setFirstImagePath(String firstImagePath)
	{
		this.firstImagePath = firstImagePath;
	}

	public String getName()
	{
		return name;
	}
	public int getCount()
	{
		return count;
	}

	public void setCount(int count)
	{
		this.count = count;
	}



}
