package com.am.frame.unitedpress.action;

import org.json.JSONObject;

import com.am.frame.member.MemberManager;
import com.am.frame.member.MemberManager.MemberRole;
import com.am.frame.unitedpress.UnitedpressBusinessService;
import com.am.frame.webapi.member.service.SystemAccountServer;
import com.ambdp.agricultureProject.server.BuyProjectStockServer;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/** 
 * @author  作者：wz
 * @date 创建时间：2016年4月27日 下午12:11:52
 * @version 农场表单 通过审核通过
 */
public class UnitedFarmCenterFormAction extends DefaultAction {
	
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		// 通过审核
		String id = ac.getRequestParameter("united_farm_manager.form.id");
		String remarks = ac.getRequestParameter("united_farm_manager.form.remarks");
		String userId=ac.getRequestParameter("united_farm_manager.form.admin_account");
		
		//购买人
		String purchaser=ac.getRequestParameter("united_farm_manager.form.purchaser");
		
		String paramStatus = ac.getActionParameter();
		String sql = "";
		String revokesql="";
		boolean flag = true;
		UnitedpressBusinessService sps=new UnitedpressBusinessService();
		
		
		if("1".equals(paramStatus)){
			
			
			if(Checker.isEmpty(userId)){
				ac.getActionResult().setSuccessful(false);
				ac.getActionResult().addErrorMessage("管理员帐号不能为空，请重新输入！");
				return;
			}
			// 审核通过
			//1,检查管理员帐号是否存在
			SystemAccountServer sas=new SystemAccountServer();
			
			if(sas.checkUserExist(db, userId)){
				ac.getActionResult().setSuccessful(false);
				ac.getActionResult().addErrorMessage("管理员帐号已存在，请重新输入！");
				return;
			}else{
				
				String orgId="";
				
				sas.updateUserId(db,userId,id,"home_farm");
				
				//创建联合社，合作社，涉农企业，配送中心
				JSONObject result=sas.initSystemServiceCommodity(db, id, "home_farm");
				
				if(result!=null&&"0".equals(result.get("code"))){
					orgId=result.getString("msg");
					//审核通过
					String updateOrgSql = " UPDATE HOME_FARM SET f_status = 1,orgid='"+orgId+"',remarks = '"+remarks+"' WHERE id = '"+id+"' ";
					db.execute(updateOrgSql);
					
					//判断是否为借款创业
					String judgeSql = "SELECT * FROM MALL_BORROWING_RECORDS WHERE orgid = '"+id+"'";
					MapList LoanBusiness = db.query(judgeSql);
					if(!Checker.isEmpty(LoanBusiness)){
						UnitedpressBusinessService unitedpressBusinessService = new UnitedpressBusinessService();
						unitedpressBusinessService.addPaymentr(db, id, orgId);
					};
					
					
					//给上级分利
					long rewardMeony=sps.executeShareBenefits(db,"home_farm",orgId);
					sps.addPaymentToCreditMargin(db, ac, "united_farm_manager.form",rewardMeony,orgId);
				
				}else{
					ac.getActionResult().setSuccessful(false);
					ac.getActionResult().addErrorMessage(result.getString("msg"));
					return ;
				}
				
				//审核通过后，为其初始化一个投资项目，投资项目编号为Var.get("PROJECT_STORAGE_CODE");
				String projectCode=Var.get("PROJECT_STORAGE_CODE");
				
				new BuyProjectStockServer().copyProject(db, projectCode, orgId,"2");
				
				//购买者如果是消费者，
				//1,则更新为生产者。
				// 设置此用户为农厂厂长
				// 为此用户生成生产者邀请码
				MemberManager memberManager=new MemberManager();
				memberManager.changeMemberType(db, purchaser,"3");
				
				memberManager.changeMemberRole(db, purchaser,MemberRole.ROLE_80.getValue()+"");
				
				memberManager.createProducerInvCode(db, purchaser, MemberRole.ROLE_80.getValue() +"",orgId);
				
				memberManager.changeMemberOrg(db,orgId,purchaser);
			}
			
		}else if("2".equals(paramStatus)){
			//审核驳回
			sql = " UPDATE HOME_FARM SET f_status = 2,remarks = '"+remarks+"' WHERE id = '"+id+"' ";
			
			//申请驳回，需要返回购买机构的金额到现金账号
			sps.rejectApplication(db, id,"HOME_FARM", remarks);
			
		}else if("3".equals(paramStatus)){
			//撤销
			sql = " UPDATE HOME_FARM SET f_status = 3,remarks = '"+remarks+"' WHERE id = '"+id+"' ";
			revokesql = " UPDATE auser SET expireddate = 'now()' WHERE userid = '"+userId+"' ";
		}
		
		if(flag){
			
			db.execute(sql);
			db.execute(revokesql);
			ac.getActionResult().setSuccessful(true);
		
		}else{
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("机构编号不能为空");
		}
	}
	
	
	
}
