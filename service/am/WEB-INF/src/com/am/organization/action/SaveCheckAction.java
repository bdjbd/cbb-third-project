package com.am.organization.action;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *后台组织机构保存判断
 *
 */
public class SaveCheckAction extends DefaultAction{

    
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		Table table = ac.getTable("lxny_organizational_relationshi");
			
	
	//获取加入组织id
	String joining_mechanism_id=ac.getRequestParameter("lxny_organizational_relationshi_joins.form.joining_mechanism_id");
	//获取当前组织id
	String be_added_to_the_body_id=ac.getRequestParameter("lxny_organizational_relationshi_joins.form.be_added_to_the_body_id");
	
	//获取状态
	String status=ac.getRequestParameter("lxny_organizational_relationshi_joins.form.status");
	
	String id = "";

	//判断申请加入的组织机构是否存在
	//不能加入自己的组织机构
	//判断加入的组织是否为已经加入机构 
	
	
	//判断是否已经加入过当前机构
	String SQL_1 = "select * from lxny_organizational_relationshi where joining_mechanism_id = '"+joining_mechanism_id+"' AND be_added_to_the_body_id='"+be_added_to_the_body_id+"'";
	MapList map = db.query(SQL_1);
	//查询组织机构id是否存在
	String SQL_2 = "select * from aorg where orgid = '"+joining_mechanism_id+"'";
	MapList maps = db.query(SQL_2);
	
	if(!Checker.isEmpty(be_added_to_the_body_id) && maps.size()>0)
	{	
		
		if(map.size()>0)
		{
			if("22".equals(map.getRow(0).get("status")) || "30".equals(map.getRow(0).get("status")))
			{
				String Update_SQL = "UPDATE lxny_organizational_relationshi SET status='10' WHERE id ='"+map.getRow(0).get("id")+"'";
				db.execute(Update_SQL);
				ac.getActionResult().setSuccessful(true);
			}else
			{
				ac.getActionResult().setSuccessful(false);
				ac.getActionResult().addErrorMessage("已加入组织机构");
			}
		}else
		{
			id = table.getRows().get(0).getValue("id");
			db.save(table);
			ac.setSessionAttribute("am_bdp.lxny_organizational_relationshi_joins.form.id", id);
			ac.getActionResult().setSuccessful(true);
		}
	}else{
		ac.getActionResult().setSuccessful(false);
		if(Checker.isEmpty(maps))
		{
			ac.getActionResult().addErrorMessage("组织id不存在");
		}else if(!Checker.isEmpty(map))
		{
			ac.getActionResult().addErrorMessage("已加入组织机构");
		}else
		{
			ac.getActionResult().addErrorMessage("请输入组织id");
		}
		
     }
	
	
	}
	
  }


