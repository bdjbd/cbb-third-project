package com.am.frame.unitedpress;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.member.MemberManager;
import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.virement.VirementManager;
import com.am.frame.unitedpress.reward.IBuyServiceShopReward;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年9月30日
 * @version 
 * 说明:<br />
 * 组织机构购买服务类
 */
public class UnitedpressBusinessService {

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	public void rejectApplication(DB db,String id,String tableName,String remarks) throws JDBCException{
		//申请驳回，需要返回购买机构的金额到现金账号
		String querySQL="SELECT buy_price/100.0::float AS buy_prices,* FROM "+tableName+" WHERE id=? ";
		VirementManager vm=new VirementManager();
		MapList map=db.query(querySQL,id,Type.VARCHAR);
		
		if(!Checker.isEmpty(map)){
			vm.execute(db,"", map.getRow(0).get("purchaser"), 
					"",SystemAccountClass.CASH_ACCOUNT,
					map.getRow(0).get("buy_prices"), remarks, "",
					"", false);
		}
	}
	
	
	/**
	 * 将金额转到购买机构的信用保证金账号
	 * @param db
	 * @param ac
	 * @param unitId
	 * @param rewardMeony 
	 * @throws JDBCException
	 */
	public void addPaymentToCreditMargin(DB db,ActionContext ac,String unitId, long rewardMeony,String orgId) throws JDBCException{
		//将金额转账到购买机构的信用保证金
		
		String gap_name=ac.getRequestParameter(unitId+".gap_name");
		String payMeont=ac.getRequestParameter(unitId+".buy_prices");
		String purchaser=ac.getRequestParameter(unitId+".purchaser");
		
		String id=ac.getRequestParameter(unitId+".orgid");
		
		
		MemberManager mm=new MemberManager();
		MapList memberMap=mm.getMemberById(purchaser, db);
		
		if(!Checker.isEmpty(memberMap)){
			purchaser=memberMap.getRow(0).get("membername");
		}
		
		long rw=VirementManager.changeY2F(payMeont)-rewardMeony;
		
		if(rw<0){
			payMeont="0";
			logger.error("奖励后到信用保证金账户的钱小于0。");
		}else{
			payMeont=VirementManager.changeF2Y(rw)+"";
		}
		
		String iremakers=purchaser+"创办"+gap_name+"，信用保证金为"+payMeont+"元。";
		
		VirementManager vm=new VirementManager();
		
//		orgId=Var.get("operation_rog_orgid");//运营管理机构机构编号
		
		vm.execute(db, 
				"",
				orgId,
				"",
				SystemAccountClass.GROUP_CREDIT_MARGIN_ACCOUNT,
				payMeont,
				iremakers,
				"",
				"",
				false);
		
		//为机构增加空间费用
//		SpecRechangeService srs=new SpecRechangeService();
//		String rechangeId=srs.addBuySpecRecord(db, "1",payMeont, "0", orgId);
//		srs.updateDueTimeToConfirm(db,rechangeId);
		
	}
	
	/**
	 * 
	 * @param db  DB
	 * @param groupTableName 组织机构表明
	 * @param orgId  机构id
	 * @return  分利后的剩余金额
	 * @throws Exception 
	 */
	public long executeShareBenefits(DB db, String groupTableName, String orgId) throws Exception {
		long surplusMeony=0;
		
		//查询区域类型
		String area_type="03";
		String getInFoSQL="SELECT area_type,* FROM "+groupTableName+" WHERE orgid=? ";
		
		MapList groupInfoMap=db.query(getInFoSQL,new String[]{
				orgId
		},new int[]{
				Type.VARCHAR
		});
		
		if(!Checker.isEmpty(groupInfoMap)){
			//01 全国;02 省;03 市;04 区县
			area_type=groupInfoMap.getRow(0).get("area_type");
		}
		
		//1,查询对应的接口
		//2,执行购买后分利接口实现类
		if(groupTableName!=null){
			groupTableName=groupTableName.toLowerCase();
		}
		
		String querySQL="SELECT msct.reward_impl,msct.org_type   "+
						" FROM mall_service_commodity AS msc     "+
						" LEFT JOIN mall_service_comd_type AS msct ON msc.sc_type=msct.id      "+
						" LEFT JOIN aorgtype AS ogt ON ogt.orgtype=msct.org_type      "+
						" WHERE t_table_name=? AND area_type=? ";
		MapList map=db.query(querySQL, new String[]{
				groupTableName,area_type
		},new int[]{
				Type.VARCHAR,Type.VARCHAR
		});
		
		
		
		if(!Checker.isEmpty(map)){
			String rewImplClass=map.getRow(0).get("reward_impl");
			
			if(!Checker.isEmpty(rewImplClass)){
				IBuyServiceShopReward rewardImpl=(IBuyServiceShopReward) Class.forName(rewImplClass).newInstance();
				
				Map<String,String> params=new HashMap<String,String>();
				String result=rewardImpl.executeReward(db, groupTableName, orgId, params);
				
				JSONObject mResult=new JSONObject(result);
				
				//获取总共奖励的金额
				if(mResult!=null&&mResult.has(IBuyServiceShopReward.REWARD_MEONY)){
					surplusMeony=mResult.getLong(IBuyServiceShopReward.REWARD_MEONY);
				}else{
					logger.error("执行招商费用分配奖励，但是未分配金额。"+mResult);
				}
				
			}
			
		}
		
		return surplusMeony;
		
	}


	
	/**
	 * 增加金额到信用保证金
	 * @param db
	 * @param ac
	 * @param string
	 * @param memberId
	 * @throws JDBCException 
	 */
	public void addPaymentToMembr(DB db, ActionContext ac, String unitId, String memberId) throws JDBCException {

		String gap_name=ac.getRequestParameter(unitId+".gap_name");
		String payMeont=ac.getRequestParameter(unitId+".buy_prices");
		String purchaser=ac.getRequestParameter(unitId+".purchaser");
		
		MemberManager mm=new MemberManager();
		MapList memberMap=mm.getMemberById(purchaser, db);
		
		if(!Checker.isEmpty(memberMap)){
			purchaser=memberMap.getRow(0).get("membername");
		}
		
		String iremakers=purchaser+"创办"+gap_name+"，信用保证金为"+payMeont+"元。";
		
		VirementManager vm=new VirementManager();
		
//		orgId=Var.get("operation_rog_orgid");//运营管理机构机构编号
		
		vm.execute(db, 
				"",
				memberId,
				"",
				SystemAccountClass.CREDIT_MARGIN_ACCOUNT,
				payMeont,
				iremakers,
				"",
				"",
				false);
		
	}

	/**
	 * 借款创业增加金额到借款金额
	 * @param db
	 * @param ac
	 * @param string
	 * @param memberId
	 * @throws JDBCException 
	 */
		public void addPaymentr(DB db, String id, String orgId) throws Exception {
			
		int money = 0;
		int promise = 1;
		//查询大众创业借款金额
		String LoanBusinessSql = "SELECT MSC.PRICE,MBR.PROMISE "
				+ " FROM MALL_BORROWING_RECORDS AS MBR "
				+ " LEFT JOIN MALL_SERVICE_COMMODITY AS MSC ON MBR.SC_ID = MSC.ID "
				+ " WHERE MBR.ORGID = '"+id+"'";
		
		MapList LoanBusiness = db.query(LoanBusinessSql);
		
		if(!Checker.isEmpty(LoanBusiness)){
			money = LoanBusiness.getRow(0).getInt("price", 0);
			promise = Integer.parseInt(LoanBusiness.getRow(0).get("promise"));
		}
		
		
		
		//向组织借款账户添加借款记录
		if(!Checker.isEmpty(orgId)){
			String updateLoanBusinessSql = "UPDATE mall_account_info "
					+ " SET loan_amount = '"+money+"',"
					+ " repayment_amount = '0',"
					+ " repayment_date= (SELECT to_date(extract(year from (SELECT now() + interval '"+promise+" month') )||'-'||extract(month from (SELECT now() + interval '"+promise+" month') )||'-01','yyyy-MM-dd HH24:MI:SS')) "
					+ " WHERE id = "
					+ " ( "
					+ " SELECT mai.id "
					+ " FROM mall_account_info as mai "
					+ " LEFT JOIN mall_system_account_class as msac on msac.id = mai.a_class_id "
					+ " WHERE mai.member_orgid_id= '"+orgId+"' and msac.sa_code= 'GROUP_LOAN_ACCOUNT')";
			db.execute(updateLoanBusinessSql);
		}
			
		
//		VirementManager vir = new VirementManager();
//		String iremakers = "您通过借款创业，向平台借取"+""+money+""+"元。";
//
//		if(!Checker.isEmpty(money)){
//			
//			vir.execute(db, "", orgId, "", SystemAccountClass.GROUP_LOAN_ACCOUNT, money, iremakers, "", "", false);
//			
//		}
		
		
	}
	
	
	
}
