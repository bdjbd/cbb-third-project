package com.am.complaintmanager.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/** 
 * @author  作者：yangdong
 * @date 创建时间：2016年4月27日 上午11:27:08
 * @version 保存投诉信息
 */
public class ComplaintManagerSaveAction extends DefaultAction{

	@Override
	public  void doAction(DB db, ActionContext ac) throws Exception {
		//当前登录人
		String memberid = ac.getVisitor().getUser().getId();
		//当前登陆人所在组织机构
		String orgid = ac.getVisitor().getUser().getOrgId();
		//当前时间
		Date now = new Date(); 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowDate = dateFormat.format(now);
		
		//投诉id
		String comp_id = ac.getRequestParameter("mall_complaint_manager.form.id");
		//退款信息id
		String refund_id = ac.getRequestParameter("mall_complaint_manager.form.refund_id");
		//订单id
		String order_id = ac.getRequestParameter("mall_complaint_manager.form.order_id");
		
		String queryOwnerSQL = " SELECT * FROM mall_complaint_owner WHERE comp_id = '"+comp_id+"' ";
		MapList maplist = db.query(queryOwnerSQL);
		//表单责任人处理信息
		String united_press = ac.getRequestParameter("mall_complaint_owner.form.united_press");
		int up_fine = (int) (Double.parseDouble(ac.getRequestParameter("mall_complaint_owner.form.up_fines"))*100);
		String distribution_centre = ac.getRequestParameter("mall_complaint_owner.form.distribution_centre");
		int dc_fine = (int) (Double.parseDouble(ac.getRequestParameter("mall_complaint_owner.form.dc_fines"))*100);
		String cooperative_id = ac.getRequestParameter("mall_complaint_owner.form.cooperative_id");
		int cp_finex = (int) (Double.parseDouble(ac.getRequestParameter("mall_complaint_owner.form.cp_finexs"))*100);
		String farm_id = ac.getRequestParameter("mall_complaint_owner.form.farm_id");
		int farm_fine = (int) (Double.parseDouble(ac.getRequestParameter("mall_complaint_owner.form.farm_fines"))*100);
		String producer_id = ac.getRequestParameter("mall_complaint_owner.form.producer_id");
		int prd_fine = (int) (Double.parseDouble(ac.getRequestParameter("mall_complaint_owner.form.prd_fines"))*100);
		
		
		//保存责任人处理信息
		Table ownerTable = new Table("am_bdp", "mall_complaint_owner");
		//新增
		if(Checker.isEmpty(maplist)){
			TableRow row=ownerTable.addInsertRow();
			row.setValue("united_press", united_press);
			row.setValue("up_fine", up_fine);
			row.setValue("distribution_centre", distribution_centre);
			row.setValue("dc_fine", dc_fine);
			row.setValue("cooperative_id",cooperative_id);
			row.setValue("cp_finex", cp_finex);
			row.setValue("farm_id", farm_id);
			row.setValue("farm_fine", farm_fine);
			row.setValue("producer_id", producer_id);
			row.setValue("prd_fine", prd_fine);
			row.setValue("comp_id", comp_id);
			row.setValue("operationer", memberid);
			row.setValue("operation_orgid",orgid);
			db.save(ownerTable);
			db.getConnection().commit();
		}else{
			//修改	
			String owner_id = ac.getRequestParameter("mall_complaint_owner.form.id");
			TableRow row = ownerTable.addUpdateRow();
			row. setOldValue("id",owner_id);
			row.setValue("united_press", united_press);
			row.setValue("up_fine", up_fine);
			row.setValue("distribution_centre", distribution_centre);
			row.setValue("dc_fine", dc_fine);
			row.setValue("cooperative_id",cooperative_id);
			row.setValue("cp_finex", cp_finex);
			row.setValue("farm_id", farm_id);
			row.setValue("farm_fine", farm_fine);
			row.setValue("producer_id", producer_id);
			row.setValue("prd_fine", prd_fine);
			row.setValue("comp_id", comp_id);
			db.save(ownerTable);
			db.getConnection().commit();
		}
		
		String queryRefundSQL = " SELECT * FROM mall_refund WHERE id = '"+refund_id+"' ";
		MapList refundList = db.query(queryRefundSQL);
//		int realrefundmenoy = (int) (Double.parseDouble(ac.getRequestParameter("mall_complaint_refund.form.realrefundmenoy"))*100);
		String realRefundMenoy=ac.getRequestParameter("mall_complaint_refund.form.realrefundmenoy");
		
		try{
			double inReal=Double.parseDouble(realRefundMenoy);
			if(inReal>999999.9999){
				throw new Exception("");
			}
		}catch(Exception e){
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("请检查输入的是否合法！");
			return;
		}
		
		
		String refundexplain = ac.getRequestParameter("mall_complaint_refund.form.refundexplain");
		Table refundTable = new Table("am_bdp", "mall_refund");
		
		
		
		if(Checker.isEmpty(refundList)){
			TableRow row = refundTable.addInsertRow();
			row.setValue("order_id", order_id);
			row.setValue("state", "1");
			row.setValue("realrefundmenoy", realRefundMenoy);
			row.setValue("applyrefundmenoy", realRefundMenoy);
			row.setValue("refundexplain", refundexplain);
			row.setValue("operationerid", memberid);
			row.setValue("applyTime", nowDate);
			
			db.save(refundTable);
			db.getConnection().commit();
			String refundid = refundTable.getRows().get(0).getValue("id");
			
			Table complaintTable = new Table("am_bdp", "mall_complaint_manager");
			TableRow complaintRow = complaintTable.addUpdateRow();
			complaintRow.setOldValue("id", comp_id);
			complaintRow.setValue("refund_id", refundid);
			db.save(complaintTable);
			db.getConnection().commit();
		}else{
			String refundid = ac.getRequestParameter("mall_complaint_refund.form.id");
			TableRow row = refundTable.addUpdateRow();
			row.setOldValue("id",refundid );
			row.setValue("realrefundmenoy", realRefundMenoy);
			row.setValue("applyrefundmenoy", realRefundMenoy);
			row.setValue("refundexplain", refundexplain);
			row.setValue("operationerid", memberid);
			row.setValue("applyTime", nowDate);
			row.setValue("order_id", order_id);
			db.save(refundTable);
			db.getConnection().commit();
		}
	}

}
