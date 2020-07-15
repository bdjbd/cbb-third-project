package com.p2p.tools.dbtable;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;


//数据表list页面管理
//输出页面内容，实现分页
//LIMIT { number | ALL }] OFFSET number
//limit 每次返回的最大行数 offset 忽略前面的行数
//LIMIT 2 OFFSET 0
public class DBTableListManager 
{
	//样式文件名称定义
	//集合外框样式名称
	public static String TABLE_CLASS_NAME="db_table_List_TableClassName";
	//标题栏整体样式名称
	public static String TITLE_ROW_CLASS_NAME="db_table_List_TitleRowClassName";
	//标题栏列样式名称
	public static String TITLE_COL_CLASS_NAME="db_table_List_TitleColClassName";
	
	//标题栏整体样式名称
	public static String CONTENT_ROW_CLASS_NAME="db_table_List_ContentRowClassName";
	//标题栏列样式名称
	public static String CONTENT_COL_CLASS_NAME="db_table_List_ContentColClassName";
	
	//链接按钮样式
	public static String LINK_CLASS_NAME="db_table_List_LinkClassName";
	
	
	//控件的名称
	public String CtlName="";
	
	//查询结果集的SQL语句
	private String mSQL="";
	
	//每页显示的行数，默认为10行
	private int mPageNumber=10;
	
	//列集合
	private HashMap<String, IDBTableCol> mColList=new HashMap<String, IDBTableCol>();
	
	//数据集合
	private MapList mTableData=null;
	
	private DB mDB=null;
	private HttpServletRequest mRequest=null;
	private String mThisPagePath="";
	
	private long mThisPageNumber=0;
	
	//添加列的全局计数
	public int mColNumber=0;
	
	public DBTableListManager(String tSql
			,int pageNum
			,DB db
			,String thisFilePath
			,HttpServletRequest request) throws JDBCException
	{
		init("DB_TLM01",tSql,pageNum,db,thisFilePath,request);
	}
	//构造函数
	public DBTableListManager(String name,String tSql
			,int pageNum
			,DB db
			,String thisFilePath
			,HttpServletRequest request) throws JDBCException
	{
		init(name,tSql,pageNum,db,thisFilePath,request);
	}
	
	public void init(String name,String tSql
			,int pageNum
			,DB db
			,String thisFilePath
			,HttpServletRequest request) throws JDBCException
	{
		CtlName=name;
		mDB=db;
		mPageNumber=pageNum;
		mRequest=request;
		mThisPagePath=thisFilePath;
		
		//获得当前页数
		String pagenum=request.getParameter("ThisPageNumber");
		
		if(pagenum!=null)
		{
			mThisPageNumber=Long.decode(pagenum);
		}
		
		//获取数据，截取页数
		mTableData=db.query(tSql + " LIMIT " + pageNum + " OFFSET " + (mThisPageNumber * pageNum));
	}
	
	//构建上下翻页Html
	public String getDataPageHtml(String preHtml,String nexHtml)
	{
		if(preHtml.length()==0)
			preHtml="上页";
		
		if(nexHtml.length()==0)
			nexHtml="下页";
		
		String sHtml="";
		
		long prePageNum=mThisPageNumber-1;
		long nextPageNum=mThisPageNumber+1;
		
		
		String prePageHtml="";
		if(prePageNum>=0)
		{
			prePageHtml="<a class='" + DBTableListManager.LINK_CLASS_NAME + "'";
			prePageHtml+=" href='" + mThisPagePath + "?ThisPageNumber=" + prePageNum + "');\">" + preHtml + "</a>";
		}
		
		String nextPageHtml="";
		//if(!Checker.isEmpty(mTableData))
		if(this.mTableData.size()>=mPageNumber)
		{
			nextPageHtml="<a class='" + DBTableListManager.LINK_CLASS_NAME + "'";
			nextPageHtml+=" href='" + mThisPagePath + "?ThisPageNumber=" + nextPageNum + "');\">" + nexHtml + "</a>";
		}
		
		sHtml=prePageHtml + " &nbsp; " + nextPageHtml;
		
		return sHtml;
	}
	
	//添加列对象
	public void addCol(IDBTableCol dtc)
	{
		mColList.put( mColNumber + "", dtc);
		
		mColNumber++;
	}
	
	/*
	 * 传入不同的IDBTableRow实现类，获得相应HtmlTable
	 * 支持：HtmlTableRow ， HtmlItemRow 
	 */
	public String getHtml(IDBTableRow dtr)
	{
		String sHtml="";
		
		dtr.init(mColList);
		
		sHtml+=dtr.getHeadHtml();
		sHtml+=dtr.getTitleHtml();

		if(!Checker.isEmpty(mTableData))
		{
			for(int i=0;i<mTableData.size();i++)
			{
				sHtml+=dtr.getHtml(mTableData.getRow(i), i,this);
			}
		}
		
		sHtml+=dtr.getFootHtml();
		
		return sHtml;
	}
	
	/*
	 * 返回默认表格方式的Table
	 */
	public String getHtml(String StyleClassPath)
	{
		return getHtml(new HtmlTableRow());
	}
	
	//返回表数据集合
	public MapList getTableData()
	{
		return mTableData;
	}
}
