package com.am.instore.action;

import org.json.JSONObject;

import com.am.instore.server.UpdateOutStoreInfoServer;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月29日
 *@version
 *说明：出库管理--确认出库Action
 */
public class OutStoreConfirmAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//出库ID
		String Id=ac.getRequestParameter("id");
		
		String querSql = " SELECT store.orgid,store.total_amount,store.total_amount/100 AS totalamountyuan,store.buyer_account,out.storeallocid,out.materialscode,out.outprice,out.counts  "
				+ "	FROM p2p_outstore AS store "
				+ " LEFT JOIN mall_out_store AS out ON store.id=out.out_store "
				+ " WHERE store.id='"+Id+"' ";
		MapList list = db.query(querSql);
		if (!Checker.isEmpty(list)) {
			//组织机构ID
			String orgId = list.getRow(0).get("orgid");
			//买方帐号
			String buyerAccount = list.getRow(0).get("buyer_account");
			//转账金额（分）
			String totalAmount = list.getRow(0).get("total_amount");
			//转账金额(元)
			String totalAmountYuan = list.getRow(0).get("totalamountyuan");
			//帐号
			String accountCode = "GROUP_CASH_ACCOUNT";
//			VirementManager memager = new VirementManager();
			//检查买方帐号余额是否充足
//			JSONObject json = memager.balance(buyerAccount,totalAmount,accountCode,db);
			
//			if("503".equals(json.get("code"))){
//				ac.getActionResult().setSuccessful(false);
//				ac.getActionResult().addErrorMessage("买家帐号"+json.getString("msg"));
//				return;
//			}else{
				for (int i = 0; i < list.size(); i++) {
					//物资编码
					String materialsCode = list.getRow(i).get("materialscode");
					//货位ID
					String storeAllocId = list.getRow(i).get("storeallocid");
					//数量
					String counts = list.getRow(i).get("counts");
					//单价(分)
					String outprice = list.getRow(i).get("outprice");
					
					UpdateOutStoreInfoServer server = new UpdateOutStoreInfoServer();
					//检查库存数量是否满足出库数量
					JSONObject jso = server.storeAllocInfo(materialsCode,storeAllocId,counts,db);
					
					if("403".equals(jso.get("code"))){
						ac.getActionResult().setSuccessful(false);
						ac.getActionResult().addErrorMessage(jso.getString("msg"));
						return;
					}else{
						//更新物资编码表的(减去)数量和出库单价
						server.updateMaterialsCode(materialsCode,counts,outprice,db);
						//更新货位库存信息表的数量(减去)
						server.updateStoreAllocInfo(materialsCode,counts,storeAllocId,db);
						//更新出库单状态
						server.updateOutStore(Id,db);
					}
				}
			}
//			String iremakers = "出库支出";
//			String oremakers = "出库收入";
			//入库帐号转账给供应商帐号
//			JSONObject json1 = memager.execute(db, buyerAccount, orgId, accountCode, accountCode, totalAmountYuan, iremakers, oremakers,"",true);
//		}
		
	}

}
