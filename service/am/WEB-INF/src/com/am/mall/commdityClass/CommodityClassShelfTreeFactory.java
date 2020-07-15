package com.am.mall.commdityClass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ambdp.menu.tree.MoblieMenuTreeFactory;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.TreeFactory;
import com.fastunit.util.Checker;
import com.fastunit.view.tree.Tree;
import com.fastunit.view.tree.config.TreeActionType;

/**
 * @author Mike
 * @create 2014年10月24日
 * @version 
 * 
 * 说明:<br />
 * 商品分类TreeFactory
 * 
 */
public class CommodityClassShelfTreeFactory extends TreeFactory {
	
	private static final Logger log = LoggerFactory.getLogger(MoblieMenuTreeFactory.class);
	
	
	private static final String ATTRIBUTE = "oncontextmenu=mallCommoditClassRightMenu()  onclick=onclickShowMenu() ";

	@Override
	public Tree createTree(ActionContext ac, String treeDoamin) throws Exception {
		
		//直查询状态为启用不显示的
//		String sql="SELECT mcc.id,mcc.title,mcc.upid FROM  mall_commodityclass as mcc"
//				+ " left join auser as au on au.department_id = mcc.department_id"
//				+ " WHERE (mcc.orgcode LIKE '"+ac.getVisitor().getUser().getOrgId()+"%' AND mcc.CommodityClassState='2' ) "
//				+ " OR mcc.id='1' and au.userid = '"+ac.getVisitor().getUser().getId()+"'"
//				+ " ORDER BY mcc.sort  ";
		String sql="SELECT mcc.id,mcc.title,mcc.upid FROM  mall_commodityclass as mcc"
				+ " left join auser as au on au.department_id = mcc.department_id "
				+ " WHERE (mcc.orgcode LIKE '"+ac.getVisitor().getUser().getOrgId()+"%' OR mcc.id='1') "
				+ " and ( au.userid = '"+ac.getVisitor().getUser().getId()+"' OR mcc.id='1' ) AND ( mcc.CommodityClassState='2' OR mcc.id='1') "
				+ " ORDER BY mcc.sort  ";
	
		DB db = DBFactory.getDB();
		// 获得数据
		MapList data = db.query(sql);
		if (Checker.isEmpty(data)) {
			return null;
		}
		// 确定根节点
		int rootRowIndex = data.findRowIndex("upid", "");
		if (rootRowIndex < 0) {
			rootRowIndex = data.findRowIndex("upid", null);
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
			String menuname = row.get("title");
			tree.setId(id);
			tree.setName(menuname);
			tree.setAttribute(ATTRIBUTE);
			
			// 动作类型:右键菜单
			tree.setActionType(TreeActionType.FUNCTION);
			//单击事件
			tree.setAction("mallSelectCommodityClassId");//
			
			for (int i = 0; i < data.size(); i++) {
				String parent = data.getRow(i).get("upid");
				if (id.equals(parent)) {
					tree.add(getTree(db, data, i));
				}
			}
			return tree;
		}

}
