package com.am.frame.state.flow;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.state.OrderFlowParam;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年6月21日
 * @version 
 * 说明:<br />
 * 呼旅网，特产类商品配送，
 * 如果是商品分为门店自取和邮寄到家 都需要配单动作
 */
public class TriphOrderDistributionAction extends DefaultOrderStateAction {


	@Override
	public String execute(OrderFlowParam ofp) {
		
		JSONObject result=new JSONObject();
		DB db=null;
		try{
			
			db=DBFactory.newDB();
			JSONObject distResult=null;
			try{
				distResult=dcDistOrderRule(ofp,db);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			if(distResult!=null&&distResult.getBoolean("SUCCESS")){
				//派单成功
				
				//接单ID  配送中心 机构ID
				String acceptID=distResult.getString("DCID");
				
				//向派单表中插入派单信息
				insertDistRecv(ofp,acceptID,db);
				
				//更新订单状态到下一个流程状态
				result=new JSONObject(super.execute(ofp));
				
				//更新订单中的派单时间
				updateDistTimeByOrderId(ofp.orderId, db);
				
			}else{
				//派单失败
				result=distResult;
				//更新订单状态为失败状态值
				result=new JSONObject(super.executeFailure(ofp));
			}
		}catch(Exception e){
			e.printStackTrace();
			try {
				result.put("CODE",0);
				result.put("ERRCODE",1);
				result.put("MSG",e.getMessage());
				result.put("SUCCESS",false);
				result.put("STATE",ofp.stateValue);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		return result.toString();
	}

	
	private void updateDistTimeByOrderId(String orderId, DB db) throws JDBCException {
		String updateSQL="UPDATE mall_MemberOrder SET DistTime=now() WHERE id=?";
		db.execute(updateSQL, orderId, Type.VARCHAR);
	}
	
	
	/**
	 * 向派单表中插入派单数据
	 * @param ofp
	 * @param db
	 * @throws JDBCException 
	 */
	private String insertDistRecv(OrderFlowParam ofp,String acceptId, DB db) throws JDBCException {
		
		String id=UUID.randomUUID().toString();
		
		String addSQL="INSERT INTO mall_commoditydistribution("
				+ "id, orderid, disttime,AcceptOrderID )VALUES "
				+ "(?, ?, now(),?)";
		
		db.execute(addSQL,
				new String[]{id,ofp.orderId,acceptId},
				new int[]{Type.VARCHAR,Type.VARCHAR,Type.VARCHAR});
		
		return id;
	}

	/**
	 * 找到商品的店铺进行订单的配送处理
	 */
	private JSONObject dcDistOrderRule(OrderFlowParam ofp,DB db)throws Exception {
		JSONObject result=new JSONObject();
		
		String dcOrgId=findCommodityMallStroeOrgId(db,ofp.orderId);
		//检查是否派单成功
		if(Checker.isEmpty(dcOrgId)){
			//派单失败
			result.put("ORDERID", ofp.orderId);
			result.put("SUCCESS",false);
			
		}else{
			//派单成功
			//TODO 发送通知
			
			result.put("STOREID", dcOrgId);
			result.put("DCID", dcOrgId);
			result.put("ORDERID", ofp.orderId);
			result.put("SUCCESS",true);
		}
		
		return result;
	}

	
	/**
	 * 通过订单ID找到对应的店铺机构
	 * @param db
	 * @param orderId
	 * @return
	 * @throws JDBCException 
	 */
	private String findCommodityMallStroeOrgId(DB db, String orderId) throws JDBCException {
		
		String reuslt="org";
		
		String querSQL="SELECT st.orgcode FROM mall_MemberOrder AS od "+
				" LEFT JOIN  mall_commodity AS comd ON od.commodityid=comd.id "+
				" LEFT JOIN  mall_store AS st ON comd.store=st.id  "+
				" WHERE od.id=? ";
		MapList map=db.query(querSQL, orderId, Type.VARCHAR);
		
		if(!Checker.isEmpty(map)){
			reuslt=map.getRow(0).get("orgcode");
		}
		
		return reuslt;
	}
	

	
	
	
}
