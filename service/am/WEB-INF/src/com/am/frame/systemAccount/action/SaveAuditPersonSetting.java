package com.am.frame.systemAccount.action;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author 作者：yangdong
 *@create 时间：2016年7月25日 下午2:49:28
 *@version 说明：设置审核人action
 */
public class SaveAuditPersonSetting extends DefaultAction{
	
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		String id=ac.getRequestParameter("mall_auditpersonsetting.form.id");
		
		String second_director = ac.getRequestParameter("mall_auditpersonsetting.form.second_director");
		
		String director = ac.getRequestParameter("mall_auditpersonsetting.form.director");
		
		String orgid = ac.getRequestParameter("mall_auditpersonsetting.form.orgid");
		
		String status = ac.getRequestParameter("mall_auditpersonsetting.form.stas");
		
		Table table = ac.getTable("mall_auditpersonsetting");
		
		if(second_director.equals(director))
		{
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("主任很副主任不能是同一个人！");
			return;
		}
		
		//新增处理
		if("e".equals(status))
		{
			//1，获取旧数据中主任，副主任的账号
			//2,更新旧数据中的主任，副主任的账号类型为空
			//3,设置新主任，副主任
			String querSQL="SELECT second_director,director FROM mall_auditpersonsetting WHERE id='"+id+"'";
			
			MapList map=db.query(querSQL);
			
			if(!Checker.isEmpty(map)){
				Row row=map.getRow(0);
				String oldUPdateaSQL="UPDATE am_member SET audit_role =0 WHERE loginaccount='"+row.get("second_director")+"'  ";
				db.execute(oldUPdateaSQL);
				
				oldUPdateaSQL="UPDATE am_member SET audit_role =0 WHERE loginaccount='"+row.get("director")+"'  ";
				db.execute(oldUPdateaSQL);
			}
		}
		
		db.save(table);
		
//		//修改审核角色为副主任
		String sql = " UPDATE am_member SET audit_role =1 WHERE loginaccount = '"+second_director+"' ";
		db.execute(sql);
//		//修改审核角色为主任
		sql = " UPDATE am_member SET audit_role =2 WHERE loginaccount = '"+director+"' ";
		db.execute(sql);
		
	}
}
