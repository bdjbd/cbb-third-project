package com.am.frame.unitedpress.action;

import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.detail.AfterDetailBean;
import com.am.frame.transactions.detail.TransactionDetail;
import com.am.tools.Utils;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/** 
 * @author  作者：wz
 * @date 创建时间：2016年4月27日 下午12:11:52
 * @version 联合社信用保证金管理
 */
public class MallCreditMarginManagerFormAction extends DefaultAction {
	
	private  Logger log = LoggerFactory.getLogger(getClass());
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		String actionParam = ac.getActionParameter();
		
		Table table = ac.getTable("MALL_CREDIT_MARGIN_MANAGER");
		
		/**
		 * 业务状态
		 */
		String cm_status = ac.getRequestParameter("mall_credit_margin_manager.form.cm_status");
		
		/**
		 * 人员类型    1 人员 2 区县 3 组织
		 */
		String member_type = ac.getRequestParameter("mall_credit_margin_manager.form.member_type");
		
		/**
		 * 操作类型	 1 转入 2 转出
		 */
		String operation_type = ac.getRequestParameter("mall_credit_margin_manager.form.operation_type");
		
		/**
		 * 用户账号
		 */
		String member_org_id = ac.getRequestParameter("mall_credit_margin_manager.form.member_org_id");
		
		String tableName = "";
		
		JSONObject json = null;
		
		/**
		 * 保存操作
		 * 1.保存数据 业务状态设为1 草稿状态
		 */
		if("1".equals(actionParam)){
			
			if(Checker.isEmpty(cm_status)){
				table.getRows().get(0).setValue("cm_status", "1");
			}
			
			db.save(table);
		
		}else if("2".equals(actionParam)){
			/**
			 * 执行确认操作 
			 * 1.业务状态设为2 执行状态
			 * 2.判断人员类型 如果为1是单个社员 如果为2则是区县
			 * 3.判断操作类型
			 * 4.执行账户扣除或添加操作
			 * 5.插入交易记录
			 */
			table.getRows().get(0).setValue("cm_status", "2");
			
			db.save(table);
			
			if("1".equals(member_type) || "3".equals(member_type)){
			
				if(!Checker.isEmpty(member_org_id)){
					
					String sq = "select * from mall_account where loginaccount = '"+member_org_id+"'";
					MapList memberMap = db.query(sq);
					
					MapList accountList = accountIs(db,member_org_id);
					
					if(accountList.size()>0 && !Checker.isEmpty(memberMap)){
						
						tableName = accountList.getRow(0).get("tablename");
						
						json = excuteUp(db,memberMap.getRow(0).get("id"),table.getRows().get(0).getValue("operation_amount"),operation_type,tableName,ac);
						
					}else{
						
						json = new JSONObject();
						json.put("code", "401");
						json.put("msg", "用户不存在");
						
					}
					
					if("0".equals(json.get("code"))){
						
						ac.getActionResult().setSuccessful(true);
						
					}else{
						
						ac.getActionResult().setSuccessful(false);
						ac.getActionResult().addErrorMessage(json.getString("msg"));
					}
					
				}else{
					
					ac.getActionResult().setSuccessful(false);
					ac.getActionResult().addErrorMessage("选择的为人员类型,社员账号不能为空");
				
				}
				
			}else if("2".equals(member_type)) {
				
				String msql = "select * from account_info "
						+ " where province_id = '"+table.getRows().get(0).getValue("province_id")+"'"
								+ " and city_id ='"+table.getRows().get(0).getValue("city_id")+"' "
										+ " and zone_id = '"+table.getRows().get(0).getValue("zone_id")+"'";
				
				MapList memberList = db.query(msql);
				
				if(memberList.size()>0){
					
					for (int i = 0; i < memberList.size(); i++) {
						
						if("none".equals(memberList.getRow(i).get("area_type"))){
							
							excuteUp(db,memberList.getRow(i).get("id"),table.getRows().get(0).getValue("operation_amount"),operation_type,"am_member",ac);
						
						}else{
						
							excuteUp(db,memberList.getRow(i).get("id"),table.getRows().get(0).getValue("operation_amount"),operation_type,"aorg",ac);
						
						}
					}
					
					ac.getActionResult().setSuccessful(true);
					
				}
				
			}
			
		}
		
	}
	
	/**
	 * 用户是否存在 返回
	 * @param memberId
	 * @return
	 */
	private MapList accountIs(DB db,String memberId){
		
		MapList list = null;
		
		String sql = "select * from mall_account where loginaccount = '"+memberId+"'";
		
		try {
			list = db.query(sql);
			
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * 获取用户账户信息
	 * @param db
	 * @param accountId
	 * @return
	 */
	private MapList getAccountInfo(DB db,String memberId,String accountCode){
		
		MapList list = null;
		
		String sql = "";	
			sql = "select mai.*"
					+ " ,myac.max_gmv"
					+ " ,myac.min_gmv"
					+ " ,myac.sa_code"
					+ " ,myac.id as class_id"
					+ " ,myac.transfer_fee_ratio"
					+ " FROM mall_account_info as mai"
					+ " left join mall_system_account_class as myac on "
					+ " myac.id=mai.a_class_id "
					+ " WHERE  mai.member_orgid_id = '"+memberId+"' and sa_code = '"+accountCode+"'"
					+ " and myac.status_valid='1'";	
		try {
			list = db.query(sql); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * 执行扣除或增加操作
	 */
	private JSONObject excuteUp(DB db,String  member_org_id,String number,String status,String tableName,ActionContext ac){
		
		JSONObject jso = new JSONObject();
		
		String usql  = "";
		
		Long money = 0L;
		
		Long balance = 0L;
		
		AfterDetailBean afterDetailBean = new AfterDetailBean();
		afterDetailBean.setTableRow(afterDetailBean.getTranTable().addInsertRow());
		TransactionDetail transactionDetail=new TransactionDetail();
		
			String code = "";
			
			if("am_member".equals(tableName)){
				
				code = SystemAccountClass.CREDIT_MARGIN_ACCOUNT;
				
			}else if("aorg".equals(tableName)){
				code = SystemAccountClass.GROUP_CREDIT_MARGIN_ACCOUNT;
			}
			
			MapList getAccoutList = getAccountInfo(db,member_org_id,code);
			
			
			if(Checker.isEmpty(getAccoutList))
			{
				
			}
			
			balance  = Long.parseLong(getAccoutList.getRow(0).get("balance"));
			
			String ids = UUID.randomUUID().toString();
			
			try {
			
				if(!Checker.isEmpty(getAccoutList)){
					afterDetailBean.setId(ids);
					afterDetailBean.setMember_id(getAccoutList.getRow(0).get("member_orgid_id"));
		    		afterDetailBean.setAccount_id(getAccoutList.getRow(0).get("id"));
		    		afterDetailBean.setBusiness_id(getAccoutList.getRow(0).get("class_id"));
		    		afterDetailBean.setSa_class_id(getAccoutList.getRow(0).get("class_id"));
		    		afterDetailBean.setTrade_state("1");
		    		afterDetailBean.setIs_process_buissnes("1");
		    		afterDetailBean.setCounter_fee(0);
		    		afterDetailBean.setTrade_total_money(Utils.changeY2F(number));
		    		
					//转入 增加金额
					if("1".equals(status)){
						
						money = Utils.changeY2F(number)+balance;
						
			    		afterDetailBean.setRmarks("机构操作信用保证金转入，信用保证金增加");
						
						transactionDetail.earningActions(db, afterDetailBean);
						
						usql = "update mall_account_info set balance = '"+money+"' where id = '"+getAccoutList.getRow(0).get("id")+"'";
						
						log.info("执行信用保证金账号金额转入操作:"+usql);
						
						db.execute(usql);
						jso.put("code", "0");
						jso.put("msg", "操作成功");
						
					}else{
						
						if(balance>Utils.changeY2F(number)){
							
							money = balance - Utils.changeY2F(number);
							
							afterDetailBean.setRmarks("机构操作信用保证金转出，信用保证金扣除");
							
							transactionDetail.afterActions(db, afterDetailBean);
							
							usql = "update mall_account_info set balance = '"+money+"' where id = '"+getAccoutList.getRow(0).get("id")+"'";
							
							log.info("执行信用保证金账号金额转出操作:"+usql);
							
							db.execute(usql);
							
							jso.put("code", "0");
							jso.put("msg", "操作成功");
						}else
						{
							jso.put("code", "999");
							jso.put("msg", "用户金额不足！");
						}
					}
					
				}else{
					
					jso.put("code", "999");
					jso.put("msg", "用户不存在");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		return jso;
	}
	
}
