package com.p2p.recharge;

import com.fastunit.Ajax;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

public class P2pConfirmVerifyAction extends DefaultAction {
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		String rmid=ac.getRequestParameter("rmid");
		String checkSQL="SELECT member_code,rechargemoney,verifystatus FROM p2p_rechargemanage WHERE rmid='"+rmid+"'";
		String verifystatus="";
		String memberCode;
		String rechargemoney="0";
		String userid=ac.getVisitor().getUser().getId();
		Ajax ajax=new Ajax(ac);
		MapList map=db.query(checkSQL);
		if(!Checker.isEmpty(map)){
			verifystatus=map.getRow(0).get("verifystatus");
			memberCode=map.getRow(0).get("member_code");
			rechargemoney=map.getRow(0).get("rechargemoney");
			
			//检查是否已充值
			if("2".equalsIgnoreCase(verifystatus)){
				ajax.addScript("alert('无效操作！此记录已充值完成。')");
			}
			if("1".equalsIgnoreCase(verifystatus)){
				String updateSQL="UPDATE ws_member SET cash=("
						+ "(SELECT cash FROM ws_member WHERE member_code="+memberCode+")+"+rechargemoney+") "
						+ " WHERE member_code="+memberCode;
				db.execute(updateSQL);
				updateSQL="UPDATE p2p_rechargemanage  SET verifystatus=2,verifyer='"+userid+"' WHERE rmid='"+rmid+"'";
				db.execute(updateSQL);
				ajax.addScript("alert('操作完成！');window.location.reload();");
			}
		}
		ajax.send();
	}
}
