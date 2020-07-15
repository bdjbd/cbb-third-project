package com.am.frame.task.reward.impl;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;

/**
 * 计算用户充值奖励处理
 * @author xianlin
 *2016年4月14日
 */
public class RechargeRewardMethod {

	//私有的默认构造子
	private RechargeRewardMethod() {}
	//注意，这里没有final
	private static RechargeRewardMethod singlerrm=null;
	//静态工厂方法 
	public synchronized  static RechargeRewardMethod getInstance() {
		 if (singlerrm == null) { 
			 singlerrm = new RechargeRewardMethod();
		 }
		 return singlerrm;
	}
	
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	
	
	/**
	 * 增加用户分红权
	 * @param Memberid		会员id
	 * @param bonusNumber	分红权数
	 * @param db
	 * @param conn
	 * @return
	 * @throws JDBCException 
	 */
	public boolean increaseMemberDividendRight(String Memberid,long bonusNumber,DB db)
			throws JDBCException
	{
		boolean bool=false;
		
		String AccountTypeid = RechargeRewardMethod.getInstance().returnAccountTypeId(Var.get("Cumulative_consumption_account_type"), db);
		
		String SQL="UPDATE mall_account_info SET enabled_dividend_sharing = enabled_dividend_sharing+?"
				+ " WHERE member_orgid_id=? AND a_class_id=?";
		
		int result = db.execute(SQL, new String[]{bonusNumber+"",Memberid,AccountTypeid}, new int[]{Type.INTEGER,Type.VARCHAR,Type.VARCHAR});
		
		if(result>0)
		{
			bool=true;
		}
		
		return bool;
	}
	
	/**
	 * 删减用户分红权
	 * @param Memberid		会员id
	 * @param bonusNumber	分红权数
	 * @param db
	 * @param conn
	 * @return
	 * @throws JDBCException 
	 */
	public boolean CutMemberDividendRight(String Memberid,long bonusNumber,DB db)
			throws JDBCException
	{
		boolean bool=false;
		
		String AccountTypeid = RechargeRewardMethod.getInstance().returnAccountTypeId(Var.get("Cumulative_consumption_account_type"), db);
		
		String SQL="UPDATE mall_account_info SET enabled_dividend_sharing = enabled_dividend_sharing-?"
				+ " WHERE member_orgid_id=? AND a_class_id=?";
		
		int result = db.execute(SQL, new String[]{bonusNumber+"",Memberid,AccountTypeid}, new int[]{Type.INTEGER,Type.VARCHAR,Type.VARCHAR});
		
		if(result>0)
		{
			bool=true;
		}
		
		return bool;
	}
	
	/**
	 * 增加用户累计充值金额
	 * @param MemberId		会员id
	 * @param money		单位必须是分
	 * @param db
	 * @return
	 * @throws SQLException 
	 * @throws JDBCException 
	 */
	
	public boolean UpdateMemberCumulativeChargeMoney(String MemberId,long money,DB db) throws JDBCException
	{
	
		String AccountTypeid = RechargeRewardMethod.getInstance().returnAccountTypeId(Var.get("Accumulated_amount_account"), db);
		
		boolean bool=false;
		
		String SQL="UPDATE mall_account_info SET total_charge_amount=total_charge_amount+?"
				+ " WHERE member_orgid_id=? AND a_class_id=?";
		
		int result = db.execute(SQL, new String[]{money+"",MemberId,AccountTypeid}, new int[]{Type.INTEGER,Type.VARCHAR,Type.VARCHAR});
		
		if(result>0)
		{
			bool=true;
		}
		
		return bool;
	}
	
	/**
	 * 设置用户累计充值金额
	 * @param Memberid		会员id
	 * @param Money		累计充值金额
	 * @param db
	 * @param conn
	 * @return
	 * @throws JDBCException 
	 */
	
	public boolean UpdateSetupMemberCumulativeChargeMoney(String Memberid,long Money,DB db)
			throws JDBCException
	{	
		String AccountTypeid = RechargeRewardMethod.getInstance().returnAccountTypeId(Var.get("Accumulated_amount_account"), db);
		
		boolean bool=false;
		
		String SQL="UPDATE mall_account_info SET total_charge_amount=?"
				+ " WHERE member_orgid_id=? AND a_class_id=?";
		
		int result = db.execute(SQL, new String[]{Money+"",Memberid,AccountTypeid}, new int[]{Type.INTEGER,Type.VARCHAR,Type.VARCHAR});
		
		if(result>0)
		{
			bool=true;
		}
		
		return bool;
	}
	
	/**
	 * 查询会员累计充值金额
	 * @param Memberid
	 * @param db
	 * @return
	 * @throws JDBCException 
	 */
	public long QueryMoney(String Memberid,DB db) throws JDBCException
	{
		long totalmoney=0;
		
		String AccountTypeid = RechargeRewardMethod.getInstance().returnAccountTypeId(Var.get("Accumulated_amount_account"), db);
		
		String SQL="SELECT total_charge_amount AS totalmoney"
				+ " FROM mall_account_info"
				+ " WHERE member_orgid_id=? AND a_class_id=?";
		
		
		MapList maplist= db.query(SQL,new String[]{Memberid,AccountTypeid}, new int[]{Type.VARCHAR,Type.VARCHAR});
		
		if(maplist.size()>0)
		{
			totalmoney =maplist.getRow(0).getLong("totalmoney",0);
		}
		
		return totalmoney;
	}
	
	
	/**
	 * 查询账号编号的id
	 * @param code	账号编号
	 * @return 
	 * @throws JDBCException 
	 */
	public String returnAccountTypeId(String code,DB db) throws JDBCException
	{
		//编号id
		String codeid = "";
		
		String SQL="SELECT id FROM mall_system_account_class WHERE sa_code=?";
		
		MapList maplist= db.query(SQL,new String[]{code}, new int[]{Type.VARCHAR});
		
		if(maplist.size()>0)
		{
			codeid = maplist.getRow(0).get("id");
		}
		return codeid;
	}
	/**
	 * 获取后台设置最大设置金额
	 * @param db
	 * @return
	 * @throws JDBCException 
	 */
	public long returnsetmaxMoney(String valcode,DB db) throws JDBCException
	{
		long maxmoney=0; 
		
		String SQL="SELECT vvalue FROM avar WHERE vid = ?";
		
		MapList maplist= db.query(SQL,new String[]{valcode}, new int[]{Type.VARCHAR});
		
		if(maplist.size()>0)
		{
			maxmoney = Long.parseLong(maplist.getRow(0).get("vvalue"))*100;
		}
		
		return maxmoney;
	}
	
}
