package com.am.mall.iwebAPI;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * @author Mike
 * @create 2014年12月23日
 * @version 
 * 说明:<br />
 * 获取商品分类
 */
public class GetMallClassIWebAPI implements IWebApiService {

	/**先查所有的一级分类**/
	String getOneClass="SELECT id,title,upid,orgcode,CommodityClassState FROM mall_commodityclass WHERE upid='1'";
	/**查询所有的二级分类*/
	String getTowClass="SELECT id,title,upid,orgcode,CommodityClassState FROM mall_commodityclass WHERE upid IN ("
			+ " SELECT id FROM  mall_commodityclass WHERE upid='1')";
	/**查询所有的三级分类**/
	String getTheClass="SELECT id,title,upid,orgcode,CommodityClassState FROM mall_commodityclass WHERE upid IN ( "
			+ "SELECT id FROM mall_commodityclass WHERE upid IN ( "
			+ "SELECT id FROM  mall_commodityclass WHERE upid='1' ))";
	
	String getMallSQL=
			"SELECT onwClass.id AS onwid,onwClass.title AS onwtitle,onwClass.upid AS onwUpid,"+
					"       theClass.id AS theID,theClass.title AS theTitle,theClass.upid AS theUpid "+
					"	FROM mall_commodityclass AS onwClass INNER JOIN ("+
					"	SELECT id,upid FROM mall_commodityclass WHERE upid IN ( SELECT id FROM  mall_commodityclass WHERE upid='1' )"+
					") AS towClass ON onwClass.id=towClass.upid "+
					"	INNER JOIN ("+
					"	SELECT id,title,upid,orgcode,CommodityClassState FROM mall_commodityclass WHERE upid IN ("+
					"	SELECT id FROM mall_commodityclass WHERE upid IN ("+
					"	SELECT id FROM  mall_commodityclass WHERE upid='1'"+
					"		)"+
					"	)	"+
					"	)AS theClass ON theCLass.upid=towClass.id WHERE theClass.CommodityClassState='1' "+
					"	ORDER BY onwid";
	
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		String orgcode=request.getParameter("orgcode");
		
		JSONObject result=new JSONObject();
		DB db =null;
		try{
			db=DBFactory.newDB();
			
			MapList map=db.query(getMallSQL);
		
			JSONArray array=new JSONArray();
			
			Map<String,String> onwClass=new HashMap<String,String>();
			
			if(!Checker.isEmpty(map)){
				
				for(int i=0;i<map.size();i++){
					JSONObject item=new JSONObject();
					Row row=map.getRow(i);
					
					item.put("onwid",row.get("onwid"));
					item.put("onwtitle",row.get("onwtitle"));
					item.put("onwupid",row.get("onwupid"));
					item.put("theid",row.get("theid"));
					item.put("thetitle",row.get("thetitle"));
					item.put("theupid",row.get("theupid"));
					array.put(i, item);
					
					onwClass.put(row.get("onwid"),row.get("onwtitle"));
				}
			}
			
			Iterator<String> iter=onwClass.keySet().iterator();
			
			JSONArray mallClass=new JSONArray();
			
			while(iter.hasNext()){
				 String onwid = iter.next();
				 
				 JSONObject parent=new JSONObject();
				 parent.put("onwid", onwid);
				 parent.put("onwtitle",onwClass.get(onwid));
				 parent.put("childs", new JSONArray());
				 
				 for(int i=0;i<array.length();i++){
					 if(array.getJSONObject(i).get("onwid").equals(onwid)){
						 parent.getJSONArray("childs").put(array.getJSONObject(i));
					 }
				 }
				 
				 mallClass.put(parent);
			}
			
		result.put("DATA", mallClass);
		
		}catch(Exception e){
			
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		
		return result.toString();
	}
	
}
