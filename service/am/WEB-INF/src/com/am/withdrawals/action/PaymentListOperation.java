package com.am.withdrawals.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.transactions.withdraw.WithdrawService;
import com.fastunit.context.ActionContext;
import com.fastunit.context.LocalContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年9月8日
 * @version 
 * 说明:<br />
 * 银联，微信，支付宝付款
 */
public class PaymentListOperation extends DefaultAction {

	
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	
	@Override
	public void doAction(DB db, ActionContext ac) {
		
		// 获得选择列: _s_单元编号
		String[] select = ac.getRequestParameters("_s_withdrawals.list");
		// 获得主键: 单元编号.元素编号.k（设置了映射表时会自动用隐藏域记录主键值）
		String[] wids = ac.getRequestParameters("withdrawals.list.id.k");
		
		//'1' THEN '银行卡' WHEN '2' THEN '支付宝' WHEN '3' THEN '微信'
		String[] accountTypes=ac.getRequestParameters("withdrawals.list.in_account_type");
		
		//提现账号类型  '1' THEN '银行卡' WHEN '2' THEN '支付宝' WHEN '3' THEN '微信'
		String accountType="";
		
		List<String> payWids = new ArrayList<String>();
		
		HttpServletRequest request = LocalContext.getLocalContext().getHttpServletRequest();
		String ip=request.getRemoteHost();
		
		if (!Checker.isEmpty(select)) {
			for (int i = 0; i < select.length; i++) {
				if ("1".equals(select[i])) {// 1为选中
					payWids.add(wids[i]);
					
					accountType=accountTypes[i];
				}
			}
		}

		// 设置提示消息
		if (payWids.size()==0) {
			ac.getActionResult().addNoticeMessage("没有选择数据");
		} else {
			//进行提现付款操作
			logger.info("提现账号类型："+accountType);
			logger.info("提现ID:"+Arrays.toString(payWids.toArray()));
			
			try {
				new WithdrawService().executeWithdar(accountType, payWids, db, ip,ac);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
}
