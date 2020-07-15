package com.am.frame.transactions.callback;

import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.util.Checker;

/**
 *
 * 支付完成，提现完成，提现失败，充值成功，充值失败
 * 通过查看交易记录表中业务参数配置的 回调类来反射调用执行
 * 回调反射接口
 * "business":{
 * 		success_call_back:'com.am.xx.xx'
 * 		lost_call_back:'com.am.xx.xx'
 * }
 * @author mac
 */
public class BusinessCallBack {
	
	private Logger logger=LoggerFactory.getLogger(getClass());

	/**第三方订单号***/
	public static final String OTHER_ORDER_CODE ="BusinessCallBack.OTHER_ORDER_CODE";
	
	/**第三方返回资金 单位分**/
	public static final String CASH_FREE="BusinessCallBack.CASH_FREE";

//	/***
//	 * 回调接口如果金额需要从第三方接口调入，则调用次接口
//	 * 如支付宝，微信支付
//	 * @param id 交易号id
//	 * @param db  DB
//	 * @param type 入账 1 表示 执行入账账户的回调   出账 2 表示 执行出账账户的回调
//	 * @param params 参数集合
//	 * 		 如参数 cashFee cashFee 交易金额，单位为分，第三方支付接口次字段有效，如支付宝，微信
//	 * @return 
//	 * @throws Exception
//	 */
//	public String callBack(String id,DB db,String type,Map<String,String> params)throws Exception{
//		
//		String result=null;
//		
//		String cashFee="";
//		if(params!=null){
//			cashFee=params.get("cashFee");
//		}
//		
//		//1,更新外部数据到数据库中
//		String querSQL="SELECT trade_total_money FROM mall_trade_detail WHERE id=? ";
//		MapList trandMap=db.query(querSQL,id,Type.VARCHAR);
//		if(!Checker.isEmpty(trandMap)&&!Checker.isEmpty(cashFee)){
//			String trade_total_money=trandMap.getRow(0).get("trade_total_money");
//			
//			//2,将交易记录中的数据更新到记录数据中
//			String updateSQL="UPDATE mall_trade_detail SET record_trade_total_money=?,"
//					+ " trade_total_money=?  WHERE id=? ";
//			db.execute(updateSQL,new String[]{
//						trade_total_money,cashFee,id
//					},
//					new int[]{
//						Type.BIGINT,Type.BIGINT,Type.VARCHAR
//					});
//		}
//		
//		//回调业务处理
//		result=callBack(id, db, type);
//		
//		return result;
//	}
	
	/**
	 * @param id              交易号id
	 * @param type            入账 1 表示 执行入账账户的回调   出账 2 表示 执行出账账户的回调  
	 * @return
	 */
	public String callBack(String id,DB db,String type) throws Exception{
		
		String sql = "";
		
		String buissnes = "";
		
		JSONObject businessJso = null;
		
		String is_process_business = "";
		
		String result = "";
		
		String pay_type="";
		
		JSONObject jso = new JSONObject();
		
		if(!Checker.isEmpty(id)){
			//更新交易时间
			String updateSQL="UPDATE mall_trade_detail SET trade_time=now() WHERE id=? ";
			
			db.execute(updateSQL, id, Type.VARCHAR);
			
			
			sql = "SELECT * FROM mall_trade_detail WHERE id='"+id+"'";
			
			MapList mList = db.query(sql);
			
			if(!Checker.isEmpty(mList)){
				
				buissnes = mList.getRow(0).get("business_json");
				
				is_process_business =  mList.getRow(0).get("is_process_buissnes");
				
				pay_type = mList.getRow(0).get("trade_state");
				
				if("0".equals(is_process_business)){
					
					if(!Checker.isEmpty(buissnes)){
						
						businessJso = new JSONObject(buissnes);
						
						if(businessJso.length()>0 
								&& "1".equals(pay_type) 
								&& businessJso.has("success_call_back")){
							//
							logger.info("交易处理，交易ID(mall_trade_detail.id)："+id+"\t处理参数："+businessJso);
							
							//执行成功回调方法
							if(!Checker.isEmpty(businessJso.getString("success_call_back"))){
								
								IBusinessCallBack ias = classNameToObject(businessJso.getString("success_call_back"));
	
								if(ias!=null)
								{
									result=ias.callBackExec(id,buissnes,db,type);
									
									//更新业务为已处理状态
									updateSQL="UPDATE mall_trade_detail SET is_process_buissnes=1  WHERE id=? ";
									db.execute(updateSQL,id, Type.VARCHAR);
								}
								else
								{
									jso.put("code", "999");
									jso.put("msg","接口不存在");
									result=jso.toString();
								}
								
							}	
							
						}else{
							logger.info("业务无回调接口处理业务，交易ID："+id);
						}						
					}else{
						
						jso.put("code", "0");
						jso.put("msg","无需处理接口");
						result=jso.toString();
						
					}
				}
			
			}
		
		}
		return result;
	}
	
	//依据类名反射出对象
	private IBusinessCallBack classNameToObject(String className)
	{
		IBusinessCallBack result=null;

		try 
		{
			result=(IBusinessCallBack)Class.forName(className).newInstance();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return result;
	}

	
	/***
	 * 第三方接口返回回调业务处理回调
	 * @param id  ambdp系统订单的号
	 * @param db DB DB
	 * @param type 入账 1 表示 执行入账账户的回调   出账 2 表示 执行出账账户的回调   
	 * @param prams 业务参数
	 * @throws Exception 
	 */
	public void callBack(String id, DB db,  Map<String, String> prams,String type) throws Exception {
		
		
		if(prams!=null){
			String otherOrderCode=prams.get(OTHER_ORDER_CODE);
			//更新第三方接口订单号
			String updateSQL="UPDATE mall_trade_detail SET other_order_code=? WHERE id=? ";
			
			db.execute(updateSQL, new String[]{
					otherOrderCode,id
			}, new int[]{
					Type.VARCHAR,Type.VARCHAR
			});
			
			String querySQL = "SELECT * FROM mall_trade_detail WHERE id='"+id+"'";
			MapList mList = db.query(querySQL);
			
			if(!Checker.isEmpty(mList)){
				
				String buissnes = mList.getRow(0).get("business_json");
				
				JSONObject buissnesJS=new JSONObject(buissnes);
				
				if(buissnesJS.has("orders")){
					String orders=buissnesJS.getString("orders");
					if(!Checker.isEmpty(orders)){
						String[] orderIds=orders.split(",");
						for(String orderId:orderIds){
							
							//更新第三方支付订单账号到
							updateSQL="UPDATE mall_MemberOrder SET other_order_code=?  WHERE id=? ";
							db.execute(updateSQL,new String[]{
									otherOrderCode,orderId
							}, new int[]{
									Type.VARCHAR,Type.VARCHAR
							});
						}
					}
				}
			}//订单支付单号处理 end
			
			
			
			//1,更新外部数据到数据库中
			String cashFee=prams.get(CASH_FREE);
			
			String querSQL="SELECT trade_total_money FROM mall_trade_detail WHERE id=? ";
			MapList trandMap=db.query(querSQL,id,Type.VARCHAR);
			if(!Checker.isEmpty(trandMap)&&!Checker.isEmpty(cashFee)){
				String trade_total_money=trandMap.getRow(0).get("trade_total_money");
				
				//2,将交易记录中的数据更新到记录数据中
				updateSQL="UPDATE mall_trade_detail SET record_trade_total_money=?,"
						+ " trade_total_money=?  WHERE id=? ";
				db.execute(updateSQL,new String[]{
							trade_total_money,cashFee,id
						},
						new int[]{
							Type.BIGINT,Type.BIGINT,Type.VARCHAR
						});
			}//
		}
		
		
		
		//回调业务处理接口
		callBack(id, db, type);
		
	}
	
}
