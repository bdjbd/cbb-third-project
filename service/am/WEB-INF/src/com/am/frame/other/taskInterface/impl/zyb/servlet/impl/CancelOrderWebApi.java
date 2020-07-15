package com.am.frame.other.taskInterface.impl.zyb.servlet.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

//import com.am.b2c.Unread_message.UnreadMessage;
import com.am.frame.state.OrderFlowParam;
import com.am.frame.state.StateFlowManager;
import com.am.frame.transactions.rechange.Rechange;
import com.am.frame.webapi.db.DBManager;
import com.am.mall.beans.commodity.Commodity;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.commodity.CommodityManager;
import com.am.mall.order.OrderManager;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年6月28日
 * @version 
 * 说明:<br />
 * 智游宝退票通知接口 
 * 
orderCode第三方订单号
subOrderCode第三方子订单号
retreatBatchNo退票批次号（退票时智游宝返回）
auditStatus: failure/success审核状态:失败/成功
returnNum退票数量
sign 签名
我们系统会以审核完成时，回调下订单方系统。
审核完成：
url?retreatBatchNo={retreatBatchNo}&
orderCode={orderCode}&subOrderCode={subOrderCode}&auditStatus=${auditStatus}&
returnNum=1&sign=md5({orderCode}{privateKey})
 * 
 */
public class CancelOrderWebApi extends  ZybAbstraceWebApiService {

	@Override
	protected String processBusess(DB db, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
//		orderCode第三方订单号
//		subOrderCode第三方子订单号
//		retreatBatchNo退票批次号（退票时智游宝返回）
//		auditStatus: failure/success审核状态:失败/成功
//		returnNum退票数量
//		sign 签名
//		我们系统会以审核完成时，回调下订单方系统。
//		审核完成：
//		url?retreatBatchNo={retreatBatchNo}&orderCode={orderCode}
//		&subOrderCode={subOrderCode}&auditStatus=${auditStatus}&returnNum=1&sign=
//				md5({orderCode}{privateKey})

		String orderCode=request.getParameter("orderCode");//第三方订单号
		String subOrderCode=request.getParameter("subOrderCode");//subOrderCode;//第三方子订单号
		String retreatBatchNo=request.getParameter("retreatBatchNo");//retreatBatchNo;//退票批次号（退票时智游宝返回）
		String auditStatus=request.getParameter("auditStatus");//auditStatus;//: failure/success审核状态:失败/成功
		String returnNum=request.getParameter("returnNum");//returnNum;//退票数量
		String sign=request.getParameter("sign");//sign;// 签名 
		
		//1，根据订单号和退表批次号，查询订单
		OrderManager orderManager=new OrderManager();
		
		MapList orderMap=orderManager.getMemberOrderMapIdOrRetreatBatchNo(orderCode,retreatBatchNo, db);
		
		if(!Checker.isEmpty(orderMap)){
			
			//订单信息
			MemberOrder order=new OrderManager().getMemberOrderById(orderCode, db);
			
			// 会员ID
			String memberid = order.getMemberID();
			
			// 机构ID
			String orgcode = order.getOrgcode();
			//true=退票成功
			if("success".equals(auditStatus))
			{
				
				logger.info("订单退款信息说明："+orderMap.getRow(0).toString());
				//判断门票的有效数量大于0时则执行设置订单状态为待使用反之则退单成功
				if(!"0".equals(orderMap.getRow(0).get("effective_num")) && !Checker.isEmpty(orderMap.getRow(0).get("effective_num")))
				{
					logger.info("订单退款信息说明V1："+orderMap.getRow(0).toString());
//					退款方法
//		    	    returnMoney(orderCode, db, memberid);
		    	    
					//发送站内信,并添加未读站内信
					insertMail(orderCode, db, memberid, orgcode, "1");
					
					//修改状态至待使用
					String sql = "update mall_memberorder set orderstate='320' where id = '" + orderCode + "'";
					db.execute(sql);
					
					// 修改退单表状态
					changeReturnState(orderCode, "1", db);
				}
				else
				{
					logger.info("订单退款信息说明V2："+orderMap.getRow(0).toString());
					//更新退票数量
					String updateSQL=" UPDATE mall_MemberOrder SET return_num=? WHERE id=? ";
					
					db.execute(updateSQL,new String[]{returnNum,orderCode},new int[]{Type.INTEGER,Type.VARCHAR});
					
					
					//订单对应的商品信息
					Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID());
					Map<String,String> otherParam=new HashMap<String,String>();
					otherParam.put(OrderFlowParam.STATE_ACTON_CODE,"1113");
					StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),"11",order.getId(),otherParam);
					
					//退款方法
//		    	    returnMoney(orderCode, db, memberid);
		    	    
//		    	    //发送站内信,并添加未读站内信
//					insertMail(orderCode, db, memberid, orgcode, "1");
//					
					// 修改退单表状态
					changeReturnState(orderCode, "1", db);
				}
				
			}else{
				//退票失败
				logger.info("订单退票失败:订单ID："+orderCode+"\t退票批次:"+retreatBatchNo);
				
				// 修改退单表状态
				changeReturnState(orderCode, "2", db);

				//发送站内信,并添加未读站内信
				insertMail(orderCode, db, memberid, orgcode, "2");

				// 如果退款失败将订单状态改为待使用
//				String sql = "update mall_memberorder set orderstate = '320' where id = '" + orderCode + "'";
//				db.execute(sql);
				
				String updateSQL = "update mall_memberorder set return_num = return_num - '"+ Long.parseLong(returnNum) +"',effective_num=effective_num+'"+ Long.parseLong(returnNum) +"' "
						+ "where id = '"+ orderCode +"'";
				db.execute(updateSQL);
				//订单信息
//				MemberOrder order=new OrderManager().getMemberOrderById(orderCode, db);
//				订单对应的商品信息
				Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID());
				Map<String,String> otherParam=new HashMap<String,String>();
				otherParam.put(OrderFlowParam.STATE_ACTON_CODE,"11120");
				StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),"11",order.getId(),otherParam);
			}
		}else{
			logger.info("未找到订单ID："+orderCode+"\t 退款批号为："+retreatBatchNo);
		}
		
		//2，修改订单流程状态
		//3，更新订单数据
		
		return "success";
	}
	
	// 退单成功退款
	public static void returnMoney(String id,DB db,String memberid){
		// 查询退单信息
		String resetSql = "select * from mall_refund where orderid = '"+ id +"'";
		MapList resetMap = null;
		Long money = null;
		try {
			resetMap = db.query(resetSql);
		
		
			// 退单金额
		    if(!Checker.isEmpty(resetMap)){
			   money = (long)(resetMap.getRow(0).getDouble("applyrefundmenoy", 0) * 100);
		    }
		    
		    String getSa_class = "select * from mall_trade_detail where business_json is not null  order by create_time desc";
		    
	    	MapList saMap = db.query(getSa_class);
	    	String sa_class_id = null;
	    	JSONObject json = null;
	    	String business_json = null;
	    	if(!Checker.isEmpty(saMap)){
	    		for(int i = 0; i < saMap.size(); i++){
			    business_json = saMap.getRow(i).get("business_json");
			    	if(!Checker.isEmpty(business_json)){
			    			json = new JSONObject(business_json);
						    if(!json.isNull("orders")){
						    	String orders = String.valueOf(json.get("orders"));
						    	if(orders.indexOf(",") == -1){
								    if(id.equals(orders)){
								    	sa_class_id = saMap.getRow(i).get("sa_class_id");
								     	break;
									}
						    	}else{
						    		String arr[] = orders.split(",");
						    		for(int j = 0; j< arr.length; j++){
						    			if(id.equals(arr[j])){
						    				sa_class_id = saMap.getRow(i).get("sa_class_id");
									     	break;
						    			}
						    		}
						    	}
						    }
	     		    } 
			    }
		    }
			   
		    String getCode = "select * from mall_system_account_class where id = '"+ sa_class_id +"'";
		   
		    MapList codeMap = db.query(getCode);
		    String sa_code = null;
		    if(!Checker.isEmpty(codeMap)){
			    sa_code = codeMap.getRow(0).get("sa_code");
		    }
		    Rechange rec = new Rechange();
    	    DBManager dd = new DBManager();
		    
    	    MapList list = rec.getAccountInfo(dd, sa_code, memberid);
    	    String asql = "update mall_account_info "
    				+ " set balance='"+(Long.parseLong(list.getRow(0).get("balance"))+money)+"' where id ='"+list.getRow(0).get("id")+"'";
			
			db.execute(asql);
			
			
			String isql = "insert into mall_trade_detail (id,member_id,account_id,sa_class_id"
					+ ",trade_time,trade_total_money,rmarks"
					+ ",create_time,business_id,trade_type,trade_state,business_json"
					+ ",is_process_buissnes,ty_pay_id) "
					+ "values('"+UUID.randomUUID().toString()+"','"+memberid+"','"+list.getRow(0).get("id")+"','"+list.getRow(0).get("class_id")+"'"
							+ ",now(),'"+money+"','退款',now(),'','2','1','','0','')";
			db.execute(isql);
		} catch (JDBCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// 添加站内信方法
	public static void insertMail(String id,DB db,String memberid,String orgcode,String type){
		String uuid = UUID.randomUUID().toString();
		String insertsql = null;
		if("1".equals(type)){
			insertsql = "insert into b2b_mail(id,publisher_id,object_type,member_id,mail_content,create_datetime,mail_title) "
	    	   		+ "values('"+ uuid +"','"+ orgcode +"','1','"+ memberid +"','订单号为？的订单退单成功！','now()','退款通知。')";
		}else{
			insertsql = "insert into b2b_mail(id,publisher_id,object_type,member_id,mail_content,create_datetime,mail_title) "
	    	   		+ "values('"+ uuid +"','"+ orgcode +"','1','"+ memberid +"','订单号为？的订单退单失败！','now()','退款通知。')";
		}
		try {
			db.execute(insertsql);
		} catch (JDBCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 添加未读站内信(汽车公社无此功能，已注释掉了)
		//UnreadMessage.getIstance().add_message(memberid, id);
	}
	
	// 更改退单状态
	public static void changeReturnState(String id,String type,DB db){
		// 修改退单表状态SQL
		String sql = "";
		
		// 判断是否退单成功,1为成功(修改退单状态为4，退单成功)，2为失败(修改退单状态为3，退单失败)
		if("1".equals(type)){
			sql = "update mall_refund set state = '4' where orderid = '"+id+"'";
		}else{
			sql = "update mall_refund set state = '3' where orderid = '"+id+"'";
		}
		try {
			db.execute(sql);
		} catch (JDBCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
