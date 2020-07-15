package com.wd.init.arog;

import org.json.JSONArray;
import com.wd.ICuai;
import com.wd.tools.DatabaseAccess;

/**
 * 查询所有组织机构
 * 
 * @author zhouxn
 * @time 2012-5-10
 */
public class FindAllArogCuai implements ICuai {
	/**
	 * 查询所有组织机构
	 * 
	 * @param org
	 *            .json.JSONArray json数组
	 * @return org.json.JSONArray json数组
	 */
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		String sql = "select * from AORG";
		return DatabaseAccess.query(sql);
	}
}