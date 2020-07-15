package com.p2p.servproject;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.TreeFactory;
import com.fastunit.view.tree.Tree;
import com.fastunit.view.tree.config.TreeActionType;

public class ServProjectClazzTree extends TreeFactory {
//
//	private static final String ATTRIBUTE = "oncontextmenu=showMenu()";
//
//	public Tree createTree(ActionContext ac, String domain) throws Exception {
//		DB db = DBFactory.getDB();
//		String orgid = ac.getVisitor().getUser().getOrgId();
//		MapList data = db.query("select * from ws_commodity where orgid='"+orgid+"'  AND comdytype=3 order by create_time");
//		// 从根节点创建树
//		Tree tree = getTree(db, data,0);
//		return tree;
//	}
//
//	/**
//	 * 获得树
//	 * 
//	 * @param db
//	 *            数据库连接
//	 * @param data
//	 *            数据集合
//	 * @param index
//	 * @return 返回树
//	 * @throws Exception
//	 */
//	private Tree getTree(DB db, MapList data, int index) throws Exception {
//		Tree tree = new Tree();
//		String id = "";
//		String menuname = "";
//		String ccode="";
//		if (index == 0) {
//			id="1";
//			menuname="服务分类";
//		}else{
//			Row row = data.getRow(index-1);
//			id = row.get("comdy_class_id");
//			menuname = row.get("class_name");
//			ccode=row.get("c_code");
//			
//		}
//		tree.setId(id);
//		tree.setName(menuname);
//		tree.setAttribute(ATTRIBUTE);
//		if (id.equals("3")) {
//			tree.setAction("");
//		} else {
//			tree.setActionType(TreeActionType.FUNCTION);// 动作类型:连接/方法
//			tree.setAction("setRole");
//		}
//		for (int i = 0; i < data.size(); i++) {
//			String parent = data.getRow(i).get("parent_id");
//			if (id.equals(parent)) {
//				tree.add(getTree(db, data, i+1));
//			}
//		}
//		return tree;
//	}
	private static final String ATTRIBUTE = "oncontextmenu=showMenu()";

	@Override
	public Tree createTree(ActionContext ac, String domain) throws Exception {
		DB db = DBFactory.getDB();
		String orgid = ac.getVisitor().getUser().getOrgId();
		String comdtytype="3";
		MapList data = db.query("select * from ws_commodity where orgid='"
				+ orgid + "'   AND comdytype="+comdtytype+"  order by create_time");
		// 从根节点创建树
		Tree tree = getTree(db, data,0,comdtytype);
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
	private Tree getTree(DB db, MapList data, int index,String type) throws Exception {
		Tree tree = new Tree();
		String id = "";
		String menuname = "";
		String ccode="";
		if (index == 0) {
			menuname="服务项目分类";
			id="3";
		}else{
			Row row = data.getRow(index-1);
			id = row.get("comdy_class_id");
			menuname = row.get("class_name");
			ccode=row.get("c_code");
		}
		tree.setId(id);
		tree.setName(menuname);
		tree.setAttribute(ATTRIBUTE);
		if (id.equals("1")) {
			tree.setAction("");
		} else {
			tree.setActionType(TreeActionType.FUNCTION);// 动作类型:连接/方法
			tree.setAction("setRole");
		}
		for (int i = 0; i < data.size(); i++) {
			String parent = data.getRow(i).get("parent_id");
			if (id.equals(parent)) {
				tree.add(getTree(db, data, i+1,type));
			}
		}
		return tree;
	}

}
