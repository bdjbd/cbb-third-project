package com.p2p.member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * Author: Mike
 * 2014年7月22日
 * 说明：
 *   会员绘制保存Action
 **/
public class UserDadgeListSaveAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		Table table=ac.getTable("P2P_USERBADGE");
		List<TableRow> rows=table.getRows();
		String memberCode=ac.getRequestParameter("p2p_userbadge.list.member_code");
		if(memberCode==null){
			memberCode=(String)ac.getSessionAttribute("authen.membercode");
		}
		
		Map<String,String> badges=new HashMap<String,String>();
		Map<String,String> badgesType=new HashMap<String,String>();
		
		int rowsSize=rows.size();
		int i=0;
		for(TableRow tr: rows){
			i++;
			String entid=tr.getValue("enterprisebadgeid");
			String params=tr.getValue("badgeparame");
//			String checkSQL=
//					"SELECT bt.id FROM p2p_BadgeTemplate AS bt "+
//							"	LEFT JOIN p2p_EnterpriseBadge AS eb      "+
//							"	ON bt.id=eb.badgetemplateid              "+
//							"	LEFT JOIN p2p_UserBadge AS ub            "+
//							"	ON ub.enterprisebadgeid=eb.id            "+
//							"	WHERE  eb.id='"+entid+"'"+
//							"	AND ub.member_code="+memberCode+
//							"	AND eb.orgid='"+ac.getVisitor().getUser().getOrgId()+"'";
			String checkSQL="SELECT badgecode FROM "
					+ " p2p_BadgeTemplate AS bt "
					+ " LEFT JOIN p2p_EnterpriseBadge AS eb "
					+ " ON eb.BadgeTemplateID=bt.id "
					+ " WHERE eb.id='"+entid+"' "
					+ " AND orgid='"+ac.getVisitor().getUser().getOrgId()+"'";
			MapList map=db.query(checkSQL);
			if(!Checker.isEmpty(map)){
				badges.put(map.getRow(0).get("badgecode"), params);
			}
			String sql="SELECT * FROM p2p_enterprisebadge WHERE id='"+entid+"'";
			MapList maps=db.query(sql);
			if(Checker.isEmpty(maps))continue;
			tr.setValue("badgeparame", maps.getRow(0).get("badgeparame"));
			//更新会员为维修人员徽章
			String  checkWXRJ="SELECT bt.badgecode FROM p2p_EnterpriseBadge AS eb "
					+ " LEFT JOIN p2p_BadgeTemplate AS bt "
					+ " ON eb.BadgeTemplateID=bt.id "
					+ " WHERE eb.id='"+entid+"' AND bt.badgecode='Badge.WXRJ'";
			MapList wxrjMap=db.query(checkWXRJ);
			if(!Checker.isEmpty(wxrjMap)){
				checkWXRJ="UPDATE ws_member SET type=1 WHERE member_code="+memberCode;
				db.execute(checkWXRJ);
			}
		}
		if(rowsSize>badges.size()){
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("此用户已经拥有该徽章");
			ac.getActionResult().setUrl("/wwd/p2p_member_authen.form.do?m=e&membercode="+memberCode);
			ac.getActionResult().setScript("location.reload();");
		}else{
			///wwd/p2p_member_authen.form.do?m=e&membercode=$D{member_code}
			ac.getActionResult().setUrl("/wwd/p2p_member_authen.form.do?m=e&membercode="+memberCode);
		}
		
		super.doAction(db, ac);
		
	}
}
