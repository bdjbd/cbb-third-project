package com.am.cro.orderPayment;


import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.cro.entity.CarRepairOrder;
import com.am.frame.pay.PayManager;
import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.callback.IBusinessCallBack;
import com.am.frame.transactions.rechange.Rechange;
import com.fastunit.jdbc.DB;

/*
 * 说明:<br />
 * 订单支付完成处理类
 */
public class OrderPaymentCallBack implements IBusinessCallBack {

	private Logger logger=LoggerFactory.getLogger(getClass());
	//入参：支付ID（交易记录ID）
	@Override
	public String callBackExec(String id, String business, DB db, String type)
			throws Exception {
		logger.info("正在执行回调类.................."+id);
		//引入支付管理类
		PayManager payManager = new PayManager();
		//根据交易记录ID判断当前用户所使用的账户类型，是否为现金账户
		String sa_code = payManager.getSaCode(id,db);
		//一个订单对象，刚好是这笔交易所对应的订单信息，以下全是该实体类的get方法
		CarRepairOrder order=payManager.getOrder(id,db);
		String orderId = order.getId();//订单ID
		String orgcode = order.getOrgCode();//所属机构
		String memberId = order.getMemberId();//会员ID
		int money = (int) order.getTotalMoney();//订单金额
		String orderExplain = order.getOrderExplain();//订单说明
		//从业务参数得到支付流水号（这个好像用不上额）
		JSONObject jsonObject = new JSONObject(business);
		String payment_id = jsonObject.getString("payment_id");
		//线上支付成功后，修改订单表的支付方式为“线上支付”，支付状态为“已支付”
		payManager.updateOrderPayState(orderId, db);
		//如果支付账户是现金账户，说明充值时已经增加过积分了，则此处不增加积分
		//若不是现金账户，则增加会员积分，判断是否升级
		if (!sa_code.equals("CASH_ACCOUNT")) 
		{
			//更新积分
			payManager.updateMemberScore(memberId,money, db);
			//向会员积分记录表增加一条积分信息
			payManager.addMemberScore(memberId,money,orderExplain, db);
			//修改会员等级
			payManager.updateMemberLevel(memberId, db);
			
			//如果支付方式是微信、支付宝，并且第三方支付成功，第三方扣款完成，则现金账户不再出账，并且直接给汽修厂账户入账
			//引入充值管理类
			Rechange rechange = new Rechange();

			JSONObject obj = new JSONObject();
			//交易总金额  元转分，再化成字符串，便于截取整数部分金额
			String str=(order.getTotalMoney()*100)+"";
			//交易总金额（单位：分），只截取整数部分金额
			Long money1 = Long.valueOf(str.substring(0,str.indexOf(".")));
			
			//向汽修厂系统现金账户，进行入账充值操作（单位：分）
			obj = rechange.rechangeExc(money1,SystemAccountClass.GROUP_CASH_ACCOUNT,orgcode);
			if(obj != null && "0".equals(obj.getString("code"))){
				logger.info("充值结果" + obj);
			}
			
		}
		return "";
	}

}
