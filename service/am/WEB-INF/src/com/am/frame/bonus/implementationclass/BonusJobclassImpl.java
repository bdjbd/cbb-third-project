package com.am.frame.bonus.implementationclass;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.jgroups.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.VariableAcquisition;
import com.am.frame.badge.AMBadgeManager;

import com.am.frame.transactions.detail.AfterDetailBean;
import com.am.frame.transactions.detail.TransactionDetail;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

public class BonusJobclassImpl {

	private BonusJobclassImpl() {}
	private static BonusJobclassImpl singlebji=null;
	public synchronized  static BonusJobclassImpl getInstance() {
		 if (singlebji == null) { 
			 singlebji = new BonusJobclassImpl();
		 }
		 return singlebji;
	}
	private Logger logger=LoggerFactory.getLogger(this.getClass());
	
	
	/**
	 * 判断是否扣减分红权
	 * @param Memberid
	 * @param db
	 * @return
	 * @throws JDBCException 
	 */
	public boolean UpdateMemberbonus (String Memberid,DB db) throws JDBCException
	{
		boolean result=false;
		//获取平台设置的分红额度
		long platfrommoney = Long.parseLong(VariableAcquisition.getInstance().returnstr("dividendamount", db))*100;
		//用户分红过期累计金额
		long  outdatemney= BonusJobclassImpl.getInstance().querymember(Memberid, db);
		//用户累计金额
		long Accumulatedamount = outdatemney%platfrommoney;
		//减掉分红权数量
		long bonusNumber = outdatemney/platfrommoney;
		
		if(platfrommoney<=outdatemney)
		{
			BonusJobclassImpl.getInstance().CutMemberDividendRight(Memberid, bonusNumber,Accumulatedamount, db);
		}
		
		return result;
	}
	
	/**
	 * 判断是否扣减分红权
	 * @param Memberid
	 * @param db
	 * @return
	 * @throws JDBCException 
	 */
	public boolean UpdateMemberbonus_Business(String Memberid,String AccountTypeName,DB db) throws JDBCException
	{
		boolean result=false;
		//获取平台设置的分红额度
		long platfrommoney = Long.parseLong(VariableAcquisition.getInstance().returnstr("dividendamount", db))*100;
		//用户分红过期累计金额
		long outdatemney= BonusJobclassImpl.getInstance().querymember_Business(Memberid,AccountTypeName, db);
		//用户累计金额
		long Accumulatedamount = outdatemney%platfrommoney;
		//减掉分红权数量
		long bonusNumber = outdatemney/platfrommoney;
		
		if(platfrommoney<=outdatemney)
		{
			BonusJobclassImpl.getInstance().CutMemberDividendRight_Business(Memberid, bonusNumber,Accumulatedamount,AccountTypeName, db);
		}
		
		return result;
	}
	
	
	/**
	 * 删减企业分红权
	 * @param Memberid		企业id
	 * @param bonusNumber	分红权数
	 * @param money		企业过期累计金额
	 * @param db
	 * @param conn
	 * @return
	 * @throws JDBCException 
	 */
	public boolean CutMemberDividendRight_Business(String Memberid,long bonusNumber,long money,String AccountTypeName,DB db)
			throws JDBCException
	{
		boolean bool=false;
		//账号类型
		String AccountType = returnAccountTypeId(AccountTypeName,db);
		
		String SQL="UPDATE mall_account_info SET enabled_dividend_sharing = enabled_dividend_sharing-?"
				+ ",disable_dividend_sharings=disable_dividend_sharings+?,expired_bouns_total_amount=? WHERE member_orgid_id=? AND a_class_id=?";
		
		int result = db.execute(SQL, new String[]{bonusNumber+"",bonusNumber+"",money+"",Memberid,AccountType}, new int[]{Type.INTEGER,Type.INTEGER,Type.INTEGER,Type.VARCHAR,Type.VARCHAR});
		
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
	 * @param money		用户过期累计金额
	 * @param db
	 * @param conn
	 * @return
	 * @throws JDBCException 
	 */
	public boolean CutMemberDividendRight(String Memberid,long bonusNumber,long money,DB db)
			throws JDBCException
	{
		boolean bool=false;
		
		String AccountTypeName = VariableAcquisition.getInstance().returnstr("BONUS_ACCOUNTCLASS", db);
		//账号类型
		String AccountType = returnAccountTypeId(AccountTypeName,db);
		
		String SQL="UPDATE mall_account_info SET enabled_dividend_sharing = enabled_dividend_sharing-?"
				+ ",disable_dividend_sharings=disable_dividend_sharings+?,expired_bouns_total_amount=? WHERE member_orgid_id=? AND a_class_id=?";
		
		int result = db.execute(SQL, new String[]{bonusNumber+"",bonusNumber+"",money+"",Memberid,AccountType}, new int[]{Type.INTEGER,Type.INTEGER,Type.INTEGER,Type.VARCHAR,Type.VARCHAR});
		
		if(result>0)
		{
			bool=true;
		}
		
		return bool;
	}
	
	/**
	 * 查询用户的过期累计金额
	 * @param Memberid	会员id
	 * @param db
	 * @return
	 * @throws JDBCException 
	 */
	public long querymember(String Memberid,DB db) throws JDBCException
	{
		String AccountTypeName = VariableAcquisition.getInstance().returnstr("BONUS_ACCOUNTCLASS", db);
		//账号类型
		String AccountType = returnAccountTypeId(AccountTypeName,db);
		//用户累计过期金额
		long totalmoney = 0;
		
		String SQL="SELECT expired_bouns_total_amount AS totalmomey FROM mall_account_info"
				+ " WHERE member_orgid_id=? AND a_class_id=?";
		
		MapList maplist = db.query(SQL,new String[]{Memberid,AccountType},new int[]{Type.VARCHAR,Type.VARCHAR});
		if(maplist.size()>0)
		{
			totalmoney = maplist.getRow(0).getLong("totalmomey", 0);
		}
		
		return totalmoney;
	}
	
	/**
	 * 查询企业的过期累计金额
	 * @param Memberid	会员id
	 * @param db
	 * @return
	 * @throws JDBCException 
	 */
	public long querymember_Business(String Memberid,String AccountTypeName,DB db) throws JDBCException
	{
		//账号类型
		String AccountType = returnAccountTypeId(AccountTypeName,db);
		//用户累计过期金额
		long totalmoney = 0;
		
		String SQL="SELECT expired_bouns_total_amount AS totalmomey FROM mall_account_info"
				+ " WHERE member_orgid_id=? AND a_class_id=?";
		
		MapList maplist = db.query(SQL,new String[]{Memberid,AccountType},new int[]{Type.VARCHAR,Type.VARCHAR});
		if(maplist.size()>0)
		{
			totalmoney = maplist.getRow(0).getLong("totalmomey", 0);
		}
		
		return totalmoney;
	}
	
	
	/**
	 * 给用户插入交易记录
	 * @param Memberid
	 * @trdl	用户交易插入
	 * @bdb		用户交易bean类
	 * @param db
	 * @return
	 * @throws JDBCException 
	 */
	public void transactionrecord(String Memberid,TransactionDetail trdl,AfterDetailBean bdb,long money,DB db) throws JDBCException
	{
		
		String AccountTypeName = VariableAcquisition.getInstance().returnstr("BONUS_ACCOUNTCLASS", db);
		//账号类型
		String AccountType = returnAccountTypeId(AccountTypeName,db);
		//账号id
		String Accountid = BonusJobclassImpl.getInstance().returnAccountinfoId(AccountType, Memberid, db);
		
		bdb.setTableRow(bdb.getTranTable().addInsertRow());
		
		if(Checker.isEmpty(Accountid))
		{
			logger.error("会员id为:"+Memberid+"  该账户未查询到经营分红账户！");
		}else
		{
			try {
				bdb.setId(UUID.randomUUID().toString());
				bdb.setAccount_id(Accountid);
				bdb.setMember_id(Memberid);
				bdb.setRmarks("经营分红");
				bdb.setTrade_total_money(money);
				bdb.setCounter_fee(0);
				bdb.setSa_class_id(AccountType);
				
				trdl.afterActions(db, bdb);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 给企业插入交易记录
	 * @param Memberid
	 * @trdl	企业交易插入
	 * @bdb		企业交易bean类
	 * @param db
	 * @return
	 * @throws JDBCException 
	 */
	public void transactionrecord_business(String Memberid,TransactionDetail trdl,AfterDetailBean bdb,long money,String AccountTypeName,DB db) throws JDBCException
	{
		//账号类型
		String AccountType = returnAccountTypeId(AccountTypeName,db);
		//账号id
		String Accountid = BonusJobclassImpl.getInstance().returnAccountinfoId(AccountType, Memberid, db);
		
		bdb.setTableRow(bdb.getTranTable().addInsertRow());
		
		if(Checker.isEmpty(Accountid))
		{
			logger.error("企业id为:"+Memberid+"  该账户未查询到经营分红账户！");
		}else
		{
			try {
				bdb.setId(UUID.randomUUID().toString());
				bdb.setAccount_id(Accountid);
				bdb.setMember_id(Memberid);
				bdb.setRmarks("经营分红");
				bdb.setTrade_total_money(money);
				bdb.setCounter_fee(0);
				bdb.setSa_class_id(AccountType);
				
				trdl.afterActions(db, bdb);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 执行用户分红金额
	 * @param Memeberid		会员id
	 * @param db
	 * @throws JDBCException
	 */
	public long Platformbonus(String Memeberid,DB db,VirementManager vir)throws JDBCException
	{
		
		String AccountTypeName = VariableAcquisition.getInstance().returnstr("BONUS_ACCOUNTCLASS", db);
		//账号类型
		String AccountType = returnAccountTypeId(AccountTypeName,db);
		//获取平台分红比例
		double  dividendpayoutratio = Double.parseDouble(VariableAcquisition.getInstance().returnstr("Platformdividendratio", db));
		//消费总金额
		double consumptionMoney = BonusJobclassImpl.getInstance().QuerySalesvolume(db);
		//分红总金额
		long Amountofdividends = new Double(consumptionMoney*dividendpayoutratio).longValue();
		//总分红权数
		int Totalredweight=BonusJobclassImpl.getInstance().SumDividendRight(db);
		//每个分红权分红金额
		double averagemoney = BonusJobclassImpl.getInstance().CalculationAveragedividend(Amountofdividends, Totalredweight);
		//用户所获得分红金额
		long money = BonusJobclassImpl.getInstance().calculationbonus(Memeberid, averagemoney+"", db,vir);
		//备注
		String iremakers = "";
		
		if(money>0)
		{
			//更新用户分红权过期累计金额
			
			String Update_expired_bouns_total_amount_SQL = "UPDATE mall_account_info SET expired_bouns_total_amount=expired_bouns_total_amount+? WHERE a_class_id=? AND member_orgid_id=?";
			
			db.execute(Update_expired_bouns_total_amount_SQL,new String[]{(money)+"",AccountType,Memeberid},new int[]{Type.INTEGER,Type.VARCHAR,Type.VARCHAR});
			
			//金额
	//		String Share_bonus_money = String.valueOf((Double.valueOf(money)));//"200"; //
			
	//		String SQL="UPDATE mall_account_info SET"
	//				+ " balance=balance+?"
	//				+ " ,total_bonus_amount=total_bonus_amount+?"
	//				+ " ,total_amount=total_amount+?"
	//				+ " ,expired_bouns_total_amount=expired_bouns_total_amount+?"
	//				+ " WHERE a_class_id=? AND member_orgid_id=?";
	//		
	//		db.execute(SQL,new String[]{money+"",money+"",money+"",money+"",AccountType,Memeberid}
	//		, new int[]{Type.INTEGER,Type.INTEGER,Type.INTEGER,Type.INTEGER,Type.VARCHAR,Type.VARCHAR});
			
			//String id = BonusJobclassImpl.getInstance().Query(db).getRow(0).get("id");
			
			if(!Checker.isEmpty(Memeberid)){
				vir.execute(db, "", Memeberid, "", AccountTypeName,(money*0.01)+"", iremakers, "", "", false);
				String id=UUID.randomUUID().toString();
				//向分红详情表中添加数据
//				String insertSql = " insert into lxny_Share_bonus_details (id,member_id,Share_bonus_money,create_datetime) "
//						+ "values ('" + id + "','"+Memeberid+"','"+money*0.01+"',now())  ";
//				db.execute(insertSql);
			};
		}
		
		return money;
		
	}
	
	
	
	/**
	 * 2016-12-06 鲜琳
	 * 执行企业分红金额
	 * @param Memeberid		会员id
	 * @param db
	 * @throws JDBCException
	 */
	public long Platformbonus_Business(String business_Id,String AccountTypeName,DB db)throws JDBCException
	{
		//账号类型
		String AccountType = returnAccountTypeId(AccountTypeName,db);
		//获取平台分红比例
		double  dividendpayoutratio = Double.parseDouble(VariableAcquisition.getInstance().returnstr("Platformdividendratio", db));
		//消费总金额
		double consumptionMoney = BonusJobclassImpl.getInstance().QuerySalesvolume(db)/100;
		//分红总金额
		long Amountofdividends = new Double(consumptionMoney*dividendpayoutratio).longValue();
		//总分红权数
		int Totalredweight=BonusJobclassImpl.getInstance().SumDividendRight(db);
		//每个分红权分红金额
		double averagemoney = BonusJobclassImpl.getInstance().CalculationAveragedividend(Amountofdividends, Totalredweight);
		//用户所获得分红金额
		long money = BonusJobclassImpl.getInstance().calculationbonus_business(business_Id, averagemoney+"",AccountTypeName, db);
		
		String SQL="UPDATE mall_account_info SET"
				+ " balance=balance+?"
				+ " ,total_bonus_amount=total_bonus_amount+?"
				+ " ,total_amount=total_amount+?"
				+ " ,expired_bouns_total_amount=expired_bouns_total_amount+?"
				+ " WHERE a_class_id=? AND member_orgid_id=?";
		
		db.execute(SQL,new String[]{money+"",money+"",money+"",money+"",AccountType,business_Id}
		, new int[]{Type.INTEGER,Type.INTEGER,Type.INTEGER,Type.INTEGER,Type.VARCHAR,Type.VARCHAR});
		
		return money;
	}
	
	
	
	
	/**
	 * 计算用户分红金额
	 * @param Memeberid		会员id
	 * @param bonusmoney	每个分红权金额
	 * @param db
	 * @throws JDBCException 
	 */
	public long calculationbonus(String Memeberid,String bonusmoney,DB db,VirementManager vr) throws JDBCException
	{
		
		String AccountTypeName = VariableAcquisition.getInstance().returnstr("BONUS_ACCOUNTCLASS", db);
		
		String AccountType = returnAccountTypeId(AccountTypeName,db);
		//用户分红比例
		double badge=querybadge(Memeberid,db);
		//用户当日分红金额
		double Amountofdividends = (Double.parseDouble(bonusmoney)*badge)*BonusJobclassImpl.getInstance().SumDividendRightByMemberid(Memeberid, db);
		
//		long longAmountofdividends = new Double(Amountofdividends).longValue();
		
		long longAmountofdividends = format(Amountofdividends,vr);
		
		return longAmountofdividends;
	}
	
	//保留两位小数且不做四舍五入
	public long format(double finalMoney,VirementManager vr)
	{
		DecimalFormat formater = new DecimalFormat();
        formater.setMaximumFractionDigits(2);
        formater.setGroupingSize(0);
        formater.setRoundingMode(RoundingMode.FLOOR);
        
        String s = formater.format(finalMoney);
        
        return VirementManager.changeY2F(s);
	}
	
	
	/**
	 * 2016-12-06 xianlin
	 * 计算企业分红金额
	 * @param Memeberid		企业id
	 * @param bonusmoney	每个分红权金额
	 * @param db
	 * @throws JDBCException 
	 */
	public long calculationbonus_business(String Memeberid,String bonusmoney,String AccountTypeName,DB db) throws JDBCException
	{
		String AccountType = returnAccountTypeId(AccountTypeName,db);
		//用户当日分红金额
		double Amountofdividends = (Double.parseDouble(bonusmoney))*BonusJobclassImpl.getInstance().SumDividendRightByMemberid_business(Memeberid,AccountTypeName, db);
		
		long longAmountofdividends = new Double(Amountofdividends).longValue();
		
		return longAmountofdividends;
	}
	
	
	
	/**
	 * 获取用户分红比例
	 * @param Memberid		会员id
	 * @param db
	 * @return
	 */
	public double querybadge(String Memberid,DB db)
	{
		double resout=0;
		
		AMBadgeManager badgeManger=new AMBadgeManager();
		resout=badgeManger.getBounsRatio(db, Memberid);
		
		return resout;
	}
	
	/**
	 * 查询当天销售信息
	 * @param db
	 * @return
	 * @throws JDBCException 
	 */
	public MapList Query(DB db) throws JDBCException
	{
		String thisdate = BonusJobclassImpl.getInstance().timeconversion(db);
		
		String SQL="SELECT sum(total_amount_money) AS total_amount_money FROM mall_consumer_total_record"
				+ " WHERE create_time > '"+thisdate+"' :: TIMESTAMP - INTERVAL '1 day'"
				+ " AND create_time <= '"+thisdate+"' AND ";
		
		MapList maplist = db.query(SQL);
//		db.query(SQL, new String[]{thisdate,thisdate},new int[]{Type.VARCHAR,Type.VARCHAR})
		return maplist;
	}
	/**
	 * 更新平台当日消费额记录表
	 * @param id
	 * @param db
	 * @throws JDBCException 
	 */
	public void UpdatePlatformConsumerInformation(String id,String totaldividend_number
			,String totaldividend_money,DB db) throws JDBCException
	{
		String SQL="UPDATE mall_consumer_total_record SET total_dividend_number=total_dividend_number+?,single_dividend_money=single_dividend_money+?"
				+ " WHERE id=?";
		
		db.execute(SQL,new String[]{totaldividend_number,totaldividend_money,id}, new int[]{Type.INTEGER,Type.INTEGER,Type.VARCHAR});
	}
	
	/**
	 * 查询当天平台消费信息id
	 * @param db
	 * @return
	 * @throws JDBCException
	 */
	public String PlatformConsumptionId(DB db) throws JDBCException
	{
		String id = "";
		
		MapList maplist = BonusJobclassImpl.getInstance().Query(db);
		
		if(maplist.size()>0)
		{
			id = maplist.getRow(0).get("id");
		}
		return id;
	}
	
	/**
	 * 查询当天平台消费金额
	 * @param db
	 * @return
	 * @throws JDBCException
	 */
	public long QuerySalesvolume(DB db) throws JDBCException
	{
		long totalmoney = 0;
		
		String thisdate = BonusJobclassImpl.getInstance().timeconversion(db);
		
		String SQL = "SELECT trim(to_char(SUM(totalprice),'99999999999999999999999')) AS totalprice FROM mall_memberorder WHERE completedate > '"+thisdate+"' :: TIMESTAMP - INTERVAL '1 day'"
				+ " AND completedate <= '"+thisdate+"' AND (orderstate = '7' OR orderstate = '8' OR orderstate='9' OR orderstate='10' OR orderstate = '81')";
		
		MapList maplsit = db.query(SQL);
		
		return maplsit.getRow(0).getLong("totalprice", 0);
	}
	
	/**
	 * 转换时间
	 * @return
	 * @throws JDBCException 
	 */
	public String timeconversion(DB db) throws JDBCException
	{
		//获取计算时间
		String avarvalue = VariableAcquisition.getInstance().returnstr("salescalculationtime", db);
		Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);//获取年份
        int month=calendar.get(Calendar.MONTH);//获取月份
        int day=calendar.get(Calendar.DATE);//获取日
        int hour=Integer.parseInt(avarvalue);//小时
		
	    calendar.set(year, month, day, hour,0,0);  
		String smfdate =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
		
		System.out.println(smfdate);
		
		return smfdate;
	}
	
	
	/**
	 * 计算平均分红金额
	 * @param Amountofdividends	 分红金额
	 * @param Dividendnumber  分红权总数量
	 * @return
	 */
	public double CalculationAveragedividend(long Amountofdividends,long Dividendnumber)
	{
		double avgmoney=0;
		if(Dividendnumber>0)
		{
			avgmoney =(double) Amountofdividends/Dividendnumber;
		}
		return avgmoney;
	}
	
	/**
	 * 计算用户分红权总数
	 * @param db
	 * @return
	 * @throws JDBCException 
	 */
	public int SumDividendRightByMemberid(String MemberId,DB db) throws JDBCException
	{
		int totaldividendnumber=0;
		
		String AccountTypeName = VariableAcquisition.getInstance().returnstr("BONUS_ACCOUNTCLASS", db);
		
		String AccountType = returnAccountTypeId(AccountTypeName,db);
		
		String SQL="SELECT SUM(enabled_dividend_sharing) AS totalnumber"
				+ " FROM mall_account_info WHERE a_class_id=? AND member_orgid_id=?";
	
		MapList maplist = db.query(SQL, new String[]{AccountType,MemberId},new int[]{Type.VARCHAR,Type.VARCHAR});
		
		if(maplist.size()>0)
		{
			totaldividendnumber = maplist.getRow(0).getInt("totalnumber", 0);
		}
		return totaldividendnumber;
	}
	
	
	/**
	 * 2016-12-06 xainlin
	 * 计算企业分红权总数
	 * @param db
	 * @return
	 * @throws JDBCException 
	 */
	public int SumDividendRightByMemberid_business(String MemberId,String AccountTypeName,DB db) throws JDBCException
	{
		int totaldividendnumber=0;
		
		String AccountType = returnAccountTypeId(AccountTypeName,db);
		
		String SQL="SELECT SUM(enabled_dividend_sharing) AS totalnumber"
				+ " FROM mall_account_info WHERE a_class_id=? AND member_orgid_id=?";
	
		MapList maplist = db.query(SQL, new String[]{AccountType,MemberId},new int[]{Type.VARCHAR,Type.VARCHAR});
		
		if(maplist.size()>0)
		{
			totaldividendnumber = Integer.parseInt(maplist.getRow(0).get("totalnumber"));
		}
		return totaldividendnumber;
	}
	
	
	/**
	 * 计算总分红权
	 * @param db
	 * @return
	 * @throws JDBCException 
	 */
	public int SumDividendRight(DB db) throws JDBCException
	{
		int totaldividendnumber=0;
		
		String AccountTypeName = VariableAcquisition.getInstance().returnstr("BONUS_ACCOUNTCLASS", db);
		
		String AccountType = returnAccountTypeId(AccountTypeName,db);
		
		String SQL="SELECT SUM(enabled_dividend_sharing) AS totalnumber"
				+ " FROM mall_account_info WHERE a_class_id=?";
	
		MapList maplist = db.query(SQL, new String[]{AccountType},new int[]{Type.VARCHAR});
		
		if(maplist.size()>0)
		{
			
			if(Checker.isEmpty(maplist.getRow(0).get("totalnumber")))
			{
				totaldividendnumber=0;
			}else
			{
				totaldividendnumber = Integer.parseInt(maplist.getRow(0).get("totalnumber"));	
			}
			
		}
		return totaldividendnumber+SumDividendRight_Bussiness("GROUP_BONUS_ACCOUNT",db);
	}
	
	/**
	 * 2016-12-06 鲜琳
	 * 计算企业总分红权
	 * @param db
	 * @return
	 * @throws JDBCException 
	 */
	public int SumDividendRight_Bussiness(String AccountTypeName,DB db) throws JDBCException
	{
		int totaldividendnumber=0;
		
		String AccountType = returnAccountTypeId(AccountTypeName,db);
		
		String SQL="SELECT SUM(enabled_dividend_sharing) AS totalnumber"
				+ " FROM mall_account_info WHERE a_class_id=?";
	
		MapList maplist = db.query(SQL, new String[]{AccountType},new int[]{Type.VARCHAR});
		
		if(maplist.size()>0)
		{
			
			if(Checker.isEmpty(maplist.getRow(0).get("totalnumber")))
			{
				totaldividendnumber=0;
			}else
			{
				totaldividendnumber = Integer.parseInt(maplist.getRow(0).get("totalnumber"));	
			}
			
		}
		return totaldividendnumber;
	}
	
	
	/**
	 * 查询会员系统账号信息id
	 * @param code	账号编号
	 * @return 
	 * @throws JDBCException 
	 */
	public String returnAccountinfoId(String typeid,String Memberid,DB db) throws JDBCException
	{
		//id
		String id = "";
		
		String SQL="SELECT id FROM mall_account_info WHERE a_class_id=? AND member_orgid_id=?";
		
		MapList maplist= db.query(SQL,new String[]{typeid,Memberid}, new int[]{Type.VARCHAR,Type.VARCHAR});
		
		if(!Checker.isEmpty(maplist))
		{
			id = maplist.getRow(0).get("id");
		}
		return id;
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
}
