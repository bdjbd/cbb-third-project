package com.p2p.business;

import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;

/**
 * 人工派单
 * @author Administrator
 */
public class ManPowerDispatcherOrder {
	
	/**
	 * 人工派单
	 * @param orderCode 订单号
	 * @param memberCode
	 */
	@SuppressWarnings("finally")
	public boolean dispatcher(String orderCode,String memberCode){
		boolean result=false;
		try{
			String sql="UPDATE p2p_DispatchRecod SET MEMBER_CODE="+memberCode
					+",ORStatus=0 WHERE order_code='"+orderCode+"'";
			DB db=DBFactory.getDB();
			db.execute(sql);
			DispatcherOrderService.sendOrdMsgToMember(memberCode,orderCode,db);
			result=true;
		}catch(Exception e){
			e.printStackTrace();
			result=false;
		}finally{
			return result;
		}
	}
}
