package com.am.frame.state.flow;

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
 * 订单退货功能
 * 
 */
public class ReturnGoodsOrderStateAction extends DefaultOrderStateAction {

		@Override
		public String execute(OrderFlowParam ofp) {
			
			JSONObject params=new JSONObject();
			
			try{
				DBManager dbManager=new DBManager();
				
				//1,根据订单ID获取退货金额
				String  querySQL="SELECT trim(to_char(applyrefundmenoy,'999999990D99')) AS applyrefundmenoys , "+
								" trim(to_char(realrefundmenoy,'999999990D99')) AS realrefundmenoys,*  "+
								" from MALL_REFUND WHERE orderId='"+ofp.orderId+"' ";
				//2，调用退货流程接口
				MapList orderRetMap=dbManager.query(querySQL);
				
				
				String queryOrderSQL=" SELECT id,trim(to_char(totalprice,'99999999990D99')) AS totalprice, "
						+ " memberid,commodityid,mall_class,pay_id   "
						+ " FROM mall_MemberOrder WHERE id='"+ofp.orderId+"' ";
				
				MapList orderMap=dbManager.query(queryOrderSQL);
				
				if(!Checker.isEmpty(orderRetMap)){
					Row row=orderMap.getRow(0);
					
					params.put("orderid", ofp.orderId);
					params.put("stateValue", ofp.stateValue);
					params.put("failureStateVaule", ofp.failureStateVaule);
					params.put("pay_id",row.get("pay_id"));
					params.put("memberId",row.get("memberid"));
					params.put("commodityid",row.get("commodityid"));
					params.put("mall_class",row.get("mall_class"));
					params.put("totalprice",orderRetMap.getRow(0).get("realrefundmenoys"));//实际退款金额
					params.put("reason",row.get("applyreason"));//退款原因
					
					//更新订单状态到退款处理中状态
					//更新流程定义的SQL
					String updateSQL="UPDATE "+ofp.tableName+" SET "+ofp.stateFieldName+"='"+
							ofp.nextStateValue+"' WHERE "+
							ofp.keyName+"='"+ofp.keyValue+"'";
					
					dbManager.execute(updateSQL);
					
					RefundManager  refundManager=new RefundManager();
					refundManager.doRefundManager(null, null, params);
					
					//修改退货模块中的订单对应的状态为完成退货 state==4
					updateSQL="UPDATE mall_refund SET state='4' WHERE orderid='"+ofp.orderId+"' ";
					dbManager.execute(updateSQL);
					
				}
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
			return super.execute(ofp);
		}
	
		
		@Override
		public String executeFailure(OrderFlowParam ofp) {
			
			String updateSQL="UPDATE "+ofp.tableName+" SET "+ofp.stateFieldName+"='"+
					ofp.failureStateVaule+"' WHERE "+
					ofp.keyName+"='"+ofp.keyValue+"'";
			
			DBManager dbManager=new DBManager();
			
			dbManager.execute(updateSQL);
			
		    return super.executeFailure(ofp);
		}
		
}
