package com.am.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.support.MapListFactory;
import com.fastunit.util.Checker;

public class OrgMapListFactory implements MapListFactory {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	private MapList rValues = new MapList();
	private int count = 0;
	public MapList getMapList(ActionContext ac) {
		DBManager db = new DBManager();
		String orgid = ac.getVisitor().getUser().getOrgId();
		String orgname = "";
		String sql = "select orgname from aorg where orgid='"+orgid+"'";
		MapList mapList = db.query(sql);
		if(!Checker.isEmpty(mapList)){
			orgname = mapList.getRow(0).get("orgname");
		}
		rValues.put(0, "name", orgname);
		rValues.put(0, "value", orgid);
		getOrg(db, "", orgid);
		logger.info(rValues.getText());
		return rValues;

	}

	public void getOrg(DBManager db,String prefix,String parentid) {
		count++;
		prefix+="&nbsp;&nbsp;";
		String sql = "select orgid as value,orgname as name,parentid from aorg where parentid='"+parentid+"'";
		MapList mapList = db.query(sql);
		if(!Checker.isEmpty(mapList)){
			for (int i = 0; i < mapList.size(); i++) {
				String name = prefix+mapList.getRow(i).get("name");
				String value = mapList.getRow(i).get("value");
				rValues.put(count, "name", name);
				rValues.put(count, "value", value);
				getOrg(db,prefix,value);
			}
			
		}	
	}

}
