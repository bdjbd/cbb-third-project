package com.am.frame.systemAccount.action;

import java.util.UUID;

import org.json.JSONObject;

import com.am.frame.systemAccount.bean.TransactionBusinessBeans;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.Action;
import com.fastunit.util.Checker;

/** 
 * @author  wz  
 * @descriptions  后台向他人转账action 
 * @date 创建时间：2016年4月21日 下午3:35:09 
 * @version 1.0   
 */
public class VirementToOtherAction implements Action{

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		
		
		DB db=DBFactory.newDB();
		
		//获取拼接业务回调参数 business参数集合
		TransactionBusinessBeans business = new TransactionBusinessBeans();
		JSONObject businessObj = null;
		JSONObject actionObj = null;
		
		/**
		 * 出账会员id
		 */
		String outMemberId = ac.getVisitor().getUser().getOrgId();
		
		/**
		 * 出账sa_code
		 */
//		String outAccountCode = ac.getRequestParameter("mall_account_other_virement.form.out_account_id");
		String outAccountCode = null;
		/**
		 * sa_code
		 */
		
		String inAccountCode = null;
		
		/**
		 * 最大金额限制
		 */
		String max_long = "";
		
		/**
		 * 最大金额限制提示
		 */
		String max_message = "超出最大可操作金额，无法进行操作，请重新输入！";
		
		/**
		 * 入账人账号
		 */
		//String inMemberId = ac.getRequestParameter("mall_account_other_virement.form.out_other_memberid");
		String inMemberId = ac.getRequestParameter("mall_account_other_virement.form.out_other_memberid");
		
		String person_remaker = ac.getRequestParameter("mall_account_other_virement.form.other_person_reason");
		
		/**
		 * 转账金额
		 */
		String outMoney = ac.getRequestParameter("mall_account_other_virement.form.out_other_money");
		
		String msql ="";
		
		if(!Checker.isEmpty(inMemberId))
		{
			msql ="select * from mall_account where loginaccount='"+inMemberId+"'";
		}
		
		
		if(business.getFormatBusiness(ac).has("business") && business.getFormatBusiness(ac).has("action_params"))
		{
			businessObj = business.getFormatBusiness(ac).getJSONObject("business");
			businessObj.put("person_remaker", person_remaker);
			actionObj = business.getFormatBusiness(ac).getJSONObject("action_params");
			//如果最大金额限制不为空则执行判断限制
			if(!Checker.isEmpty(actionObj.getString("max_long")))
			{
				max_long = actionObj.getString("max_long");
			}
			
			//判断最大金额限制信息提示max_message
			if(!Checker.isEmpty(actionObj.getString("max_message")))
			{
				max_message = actionObj.getString("max_message");
			}
			
			if(!Checker.isEmpty(actionObj.getString("out_money")))
			{
				outMoney = actionObj.getString("out_money");
			}
			if(!Checker.isEmpty(actionObj.getString("in_member_id")))
			{
				inMemberId = actionObj.getString("in_member_id");
				msql ="select * from mall_account where id='"+inMemberId+"'";
			}
			
			inAccountCode = actionObj.getString("in_group_account_code");
			outAccountCode = actionObj.getString("out_group_account_code");
		}
		
		outAccountCode = ac.getRequestParameter("mall_account_other_virement.form.out_other_accountid");
		
		inAccountCode = ac.getRequestParameter("mall_account_other_virement.form.in_other_accountid");
		
		
		MapList malist = db.query(msql);
		
		if(Checker.isEmpty(malist))
		{
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("请输入正确的账号");
			return ac;
		}else
		{
			inMemberId = malist.getRow(0).get("id");
		}
		
		//获取副主任
		String sql = "SELECT second_director,director FROM mall_auditpersonsetting WHERE orgid= '"+outMemberId+"' ";
		
		MapList list = db.query(sql);
		
		if(Checker.isEmpty(list)){
			//检查是否这是主管，副主管
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("没有设置主管和副主管！请先设置主管和副主管");
			return ac;
		}
		
		String second_director = list.getRow(0).get("second_director");
		String director = list.getRow(0).get("director");
		
		String id = UUID.randomUUID().toString();
		
		VirementManager vm = new VirementManager();
		MapList maplist = vm.getAccountInfo(db, outAccountCode, outMemberId);
		//手续费
		double fee = Double.parseDouble(maplist.getRow(0).get("transfer_fee_ratio"));
		long counter_fee =  (long)Math.floor(VirementManager.changeY2F(outMoney)*(new Float(fee)));
		
		
		MapList inList = vm.getAccountInfo(db, inAccountCode, inMemberId);
		
		//判断最大金额限制
		if(!Checker.isEmpty(max_long))
		{
			if(Long.parseLong(max_long) < Long.parseLong(outMoney))
			{
				ac.getActionResult().setSuccessful(false);
				ac.getActionResult().addErrorMessage(max_message);
				return ac;
			}
		}
		
		if(Checker.isEmpty(outMoney))
		{
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("请输入金额！");
			return ac;
		}else
		{
			if(Long.parseLong(outMoney)<=0)
			{
				ac.getActionResult().setSuccessful(false);
				ac.getActionResult().addErrorMessage("请输入正确金额！");
				return ac;
			}
		}
		
		String varInAccount= null;
		
		if(!Checker.isEmpty(inList))
		{
			varInAccount = inList.getRow(0).get("id");
		}else
		{
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("输入的用户财务账户不存在，请仔细查询 后再进行操作！");
			return ac;
		}
		
		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" INSERT INTO  mall_transfer(id,member_id,in_member_id,out_account_id,in_account_id,cash_withdrawal,counter_fee,settlement_state,audit_person,second_director,director,business,person_remaker) ");
		if(Checker.isEmpty(varInAccount))
		{
			inMemberId = "";
			insertSql.append(" VALUES('"+id+"','"+outMemberId+"','null','"+maplist.getRow(0).get("id")+"','"+varInAccount+"',"+VirementManager.changeY2F(outMoney)+","+counter_fee+",0,'"+second_director+"','"+second_director+"','"+director+"','"+businessObj.toString()+"','"+person_remaker+"') ");
		}else
		{
			insertSql.append(" VALUES('"+id+"','"+outMemberId+"','"+inMemberId+"','"+maplist.getRow(0).get("id")+"','"+varInAccount+"',"+VirementManager.changeY2F(outMoney)+","+counter_fee+",0,'"+second_director+"','"+second_director+"','"+director+"','"+businessObj.toString()+"','"+person_remaker+"') ");
		}
		
		
		
		
		int res = db.execute(insertSql.toString());
		if(res != 0){
			ac.getActionResult().setSuccessful(true);
			ac.getActionResult().addSuccessMessage("保存申请成功，点击提交后可进行提交审核！");
			ac.getActionResult().setUrl("/am_bdp/mall_transfer.form.do?m=e&mall_transfer.form.id="+id+"");
		}
		
	
		if(db!=null){
			try {
				db.close();
			} catch (JDBCException e) {
				e.printStackTrace();
			}
		}
		return ac;
		
	
		
		
		/**
		 * 调用转账接口方法执行转账操作
		 */
//		VirementManager vm = new VirementManager();
//		
//		JSONObject jso = vm.execute(db,outMemberId,inMemberId, outAccountCode, inAccountCode, outMoney,"账户转入操作","账户转出操作","",true);
//		
//		if("1000".equals(jso.get("code"))){
//			ac.getActionResult().setSuccessful(false);
//			ac.getActionResult().addErrorMessage(jso.getString("msg"));
//		}else{
//			ac.getActionResult().setSuccessful(true);
//			ac.getActionResult().addSuccessMessage("转账成功");;
//		}
	
	}

}
