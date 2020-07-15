package com.am.frame.systemAccount.action;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.Action;

public class SelfReacharge  implements Action {
	/**日志记录**/
	private Logger logger=LoggerFactory.getLogger(getClass());
	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		DB db=DBFactory.newDB();
		/**
		 * 入账账号
		 */
		String id = ac.getRequestParameter("am_member_recharge.form.id");
		logger.info("id=>>"+id);
		/**
		 * 入账金额
		 */
		String rechargenum = ac.getRequestParameter("am_member_recharge.form.rechargenum");
		logger.info("rechargenum=>>"+rechargenum);
		/**
		 * 入账说明
		 */
		String explain = ac.getRequestParameter("am_member_recharge.form.explain");
		logger.info("explain=>>"+explain);
		
		
//		//充值金额元，转换为分
//				String str=(Double.parseDouble(rechargenum)*100)+"";
//				
//				Long money = Long.valueOf(str.substring(0,str.indexOf(".")));
//				
//				Rechange rechange = new Rechange();
//				
//				JSONObject obj = new JSONObject();
//				
//				obj = rechange.rechangeExc(str, SystemAccountClass.GROUP_CASH_ACCOUNT, id);
//				
//				logger.info("处理充值回调 执行转账回调 返回结果:"+obj);
//				
//				if(obj!=null&&"0".equals(obj.getString("code"))){
//				logger.info("充值成功!");
//		
		VirementManager vm = new VirementManager();
		
		JSONObject jso = vm.executeJin(db, "", id, "", SystemAccountClass.CASH_ACCOUNT, rechargenum, explain, "", "", false);
		
		logger.info("jso=>>"+jso);
		
		return ac;
	}
}
