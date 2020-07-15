package com.p2p.tools.dbtable;

import java.util.HashMap;

import com.fastunit.Row;

public class HtmlItemRow implements IDBTableRow 
{
	private String mTemplate = "";
	private String mTagStart = "[";
	private String mTagEnd = "]";

	private HashMap<String, IDBTableCol> mColList = null;

	/**
	 * @param sTmp
	 *            模板内容
	 * @param sTagStart
	 *            需要替换的开始标示符，默认为：[
	 * @param sTagEnd
	 *            需要替换的结束标示符，默认为：]
	 * 
	 * 
	 */
	public HtmlItemRow(String sTemplate, String sTagStart, String sTagEnd) 
	{
		mTemplate = sTemplate;
		mTagStart = sTagStart;
		mTagEnd = sTagEnd;

	}

	public HtmlItemRow(String sTemplate) 
	{
		mTemplate = sTemplate;
	}

	@Override
	public void init(HashMap<String, IDBTableCol> mapCol) 
	{
		mColList = mapCol;
	}

	@Override
	public String getHtml(Row rw, int rowIndex,DBTableListManager tlm) 
	{
		String sHtml = this.mTemplate;

		IDBTableCol val = null;
		
		//String tFiledValue=mFormatHtml.replace("[" + super.mColName + "]", sFieldValue);
		for (int i = 0; i < mColList.size(); i++) 
		{
			val = mColList.get(i + "");
			
			sHtml=sHtml.replace(mTagStart+ val.getColName() + mTagEnd, val.getHtml(rw, rowIndex,tlm));
		}

		sHtml += "<script type=\"text/javascript\">\n";
		sHtml += "<!--\n";
		sHtml += "function on_" + tlm.CtlName + "_" + rowIndex + "_Click(actionUrl,eventName)\n";
		sHtml += "{\n";
		sHtml += "	var tUrl=actionUrl + \"?EventName=\" + eventName;\n";

		sHtml += "	tUrl+=\"" + HtmlTableRow.createUrlValues(rw) + "\";\n";

		sHtml += "	window.location.href=tUrl;\n";
		sHtml += "}\n";
		sHtml += "//-->\n";
		sHtml += "</script>\n";

		return sHtml;
	}

	@Override
	public String getTitleHtml() {
		return "";
	}

	@Override
	public String getHeadHtml() {
		return "";
	}

	@Override
	public String getFootHtml() {
		return "";
	}

}
