package com.am.instore.action;

import java.util.List;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月29日
 *@version
 *说明：出库管理保存Action
 */
public class OutStoreSaveAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// 买方帐号
		String buyerAccount = ac.getRequestParameter("p2p_outstorage.form.buyer_account");
		
		String buyerAccountSql = "SELECT  * FROM mall_account WHERE loginaccount = '"+buyerAccount+"' ";
		MapList buyerAccountMap =db.query(buyerAccountSql);
		//判断买方帐号是否存在
		if(Checker.isEmpty(buyerAccountMap)){
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("买方帐号不存在");
			return;
		}
		
		//出库单
		Table table=ac.getTable("P2P_OUTSTORE");
		
		db.save(table);
		//主表主键
		String id=table.getRows().get(0).getValue("id");
		//获取子表
		Table childTable=ac.getTable("MALL_OUT_STORE");
		
		for(int i=0;i<childTable.getRows().size();i++){
			childTable.getRows().get(i).setValue("out_store", id);
		}

		db.save(childTable);
		//子表
		List<TableRow> listChildTable = childTable.getRows();

		for (int i = 0; i < listChildTable.size(); i++) {

		// 获取子表平均单价
		double outpriceYuan = childTable.getRows().get(i).getValueDouble("outprice_yuan",0);
			
		//更新子表单价值   （将元转换成分）
		String updateChildTableSQL="UPDATE mall_out_store SET outprice=" +outpriceYuan+"*100 where id='"+listChildTable.get(i).getValue("id")+"' ";
		db.execute(updateChildTableSQL);
		}
		//更新主表总价
		String updateSQL=" UPDATE p2p_outstore SET total_amount=(SELECT sum(counts*outprice) FROM mall_out_store WHERE out_store='"+id+"') WHERE id='"+id+"'";
		db.execute(updateSQL);
		
		ac.setSessionAttribute("am_bdp.p2p_outstorage.form.id", id);
	}
}
