package com.am.app_plugins_common.specRechange.action;

import java.net.URLEncoder;
import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.systemAccount.SystemAccountClass;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;


/**
 * 空间费用 确认续费 Action
 * @author yuebin
 * 
 * 1,保存数据
 * 2,跳转到支付界面
 *
 */
public class SapceConfirmRechangeAction extends DefaultAction {

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		Table table=ac.getTable("LXNY_SPACE_USAGE_FEE");
		
		TableRow tr=table.getRows().get(0);
		
		String memberId=ac.getRequestParameter("space_rechange.form.org_code");
		String orgName="";
		
		String querySQL="SELECT * FROM aorg WHERE orgid=? ";
		MapList map=db.query(querySQL, memberId, Type.VARCHAR);
		
		if(!Checker.isEmpty(map)){
			orgName=map.getRow(0).get("orgname");
		}
		
		//购买的年份
		String years=ac.getRequestParameter("space_rechange.form.years");
		//单价
		String price=ac.getRequestParameter("space_rechange.form.price");
		String pay_money=(Double.parseDouble(price)*Long.parseLong(years))+"";
		
		//入账账号为运营部的现金账号
		String inAccountCode=ac.getRequestParameter("space_rechange.form.in_account_id");
		
		String outAccountCode=SystemAccountClass.GROUP_ALIPAY_ACCOUNT_MODE_WEB+
				","+SystemAccountClass.GROUP_SCAN_WECHAT_ACCOUNT_MODE;
		
		String pay_id=UUID.randomUUID().toString();
		
		
		tr.setValue("buy_time_length", years);
		tr.setValue("transaction_amount", pay_money);
		tr.setValue("status", 0);
		tr.setValue("org_code",memberId);
		
		db.save(table);
		String id=tr.getValue("id");
		
		
		
		String pay_type="2";//支付类型,支付类型  1 支付 2 充值
		String account_type="2";//账户类型  1 系统账户 2 支付宝 3 微信 4 银联
		String inremakes="";
		String outremakes="";
		String platform="2";
		String success_url="";
		
		JSONObject business=new JSONObject();
		
		
		outremakes=orgName+"购买空间"+years+"年空间，总费用为"+pay_money;
		
		business.put("payment_id", pay_id);
		business.put("in_account_code", inAccountCode);
		business.put("memberid", memberId);
		business.put("orders", pay_id);
		business.put("paymoney", pay_money);
		business.put("success_call_back", "com.am.app_plugins_common.specRechange.callback.SpecRechangeBusinessCallBack");
		business.put("rechange_id", id);
		business.put("outremakes", outremakes);
		
		String requestUrl="/am_bdp/common_confirm_payment.do?m=e"
				+ "&memberId="+memberId
				+ "&in_account_code="+inAccountCode
				+ "&out_account_code="+outAccountCode
				+ "&pay_id="+pay_id 
				+ "&pay_money="+pay_money
				+ "&pay_type="+pay_type
				+ "&account_type="+account_type
				+ "&business="+URLEncoder.encode(business.toString(),"UTF-8")
				+ "&inremakes="+inremakes
				+ "&outremakes="+URLEncoder.encode(outremakes,"UTF-8")
				+ "&platform="+platform
				+ "&success_url="+success_url
				+ "&commodityname="+URLEncoder.encode(outremakes,"UTF-8")
				+ "&autoback=1";
		
		
		logger.info("设置跳转路径："+requestUrl);
		
		ac.getActionResult().setSuccessful(true);
		ac.getActionResult().setUrl(requestUrl);
		
	}
	
}
