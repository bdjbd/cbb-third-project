package com.fastunit.app;


import com.fastunit.LangUtil;
import com.fastunit.MapList;
import com.fastunit.adm.AdmLang;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.TransactionAction;
import com.fastunit.user.admin.AdminCache;
import com.fastunit.user.org.OrgCache;
import com.fastunit.user.table.AAdminOrg;
import com.fastunit.user.table.AOrg;
import com.fastunit.user.table.AOrgLeader;
import com.fastunit.user.table.AOrgLevel;
import com.fastunit.user.table.AOrgRole;
import com.fastunit.user.table.AUser;
import com.fastunit.util.Checker;

public class DeleteOrgCarAction extends TransactionAction {

	// 删除前确认没有子节点
	private static final String CONFIRM = "select * from " + AOrg.TABLENAME
			+ " where " + AOrg.PARENT_ID + "=?";
    // TODO 删除前确认没有被引用
	
	// 删除前确认没有车辆
		private static final String CARNAME = "select * from cdms_vehiclebasicinformation"
				+ " where  orgcode=?";
	// org
	private static final String ORG = "delete from " + AOrg.TABLENAME + " where "
			+ AOrg.ORG_ID + "=?";
	// OrgLeader
	private static final String ORG_LEADER = "delete from "
			+ AOrgLeader.TABLENAME + " where " + AOrgLeader.ORG_ID + "=?";
	// OrgLevel
	private static final String ORG_LEVEL = "delete from " + AOrgLevel.TABLENAME
			+ " where " + AOrgLevel.UPPER_ID + "=? or " + AOrgLevel.LOWER_ID + "=? ";
	// administrator
	private static final String ADMIN = "delete from " + AAdminOrg.TABLENAME
			+ " where " + AAdminOrg.ORGANIZATION_ID + "=?";
	// role
	private static final String ROLE = "delete from " + AOrgRole.TABLENAME
			+ " where " + AOrgRole.ORGANIZATION_ID + "=?";
	// user
	private static final String USER = "update " + AUser.TABLENAME + " set "
			+ AUser.ORG_ID + "='' where " + AUser.ORG_ID + "=?";

	public void doAction(DB db, ActionContext ac) throws Exception {
		String orgId = ac.getRequestParameter("orgid");

		MapList list = db.query(CONFIRM, orgId, Type.VARCHAR);
		MapList listcar = db.query(CARNAME, orgId, Type.VARCHAR);
	     if((Checker.isEmpty(list)&&Checker.isEmpty(listcar))) {
			// delete
			db.execute(ORG, orgId, Type.VARCHAR);
			db.execute(ORG_LEADER, orgId, Type.VARCHAR);
			db.execute(ORG_LEVEL, new String[] { orgId, orgId }, new int[] {
					Type.VARCHAR, Type.VARCHAR });
			db.execute(ADMIN, orgId, Type.VARCHAR);
			db.execute(ROLE, orgId, Type.VARCHAR);
			db.execute(USER, orgId, Type.VARCHAR);
			// clear cache
			OrgCache.clear(orgId);
			AdminCache.clearAll();
		  }
		else {
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("有下级机构或者有车辆信息");
					
			
		}
	}

	



}
