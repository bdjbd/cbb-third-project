package com.p2p.member.maintain;

import com.fastunit.Ajax;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

public class ForzenMaintainAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		String memberCode=ac.getRequestParameter("member_code");
		String checkSQL="SELECT type FROM ws_member WHERE member_code="+memberCode;
		db=DBFactory.getDB();
		MapList map=db.query(checkSQL);
//		1=维修人员；
//		2=冻结维修人员
		String status="2";
		if(!Checker.isEmpty(map)&&"2".equals(map.getRow(0).get("type"))){
			status="1";
		}
		String sql="UPDATE ws_member SET  type="+status+" WHERE member_code="+memberCode;
		db.execute(sql);
		Ajax ajax=new Ajax(ac);
		ajax.addScript("location.reload();");
		ajax.send();
	}

}
