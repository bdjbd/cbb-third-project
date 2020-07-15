package com.am.hc.data.other;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.p2p.service.IWebApiService;
/**
 * 数据保存类
 * @author guorenjie
 * 2017-06-04
 */
public class DefaultDataSave implements IWebApiService {
	private static final Logger log = LoggerFactory.getLogger(DefaultDataSave.class);
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		String account = request.getParameter("account");
		String password = request.getParameter("password");
		String data = request.getParameter("data");
		String token = request.getParameter("token");
		String str = "account="+account+" password="+password+" data="+data+" token="+token;
		
		//解析json
		JSONObject obj = JSONObject.fromObject(data);
		
		String fun = obj.getString("fun");
		String card_id = obj.getString("card_id");
		String year = obj.getString("year");
		String month = obj.getString("month");		
		String row = obj.getString("row");
		
		//移除不需要返回的參數
		obj.remove("fun");
		obj.remove("card_id");
		obj.remove("year");
		obj.remove("month");
		obj.remove("row");
		//sql查询条件
		String where ="where card_id='" + card_id + "'";
		if (!("".equals(year))) {
			where+="and year = '"+year+"'";
		}if (!("".equals(month))) {
			where+="and month = '"+month+"'";
		}
		//查询数据是否存在，存在则更新，否则新增
		String tSql="select * from " + fun+" "+where;
		String updateColumn = "";
		String columns = "";
		String values = "";		 
		//解析json数据，将参数拼入sql				
		JSONObject row1 = JSONObject.fromObject(row);
		Iterator it = row1.keys();
		while (it.hasNext()) {
			String key = (String) it.next();
			String value = row1.getString(key);
			updateColumn+=key+"='"+value+"'";
			columns+=key;
			values+="'"+value+"'";
			if (it.hasNext()) {
				updateColumn+=",";
				columns+=",";
				values+=",";
			}
		}
				  
		DBManager db = new DBManager();
		//更新sql以及新增sql
		String updateSql="UPDATE "+fun+" SET "+updateColumn+" "+where;
		String insertSql = "insert into "+ fun+"(id,"+columns+",card_id"+",year"+",month"+")"+ " values "+"('"+UUID.randomUUID()+"',"+values+","+card_id+","+year+","+month+")";
		String calling_time="";
		//根据身份证号以及年月参数查询数据是否存在，存在则更新，否则新增
		MapList mlData =db.query(tSql);
		if(mlData.size()>0)
		{
			int number = 0;//修改行数
			number = db.execute(updateSql);
			calling_time = getCurrentTime();
			//当修改行数大于0时操作成功，否则操作失败
			if (number>0) 
			{
				obj.put("CODE", "3");
				obj.put("MSG", "更新成功");
				obj.put("number",number );
				
			}else 
			{
				obj.put("CODE", "1");
				obj.put("MSG", "操作失败");
				obj.put("number", number);
			}
			
		}
		else 
		{
			int number = 0;
			number = db.execute(insertSql);
			calling_time = getCurrentTime();
			if (number>0) 
			{
				obj.put("CODE", "2");
				obj.put("MSG", "新增成功");
				obj.put("number", number);
				
			}else 
			{
				obj.put("CODE", "1");
				obj.put("MSG", "操作失败");
				obj.put("number", number);
			}
		}
		String return_time = getCurrentTime();
		dataRecord(db, account, fun, str, calling_time, return_time, obj);
		return obj.toString();
	}
	/**
	 * 获取当前时间
	 * @return
	 */
	private String getCurrentTime()
	{
		Date date = new Date();
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sFormat.format(date);
		return time;
	}
	/**
	 * 接口数据记录
	 * @param db
	 * @param account	白名单账号
	 * @param fun	表名
	 * @param row	入参
	 * @param calling_time	调用时间
	 * @param return_time	返回时间
	 * @param obj	回参
	 */
	private void dataRecord(DBManager db,String account,String fun,String str,String calling_time,String return_time,JSONObject obj)
	{
		String white_idsql ="select id from AM_OTHERSYSTEMWHITE where account='" + account + "'";
		String white_id ="";
		MapList mapList = db.query(white_idsql);
		if (mapList.size()>0) {
			white_id = mapList.getRow(0).get(0);
		}
		String infoSql ="insert into AM_OTHERSYSTEM_INFO (id,white_id,method_name,interface_data,calling_time, return_parameter, return_time) values ("+"'"+UUID.randomUUID()+"','"+white_id+"','"+fun+"','"+str+"','"+calling_time+"'";		
		infoSql+=",'"+obj.toString()+"','"+return_time+"')";
		db.execute(infoSql); 
	}
}
