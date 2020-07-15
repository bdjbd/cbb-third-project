package com.ambdp.content;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;

/** * @author  作者：yangdong
 * @date 创建时间：2016年3月30日 下午12:19:17
 * @version 
 * @parameter  
 * @since  
 * @return
 */
public class ContentMenuSave extends DefaultAction {
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//主表----内容组件菜单
		Table table=ac.getTable("contentmenu");
		db.save(table);
		//获取主表主键
		String parentId=table.getRows().get(0).getValue("id");
		
		//获取子表--内容菜单属性
		Table childTable=ac.getTable("contentattribute");
		for(int i=0;i<childTable.getRows().size();i++){
			childTable.getRows().get(i).setValue("contentid", parentId);
		}
		
		db.save(childTable);
	}
}
