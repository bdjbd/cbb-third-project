package com.p2p.tools.dbtable;

import com.fastunit.Row;

//DataLableCol=Title,ColName,FieldName,Width,isShow,sFormatHtml(替换[FieldName])
public class HtmlDataLableCol extends HtmlColAbstract implements
		IDBTableCol 
{
	private String mFieldName="";
	private String mFormatHtml="";
	
	public HtmlDataLableCol(String title,String colname,String width,boolean isshow,String FieldName,String formatHtml)
	{
		super.mTitle=title;
		super.mColName=colname;
		//super.mHtml=html;
		super.mWidth=width;
		super.mIsShow=isshow;
		
		mFieldName=FieldName;
		mFormatHtml=formatHtml;
	}

	@Override
	public String getHtml(Row rw, int rowIndex,DBTableListManager tlm) 
	{
		
		String sHtml=mFormatHtml;
		
		String sFieldValue=rw.get(mFieldName);
		if(sFieldValue==null)
			sFieldValue=" ";
		
		sHtml= sHtml.replace("[" + super.mColName + "]", sFieldValue);

		return sHtml;
	}

}
