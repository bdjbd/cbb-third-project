package com.p2p.material;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.TreeFactory;
import com.fastunit.util.Checker;
import com.fastunit.view.tree.Tree;
import com.fastunit.view.tree.config.TreeActionType;

public class P2pMaterialTypeTree extends TreeFactory {

	private static final Logger log = LoggerFactory.getLogger(P2pMaterialTypeTree.class);
	
	private static final String ATTRIBUTE =" oncontextmenu=showMenu()  onclick=onclickShowMenu()"; //onmousedown=whichElement(event) 

	@Override
	public Tree createTree(ActionContext ac, String treeDoamin) throws Exception {
		
		String orgid = ac.getVisitor().getUser().getOrgId();
		
		String sql="SELECT * FROM p2p_materialstype WHERE ( orgid = '"+ orgid + "' OR id ='1' ) AND datastatus!=3  ORDER BY createtime";
		
		DB db = DBFactory.getDB();
		// 获得数据
		MapList data = db.query(sql);
		if (Checker.isEmpty(data)) {
			return null;
		}
		// 确定根节点
		int rootRowIndex = data.findRowIndex("parentid", "");
		if (rootRowIndex < 0) {
			rootRowIndex = data.findRowIndex("parentid", null);
		}
		if (rootRowIndex < 0) {

			log.error("could not found the root of role");
			return null;
		} else {
			// 从根节点创建树
			Tree tree = getTree(db, data, rootRowIndex);
			return tree;
		}
	}

	private Tree getTree(DB db, MapList data, int index) throws Exception {
			Tree tree = new Tree();
			Row row = data.getRow(index);
			String id = row.get("id");
			String menuname = row.get("tname");
			tree.setId(id);
			tree.setName(menuname);
			tree.setAttribute(ATTRIBUTE);
			
			// 动作类型:点击菜单
			tree.setActionType(TreeActionType.FUNCTION);
			//单击事件
			tree.setAction("setRole");//
			
			for (int i = 0; i < data.size(); i++) {
				String parent = data.getRow(i).get("parentid");
				if (id.equals(parent)) {
					tree.add(getTree(db, data, i));
				}
			}
			return tree;
		}

}
