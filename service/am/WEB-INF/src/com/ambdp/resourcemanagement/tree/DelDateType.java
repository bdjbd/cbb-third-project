package com.ambdp.resourcemanagement.tree;

import com.fastunit.Ajax;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

	/***
	 * 
	 * 删除资料类型
	 *
	 */
public class DelDateType extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//请求ID
		String clazzId=ac.getRequestParameter("id");
		//检查是否有子元素
		String checkSQL="SELECT * FROM mjyc_resourceclass  WHERE parentid=? ";
		
		MapList map=db.query(checkSQL,clazzId,Type.VARCHAR);
		
		
		Ajax ajax=new Ajax(ac);
		
		if(Checker.isEmpty(map)){
			
			//删除分类
			String deletSQL="DELETE FROM mjyc_resourceclass WHERE id=? ";
			db.execute(deletSQL,clazzId,Type.VARCHAR);
			
			ajax.addScript("location.reload();");
		}else{
			ajax.addScript("alert('此分类下面有子分类，请先删除子分类。');location.reload();");
		}
		
		ajax.send();
	}
}
