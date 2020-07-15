package com.ambdp.agricultureProject.action;

import com.fastunit.Ajax;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月21日
 *@version
 *说明：计算退股金额Action
 */
public class CalculationWithdrawShareMoneyAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// 购买股份记录表的ID
		String Id = ac.getRequestParameter("mall_withdrawal_stock_record.form.id");
		// 购买总金额(元)
		String totalAmount = ac.getRequestParameter("mall_withdrawal_stock_record.form.total_amount");
		// 退股期限(天)
		String withdrawalLimit = ac.getRequestParameter("mall_withdrawal_stock_record.form.withdrawal_limit");
		// 违约金比例(%)
		String penaltyRatio = ac.getRequestParameter("mall_withdrawal_stock_record.form.penalty_ratio");
		
		if (!Checker.isEmpty(Id)) {
			String difference="true";
			String returnAmount="0";
			//购买时间加上退股期限比当前时间    返回true 不扣手续费   返回 false 扣除手续费 
			String differenceSql = "SELECT (create_time+'"+withdrawalLimit+" days'<now())::VARCHAR AS difference FROM mall_buy_stock_record WHERE id='"+Id+"' ";
			MapList differenceMap =db.query(differenceSql);
			if(!Checker.isEmpty(differenceMap)){
				difference = differenceMap.getRow(0).get("difference");
			}
			//返回true 不扣手续费
			if("true".equals(difference)){
				returnAmount=totalAmount;
			}else{
				//购买金额-购买金额*违约金比例/100
				String returnAmountSql = "SELECT "+totalAmount+"-"+totalAmount+"*"+penaltyRatio+"/100 AS returnamount";
				MapList returnAmountMap =db.query(returnAmountSql);
				if(!Checker.isEmpty(returnAmountMap)){
					returnAmount = returnAmountMap.getRow(0).get("returnamount");
				}
				
			}
			Ajax ajax=new Ajax(ac);
			
			ajax.addScript("document.getElementById(\"mall_withdrawal_stock_record.form.return_amount\").value='"+returnAmount+"'");
			ajax.send();
		}
	}
}
