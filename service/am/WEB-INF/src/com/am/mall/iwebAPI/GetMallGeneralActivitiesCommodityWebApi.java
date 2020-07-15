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
 * @create 2014年12月25日
 * @version 
 * 说明:<br />
 * 获取普通卖场的商品数据集合
 */
public class GetMallGeneralActivitiesCommodityWebApi implements IWebApiService {

	/**获取普通卖场的商品数据**/
	private String getDataSQL=
			"SELECT act.title,act.id AS actid,c.id,c.name,c.PCListImage,c.SaleNumber,c.Price  "+
			"	,to_char(price,'FM999999999.90') AS tprice"+
		    "	,c.Abstract ,act.commodityclassid "+
			"	FROM mall_commodity AS c "+
			"	INNER JOIN mall_CommodityClassRelationship AS ccr ON c.id=ccr.CommodityID "+
			"	INNER JOIN mall_activities AS act ON ccr.commodityclassid=act.commodityclassid "+
			"	WHERE act.ActivitiesState='1' AND act.activitiestype='0' AND act.enddate>now() "+
			"	ORDER BY startdate ";
	
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		JSONObject result=new JSONObject();
		DB db =null;
		try{
			db=DBFactory.newDB();
			
			MapList map=db.query(getDataSQL);
		
			JSONArray array=new JSONArray();
			
			Map<String,String> onwClass=new HashMap<String,String>();
			
			if(!Checker.isEmpty(map)){
				
				for(int i=0;i<map.size();i++){
					JSONObject item=new JSONObject();
					Row row=map.getRow(i);
					
					item.put("title",row.get("title"));
					item.put("actid",row.get("actid"));
					item.put("id",row.get("id"));
					item.put("name",row.get("name"));
					item.put("pclistimage",row.get("pclistimage"));
					item.put("salenumber",row.get("salenumber"));
					item.put("price",row.get("price"));
					item.put("tprice",row.get("tprice"));
					item.put("abstract",row.get("abstract"));
					item.put("commodityclassid",row.get("commodityclassid"));
					
					array.put(i, item);
					
					onwClass.put(row.get("actid"),row.get("title"));
				}
			}
			
			Iterator<String> iter=onwClass.keySet().iterator();
			
			JSONArray mallClass=new JSONArray();
			
			while(iter.hasNext()){
				 String actid = iter.next();
				 
				 JSONObject parent=new JSONObject();
				 parent.put("actid", actid);
				 parent.put("title",onwClass.get(actid));
				 parent.put("childs", new JSONArray());
				 
				 for(int i=0;i<array.length();i++){
					 if(array.getJSONObject(i).get("actid").equals(actid)){
						 parent.getJSONArray("childs").put(array.getJSONObject(i));
						 parent.put("commodityclassid",array.getJSONObject(i).get("commodityclassid"));
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
