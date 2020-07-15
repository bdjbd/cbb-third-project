package com.p2p.tools.dbtable;

import com.fastunit.Row;


/*
 * 数据表类接口，应有以下类接口
 * 
 * LableCol=Title,ColName,sHtml,width,isShow
 * LableLinkCol=Title,ColName,sHtml,width,isShow,ActionUrl,EventName
 * DataLableCol=Title,ColName,FieldName,Width,isShow,sFormatHtml(替换[FieldName])
 * DataLinkCol=Title,ColName,FieldName,width,isShow,sFormatHtml(替换[FieldName]),ActionUrl,EventName
 * AutoNumberCol=Title,ColName,width,isShow,sFormatHtml(替换[FieldName])
 * */
public interface IDBTableCol 
{
	//得到列Html,返回一个TD
	public String getHtml(Row rw,int rowIndex,DBTableListManager tlm);
	
	//得到Title
	public String getTitle();
	
	//得到列名称
	public String getColName();
	
	//得到宽度
	public String getWidth();
	
	//得到是否显示
	public boolean getIsShow();
}
