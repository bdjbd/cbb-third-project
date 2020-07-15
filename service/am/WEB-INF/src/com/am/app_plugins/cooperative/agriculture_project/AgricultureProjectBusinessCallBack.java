package com.am.app_plugins.cooperative.agriculture_project;

import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.app_plugins.rural_market.server.RuralMarketServer;
import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.task.instance.GetVolunteerAccountWithQualificationItask;
import com.am.frame.task.instance.MemberAuthorityBadgeTask;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.TaskEngine;
import com.am.frame.transactions.callback.IBusinessCallBack;
import com.am.frame.transactions.virement.VirementManager;
import com.ambdp.agricultureProject.server.BuyProjectStockServer;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * 投资三农 支付成功回调
 * 
 * @author yuebin
 */
public class AgricultureProjectBusinessCallBack implements IBusinessCallBack {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public String callBackExec(String id, String business, DB db, String type) throws Exception {
		JSONObject result = new JSONObject();

		BuyProjectStockServer service = new BuyProjectStockServer();
		RuralMarketServer marketServer = new RuralMarketServer();
		
		JSONObject jobBus=new JSONObject(business);
		
		Long balance = 0L;
		Long sumPrice = 0L;
		String stringSumprice = "";
		String code = SystemAccountClass.CASH_ACCOUNT;// "CASH_ACCOUNT";
		String iremakers = "创办农业项目";
		String stockCode = SystemAccountClass.IDENTITY_STOCK_ACCOUNT;
		//一级会员ID
		String firstmemberid =null;
		//二级会员ID
		String secondmemberid =null;
		//三级会员ID
		String thirdmemberid =null;
		//一级分红比率
		Float firstfhrate = null;
		//二级分红比率
		Float secondfhrate = null;
		//三级分红比率
		Float thirdfhrate = null;
		//会员分红金额
		String firstmoney=null,secondmoney=null,thirdmoney=null;

		if(jobBus.has("is_group")){
			//组织机构购买时，在business字段中包含此字段
			code = SystemAccountClass.GROUP_CASH_ACCOUNT;
			stockCode = SystemAccountClass.GROUP_IDENTITY_STOCK_ACCOUNT;
		}
		
		// 支付ID 根据支付id判断是否已经支付，如果已经支付则，不在进行支付处理
		String payId = id;

		String checkSQL = "SELECT id,user_submit_trade_money,is_process_buissnes "
				+ " FROM mall_trade_detail WHERE id=? ";

		MapList checkMap = db.query(checkSQL, payId, Type.VARCHAR);

		if (!Checker.isEmpty(checkMap)) {
			//// 业务是否处理0：未处理 ;1：处理
			String is_process_buissnes = checkMap.getRow(0).get("is_process_buissnes");
			if ("0".equals(is_process_buissnes)) {
				// 业务为处理，进行业务处理

				// 购买股数
					int number = Integer.parseInt(jobBus.getString("number"));
					// 购买项目ID
					String projectId = jobBus.getString("projectid");
					// 购买会员ID
					String memberid = jobBus.getString("memberid");
					// 单股价格 单位分
					String stockPrice =jobBus.getString("stockprice");
					//会员名称
					String membername = jobBus.getString("membername");
					//农业项目名称
					String pname = jobBus.getString("pname");
					//总金额 单位元
					Float totalmoney = Float.parseFloat(jobBus.getString("totalmoney"));
					//购买记录id
					String recodeId = jobBus.getString("recodeId");
					
					
					//查询上三级会员ID
					if(!Checker.isEmpty(membername)){
						
						String sanjiidSql = "SELECT amap.member_id,"+
								" cast(cdv.dividend_payout_ratio*"+totalmoney+" AS DECIMAL(10,2)) AS dv_money "+
								" FROM  am_member_distribution_map AS amap "+
								" LEFT JOIN consumer_dividend AS cdv ON cdv.level=4-amap.level "+
								" WHERE amap.SUB_MEMBER_ID='"+memberid+"' "+
								" AND amap.LEVEL<4 AND amap.INVITATION_STATUS = '1' ORDER BY amap.level";
						
						MapList sanjiId = db.query(sanjiidSql);
						
						if(!Checker.isEmpty(sanjiId)) {
//							if(sanjiId.size()==3){
//								firstmemberid = sanjiId.getRow(2).get("member_id");
//							}
//							secondmemberid = sanjiId.getRow(1).get("member_id");
//							thirdmemberid = sanjiId.getRow(0).get("member_id");
							for(int i=0;i<sanjiId.size();i++){
								Row row=sanjiId.getRow(i);
								if(i==0){
									firstmemberid =row.get("member_id");
									firstmoney=row.get("dv_money");
								}
								if(i==1){
									secondmemberid = row.get("member_id");
									secondmoney=row.get("dv_money");;
								}
								if(i==2){
									thirdmemberid = row.get("member_id");
									thirdmoney=row.get("dv_money");
								}
							}
							
						}
						
//						//查询分红比例
//						String fhrateSql = "SELECT * FROM CONSUMER_DIVIDEND ORDER BY LEVEL";
//						
//						MapList fhrate = db.query(fhrateSql);
//						
//						if(!Checker.isEmpty(fhrate)) {
//							
//							firstfhrate = Float.parseFloat(fhrate.getRow(0).get("dividend_payout_ratio"));
//							secondfhrate = Float.parseFloat(fhrate.getRow(1).get("dividend_payout_ratio"));
//							thirdfhrate = Float.parseFloat(fhrate.getRow(2).get("dividend_payout_ratio"));
//						}
//						
//						firstmoney = String.valueOf(totalmoney*firstfhrate/100);
//						secondmoney = String.valueOf(totalmoney*secondfhrate/100);
//						thirdmoney = String.valueOf(totalmoney*thirdfhrate/100);
						
						VirementManager vir = new VirementManager();
						iremakers = membername+"投资" + pname + "项目分红";
						
						//2017年1月4日19:23:31，谢超，将向上三级转账10%到现金账户改为，向志愿者服务账户
						
						if(!Checker.isEmpty(firstmemberid)&&!Checker.isEmpty(firstmemberid)){
							vir.execute(db, "", firstmemberid, "", SystemAccountClass.VOLUNTEER_ACCOUNT, firstmoney, iremakers, "", "", false);
						};
						
						if(!Checker.isEmpty(secondmemberid)&&!Checker.isEmpty(secondmemberid)){
							vir.execute(db, "", secondmemberid, "", SystemAccountClass.VOLUNTEER_ACCOUNT, secondmoney, iremakers, "", "", false);
						};
						
						if(!Checker.isEmpty(thirdmemberid)&&!Checker.isEmpty(thirdmemberid)){
							vir.execute(db, "", thirdmemberid, "", SystemAccountClass.VOLUNTEER_ACCOUNT, thirdmoney, iremakers, "", "", false);
						};
					}//查询上三级会员ID end
						
				// 查询现金
				String balanceSql = " SELECT balance,(" + stockPrice + "*" + number + ") AS sumprice,(" + stockPrice
						+ "*" + number + ")/100 AS stringSumprice " + " FROM mall_account_info WHERE member_orgid_id='"
						+ memberid + "' "
						+ "	AND a_class_id=(SELECT id FROM mall_system_account_class WHERE sa_code='"
						+ code+ "') ";
				
				MapList balanceMap = db.query(balanceSql);

				if (!Checker.isEmpty(balanceMap)) {

					balance = Long.parseLong(balanceMap.getRow(0).get("balance"));
					sumPrice = Long.parseLong(balanceMap.getRow(0).get("sumprice"));
					stringSumprice = balanceMap.getRow(0).get("stringsumprice");
				}
				

				if (balance < sumPrice||balance<=0) {
					//余额不住
					result.put("code", "999");
					result.put("msg", "现金帐号余额不足！");
				} else {
					

					String uuid = UUID.randomUUID().toString();
					// 给社员/机构项目投资购买股份记录表新增购买记录
//					insert(db, uuid, projectId, memberid, number, stockPrice);
					// 更新农业项目表中已经认购份数
//					service.updateProject(projectId, number, db);
					// 更新购买记录表中的记录状态 成功状态
					String uprosql = "update mall_buy_stock_record set status = '2' where id = '"+recodeId+"'";
					db.execute(uprosql);
					// 扣除购买用户的现金账号余额 增加至身份股金账号的累计金额
//					service.updateAccountInfo(code, stockCode, stockPrice, number, memberid, db);

					
					// 理性农业 会员权限徽章任务，具体权限参考需求 社员分类划分

					// 任务在WebAPI中触发的主要原因是后台购买无任务功能。 单位 分
					float insertMoeny = Float.parseFloat(stockPrice) * (number);

					if(!Checker.isEmpty(membername)){
					// 会员权限徽章任务 START
					TaskEngine taskEngine = TaskEngine.getInstance();
					RunTaskParams params = new RunTaskParams();
					params.setTaskCode(MemberAuthorityBadgeTask.TASK_ECODE); // 会员权限徽章任务

					params.pushParam(MemberAuthorityBadgeTask.TRIGGER_POINT,
							MemberAuthorityBadgeTask.INVST_PROJECT_MEONY);
					params.pushParam(MemberAuthorityBadgeTask.INVST_PROJECT_MEONY, insertMoeny/100);
					params.setMemberId(memberid);
					taskEngine.executTask(params);
					// 会员权限徽章任务 END

					// 获取志愿者账号提现资格任务 START
					String selesql = "select msac.sa_code from mall_trade_detail as mtd "
							+ "left join mall_account_info  as mai on mai.id = mtd.account_id "
							+ "left join mall_system_account_class as msac on msac.id = mai.a_class_id where mtd.id= '"+id+"'";
					MapList lists = db.query(selesql);
						//查询判断是否是志愿者账户支付，如果是志愿者账户支付则更新志愿者服务账户提现额度
//						if("VOLUNTEER_ACCOUNT".equals(lists.getRow(0).get("sa_code")))
//						{
							params = new RunTaskParams();
							params.setTaskCode(GetVolunteerAccountWithQualificationItask.TASK_ECODE);
							params.pushParam(GetVolunteerAccountWithQualificationItask.INTVISM_AGRICULT_PROJ_MONEY,
									insertMoeny+"");
		
							params.setMemberId(memberid);
							taskEngine.executTask(params);
//						}
						// 获取志愿者账号提现资格任务 END
					}
					
					result.put("code", "0");
					result.put("msg", "投资成功！");
				
				}

				// 更新交易记录为业务已处理状态
				String updatePay = "UPDATE mall_trade_detail SET is_process_buissnes=1 WHERE id=? ";
				db.execute(updatePay, payId, Type.VARCHAR);

			} else {
				logger.info("业务重复提交处理拒绝。pay_id：" + payId);

				result.put("code", "0");
				result.put("msg", "业务已经处理");
			}
		}

		return result.toString();
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
	private void insert(DB db, String uuid, String projectId, String memberid, int number, String stockPrice)
			throws JDBCException {
		if (!Checker.isEmpty(projectId)) {
			String sqlinsert = "INSERT INTO mall_buy_stock_record( id, project_id, buyer_id, buyer_type, buy_number, stock_price, create_time,status) "
					+ "values ('" + uuid + "','" + projectId + "','" + memberid + "','1','" + number + "','"
					+ stockPrice + "',now(),'2') ";
			db.execute(sqlinsert);
		}
	}
	
	

	
}
