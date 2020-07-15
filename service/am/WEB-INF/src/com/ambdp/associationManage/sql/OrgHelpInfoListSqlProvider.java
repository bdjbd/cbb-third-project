package com.ambdp.associationManage.sql;

import com.fastunit.FastUnit;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月18日
 *@version
 *说明：单位帮扶信息列表SqlProvider
 */
public class OrgHelpInfoListSqlProvider implements SqlProvider{

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		StringBuilder querSQL = new StringBuilder();
		DB db=null;
		try {
			db = DBFactory.getDB();
		String orgType="";
		String parentId = "";
		String org="0";
		String orgId=ac.getVisitor().getUser().getOrgId();
		if("org".equals(orgId)){
			org="1";
		}else{
			
			//查询当前登陆人所在机构在联合社中是否是区域联合社
			String selSQL = "SELECT * FROM aorg WHERE orgid = '"+orgId+"'";
			MapList map=db.query(selSQL);
			if(!Checker.isEmpty(map)){
				orgType = map.getRow(0).get("orgtype");
				parentId = map.getRow(0).get("parentid");
			}
			if("11".equals(orgType)){
				org="1";
			}else{
				org="2";
			}
		}
		
		querSQL.append("SELECT pce.proname||city.cityname||zone.zonename AS zonename,trim(to_char(COALESCE(mai.help_amount / 100.00),'99999999999990D99')) AS money,mai.*  ");
		querSQL.append(" FROM mall_help_info AS mai   ");
		querSQL.append(" LEFT JOIN aorg AS aorg ON mai.be_helped_id = aorg.orgid  ");
		querSQL.append(" LEFT JOIN am_member AS mem ON mai.help_id = mem.id   ");
		querSQL.append(" LEFT JOIN account_info AS info ON mai.be_helped_id=info.id   ");
		querSQL.append(" LEFT JOIN province AS pce ON info.province_id=pce.proid  ");
		querSQL.append(" LEFT JOIN city AS city ON info.city_id=city.cityid  ");
		querSQL.append(" LEFT JOIN zone AS zone ON info.zone_id=zone.zoneid  ");
		querSQL.append(" WHERE mai.help_role_type=2  ");
		if("2".equals(org)){
			if("41".equals(orgType)){
				 querSQL.append(" AND aorg.parentid='"+parentId+"'");
			}else{
				 querSQL.append(" AND mai.be_helped_id='"+orgId+"'");
			}
		}
		
		// 区县
		String zonename = null;
		// 联合社名称
		String help_idq = null;
		// 被帮扶自然村农厂/联合社
		String be_helped_idq = null;

		// 1,获取查询单元
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp","mall_aorg_help_info.query");

		if (queryRow != null) {
			// 区县
			zonename = queryRow.get("zonename");
		}
		if (queryRow != null) {
			// 帮扶者
			help_idq = queryRow.get("help_idq");
		}
		if (queryRow != null) {
			// 被帮扶者
			be_helped_idq = queryRow.get("be_helped_idq");
		}
		// 3,如果查询单元有数据，将数据拼接到where中
		if (!Checker.isEmpty(zonename)) {
			querSQL.append(" AND (pce.proname||city.cityname||zone.zonename)  LIKE  '%" + zonename + "%' ");
		}
		if (!Checker.isEmpty(help_idq)) {
			querSQL.append(" AND  mem.membername LIKE '%" + help_idq + "%' ");
		}
		if (!Checker.isEmpty(be_helped_idq)) {
			querSQL.append("  AND aorg.orgname LIKE '%" + be_helped_idq + "%' ");
		}

		querSQL.append(" ORDER BY mai.create_time DESC ");
		
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		return querSQL.toString();
	}

} 
