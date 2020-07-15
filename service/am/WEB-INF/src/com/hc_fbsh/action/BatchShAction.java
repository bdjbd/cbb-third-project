package com.hc_fbsh.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.DefaultAction;
import com.hc_fbsh.util.ShUpdate;

/**
 * @author 郭仁杰
 *  说明：批量审核action
 */
public class BatchShAction extends DefaultAction {

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {

		String[] ids = ac.getRequestParameters("hc_fbsh.list.id.k");
		String[] idSelects = ac.getRequestParameters("_s_hc_fbsh.list");
		String actionParam = ac.getActionParameter();
		String shr = ac.getRequestParameter("hc_fbsh.list.shr1");
		String shsj = ac.getRequestParameter("hc_fbsh.list.shsj1");
		
		DB db = DBFactory.getDB();
		for (int i = 0; i < ids.length; i++) {
			if ("1".equals(idSelects[i])) {
				String updateSQL = "UPDATE HC_FBSH SET shr="+"'"+shr+"'"+",shsj="+"'"+shsj+"'"+" WHERE id="+"'"+ids[i]+"'";
				db.execute(updateSQL);
				ShUpdate.update(ac, db, actionParam, ids[i]);
				
				
			}
		}
		return ac;
	}

	
}
