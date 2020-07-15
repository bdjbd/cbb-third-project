package com.p2p.tools.dbtable;

import com.fastunit.Row;

/*
 * LableCol=Title,ColName,sHtml,width,isShow
 * 标签列
 * */
public class HtmlLableCol extends HtmlColAbstract implements IDBTableCol 
{
	
	public HtmlLableCol(String title,String colname,String html,String width,boolean isshow)
	{
		super.mTitle=title;
		super.mColName=colname;
		super.mHtml=html;
		super.mWidth=width;
		super.mIsShow=isshow;
	}

	@Override
	public String getHtml(Row rw, int rowIndex,DBTableListManager tlm) 
	{
		String sHtml=super.mHtml;
		
		return sHtml;
	}


}
