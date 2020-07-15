package com.am.app_plugins.precision_help.server;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.badge.AMBadgeManager;
import com.am.frame.badge.BadgeImpl;
import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.detail.AfterDetailBean;
import com.am.frame.transactions.detail.TransactionDetail;
import com.am.frame.transactions.virement.VirementManager;
import com.ambdp.agricultureProject.server.BuyProjectStockServer;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年5月26日
 *@version
 *说明：帮扶server
 */
public class PrecisionHelpServer {

	/**
	 * 给被帮扶者信誉卡增加额度
	 * @param db
	 * @param tomemberid  入账账户id
	 * @param inAccountCode  入账类型
	 * @param helpCreditAmount   入账金额
	 * @throws JDBCException 
	 */
	public void updateTomember(DB db, String tomemberid, String inAccountCode,
			String helpCreditAmount,String inRemarks) throws JDBCException {
		
		VirementManager vm=new VirementManager();
		
		MapList iList =vm.getAccountInfo(db,inAccountCode,tomemberid); 
		//credit_amount 授信额度
		//consumer_donor_amount 消费者捐助额度
		String updateTomemberSql = "UPDATE  mall_account_info SET credit_amount = credit_amount+("+helpCreditAmount+"*100),consumer_donor_amount=consumer_donor_amount+("+helpCreditAmount+"*100) "
				+ " WHERE member_orgid_id='"+tomemberid+"'  AND a_class_id=(SELECT id FROM mall_system_account_class WHERE sa_code='"+inAccountCode+"')";
		
		db.execute(updateTomemberSql);
		
		long trade_total_money=VirementManager.changeY2F(helpCreditAmount);
		
		//入账交易记录
		TransactionDetail transactionDetail=new TransactionDetail();
    	AfterDetailBean inDetailBean = new AfterDetailBean();
    	inDetailBean.setTableRow(inDetailBean.getTranTable().addInsertRow());
    	String inId = UUID.randomUUID().toString();
		inDetailBean.setId(inId);
		inDetailBean.setMember_id(tomemberid);
		inDetailBean.setTrade_state("2");
		inDetailBean.setRmarks(inRemarks);
		inDetailBean.setTrade_total_money(trade_total_money);
		inDetailBean.setAccount_id(iList.getRow(0).get("id"));
		inDetailBean.setSa_class_id(iList.getRow(0).get("class_id"));
		
		transactionDetail.earningActions(db, inDetailBean);
	}

	/**
	 * 分配分红徽章
	 * @param db
	 * @param tomemberid  分配者id
	 * @param helpType 帮扶类型
	 * @param enjoyMaxAmount  经营分红最大额度
	 * @param enjoySingleBorrowAmount  帮借单次额度
	 * @param helpAmount  帮赠分红金额比例
	 * @throws JDBCException 
	 * @throws JSONException 
	 */
	public void dividendRight(DB db, String memberid, String helpType, Double helpAmount, Double borrowAmount, Double enjoyMaxAmount) throws JDBCException, JSONException {
		//该会员是否有徽章
		String  badgeSql = "SELECT * FROM mall_userbadge WHERE enterprisebadgeid="
				+ "(SELECT id FROM mall_enterprisebadge WHERE ent_badge_code='Badge.FH.LEVEL') AND memberid='"+memberid+"'";
	
		MapList	badgeList = db.query(badgeSql);
		//判断该会员是否有徽章 为空没有徽章
		if (!Checker.isEmpty(badgeList)){
			//徽章id
			String id= badgeList.getRow(0).get("id");
			//徽章参数
			String badgeParame = badgeList.getRow(0).get("badgeparame");
			JSONObject bgParameJs=new JSONObject(badgeParame);
			Double badge = bgParameJs.getDouble("FH_RATIO");
				//判断是帮借还是帮赠 1：帮赠 2：帮借
				if("1".equals(helpType)){
					updateBadgeParame(db,id,helpAmount,bgParameJs,badge,enjoyMaxAmount);
				}else{
					updateBadgeParame(db,id,borrowAmount,bgParameJs,badge,enjoyMaxAmount);
				}
		}else{
			String Badge_FH = BadgeImpl.Badge_FH;
			AMBadgeManager badgeManager = new AMBadgeManager();
			JSONObject reuslt=badgeManager.addBadgeByEntBadgeCode(db,Badge_FH,memberid);
			String id = reuslt.getString("ID");
			
			//判断是帮借还是帮赠 1：帮赠 2：帮借
			if("1".equals(helpType)){
				addUpdateBadgeParame(db,id,helpAmount);
			}else{
				addUpdateBadgeParame(db,id,borrowAmount);
			}
		}
	}
	
	/**
	 * 没有超过最大额度时 修改参数
	 * @param db
	 * @param id 会员徽章比例
	 * @param helpAmount 分红金额比例
	 * @param bgParameJs 
	 * @param badge 
	 * @param enjoyMaxAmount 
	 * @throws JDBCException 
	 * @throws JSONException 
	 */
	private void updateBadgeParame(DB db, String id,
			Double helpAmount, JSONObject bgParameJs, Double badge, Double enjoyMaxAmount) throws JDBCException, JSONException {
		
		//修改完后判断是否超过最大限度
		Double updateValue = badge+helpAmount;
		if(enjoyMaxAmount<updateValue||enjoyMaxAmount==updateValue){
			bgParameJs.put("FH_RATIO", enjoyMaxAmount);
			String updateMax = " UPDATE mall_UserBadge SET BadgeParame = '"+bgParameJs+"' WHERE id ='"+id+"'";
			db.execute(updateMax);
		}else{
			bgParameJs.put("FH_RATIO", badge+helpAmount);
			String update = " UPDATE mall_UserBadge SET BadgeParame = '"+bgParameJs+"' WHERE id ='"+id+"'";
			db.execute(update);
		}
		
		
	}

	/**
	 * 新增的时候修改参数
	 * @param db
	 * @param id  会员徽章id
	 * @param helpAmount 分红金额比例
	 * @throws JDBCException 
	 * @throws JSONException 
	 */
	private void addUpdateBadgeParame(DB db, String id,
			Double helpAmount) throws JDBCException, JSONException {
		//该会员是否有徽章
		String  Sql = "SELECT * FROM mall_userbadge WHERE id='"+id+"' ";
		MapList	badgeList = db.query(Sql);
		if (!Checker.isEmpty(badgeList)){
			//徽章参数
			String badgeParame = badgeList.getRow(0).get("badgeparame");
			JSONObject bgParameJs=new JSONObject(badgeParame);
			
			bgParameJs.put("FH_RATIO", helpAmount);
			String update = " UPDATE mall_UserBadge SET BadgeParame = '"+bgParameJs+"' WHERE id ='"+id+"'";
			db.execute(update);
			
		}
	}
	
	/**
	 * 给帮扶信息表中添加数据
	 * @param db
	 * @param helpType 帮扶类型
	 * @param tomemberid 被帮扶者id
	 * @param memberid 帮扶者id
	 * @param pay_money 帮扶金额
	 * @param roleid 帮扶者类型
	 * @throws JDBCException 
	 */
	public void addHelpInfo(DB db, String helpType, String tomemberid,
			String memberid, String pay_money, String roleid) throws JDBCException {
		String uuid = UUID.randomUUID().toString();
		
		//判断是帮借还是帮赠 1：帮赠 2：帮借
		if("1".equals(helpType)){
			String sqlinsert = "INSERT INTO mall_help_info( id, be_helped_id, help_id, help_role_type,help_role,help_type,help_amount,create_time,repayment_status) "
					+ "VALUES ('"+uuid+"','"+tomemberid+ "','"+memberid+ "','1','"+roleid+"','1',"+pay_money+"*100,now(),'1') ";
			db.execute(sqlinsert);
		}else{
			String sqlinsert = "INSERT INTO mall_help_info( id, be_helped_id, help_id, help_role_type,help_role,help_type,help_amount,create_time,repayment_status) "
					+ "VALUES ('"+uuid+"','"+tomemberid+ "','"+memberid+ "','1','"+roleid+"','2',"+pay_money+"*100,now(),'2') ";
			db.execute(sqlinsert);
		}
		
	}
	/**
	 * 单位帮扶
	 * @param db
	 * @param helpType
	 * @param tomemberid
	 * @param memberid
	 * @param pay_money
	 * @param roleid
	 * @throws JDBCException 
	 */
	public void addCompanyHelpInfo(DB db, String helpType, String tomemberid,
			String memberid, String pay_money, String roleid) throws JDBCException {
		String uuid = UUID.randomUUID().toString();
		//判断是帮借还是帮赠 1：帮赠 2：帮借
		if("1".equals(helpType)){
			String sqlinsert = "INSERT INTO mall_help_info( id, be_helped_id, help_id, help_role_type,help_role,help_type,help_amount,create_time,repayment_status) "
					+ "VALUES ('"+uuid+"','"+tomemberid+ "','"+memberid+ "','2','"+roleid+"','1',"+pay_money+"*100,now(),'1') ";
			db.execute(sqlinsert);
		}else{
			String sqlinsert = "INSERT INTO mall_help_info( id, be_helped_id, help_id, help_role_type,help_role,help_type,help_amount,create_time,repayment_status) "
					+ "VALUES ('"+uuid+"','"+tomemberid+ "','"+memberid+ "','2','"+roleid+"','2',"+pay_money+"*100,now(),'2') ";
			db.execute(sqlinsert);
		}
		
	}
	
	
	/**
	 * 帮助被帮扶者投资农业项目
	 * 1，给被帮扶者身份股金账户入账 股金单价*数量元金额 ，入账记录：xxx帮扶投资村头冷库1股（10000元）账户金额入账。
	 * 2，给被帮扶在身份股金账户出账  股金单价*数量元金额，入账记录：xxx帮投资村头冷库1股（10000元）进行投资。
	 * 3，给被帮扶投资农业项目
	 * @param db
	 * @param tomemberid  被帮扶者
	 * @param memberName 帮扶者会员名称
	 * @param stockNumber  投资股数
	 * @param  stockPrice  投资项目每股价格,单位元
	 * @throws Exception 
	 */
	public void farmProjectInvest(DB db, String tomemberid,String memberName, long stockNumber,double stockPrice)
			throws Exception{
		VirementManager vm = new VirementManager();
		
		String virementNumber=(stockPrice*stockNumber)+"";
		String inRemarks=memberName+"帮扶投资村头冷库"+stockNumber+"股（"+virementNumber+"元）账户金额入账。";
		//入账
		vm.execute(db,"", tomemberid, "",SystemAccountClass.IDENTITY_STOCK_ACCOUNT, virementNumber, inRemarks,"", "",false);
		
		String outRemarks=memberName+"帮投资村头冷库"+stockNumber+"股（"+virementNumber+"元）进行投资。";
		
		//出账
		vm.execute(db, tomemberid, SystemAccountClass.IDENTITY_STOCK_ACCOUNT, virementNumber, outRemarks, "", "");
		
		farmProject(db, tomemberid, "", virementNumber);
	}
	
	/**
	 * 购买农业项目
	 * @param db
	 * @param tomemberid  被帮扶的人
	 * @param memberid	帮扶的人
	 * @param helpAmountStorage	投资金额 单位元
	 * @throws JDBCException 
	 */
	public void farmProject(DB db, String tomemberid, String memberid,
			String helpAmountStorage) throws JDBCException {
		
		//村头冷酷项目项目编号
		String projectCode=Var.get("PROJECT_STORAGE_CODE");
		
		//查询被帮扶人所在机构的农业项目 和能购买的股数
		String  projectSql = "SELECT ("+helpAmountStorage+"*100/ap.stock_price)AS number,ap.* "
				+ " FROM mall_agriculture_projects AS ap "
						+ " LEFT JOIN am_member AS mem ON ap.org_id=mem.orgcode "
						+ " WHERE mem.id='"+tomemberid+"' "
						+ " and AP.status=2 "
						+ " AND ap.p_code='"+projectCode+"'  "
						+ " ORDER BY ap.create_time DESC ";
		
		MapList	projectList = db.query(projectSql);
		if (!Checker.isEmpty(projectList)){
			//项目id
			String projectId = projectList.getRow(0).get("id");
			
			//股数
			int number =VirementManager.changeY2FInt(projectList.getRow(0).get("number"));
			
			//单价
			String stockPrice = projectList.getRow(0).get("stock_price");
			
			//给购买股份记录表中加数据
			SaverBuyProject(db,projectId,tomemberid,number,stockPrice);
			
			BuyProjectStockServer service=new BuyProjectStockServer();
			
			//更新农业项目表中已经认购份数
			service.updateProject(projectId,number,db);
		}
		
	}
	
	/**
	 * 给购买股份记录表中加数据
	 * @param db 
	 * @param projectId 项目id
	 * @param tomemberid  购买人
	 * @param number  购买股数
	 * @param stockPrice 购买单价
	 * @throws JDBCException 
	 */
	private void SaverBuyProject(DB db, String projectId, String memberid,
			int number, String stockPrice) throws JDBCException {
		//获取 MALL_BUY_STOCK_RECORD 组件
		Table table=new Table("am_bdp","MALL_BUY_STOCK_RECORD");
		TableRow insertTr=table.addInsertRow();
		insertTr.setValue("project_id", projectId);
		insertTr.setValue("buyer_id", memberid);
		insertTr.setValue("buyer_type", 1);
		insertTr.setValue("buy_number", number);
		insertTr.setValue("stock_price", stockPrice);
		insertTr.setValue("status", 2);
		db.save(table);
		
	}
	
	/**
	 * 修改还款状态为已还款
	 * @param db
	 * @param helpId  帮助信息ID
	 * @throws JDBCException 
	 */
	public void updateHelpInfo(DB db, String helpId) throws JDBCException {
		
		String updateHelpInfoSql = "UPDATE mall_help_info  "
				+ " SET repayment_status='3',repayment_time='now()' WHERE id='"+helpId+"' ";
		db.execute(updateHelpInfoSql);
		
	}

	/**
	 * 被帮扶者帮扶次数+1
	 * @param db
	 * @param tomemberid 被帮扶者ID
	 * @param tabname  表名
	 * @throws JDBCException
	 */
	public void updateToMem(DB db, String tomemberid, String tabname) throws JDBCException {
		
		String updateToMem = "UPDATE "+tabname+"  "
				+ " SET total_aid_times=total_aid_times+1 WHERE id='"+tomemberid+"' ";
		
		db.execute(updateToMem);
		
	}

	/**
	 * 更新帮扶信息 ，此帮扶者社员与被帮扶者信息关系存在，则只更新相关信息
	 * 
	 * @param db DB
	 * @param helpMemberId 帮扶者
	 * @param memberId 被帮扶者
	 * @param amonut 帮扶金额，单位分
	 * @throws JDBCException 
	 */
	public void updateHelpInfo(DB db, String helpMemberId, String memberId, long amonut) throws JDBCException{
		
		String updateSQL="UPDATE mall_help_info "
				+ " SET help_invest_times=COALESCE(help_invest_times,0)+1,"
				+ " help_invest_total_amount="+amonut+"+COALESCE(help_invest_total_amount,0) "
				+ " WHERE be_helped_id='"+helpMemberId+"' AND help_id='"+memberId+"' ";
		
		db.execute(updateSQL);
	}

}
