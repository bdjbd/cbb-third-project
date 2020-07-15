package com.am.organization.action;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;
/**
 * 后台组织机构审核拒绝
 *
 */
public class RefuseApproveAction extends DefaultAction{
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		//获取拒绝原因
		String exit_description = ac.getRequestParameter("lxny_organizational_relationshi.form.exit_description");
		
		//获取状态
		String status=ac.getRequestParameter("lxny_organizational_relationshi.form.status");
		
		//id
		String id = ac.getRequestParameter("lxny_organizational_relationshi.form.id");
		
		//判断退出状态
		String updateStatusSQL=null;
		
		//请求参数 
		String actionParam = ac.getActionParameter();
		
		String sstatus = "";
//		10=待审核
//		11=审核拒绝
//		12=通过审核
//		20=退出待审核
//		21=退出审核拒绝
//		22=退出审核通过
//		30=强制踢出
		switch (actionParam) {
		case "1":
			if("20".equals(status))
			{
				sstatus = "12";
				exit_description = "";
			}else{
				sstatus = "11";
			}
			
			break;
		case "2":
			if("20".equals(status))
			{
				sstatus = "22";
				exit_description = "";
			}else{
				sstatus = "12";
			}
			break;
		case "3":
			sstatus = "21";
			break;
		case "4":
			sstatus = "22";
			break;
		default:
			break;
		}
		
		MapList map = db.query("select * from lxny_organizational_relationshi where id = '"+id+"'");
		
		if(!Checker.isEmpty(map))
		{
			updateStatusSQL = " UPDATE  lxny_organizational_relationshi SET status = '"+sstatus+"',exit_description = '"+exit_description+"' where id='"+id+"'";
			db.execute(updateStatusSQL);
		}
		
		ac.getActionResult().setSuccessful(true);
		super.doAction(db, ac);
	}
	
}


