package com.cdms.test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.p2p.service.IWebApiService;
/*
 * 清空报表数据
 * http://服务器ip/AmRes/com.cdms.test.deleteTable.do
 */
public class deleteTable implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		String sql="select table_name FROM information_schema.tables\r\n" + 
				"Where table_name LIKE 'cdms_vcd_%'";
		DBManager db=new DBManager();
		MapList ml=db.query(sql);
		
		ml.put(ml.size(),"table_name", "cdms_VehicleUtilization");
		ml.put(ml.size(),"table_name", "cdms_VehiclesRunningCondition");
		ml.put(ml.size(),"table_name", "cdms_VehicleIdling");
		ml.put(ml.size(),"table_name", "cdms_NotworkingVehicleDetail");
		ml.put(ml.size(),"table_name", "cdms_EnclosureRecord");
		ml.put(ml.size(),"table_name", "cdms_AlarmRecord");
		ml.put(ml.size(),"table_name", "cdms_VehicleDayUtilization");
		for(int i =0 ; i<ml.size();i++) {
			String tn=ml.getRow(i).get("table_name");
			String tnsql="delete from "+tn;
			db.execute(tnsql);
		}
		return null;
	}



}
