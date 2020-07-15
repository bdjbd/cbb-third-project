package com.cdms.org;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.TreeFactory;
import com.fastunit.util.Checker;
import com.fastunit.view.tree.Tree;

public class UserRoleTreeFactory extends TreeFactory{
	Logger logger = LoggerFactory.getLogger(getClass());

	private String SQL_MENUMANAGER = "select * from arole order by o";
	@Override
	public Tree createTree(ActionContext ac, String domain) throws Exception {
		DBManager db = new DBManager();
		// 获得数据
		MapList data = db.query(SQL_MENUMANAGER);
		if (Checker.isEmpty(data)) {
			return null;
		}
		// 确定根节点
		int rootRowIndex = data.findRowIndex("parentid", "");
		// 从根节点创建树
		Tree tree = getTree(db, data, 0);
		return tree;

	}

	/**
	 * 获得树
	 * 
	 * @param db
	 *            数据库连接
	 * @param data
	 *            数据集合
	 * @param index
	 * @return 返回树
	 * @throws Exception
	 */
	private Tree getTree(DBManager db, MapList data, int index) throws Exception {
		Tree tree = new Tree();
		Row row = data.getRow(index);
		String id = row.get("roleid");
		String rolename = row.get("rolename");
		tree.setId(id);
		tree.setName(rolename);
		//根节点，默认勾选，不可修改，所有用户用户此角色
		if(index==0){
			tree.setCheckAttribute("checked");
			tree.setTitle("所有用户拥有此角色");
		}
		//如果下级管理员可用角色设置表中存在此角色，则勾选
		String sql = "select * from abdp_childadminroleset where roleid='"+id+"'";
		MapList mapList = db.query(sql);
		if(!Checker.isEmpty(mapList)&&mapList.size()>0){
			tree.setCheckAttribute("checked");
		}
		for (int i = 0; i < data.size(); i++) {
			String parent = data.getRow(i).get("parentid");
			if (id.equals(parent)) {
				tree.add(getTree(db, data, i));
			}
		}
		return tree;
	}

}
