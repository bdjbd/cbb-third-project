package com.ambdp.agricultureProject.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月20日
 *@version
 *说明：购买农业项目Server
 */
public class BuyProjectStockServer {
	
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	
	/**
	 * 为对应的机构出事化一个指定编号模版的农业项目。
	 * @param db  DB
	 * @param projectCode 项目模版项目编号
	 * @param orgId 机构ID，可以为空
	 * @param state 1:草稿;2:发布
	 * @return  新项目的id
	 * @throws JDBCException 
	 */
	public String copyProject(DB db,String projectCode,String orgId,String state) throws JDBCException{
		
		String id="";
		
		//1，查询机构名称
		//2,根据模版编号查询项目
		//3,替换项目数据
		String queryOrg="SELECT * FROM aorg WHERE orgid=? ";
		MapList orgMap=db.query(queryOrg, orgId,Type.VARCHAR);
		String orgName="";
		if(!Checker.isEmpty(orgMap)){
			orgName=orgMap.getRow(0).get("orgname");
		}
		
		//p_type 1:手动项目，2：模板项目
		String querySQL="SELECT * FROM mall_agriculture_projects WHERE p_code=? AND p_type=2 ";
		
		MapList projectMap=db.query(querySQL,projectCode,Type.VARCHAR);
		
		if(!Checker.isEmpty(projectMap)){
			Row row=projectMap.getRow(0);
			
			Table table=new Table("am_bdp","MALL_AGRICULTURE_PROJECTS");
			TableRow insertRow=table.addInsertRow();
			insertRow.setValue("p_name",orgName+row.get("p_name"));
			insertRow.setValue("stock_price",row.get("stock_price"));
			insertRow.setValue("total_stocks",row.get("total_stocks"));
			insertRow.setValue("min_buy_number",row.get("min_buy_number"));
			insertRow.setValue("prospectus",row.get("prospectus"));
			insertRow.setValue("publish_time",row.get("publish_time"));
			insertRow.setValue("status",state);
			insertRow.setValue("withdrawal_limit",row.get("withdrawal_limit"));
			insertRow.setValue("penalty_ratio",row.get("penalty_ratio"));
			insertRow.setValue("already_buy_number",0);
			
			//项目类型
			insertRow.setValue("p_type",1);
			//项目编号
			insertRow.setValue("p_code",projectCode);
			insertRow.setValue("org_id",orgId);
			
			//保存项目
			db.save(table);
			id=table.getRows().get(0).getValue("id");

		}else{
			logger.error("未查询到编号为："+projectCode+"的项目，无法为机构"+orgId+"初始化项目。");
		}
		
		return id;
	}
	
	
	/**
	 * 更新农业项目表中已经认购份数
	 * @param projectId 农业项目表ID
	 * @param buyNumber 购买分数
	 * @param db
	 * @throws JDBCException 
	 */
	public void updateProject(String projectId, int buyNumber, DB db) throws JDBCException {
		
		String updateProjectsSql = "UPDATE mall_agriculture_projects SET already_buy_number=(COALESCE(already_buy_number,0)+"+buyNumber+") WHERE id='"+projectId+"' ";
		
		db.execute(updateProjectsSql);
	}
	
	/**
	 * 更新社员/机构项目投资购买股份记录表中单股价格和状态
	 * @param id 项目投资购买股份 ID
	 * @param stockPrice 每股金额
	 * @param db
	 * @throws JDBCException 
	 */
	public void updateBuyStock(String id, String stockPrice, DB db) throws JDBCException {
		
		String updateBuyStockSql = "UPDATE mall_buy_stock_record SET stock_price='"+stockPrice+"',status=2 WHERE id='"+id+"' ";
		
		db.execute(updateBuyStockSql);
		
	}
	
	/**
	 * 扣除购买用户的现金账号余额
	 * @param stockPrice 每股金额
	 * @param buyNumber  购买的股份数量
	 * @param orgid 购买者的ID
	 * @param db
	 * @throws JDBCException 
	 */
	public void updateAccountInfo(String code,String stockCode,String stockPrice, int buyNumber,
			String orgid, DB db) throws JDBCException {
		
//		String updateAccountInfoSql = "UPDATE mall_account_info SET balance=balance-("+stockPrice+"*"+buyNumber+") WHERE member_orgid_id='"+orgid+"' "
//				+ " AND a_class_id=(SELECT id FROM mall_system_account_class WHERE sa_code='"+code+"') ";
//		
//		db.execute(updateAccountInfoSql);	
//		String UpdateStockAccountSql  = "UPDATE mall_account_info SET total_amount=total_amount+("+stockPrice+"*"+buyNumber+") WHERE member_orgid_id='"+orgid+"' "
//				+ " AND a_class_id=(SELECT id FROM mall_system_account_class WHERE sa_code='"+stockCode+"') ";
//		db.execute(UpdateStockAccountSql);
		
	}
	
	
	/**
	 * 给社员/机构项目投资购买股份记录表新增购买记录
	 * 
	 * @param db
	 * @param uuid
	 *            uuid
	 * @param projectId
	 *            农业项目id
	 * @param memberid
	 *            购买人
	 * @param number
	 *            购买数
	 * @param stockPrice
	 *            单股价格
	 * @throws JDBCException
	 */
	public void insert(DB db, String uuid, String projectId, String memberid, int number, String stockPrice)
			throws JDBCException {
		if (!Checker.isEmpty(projectId)) {
			String sqlinsert = "INSERT INTO mall_buy_stock_record( id, project_id, buyer_id, buyer_type, buy_number, stock_price, create_time,status) "
					+ "values ('" + uuid + "','" + projectId + "','" + memberid + "','1','" + number + "','"
					+ stockPrice + "',now(),'2') ";
			db.execute(sqlinsert);
		}
	}
	

}
