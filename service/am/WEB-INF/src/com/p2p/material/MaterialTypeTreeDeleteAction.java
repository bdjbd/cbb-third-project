package com.p2p.material;

import com.fastunit.Ajax;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * 商品分类删除Action
 * @author Administrator
 *
 */
public class MaterialTypeTreeDeleteAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		Ajax ajax=new Ajax(ac);
		
		String typeCodeId=ac.getRequestParameter("typecodeId");
		//检查此分类是否被使用
		String checkSQL="SELECT * FROM p2p_MaterialsCode WHERE TypeCode='"+typeCodeId+"'";
		MapList map=db.query(checkSQL);
		
		
		if(!Checker.isEmpty(map)){
			ajax.addScript("alert(\"该分类已经被引用，不能删除！\")");
			
			ac.getActionResult().setSuccessful(false);
		}else{
			String deletSQL="UPDATE p2p_materialsType SET datastatus=3 WHERE id='"+typeCodeId+"'";
			db.execute(deletSQL);
			
			
			ajax.addScript("window.parent.document.frames[0].location='/wwd/p2p_materialstype.tree.do?m=s';");
			ajax.addScript("window.parent.document.getElement('iframe[id=p2p_materialstype.frame.e2]').src='about:blank';");
			ac.getActionResult().setSuccessful(true);
		}
		ajax.send();
	}
}
