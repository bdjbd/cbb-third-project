package com.am.mall.iwebAPI;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
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
 * @create 2014年12月30日
 * @version 
 * 说明:<br />
 * 获取商品套餐信息，更加商品ID
 */
public class GetCommoditGroupSaleDataWebAPI implements IWebApiService {

	/**获取商品套餐商品信息**/
	private String getDataSQL=
			"SELECT 	c.id AS cid,c.name,c.pclistimage,"+
			" cgs.Price AS thisPrice, cgss.price AS gprice,"+
			" cgs.id AS sgroupId,cgs.title,cgs.explain,cgs.thiscommodityid,"+
			" cgss.GroupCommodityID"+
			" FROM mall_CommodityGroupSale AS cgs "+
			" LEFT JOIN mall_CommodityGroupsSaleSet AS cgss "+
			" ON cgs.id=cgss.CommodityGroupSaleID "+
			" LEFT JOIN mall_commodity AS c ON c.id=cgss.groupcommodityid "+
			" WHERE cgs.ThisCommodityID =?";
	
	
	/**获取当前商品信息**/
	private String getCurrentData="SELECT id,name,pclistimage FROM mall_commodity WHERE id=? ";
		
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		String commoditId=request.getParameter("COMMODITYID");//商品id
		
		//构造返回结果
		JSONObject result=new JSONObject();
		DB db =null;
		try
		{
			db=DBFactory.newDB();
			
			JSONArray array=new JSONArray();
			
			MapList map=db.query(getDataSQL,commoditId,Type.VARCHAR);
			Map<String,String> groupInfos=new HashMap<String,String>();
			
			if(!Checker.isEmpty(map)){
				
				for(int i=0;i<map.size();i++){
					JSONObject item=new JSONObject();
					Row row=map.getRow(i);
					
					item.put("cid",row.get("cid"));
					item.put("name",row.get("name"));
					item.put("pclistimage",row.get("pclistimage"));
					item.put("thisprice",row.get("thisprice"));
					item.put("gprice",row.get("gprice"));
					item.put("groupid",row.get("sgroupid"));
					
					array.put(i, item);
					groupInfos.put(row.get("sgroupid"),row.get("title"));
				}
				
				Iterator<String> iter=groupInfos.keySet().iterator();
				
				JSONArray groupInfo=new JSONArray();
				
				while(iter.hasNext()){
					 String groupid = iter.next();
					 
					 JSONObject parent=new JSONObject();
					 parent.put("groupid", groupid);
					 parent.put("title",groupInfos.get(groupid));
					 parent.put("childs", new JSONArray());
					 
					 for(int i=0;i<array.length();i++){
						 if(array.getJSONObject(i).get("groupid").equals(groupid)){
							 parent.getJSONArray("childs").put(array.getJSONObject(i));
						 }
					 }
					 
					 groupInfo.put(parent);
				}
				
				
				result.put("DATA", groupInfo);
				
				map=db.query(getCurrentData,commoditId,Type.VARCHAR);
				
				if(!Checker.isEmpty(map)){
					
					JSONObject currentComd=new JSONObject();
					Row row=map.getRow(0);
					
					currentComd.put("id",row.get("id"));
					currentComd.put("name",row.get("name"));
					currentComd.put("pclistimage",row.get("pclistimage"));
					
					result.put("CURRENTCOMD", currentComd);
				}
				
			}
			
		}catch(Exception e){
			
			try{
				result.put("COUNT",0);
				result.put("ERRCODE",1);
				result.put("ERRMSG",""+e.getMessage());
				result.put("PAYMONEY","");
				result.put("COMDITYNAME","");
				result.put("PAYID","");
				
			}catch(JSONException e1){
			}
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
