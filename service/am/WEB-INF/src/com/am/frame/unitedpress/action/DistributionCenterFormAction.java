package com.am.frame.unitedpress.action;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.unitedpress.UnitedpressBusinessService;
import com.am.frame.webapi.member.service.SystemAccountServer;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/** 
 * @author  作者：wz
 * @date 创建时间：2016年4月27日 下午12:11:52
 * @version 配送中心表单通过审核通过
 */
public class DistributionCenterFormAction extends DefaultAction {
	
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// 通过审核
		String id = ac.getRequestParameter("distribution_center.form.id");
		String remarks = ac.getRequestParameter("distribution_center.form.remarks");
		String userId=ac.getRequestParameter("distribution_center.form.admin_account");
		
		String purchaser=ac.getRequestParameter("distribution_center.form.purchaser");
		
		String paramStatus = ac.getActionParameter();
		String sql = "";
		String revokesql="";
		boolean flag = true;
		
		UnitedpressBusinessService sps=new UnitedpressBusinessService();
		
		if("1".equals(paramStatus)){
			//1,检查管理员帐号是否存在
			SystemAccountServer sas=new SystemAccountServer();
			if(Checker.isEmpty(userId)){
				ac.getActionResult().setSuccessful(false);
				ac.getActionResult().addErrorMessage("管理员帐号不能为空，请重新输入！");
				return;
			}else if(sas.checkUserExist(db, userId)){
				ac.getActionResult().setSuccessful(false);
				ac.getActionResult().addErrorMessage("管理员帐号已存在，请重新输入！");
				return;
			}else{
				sas.updateUserId(db,userId,id,"mall_distribution_center");
				JSONObject result=sas.initSystemServiceCommodity(db, id, "mall_distribution_center");
				if(result!=null&&"0".equals(result.get("code"))){
					String orgId=result.getString("msg");
					
					//审核通过
					String updateOrgSql = " UPDATE mall_distribution_center SET f_status = 1,orgid='"+orgId+"',remarks = '"+remarks+"' WHERE id = '"+id+"' ";
					db.execute(updateOrgSql);
					
					//更新配送中心对应店铺的地址
					if(result.has("store_id")){
						String storeId=result.getString("store_id");//店铺ID
						StringBuilder querySQL=new StringBuilder();
						
						querySQL.append("SELECT  m.membername,m.phone, p.proname||c.cityname||z.zonename AS dcAddres,dc.* ");
						querySQL.append("	FROM mall_distribution_center AS dc                    ");
						querySQL.append("	LEFT JOIN province AS p ON  p.proid=dc.province_id     ");
						querySQL.append("	LEFT JOIN city AS c ON c.cityid=dc.city_id             ");
						querySQL.append("	LEFT JOIN zone AS z ON z.zoneid=dc.zone_id             ");
						querySQL.append("	LEFT JOIN am_member AS m ON m.id=purchaser    		    ");
						querySQL.append("	WHERE dc.id=?                 ");
						
						MapList dcMap=db.query(querySQL.toString(),id,Type.VARCHAR);
						
						if(!Checker.isEmpty(dcMap)){
							//contactphone,contact,
							String updateSQL="UPDATE mall_store "
									+ " SET addressdetil=?,store_name=?,"
									+ " contactphone=?,contact=? "
									+ " WHERE id=? ";
							
							Row row=dcMap.getRow(0);
							
							db.execute(updateSQL,new String[]{
									row.get("dcaddres"),row.get("gap_name"),
									row.get("phone"),row.get("membername"),
									storeId
							}, new int[]{Type.VARCHAR,Type.VARCHAR,
									Type.VARCHAR,Type.VARCHAR,
									Type.VARCHAR});
						}
					}else{
						logger.info("机构为初始化店铺");
					}
					
				
					//判断是否为借款创业
					String judgeSql = "SELECT * FROM MALL_BORROWING_RECORDS WHERE orgid = '"+id+"'";
					MapList LoanBusiness = db.query(judgeSql);
					if(!Checker.isEmpty(LoanBusiness)){
						UnitedpressBusinessService unitedpressBusinessService = new UnitedpressBusinessService();
						unitedpressBusinessService.addPaymentr(db, id, orgId);
					};
					
					//给上级分利
					long rewardMeony=sps.executeShareBenefits(db,"mall_distribution_center",orgId);
					sps.addPaymentToCreditMargin(db, ac, "distribution_center.form",rewardMeony,orgId);
					
				}else{
					ac.getActionResult().setSuccessful(false);
					ac.getActionResult().addErrorMessage(result.getString("msg"));
					return ;
				}
				
			}
		}else if("2".equals(paramStatus)){
			//审核驳回
			sql = " UPDATE mall_distribution_center SET f_status = 2,remarks = '"+remarks+"' WHERE id = '"+id+"' ";
			
			//申请驳回，需要返回购买机构的金额到现金账号
			sps.rejectApplication(db, id,"mall_distribution_center", remarks);
			
		}else if("3".equals(paramStatus)){
			//撤销
			sql = " UPDATE mall_distribution_center SET f_status = 3,remarks = '"+remarks+"' WHERE id = '"+id+"' ";
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
