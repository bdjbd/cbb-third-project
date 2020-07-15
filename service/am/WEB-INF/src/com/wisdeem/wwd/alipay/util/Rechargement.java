package com.wisdeem.wwd.alipay.util;

import java.util.HashMap;
import java.util.Map;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.action.DefaultAction;
import com.wisdeem.wwd.alipay.config.AlipayConfig;

/**
 * 企业充值
 * 
 * @author liyushuang
 * @date 2013-12-18
 */
public class Rechargement extends DefaultAction {
	// 订单主键
	String alipay_id = "";

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// 保存订单
		saveRacharge(db, ac);

		Map<String, String> sPara = new HashMap<String, String>();

		String findAllSQL = "SELECT * FROM ws_alipay_order WHERE alipay_id='"+alipay_id+"' ";
		MapList strMap = db.query(findAllSQL);

		sPara.put("out_trade_no", strMap.getRow(0).get("out_trade_no"));// 商户网站唯一订单号
		sPara.put("subject", strMap.getRow(0).get("subject"));// 商品名称
		sPara.put("seller_email", strMap.getRow(0).get("seller_email"));// 卖家支付宝账号
		sPara.put("total_fee", strMap.getRow(0).get("total_fee"));// 交易金额
		sPara.put("payment_type", AlipayConfig.payment_type);// 支付类型
		sPara.put("service", AlipayConfig.service);
		sPara.put("partner", AlipayConfig.partner);
		sPara.put("_input_charset", AlipayConfig._input_charset);
		sPara.put("sign_type", AlipayConfig.sign_type);
		sPara.put("notify_url", AlipayConfig.notify_url);
		sPara.put("return_url", AlipayConfig.return_url);
		sPara.put("anti_phishing_key", AlipaySubmit.buildRequestMysign(sPara));

		String sHtmlText = AlipaySubmit.buildRequest(sPara, "get", "确认");
		ac.setSessionAttribute("APPLY_FORM", sHtmlText);
		ac.getActionResult().setScript("window.open('/domain/wwd/apply/submit.jsp','newwindow','height=700,width=1200,resizable=yes');");
	}

	private String saveRacharge(DB db, ActionContext ac) throws JDBCException {
		// 商户网站唯一订单号
		String out_trade_no = UtilDate.getOrderNum();
		Table table = ac.getTable("WS_ALIPAY_ORDER");
		TableRow ctable = table.getRows().get(0);
		ctable.setValue("out_trade_no", out_trade_no);
		ctable.setValue("seller_email", AlipayConfig.seller_email);
		ctable.setValue("payment_type", AlipayConfig.payment_type);
		int quantity = Integer.parseInt(ac.getRequestParameter("ws_alipay_order.form.quantity"));
		if (quantity <= 0) {
			ac.getActionResult().addErrorMessage("购买数量：无效的整数");
			ac.getActionResult().setSuccessful(false);
		}
		db.save(table);
		String findAlipayIdSQL = "SELECT alipay_id FROM ws_alipay_order WHERE out_trade_no='"+out_trade_no+"' ";
		MapList strMap = db.query(findAlipayIdSQL);
		alipay_id = strMap.getRow(0).get("alipay_id");
		return alipay_id;
	}
}
