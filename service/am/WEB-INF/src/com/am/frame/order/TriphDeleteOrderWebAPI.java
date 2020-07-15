package com.am.frame.order;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.jdbc.Type;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年6月23日
 * @version 
 * 说明:<br />
 * 我的订单，删除订单
 * 如果订单状态处理2，则可以删除
 */
public class TriphDeleteOrderWebAPI implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject reuslt=new JSONObject();
		
		DBManager dbManager=new DBManager();
		
		//会员ID
		String memberId=request.getParameter("memberId");
		//订单ID
		String orderId=request.getParameter("orderId");
		
//		 mallClass:mallClass,
//         orderState:orderState
		//订单商品商城分类
		String mallClass=request.getParameter("mallClass");
		//订单当前状态
		String orderState=request.getParameter("orderState");
		
		
		if("2".equals(orderState)){
			String queryOrderSQL=" SELECT id,memberid,commodityid,orderstate,coup_id "
					+ " FROM mall_memberorder WHERE id='"+orderId+"' ";
			
			MapList orderMap=dbManager.query(queryOrderSQL);
			
			if(!Checker.isEmpty(orderMap)){
				//更新优惠券使用状态
				String recvSQL="UPDATE am_orgelectticker SET usedatetime=null,usestatus=1 "
						+ " WHERE id=? ";
				dbManager.execute(recvSQL,new String[]{
						orderMap.getRow(0).get("coup_id")
				},new int[]{
						Type.VARCHAR
				});
			}
			
			//删除订单状态为2的社员对于的订单
			String delSQL="DELETE FROM mall_memberorder WHERE id=? AND orderstate=? AND memberid=? ";
			String[] values=new String[]{
					orderId,"2",memberId
			};
			int[] typs=new int[]{
					Type.VARCHAR,Type.VARCHAR,Type.VARCHAR
			};
			
			dbManager.execute(delSQL, values, typs);
			
			
			
			
		}else{
			String updateSQL="UPDATE mall_memberorder SET orderstate=? WHERE id=? ";
			String[] values=new String[]{"10",orderId};
			int[] typs=new int[]{Type.VARCHAR,Type.VARCHAR};
			dbManager.execute(updateSQL, values, typs);
		}
		
		try {
			reuslt.put("CODE", "0");
			reuslt.put("MSG", "订单删除成功！");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return reuslt.toString();
	}

}
