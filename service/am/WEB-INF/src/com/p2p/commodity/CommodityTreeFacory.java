package com.p2p.commodity;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.MapListFactory;
import com.fastunit.view.tree.TreeUtil;

public class CommodityTreeFacory  implements MapListFactory {

	/**
	*SELECT comdy_class_id AS VALUE ,
	*	class_name AS name FROM ws_commodity  
	*	WHERE orgid='$O{orgid}' 
	*	AND comdytype=1
	**/
	@Override
	public MapList getMapList(ActionContext ac) {
//		try {
//			MapList result=null;
//			String orgid= ac.getVisitor().getUser().getOrgId();
//			DB db = DBFactory.getDB();
//			MapList map=db.query("SELECT comdy_class_id AS value ,"
//					+ " class_name AS name,parent_id FROM ws_commodity  "
//					+ " WHERE orgid='"+orgid+"'  "
//					+ " AND comdytype=1 ");
//			if("org".equalsIgnoreCase(orgid)){
//				result = TreeUtil.getEnumeration(map,"&nbsp;&nbsp;", "value", "name", "parent_id", "");
//			}else{
//				result = TreeUtil.getEnumeration(map,"&nbsp;&nbsp;", "value", "name", "parent_id", "1");
//			}
//			
//			if (result == null) {
//				result = new MapList();
//			}
//			return result;
//		} catch (Exception e) {
//           e.printStackTrace();
//		}
		
		
		
		try {
			String orgid= ac.getVisitor().getUser().getOrgId();
			String comditype="1";
//			if(comditype==null){
//				comditype=ac.getRequestParameter("comdytype");
//			}
//			if(comditype==null){
//				comditype=(String)ac.getSessionAttribute("commodityType");
//			}
			DB db = DBFactory.getDB();

//			String sql = "select comdy_class_id as value,class_name as name from WS_COMMODITY where parent_id is not null and orgid='"+orgid+"'";
//			db.query(sql);
			
//			MapList result = TreeUtil.getEnumeration(
//							db.query("select comdy_class_id as value,class_name as name,parent_id from WS_COMMODITY where comdy_class_id = "+comditype+" AND comdytype="+comditype+"  or orgid='"+orgid+"'"),
//							"&nbsp;&nbsp;", "value", "name", "parent_id", "");
			MapList result=null;
			if("org".equalsIgnoreCase(orgid)){
				result = TreeUtil.getEnumeration(
						db.query("select comdy_class_id AS value ,class_name AS name,parent_id from "
								+ " WS_COMMODITY where comdy_class_id ="+comditype
								+" OR comdytype="+comditype+"  AND orgid='"+orgid+"' "),
						"&nbsp;&nbsp;", "value", "name", "parent_id", "");
			}else{
				result = TreeUtil.getEnumeration(
						db.query("select comdy_class_id AS value ,class_name AS name,parent_id from "
								+ " WS_COMMODITY where comdy_class_id ="+comditype
								+" OR comdytype="+comditype+"  AND orgid='"+orgid+"' "),
						"&nbsp;&nbsp;", "value", "name", "parent_id", "parent_id");
			}
			result = TreeUtil.getEnumeration(
					db.query("select comdy_class_id AS value ,class_name AS name,parent_id from "
							+ " WS_COMMODITY where comdy_class_id ="+comditype
							+" OR comdytype="+comditype+"  AND orgid='"+orgid+"' "),
					"&nbsp;&nbsp;", "value", "name", "parent_id", "");
			if (result == null) {
				result = new MapList();
			}
			return result;
		} catch (Exception e) {
           e.printStackTrace();
		}
		return null;
	}


}
