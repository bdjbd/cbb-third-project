package com.p2p.recharge;

import com.fastunit.Ajax;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * 充值管理驳回Action
 * @author Administrator
 *
 */
public class RejectAction extends DefaultAction {

	
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		
		String rmid=ac.getRequestParameter("rmid");
		
		Ajax ajax=new Ajax(ac);
		
		//判断是否为已通过，如果审核已经通过，则无法驳回
		String checkSQL="SELECT verifystatus FROM p2p_rechargemanage WHERE rmid=? ";
		
		MapList map=db.query(checkSQL,new String[]{rmid},new int[]{Type.VARCHAR});
		
		if(!Checker.isEmpty(map)&&!map.getRow(0).get("verifystatus").equals("2")){
			
			//驳回 更新充值状态为-1
			String sql="UPDATE p2p_rechargemanage SET verifystatus=-1 WHERE rmid=?";
			
			db.execute(sql,new String[]{rmid},new int[]{Type.VARCHAR});
			
			ajax.addScript("location.reload();");
			
		}else{
			
			ajax.addScript("alert('此充值记录数据无法驳回！')");
			
		}
		
		ajax.send();
		
	}
	
}
