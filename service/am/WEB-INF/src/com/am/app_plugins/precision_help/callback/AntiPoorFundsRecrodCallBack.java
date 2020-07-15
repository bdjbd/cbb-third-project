package com.am.app_plugins.precision_help.callback;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.callback.AbstraceBusinessCallBack;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.util.Checker;

public class AntiPoorFundsRecrodCallBack extends AbstraceBusinessCallBack{
	
	private Logger logger=LoggerFactory.getLogger(getClass());

	@Override
	public String callBackExec(String id, String business, DB db, String type) throws Exception {
		
		JSONObject result=new JSONObject();
		
		//1,检查交易是否成功
		if(checkTradeState(id, business, db, type)&&!checkProcessBuissnes(id, db)){
			
			JSONObject businessJS=new JSONObject(business);
			
			//更新还款日期和还款状态
			if(businessJS!=null){
				//业务ID
				String businessId=businessJS.getString("business_id");
				
				//1,查询分配记录素有用户
				//2,为平困户分配资金
				String querySQL="SELECT member_id,anti_amount,remarks,id "
						+ " FROM lxny_anti_pfunds_record_dateils WHERE record_id=? ";
				
				MapList memberMap=db.query(querySQL,businessId,Type.VARCHAR);
				if(!Checker.isEmpty(memberMap)){
					
					VirementManager vm=new VirementManager();
					String iremakers=businessJS.getString("remarks");
					JSONObject reuslt=new JSONObject();
					
					for(int i=0;i<memberMap.size();i++){
						Row row=memberMap.getRow(i);
						
						iremakers+=row.get("remarks");
						reuslt=vm.execute(db,"", row.get("member_id"),"",
								SystemAccountClass.CASH_ACCOUNT,
								VirementManager.changeF2Y(row.get("anti_amount"))+"",
								iremakers, "", "", false);
						
						logger.info("扶贫资金转账，member_id:"+row.get("member_id")+",金额："+row.get("anti_amount")+",转账结果："+reuslt);
					}
				}
				
				//3,更新支付时间
				String updateSQL="UPDATE lxny_anti_poverty_funds_record WHERE  payment_time=now() WHERE id=? ";
				db.execute(updateSQL, businessId,Type.VARCHAR);
				
			}
			
			updateProcessBuissnes(id, db, "1");
		}
		
		return result.toString();
	}


}

