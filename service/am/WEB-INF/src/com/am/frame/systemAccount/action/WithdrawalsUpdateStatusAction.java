package com.am.frame.systemAccount.action;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月11日
 *@version
 *说明：社员提现申请 处理action  
 */
public class WithdrawalsUpdateStatusAction extends DefaultAction {

	
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		//id 提现ID
		String Id=ac.getRequestParameter("withdrawals.form.id");
		
		//提现账号  1：银行卡；2:支付宝;3:微信 
		String inAccountType=ac.getRequestParameter("withdrawals.form.in_account_type");
		
		//提现金额,单位元
		String cashWithdrawals=ac.getRequestParameter("withdrawals.form.cash_withdrawals");
		
		//提现金额+手续费
		String actuaCashWithdrawal=ac.getRequestParameter("withdrawals.form.actual_cash_withdrawal");
		
		//memberId
		String memberId=ac.getRequestParameter("withdrawals.form.member_id");;
		
		//in_account_code
		String inAccountCode=ac.getRequestParameter("withdrawals.form.out_account_ids");
		
		String actionParam = ac.getActionParameter();
		
		String updateSQL = "";
		//审核通过
		if("1".equals(actionParam)){
			
			String reuslt=checkerWithdrawalsData(db,inAccountType,cashWithdrawals,memberId,Id);
			
			if(reuslt==null){
				updateSQL="UPDATE withdrawals SET settlement_state='3' WHERE id='"+Id+"'";
				db.execute(updateSQL);
				ac.getActionResult().setSuccessful(true);
			}else{
				ac.getActionResult().setSuccessful(false);
				ac.getActionResult().addErrorMessage(reuslt);
			}
			
			
		}else if("2".equals(actionParam)){
			
			//检查拒绝原因是否填写
			String  no_reasons=ac.getRequestParameter("withdrawals.form.no_reasons");
			
			if(!Checker.isEmpty(no_reasons)){
				
				//审核拒绝
				updateSQL="UPDATE mall_transactions_class SET u_status='5' WHERE id='"+Id+"'";
				db.execute(updateSQL);
				
				
				updateSQL="UPDATE withdrawals SET settlement_state=5, no_reasons=? WHERE id=? ";
				db.execute(updateSQL,new String[]{
						no_reasons,Id
				},new int[]{
						Type.VARCHAR,Type.VARCHAR
				});
				
				VirementManager vm=new VirementManager();
				//回复金额到对应的提现账号
				//提现金额+手续费 ，单位分
				String iremakers=no_reasons+" 提现审核拒绝，返回提现金额和手续费！";
				//使用转账功能
				vm.execute(db,"", memberId, "", inAccountCode,VirementManager.changeF2Y(actuaCashWithdrawal)+"", iremakers,"", "", false);
				
				ac.getActionResult().setSuccessful(true);
				
			}else{
				ac.getActionResult().setSuccessful(false);
				ac.getActionResult().addErrorMessage("必须填写拒绝原因！");
			}
			
			
		}
		
	}
	
	
	/**
	 * 检查是否可以提现
	 * @param DB db
	 * @param inAccountType 1:银行卡;2:支付宝;3:微信 
	 * @param cashWithdrawals 提现金额，单位元
	 * @param ac ActionContext
	 * @return
	 * @throws JDBCException 
	 */
	private String  checkerWithdrawalsData(DB db,String inAccountType,
			String cashWithdrawals,String memberId,String id) throws JDBCException {
		String result=null;
		
		//1,检查会员提现任务是否完成
		result=checkMemberTask(db,cashWithdrawals,memberId,id);
		
		if(result!=null){
			return result;
		}
		
		if("3".equals(inAccountType)){
			result=weiChartPayValidate(db,cashWithdrawals,memberId);
		}
		
		return result;
	}
	
	
	/**
	 * 检查会员提现任务是否完成
	 * @param db
	 * @param cashWithdrawals
	 * @param memberId
	 * @return
	 * @throws JDBCException 
	 */
	private String checkMemberTask(DB db, String cashWithdrawals,
			String memberId,String id) throws JDBCException {
		
		String result=null;
		
		//如果提现账号是社区服务志愿者账号，则需要验证是否有体现资格
		String querSQL="SELECT cl.sa_code,ws.* "+
				" FROM withdrawals AS ws "+
				" LEFT JOIN mall_account_info AS ain ON ws.out_account_id=ain.id "+
				" LEFT JOIN mall_system_account_class AS cl ON cl.id=ain.a_class_id "+
				" WHERE cl.account_type=1 "+
				" AND cl.sa_code IN ('"+SystemAccountClass.VOLUNTEER_ACCOUNT+"') "+
				" AND ws.id='"+id+"' ";
		
		MapList qMap=db.query(querSQL);
		if(!Checker.isEmpty(qMap)){
			String checkSQL="SELECT ut.id,ut.TaskRunState "+
					" FROM am_UserTask AS ut  "+
					" LEFT JOIN am_EnterpriseTask AS et ON ut.entertaskid=et.id "+
					"  WHERE  "+
					" ut.memberId=?  "+
					" AND et.task_code='GET_VOLUNTEER_ACCOUNT_WITH_QUALIFICATION' ";
		
			MapList map=db.query(checkSQL,
					new String[]{memberId},new int[]{
					Type.VARCHAR
			});
			
			if(!Checker.isEmpty(map)){
				String taskRunState=map.getRow(0).get("taskrunstate");
				
				if("1".equals(taskRunState)){
					result="社员提现任务未完成，不能提现！";
				}
				
			}
		}
		
		
		return result;
	}


	/**
	 * 微信支付验证
	 * 
	 * @param db
	 * @param cashWithdrawals
	 * @param ac
	 * @return
	 * ◆ 给同一个实名用户付款，单笔单日限额2W/2W
	 * ◆ 给同一个非实名用户付款，单笔单日限额2000/2000
	 * ◆ 一个商户同一日付款总额限额100W
	 * ◆ 单笔最小金额默认为1元
	 * ◆ 每个用户每天最多可付款10次，可以在商户平台--API安全进行设置
	 * ◆ 给同一个用户付款时间间隔不得低于15秒
	 * @throws JDBCException 
	 */

	public String  weiChartPayValidate(DB db, String cashWithdrawals,String memberId) throws JDBCException {
		String result=null;
		
		
		//2,验证单笔交易金额值
		if(2000<Double.parseDouble(cashWithdrawals)){
			result="微信提现申请金额不能超过2000元";
			return result;
		}
		//验证最小交易金额
		if(1>Double.parseDouble(cashWithdrawals)){
			result="微信提现最小交易金额为1元";
			return result;
		}
		
		return result;
	}
	
}
