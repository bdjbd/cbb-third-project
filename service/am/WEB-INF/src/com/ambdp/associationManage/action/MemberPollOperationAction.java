package com.ambdp.associationManage.action;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;
/**
 *@author wangxi
 *@create 2016.06.04
 *@version
 *说明：社员代表大会列表撤销发布action
 */
public class MemberPollOperationAction extends DefaultAction{
	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		String Id=ac.getRequestParameter("id");
		DB db=DBFactory.getDB();
		String checkSQL="SELECT * FROM mall_member_poll WHERE id='"+Id+"'";
		MapList map=db.query(checkSQL);
		
		if(!Checker.isEmpty(map)){
			String dataStatus=map.getRow(0).get("status");
			String updateSQL="";
			
			if("0".equals(dataStatus)){//草稿--》发布
				updateSQL="UPDATE mall_member_poll SET status='1',push_time='now()' WHERE id='"+Id+"'";
			}
			
			if("1".equalsIgnoreCase(dataStatus)){//1 发布-->草稿
				updateSQL="UPDATE mall_member_poll SET status='0',push_time=null WHERE id='"+Id+"'";
			}
			
			if(!Checker.isEmpty(updateSQL)){
				db.execute(updateSQL);
			}
		}
		return ac;
	}
}
