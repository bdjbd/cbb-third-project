package com.am.app_plugins.rural_market.my_booth;

import org.json.JSONObject;

import com.am.frame.transactions.callback.AbstraceBusinessCallBack;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;


/**
 * 农场大市场扣除手续费回调界面
 * 
 * 1，从现金账号扣除手续费，并将费用转入到抗风险自救金账号中
 * 
 * @author yuebin
 *
 */
public class MarketPublishBusinessCall extends AbstraceBusinessCallBack {

	@Override
	public String callBackExec(String id, String business, DB db, String type) throws Exception {
		
		//mall_marketplace_entity  
		JSONObject result=new JSONObject();
		
		if(checkTradeState(id, business, db, type)&&!checkProcessBuissnes(id, db)){
			
			JSONObject businessJS=new JSONObject(business);
			
			String upateSQL="UPDATE mall_marketplace_entity SET status=?,audit_fee=? WHERE id=? ";
			
			db.execute(upateSQL,new String[]{
					businessJS.getString("start"),businessJS.getString("paymoney"),businessJS.getString("uuid")
			},new int[]{Type.INTEGER,Type.INTEGER,Type.VARCHAR});
			
		}
		
		return result.toString();
	}

}
