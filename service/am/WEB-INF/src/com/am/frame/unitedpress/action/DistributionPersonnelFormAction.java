package com.am.frame.unitedpress.action;

import com.am.frame.badge.AMBadgeManager;
import com.am.frame.badge.BadgeImpl;
import com.am.frame.unitedpress.UnitedpressBusinessService;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年5月4日
 *@version
 *说明：配送人员表单Action
 */
public class DistributionPersonnelFormAction extends DefaultAction {
	
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// 通过审核
		String id = ac.getRequestParameter("mall_distribution_personnel.form.id");
		String remarks = ac.getRequestParameter("mall_distribution_personnel.form.remarks");
		String memberId = ac.getRequestParameter("mall_distribution_personnel.form.purchaser");
		String paramStatus = ac.getActionParameter();
		String sql = "";
		boolean flag = true;
		UnitedpressBusinessService sps=new UnitedpressBusinessService();
		
		if("1".equals(paramStatus)){
			if(Checker.isEmpty(remarks)){
				flag = false;
			}
			//审核通过
			String updateOrgSql = " UPDATE mall_distribution_personnel SET f_status = 1,last_modify_time=now(),remarks = '"+remarks+"' WHERE id = '"+id+"' ";
			db.execute(updateOrgSql);
			
			
			//给配送人员添加徽章
			addBadges(db,ac,memberId);
			
			sps.addPaymentToMembr(db, ac,"mall_distribution_personnel.form",memberId);
			
		}else if("2".equals(paramStatus)){
			//审核驳回
			sql = " UPDATE mall_distribution_personnel SET f_status = 2,remarks = '"+remarks+"' WHERE id = '"+id+"' ";

			//申请驳回，需要返回购买机构的金额到现金账号
			sps.rejectApplication(db, id,"mall_distribution_personnel", remarks);
			
		}else if("3".equals(paramStatus)){
			//撤销
			sql = " UPDATE mall_distribution_personnel SET f_status = 3,remarks = '"+remarks+"' WHERE id = '"+id+"' ";
			//去掉配送人员的徽章
			delBadges(db,ac,memberId);
		}
		if(flag){
			
			db.execute(sql);
			ac.getActionResult().setSuccessful(true);
		
		}else{
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("审核说明不能为空");
		}
	}
	/**
	 * 去掉配送人员的徽章
	 * @param db
	 * @param ac
	 * @param memberId  购买会员ID
	 * @throws JDBCException 
	 */
	private void delBadges(DB db, ActionContext ac, String memberId) throws JDBCException {

		String Badge_JSZJ = BadgeImpl.Badge_PSRY;
		AMBadgeManager badgeManager = new AMBadgeManager();
		badgeManager.deleteBadgeByEntBadgeCode(db,Badge_JSZJ,memberId);
	}
	/**
	 * 给配送人员添加徽章
	 * @param db
	 * @param ac
	 * @param memberId   购买会员ID
	 * @throws JDBCException 
	 */
	private void addBadges(DB db, ActionContext ac, String memberId) throws JDBCException {

		String Badge_JSZJ = BadgeImpl.Badge_PSRY;
		AMBadgeManager badgeManager = new AMBadgeManager();
		badgeManager.addBadgeByEntBadgeCode(db,Badge_JSZJ,memberId);
	}
}