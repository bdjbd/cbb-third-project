package com.ambdp.label;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;

/** * @author  作者：yangdong
 * @date 创建时间：2016年1月14日 下午4:36:39
 * @version 
 * @parameter  
 * @since  
 * @return
 */
public class SaveLabelAction extends DefaultAction{
	
		@Override
		public void doAction(DB db,ActionContext ac) throws Exception{
			
			//主表 标签分类表-MALL_MARKCLASSIFY 
			Table parentTable = ac.getTable("mall_markclassify");
			//保存主表
			db.save(parentTable);
			//获取主表主键id
			String parentId = parentTable.getRows().get(0).getValue("id");
			
			//获取子表
			Table childTable = ac.getTable("mall_mark");
			
			//将主表id保存到子表中
			for(int i = 0; i < childTable.getRows().size();i ++){
				childTable.getRows().get(i).setValue("classid", parentId);
			}
			
			db.save(childTable);
		}
}
