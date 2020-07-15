package com.am.instore.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * 机构分红服务类
 * @author yuebin
 *
 */
public class GroupBonusService {

	
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	/**
	 * 根据入账金额计算分红权
	 * 1，记录分红权到对应的账户的累计金额字段中。
	 * 2，根据累计金额和分红权金额计算分红权。
	 * 3，更新累计金额。
	 * 4，更新分红权。
	 * @param db DB
	 * @param menoy  入账金额,单位分
	 * @param 分红机构账号
	 * @throws JDBCException 
	 */
	public void calculationDividendRightBuyInMeony(DB db,long menoy,String orgId) throws JDBCException{
		
		//获取累计充值账号
		String accountCode=Var.get("GROUP_Accumulated_amount_account");
		
		//1,记录分红权到对应的账户的累计金额字段中。
		updateTotalChargeAmount(db, menoy, orgId,accountCode);
		
		//2,根据累计金额和分红权金额计算分红权,并更新累计金额
		int divInt=calculationDividentByTotlteAmount(db,orgId,accountCode);
		
		//更新分红权
		updateDividentRight(db,divInt,orgId,SystemAccountClass.GROUP_BONUS_ACCOUNT);
	}

	
	/**
	 * 更新账号对应的分红权
	 * @param db
	 * @param divInt
	 * @param orgId
	 * @param accountCode
	 * @throws JDBCException 
	 */
	private void updateDividentRight(DB db, int divInt, String orgId, String accountCode) throws JDBCException {
		String updateSQL="UPDATE mall_account_info "+
				"   SET enabled_dividend_sharing=COALESCE(enabled_dividend_sharing,0)+? "+
				"   WHERE member_orgid_id=?  "+
				"        AND a_class_id IN( "+
				"           SELECT id FROM mall_system_account_class WHERE sa_code=?  "+
				"         )";
		
		db.execute(updateSQL,new String[]{
				divInt+"",orgId,accountCode
		},new int[]{
				Type.BIGINT,Type.VARCHAR,Type.VARCHAR
		});
		
		logger.info("更新分红权，member_orgid_id："+orgId+",分红权数量："+divInt+",账户编号："+accountCode);
	}



	/**
	 * 据累计金额和分红权金额计算分红权,并更新累计金额
	 * @param db DB
	 * @param orgId  机构ID
	 * @param accountCode 计算分红权累计金额的财务账号编码，够一个分红权将分配一个分红权
	 * @return 可以获取分红权数量
	 * @throws JDBCException 
	 */
	private int calculationDividentByTotlteAmount(DB db, String orgId,String accountCode) throws JDBCException {
		int reuslt=0; 
		
		//单位元
		double cumulativecharge=Var.getDouble("cumulativecharge", 0);
		
		long cumulativechargeL=VirementManager.changeY2F(cumulativecharge);
		
		//查询累计金额
		String querySQL="SELECT COALESCE(total_charge_amount,0)/"+cumulativechargeL+" AS div_int,"+//div_int 分红权
						"   COALESCE(total_charge_amount,0)%"+cumulativechargeL+" AS balance "+//balance 余额
						"  FROM mall_account_info AS mai "+
						"  LEFT JOIN mall_system_account_class AS msac ON mai.a_class_id=msac.id  "+
						"  WHERE msac.sa_code='"+accountCode+"'  "+
						"  AND mai.member_orgid_id='"+orgId+"' ";
		
		MapList map=db.query(querySQL);
		
		if(!Checker.isEmpty(map)){
			//分红权
			reuslt=map.getRow(0).getInt("div_int", 0);
			
			//获取余额
			long balance=map.getRow(0).getInt("balance", 0);
			
			//更新余额   获取财务账号对应 的ID
			querySQL="SELECT * FROM mall_system_account_class WHERE sa_code='"+accountCode+"' ";
			map=db.query(querySQL);
			if(!Checker.isEmpty(map)){
				String aClassId=map.getRow(0).get("id");
				
				//更新累计金额
				String updateSQL="UPDATE mall_account_info "
						+ " SET total_charge_amount=? "
						+ " WHERE member_orgid_id=? AND a_class_id=?  ";
				
				db.execute(updateSQL,new String[]{
						balance+"",orgId,aClassId
				}, new int[]{
						Type.INTEGER,Type.VARCHAR,Type.VARCHAR
				});
			}
			
			logger.info(orgId+"获得分红权,数量："+reuslt+"，剩余金额(分)："+balance);
			
		}else{
			logger.error("未找到对应的累计金额记录，orgId："+orgId+",accountCode:"+accountCode);
		}
		
		return reuslt;
	}


	/**
	 * 更新累计分红权金额.
	 * @param db DB
	 * @param menoy  金额，单位分
	 * @param orgId  机构ID
	 * @param accountCode 计算分红权累计金额的财务账号编码，够一个分红权将分配一个分红权
	 * @throws JDBCException
	 */
	private void updateTotalChargeAmount(DB db, long menoy, String orgId,String accountCode) throws JDBCException {
		
		//获取财务账号对应 的ID
		String querySQL="SELECT * FROM mall_system_account_class WHERE sa_code='"+accountCode+"' ";
		
		MapList map=db.query(querySQL);
		if(!Checker.isEmpty(map)){
			String aClassId=map.getRow(0).get("id");
			
			//更新累计金额
			String updateSQL="UPDATE mall_account_info "
					+ " SET total_charge_amount=COALESCE(total_charge_amount,0)+? "
					+ " WHERE member_orgid_id=? AND a_class_id=?  ";
			
			db.execute(updateSQL,new String[]{
					menoy+"",orgId,aClassId
			}, new int[]{
					Type.INTEGER,Type.VARCHAR,Type.VARCHAR
			});
			
			logger.info("更新累计充值金额，accountCode:"+accountCode+",member_orgid_id:"+orgId);
			
		}else{
			logger.error("找不到对应的账户编码，编码："+accountCode);
		}
			
	}
	
}
