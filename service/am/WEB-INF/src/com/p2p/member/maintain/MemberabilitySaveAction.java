package com.p2p.member.maintain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;

public class MemberabilitySaveAction extends DefaultAction {
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//$RS{p2p_maintain.form.member_code,wwd.p2p_maintain.form.member_code}
		Table table=ac.getTable("P2P_MEMBERABILITY");
		List<TableRow> trs=table.getRows();
		Map<String,String> maps=new HashMap<String, String>();
		StringBuffer extenKeys=new StringBuffer();
		int updateNumber=0;
		for(int i=0;i<trs.size();i++){
			if(trs.get(i).isInsertRow()||trs.get(i).isUpdateRow()){
				String key=maps.put(trs.get(i).getValue("serbver_code"),trs.get(i).getValue("id"));
				updateNumber++;
				if(key!=null){
					extenKeys.append(key+",");
				}
			}
		}
		if(updateNumber!=maps.size()){
//			if(extenKeys.length()>1){
//				extenKeys.delete(extenKeys.length()-1, extenKeys.length());
//			}
//			
//			String checkSQL="SELECT name FROM ws_commodity_name WHERE comdity_id IN ("+extenKeys.toString()+")";
//			MapList map=db.query(checkSQL);
//			if(!Checker.isEmpty(map)){
//				extenKeys=new StringBuffer();
//				for(int i=0;i<map.size();i++){
//					extenKeys.append(map.getRow(i).get("name"));
//				}
//			}
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("会员服务能力不可以重复。");
		}
		
		super.doAction(db, ac);
	}
}
