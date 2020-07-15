package com.p2p.material.outstore;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

public class OutstoreAction extends DefaultAction {

	
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		String id=ac.getRequestParameter("id");
		
		//查询数量
		String queryCountsSQL="SELECT counts,code FROM p2p_outstore WHERE id='"+id+"'";
		MapList countsMap=db.query(queryCountsSQL);
		
		if(!Checker.isEmpty(countsMap)){
			String counts=countsMap.getRow(0).get("counts");
			String code=countsMap.getRow(0).get("code");
			
			//跟新数量
			String updateSQL="UPDATE p2p_MaterialsCode SET amount=(COALESCE(amount,0)-"+counts+") WHERE code='"+code+"'";
			db.execute(updateSQL);
			
		}
		
		//更新状态
		String sql="UPDATE p2p_outstore SET  datatstatus=3 WHERE id='"+id+"'";
		db.execute(sql);
	}
}
