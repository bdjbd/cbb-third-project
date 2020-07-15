package com.am.frame.systemAccount.groupAccount;

import org.json.JSONObject;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年4月29日
 * @version 
 * 说明:<br />
 * 
 */
public abstract class GroupInitActionAbstract implements GroupInitAccountAction {

	@Override
	public JSONObject initOrg(DB db, String orgId) throws Exception{
		return null;
	}
	
	
	/**
	 * 检查同一个机构是否已经初始化过
	 * @param db
	 * @param orgId
	 * @param tableName
	 * @return
	 * @throws JDBCException
	 */
	public MapList checkOrgExist(DB db,String orgId,String tableName) throws JDBCException{
		MapList map=null;
		String checkSQL="SELECT * FROM "+tableName+" WHERE orgid=? ";
		map=db.query(checkSQL, orgId, Type.VARCHAR);
		return map;
	}
	
	/**
	 * 检查相同类的机构是否已经存在
	 * @param db DB 
	 * @param orgid 机构ID
	 * @param tableName 表名
	 * @return MapList id,f_status(0：待审核;1：审核通过;2：审核拒绝;3：撤销),
	 * @throws JDBCException 
	 */
	public MapList checkOrgTypeExist(DB db,String orgid,String tableName) throws JDBCException{
		
		String checkSQL="SELECT id,f_status FROM "+tableName+" WHERE orgid IN ( "+
						" SELECT orgid FROM aorg WHERE parentid IN ( " + 
						" SELECT parentid FROM aorg WHERE orgid=? ) "+
						"  AND orgtype=(SELECT orgtype FROM aorg WHERE orgid=? ) "+
						" )";
		
		MapList map=db.query(checkSQL, new String[]{orgid,orgid}, new int[]{Type.VARCHAR,Type.VARCHAR});
		
		return map;
	}
	
	/**
	 * 根据机构类型获取区域类型
	 * @param db
	 * @param orgType 机构类型
	 * @return
	 */
	public String getAreaType(DB db,String orgId){
		String result="";
		String querySQL="SELECT * FROM aorgtype WHERE orgtype =(SELECT orgtype FROM aorg WHERE orgid=? ) ";
		
		MapList map=null;
		try {
			map = db.query(querySQL,orgId,Type.VARCHAR);
			if(!Checker.isEmpty(map)){
				result=map.getRow(0).get("area_type");
			}
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	/**
	 * 初始化机构
	 * @param db DB
	 * @param orgId  对应的机构ID
	 * @param tableName  表名 联合社，合作社，农技协联合会，农厂，
	 * @throws JDBCException 
	 */
	public void addOrg(DB db,String orgId,String tableName) throws JDBCException{
		
//		2016年5月6日  有前台请求新增
//		Table table=new Table("am_bdp",tableName);
//		TableRow inserTr=table.addInsertRow();
//		
//		inserTr.setValue("orgid", orgId);
//		inserTr.setValue("area_type",getAreaType(db, orgId) );
//		inserTr.setValue("f_status","0" );
//		
//		db.save(table);
	}
	
	/**
	 * 初始化机构
	 * @param db DB
	 * @param orgId  对应的机构ID
	 * @param tableName  表名 联合社，合作社，农技协联合会，农厂，
	 * @param fStatus 状态 fStatus(0：待审核;1：审核通过;2：审核拒绝;3：撤销),
	 * @throws JDBCException 
	 */
	public void updateOrg(DB db,String orgId,String tableName,String fStatus) throws JDBCException{
		String updateSQL="UPDATE "+tableName+" SET f_status=? WHERE orgid=? ";
		db.execute(updateSQL,new String[]{fStatus,orgId}, new int[]{Type.VARCHAR,Type.VARCHAR});
	}
	
	
	
	
}
