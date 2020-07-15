package com.p2p.base.badge;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.Action;
import com.fastunit.util.Checker;

/**
 * Author: Mike
 * 2014年7月18日
 * 说明：
 *
 **/
public class StopOrRunAction implements Action {

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		String id=ac.getRequestParameter("id");
		
		String sql="SELECT * FROM p2p_badgeTemplate WHERE id='"+id+"'";
		DB db=DBFactory.getDB();
		MapList map=db.query(sql);
		if(!Checker.isEmpty(map)){
			if("1".equals(map.getRow(0).get("badgestate"))){
				sql="UPDATE p2p_badgeTemplate SET badgestate='0' WHERE id='"+id+"'";
			}
			if("0".equals(map.getRow(0).get("badgestate"))){
				sql="UPDATE p2p_badgeTemplate SET badgestate='1' WHERE id='"+id+"'";
			}
			db.execute(sql);
		}
		return ac;
	}

}
