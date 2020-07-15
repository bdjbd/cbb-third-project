package com.am.app_plugins.precision_help.action;

import com.am.frame.member.MemberManager;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;


/**
 * 保存社员为平困户社员
 * @author yuebin
 * 
 * 区县扶贫办，只能添加当前区县的社员
 * 
 */
public class AddAntiPoorSaveAction extends DefaultAction {
	
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		//获取机构的ID
		String orgId=ac.getVisitor().getUser().getOrgId();
		
		String checkOrgid=orgId.replace("_IPRCC","");

		
		//贫困户账号
		String loginAccount=ac.getRequestParameter("anti_am_member.form.loginaccount");
		
		MemberManager mm=new MemberManager();
		
		MapList memberMap=mm.getMemberByLoginAccount(db, loginAccount);
		if(!Checker.isEmpty(memberMap)){
			
			//获取会员的社员类型
			Row memberRow=memberMap.getRow(0);
			String memberType=memberRow.get("member_type");
			
			if(!"3".equals(memberType)){
				ac.getActionResult().setSuccessful(false);
				ac.getActionResult().addErrorMessage("账号"+loginAccount+"不是生产者账号。");
			}else{
				String memberId=memberMap.getRow(0).get("id");
				
				//验证当前机构是否为当前区县的联合社
				String memberArea="org_P"+memberRow.get("province")+"_C"+memberRow.get("city")+"_Z"+memberRow.get("zzone");
				
				if(memberArea.contains(checkOrgid)){
					//验证社员是否为当前区县市的机构
					String updateSQL="UPDATE am_member SET is_poor=1 WHERE id=? ";
					db.execute(updateSQL, memberId,Type.VARCHAR);
				}else{
					ac.getActionResult().setSuccessful(false);
					ac.getActionResult().addErrorMessage("账号"+loginAccount+"不在当前扶贫办管辖内！");
				}
			}
		}else{
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("账号"+loginAccount+"不存在");
		}
		
	}

}
