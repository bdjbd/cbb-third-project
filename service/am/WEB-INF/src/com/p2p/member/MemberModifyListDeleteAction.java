package com.p2p.member;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;


/**
 * 删除会员Action
 * @author Administrator
 *
 */
public class MemberModifyListDeleteAction extends DefaultAction {
	private final String tag="com.p2p.member.MemberModifyListDeleteAction";
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		String[] selects=ac.getRequestParameters("_s_ws_member1.list");
		String[] mmeberCodes=ac.getRequestParameters("ws_member1.list.member_code.k");
		
		if(!Checker.isEmpty(selects)){
			for(int i=0;i<selects.length;i++){
				if("1".equals(selects[i])){//判断选中会员
					deleteMember(db,mmeberCodes[i],ac.getVisitor().getUser().getId());
				}
			}
		}
	}
	
	/**
	 * 根据会员编号删除会员
	 * @param db
	 * @param memberCode
	 * @param operatoer 
	 */
	private void deleteMember(DB db,String memberCode,String operatoer)throws JDBCException{
		String deleteSQL=
				"begin;                                                      "+
				"	DELETE FROM p2p_OrgElectTicker WHERE member_code="+memberCode+";"+
				"	DELETE FROM p2p_UserBadge WHERE member_code="+memberCode+";"+
				"	DELETE FROM p2p_UserTask WHERE member_code="+memberCode+";"+
				"	DELETE FROM p2p_MemberAbility WHERE member_code="+memberCode+";"+
				"	DELETE FROM p2p_DispatchRecod WHERE member_code="+memberCode+";"+
				"	DELETE FROM p2p_DispatchRecod WHERE order_code IN(         "+
				"	SELECT order_code FROM ws_order WHERE member_code="+memberCode+");"+
				"	DELETE FROM ws_order WHERE member_code="+memberCode+";"+
				"	DELETE FROM ws_addres WHERE member_code="+memberCode+";"+
				"	DELETE FROM  ws_member WHERE member_code="+memberCode+";"+
				"END;";
		com.wisdeem.wwd.WeChat.Utils.Log(tag,"删除会员操作,操作人："+operatoer+"   SQL:"+deleteSQL);
		db.execute(deleteSQL);
	}
}
