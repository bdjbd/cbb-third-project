package com.am.frame.member.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年8月13日
 * @version 说明:<br />
 *          会员管理，列表删除会员
 */
public class MemberListDeleteAction extends DefaultAction {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {

		// 获得选择列: _s_单元编号
		String[] select = ac.getRequestParameters("_s_am_member_list");
		// 获得主键: 单元编号.元素编号.k（设置了映射表时会自动用隐藏域记录主键值）
		String[] memberIds = ac.getRequestParameters("am_member_list.id.k");
		if (!Checker.isEmpty(select)) {
			for (int i = 0; i < select.length; i++) {
				if ("1".equals(select[i])) {// 1为选中
					//需要删除的会员ID
					String membeId=memberIds[i];
					logger.info("删除会员操作，操作帐号:"+ac.getVisitor().getUser().getId()
							+",删除会员ID为："+membeId);
					
					String delSQL="DELETE FROM am_member WHERE id=? ";
					db.execute(delSQL,membeId,Type.VARCHAR);
					
					//删除会员的邀请码
					delSQL="DELETE FROM am_MemberInvitationCode WHERE am_memberid = ?";
					db.execute(delSQL,membeId,Type.VARCHAR);
					
					//删除邀请会员与社员之间的关系
					delSQL="DELETE FROM am_member_distribution_map WHERE sub_member_id=? ";
					db.execute(delSQL, membeId, Type.VARCHAR);
					
					//删除会员的任务
					delSQL="DELETE FROM am_UserTask  WHERE memberid=? ";
					db.execute(delSQL, membeId, Type.VARCHAR);
				}
			}
		}

	}

}
