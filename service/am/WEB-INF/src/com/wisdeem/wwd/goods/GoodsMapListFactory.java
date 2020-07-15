package com.wisdeem.wwd.goods;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.support.MapListFactory;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.view.tree.TreeUtil;

public class GoodsMapListFactory implements MapListFactory {

	@Override
	public MapList getMapList(ActionContext ac) {
		try {
			String orgid= ac.getVisitor().getUser().getOrgId();
			String comditype=ac.getRequestParameter("type");
			if(comditype==null){
				comditype=ac.getRequestParameter("comdytype");
			}
			if(comditype==null){
				comditype=(String)ac.getSessionAttribute("commodityType");
			}
			if(comditype!=null){
				ac.setSessionAttribute("commodityType",comditype);
			}
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