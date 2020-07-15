package com.am.cro;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/** 
 * @author  作者：liuhao
 * @date 创建时间：2017年5月18日 下午4:09:17
 * @explain 说明 : 
 */
public class zr_gl implements IWebApiService {

	
	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		
		DBManager db = new DBManager();
		
		String id=request.getParameter("id");		
		String type = request.getParameter("type");
		String wname = request.getParameter("wanme");
		String orderstate = request.getParameter("orderstate");
		String day = request.getParameter("day");
		
		if(  type.equals("0")){			
			String sql_id="select  id  from cro_Repairman where name='"+wname+"'";
			
			MapList mapList = db.query(sql_id);			
			if(!Checker.isEmpty( mapList)){
				String RepairmanID=mapList.getRow(0).get("id");
//				String sql_update="update cro_CarRepairOrder set  RepairmanID='"+RepairmanID+"' ,DistributeLeafletsTime= now(),OrderState='2' where id='"+id+"'";
//				String sql_update1="update cro_CarRepairOrder set  RepairmanID='"+RepairmanID+"' ,StartRepairTime= now(),OrderState='2' where id='"+id+"'";
//				if(orderstate.equals("1")){
//					db.execute(sql_update);
//				}else{
//					db.execute(sql_update1);
//				}
				String sql_update="update cro_CarRepairOrder set  RepairmanID='"+RepairmanID+"' ,DistributeLeafletsTime= now(),StartRepairTime= now(),OrderState='2' where id='"+id+"'";
				db.execute(sql_update);
				
			}
		}else{
			String sql_update_orderstae="update cro_CarRepairOrder set  OrderState='3',EndRepairTime=NOW() ,OrderTotalTime='"+day+"' where id='"+id+"'";
			//查询订单的memberid和orgcode
			String sql_query = "select * from cro_CarRepairOrder where id = '"+id+"'";
			MapList mapList = db.query(sql_query);
			if(mapList.size()>0){
				String memberid = mapList.getRow(0).get("memberid");
				String orgcode = mapList.getRow(0).get("orgcode");
				String carplatenumber = mapList.getRow(0).get("carplatenumber");
				//维修完成更新车辆的预约状态
				String sql_updateCarState = "update cro_carmanager set car_state = '1' where orgcode='"+orgcode+"' and memberid='"+memberid+"' and carplatenumber = '"+carplatenumber+"'";
				db.execute(sql_updateCarState);
			}
			
			db.execute(sql_update_orderstae);
		}
		
		
	
		return "1";
				
		
	}

}
