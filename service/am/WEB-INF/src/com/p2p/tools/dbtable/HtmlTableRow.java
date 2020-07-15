package com.p2p.tools.dbtable;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import com.fastunit.Row;

//生成HtmlTable样式的表格
public class HtmlTableRow implements IDBTableRow
{
	private HashMap<String, IDBTableCol> mColList=null;
	
	@Override
	public void init(HashMap<String, IDBTableCol> mapCol)
	{
		mColList=mapCol;

	}

	@Override
	public String getHtml(Row rw, int rowIndex,DBTableListManager tlm)
	{
		
		String sHtml="<tr class='" +  DBTableListManager.CONTENT_ROW_CLASS_NAME + "'>";
		
		IDBTableCol val=null;
		
		for(int i=0;i<mColList.size();i++)
		{
			val=mColList.get(i+"");
			
			if(val.getIsShow())
			{
				sHtml+="<td class='" +  DBTableListManager.CONTENT_COL_CLASS_NAME + "'";
				sHtml+=" width='" + val.getWidth() + "'";
				sHtml+=">";
				sHtml+= val.getHtml(rw, rowIndex,tlm);
				sHtml+= "</td>";
			}
			
		}
		
		
		sHtml+="</tr>";
		
		sHtml+= "<script type=\"text/javascript\">\n";
		sHtml+= "<!--\n";
		sHtml+= "function on_" + tlm.CtlName + "_" + rowIndex + "_Click(actionUrl,eventName)\n";
		sHtml+= "{\n";
		sHtml+= "	var tUrl=actionUrl + \"?EventName=\" + eventName;\n";
		
		sHtml+= "	tUrl+=\"" + createUrlValues(rw) + "\";\n";

		sHtml+= "	window.location.href=tUrl;\n";
		sHtml+= "}\n";
		sHtml+= "//-->\n";
		sHtml+= "</script>\n";

		
		return sHtml;

	}
	
	//拼接字符串，返回该行的所有值
	public static String createUrlValues(Row rw)
	{
		String rValue="";
		try 
		{
			String tValue ="";
			//String mytext2 =java.net.URLDecoder.decode(mytext,   "utf-8"); 
			
			for(int i=0;i<rw.size();i++)
			{
				tValue = java.net.URLEncoder.encode(rw.get(i)+"","utf-8");
				
				rValue+= "&" + rw.getKey(i) + "=" + tValue;
			}
		} 
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}  
		
		return rValue;
	}

	@Override
	public String getTitleHtml() 
	{
		String sHtml="<tr class='" +  DBTableListManager.TITLE_ROW_CLASS_NAME + "'>";
		
		IDBTableCol val=null;
		
		for(int i=0;i<mColList.size();i++)
		{
			val=mColList.get(i+"");
			
			if(val.getIsShow())
			{
				sHtml+="<td class='" +  DBTableListManager.TITLE_COL_CLASS_NAME + "'";
				sHtml+=" width='" + val.getWidth() + "'";
				sHtml+=">";
				sHtml+= val.getTitle();
				sHtml+= "</td>";
			}
		}
		
		sHtml+="</tr>";

		
		return sHtml;
	}

	@Override
	public String getHeadHtml() {
		return "<table class='" +  DBTableListManager.TABLE_CLASS_NAME + "'>";
	}

	@Override
	public String getFootHtml() {
		return "</table>";
	}

}
