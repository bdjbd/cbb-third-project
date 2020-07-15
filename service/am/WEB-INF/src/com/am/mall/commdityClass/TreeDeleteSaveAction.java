package com.am.mall.commdityClass;

import com.fastunit.Ajax;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年12月3日
 * @version 
 * 说明:<br />
 * 树删除Action
 */
public class TreeDeleteSaveAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//请求ID
		String clazzId=ac.getRequestParameter("clazzId");
		
		//检查是否有子元素
		String checkSQL="SELECT * FROM mall_CommodityClass  WHERE upid=? ";
		
		MapList map=db.query(checkSQL,clazzId,Type.VARCHAR);
		
		
		Ajax ajax=new Ajax(ac);
		
		if(Checker.isEmpty(map)){
			
			//映射关系删除
			String deletSQL="DELETE FROM mall_CommodityClassRelationship WHERE commodityclassid=?";
			db.execute(deletSQL,clazzId,Type.VARCHAR);
			
			//删除分类
			deletSQL="DELETE FROM mall_CommodityClass WHERE id=? ";
			db.execute(deletSQL,clazzId,Type.VARCHAR);
			
			ajax.addScript("location.reload();");
		}else{
			
			ajax.addScript("alert('此分类下面有子分类，请先删除子分类。');location.reload();");
		}
		
		ajax.send();
	}
	
}
