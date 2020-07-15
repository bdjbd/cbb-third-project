package com.p2p.member.maintain;

import java.util.ArrayList;
import java.util.List;

import com.fastunit.Ajax;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.AjaxAction;
import com.fastunit.util.Checker;

/**
 * 批量添加服务能力
 * @author Administrator
 *
 */
public class BatchSaveMaintainAjaxAction extends AjaxAction {
	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		String orgId=ac.getVisitor().getUser().getOrgId();
		String memberCode=ac.getRequestParameter("member_code");
		String clazzVale=ac.getRequestParameter("clazzVale");
		DB db=DBFactory.getDB();
		
		//获取分类编码
		String sql="SELECT c_code FROM ws_commodity "
				+ " WHERE comdytype=3  "
				+ " AND orgid='"+orgId
				+ "' AND comdy_class_id="+clazzVale;
		MapList map=db.query(sql);
		
		if(!Checker.isEmpty(map)){
			String querySQL="SELECT  cn.comdity_id,com.comdy_class_id  "
					+ " FROM ws_commodity_name AS cn "
					+ " LEFT JOIN ws_commodity AS com ON cn.comdity_class_id=com.comdy_class_id  "
					+ " WHERE cn.type=3 AND cn.orgid='"+orgId+"' "
					+ " AND com.c_code like '"+map.getRow(0).get("c_code")+"%'  "
					+ " AND cn.comdity_id NOT IN ( "
					+ " SELECT serbver_code FROM p2p_MemberAbility WHERE member_code="+memberCode 
					+ " ) ORDER BY cn.comdity_id";
			//查询此分类及其所有的子分类
			map=db.query(querySQL);
			List<String[]> comdityIds=new ArrayList<String[]>();
			if(!Checker.isEmpty(map)){
				for(int i=0;i<map.size();i++){
					comdityIds.add(new String[]{map.getRow(i).get("comdity_id"),map.getRow(i).get("comdy_class_id")});
				}
			}
			String inserSQL="INSERT INTO p2p_memberability("
					+ "id, member_code, serbver_code, create_time, server_clazzs)VALUES ("
					+ "uuid_generate_v1(),"+memberCode+", ?, now(), ?)";
			db.executeBatch(inserSQL, comdityIds, new int[]{Type.BIGINT,Type.BIGINT});
		}
		ac.getActionResult().setSuccessful(true);
		///wwd/p2p_maintain.form.do?m=e&p2p_maintain.form.member_code=$D{member_code}
		ac.getActionResult().setUrl("/wwd/p2p_maintain.form.do?m=e&p2p_maintain.form.member_code="+memberCode);
		Ajax ajax=new Ajax(ac);
		ajax.addScript("window.location.reload();");
		ajax.send();
		return ac;
	}
}
