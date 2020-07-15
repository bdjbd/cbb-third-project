package com.p2p.tools.dbtable;

import com.fastunit.Row;

//DataLinkCol=Title,ColName,FieldName,width,isShow,sFormatHtml(替换[FieldName]),ActionUrl,EventName
public class HtmlDataLinkCol extends HtmlColAbstract implements IDBTableCol 
{
	
	private String mFieldName="";
	private String mFormatHtml="";
	private String mActionUrl="";
	private String mEventName="";
	
	public HtmlDataLinkCol(String title
			,String colname
			,String width
			,boolean isshow
			,String FieldName
			,String formatHtml
			,String ActionUrl
			,String EventName)
	{
		super.mTitle=title;
		super.mColName=colname;
		//super.mHtml=html;
		super.mWidth=width;
		super.mIsShow=isshow;
		
		mFieldName=FieldName;
		mFormatHtml=formatHtml;
		
		mActionUrl=ActionUrl;
		mEventName=EventName;
		
	}
	
	@Override
	public String getHtml(Row rw, int rowIndex,DBTableListManager tlm) 
	{
		String sFieldValue=rw.get(mFieldName);
		if(sFieldValue==null)
			sFieldValue=" ";
		
		String tFiledValue=mFormatHtml.replace("[" + super.mColName + "]", sFieldValue);
		
		String sHtml="<a class='" + DBTableListManager.LINK_CLASS_NAME + "'";
		sHtml+=" onclick=\"on_" + tlm.CtlName + "_" + rowIndex + "_Click('" + mActionUrl + "','" + mEventName + "');\">";
		sHtml+=tFiledValue;
		sHtml+="</a>";
		
		return sHtml;
	}

}
