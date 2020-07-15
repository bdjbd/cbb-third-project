package com.p2p.material.instore;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * 入库Action
 * @author Administrator
 *
 */
public class InstoreAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		String id=ac.getRequestParameter("id");
		
		//查询数量,编号，平均价
		String queryCountsSQL="SELECT inprice,counts,materialscode FROM p2p_InStore WHERE id='"+id+"'";
		MapList countsMap=db.query(queryCountsSQL);
		
		
		if(!Checker.isEmpty(countsMap)){
			String counts=countsMap.getRow(0).get("counts");
			String inprice=countsMap.getRow(0).get("inprice");
					
			String materialscode=countsMap.getRow(0).get("materialscode");
			
			//评价单价，累计平均价格
			String sql1="UPDATE p2p_MaterialsCode SET "
					+ " amount=COALESCE(amount,0)+"+counts+","
					+ " avgPrice=(CASE COALESCE(avgPrice,0) WHEN 0 THEN "+inprice+" ELSE   (avgPrice+"+inprice+")/2 END)  "
					+ " WHERE code='"+materialscode+"'";
			
			db.execute(sql1);
			
		}
		
		
		//6，修改入库单状态
		String sql="UPDATE p2p_InStore SET  instorestatus=3 WHERE id='"+id+"'";
		db.execute(sql);
		
	}
}
