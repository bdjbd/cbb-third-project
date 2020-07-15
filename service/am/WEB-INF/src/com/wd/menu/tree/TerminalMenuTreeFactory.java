package com.wd.menu.tree;

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

/**
 * @author zhujun 2012-05-15 终端菜单管理
 * 
 *         动态生成终端菜单管理树 MaterielCatalogTreeFactory
 */
public class TerminalMenuTreeFactory extends TreeFactory {
	private static final Logger log = LoggerFactory
			.getLogger(TerminalMenuTreeFactory.class);

	private static final String ATTRIBUTE = "oncontextmenu=showMenu()";

	private String SQL_MENUMANAGER = "select * from abdp_terminalmenu order by sortno";

	// copy from TreeHelper: 不使SQL怪异防止某些数据库不支持，同时提高效率
	@Override
	public Tree createTree(ActionContext ac, String domain) throws Exception {
		DB db = DBFactory.getDB();
		// 获得数据
		MapList data = db.query(SQL_MENUMANAGER);
		if (Checker.isEmpty(data)) {
			return null;
		}
		// 确定根节点
		int rootRowIndex = data.findRowIndex("fathermenuid", "");
		if (rootRowIndex < 0) {
			rootRowIndex = data.findRowIndex("fathermenuid", null);
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
	private Tree getTree(DB db, MapList data, int index) throws Exception {
		Tree tree = new Tree();
		Row row = data.getRow(index);
		String id = row.get("id");
		String menuname = row.get("menuname");
		tree.setId(id);
		tree.setName(menuname);
		tree.setAttribute(ATTRIBUTE);
		// tree.setActionType(TreeActionType.FUNCTION);//动作类型:连接/方法
		tree.setAction("/app/terminalmenuparam.do?m=s&terminalmenu.form.id="
				+ id);// 制定方法名
		for (int i = 0; i < data.size(); i++) {
			String parent = data.getRow(i).get("fathermenuid");
			if (id.equals(parent)) {
				tree.add(getTree(db, data, i));
			}
		}
		return tree;
	}

}
