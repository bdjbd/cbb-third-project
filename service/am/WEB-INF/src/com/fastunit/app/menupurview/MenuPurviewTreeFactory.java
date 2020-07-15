/**
 * 终端菜单权限点集合树
 * 丁照祥
 * 2012-05-16
 * */
package com.fastunit.app.menupurview;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.TreeFactory;
import com.fastunit.util.Checker;
import com.fastunit.util.EmptyObject;
import com.fastunit.view.tree.Tree;

public class MenuPurviewTreeFactory extends TreeFactory {

	private static final Logger log = LoggerFactory
			.getLogger(MenuPurviewTreeFactory.class);
	private MapList list=null;

	private static final String SQL_ORG = "select * from abdp_TerminalMenu";
	@Override
	public Tree createTree(ActionContext ac, String domain) throws Exception {
		DB db = DBFactory.getDB();
		String roleid=ac.getRequestParameter("roleid");
		//String roleidSession=ac.getRequestParameter("roleid");
		if(roleid==null)
		{
			roleid=ac.getSessionAttribute("roleid","");
		}
		ac.setSessionAttribute("roleid", roleid);
		String selSql="select * from abdp_RoleTerminalMenu where roleid='"+roleid+"'";
		list=db.query(selSql);
		
		// 获得数据
		MapList data = db.query(SQL_ORG);
		if (Checker.isEmpty(data)) {
			String id = UUID.randomUUID().toString();
			return null;
		}
		// 确定根节点
		int rootRowIndex = data.findRowIndex("fathermenuid",
				EmptyObject.STRING);
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
	
	private Tree getTree(DB db, MapList data, int index) throws Exception {
		Tree tree = new Tree();
		Row row = data.getRow(index);
		String id = row.get("id");
		String menuName = row.get("menuname");
		if(list.size()>0)
		{
			Row MenuRow=list.findRow("menuid",id);
			if(MenuRow!=null)
			{
				tree.setCheckAttribute("checked");
				String permission=MenuRow.get("permission");
				if(permission.equals("0"))
				{
					tree.setName(menuName+"(不可用)");
				}
				else if(permission.equals("1"))
				{
					tree.setName(menuName+"(查看)");
				}
				else if(permission.equals("2"))
				{
					tree.setName(menuName+"(编辑)");
				}
				else if(permission.equals("3"))
				{
					tree.setName(menuName+"(配置)");
				}
				else
				{
					tree.setName(menuName);
				}
			}
			else
			{
				tree.setName(menuName);
			}
		}
		else
		{
			tree.setName(menuName);
		}
		tree.setId(id);

//		tree.setAction("/app/materielinfo.do?clear=wd_ylsh.materielinfo.query&materielinfo.list.catalogid="
//						+ catalogid);// 制定方法名
		for (int i = 0; i < data.size(); i++) {
			String parent = data.getRow(i).get("fathermenuid");
			if (id.equals(parent)) {
				tree.add(getTree(db, data, i));
			}
		}
		return tree;
	}
}
