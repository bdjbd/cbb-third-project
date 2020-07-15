package com.ambdp.agricultureProject.action;

import java.util.UUID;


import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月20日
 *@version
 *说明：项目分红管理确认分红Action
 */
public class ProjectBounsInfoBonusAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// 项目ID
		String projectId = ac.getRequestParameter("mall_project_bouns_info.form.project_id");
		// 每股分红金额（元）
		String price = ac.getRequestParameter("mall_project_bouns_info.form.price");
		// 项目分红管理
		Table table=ac.getTable("mall_project_bouns_info");

		db.save(table);
		// id
		String id = table.getRows().get(0).getValue("id");
		if (!Checker.isEmpty(price)) {
			//查询购买此项目股份的所有用户
			String selectBuyStockSql = "SELECT id,buyer_id,buy_number,buyer_type FROM mall_buy_stock_record WHERE project_id='"+projectId+"' AND status=2 ";
			MapList buyStockMap =db.query(selectBuyStockSql);
			
			if (buyStockMap!=null&&buyStockMap.size()>0) {
				for (int i = 0; i < buyStockMap.size(); i++) {
					//购买用户ID
					String buyerId =buyStockMap.getRow(i).get("buyer_id");
					//购买分数
					String buyNumber =buyStockMap.getRow(i).get("buy_number");
					String buyerType = buyStockMap.getRow(i).get("buyer_type");
					
					//给项目分红记录表插分红记录
					projectBounsRecord(db,id,buyerId,buyerType,price,buyNumber);
					//判断是会员还是机构1会员 2：机构
					if("1".equals(buyerType)){
						//给每个会员分红
						accountBonus(db,buyerId,buyNumber,price);
					}else{
						//给每个机构分红
						accountOrgBonus(db,buyerId,buyNumber,price);
					}
					
				}
			}
			
			
			// 每股分红金额(元)
			String moneyValue = ac.getRequestParameter("mall_project_bouns_info.form.price");
			// 实际分红金额(元)
			String actualBouns = ac.getRequestParameter("mall_project_bouns_info.form.actual_bouns");
			if (!Checker.isEmpty(id)) {
				//将状态改为分红
				String updateSql = "UPDATE mall_project_bouns_info  SET stock_bouns_price =" + moneyValue+"*100,actual_total_bouns_amount="+actualBouns+"*100,status =2  WHERE id='" + id + "' ";
				db.execute(updateSql);
			}
		}else{
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("每股分红金额不能为空");
			return;
		}
		ac.setSessionAttribute("mall_project_bouns_info.form.id", id);
	}
	/**
	 * 给项目分红记录表插分红记录
	 * @param db
	 * @param id  分红信息id
	 * @param buyerId  社员/机构id
	 * @param buyerType  购买者身份类型
	 * @param price  每股分红金额（元）
	 * @param buyNumber  购买数量
	 * @throws JDBCException 
	 */
	private void projectBounsRecord(DB db, String id, String buyerId,
			String buyerType, String price, String buyNumber) throws JDBCException {

		String uuid = UUID.randomUUID().toString();
		if(!Checker.isEmpty(id)){
			String sqlinsert = "INSERT INTO mall_project_bouns_record( id, mpb_record_id, buyser_id, buyser_type,bouns_amount,holding_stock,create_time) "
					+ "values ('"+uuid+"','"+id+ "','"+buyerId+ "','"+buyerType+ "',"+price+"*100*"+buyNumber+",'"+buyNumber+"',now()) ";
			db.execute(sqlinsert);
		}
		
	}

	/**
	 * 给每个机构分红
	 * @param db
	 * @param buyerId 机构id
	 * @param buyNumber 购买数量
	 * @param price 每股分红金额（元）
	 * @throws JDBCException 
	 */
	private void accountOrgBonus(DB db, String buyerId, String buyNumber,
			String price) throws JDBCException {
//		
//		String updateAccountOrgSql = "UPDATE mall_account_info SET balance=balance+("+price+"*100*"+buyNumber+") WHERE member_orgid_id='"+buyerId+"' "
//				+ " AND a_class_id=(SELECT id FROM mall_system_account_class WHERE sa_code='GROUP_IDENTITY_STOCK_ACCOUNT') ";
//		
//		db.execute(updateAccountOrgSql);
//		
		VirementManager vir = new VirementManager();
		
		 vir.execute(db,"", buyerId, "",SystemAccountClass.IDENTITY_STOCK_ACCOUNT,(Double.parseDouble(price)*Integer.parseInt(buyNumber))+"","投资农业项目分红", "", "", false);
	
		
		
		
		
		
	}

	/**
	 * 给每个会员分红
	 * @param db
	 * @param buyerId 用户id
	 * @param buyNumber 购买数量
	 * @param price 每股分红金额（元）
	 * @throws JDBCException 
	 */
	
	private void accountBonus(DB db, String buyerId, String buyNumber, String price) throws JDBCException {
		
		
		
		VirementManager vir = new VirementManager();
		
		 vir.execute(db,"", buyerId, "",SystemAccountClass.GROUP_IDENTITY_STOCK_ACCOUNT,(Double.parseDouble(price)*Integer.parseInt(buyNumber))+"","投资农业项目分红", "", "", false);
		
//		try {
//			if(obj.has("code") && "0".equals(obj.get("code").toString()))
//			{
//				String updateAccountInfoSql = "UPDATE mall_account_info SET total_amount=total_amount+("+price+"*100*"+buyNumber+") WHERE member_orgid_id='"+buyerId+"' "
//						+ " AND a_class_id=(SELECT id FROM mall_system_account_class WHERE sa_code='IDENTITY_STOCK_ACCOUNT') ";
//				
//				db.execute(updateAccountInfoSql);
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
		
		
	}


}
