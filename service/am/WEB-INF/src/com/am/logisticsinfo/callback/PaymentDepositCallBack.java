package com.am.logisticsinfo.callback;

import org.json.JSONObject;

import com.am.frame.transactions.callback.AbstraceBusinessCallBack;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;

/**
 * 物流接单 支付押金回调
 * @author yuebin
 *
 */
public class PaymentDepositCallBack extends AbstraceBusinessCallBack {

	
	@Override
	public String callBackExec(String id, String business, DB db, String type) throws Exception {
		//1,更新物流订单信息
		//2,修改订单状态
		
		JSONObject reuslt=new JSONObject();
		
		if(checkTradeState(id, business, db, type)&&!checkProcessBuissnes(id, db)){
			//检查业务是否处理，如果没有处理，处理业务
			JSONObject businessJS=new JSONObject(business);
			
			//1
//			var params=new formatAmSqlParams();
//	      params.TABLENAME="mall_recv_logistics_info";
//	      var row = new formatAmSqlupdateRow();
//	      row.setID(uuid);
//	      row.setStatus("i");
//	      row.addColumn("member_id",  memberid);
//	      row.addColumn("r_type",  2);
//	      row.addColumn("order_id", orderId);
//	      row.addColumn("last_modify_time", 'now()');
//	      row.addColumn("recv_status",  1);
//	      row.addColumn("create_time", 'now()');
//	      params.addRow(row);
			
			
			Table table=new Table("am_bdp", "MALL_RECV_LOGISTICS_INFO");
			TableRow inertTr=table.addInsertRow();
			
			inertTr.setValue("member_id", businessJS.getString("member_id"));
			inertTr.setValue("r_type", 2);
			inertTr.setValue("order_id",businessJS.getString("order_id"));
			inertTr.setValue("recv_status", 1);
			inertTr.setValue("deposit", businessJS.getString("paymoney"));
			
			db.save(table);
			
			
			//2
			String updateSQL="UPDATE mall_logistics_info SET status=3 WHERE id=?";
			
			db.execute(updateSQL,new String[]{
					businessJS.getString("order_id")
			}, new int[]{
					Type.VARCHAR
			});
			
			updateProcessBuissnes(id, db,  "1");
		}
		
		
		return reuslt.toString();
	}

}
