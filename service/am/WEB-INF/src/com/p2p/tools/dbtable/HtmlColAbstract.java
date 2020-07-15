package com.p2p.tools.dbtable;

public abstract class HtmlColAbstract implements IDBTableCol 
{
	public String mTitle="";
	public String mColName="";
	public String mHtml="";
	public String mWidth="";
	public boolean mIsShow=true;

	@Override
	public String getTitle() {

		return mTitle;
	}
	
	@Override
	public String getColName()
	{
		return mColName;
	}
	
	// 得到宽度
	@Override
	public String getWidth() 
	{
		return mWidth;
	}

	// 得到是否显示
	@Override
	public boolean getIsShow() 
	{
		return mIsShow;
	}

}
