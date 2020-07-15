package com.ambdp.enterpriseRecruitment.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;

/** 
 * @author  作者：qintao
 * @date 创建时间：2016年10月25日 下午9:03:16
 * @explain 说明 : 应聘审核
 */
public class InterviewAuditAction extends DefaultAction {
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//查询集合
		//查询集合id
		String id=ac.getRequestParameter("lxny_myapplication.form.id");
		String paramsid = ac.getActionParameter();
		String SQL="";
		
							if(paramsid.equals("1"))
							{//未发布-》发布
								SQL = "UPDATE lxny_myapplication SET status='1' WHERE id='"+id+"' ";
							}else if(paramsid.equals("2"))
							{//发布-》撤销
								SQL = "UPDATE lxny_myapplication SET status='2' WHERE id='"+id+"' ";
							}
							db.execute(SQL);
					}
			}
	

