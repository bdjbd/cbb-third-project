package com.p2p.servproject;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.MapListFactory;
import com.fastunit.view.tree.TreeUtil;

public class ServiceClazz implements MapListFactory {

	@Override
	public MapList getMapList(ActionContext ac) {
		try {
			String orgid= ac.getVisitor().getUser().getOrgId();
			String comditype="3";
			DB db = DBFactory.getDB();
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
