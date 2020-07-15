package com.am.frame.webapi.service;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.ActionParame;
import com.am.frame.webapi.ExecuteRes;
import com.am.frame.webapi.IExecuteResAfter;
import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * 执行sql
 * @author mac
 *
 */
public class ExecuteResService 
{
	
	private static final Logger log = LoggerFactory.getLogger(ExecuteRes.class);
	
	/**
	 * 后台访问sql资源业务处理
	 * @param action  请求code
	 * @param params  参数
	 * @param pageSize 一页条数 可为空
	 * @param pageNumber 当前页
	 * @param requestMethod 请求方式 post 或 get put
	 * @return
	 * @throws Exception
	 */
	public JSONObject amExecuteRes(String action,String params,String pageSize,String pageNumber,String requestMethod) throws Exception
	{
		DBManager db = new DBManager();
		
		JSONObject reObj = null;
		
		String sql = "SELECT * FROM AM_RESOURCES WHERE 1=1 AND  RCODE='"+action+"'";
		MapList mapList = db.query(sql);
		
		//执行方法
		reObj = runAction(action,params,pageSize,pageNumber,requestMethod,mapList,mapList.getRow(0).get("rparames"),db);
		
		return reObj;
		
	}
	
	
	/**
	 * app端访问sql资源业务处理
	 * @param action  请求code
	 * @param params  参数
	 * @param pageSize 一页条数 可为空
	 * @param pageNumber 当前页
	 * @param requestMethod 请求方式 post 或 get put
	 * @param request 
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public JSONObject appExecuteRes(String action,String params,String pageSize,String pageNumber,String requestMethod,HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		DBManager db = new DBManager();
		
		JSONObject reObj = null;
		
		String sql = "SELECT * FROM AM_RESOURCES WHERE 1=1 AND  RCODE='"+action+"'";
		MapList mapList = db.query(sql);
		
		//执行前置方法
		if(!Checker.isEmpty(mapList.getRow(0).get("before_class"))){
			
			JSONObject beforeJson = new JSONObject();
			
			beforeJson.put("class", mapList.getRow(0).get("before_class"));
			beforeJson.put("param", mapList.getRow(0).get("before_parameter"));
			
			if(beforeJson.getJSONArray("param").length()>0){
				
				
				ActionParame actionParame = new ActionParame(beforeJson.getString("class"),request,response);
				
				actionParame.setParame(action, beforeJson.getString("param"));
				
			}
			
			IWebApiService  ibefore= (IWebApiService)Class.forName(beforeJson.getString("class")).newInstance();
			
			ibefore.execute(request,response);
		
		}
		
		//执行方法
		reObj = runAction(action,params,pageSize,pageNumber,requestMethod,mapList,mapList.getRow(0).get("rparames"),db);
		
		//执行后置方法
		if(!Checker.isEmpty(mapList.getRow(0).get("after_class")))
		{
			ActionParame actionParame=null;
			
			JSONObject afterJson = new JSONObject();
			afterJson.put("class", mapList.getRow(0).get("after_class"));
			JSONArray after_array = new JSONArray();
			after_array.put(mapList.getRow(0).get("after_parameter"));
			afterJson.put("param",after_array);
			log.debug("+++++++++++++++++++++++++++++++");
			log.info("-------------:"+afterJson.getJSONArray("param"));
			if(afterJson.getJSONArray("param").length()>0){
				
				actionParame = new ActionParame(afterJson.getString("class"),request,response);
				
				actionParame.setParame(action, afterJson.getString("param"));
			
			}
			
			IExecuteResAfter  iafter= (IExecuteResAfter)Class.forName(afterJson.getString("class")).newInstance();
			
			reObj=iafter.execute(request,response,actionParame,reObj);
			
		}
		
		return reObj;
		
	}
	
	
	/**
	 * 通过所请求的方法到数据库查询对于的class类，通过类反射调用
	 * @param method
	 * @return
	 */
	public JSONObject runAction(String action,String params,String pageSize,String pageNumber,String requestMethod,MapList mapList,String SQL,DBManager db){
		
		String paramSql = "";
		
		
		JSONArray resultArray = null;
		
		JSONObject resObj = new JSONObject();
		
		int rowCount = 0;
		
		int pageCount = 0;
	
		try {
			
//			db = DBFactory.newDB();
//			DBManager db = new DBManager();
			
			
			if(mapList.size()>0){
				
//				paramSql = mapList.getRow(0).get("rparames");
				paramSql = SQL;
				
				if(paramSql!=null){
					String id = mapList.getRow(0).get("id");
					
					String whereparams = "";
					
					if(paramSql.indexOf("${WHERE}")>-1)
					{
						JSONObject json = null;
						if(Checker.isEmpty(params))
						{
							json = new JSONObject("{}");
						}else
						{
							json = new JSONObject(params);
						}
						whereparams = getparamsResult(db,json,id);
						String expl="\\$\\{WHERE\\}";
						paramSql=paramSql.replaceAll(expl,whereparams);
					}
					
					//获得SQL模板，并替换相应值
					if(!Checker.isEmpty(params)){
						log.info("params:"+params);
						paramSql = getDataForSql(paramSql,new JSONObject(params));
						paramSql=StringEscapeUtils.unescapeHtml4(paramSql);
					}
					
					//进行分页操作
					if("POST".equals(requestMethod)&&!"none".equals(pageSize)&&!"none".equals(pageNumber)){
						
						String coutSql = "SELECT count(*) as count FROM ("+paramSql+") as tablecount_";
						
						MapList coutList = db.query(coutSql);
						
						rowCount = coutList.getRow(0).getInt("count", 0);
						
						int offset = 0; 
							
						pageCount = (rowCount + Integer.parseInt(pageSize) - 1) /Integer.parseInt(pageSize);
						
						offset = Integer.parseInt(pageSize)*(Integer.parseInt(pageNumber)-1);
						
						paramSql += " limit "+pageSize+" offset "+offset;
						
					}
					
					paramSql=StringEscapeUtils.unescapeHtml4(paramSql);
					log.info("资源明SQl:"+paramSql);
					
					//执行装配完成的Sql语句
					MapList paramList = db.query(paramSql);
					
					//将结果集转换为json对象
					resultArray = formatJSONArray(paramList);
					
					resObj.put("PAGECOUNT", pageCount);
					resObj.put("ROWCOUNT", rowCount);
					resObj.put("THISPAGENUMBER", pageNumber);
					resObj.put("CODE", "0");
					resObj.put("DATA", resultArray);
					
				}else{
					
					resObj.put("code", "4000");
					resObj.put("msg", "没有找到方法执行参数");
					
				}
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resObj;
	}
	
	
	
	
	/**
	 * 将maplist转换为jsonArray
	 * @param list
	 * @return
	 */
	public JSONArray formatJSONArray(MapList list){
		
		JSONArray returnJsonArray = new JSONArray();
		
		if(!Checker.isEmpty(list)){
			
			int row_count=list.size();
			try {
				
				for(int i=0;i<row_count;i++) 
				{
					Row row=list.getRow(i);
					
					int column_count=row.size();
					
					JSONObject jo = new JSONObject();
					
					for (int j =0; j<column_count;j++) {
						
						String currentValue=row.get(j);
						
						if (currentValue == null || currentValue.trim().equalsIgnoreCase("null")) {
							
								jo.put(row.getKey(j).toUpperCase(), "");
							
						} else {
							jo.put(row.getKey(j).toUpperCase(),currentValue);
						}
					}
				
					returnJsonArray.put(jo);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}		
		}
		return returnJsonArray;
	}
	
	/**
	 * 获取where结果
	 * @return
	 * @throws JDBCException 
	 * @throws JSONException 
	 */
	private String getparamsResult(DBManager db,JSONObject paramJson,String id) throws JDBCException, JSONException
	{
		String WhereText="";
		
		String SQL = "SELECT * FROM am_resources_list WHERE resourcesid = '"+id+"'";
		
		MapList rmapList = db.query(SQL);
		
		String rwvalue="";
		
		for (int i = 0; i < rmapList.size(); i++) {
			WhereText += getDataForparams(rmapList.getRow(i).get("rwvalue"),paramJson)+" ";
		}
		return WhereText;
	}
	
	
	/**
	 * 返回sql返回的数据集合
	 * @param params Controller参数
	 * @param paramsMap HttpServletRequest中的参数集合
	 * @return
	 */
	private String getDataForparams(String rvalue, JSONObject paramJson) {
		
		try{
			
			if(rvalue!=null){//参数为空
				
//				sql=sql.toUpperCase();
				
				Iterator set = paramJson.keys();
				
				while(set.hasNext()){
					//获取参数
					String key=(String)set.next();
					//获取可以对应的值
					String param=paramJson.get(key).toString();
					
					if(!Checker.isEmpty(param) && !"null".equals(param) && !"".equals(param))
					{
						//如果SQL中包含${参数} 将替换掉
						String expl="${"+key+"}";
						
						if(rvalue.indexOf(expl)>-1){
							
							expl="\\$\\{"+key+"\\}";
							
							rvalue=rvalue.replaceAll(expl,param);
						
						}
					}
					
					
				}
				
				if(rvalue.indexOf("${")>-1)
				{
					rvalue="";
				}
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return rvalue;
	}

	/**
	 * 返回sql返回的数据集合
	 * @param params Controller参数
	 * @param paramsMap HttpServletRequest中的参数集合
	 * @return
	 */
	private String getDataForSql(String sql, JSONObject paramJson) {
		
		try{
			
			if(sql!=null){//参数为空
				
//				sql=sql.toUpperCase();
				
				Iterator set = paramJson.keys();
				
				while(set.hasNext()){
					//获取参数
					String key=(String)set.next();
					//获取可以对应的值
					String param=paramJson.get(key).toString();
					
					//如果SQL中包含${参数} 将替换掉
					String expl="${"+key+"}";
					
					if(sql.indexOf(expl)>-1){
						
						expl="\\$\\{"+key+"\\}";
						
						sql=sql.replaceAll(expl,param);
					
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return sql;
	}
}
