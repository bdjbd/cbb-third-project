package com.am.frame.elect;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * 电子券启用，停用Ajax
 * @author Administrator
 *
 */
public class ElectTicketStartOrStop extends DefaultAction {

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		String eleId=ac.getRequestParameter("eleId");
		
		DB db=DBFactory.getDB();
		String checkSQL="SELECT * FROM am_eterpelectticket  WHERE id='"+eleId+"'";
		MapList map=db.query(checkSQL);
		
		if(!Checker.isEmpty(map)){
			String dataStatus=map.getRow(0).get("datastatus");
			String updateSQL="";
			
			if("1".equals(dataStatus)){//起草--》启用
				updateSQL="UPDATE am_eterpelectticket SET datastatus='2' WHERE id='"+eleId+"'";
			}
			
			if("2".equalsIgnoreCase(dataStatus)){//2 启用-->3
				updateSQL="UPDATE am_eterpelectticket SET datastatus='3' WHERE id='"+eleId+"'";
			}
			
			if("3".equals(dataStatus)){//3停用-->启用
				updateSQL="UPDATE am_eterpelectticket SET datastatus='2' WHERE id='"+eleId+"'";
			}
			
			if(!Checker.isEmpty(updateSQL)){
				db.execute(updateSQL);
			}
		}
		
		ac.getActionResult().setUrl("/am_bdp/p2p_eterpelectticket.do?m=s&clear=am_bdp.p2p_eterpelectticket.query");
		ac.getActionResult().setSuccessful(true);
		return ac;
	}
}
