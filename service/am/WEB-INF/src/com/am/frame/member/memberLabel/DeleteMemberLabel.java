package com.am.frame.member.memberLabel;

import java.util.ArrayList;
import java.util.List;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年7月3日
 * @version 
 * 说明:<br />
 * 删除会员标签
 */
public class DeleteMemberLabel extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		// 获得选择列: _s_单元编号
				String[] select = ac.getRequestParameters("_s_am_member_label_ration.list");
				// 获得主键: 单元编号.元素编号.k（设置了映射表时会自动用隐藏域记录主键值）
				String[] bookId = ac.getRequestParameters("am_member_label_ration.list.id.k");
				List<String> selectedBookId = new ArrayList<String>();
				if (!Checker.isEmpty(select)) {
					for (int i = 0; i < select.length; i++) {
						if ("1".equals(select[i])) {// 1为选中
							selectedBookId.add(bookId[i]);
							String delsql="DELETE  FROM   mall_member_label_relation WHERE id=? ";
							db.execute(delsql,bookId[i],Type.VARCHAR);
						}
					}
				}

				// 设置提示消息
				StringBuffer msg = new StringBuffer();
				if (Checker.isEmpty(selectedBookId)) {
					msg.append("没有选择数据");
				} 
		
		
	}
	
}
