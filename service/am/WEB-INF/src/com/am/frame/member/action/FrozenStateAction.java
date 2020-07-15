package com.am.frame.member.action;


import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;


/**
 * 冻结 /解冻
 * 会员状态
 */
public class FrozenStateAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//查询集合
		String[] list = ac.getRequestParameters("_s_am_member_list");
		//查询集合id
		String[] arraylist = ac.getRequestParameters("am_member_list.id");
		
		String paramsid = ac.getActionParameter();
		
		String SQL = "";
		
		
			for (int i = 0; i < list.length; i++) {
					if(list[i].equals("1"))
					{
							if(paramsid.equals("0"))
							{//冻结-》解冻
								SQL = "UPDATE am_member SET account_freeze='0' WHERE id='"+arraylist[i]+"' ";
							}else
							{//解冻-》冻结
								SQL = "UPDATE am_member SET account_freeze='1' WHERE id='"+arraylist[i]+"' ";
							}
							db.execute(SQL);
					}
			}
	}
}
