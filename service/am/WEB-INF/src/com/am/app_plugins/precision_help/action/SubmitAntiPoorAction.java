package com.am.app_plugins.precision_help.action;

import org.json.JSONObject;

import com.am.frame.systemAccount.SystemAccountClass;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * 扶贫资金分配 提交审核  action
 * 1,修改状态；
 * 2,新增一条转账审核记录
 * @author yuebin
 */

public class SubmitAntiPoorAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		//扶贫资金分配id
		String id=ac.getRequestParameter("id");
		
		//更新项目状态
		String updateSQL="UPDATE lxny_anti_poverty_funds_record SET settlement_state=1 WHERE id=? ";
		db.execute(updateSQL,id,Type.VARCHAR);
		
		
		//获取副主任
		String sql = "SELECT second_director,director FROM mall_auditpersonsetting WHERE orgid= '"+ac.getVisitor().getUser().getOrgId()+"' ";
		
		String second_director="";
		String director="";
		MapList list = db.query(sql);
		if(Checker.isEmpty(list)){
			//检查是否这是主管，副主管
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("没有设置主管和副主管！请先设置主管和副主管");
			return;
		}else{
			second_director=list.getRow(0).get("second_director");
			director=list.getRow(0).get("director");
		}
		
		
		//查询转账信息
		String querySQL="SELECT * FROM lxny_anti_poverty_funds_record WHERE id=? ";
		
		MapList map=db.query(querySQL,id,Type.VARCHAR);
		
		if(!Checker.isEmpty(map)){
			Row row=map.getRow(0);
			
			
			//查询账号ID信息
			querySQL="SELECT * FROM  mall_account_info "+
					" WHERE member_orgid_id='"+row.get("org_id")+"'  "+         
					" AND a_class_id IN(  "+
					" 	SELECT id FROM mall_system_account_class  "+
					"  WHERE sa_code='"+SystemAccountClass.GROUP_POVERTY_ACCOUNT+"' )";
			
			String out_account_id="";
			MapList accountMap=db.query(querySQL);
			if(!Checker.isEmpty(accountMap)){
				out_account_id=accountMap.getRow(0).get("id");
			}
			
			JSONObject business=new JSONObject();
			business.put("business_id", id);
			business.put("success_call_back", "com.am.app_plugins.precision_help.callback.AntiPoorFundsRecrodCallBack");
			business.put("orders", id);
			business.put("remarks",row.get("remarks"));
			
			
			Table trTable=new Table("am_bdp","MALL_TRANSFER");
			TableRow trInster=trTable.addInsertRow();
			
			trInster.setValue("member_id",row.get("org_id"));
			trInster.setValue("out_account_id", out_account_id);
			trInster.setValue("account_type", "2");
			trInster.setValue("cash_withdrawal",row.get("anti_total_amount"));//提现金额
			trInster.setValue("settlement_state", "1");//提现状态
			trInster.setValue("operation_user",row.get("operationer"));//提现操作人
			trInster.setValue("remarks",row.get("remarks"));//备注
			trInster.setValue("business",business.toString());//business
			trInster.setValue("second_director",second_director);//,副主任
			trInster.setValue("director",director);//主任
			trInster.setValue("audit_person",second_director);//主任
			trInster.setValue("audit_state",1);
			
			db.save(trTable);
		}
		
		
		
	}
	
}
