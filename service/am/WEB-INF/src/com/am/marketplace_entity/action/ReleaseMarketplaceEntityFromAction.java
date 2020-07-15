package com.am.marketplace_entity.action;


import org.json.JSONArray;
import com.am.common.util.FileUtils;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

public class ReleaseMarketplaceEntityFromAction extends DefaultAction{
	@Override
	public void doAction(DB db, ActionContext ac)throws Exception{
		Table commodityTable=ac.getTable("MALL_MARKETPLACE_ENTITY");
		db.save(commodityTable);
		
		String id = commodityTable.getRows().get(0).getValue("id");
		
		String price = commodityTable.getRows().get(0).getValue("price");;
		long prices = 0L;
		if(!Checker.isEmpty(price))
		{
			prices = Long.parseLong(price)*100;
		}
		
		String mainimages=new FileUtils().getFastUnitFilePathJSON("MALL_MARKETPLACE_ENTITY", "bdp_list_images", id);
		
		String arr [] = null;

	
		JSONArray jsoArr = new JSONArray(mainimages);
		
		String str = "";
		
		for (int i = 0; i < jsoArr.length(); i++) {
			
			str += jsoArr.getJSONObject(i).get("path")+",";
			
		}
		
		
		if(!Checker.isEmpty(str))
		{
			str = str.substring(0, str.length()-1);
		}
	
//		if(mainimages.indexOf(",")>0)
//		{str
//			arr = mainimages.split(",");
//			
//			for(int i = 0; arr.length>0;i++)
//			{
//				obj.put("path", arr[i]);
//				obj.put("name", arr[i].split(".")[1]);
//				jsoArr.put(obj);
//			}
//			
//		}
		
		String updateSql="UPDATE MALL_MARKETPLACE_ENTITY  SET list_images='"+str+"',price="+prices+" WHERE id='"+id+"' ";
			
		db.execute(updateSql);
	}
}
