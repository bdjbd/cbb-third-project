package com.am.borrowing.action;

import com.am.frame.systemAccount.SystemAccountClass;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/** * @author  作者：yangdong
 * @date 创建时间：2016年4月28日 下午6:03:42
 * @version 借款创业通过审核
 */
public class PassBorrowingAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//机构借款账户
		String GROUP_LOAN_ACCOUNT = SystemAccountClass.GROUP_LOAN_ACCOUNT;
		//消费账户
		String CONSUMER_ACCOUNT = SystemAccountClass.CONSUMER_ACCOUNT;
		//获取审核说明
		String review_remarks = ac.getRequestParameter("mall_borrowing_records.form.review_remarks");
		//申请金额
		double amount_of_subsidy = Double.parseDouble(ac.getRequestParameter("mall_borrowing_records.form.amount_of_subsidy"));
		//申请人
		String member_id = ac.getRequestParameter("mall_borrowing_records.form.member_id");
		//机构id
		String orgid_id = ac.getRequestParameter("mall_borrowing_records_orgid.form.orgid");
		
		//id
		String id = ac.getRequestParameter("mall_borrowing_records.form.id");
		
		if(!Checker.isEmpty(orgid_id)){
			//修改审核状态为审核通过，并修改审核说明
			String updateStatusSQL = " UPDATE  mall_borrowing_records SET status = 2,review_remarks = '"+review_remarks+"',orgid= '"+orgid_id+"' where id ='"+id+"' ";
			
			
			int res = db.execute(updateStatusSQL);
			String sql = "select mbr.*,msct.t_table_name from MALL_BORROWING_RECORDS as mbr "
					+ "left join mall_service_commodity as msc on msc.id = mbr.sc_id "
					+ "left join mall_service_comd_type as msct on msct.id = msc.sc_type "
					+ "where mbr.id = '"+id+"'";
			

			MapList mList = db.query(sql);
			
			if(!Checker.isEmpty(mList)){
				String usql  = "update "+mList.getRow(0).get("t_table_name")+" set f_status = '0' where id = '"+mList.getRow(0).get("orgid")+"'";
				db.execute(usql);
			}
			//将钱存入借款账户
			if(res !=0){
				String updateAccountSQL = "UPDATE mall_account_info SET balance = balance + "+amount_of_subsidy+" WHERE member_orgid_id = '"+member_id+"' "
						+ "and a_class_id= (SELECT id FROM mall_system_account_class WHERE sa_code = '"+GROUP_LOAN_ACCOUNT+"' ) ";
				db.execute(updateAccountSQL);
			}
		}else{
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("请输入联合社/合作社id");
		}
		
		
	}
}
