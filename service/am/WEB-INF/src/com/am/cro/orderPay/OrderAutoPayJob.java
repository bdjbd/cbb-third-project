package com.am.cro.orderPay;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.pay.PayManager;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * 订单自动付款任务(只针对个人会员，不涉及汽修厂)
 * @author 张少飞    2017/7/17
 * 订单自动付款任务
 *  1.订单状态=已完成，7天后会员未付款，则自动从其现金账户扣款
 *  2.计划任务执行时间为每天凌晨 2:00
 */
public class OrderAutoPayJob implements Job {
	//1.查询“订单状态=已完成”且“超出7天”且“支付状态=未支付”的订单列表，包括订单ID、会员ID、订单费用【维修结束时间超出7天   now()-EndRepairTime>=7】
	
	//select (to_date(to_char(now(),'YYYY-MM-DD'),'YYYY-MM-DD'))-(to_date(to_char(EndRepairTime,'YYYY-MM-DD'),'YYYY-MM-DD')) as daynum, id,MemberID,TotalMoney,EndRepairTime 
	//from cro_CarRepairOrder 
	//where OrderState='3' and (to_date(to_char(now(),'YYYY-MM-DD'),'YYYY-MM-DD'))-(to_date(to_char(EndRepairTime,'YYYY-MM-DD'),'YYYY-MM-DD')) >= 7 and PayState='0';
	
	//2.for()循环列表，得到每个超期订单的会员ID，以及他们的现金账号，一单一单进行扣款，并进行记录
	//3.检查会员现金账户余额
	//4.从该会员现金账户扣款
	//5.更新记录
	private Logger logger=LoggerFactory.getLogger(getClass());
	@Override
	public void execute(JobExecutionContext job) throws JobExecutionException {
		logger.info("开始执行计划任务-----订单自动付款！");
		
		//1.查询“订单状态=已完成”且“超出7天”且“支付状态=未支付”的订单列表，包括订单ID、会员ID、订单费用【维修结束时间超出7天   now()-EndRepairTime>=7】
		DB db;
		try {
			db = DBFactory.newDB();
		
		String sql = " select (to_date(to_char(now(),'YYYY-MM-DD'),'YYYY-MM-DD'))-(to_date(to_char(EndRepairTime,'YYYY-MM-DD'),'YYYY-MM-DD')) as daynum, "
				+ " id,MemberID,TotalMoney,TotalMoney*100 as TotalMoneyCent,EndRepairTime "
				+ " from cro_CarRepairOrder "
				+ " where OrderState='3' "
				+ " and (to_date(to_char(now(),'YYYY-MM-DD'),'YYYY-MM-DD'))-(to_date(to_char(EndRepairTime,'YYYY-MM-DD'),'YYYY-MM-DD')) >= 7 "
				+ " and PayState='0' ";	
		System.err.println("查询sql》》》"+sql);
		MapList list = db.query(sql);
		//2.for()循环列表，得到每个超期订单的会员ID，以及他们的现金账号，一单一单进行扣款，并进行记录
		if(!Checker.isEmpty(list)){
			for (int i = 0; i < list.size(); i++) 
			{
				//订单ID
				String id = list.getRow(i).get("id");
				//完成天数=当前时间-维修结束时间
				String daynum = list.getRow(i).get("daynum");
				System.err.println("获得完成天数daynum>>>"+daynum);
				//订单ID
				String orderId = list.getRow(i).get("id");
				System.err.println("获得订单orderId>>>"+orderId);
				//会员ID
				String MemberID = list.getRow(i).get("memberid");
				//订单总金额(字符串格式 单位：元)
				String TotalMoney = list.getRow(i).get("totalmoney");
				System.err.println("获得订单总金额(单位：元)TotalMoney>>>"+TotalMoney);
				//string型的 总价  单位：分  (带有多余的小数点，需要转换一下)
				String temp = list.getRow(i).get("totalmoneycent");
				System.err.println("获得订单总金额(单位：分)temp>>>"+temp);
				temp = temp.substring(0,temp.indexOf("."));  //总价  分  去掉多余的小数点
				//订单总金额(单位：分)
				Long TotalMoneyCent = Long.parseLong(temp);  //总价  分  string转long型
				System.err.println("获得订单总金额(单位：分)TotalMoneyCent>>>"+TotalMoneyCent);
				//订单总金额(单位：元)
				//Double TotalMoney = Double.parseDouble(list.getRow(i).get("totalmoney"));
				
				//查询该会员的现金账号余额balance(单位：分)
				String balanceSQL = " select m.id as account_id,c.id as sa_class_id, m.balance as balance from mall_account_info as m,mall_system_account_class as c "
						+ " where m.a_class_id=c.id "
						+ " and c.sa_code='CASH_ACCOUNT' "
						+ " and m.member_orgid_id ='"+MemberID+"' ";
				System.err.println("查询该会员的现金账号余额>>>"+balanceSQL);
				MapList balanceList = db.query(balanceSQL);
				//现金账户ID
				String account_id = "";
				//系统账号分类ID
				String sa_class_id = "";
				//现金账户余额(单位：分)
				Long balance = 0L;
				if(!Checker.isEmpty(balanceList)){
					 balance = Long.parseLong(balanceList.getRow(0).get("balance")); 
					 account_id = balanceList.getRow(0).get("account_id");
					 sa_class_id = balanceList.getRow(0).get("sa_class_id");
					 //若账户余额足够支付则扣款
					if(balance-TotalMoneyCent>0){
						/**
						 * 系统账户 现金支付 操作
						 * @param MemberID 用户id
						 * @param accountCode 支付账户id
						 * @param TotalMoney 支付金额 单位元
						 * @param pay_id 支付单号
						 * @param busines 业务参数  此时业务参数为空
						 * @param outRemakes 支付描述
						 * @return 
						 */
						//支付单号 UUID
						String pay_id = UUID.randomUUID().toString();
						System.err.println("最重要的支付ID、交易记录ID》》》"+pay_id);
						//支付描述
						String outRemakes="订单自动付款任务";
						//业务参数
						String business = "";
						//支付金额 单位元
						PayManager payManager = new PayManager();
						JSONObject resultJson = payManager.excunte(MemberID,SystemAccountClass.CASH_ACCOUNT, TotalMoney, pay_id,business.toString(), outRemakes);
						//转账支付成功，则扣除用户现金账户余额(单位：分)
						try {
							if("0".equals(resultJson.getString("code"))){
								//更新用户现金账户余额(单位：分)  TotalMoneyCent(支付金额 单位:分)
								//String updateSQL = " update mall_account_info set balance=balance-"+TotalMoneyCent+" where id='"+account_id+"' ";
								//db.execute(updateSQL);
								
								//修改订单的支付状态 '0'=未支付;'1'=已支付
								String payStateSQL = " update cro_CarRepairOrder set PayState='1' where id='"+id+"'";
								System.err.println("修改订单状态sql>>>"+payStateSQL);
								db.execute(payStateSQL);
								
								TotalMoney = TotalMoney.substring(0,TotalMoney.indexOf("."));  //总价  元  去掉多余的小数点
								//会员新增积分 String型转成integer型
								Integer TotalMoneyScore = Integer.parseInt(TotalMoney);
								//更新会员表的积分score    每消费1元=1积分 (score为整数型)
								String changeSql = " update am_member set score=score+"+TotalMoneyScore+" where id='"+MemberID+"' ";
								System.err.println("更新会员表的积分score>>>"+changeSql);
								db.execute(changeSql);
								//更新会员积分记录表
								String insertSql = " insert into am_MemberIntegralRecord(id,MemberID,IntegralNumber,IntegralExplain,CreateDate)"
										+ " values('"+UUID.randomUUID().toString()+"','"+MemberID+"',"+TotalMoney+",'订单自动付款任务','now()') ";
								System.err.println("更新会员积分记录表>>>"+insertSql);
								db.execute(insertSql);
								
								//更新交易详情表中的业务状态 业务是否处理(整数型)  0=未处理  ; 1=已处理
								String businessStateSQL = " update mall_trade_detail set is_process_buissnes=1 where id='"+pay_id+"' ";
								db.execute(businessStateSQL);
								//更新账户交易记录表    
								//String insertSQL = " insert into mall_trade_detail(id,member_id,account_id,trade_time,trade_total_money,rmarks,create_time,sa_class_id,trade_type,trade_state) "
								//		+ " values('"+pay_id+"','"+MemberID+"','"+account_id+"','now()',"+TotalMoneyCent+",'订单自动付款任务','now()','"+sa_class_id+"',1,'1') ";
								//db.execute(insertSQL);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};
				}
				
			}
		}
		//for循环结束后，关闭数据库连接
		db.close();
		} catch (JDBCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
  }


