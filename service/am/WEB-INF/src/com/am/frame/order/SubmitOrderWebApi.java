package com.am.frame.order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.util.SystemOutLogger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.elect.ElectTicketManager;
import com.am.frame.elect.MemberElectTicket;
import com.am.frame.member.MemberManager;
import com.am.frame.order.util.ConsumerCodeProvider;
import com.am.frame.pay.PayManager;
import com.am.frame.state.StateFlowManager;
import com.am.mall.beans.commodity.Commodity;
import com.am.mall.beans.commodity.CommoditySpecifications;
import com.am.mall.beans.member.MemberAddres;
import com.am.mall.beans.member.MemberPayment;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.commodity.CommodityManager;
import com.am.mall.order.OrderManager;
import com.am.utils.DateFormatUtil;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/***
 * 提交订单接口
 * @author Administrator
 * <hr />
 * 请求参数格式：<br />
 * ORDERACTION=shoppingCar||order
 * &ORDERINFO=
 * {"MEMBERID":"会ID",
 * 	"RECVADDRESS":"地址ID",
 * 	"ORGCODE":"机构编号",
 * 	"ELECTTICKID":"使用的电子券ID",
 * 	"PAYMENT":"此次购买的所有商品应付金额（减去优惠券的）",
 * 	"ISGROUPSALE":"是否为套餐，true，表示此次提交的全部为套餐，不填或者false表示为非套餐",
 *  "SHIPPING_METHOD":" 商品配送方式  '1:即时送货 2：预约送货'",
 *  "PAYMENT_SHOW_MSG":"支付页面显示信息",
 * 	"COMMODITINFO":[{
 * 		"COMMODITYID":"商品ID",
 * 		"SPECIFICATIONSID":"规格ID",
 * 		"ORDERCODE":"订单编号"
 * 		"SALEPRICE":"销售价格",
 * 		"SCALENUMBER":"数量",
 * 		"POSTAGE":"运费金额",
 * 		"TOTALPRICE":"商品总价",
 * 		"PAYMENTTYPE":"支付方式",
 * 		},
 * 		{"":""}
 * 	]
 * }
 * //电子券每个订单只选择一个，并且按照第一个来计算价格。
 * <hr />
 * <ul>
 * 	<li>解析订单提交参数，包括当前会员ID，商品ID，商品规格，使用的电子券ID，收货地址，
 * 	订单提交状态：购物车，直接购买。</li>
 * 	<li>根据商品信息生成订单。</li>
 * 	<li>初始化订单的首流程。</li>
 * 	<li>根据订单提交状态，初始化订单流程。</li>
 * 	<li>处理电子券</li>
 * </ul>
 */
public class SubmitOrderWebApi implements IWebApiService {
	
	private Logger logger=LoggerFactory.getLogger(com.am.frame.order.SubmitOrderWebApi.class);

	@Override
	public String execute(HttpServletRequest req,HttpServletResponse rep) {
		logger.info("---------"+rep);
		//构造返回结果
		JSONObject job=new JSONObject();
		
		try{
			
			//根据商品信息和会员ID创建订单；
			MemberPayment memberPay=builderCreateOrder(req);
			
			//初始化流程的首流程状态  ；
			//2016-01-19  现在订单的状态与支付方式相关，所以以前就屏蔽掉了。
//			initOrderFirstState(memberPay);
			
			
			job.put("COUNT",memberPay.getPayCode().split(",").length);
			job.put("ERRCODE",0);
			job.put("ERRMSG","订单提交成功!");
			job.put("PAYMONEY",memberPay.getPayMoney());
			job.put("COMDITYNAME",memberPay.getPayContent());
			job.put("PAYID",memberPay.getId());
			job.put("ORDERS",memberPay.getAmOrders());
			
		}catch(Exception e){
			e.printStackTrace();
			try{
				job.put("COUNT",0);
				job.put("ERRCODE",1);
				job.put("ERRMSG",e.getMessage());
				job.put("PAYMONEY","");
				job.put("COMDITYNAME","");
				job.put("PAYID","");
				job.put("ORDERS","");
			}catch(JSONException e1){
				e1.printStackTrace();
			}
		}
		
		logger.info("提交订单返回参数"+job.toString());
		
		return job.toString();
	}

	
	
	/**
	 * 
	 * @param memberPay
	 */
	private void initOrderFirstState(MemberPayment memberPay) {
		
		String orders[]=memberPay.getAmOrders().split(",");
		DB db=null;
		//初始化订单的首流程
		try{
			
			db=DBFactory.newDB();
			
			for(int i=0;i<orders.length;i++){
				
				MemberOrder memberOrder=new OrderManager().getMemberOrderById(orders[i],db);
				Commodity commodity=CommodityManager.getInstance().getCommodityByID(memberOrder.getCommodityID(),db);
				//设置首流程状态值，并不调用状态值实现类,
				
				StateFlowManager.getInstance().setFirstState(commodity.getOrderStateID(), memberOrder.getId());
			}
			
		}catch(Exception e){
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
		
	}

	/**
	 * 构建支付对象<br />
	 * 会员ID,商品ID,支付方式,商品名称,商品规格名称,商品原价<br />
	 * ,购买数量,销售价格,运费金额,优惠金额,优惠明细,应付金额<br />
	 * 收货联系人(选择，只需提交地址ID即可处理，)
	 * 商品ID@规格ID@支付方式
	 * @param req HttpServletRequest
	 * @return MemberOrder 
	 * @throws Exception 
	 */
	private MemberPayment builderCreateOrder(HttpServletRequest req) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfAll = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		//是否为套餐
		String isGroupSeal="0";
		
		MemberPayment result=null;
		//获取订单信息
		String orderinfo=req.getParameter("ORDERINFO");
		logger.info("---------"+orderinfo);
		//订单提交 订单扩展信息
		String extendData=req.getParameter("EXTENDDATA");
		
		DB db=null;
		
		String getOrderInfoSQL=" SELECT id,ordercode FROM mall_memberorder WHERE ordercode=";
		
		try {
			
			db=DBFactory.newDB();
			
			JSONObject orders=new JSONObject(orderinfo);
			
			String memberId=orders.getString("MEMBERID");//会员ID
			String recvID=orders.getString("RECVADDRESS");//收获地址ID
			String orgCode=orders.getString("ORGCODE");//机构ID
			String electTickId=orders.getString("ELECTTICKID");//电子券ID
			String payment=orders.getDouble("PAYMENT")+"";//支付金额，在下订单时使用
			
			//支付业务显示信息
			String paymentShowMsg=orders.getString("PAYMENT_SHOW_MSG");
			
			
			String bespeakTime="";//预约送货日期
			
			String shippingMethod="";//商品配送方式  '1:即时送货 2：预约送货 3:门店自取
			
			if(orders.has("SHIPPING_METHOD")){
				shippingMethod=orders.getString("SHIPPING_METHOD");
			}
			
			if(orders.has("BESPEAKTIME")){
				bespeakTime=orders.getString("BESPEAKTIME");
				bespeakTime=formatData(bespeakTime);
			}
			
			if(orders.has("ISGROUPSALE")){//检查是否包含套餐字段
				isGroupSeal=orders.getBoolean("ISGROUPSALE")?"1":"0";
			}
			
			//会员管理类
			MemberManager memManager=new MemberManager();
			//获取地址信息
			MemberAddres addr=memManager.getMemberAddressById(recvID,db);
			
			
			JSONArray commoditInfo=orders.getJSONArray("COMMODITINFO");
			
			/***订单批处理参数集合 此集合的第一个字段为订单ID***/
			List<String[]> orderParams=new ArrayList<String[]>();
			
			/**保存购物车状态的订单编号，并将此数据删除**/
			List<String[]> orderCodes=new ArrayList<String[]>();
			/**更新预约送货，送货时间的订单ID**/
			List<String[]> updateTimeOrders=new ArrayList<String[]>();
			//商品名称
			String comodNams="";
			
			//商品管理类
			CommodityManager comdManager=CommodityManager.getInstance();
			
			//获取商品
			Commodity commodity=null;
			//获取规格信息
			CommoditySpecifications spec=null;
			
			String orderCode="";
			String orderId="";
			
			//商城分类
			String mallClass="";
			
			JSONObject commodityInfo=null;
			
			
			//电子券金额 优惠金额
			String preferentialprice="0.00";
			if(!Checker.isEmpty(electTickId)){
				preferentialprice=getPreferentialPrice(db,electTickId);
			}
			
			//优惠券价格
			double coupPreferentialpriceD=Double.parseDouble(preferentialprice);
			
			//循环处理多个商品信息
			for(int i=0;i<commoditInfo.length();i++){
				
				commodityInfo=commoditInfo.getJSONObject(i);
				//商品ID
				String comdId=commodityInfo.getString("COMMODITYID");
				
				commodity=comdManager.getCommodityByID(comdId,db);
				
				//获取商城分类
				mallClass=commodity.getMallClass();
				
				//票型编码
				String goodsCode=commodity.getGoods_code();
				
				//消费码
				String cumsCode="";
				if("2".equals(mallClass)||"6".equals(mallClass)){
//					cumsCode=UUID.randomUUID().toString().toUpperCase();
					cumsCode=ConsumerCodeProvider.getConsumerCode();
				}
				
				//获取商品名称
				comodNams+=","+commodity.getName();
				
				//获取规格信息
				spec=comdManager.getCommoditSpecificationsByID(commodityInfo.getString("SPECIFICATIONSID"),db);
				
				//orderId=UUID.randomUUID().toString().toUpperCase().replaceAll("-","0");
				
				//购物车数据
				orderCode=commodityInfo.getString("ORDERCODE");
				
				orderId=db.query(getOrderInfoSQL+"'"+orderCode+"'").getRow(0).get("id");
				
				updateTimeOrders.add(new String[]{orderId,bespeakTime});
				
				String buyerMsg=commodityInfo.getString("BUYERS_MESSAGE");//买家留言
				if(Checker.isEmpty(buyerMsg)){
					buyerMsg="";
				}
			
//				String shippingMethod=commodityInfo.getString("SHIPPING_METHOD");//商品配送方式  '1:即时送货 2：预约送货 3:门店自取
//				if(Checker.isEmpty(shippingMethod)&&"5".equals(mallClass)){
//					shippingMethod="1";
//				}
				//开始时间
				String startDate=getJSONString("START_DATE",commodityInfo);
				//结束时间
				String endDate=getJSONString("END_DATE",commodityInfo);//ommodityInfo.getString("END_DATE");
				//格式化日期
				if(startDate!=null&&!"null".equalsIgnoreCase(startDate)&&!"".equals(startDate)){
					startDate=sdf.format(sdf.parse(startDate));
				}
				if(endDate!=null&&!"null".equalsIgnoreCase(endDate)&&!"".equals(endDate)){
					endDate=sdf.format(sdf.parse(endDate));
				}
				
				//预计到店时间
				String exceptArriTime=getJSONString("EXPECT_ARRIVAL_TIME",commodityInfo);//commodityInfo.getString("EXPECT_ARRIVAL_TIME");
				
				if(!Checker.isEmpty(exceptArriTime)){
					exceptArriTime=startDate+" "+exceptArriTime;
					exceptArriTime=sdfAll.format(sdfAll.parse(exceptArriTime));
				}else{
					exceptArriTime="";
				}
				
				//销售价格
				String salePrice=getJSONString("SALEPRICE",commodityInfo);//commodityInfo.getString("SALEPRICE");
				
				//儿童价格
				String childrenPrice=getJSONString("CHILDPRICE",commodityInfo);//commodityInfo.getString("CHILDPRICE");
				//儿童购买数量
				String childBuyNumber=getJSONString("CHILDBUYNUMBER",commodityInfo);//commodityInfo.getString("CHILDBUYNUMBER");
				
				double totlaPrice=Double.parseDouble(commodityInfo.getString("TOTALPRICE"));
				//优惠金额
				double preferentialpriceD=0.0;
				//计算电子券金额，如果优惠金额大于商品价格，则需要将在下一个订单中减去剩余优惠金额
				if(totlaPrice>=coupPreferentialpriceD){
					totlaPrice=totlaPrice-coupPreferentialpriceD;
					preferentialpriceD=coupPreferentialpriceD;
					
					//优惠券一次性消费完，优惠券几个为0
					coupPreferentialpriceD=0;
				}else{
					//如果优惠券的金额大于单个订单的单价，则，此订单价格为0，
					//剩余优惠券的价格为减去当前订单产品的价格
					preferentialpriceD=totlaPrice;
					totlaPrice=0;
					//剩余优惠券的价格=优惠券价格-优惠价格
					coupPreferentialpriceD=coupPreferentialpriceD-preferentialpriceD;
				}
				
				
				orderParams.add(new String[]{
						orderId,memberId, comdId,"1", /**订单提交状态**/
						commodity.getSupportPaymentMode(),commodity.getName(),
						spec.getName()+spec.getDates(),spec.getPrice(),
						commodityInfo.getString("SALENUMBER")/**SCALENUMBER 销售数量，SALENUMBER销售数量，包含儿童票价的销售数量和成人价的销售数量  **/,
						salePrice,commodityInfo.getString("POSTAGE"), //Postage
						commodityInfo.getString("TOTALPRICE"),//TotalPrice
						addr.getRecvName(),addr.getRecvPhone(),
						addr.getRecvDetail(),addr.getRecvPostalCode(),//ZipCode
						addr.getLongitud(),addr.getLatitude(),
						UUID.randomUUID().toString(),//System.currentTimeMillis()+""+RandomUtil.getRandomString(3).toUpperCase(),//订单ID
						orgCode,   //orgcode
						addr.getRecvProvince(),addr.getRecvCity(),addr.getRecvArea(),//省，市，区
						spec.getId(),isGroupSeal,shippingMethod,bespeakTime,buyerMsg,
						startDate,endDate,exceptArriTime,cumsCode,
						goodsCode,startDate,mallClass,childrenPrice,childBuyNumber,
						commodity.getValidityDateStart(),commodity.getValidityDateEnd(),paymentShowMsg
						,electTickId,preferentialpriceD+""});
				
				if(!Checker.isEmpty(orderCode)&&!"null".equalsIgnoreCase(orderCode)){
					orderCodes.add(new String[]{orderCode});
				}
			}//订单循环 end
			
			//删除本次在购车中包含的商品
			String deleteSQL=" DELETE FROM mall_memberorder WHERE ordercode=?";
			
			db.executeBatch(deleteSQL, orderCodes,new int[]{Type.VARCHAR});
			
			
			//批量处理插入数据库数据。
			String insertSQL=
							"INSERT INTO mall_memberorder(                                        "+
							"            id, memberid, commodityid, orderstate,                   "+
							"            paymentmode, commodityname, specname, specprice,         "+
							"            salenumber, saleprice, postage,       "+
							"            totalprice, "+
							"            contact,contactphone, address, zipcode,                  "+
							"            longitud, latitude, ordercode, orgcode,                  "+
							"			 province_id,city_id,zone_id,	"+
							"            specid,IsGroupSale,shipping_method,bespeak_time,BUYERS_MESSAGE, "+
							"            check_in_time,leave_time,expect_arrival_time,custom_code,"+
							" 			goods_code,start_date,mall_class,children_price,children_buy_number,"+
							"			VALIDITY_DATE_START,VALIDITY_DATE_END,payment_show_msg"  + 
							"			,coup_id,preferentialprice )  "+
							"    VALUES (                      "+
							"	    	?, ?, ?, ?,            "+
							"	    	?, ?, ?, ?,            "+
							"	    	?, ?, ?,               "+
							"           ?,                     "+
							"           ?, ?, ?, ?,            "+
							"           ?, ?, ?, ?,            "+
							"           ?, ?, ?,               "+
							"           ?,?,?,?,?,           "+ 
							"           ?,?,?,?," +
							"           ? ,?,?,?,?,"+
							" 			?,?,? "  +
							"           ,?,? );       ";
			
			//保存新订单
			db.executeBatch(insertSQL, orderParams, new int[]{
					Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,
					Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,Type.DECIMAL,
					Type.INTEGER,Type.DECIMAL,Type.DECIMAL,
					Type.DECIMAL,
					Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,
					Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,
					Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,
					Type.VARCHAR,Type.VARCHAR,Type.INTEGER,Type.DATE,Type.VARCHAR,
					Type.TIMESTAMP,Type.TIMESTAMP,Type.TIMESTAMP,Type.VARCHAR,
					Type.VARCHAR,Type.TIMESTAMP,Type.VARCHAR,Type.DECIMAL,Type.INTEGER,
					Type.DATE,Type.DATE,Type.VARCHAR
					,Type.VARCHAR,Type.DECIMAL
			});
			
			//更新预约送货时间
			if(!Checker.isEmpty(bespeakTime)){
				String updateBespeakTimeSQL="UPDATE mall_memberorder SET bespeak_time=? "
						+ " WHERE id=? ";
				db.executeBatch(updateBespeakTimeSQL,updateTimeOrders,new int[]{Type.DATE,
						Type.VARCHAR});
			}
			
			
			//消费电子券
			userElectTicke(electTickId,memberId);
			
			//更新销售数量
			updateCommodityNumber(orderParams,db);
			
			
			//处理我的支付内容
			String orderIds="";
			
			for(int i=0;i<orderParams.size();i++){
				orderIds+=orderParams.get(i)[0]+",";
			}
			
			//增加支付状态信息
			result=new PayManager().createPaymentByOrder(
					comodNams, memberId, "1",Double.parseDouble(payment),orderIds);
			
			
			//呼旅 
			OrderManager orderManager=new OrderManager();
			//订单子表数据保存处理
			orderManager.processExendsOrder(db,orderIds,extendData);
			
			//根据后台变量设置支付到期时间
			orderManager.setOrderCloseTime(db,orderIds,mallClass);
			
			
		} catch (JSONException e) {
			e.printStackTrace();
			throw new Exception("订单请求参数不是正确的JSON格式，请求参数："+orderinfo);
		}finally{
			if(db!=null){
				db.close();
			}
		}
		
		return result;
	}
	
	
	private String formatData(String bespeakTime) {
		String result="";
		
		logger.info("bespeakTime:"+bespeakTime);
		
		DateFormatUtil dfu=new DateFormatUtil();
		result=dfu.getFormatDate(bespeakTime);
		
		logger.info("result bespeakTime:"+result);
		return result;
	}



	/**
	 * 更新销售数量
	 * @param orderParams
	 * @param db
	 * @throws JDBCException 
	 */
	private void updateCommodityNumber(List<String[]> orderParams, DB db) throws JDBCException {
		
		String sql="UPDATE mall_Commodity SET SaleNumber=COALESCE(SaleNumber,0)+? WHERE id=? ";
		
		//更新商城销售数量
		String updateMallStoreSQL="UPDATE mall_store SET yscpzs=COALESCE(yscpzs,0)+? "
				+ " WHERE id IN (SELECT store FROM mall_Commodity WHERE id=? ) ";
		
		for(int i=0;i<orderParams.size();i++){
			//商品销售数量
			String saleNumber=orderParams.get(i)[8];
			//商品ID
			String commodityId=orderParams.get(i)[2];
			
			db.execute(sql,
					new String[]{saleNumber,commodityId},
					new int[]{Type.INTEGER,Type.VARCHAR});
			
			//更新店铺对于的销售数量
			db.execute(updateMallStoreSQL,new String[]{
					saleNumber,commodityId
			},new int[]{
					Type.INTEGER,Type.VARCHAR
			});
		}
		
	}


	private void userElectTicke(String electTickId,String memberId){
		//查询是否消费电子券
		if(!Checker.isEmpty(electTickId)){
			ElectTicketManager etm=ElectTicketManager.getInstance();
					
			etm.initMemberElectTicket(memberId);

			MemberElectTicket met=etm.getMemmberElectById(electTickId);
					
			ElectTicketManager.getInstance().userElectTicke(met);
		}
	}
	
	
	public String getJSONString(String key,JSONObject targetJSON) throws JSONException{
		String result=null;
		if(targetJSON!=null&&targetJSON.has(key)){
			result=targetJSON.getString(key);
		}
		return result;
	}
	
	
	/**
	 * 电子券金额
	 * @param db
	 * @param electTickId
	 * @return
	 * @throws JDBCException 
	 */
	private String getPreferentialPrice(DB db, String electTickId) throws JDBCException {
		String reuslt="0.0";//preferentialPrice
		String querSQL="SELECT et.sname,et.cash FROM am_OrgElectTicker AS ut "+
				"   LEFT JOIN am_EterpElectTicket AS et ON ut.EterpElectTicketID=et.id "+
				"   WHERE ut.id=? ";
		MapList map=db.query(querSQL, electTickId, Type.VARCHAR);
		
		if(!Checker.isEmpty(map)){
			reuslt=map.getRow(0).get("cash");
		}
		
		return reuslt;
	}

	
}
