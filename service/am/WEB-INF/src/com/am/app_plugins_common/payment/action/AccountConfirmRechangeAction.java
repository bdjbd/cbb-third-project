package com.am.app_plugins_common.payment.action;

import java.util.UUID;

import org.json.JSONObject;

import com.am.frame.systemAccount.SystemAccountClass;
import com.fastunit.Ajax;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * 
 * @author yuebin
 *充值确认界面 确认Action<br />
 *1,完成充值界面参数拼接
 * memberId   :f193be7d-d9af-4fec-b3d7-4227915e18e6 会员ID      <br/>
 * inAccountCode  :  入账账号编码 转账时有      <br/>
 * outAccountCode : UNIONPAY_ACCOUNT_MODE  出帐账号编码      <br/>
 * 	pay_id : F7200D38-42EE-4BB0-A343-4D78E38D7EE7 支付ID      <br/>
 * pay_money : 44.8  支付金额，单元      <br/>
 * pay_type : 1           支付类型,支付类型  1 支付 2 充值      <br/>
 * account_type  :  账户类型  1 系统账户 2 支付宝 3 微信 4 银联      <br/>
 * business : {      <br/>
 * 						"payment_id":"F7200D38-42EE-4BB0-A343-4D78E38D7EE7",      <br/>
 * 						"memberid":"f193be7d-d9af-4fec-b3d7-4227915e18e6",      <br/>
 * 						"orders":"7DB39221647A452FB3606F3A0D4B79ED,",      <br/>
 * 						"paymoney":44.8,      <br/>
 * 						"success_call_back":"com.am.frame.order.process.OrderBusinessCallBack"      <br/>
 * 			}      <br/>
 * inremakes : ,502胶   入账描述      <br/>
 * outremakes : ,502胶   出账描述      <br/>
 * platform : 1    平台类型  1 移动端  2 pc端      <br/>
 */
public class AccountConfirmRechangeAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		String memberId=ac.getVisitor().getUser().getOrgId();
		
		
		String outAccountCode=SystemAccountClass.GROUP_ALIPAY_ACCOUNT_MODE_WEB+
				","+SystemAccountClass.GROUP_SCAN_WECHAT_ACCOUNT_MODE;
		
		String pay_id=UUID.randomUUID().toString();
		String pay_money=ac.getRequestParameter("account_rechange_manager.form.pay_money");
		String pay_type="2";//支付类型,支付类型  1 支付 2 充值
		String account_type="2";//账户类型  1 系统账户 2 支付宝 3 微信 4 银联
		String inremakes="";
		String outremakes="";
		String platform="2";
		String success_url="";
		
		JSONObject business=new JSONObject();
		
		//入账账号ID，为运营机构的现金账号
		String inAccountCode="";
		String querAccoutnCodeSQL="SELECT accinfo.* FROM mall_account_info AS accinfo "+
				" LEFT JOIN mall_system_account_class AS sac ON accinfo.a_class_id=sac.id "+
				" WHERE member_orgid_id='org_operation'  "+
				" AND sac.sa_code='"+SystemAccountClass.GROUP_CASH_ACCOUNT+"'";
		
		MapList map=db.query(querAccoutnCodeSQL);
		if(!Checker.isEmpty(map)){
			inAccountCode=map.getRow(0).get("id");
		}
		
		business.put("payment_id", pay_id);
		business.put("in_account_code", inAccountCode);
		business.put("memberid", memberId);
		business.put("orders", pay_id);
		business.put("paymoney", pay_money);
		business.put("payMemberId", memberId);
		business.put("success_call_back", "com.am.frame.systemAccount.business.AccountRechangeBusinessCallBack");
		business.put("outremakes",outremakes);
		
		
		String requestUrl="/am_bdp/common_confirm_payment.do?m=e"
				+ "&memberId="+memberId
				+ "&in_account_code="+inAccountCode
				+ "&out_account_code="+outAccountCode
				+ "&pay_id="+pay_id 
				+ "&pay_money="+pay_money
				+ "&pay_type="+pay_type
				+ "&account_type="+account_type
				+ "&business="+business.toString()
				+ "&inremakes="+inremakes
				+ "&outremakes="+outremakes
				+ "&platform="+platform
				+ "&success_url="+success_url
				+ "&commodityname=充值";
		
		Ajax ajax=new Ajax(ac);
		ajax.addScript("window.open('"+requestUrl+"')");
		ajax.send();
	}
	
}
