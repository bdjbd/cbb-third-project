package com.p2p.comment;

import java.util.ArrayList;
import java.util.List;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * 评论删除action
 * @author Administrator
 *
 */
public class CommentDeleteAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//super.doAction(db, ac);
		String[] selects=ac.getRequestParameters("_s_p2p_comment.list");
		String[] comdId=ac.getRequestParameters("p2p_comment.list.id.k");
		
		List<String[]> deletId=new ArrayList<String[]>();
		if(!Checker.isEmpty(selects)){
			for(int i=0;i<selects.length;i++){
				if("1".equals(selects[i])){
					deletId.add(new String[]{comdId[i]});
				}
			}
		}
		String delteSQL="DELETE FROM p2p_comment WHERE id=?";
		db.executeBatch(delteSQL, deletId, new int[]{Type.VARCHAR});
		
	}
}
