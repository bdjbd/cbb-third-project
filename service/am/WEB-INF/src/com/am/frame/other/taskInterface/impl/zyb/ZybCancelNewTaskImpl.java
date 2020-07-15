package com.am.frame.other.taskInterface.impl.zyb;

import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

//import com.am.b2c.Unread_message.UnreadMessage;
import com.am.frame.other.taskInterface.OtherInterfaceTaskManager;
import com.am.frame.other.taskInterface.impl.zyb.xmHanlder.ZybCancelNewXMLHandler;
import com.am.frame.payment.RefundManager;
import com.am.frame.state.OrderFlowParam;
import com.am.frame.state.StateFlowManager;
import com.am.mall.beans.commodity.Commodity;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.commodity.CommodityManager;
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
 * @create 2016年6月28日
 * @version 
 * 说明:<br />
 *智游宝订单取消任务接口
 *
 */
public class ZybCancelNewTaskImpl  extends ZybTaskImplAbstract {

	protected Logger logger =LoggerFactory.getLogger(getClass());
	@Override
	public void execute(DB db, String taskRecordId) {
		try{
			
			MapList map=getTaskInfoMap(db,taskRecordId);
			
			if(!Checker.isEmpty(map)){
				
					//我们的订单号
					requestTemplate=requestTemplate.replaceAll("\\$am\\{orderCode\\}",busseData);
					
					logger.info("请求参数："+requestTemplate);
					
					String sign=DigestUtils.md5Hex("xmlMsg="+requestTemplate+key);
					
					String responseData=doPost(requestUrl,requestTemplate,sign);

					logger.info(responseData);
					
					taskRetryTime++;
					updateResponseData(db,taskRecordId,
							requestTemplate,responseData,taskRetryTime,
							maxRetryTime,busseData);
					
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}



	private void updateResponseData(final DB db,final String taskRecordId,final String requestData,
			final String responseData,final long taskRetryTime,final long maxRetryTime,final String busseData) throws Exception {
		
		ZybCancelNewXMLHandler hander=new ZybCancelNewXMLHandler(db,taskRecordId,requestData,
				responseData,taskRetryTime,maxRetryTime,busseData);
		
		hander.setCallBack(new IProcessZybCallBack() {
			
			@Override
			public String process(DB db, String taskRecordId,
					String requestData, String responseData,
					long taskRetryTime, long maxRetryTime, String busseData,String result,
					Map<String, String> extendData) {
				
				
				
				try {
					//处理记录
					//1,查询记录是否已经成立成功，如果处理成，则不在处理
					String  querySQL="SELECT * FROM am_other_task_record WHERE busse_data=?  ORDER BY create_time DESC ";
					
					MapList recodMap=db.query(querySQL,taskRecordId,Type.VARCHAR);
					
					String oldResult=null;
					
					if(!Checker.isEmpty(recodMap)){
						oldResult=recodMap.getRow(0).get("task_tate");
					}
					
					
					Table recordTable=new Table(OtherInterfaceTaskManager.DOMAIN, "am_other_task_record");
					TableRow updateRow=recordTable.addUpdateRow();
					
					updateRow.setOldValue("id", taskRecordId);
					updateRow.setValue("request_data", requestData);
					updateRow.setValue("response_data", responseData);
					updateRow.setValue("task_tate", result);
					updateRow.setValue("task_retry_time", taskRetryTime);
				
					db.save(recordTable);
					
					logger.info("订单流程信息处理："+result+",oldResult:"+oldResult);
					
					//处理业务
					//1,如果成功，更新业务状态，如果失败，不处理
					if("1".equals(result)){
						//订单取消成功  智游宝退单情况查询编号
						String updateSQL="UPDATE mall_MemberOrder SET "
								+ " retreat_Batch_No=? WHERE id=? ";
						
						db.execute(updateSQL,new String[]{
								extendData.get(ZybCancelNewXMLHandler.RETREAT_BATCH_NO),
								busseData
						},new int[]{
								Type.VARCHAR,Type.VARCHAR
						});
						
						logger.info("订单："+busseData+"\td订单取消成功："
								+extendData.get(ZybCancelNewXMLHandler.RETREAT_BATCH_NO));
					
						//执行订单取消Action
						//TODO  添加订单取消退款任务 
						try {
							updateOrderState(db, busseData);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						//订单信息
						MemberOrder order=new OrderManager().getMemberOrderById(busseData, db);
						//订单对应的商品信息
						Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID());
						Map<String,String> otherParam=new HashMap<String,String>();
						otherParam.put(OrderFlowParam.STATE_ACTON_CODE,"1112");
						logger.info("第一次:Orderstate="+order.getOrderState()+"第一次时间"+new Date());
						StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),"11",order.getId(),otherParam);
						logger.info("第二次:Orderstate="+order.getOrderState()+"第一次时间"+new Date());
						
						
//						退款方法
//						CancelOrderWebApi.returnMoney(busseData, db, order.getMemberID());
						
//						String tCode = "";
//						
//						// 更改退单状态
//						String sql = "update mall_refund set state='4' where orderid = '"+ busseData +"'";
//						
//						db.execute(sql);
//						
//						// 获取联系人手机号
//						String telephone = order.getContactPhone();
//						
//						// 查询退单信息
//						String resetSql = "select * from mall_refund where orderid = '"+ busseData +"'";
//						MapList resetMap = db.query(resetSql);
//						
//						// 退单金额
//						Long money = null;
//			    	    if(!Checker.isEmpty(resetMap)){
//			    		   money = (long)(resetMap.getRow(0).getDouble("applyrefundmenoy", 0) * 100);
//			    	    }
//						
//						String smsContent = Var.get("order_complete_msg_templ_r4");
//						
//						Row row = resetMap.getRow(0);
//						if(!Checker.isEmpty(smsContent)){
//							for(int i = 0; i < row.size(); i++){
//								String key = row.getKey(i);
//								if(key!=null)
//								{
//									String value=row.get(key);
//									value=value==null?"":value;
//									
//									if(!Checker.isEmpty(smsContent)&&row!=null&&key!=null)
//									{
//								
//										smsContent=smsContent.replaceAll("\\$am\\{"+key.toUpperCase()+"\\}",value);
//									}
//									
//								}
//							}
//							
////							SMSIdentifyingCode sic = new SMSIdentifyingCode("");
////							tCode = sic.getCode(smsContent, telephone);
//						}
//						
//						
//						String id = UUID.randomUUID().toString();
//						
//						String insertsql = "insert into b2b_mail(id,publisher_id,object_type,member_id,mail_content,create_datetime,mail_title) "
//				    	   		+ "values('"+ id +"','"+ order.getOrderCode() +"','1','"+ order.getMemberID() +"','" + smsContent + "','now()','退款通知。')";
//						db.execute(insertsql);
//						
//						
//						UnreadMessage.getIstance().add_message(order.getMemberID(), id);
//						
//						String getSa_class = "select * from mall_trade_detail where business_json is not null  order by create_time desc";
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
//					    					   break;
//						    			   }
//					    			   }
//					    		   }
//				    		   }
//				    	   }
//				    	   
//				    	   String getCode = "select * from mall_system_account_class where id = '"+ sa_class_id +"'";
//				    	   
//				    	   MapList codeMap = db.query(getCode);
//				    	   String sa_code = null;
//				    	   if(!Checker.isEmpty(codeMap)){
//				    		   sa_code = codeMap.getRow(0).get("sa_code");
//				    	   }
//				    	   
//				    	   Rechange rec = new Rechange();
//				    	   DBManager dd = new DBManager();
//				    	   
//				    	   MapList list = rec.getAccountInfo(dd, sa_code, order.getMemberID());
//				    	   String asql = "update mall_account_info "
//				    				+ " set balance='"+(Long.parseLong(list.getRow(0).get("balance"))+money)+"' where id ='"+list.getRow(0).get("id")+"'";
//							
//							db.execute(asql);
//							
//							String isql = "insert into mall_trade_detail (id,member_id,account_id,sa_class_id"
//									+ ",trade_time,trade_total_money,rmarks"
//									+ ",create_time,business_id,trade_type,trade_state,business_json"
//									+ ",is_process_buissnes,ty_pay_id) "
//									+ "values('"+UUID.randomUUID()+"','"+order.getMemberID()+"','"+list.getRow(0).get("id")+"','"+list.getRow(0).get("class_id")+"'"
//											+ ",now(),'"+money+"','充值',now(),'','2','1','','0','')";
//							db.execute(isql);
				    	   
//				    	   
//				    	   
//				    	   JSONObject json_obj = rec.rechangeExc(money, sa_code, order.getMemberID() ,"退款");
				    	   
				    	   
						
					}else if("3".equals(result)){
						logger.info("订单流程信息处理V1："+result+",oldResult:"+oldResult);
						//订单取消失败
//						String tCode = "";
//						
//						String sql = "update mall_refund set state='3' where orderid = '"+ busseData +"'";
//						
//						db.execute(sql);
//						
//						String searchSQL = "select * from mall_memberorder where id = '"+ busseData +"'";
//				    	   
//						MapList map = db.query(searchSQL);
//						
//						String telephone = null;
//						
//						String memberid = null;
//								
//						String orgcode = null;		
//						
//						if(!Checker.isEmpty(map)){
//							telephone = map.getRow(0).get("contactphone");
//							memberid = map.getRow(0).get("memberid");
//							orgcode = map.getRow(0).get("orgcode");
//						}
//						
//						String resetSql = "select * from mall_refund where orderid = '"+ busseData +"' ";
//						
//						MapList resetMap = db.query(resetSql);
//						
//						String smsContent = Var.get("order_complete_msg_templ_r3");
//						
//						Row row = resetMap.getRow(0);
//						if(!Checker.isEmpty(smsContent)){
//							for(int i = 0; i < row.size(); i++){
//								String key = row.getKey(i);
//								if(key!=null)
//								{
//									String value=row.get(key);
//									value=value==null?"":value;
//									
//									if(!Checker.isEmpty(smsContent)&&row!=null&&key!=null)
//									{
//								
//										smsContent=smsContent.replaceAll("\\$am\\{"+key.toUpperCase()+"\\}",value);
//									}
//									
//								}
//							}
//							
////							SMSIdentifyingCode sic = new SMSIdentifyingCode("");
////							tCode = sic.getCode(smsContent, telephone);
//						}
//						
//						
//						String id = UUID.randomUUID().toString();
//						
//						String insertsql = "insert into b2b_mail(id,publisher_id,object_type,member_id,mail_content,create_datetime,mail_title) "
//				    	   		+ "values('"+ id +"','"+ orgcode +"','1','"+ memberid +"','" + smsContent + "','now()','退款通知。')";
//						
//						db.execute(insertsql);
//						UnreadMessage.getIstance().add_message(memberid, id);
						if("1".equals(oldResult)){
							//订单信息
							MemberOrder order=new OrderManager().getMemberOrderById(busseData, db);
							//订单对应的商品信息
							Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID());
							Map<String,String> otherParam=new HashMap<String,String>();
							otherParam.put(OrderFlowParam.STATE_ACTON_CODE,"11120");
							StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),"11",order.getId(),otherParam);
							
						}
						
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
        StringReader sr = new StringReader(responseData);  
        InputSource is = new InputSource(sr);  
        parser.parse( is,hander);
        
	}

	
	private void updateOrderState(DB db,String orderId) throws Exception{
		
		//门票退票成功后，更新退款申请表中的“退票时间” 
		String timeSql = "update mall_refund set cofrimtime='now()' where orderid ='"+orderId+"'";
		db.execute(timeSql);
		
		String queryOrderSQL=" SELECT id,trim(to_char(totalprice,'99999999990D99')) AS totalprice, "
				+ " memberid,commodityid,mall_class,pay_id   "
				+ " FROM mall_MemberOrder WHERE id='"+orderId+"' ";
		
		MapList orderMap=db.query(queryOrderSQL);
		
		
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
