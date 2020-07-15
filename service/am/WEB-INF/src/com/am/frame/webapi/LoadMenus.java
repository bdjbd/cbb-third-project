package com.am.frame.webapi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.DataService;
import com.p2p.service.IWebApiService;

/*
 * 获得菜单JSON数据
 * */
public class LoadMenus implements IWebApiService 
{
	private int mRowCount=0;
	private String mPrefix="    ";
	
	//终端类型。目前有 pc_html  pcWEB端,ios,android,mobile_html 微信
	private String mPlatform="";
	private String mLayer="one"; //返回数据的层级
	private String mFieldName="upid";
	
	//声明日志对象
	//FATAL       0  
	//ERROR       3  
	//WARN        4  
	//INFO        6  
	//DEBUG       7 
	//使用方法：
	//每个类上方定义 ： 
	// final Logger logger = LoggerFactory.getLogger(LoadMenus.class);
	// 具体输入日志时，依据输出信息的等级调用不同方法，具体第一如下：
	// logger.debug(arg0); 输出调试信息，程序的调试信息，试运行及投入生产后就会关闭；
	// logger.info(arg0);  输出提示信息，程序试运行时所需要展示的信息，观察程序运行是否正常；
	// logger.warn(arg0);  输出警告信息，不使用；
	// logger.error(arg0); 输出错误信息，try cach 中的报错信息从这里输出；
	// logger.trace(arg0); 输出失败信息，不使用；
	
	
	final Logger logger = LoggerFactory.getLogger(LoadMenus.class);
	
	
	@Override
	public String execute(HttpServletRequest request,HttpServletResponse response)
	{
		mFieldName=request.getParameter("field_name");
		String tUpID=request.getParameter("up_id");
		String tOrgID=request.getParameter("org_id");
		
		mPlatform=DataService.getRequestValue(request.getParameter("platform")).toLowerCase();
		mLayer=DataService.getRequestValue(request.getParameter("layer"));
		
		String tStr=loadTreeData(tUpID,tOrgID,"");
		
		String tUpidStr=",\"" + "THIS_MENU" + "\" : ";
		
		if(mFieldName.equals("upid"))
			tUpidStr+=loadTableData("am_mobliemenu",tUpID,tOrgID,"id") + "";
		else
			tUpidStr+=loadTableData("am_mobliemenu",tUpID,tOrgID,mFieldName) + "";
		
		String tJsonString="{\"COUNT\" : \"" + mRowCount + "\",\"DATA\" : [" + tStr + "]" + tUpidStr + "}";
		
		return tJsonString;
	}
	
	
	//读取菜单及其下级菜单 菜单终端类型为 all 或者当前请求类型
	private String loadTreeData(String upID,String orgID,String str1)
	{
		String tSql="select * from am_mobliemenu where " + mFieldName + "='" + upID + "' and (terminaltypename='"
				+ mLayer + "' or terminaltypename LIKE '%"+ mPlatform +"%' ) order by menusort";
		String tSql_1="";
		String rValue=""; //{"COUNT":"1","DATA":[{"" : "","" : "","":""}]}";
		DB db = null;
		try 
		{
			logger.debug("LoadMenus.loadTreeData().tSql=" + tSql);
			
			db = DBFactory.newDB();
			
			MapList map=db.query(tSql);
			
			logger.debug("LoadMenus.loadTreeData().map.size()=" + map.size());

			int tIsOneRow=0;
			for(int i=0;i<map.size();i++)
			{
				if(tIsOneRow>0)
					rValue+=",";
				
				tIsOneRow++;
				
				rValue+="{" + getJsonField(map.getKey(0),map.getRow(i).get(0));
				for(int l=1;l<map.getRow(i).size();l++)
					rValue+="," + getJsonField(map.getKey(l),map.getRow(i).get(l));
				
				String tID=map.getRow(i).get("id");
				
				//构建 am_menuparame ，菜单参数
				rValue+=",\"" + "MENU_PARAME" + "\" : [";
				rValue+=loadTableData("am_menuparame",tID,orgID,"am_mobliemenuid") + "]";
				
				//构建 am_menupluginnameofterminal ，插件对应终端参数
				rValue+=",\"" + "MENU_PLUGIN" + "\" : [";
				rValue+=loadTableData("am_menupluginnameofterminal",tID,orgID,"am_mobliemenuid") + "]";
				
				//构建下级菜单，当为all时加载全部层级
				if(mLayer.equals("all"))
				{
					rValue+=",\"" + "CHILD_MENUS" + "\" : [";
					rValue+=loadTreeData(tID,orgID,str1 + mPrefix) + "]";
				}
				
				//选择设备后的 PluginName
				tSql_1="select pluginname from am_menupluginnameofterminal where am_mobliemenuid='" + tID + "'"
						+ " and terminaltypename='" + mPlatform + "'";
				rValue+="," + getJsonField("PLUGIN_NAME_OF_DIRVE"
						,getDBTableTopRowField(tSql_1, db,map.getRow(i).get("pluginname")));
				
				rValue+="," + getJsonField("PREFIX_STRING",str1);
				rValue+="}";
				
				mRowCount++;
			}
		}
		catch (JDBCException e)
		{
			e.printStackTrace();
			logger.error(e.getMessage());
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		
		return rValue;
	}
	
	public static String loadTableDataOfSql(String sql)
	{
		String rValue=""; 
		try 
		{
			//logger.debug("LoadMenus.loadTableData().tSql=" + tSql);
			
			DBManager db=new DBManager();
			
			MapList map=db.query(sql);
			
			//logger.debug("LoadMenus.loadTableData().map.size()=" + map.size());
			
			int rowCount=0;
			for(int i=0;i<map.size();i++)
			{
				if(rowCount>0)
					rValue+=",";
				
				rValue+="{" + getJsonField(map.getKey(0),map.getRow(i).get(0));
				for(int l=1;l<map.getRow(i).size();l++)
					rValue+="," + getJsonField(map.getKey(l),map.getRow(i).get(l));
				
				rValue+="}";
				
				rowCount++;
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return rValue;
	}
	
	public static String loadTableData(String tablename,String id,String orgID,String keyName)
	{
		String tSql="select * from " + tablename + " where " + keyName + "='" + id + "'";
		return loadTableDataOfSql(tSql);
	}
	
	public static String getJsonField(String name,String value)
	{
		String rValue="\"" + name.toUpperCase() + "\"";
		rValue+=":\"" + value + "\"";
		
		return rValue;
	}
	
	//获得结果集中首行的第一个字段值
	public String getDBTableTopRowField(String sql, DB db,String dfValue)
			throws JDBCException 
	{
		String rValue = dfValue;
		MapList map = db.query(sql);

		if (!Checker.isEmpty(map))
			rValue = map.getRow(0).get(0);

		return rValue;
	}

}
