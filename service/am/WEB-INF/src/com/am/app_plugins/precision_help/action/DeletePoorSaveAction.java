package com.am.app_plugins.precision_help.action;

import java.util.ArrayList;
import java.util.List;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;


/**
 * 删除贫困户action
 * @author yuebin
 *
 * 删除贫困户为，修改am_member的is_poor为0
 *
 */
public class DeletePoorSaveAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// 获得选择列: _s_单元编号
		String[] select = ac.getRequestParameters("_s_anti_am_member.list");
		
		// 获得主键: 单元编号.元素编号.k（设置了映射表时会自动用隐藏域记录主键值）
		String[] memberIds = ac.getRequestParameters("anti_am_member.list.id.k");
		List<String[]> delMemberList = new ArrayList<String[]>();
		
		String upateSQL="UPDATE am_member SET is_poor=0 WHERE id=? ";
		
		if (!Checker.isEmpty(select)) {
			for (int i = 0; i < select.length; i++) {
				if ("1".equals(select[i])) {// 1为选中
					//删除贫困户
					delMemberList.add(new String[]{memberIds[i]});
				}
			}
		}
		
		db.executeBatch(upateSQL, delMemberList, new int[]{Type.VARCHAR});
		
	}
	
}
