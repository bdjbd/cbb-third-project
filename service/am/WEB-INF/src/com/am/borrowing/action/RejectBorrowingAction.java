package com.am.borrowing.action;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/** * @author  作者：yangdong
 * @date 创建时间：2016年4月28日 下午6:04:59
 * @version 借款创业拒绝
 */
public class RejectBorrowingAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//获取拒绝原因
		String review_remarks = ac.getRequestParameter("mall_borrowing_records.form.review_remarks");
		
		//id
		String id = ac.getRequestParameter("mall_borrowing_records.form.id");
		
		if(Checker.isEmpty(review_remarks)){
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("请输入拒绝原因");
		}else{
			//修改审核状态为审核驳回，并保存拒绝原因
			String updateStatusSQL = " UPDATE  mall_borrowing_records SET status = 3,review_remarks = '"+review_remarks+"' where id='"+id+"'";
			
			String sql = "select mbr.*,msct.t_table_name from MALL_BORROWING_RECORDS as mbr "
					+ "left join mall_service_commodity as msc on msc.id = mbr.sc_id "
					+ "left join mall_service_comd_type as msct on msct.id = msc.sc_type "
					+ "where mbr.id = '"+id+"'";
			

			MapList mList = db.query(sql);
			
			if(!Checker.isEmpty(mList)){
				String usql  = "update "+mList.getRow(0).get("t_table_name")+" set f_status = '5',remarks= '"+review_remarks+"' where id = '"+mList.getRow(0).get("orgid")+"'";
				db.execute(usql);
				VirementManager virement = new VirementManager();
				virement.execute(db, "", mList.getRow(0).get("member_id"), "",SystemAccountClass.CASH_ACCOUNT,Long.parseLong(mList.getRow(0).get("credit_margin"))/100+"", "借款账户授信保证金归还", "", "", false);

			}
			
			db.execute(updateStatusSQL);
		}
		
		super.doAction(db, ac);
	}
	
}
