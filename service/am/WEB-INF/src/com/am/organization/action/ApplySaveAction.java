package com.am.organization.action;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;
/**
 * 后台组织机构申请加入审核保存
 *
 */
public class ApplySaveAction extends DefaultAction{
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
	    Table table = ac.getTable("LXNY_ORGANIZATIONAL_RELATIONSHI");
	    //获取当前组织id
		String joining_mechanism_id=ac.getRequestParameter("lxny_organizational_relationshi_joins.form.joining_mechanism_id");
		//获取被加入组织id
		String be_added_to_the_body_id=ac.getRequestParameter("lxny_organizational_relationshi_joins.form.be_added_to_the_body_id");
		//获取状态
		String status=ac.getRequestParameter("lxny_organizational_relationshi_joins.form.status");
		String id = "";
		//判断申请加入的组织机构是否存在
		MapList maps = db.query("select * from aorg where orgid ='"+joining_mechanism_id+"'");
		if(!Checker.isEmpty(joining_mechanism_id) && !Checker.isEmpty(maps)){
			db.save(table);
			id = table.getRows().get(0).getValue("id");
			ac.setSessionAttribute("am_bdp.lxny_organizational_relationshi_joins.form.id", id);
			
		}else{
			ac.getActionResult().setSuccessful(false);
		    ac.getActionResult().addErrorMessage("请输入组织id");
		    
		    }
		   
		}
	}

