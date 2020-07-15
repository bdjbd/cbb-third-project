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
 *@create 2016年4月27日
 *@version
 *说明：入库管理保存Action
 */
public class InstoreSaveAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// 供应商帐号
		String supportAccount = ac.getRequestParameter("p2p_instorage.form.support_account");
		
		// P2P_INSTORE.support_type is '供应商类型：1，生产者；2，组织';
		String supportType=ac.getRequestParameter("p2p_instorage.form.support_type");
		
		String supportAccountSql="SELECT  * FROM aorg WHERE 1=2 ";
		if("1".equals(supportType)){//1，生产者
			supportAccountSql = "SELECT  * FROM am_member WHERE loginaccount = '"+supportAccount+"' ";
		}else if("2".equals(supportType)){//机构
			supportAccountSql = "SELECT  * FROM aorg WHERE orgid = '"+supportAccount+"' ";
		}
		
		String orgId=ac.getVisitor().getUser().getOrgId();
		if(orgId.equals(supportAccount)){
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("供应商不能是自己。");
			return;
		}
		
		MapList supportAccountMap =db.query(supportAccountSql);
		//判断供应商帐号是否存在
		if(Checker.isEmpty(supportAccountMap)){
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("请填写正确的供应商账号。");
			return;
		}
		//入库单
		Table table=ac.getTable("P2P_INSTORE");
		
		db.save(table);
		//主表主键
		String id=table.getRows().get(0).getValue("id");
		//获取子表
		Table childTable=ac.getTable("MALL_INSTORE_LIST");
		
		for(int i=0;i<childTable.getRows().size();i++){
			childTable.getRows().get(i).setValue("in_store_id", id);
		}

		db.save(childTable);
		//子表
		List<TableRow> listChildTable = childTable.getRows();

		for (int i = 0; i < listChildTable.size(); i++) {

			// 获取子表评估单价
			double inpriceYuan = childTable.getRows().get(i).getValueDouble("inprice_yuan",0);
				
			//更新子表单价值   （将元转换成分）
			String updateChildTableSQL="UPDATE mall_instore_list SET inprice=" +inpriceYuan+"*100 where id='"+listChildTable.get(i).getValue("id")+"' ";
				db.execute(updateChildTableSQL);
			}
			//更新主表总价
			String updateSQL=" UPDATE p2p_instore SET total_amount=(SELECT sum(counts*inprice) FROM mall_InStore_list WHERE in_store_id='"+id+"') WHERE id='"+id+"'";
			db.execute(updateSQL);
		
			ac.setSessionAttribute("am_bdp.p2p_instorage.form.id", id);
	}
}
