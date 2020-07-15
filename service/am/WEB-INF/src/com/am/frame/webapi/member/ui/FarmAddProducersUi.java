package com.am.frame.webapi.member.ui;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

public class FarmAddProducersUi implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		String orgname = "iprcc_office,united_press,mall_cooperative";
		
		String orgid = ac.getVisitor().getUser().getOrgId();
		
		DBManager db = new DBManager();
		
		String SQL = "SELECT * FROM service_mall_info WHERE orgid='"+orgid+"' ";
		
		MapList maplist = db.query(SQL);
		
		if(orgname.indexOf(maplist.getRow(0).get("tablename"))>-1)
		{
			unit.getElement("orgcode").setDefaultValue("");
	
		};
		
		if("04".equals(maplist.getRow(0).get("area_type"))){
			
 		   unit.getElement("province").setDefaultValue(maplist.getRow(0).get("province_id"));
 		   unit.getElement("city").setDefaultValue(maplist.getRow(0).get("city_id"));
 		   unit.getElement("zone").setDefaultValue(maplist.getRow(0).get("zone_id"));
// 		   unit.getElement("province").setShowMode(ElementShowMode.READONLY);
// 		   unit.getElement("city").setShowMode(ElementShowMode.READONLY);
// 		   unit.getElement("zone").setShowMode(ElementShowMode.READONLY);
 	   };
 	   
 	   if("03".equals(maplist.getRow(0).get("area_type"))){
 		   unit.getElement("province").setDefaultValue(maplist.getRow(0).get("province_id"));
 		   unit.getElement("city").setDefaultValue(maplist.getRow(0).get("city_id"));
// 		   unit.getElement("province").setShowMode(ElementShowMode.READONLY);
// 		   unit.getElement("city").setShowMode(ElementShowMode.READONLY);   
 	   };

 	   if("02".equals(maplist.getRow(0).get("area_type"))){
 		   unit.getElement("province").setDefaultValue(maplist.getRow(0).get("province_id"));
// 		   unit.getElement("province").setShowMode(ElementShowMode.READONLY);   
 	   };
 	   
 	   if("iprcc_office".indexOf(maplist.getRow(0).get("tablename"))>-1){
 		   
 	 	   unit.setTitle("贫困户管理");
 	 	   unit.getElement("poor").setDefaultValue("是");
 		   
 	   };
 	   
 	   if("united_press,home_farm,mall_cooperative".indexOf(maplist.getRow(0).get("tablename"))>-1){
		   
	 	   unit.setTitle("添加生产者");
	 	   unit.getElement("poor").setDefaultValue("否");
   
	   };



		return unit.write(ac);
	}

}


