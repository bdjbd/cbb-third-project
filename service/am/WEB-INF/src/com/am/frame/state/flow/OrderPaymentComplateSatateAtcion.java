package com.am.frame.state.flow;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.bonus.service.PlatformConsumption;
import com.am.frame.state.OrderFlowParam;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年5月24日
 * @version 
 * 说明:<br />
 * 订单支付完成Action
 */
public class OrderPaymentComplateSatateAtcion extends DefaultOrderStateAction {

	@Override
	public String execute(OrderFlowParam ofp) {
		
		JSONObject result=new JSONObject();
		
		String res=super.execute(ofp);
		
		DB db=null;
		
		try{
			 db=DBFactory.newDB();
			
			//更新订单支付时间
			String updatePaymentDateSQL="UPDATE mall_MemberOrder SET paymentdate=now() WHERE id=? ";
			
			db.execute(updatePaymentDateSQL,ofp.orderId,Type.VARCHAR);
			
			result=new JSONObject(res);
			
			//计算销售额
			PlatformConsumption platformConsumption=new PlatformConsumption();
			platformConsumption.calculateTotalMoney(ofp.orderId);
			
			//呼旅网，减库存
//			minusStore(db,ofp.orderId);
			
			
		}catch(Exception e){
			e.printStackTrace();
			
			try {
				result.put("CODE",0);
				result.put("ERRCODE",1);
				result.put("MSG",e.getMessage());
				result.put("SUCCESS",false);
				result.put("STATE",ofp.stateValue);
			} catch (JSONException e1) {
				e1.printStackTrace();
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
	
	
	/**
	 * 减库存
	 * @param db
	 * @param orderId
	 * @throws JDBCException 
	 */
	private void minusStore(DB db, String orderId) throws JDBCException {
		String qeruySQL="SELECT salenumber,specid FROM mall_MemberOrder WHERE id=?";
		MapList map=db.query(qeruySQL,orderId, Type.VARCHAR);
		if(!Checker.isEmpty(map)){
			Row row=map.getRow(0);
			int saleNumber=row.getInt("salenumber", 1);
			String specId=row.get("specid");
			String updateSQL="UPDATE  mall_commodityspecifications SET stock=stock-? WHERE id =? ";
			db.execute(updateSQL,new String[]{
					saleNumber+"",specId
			}, new int[]{
				Type.INTEGER,Type.VARCHAR	
			});
			
		}
		
		
	}
	
}
