package com.p2p.order;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.TaskEngine;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.business.DispatcherOrderService;
import com.p2p.pay.MemberPayment;
import com.p2p.pay.PayManager;
import com.p2p.recharge.RechargeManager;
import com.p2p.task.UserTaskManage;
import com.wisdeem.wwd.WeChat.Utils;
import com.wisdeem.wwd.WeChat.beans.Order;

/**
 * 订单管理类
 * @author Administrator
 *
 */
public class OrderManager {
	
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	private static OrderManager ordManager;
	
	private static final String tag="com.p2p.order.OrderManager";
	
	private OrderManager(){}
	
	public static OrderManager getInstance(){
		if(ordManager==null){
			ordManager=new OrderManager();
		}
		return ordManager;
	}
	
	
	
	/**
	 * 根据订单编号获取订单
	 * @param orderCode
	 * @return
	 */
	public Order getOrderByOrderCode(String orderCode){
		
		Order order=null;
		DB db =null;
		try{
			db=DBFactory.newDB();
			
			String sql="SELECT * FROM ws_order WHERE order_code=?";
			
			MapList map=db.query(sql, new String[]{orderCode}, new int[]{Type.VARCHAR});
			
		}catch(JDBCException e){
			e.printStackTrace();
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		return order;
	}
	
	
	/**
	 * 
	 * @param params
	 *http://www.xxx.com/create_direct_pay_by_user-JAVA-UTF-8/return_url.jsp?
	*body=GL-5484%E5%96%B7%E5%B0%84%E8%99%B9%E5%90%B8%E5%BC%8F%E8%BF%9E%E4%BD%93%E5%BA%A7%E4%BE%BF%E5%99%A8
	*&buyer_email=yuebin616%40126.com
	*&buyer_id=2088302428817113
	*&exterface=create_direct_pay_by_user
	*&is_success=T&notify_id=RqPnCoPT3K9%252Fvwbh3InQ9JXA9lNVUSuyvL8eUmMPox1PzJp1Pin9%252FHldBx8H3xtOiLb5
	*&notify_time=2014-10-05+19%3A10%3A51
	*&notify_type=trade_status_sync
	*&out_trade_no=ekx201410050102
	*&payment_type=1
	*&seller_email=ekx_ekx%40163.com
	*&seller_id=2088511712122828
	*&subject=GL-5484%E5%96%B7%E5%B0%84%E8%99%B9%E5%90%B8%E5%BC%8F%E8%BF%9E%E4%BD%93%E5%BA%A7%E4%BE%BF%E5%99%A8
	*&total_fee=0.75
	*&trade_no=2014100526107511
	*&trade_status=TRADE_SUCCESS
	*&sign=415ca834990a113da4b2549e1e4830f0
	*&sign_type=MD5
	 */
	public void processAlipayOrder(Map<String,String> params ){
		
		//商户订单号
		String outTradeNo=params.get("out_trade_no");
		//支付宝订单号
		String tradeNo=params.get("trade_no");
		//支付金额
		String payMoney=params.get("total_fee");
		//交易结果
		//String tradeStatus=params.get("trade_status");
		
		OutTradeNo outtno=parseOutTradeNo(outTradeNo);
		
		String orderCode=outtno.getOrderCode();
		
		Order order=getOrderByOrderCode(orderCode);
		
		if(Checker.isEmpty(payMoney)||payMoney.equalsIgnoreCase("null")){
			payMoney=order.getTotal()+"";
		}
		
		MemberPayment memberPay=new MemberPayment();
		
		memberPay.setAlipayOrderCode(tradeNo);
		
		memberPay.setPayCode(outTradeNo);
		
		
		
		//设置支付单号
		if(order!=null){
			memberPay.setPayContent("支付"+order.getOrderCode()+"订单。");
			memberPay.setPaySource(1);
		}else{
			memberPay.setPayContent("现金充值"+payMoney+"元");
			memberPay.setPaySource(2);
		}
		
		memberPay.setPayMoney(Double.valueOf(payMoney));
		memberPay.setMemberCode(outtno.getMembeCode());
		
		PayManager payManager=PayManager.getInstance();
		payManager.createMemberPayment(memberPay);
		
		if(order!=null){
			
			//更新订单状态为已下单
			updateOrderDataStatus(order.getOrderCode(),"2");
			//调用自动派单
			DispatcherOrderService.dispatcherOrder(orderCode);
		}
		
		if("c".equalsIgnoreCase(outtno.getPrefix()))//充值任务
		{
			
			//更新用户金额
			RechargeManager rechargeManager=RechargeManager.getInstance();
			rechargeManager.updateMemberCash(memberPay.getMemberCode(),memberPay.getPayMoney());
			
			
			Utils.Log(tag, "更新会员的充值任务");
			
			UserTaskManage userTaskManager=UserTaskManage.getInstance();
			
			

			//执行会员信息完善任务
			RunTaskParams taskParams=new RunTaskParams();
			taskParams.setMemberId(memberPay.getMemberCode());
			taskParams.setTaskName("充值任务");//企业任务名
			logger.info("充值任务");
			//执行任务
			TaskEngine.getInstance().executTask(taskParams);
			
			//更新会员任务
			//userTaskManager.updateUserRechargeTask(memberPay.getPayMoney(), memberPay.getMemberCode());
		}
	}
	
	
	/**
	 * 更新订单状态
	 * @param orderCode
	 * @param dataStatus
	 */
	public void updateOrderDataStatus(String orderCode,String dataStatus){
		try{
			
			String sql="UPDATE ws_order SET data_status=? WHERE order_code=?";
			
			DB db=DBFactory.getDB();
			
			db.execute(sql, 
					new String[]{dataStatus,orderCode}, 
					new int[]{Type.INTEGER,Type.VARCHAR});
			
		}catch(JDBCException e){
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 解析商户订单号
	 * @param outTradeNo  商户订单号，
	 * <br />如果是订单格式为:d@membercode@订单编号，
	 * <br />如果是充值格式为：c@membercode@充值单号
	 * @return
	 */
	public OutTradeNo parseOutTradeNo(String outTradeNo){
		return new OutTradeNo(outTradeNo);
	}
	
	
	/**
	 * 货到订单编号
	 * @param orgid 
	 * @return  订单编号
	 */
	public String getOrderCode(String orgid){
		String orderCode=Utils.getOrderCode(orgid);
		return orderCode;
	}
}
