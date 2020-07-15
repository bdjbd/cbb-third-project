package com.am.frame.order.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年5月27日
 * @version 
 * 说明:<br />
 * 订单业务处理类 
 */
public class OrderService {
	
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	public JSONObject sellOutOrder(DB db,String orderId) throws Exception{
		JSONObject result=new JSONObject();
		
		//1,查询验证订单是否可以卖出
		MapList orderMap=getOrderInfoByOrderId(db,orderId);
		if(!Checker.isEmpty(orderMap)){
			Row orderRow=orderMap.getRow(0);
			String shippingMethod=orderRow.get("shipping_method");//1:即时送货 ;2：预约送货
			if("2".equals(shippingMethod)){
				//2,将订单卖出
				JSONObject sellResult=sellOutOrder(db,orderRow);
				//3,将订单卖出金额返回到社员现金账户	
				logger.info("订单卖出："+sellResult);
				if(0==sellResult.getInt("CODE")){
					
					//进行转账业务
					String iremakers="订单卖出，获取金额，买的订单号编号"+orderRow.get("id")+"。";

					VirementManager vm=new VirementManager();
					vm.execute(db,"", orderRow.get("memberid"), 
							"",SystemAccountClass.CASH_ACCOUNT,
							sellResult.getString("ACTUAL_PRICE"), iremakers, "",
							"", true);

					result=sellResult;
					
					result.put("MSG","订单卖出成功!");
					//处理订单流程状态
					
					
				}else{
					result=sellResult;
				}
			}else{
				result.put("CODE", 2910);
				result.put("MSG","此订单无法卖出.");
			}
		}
		
		return result;
	}

	/**
	 * 订单卖出
	 * @param db  DB
	 * @param orderRow  订单信息
	 * @return JSONObject CODE（0，表示成，其他值是不，具体原因MSG），TOTAL_PRICE (卖出的总价格)，
	 * 					SELL_OUT_FREE(销售手续费)，ACTUAL_PRICE(实际可获取的金额)，
	 * @throws JDBCException 
	 */
	public JSONObject sellOutOrder(DB db, Row orderRow) throws Exception {
		JSONObject result=new JSONObject();
		
		double orderSellOutFree=Var.getDouble("order_sell_out_free_ratio",0.01);
		
		//1,计算订单当前规格商品的销售价格。
		String getSpecPriceSQL=
				" SELECT trim(to_char(totalPrice-sellOutFree,'9999999990D99'))  AS ACTUAL_PRICE,* FROM ( "+
				"   SELECT price*"+orderRow.get("salenumber")+" AS totalPrice,"
				+"   price*"+orderRow.get("salenumber")+"*"+orderSellOutFree+" AS sellOutFree,"
//				+"   totalPrice-totalPrice*sellOutFree ,"
				+ "  * FROM mall_CommoditySpecifications WHERE id=? "
				+ "  )d1 ";
		
		MapList map=db.query(getSpecPriceSQL,orderRow.get("specid"),Type.VARCHAR);
		
		if(Checker.isEmpty(map)){
			logger.info("根据订单规格id获取规格为空，订单id："+orderRow.get("id")+
					" ；订单规格ID:"+orderRow.get("specid"));
			
			result.put("CODE", 2911);
			result.put("MSG","订单卖出无法完成！");
		}else{
			//2,按照定期规格价格卖出商品。
			result.put("CODE", 0);
			result.put("MSG","");
			//卖出总价
			result.put("TOTAL_PRICE",map.getRow(0).get("totalprice"));
			result.put("SELL_OUT_FREE",map.getRow(0).get("totalprice"));
			result.put("ACTUAL_PRICE",map.getRow(0).get("actual_price"));
			
		}
		
		return result;
	}



	/**
	 * 根据订单id获取订单信息
	 * @param db DB
	 * @param orderId 订单ID
	 * @return
	 * @throws JDBCException 
	 */
	public MapList getOrderInfoByOrderId(DB db, String orderId) throws JDBCException {
		String querySQL="SELECT * FROM mall_MemberOrder WHERE id=? ";
		MapList map=db.query(querySQL,orderId,Type.VARCHAR);
		
		return map;
	}
	
}
