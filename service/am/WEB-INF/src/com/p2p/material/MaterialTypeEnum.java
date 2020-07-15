package com.p2p.material;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.MapListFactory;
import com.fastunit.view.tree.TreeUtil;


public class MaterialTypeEnum implements MapListFactory {

	@Override
	public MapList getMapList(ActionContext ac) {
		//SELECT parentid AS VALUE, tname AS NAME  FROM p2p_materialsType WHERE orgid='$O{orgid}' 
		String orgid= ac.getVisitor().getUser().getOrgId();
		
		MapList result=null;
		try{
			DB db = DBFactory.getDB();
			String sql="SELECT id AS value, tname AS name ,parentid "
					+ " FROM p2p_materialsType WHERE  datastatus<>3 AND orgid='"+orgid+"' OR  id='1' ";
			
			
			MapList map=db.query(sql);
			
			
			result=TreeUtil.getEnumeration(map,"&nbsp;&nbsp;", "value", "name", "parentid","parentid");
			
			if (result == null) {
				result = new MapList();
			}
		}catch(JDBCException e){
			e.printStackTrace();
		}
		return result;
	}

}
