package com.am.mall.iwebAPI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * @author Mike
 * @create 2014年12月26日
 * @version 
 * 说明:<br />
 * 根据分类ID获取分类的商品数据集合
 */
public class GetActivityMallListDataWebApi implements IWebApiService {

	private String getMallSQL=
			"SELECT to_char(price,'FM999999999.90') AS tprice,"+
			"	* FROM mall_commodity WHERE id IN ( "+
			"	SELECT commodityid FROM mall_CommodityClassRelationship  AS ccrp "+
			"	LEFT JOIN mall_activities AS at ON at.commodityclassid=ccrp.commodityclassid  "+
			"	WHERE at.id=? "+
			"	) AND commoditystate='1' ";
		
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		JSONObject result=new JSONObject();
		String activeId=request.getParameter("activityid");//活动ID
		DB db = null;
		try{
			db=DBFactory.newDB();
			
			MapList map=db.query(getMallSQL,activeId,Type.VARCHAR);
			
			JSONArray array=new JSONArray();
			
			if(!Checker.isEmpty(map)){
				for(int i=0;i<map.size();i++){
					
					JSONObject item=new JSONObject();
					Row row=map.getRow(i);
					
					item.put("id",row.get("id"));
					item.put("name",row.get("name"));
					item.put("pcmainimages",row.get("pcmainimages"));
					item.put("pclistimage",row.get("pclistimage"));
					item.put("salenumber",row.get("salenumber"));
					item.put("price",row.get("price"));
					item.put("abstract",row.get("abstract"));
					item.put("tprice",row.get("tprice"));
					
					array.put(i, item);
				}
			}
			
			result.put("DATA", array);
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
