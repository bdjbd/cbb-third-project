package com.am.app_plugins.precision_help;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.am.frame.member.MemberManager;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;


/**
 * 
 * @author yuebin
 * 扶贫办扶贫资金分配保存action
 * 
 */
public class AntiPovertyFundsRecordSaveAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		
		//1，获取所有扶贫账号，检查扶贫办账号是否存在。
		
		//主表
		Table recordTable=ac.getTable("LXNY_ANTI_POVERTY_FUNDS_RECORD");
		//将平均价格转化成 元
		TableRow mainTr=recordTable.getRows().get(0);
		mainTr.setValue("anti_amount", VirementManager.changeY2FInt(ac.getRequestParameter("lxny_anti_poverty_funds_record.form.anti_amounts")));
		
		db.save(recordTable);
		
		String recordId=recordTable.getRows().get(0).getValue("id");
		
		//子表
		Table table=ac.getTable("LXNY_ANTI_PFUNDS_RECORD_DATEILS");
		
		List<TableRow> tableRows=table.getRows();
		
		List<String> checkResult=new ArrayList<String>();
		
		MemberManager mm=new MemberManager();
		
		
		String[] loginAccounts=ac.getRequestParameters("lxny_anti_pfunds_record_dateils.list.member_loginaccount");
		String[] antiAmounts= ac.getRequestParameters("lxny_anti_pfunds_record_dateils.list.anti_amounts");
		
		for(int i=0;i<tableRows.size();i++){
			TableRow tr=tableRows.get(i);
			
			tr.setValue("record_id", recordId);
			//获取账号
			String  loginAccount=loginAccounts[i];
			ac.getRequestParameters("lxny_anti_pfunds_record_dateils.list.member_loginaccount");
			
			MapList memberMap=mm.getMemberByLoginAccount(db, loginAccount);
			
			//将元转换为分
			tr.setValue("anti_amount", VirementManager.changeY2FInt(antiAmounts[i]));
			
			if(Checker.isEmpty(memberMap)){
				checkResult.add("账号"+loginAccount+"不存在。");
			}else if(!"1".equals(memberMap.getRow(0).get("is_poor"))){
				checkResult.add("账号"+loginAccount+"不是贫困户。");
			}else{
				tr.setValue("member_id", memberMap.getRow(0).get("id"));
			}
			
		}
		
		if(checkResult.size()==0){
			db.save(table);
			
			//更新总金额
			String updateSQL="UPDATE lxny_anti_poverty_funds_record "
					+ " SET anti_total_amount=(SELECT sum(COALESCE(anti_amount,0)) "
					+ "   FROM lxny_anti_pfunds_record_dateils WHERE record_id=? ) "
					+ " WHERE id=? ";
			
			db.execute(updateSQL,new String[]{
					recordId,recordId
			},new int[]{
					Type.VARCHAR,Type.VARCHAR
			});
			
		}else{
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage(Arrays.toString(checkResult.toArray()));
		}
		
	}
	
}
