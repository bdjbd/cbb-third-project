package com.am.cro.bespeak;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.am.frame.webapi.db.DBManager;
import com.p2p.service.IWebApiService;

public class UpdateCarState  implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		String rValue = "";
		String orderid = request.getParameter("orderid");
		String memberid = request.getParameter("memberid");
		String orgcode = request.getParameter("orgcode");
		String carNumber = request.getParameter("carNumber");
		
		DBManager db = new DBManager();
		String deleteOrderSql = "delete from cro_carrepairorder where id = '"+orderid+"'";
		int count = db.execute(deleteOrderSql);
		
		//如果订单删除成功，则更新车辆的预约状态
		if(count>0){
			String updateCarSql = "update cro_carmanager set car_state ='1'  where carplatenumber = '"+carNumber+"' and orgcode='"+orgcode+"' and memberid='"+memberid+"'";
			rValue = db.update(updateCarSql).toString();
		}
		
		return rValue;
	}

}
