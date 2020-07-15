package com.am.frame.systemAccount.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;

/** 
 * @author  wz  
 * @descriptions 
 * @date 创建时间：2016年4月19日 下午6:13:55 
 * @version 1.0   
 */
public class BindBankCardAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
//		String id_code = ac.getRequestParameter("mall_member_bank.form.bank_code");
//		
//		String sql = "select * from mall_member_bank where bank_code = '"+id_code+"' and member_orgid_id='"+ac.getVisitor().getUser().getOrgId()+"'";
//		
//		MapList list = db.query(sql);
//		
//		if(list.size()>0){
//			
//			ac.getActionResult().setSuccessful(false);
//			ac.getActionResult().addErrorMessage("银行卡卡号已存在");
//		
//		}else{
//			ac.getActionResult().setSuccessful(true);
//			ac.getActionResult().addSuccessMessage("绑定成功");
//			super.doAction(db, ac);
//		}
		
		super.doAction(db, ac);
	}
}
