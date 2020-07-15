package com.am.mall.iwebAPI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.mall.beans.commodity.Commodity;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.commodity.CommodityManager;
import com.am.mall.order.OrderDispatcherManager;
import com.am.mall.order.OrderFlowParams;
import com.am.mall.order.OrderManager;
import com.am.mall.order.ServerFlowManager;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * @author Mike
 * @create 2014年11月18日
 * @version 
 * 说明:<br />
 * 接受订单分配
 * 接收CommodityDistribution.ID，设置并设置DistState=5，OrderState=4；
 * action:com.am.mall.order.distribution.org.AcceptDistribution
 * dist_id，订单分配ID
 * return:JSON格式如下：{ CODE:”0”, MSG:”成功”,CHECK_CODE : “”}
 * CODE=0成功，非0表示失败，同时MSG给出失败原因；
 * 
 * <br/>
 * URL:URL:http://127.0.0.1/p2p/com.am.mall.iwebAPI.AcceptOrderDistributionIWebAPI.do?
 * DISTID=商品派单ID&ISACCEPT=true&ACCEPTTYPE=PERSONAL&ACCEPTID=userid
 * 
 */
public class AcceptOrderDistributionIWebAPI implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		JSONObject result=new JSONObject();
		DB db = null;
		try{
			
			//CommodityDistribution  派单ID
			String distId=request.getParameter("DISTID");
			//是否接单
			String isAccept=request.getParameter("ISACCEPT");
			//接单类型，个人为PERSONAL，组织为GROUP
			String acceptType=request.getParameter("ACCEPTTYPE");
			//接单ID,接单个人或者组织ID
			String acceptId=request.getParameter("ACCEPTID");
		
			db=DBFactory.newDB();
			
			if("true".equalsIgnoreCase(isAccept)){
				//接单处理流程
				//1，检查是否过时
				if(!checkTimeOut(distId,db)){
					//2，如果没有过时，接单，并保持数据到数据库
					result=accetpDist(distId,acceptId,acceptType,db);
				}else{
					result.put("CODE",0);
					result.put("MSG","订单已超时");
				}
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
		
		return result.toString();
	}

	
	
	/**
	 * 检查是否过时
	 * @param distId  派单ID
	 * @return  超时返回TRUE，未超时，返回false
	 * @throws JDBCException 
	 */
	private boolean checkTimeOut(String distId,DB db) throws JDBCException {
		boolean result=false;
		
		int timeOut=Var.getInt("am_dispatcherTimeOut",10);
		
		String checkSQL="SELECT   CASE WHEN disttime+'"+timeOut+" min'< now() THEN 'true' ELSE 'false' END AS timeout "
				+ " FROM mall_CommodityDistribution WHERE id=? ";
		
		MapList map=db.query(checkSQL,distId,Type.VARCHAR);
		
		if(!Checker.isEmpty(map)&&"t".equalsIgnoreCase(map.getRow(0).get("timeout"))){
			result=true;
		}
		
		return result;
	}


	/**
	 * 接单
	 * @param distId  派单ID
	 * @param acceptId 接单组织或者个人ID
	 * @param acceptType  接单类型 个人为PERSONAL，组织为GROUP
	 * @throws JDBCException 
	 */
	private JSONObject accetpDist(String distId, String acceptId, String acceptType,DB db) throws JDBCException{
		
		JSONObject result=new JSONObject();
		
		Table table=new Table("am_bdp", "MALL_COMMODITYDISTRIBUTION");
		
		TableRow row=table.addUpdateRow();
		
		row.setOldValue("id", distId);
		row.setValue("acceptorderid", acceptId);
		row.setValue("accepttype", acceptType);
	
		if(db.save(table).length>0){
			//保存成功 ，修改派单状态为已经接单,派单状态为 
			String findDistOrderInfoSQL="SELECT * FROM mall_CommodityDistribution WHERE id=?";
			
			MapList map=db.query(findDistOrderInfoSQL,distId,Type.VARCHAR);
			
			if(!Checker.isEmpty(map)){
				String orderId=map.getRow(0).get("orderid");
				
				MemberOrder order=new OrderManager().getMemberOrderById(orderId,db);
				Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID(), db);
				
				OrderFlowParams param=ServerFlowManager.getInstance().builderParamList(order, commodity, db);
				
				try {
					result=new JSONObject(OrderDispatcherManager.getInstance().setDistState(param,"5"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}

}
