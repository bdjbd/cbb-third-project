package com.am.frame.other.taskInterface.impl.zyb.xmHanlder;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.am.frame.other.taskInterface.impl.zyb.IProcessZybCallBack;
import com.am.frame.payment.RefundManager;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.order.OrderManager;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年6月27日
 * @version 
 * 说明:<br />
 * 智游宝接口结果处理类
 * 如果接口处理完成，可以设置 ProcessZybCallBack callBack;处理业务数据 
 * 
 */
public class ZybSubmitProcessXMLHandler  extends DefaultHandler{

	protected Logger logger=LoggerFactory.getLogger(getClass());
	
	/***任务状态（0=待执行【首次执行及重试均为该状态】，1=成功，3=失败【经过重试后失败】）**/
	protected String result="0";
	protected DB db;
	protected String taskRecordId;
	protected String requestData;
	protected String responseData;
	
	//任务重试次数
	protected long taskRetryTime;
	//任务最大重试次数
	protected long maxRetryTime;
	//业务数据 
	protected String busseData;
	
	//扩展业务数据 
	protected Map<String,String > extendData=new HashMap<String,String>();
	
	//结果处理完成后回调接口 
	protected IProcessZybCallBack callBack;
	
	protected Stack<String> stack = new Stack<String>();
	
	
	/**AM 系统订单号 **/
	public static final String orderCode="orderCode";
	/**辅助码**/
	public static final String assistCheckNo="assistCheckNo";
	/**智游宝订单号**/
	public static final String zybOrderCode="zybOrderCode";
	
	
	public ZybSubmitProcessXMLHandler(DB db, String taskRecordId,
			String requestData, String responseData, long taskRetryTime,
			long maxRetryTime, String busseData){
		this.db=db;
		this.taskRecordId=taskRecordId;
		this.requestData=requestData;
		this.responseData=responseData;
		this.taskRetryTime=taskRetryTime;
		this.maxRetryTime=maxRetryTime;
		this.busseData=busseData;
	}
	
	
	
	@Override
	public void startElement(String uri, String localName,
			String qName, Attributes attributes) throws SAXException {
		stack.push(qName);
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		
		String tag = stack.peek();
		String value=new String(ch,start,length).trim();
		
		if("code".equals(tag)){
			if("0".equals(value)){
				logger.info("订单下单接口成功,订单ID:"+busseData);
				result="1";
			}else if(
					value!=null&&!"".equals(value.trim())
					&&!"1".equals(result)){
				logger.info("ZybSubmitProcessXMLHandler() = 下单失败，是否应退款");
				
				logger.info("下单失败,订单ID:"+busseData);
				
				try {
					if(isReturnMoney(db,busseData)){
						updateOrderState(db,busseData);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				logger.info("ZybSubmitProcessXMLHandler() = 下单失败，应该去退款！");
				
				if(taskRetryTime<maxRetryTime){
					result="0";
				}else{
					result="3";
				}
			}
		}
		if("orderCode".equals(tag)){
			if(value!=null&&value.length()>1){
				logger.info("订单号："+value);
				if(extendData.get(zybOrderCode)==null){
//				if(zybOrderCode==null){
					//智游宝订单号
					extendData.put(zybOrderCode, value);
					logger.info("智游宝订单号："+value);
				}else{
					//我们系统订单号
					extendData.put(orderCode, value);
					logger.info("我们系统订单号："+value);
				}
			}
		}
		if("assistCheckNo".equals(tag)){
			if(value!=null&&value.length()>1){
				logger.info("获取辅助码:"+value);
				
				extendData.put(assistCheckNo, value);
				
			}
		}
	}
	
	@Override
	public void endDocument() throws SAXException {
		if(callBack!=null){
			
			extendData.put("", orderCode);
			extendData.put("", assistCheckNo);
			extendData.put("", zybOrderCode);
			
			callBack.process(db,  taskRecordId,
					 requestData,  responseData,  taskRetryTime,
					 maxRetryTime, busseData,result,extendData);
		}
	}

	public IProcessZybCallBack getCallBack() {
		return callBack;
	}

	public void setCallBack(IProcessZybCallBack callBack) {
		this.callBack = callBack;
	}

	public String getResult() {
		return result;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public String getAssistCheckNo() {
		return assistCheckNo;
	}

	public String getZybOrderCode() {
		return zybOrderCode;
	}

	public String getBusseData() {
		return busseData;
	}

	public long getTaskRetryTime() {
		return taskRetryTime;
	}

	public long getMaxRetryTime() {
		return maxRetryTime;
	}
	
	private void updateOrderState(DB db,String orderId) throws Exception{
		String queryOrderSQL=" SELECT id,trim(to_char(totalprice,'99999999990D99')) AS totalprice,paymentdate, "
				+ " memberid,commodityid,mall_class,pay_id   "
				+ " FROM mall_MemberOrder WHERE id='"+orderId+"' ";
		
		MapList orderMap=db.query(queryOrderSQL);
		
//		if(!Checker.isEmpty(orderMap)){
//			String paymentdate = orderMap.getRow(0).get("paymentdate");
//			if(Checker.isEmpty(paymentdate)){
				String editSql = "update mall_MemberOrder set orderstate = '2' where id = '"+ orderId +"'";
				
				db.execute(editSql);
//			}
//			
//		}
		
		logger.debug("ZybCancelNewTaskImpl.updateOrderState() queryOrderSQL =" + queryOrderSQL);
		
		//获取会员订单 //订单信息
		MemberOrder order=new OrderManager().getMemberOrderById(busseData, db);
		
		JSONObject params=new JSONObject();
		
		logger.debug("ZybCancelNewTaskImpl.updateOrderState() orderMap.size() =" + orderMap.size());
		
		if(!Checker.isEmpty(orderMap)){
			Row row=orderMap.getRow(0);
			
			params.put("orderid", orderId);
			params.put("stateValue",order.getOrderState());
			params.put("failureStateVaule","");
			params.put("pay_id",row.get("pay_id"));
			params.put("memberId",row.get("memberid"));
			params.put("commodityid",row.get("commodityid"));
			params.put("mall_class",row.get("mall_class"));
			// 防止第一次部分退票，第二次全部退票
			String sql = "select * from mall_refund where orderid = '"+ orderId +"' order by applytime desc ";
			MapList map = db.query(sql);
			if(!Checker.isEmpty(map)){
				row = map.getRow(0);
				params.put("totalprice", row.get("applyrefundmenoy"));
			}else{
				params.put("totalprice",row.get("totalprice"));
			}
			params.put("reason","客户申请自动退款");
			
			logger.info("ZybCancelNewTaskImpl.updateOrderState() orderMap.size() ="+params.toString());
			
			//更新订单状态到退款处理中状态
			RefundManager  refundManager=new RefundManager();
			refundManager.doRefundManager(null, null, params);
			
		}
	}
	private boolean isReturnMoney(DB db,String id){
		boolean result = false;
		
		String sql = "select * from mall_memberorder where id = '"+id+"'";
		MapList map = null;
		try {
			map = db.query(sql);
			if(!Checker.isEmpty(map)){
				String paymentdate = map.getRow(0).get("paymentdate");
				if(!Checker.isEmpty(paymentdate) || paymentdate == null || paymentdate=="null"){
					result = true;
				}
			}
		} catch (JDBCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
}
