package com.am.frame.other.taskInterface.impl.zyb;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.xml.sax.InputSource;

//import com.am.b2c.Unread_message.UnreadMessage;
import com.am.frame.other.taskInterface.OtherInterfaceTaskManager;
import com.am.frame.other.taskInterface.impl.zyb.xmHanlder.ZybSubmitProcessXMLHandler;
import com.am.frame.payment.RefundManager;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.order.OrderManager;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年6月27日
 * @version 
 * 说明:<br />
 * 
 * 智游宝下单接口
 */
/****
<!-- 提交数据 -->
<?xml version="1.0" encoding="UTF-8"?>
<PWBRequest>
	<transactionName>SEND_CODE_REQ</transactionName>固定值
	<header>
		<application>SendCode</application>固定值
		<requestTime>2016-12-20</requestTime>
	</header>
	<identityInfo>
		<corpCode>TESTFX</corpCode>企业码
		<userName>admin</userName>用户名 
	</identityInfo>
	<orderRequest>
		<order>
			<certificateNo>330182198804273139</certificateNo>身份证号
			<linkName>庄工</linkName>联系人必填
			<linkMobile>13625814109</linkMobile>必填
			<orderCode>t20141204002226</orderCode>你们的订单编码（或别的），要求唯一，我回调你们通知检票完了的标识及取消订单
			<orderPrice>200.00</orderPrice>订单总价格
			<groupNo></groupNo>团号
			<payMethod></payMethod>支付方式值spot现场支付vm备佣金，zyb智游宝支付 
			<ticketOrders>
				<ticketOrder>
					<orderCode>t2014120400222601</orderCode>必填你们的子订单编码
					<credentials>              实名制的必填，非实名制可以不填
						<credential>
							<name>帅哥</name> （真实姓名）
							<id>330182198804273139</id> (实名制商品需要传多个身份证）
						</credential>
					</credentials>
					<price>100.00</price>票价，必填，线下要统计的
					<quantity>1</quantity>必填票数量
					<totalPrice>1.00</totalPrice>必填子订单总价
					<occDate>2014-12-09</occDate>必填日期（游玩日期）
					<goodsCode>20140331011721</goodsCode> 必填 商品编码，同票型编码
					<goodsName>商品名称</goodsName> -----商品名称 
					<remark>商品名称</remark> -----备注 
				</ticketOrder>
			</ticketOrders>
		</order>
	</orderRequest>
</PWBRequest>

<!-- 返回数据 如果返回不成功如何处理？增加计划任务，重复执行，直至成功为止  -->
<?xml version="1.0" encoding="UTF-8"?>
<PWBResponse>
	<transactionName>SEND_CODE_RES</transactionName>
	<code>0</code>
	<description>成功</description>
	<orderResponse>
		<order>
			<certificateNo>330182198804273139</certificateNo>
			<linkName>庄工</linkName>
			<linkMobile>13625814109</linkMobile>
			<orderCode>201308120000045137</orderCode><智游宝订单号>
			<orderPrice>1</orderPrice>
			<payMethod>vm</payMethod>支付方式
			<assistCheckNo>00055359</assistCheckNo>辅助码
			<src>interface</src>
			<ticketOrders>
				<ticketOrder>
					<orderCode>t20141204002226</orderCode><第三方订单号>
					<credentials>              实名制的必填，非实名制可以不填
						<credential>
							<name>帅哥</name> （真实姓名）
							<id>330182198804273139</id> (实名制商品需要传多个身份证,数量跟门票数量一致）
						</credential>
					</credentials>
					<totalPrice>1</totalPrice>
					<price>1</price>
					<quantity>2</quantity>
					<occDate>2014-12-09 00:00:00</occDate>
					<goodsCode>20140331011721</goodsCode>
					<goodsName>商品名称</goodsName>
					<remark>helloworld</remark>
				</ticketOrder>
			</ticketOrders>
		</order>
	</orderResponse>
</PWBResponse>

 ****/

public class ZybSendCodeReqOtherTaskImpl extends ZybTaskImplAbstract{

	
	@Override
	public void execute(DB db, String taskRecordId){
		
		try{
			
			MapList map=getTaskInfoMap(db,taskRecordId);
			
			if(!Checker.isEmpty(map)){
				
				OrderManager orderManager=new OrderManager();
				MapList orderMap=orderManager.getMemberOrderMapById(busseData, db);
				
				if(!Checker.isEmpty(orderMap)){
					
					//订单信息
					Row orderRow=orderMap.getRow(0);
					
					//请求时间
					requestTemplate=requestTemplate.replaceAll("\\$am\\{requestTime\\}",
							new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
					
					//身份证号码
					requestTemplate=requestTemplate.replaceAll("\\$am\\{certificateNo\\}","");
					requestTemplate=requestTemplate.replaceAll("\\$am\\{linkName\\}",nullRepalce(orderRow.get("contact"))); //联系人必填
					requestTemplate=requestTemplate.replaceAll("\\$am\\{linkMobile\\}",nullRepalce(orderRow.get("contactphone")));//>联系电话
					requestTemplate=requestTemplate.replaceAll("\\$am\\{orderCode\\}",nullRepalce(orderRow.get("id")));//你们的订单编码（或别的），要求唯一，我回调你们通知检票完了的标识及取消订单
					requestTemplate=requestTemplate.replaceAll("\\$am\\{orderPrice\\}",nullRepalce(orderRow.get("ftotalprice")));//订单总价格
					requestTemplate=requestTemplate.replaceAll("\\$am\\{ticketOrderCode\\}",nullRepalce(orderRow.get("id")));//必填你们的子订单编码
					requestTemplate=requestTemplate.replaceAll("\\$am\\{ticketPrice\\}",nullRepalce(orderRow.get("fsaleprice")));//票价，必填，线下要统计的
					requestTemplate=requestTemplate.replaceAll("\\$am\\{ticketPrice\\}",nullRepalce(orderRow.get("fsaleprice")));//票价，必填，线下要统计的
					requestTemplate=requestTemplate.replaceAll("\\$am\\{ticketQuantity\\}",nullRepalce(orderRow.get("salenumber")));//必填票数量
					requestTemplate=requestTemplate.replaceAll("\\$am\\{tickettotalPrice\\}",nullRepalce(orderRow.get("ftotalprice")));//必填子订单总价
					requestTemplate=requestTemplate.replaceAll("\\$am\\{oocDate\\}",nullRepalce(orderRow.get("fstart_date")));//必填日期（游玩日期）
					requestTemplate=requestTemplate.replaceAll("\\$am\\{goodsCode\\}",nullRepalce(orderRow.get("goods_code")));//必填 商品编码，同票型编码
//					requestTemplate=requestTemplate.replaceAll("\\$am\\{goodsCode\\}",nullRepalce("PFT20170427109737"));//必填 商品编码，同票型编码
					requestTemplate=requestTemplate.replaceAll("\\$am\\{goodsName\\}",nullRepalce(orderRow.get("commodityname")));//商品名称
					requestTemplate=requestTemplate.replaceAll("\\$am\\{goodsRemark\\}",nullRepalce(orderRow.get("commodityname"))+nullRepalce(orderRow.get("specname")));//备注
					
					logger.info("请求参数："+requestTemplate);
					
					
					String sign=DigestUtils.md5Hex("xmlMsg="+requestTemplate+key);
					
					String responseData=doPost(requestUrl,requestTemplate,sign);

					logger.info(responseData);
					
					taskRetryTime++;
					updateResponseData(db,taskRecordId,
							requestTemplate,responseData,taskRetryTime,
							maxRetryTime,busseData);
					
				}else{
					//订单信息不能存在
					logger.error("任务未找到对应的数据 taskRecordId："+taskRecordId);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}



	private void updateResponseData( final DB db, final String taskRecordId, final String requestData,
			final  String responseData,final  long taskRetryTime,final  long maxRetryTime,final  String busseData) throws Exception {
		
		ZybSubmitProcessXMLHandler hander=new ZybSubmitProcessXMLHandler(
				db, taskRecordId, requestData, responseData, taskRetryTime,
				 maxRetryTime,busseData);
		
		hander.setCallBack(new IProcessZybCallBack() {

			@Override
			public String process(DB db, String taskRecordId,
					String requestData, String responseData,
					long taskRetryTime, long maxRetryTime, String busseData,
					String result, Map<String, String> extendData) {
				try {
					//处理记录
					Table recordTable=new Table(OtherInterfaceTaskManager.DOMAIN, "am_other_task_record");
					TableRow updateRow=recordTable.addUpdateRow();
					
					updateRow.setOldValue("id", taskRecordId);
					
					updateRow.setValue("request_data", requestData);
					updateRow.setValue("response_data", responseData);
					updateRow.setValue("task_tate", result);
					updateRow.setValue("task_retry_time", taskRetryTime);
					
					db.save(recordTable);
					
					
					//处理业务
					//1,如果成功，更新业务状态，如果失败，不处理
					logger.info("提交数据 ——返回参数 result:"+result);
					logger.info("extendData:"+extendData);
					if("1".equals(result)){
						//下单处理成功
						//1,更新订单数据，辅助码，智游宝定订单号 //2，修改订单状态
						String updateSQL="UPDATE mall_MemberOrder SET "
								+ " assist_check_no=?,zyb_order_code=?,orderstate='320' WHERE id=? ";
						
						db.execute(updateSQL,new String[]{
								extendData.get(ZybSubmitProcessXMLHandler.assistCheckNo),
								extendData.get(ZybSubmitProcessXMLHandler.zybOrderCode),
								busseData
						},new int[]{
								Type.VARCHAR,Type.VARCHAR,Type.VARCHAR
						});
						
						//3，添加获取二维码任务
						OtherInterfaceTaskManager otherTaskManager=new OtherInterfaceTaskManager();
						
						otherTaskManager.createTask(db,
								"com.am.frame.other.taskInterface.impl.zyb.ZybQueryImgUriTaskImpl",
								busseData);
						
						logger.info("订单 id："+busseData+"获取二维码已提交任务到系统。"
								+ "任务编码：com.am.frame.other.taskInterface.impl.zyb.ZybQueryImgUriTaskImpl");
						
					}else{
						
						
						
//			    	   String sql = "update mall_memberorder set orderstate = '2' where id = '"+ busseData +"'";
//			    	   
//			    	   db.execute(sql);
//			    	   
//			    	   String searchSQL = "select * from mall_memberorder where id = '"+ busseData +"'";
//			    	   
//			    	   MapList map = db.query(searchSQL);
//			    	   
//			    	   if(!Checker.isEmpty(map)){
//			    		   
//			    		   String memberid = map.getRow(0).get("memberid");
//			    		   String total_price = map.getRow(0).get("totalprice");
//			    		   
//				    	   String id = UUID.randomUUID().toString();
//				    	   String insertsql = "insert into b2b_mail(id,publisher_id,object_type,member_id,mail_content,create_datetime,mail_title) "
//				    	   		+ "values('"+ id +"','"+ map.getRow(0).get("orgcode") +"','1','"+ memberid +"',"
//				    	   				+ "'订单号为"+ busseData +"的订单下单失败，支付金额已退回，请重新下单！','now()','下单失败通知')";
//				    	   db.execute(insertsql);
//				    	   
//				    	   UnreadMessage.getIstance().add_message(memberid, id);
//				    	   
//				    	   String getSa_class = "select * from mall_trade_detail where business_json is not null  order by create_time desc";
//				    	   
//				    	   MapList saMap = db.query(getSa_class);
//				    	   String sa_class_id = null;
//				    	   JSONObject json = null;
//				    	   String business_json = null;
//				    	   if(!Checker.isEmpty(saMap)){
//				    		   for(int i = 0; i < saMap.size(); i++){
//				    			   business_json = saMap.getRow(i).get("business_json");
//				    			   if(!Checker.isEmpty(business_json)){
////					    			   System.out.println(business_json);
//					    			   json = new JSONObject(business_json);
////					    			   System.out.println(json.isNull("orders"));
//					    			   if(!json.isNull("orders")){
//						    			   if(busseData.equals(json.get("orders"))){
//					    					   sa_class_id = saMap.getRow(i).get("sa_class_id");
//						    			   }
//					    			   }
//					    		   }
//				    		   }
//				    	   }
//				    	   
//				    	   String getCode = "select * from mall_system_account_class where id = '"+ sa_class_id +"'";
//				    	   
//				    	   String sa_code = null;
//				    	   MapList codeMap = db.query(getCode);
//				    	   if(!Checker.isEmpty(codeMap)){
//				    		   sa_code = codeMap.getRow(0).get("sa_code");
//				    	   }
//				    	   Rechange rec = new Rechange();
//				    	   
//				    	   Long money = (long) (Double.parseDouble(total_price) * 100);
//				    	   JSONObject json_obj = rec.rechangeExc(money, sa_code, memberid ,"退款");
				    	   
//			    	   }
						
					}
					
				} catch (JDBCException e) {
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}
		});
		
		 //创建一个新的字符串  
		SAXParserFactory factory = SAXParserFactory.newInstance();
		  // step 2: 获得SAX解析器实例
        SAXParser parser = factory.newSAXParser();
        // step 3: 开始进行解析
        // 传入待解析的文档的处理器
       if(!Checker.isEmpty(responseData)){
    	   StringReader sr = new StringReader(responseData);  
           InputSource is = new InputSource(sr);  
           parser.parse( is,hander); 
       }else{
    	 //处理记录
    	   
    	   logger.info("下单失败,订单ID:"+busseData);
    	   String result="0";
    	   
    	   logger.info("下单失败，进行退款。");
    	   
    	   updateOrderState(db,busseData);
    	   
    	   logger.info("下单失败，退款完毕。");
    	   
    	   
			if(taskRetryTime<maxRetryTime){
				result="0";
			}else{
				result="3";
			}
    	   
			Table recordTable=new Table(OtherInterfaceTaskManager.DOMAIN, "am_other_task_record");
			TableRow updateRow=recordTable.addUpdateRow();
			
			updateRow.setOldValue("id", taskRecordId);
			
			
			
			updateRow.setValue("request_data", requestData);
			updateRow.setValue("response_data", responseData);
			updateRow.setValue("task_tate", result);
			updateRow.setValue("task_retry_time", taskRetryTime);
			
			db.save(recordTable);
			
       }
    
    
    
	}
	
	
	
	private void updateOrderState(DB db,String orderId) throws Exception{
		String queryOrderSQL=" SELECT id,trim(to_char(totalprice,'99999999990D99')) AS totalprice,paymentdate, "
				+ " memberid,commodityid,mall_class,pay_id   "
				+ " FROM mall_MemberOrder WHERE id='"+orderId+"' ";
		
		MapList orderMap=db.query(queryOrderSQL);
		
		if(!Checker.isEmpty(orderMap)){
			String paymentdate = orderMap.getRow(0).get("paymentdate");
			if(Checker.isEmpty(paymentdate)){
				String editSql = "update mall_MemberOrder set orderstate = '2' where id = '"+ orderId +"'";
				
				db.execute(editSql);
			}
			
		}
		
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

}
