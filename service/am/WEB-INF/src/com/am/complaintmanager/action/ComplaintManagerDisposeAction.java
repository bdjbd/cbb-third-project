package com.am.complaintmanager.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.am.frame.systemAccount.SystemAccountClass;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.action.DefaultAction;

/** 
 * @author  作者：yangdong
 * @date 创建时间：2016年4月27日 下午4:49:00
 * @version 投诉管理处理Action
 */
public class ComplaintManagerDisposeAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		//机构食品安全追溯账户
		String GROUP_FOOD_SAFETY_TRACING_ACCOUNT = SystemAccountClass.GROUP_FOOD_SAFETY_TRACING_ACCOUNT;
		//食品安全追溯账户
		String FOOD_SAFETY_TRACING_ACCOUNT = SystemAccountClass.FOOD_SAFETY_TRACING_ACCOUNT;
		//机构现金账户
		String GROUP_CASH_ACCOUNT = SystemAccountClass.GROUP_CASH_ACCOUNT;
		//运营商机构账号
		String orgid = SystemAccountClass.COUNTRY_OPERATOR_ORG;
		
		//当前时间
		Date now = new Date(); 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowDate = dateFormat.format(now);
		
		//保存信息
		ComplaintManagerSaveAction cms = new ComplaintManagerSaveAction();
		cms.doAction(db,ac);
		
		//投诉id
		String complianitID=ac.getRequestParameter("mall_complaint_manager.form.id");
		
		//联合社罚款
		String united_press = ac.getRequestParameter("mall_complaint_owner.form.united_press");
		int up_fine = (int) (Double.parseDouble(ac.getRequestParameter("mall_complaint_owner.form.up_fines"))*100);
		payRefund(united_press,up_fine,GROUP_FOOD_SAFETY_TRACING_ACCOUNT);
		//配送中心罚款
		String distribution_centre = ac.getRequestParameter("mall_complaint_owner.form.distribution_centre");
		int dc_fine = (int) (Double.parseDouble(ac.getRequestParameter("mall_complaint_owner.form.dc_fines"))*100);
		payRefund(distribution_centre,dc_fine,GROUP_CASH_ACCOUNT);
		//合作社罚款
		String cooperative_id = ac.getRequestParameter("mall_complaint_owner.form.cooperative_id");
		int cp_finex = (int) (Double.parseDouble(ac.getRequestParameter("mall_complaint_owner.form.cp_finexs"))*100);
		payRefund(cooperative_id,cp_finex,GROUP_CASH_ACCOUNT);
		//农厂罚款
		String farm_id = ac.getRequestParameter("mall_complaint_owner.form.farm_id");
		int farm_fine = (int) (Double.parseDouble(ac.getRequestParameter("mall_complaint_owner.form.farm_fines"))*100);
		payRefund(farm_id,farm_fine,GROUP_CASH_ACCOUNT);
		//生产者罚款
		String producer_id = ac.getRequestParameter("mall_complaint_owner.form.producer_id");
		int prd_fine = (int) (Double.parseDouble(ac.getRequestParameter("mall_complaint_owner.form.prd_fines"))*100);
		payRefund(producer_id,prd_fine,GROUP_CASH_ACCOUNT);
		

		//把罚款存入机构现金账户
		int totalfine = up_fine + dc_fine+cp_finex+farm_fine+prd_fine;
		depositFine(orgid,totalfine,GROUP_CASH_ACCOUNT);
		
		//从现金账户扣除退款
//		int realrefundmenoy = (int) (*100);
		try{
			double inReal=Double.parseDouble(ac.getRequestParameter("mall_complaint_refund.form.realrefundmenoy"));
			if(inReal>999999.9999){
				throw new Exception("");
			}
		}catch(Exception e){
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("请检查输入的是否合法！");
			return ;
		}
//		payRefund(orgid,realrefundmenoy,GROUP_CASH_ACCOUNT);
		
		//修改退款状态
		String refundId = ac.getRequestParameter("mall_complaint_refund.form.id");
		Table refundTable = new Table("am_bdp", "mall_refund");
		TableRow refundRow = refundTable.addUpdateRow();
		refundRow.setOldValue("id", refundId);
		refundRow.setValue("state", 2);
		refundRow.setValue("refundtime", nowDate);
		refundRow.setValue("cofrimtime", nowDate);
		db.save(refundTable);
		
		//更新处理状态为已处理
		String updateSQL="UPDATE mall_complaint_manager SET status=2 WHERE id=?";
		db.execute(updateSQL, complianitID, Type.VARCHAR);
		
//		db.getConnection().commit();
	}
	
	//从现金账户扣除退款
	public void payRefund(String member_id,int money,String sa_code){
		StringBuilder queryAccountSQL = new StringBuilder();
		queryAccountSQL.append(" UPDATE mall_account_info SET balance = balance - "+money+" WHERE member_orgid_id = '"+member_id+"' ");
		queryAccountSQL.append(" AND a_class_id= (SELECT id FROM mall_system_account_class WHERE sa_code = '"+sa_code+"') ");
		DB db = null;
		try{
			db = DBFactory.newDB();
			db.execute(queryAccountSQL.toString());
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try {
				db.close();
			} catch (JDBCException e) {
				e.printStackTrace();
			}
		}
	}
	
	//把罚款存入现金账户
	public void depositFine(String member_id,int money,String sa_code){
		StringBuilder queryAccountSQL = new StringBuilder();
		queryAccountSQL.append(" UPDATE mall_account_info SET balance = balance + "+money+" WHERE member_orgid_id = '"+member_id+"' ");
		queryAccountSQL.append(" AND a_class_id= (SELECT id FROM mall_system_account_class WHERE sa_code = '"+sa_code+"') ");
		DB db = null;
		try{
			db = DBFactory.newDB();
			db.execute(queryAccountSQL.toString());
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try {
				db.close();
			} catch (JDBCException e) {
				e.printStackTrace();
			}
		}
	}
}
