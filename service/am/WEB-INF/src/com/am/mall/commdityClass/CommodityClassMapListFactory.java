package com.am.mall.commdityClass;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.MapListFactory;
import com.fastunit.view.tree.TreeUtil;

/**
 * @author Mike
 * @create 2014年11月11日
 * @version 
 * 说明:<br />
 * 商品分类MapListFactiroy
 */
public class CommodityClassMapListFactory implements MapListFactory {

	@Override
	public MapList getMapList(ActionContext ac) {
		
		String orgid= ac.getVisitor().getUser().getOrgId();
		
		MapList result=null;
		try{
			DB db = DBFactory.getDB();
			String sql="SELECT id AS value,title AS name ,upid FROM mall_CommodityClass  WHERE orgcode='"
			+orgid+"'";
			
			MapList map=db.query(sql);
			result=TreeUtil.getEnumeration(map,"&nbsp;&nbsp;", "value", "name", "upid","upid");
			
			if (result == null) {
				result = new MapList();
			}
		}catch(JDBCException e){
			e.printStackTrace();
		}
		return result;
	}

}
