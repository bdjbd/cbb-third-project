package com.p2p.tools.dbtable;

import com.fastunit.Row;

//LableLinkCol=Title,ColName,sHtml,width,isShow,ActionUrl,EventName
public class HtmlLableLinkCol extends HtmlColAbstract implements IDBTableCol 
{
	private String mActionUrl="";
	private String mEventName="";
	
	public HtmlLableLinkCol(String title
			,String colname
			,String html
			,String width
			,boolean isshow
			,String ActionUrl
			,String EventName)
	{
		super.mTitle=title;
		super.mColName=colname;
		super.mHtml=html;
		super.mWidth=width;
		super.mIsShow=isshow;
		mActionUrl=ActionUrl;
		mEventName=EventName;
		
	}
	
	@Override
	public String getHtml(Row rw, int rowIndex,DBTableListManager tlm) 
	{
		
		String sHtml="<a class='" + DBTableListManager.LINK_CLASS_NAME + "'";
		sHtml+=" onclick=\"on_" + tlm.CtlName + "_" + rowIndex + "_Click('" + mActionUrl + "','" + mEventName + "');\">";
		sHtml+=super.mHtml;
		sHtml+="</a>";
		
		return sHtml;
	}

}
