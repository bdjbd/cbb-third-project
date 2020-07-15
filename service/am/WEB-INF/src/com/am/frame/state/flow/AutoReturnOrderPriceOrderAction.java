package com.am.frame.state.flow;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.payment.RefundManager;
import com.am.frame.state.OrderFlowParam;
import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年7月2日
 * @version 
 * 说明:<br />
 * 订单自动退款流程
 */
public class AutoReturnOrderPriceOrderAction extends DefaultOrderStateAction {

	@Override
	public String execute(OrderFlowParam ofp) {
		//1,查询订单信息
		
		JSONObject params=new JSONObject();
		
		try {
			DBManager dbManager=new DBManager();
			
			String queryOrderSQL=" SELECT id,trim(to_char(totalprice,'99999999990D99')) AS totalprice, "
					+ " memberid,commodityid,mall_class,pay_id   "
					+ " FROM mall_MemberOrder WHERE id='"+ofp.orderId+"' ";
			
			MapList orderMap=dbManager.query(queryOrderSQL);
			
			if(!Checker.isEmpty(orderMap)){
				Row row=orderMap.getRow(0);
				
				params.put("orderid", ofp.orderId);
				params.put("stateValue", ofp.stateValue);
				params.put("failureStateVaule", ofp.failureStateVaule);
				params.put("pay_id",row.get("pay_id"));
				params.put("memberId",row.get("memberid"));
				params.put("commodityid",row.get("commodityid"));
				params.put("mall_class",row.get("mall_class"));
				params.put("totalprice",row.get("totalprice"));
				params.put("reason","客户申请自动退款");
				
				
				//更新订单状态到退款处理中状态
				//更新流程定义的SQL
				String updateSQL="UPDATE "+ofp.tableName+" SET "+ofp.stateFieldName+"='"+
						ofp.nextStateValue+"' WHERE "+
						ofp.keyName+"='"+ofp.keyValue+"'";
				
				dbManager.execute(updateSQL);
				
				RefundManager  refundManager=new RefundManager();
				refundManager.doRefundManager(null, null, params);
				
			}else{
				logger.info("订单退单，订单 找不到对应数据,订单ID："+ofp.orderId);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		return "{}";
	}
	
}
