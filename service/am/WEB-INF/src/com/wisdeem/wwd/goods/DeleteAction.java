package com.wisdeem.wwd.goods;

import com.fastunit.Ajax;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;


public class DeleteAction extends DefaultAction{
	private static final String SQL = "delete from ws_commodity where comdy_class_id = ?";
	private static final int[] SQL_TYPE = new int[] { Type.BIGINT };
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
	
		super.doAction(db, ac);
		
		String id = ac.getRequestParameter("parent_id");
		String strSql="select comdity_class_id from WS_COMMODITY_NAME where comdity_class_id='"+id+"' ";
		MapList rs = db.query(strSql);
		if(rs.size()!=0){
			Ajax ajax=new Ajax(ac);
			ajax.addScript("alert(\"该分类已经被引用，不能删除！\")");
			ajax.send();
			ac.getActionResult().setSuccessful(false);
			return;
		}

		db.execute(SQL, new String[] { id }, SQL_TYPE);
	}

}
