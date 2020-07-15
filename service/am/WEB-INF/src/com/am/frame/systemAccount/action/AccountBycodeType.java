package com.am.frame.systemAccount.action;

import org.json.JSONObject;

import com.am.frame.systemAccount.bean.TransactionBusinessBeans;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.MapListFactory;
import com.fastunit.util.Checker;

/** 
 * @author  wz  
 * @descriptions 
 * @date 创建时间：2016年4月20日 下午7:39:06 
 * @version 1.0   
 */
public class AccountBycodeType implements MapListFactory{

	@Override
	public MapList getMapList(ActionContext ac) {
		
		String  outAccountClass = ac.getRequestParameter("outaccountclass");
		TransactionBusinessBeans business = new TransactionBusinessBeans();
		try 
		{
			JSONObject jso = null;
			jso = business.getFormatBusiness(ac);
			
			if(jso!=null && jso.has("action_params"))
			{
				outAccountClass = jso.getJSONObject("action_params").getString("in_account_code");
			}
			
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if(!Checker.isEmpty(outAccountClass)){
			ac.setSessionAttribute("virementui.outaccountclass", outAccountClass);
		}else{
			outAccountClass =(String) ac.getSessionAttribute("virementui.outaccountclass");
		}
		
		
		String arr[] = null;
		
		DB db = null;
		
		MapList resultList =new MapList();
		
		
		try {
			
			db = DBFactory.newDB();
			
			if(!Checker.isEmpty(outAccountClass)){
				if(outAccountClass.indexOf(",")>0){
					
					arr = outAccountClass.split(",");
					
					for (int i = 0; i < arr.length; i++) {
						String sql ="select * from mall_system_account_class where 1=1";
						sql += "and sa_code='"+arr[i]+"'";
						
						MapList list = db.query(sql);
						
						resultList.put(i, "name", list.getRow(0).get("sa_name"));
						
						resultList.put(i, "value",list.getRow(0).get("sa_code"));
					}
					
				}else{
					
					String sql = "select * from mall_system_account_class where 1=1 and sa_code='"+outAccountClass+"'";
					
					MapList list = db.query(sql);
					
					resultList.put(0, "name",list.getRow(0).get("sa_name"));
					
					resultList.put(0, "value",list.getRow(0).get("sa_code"));
					
				}
			}
			
			
		} catch (JDBCException e) {
			e.printStackTrace();
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		
		return resultList;
	}


}
