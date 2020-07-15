package com.p2p.tools.dbtable;

import java.util.HashMap;

import com.fastunit.Row;

//数据表格行Html接口
/*
 * TableRow
 * */
public interface IDBTableRow
{
	public void init(HashMap<String, IDBTableCol> mapCol);
	
	//设置Col集合，返回行Html
	public String getHtml(Row rw,int rowIndex,DBTableListManager tlm);
	
	//得到标题行Html
	public String getTitleHtml();
	
	//得到头Html
	public String getHeadHtml();
	
	//得到尾Html
	public String getFootHtml();
}
