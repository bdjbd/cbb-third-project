package com.am.app_plugins.cooperative.start_business;

import java.util.UUID;

import org.json.JSONObject;

import com.am.app_plugins_common.specRechange.SpecRechangeService;
import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.callback.AbstraceBusinessCallBack;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;

/**
 * 购买终端支付成功回调
 * 
 * @author yuebin
 *1，下单
 *2，修改订单状态为已下单
 *
 */
public class BuyTerminalCallBack extends AbstraceBusinessCallBack {

	@Override
	public String callBackExec(String id, String business, DB db, String type) throws Exception {
		
		JSONObject reuslt=new JSONObject();
		
		if(checkTradeState(id, business, db, type)&&!checkProcessBuissnes(id, db)){
			//检查业务是否处理，如果没有处理，处理业务
			JSONObject businessJS=new JSONObject(business);
//			Table table=new Table("am_bdp", "AM_TERMINAL_ORDER_MANAGER");
//			TableRow insertTr=table.addInsertRow();
//			
//			insertTr.setValue("org_id", businessJS.getString("orgid"));
//			insertTr.setValue("buyer", businessJS.getString("memberid"));
//			insertTr.setValue("order_status","2");//1：已下单;2：已付款;3：已发货;4：确认收货
//			insertTr.setValue("pay_id", id);
//			insertTr.setValue("recv_name", businessJS.getString("recv_name") );
//			insertTr.setValue("recv_phone",  businessJS.getString("recv_phone"));
//			insertTr.setValue("recv_address",  businessJS.getString("recv_address"));
//			
//			db.save(table);
			
			String director=businessJS.getString("director");
			String secondDirector=businessJS.getString("second_director");
			
			String orgId=businessJS.getString("orgid");
			
			String inserTid=UUID.randomUUID().toString();
			
			String inserSQL="INSERT INTO am_terminal_order_manager "
					+ " (id,org_id,buyer,order_status,"
					+ " pay_id,recv_name,recv_phone,"
					+ " recv_address,create_time) "
					+ " VALUES "
					+ " ('"+inserTid+"','"+orgId+"','"+businessJS.getString("memberid")+"','2',"
					+ "  '"+id+"','"+businessJS.getString("recv_name")+"','"+businessJS.getString("recv_phone")+"'"
					+ ",'"+businessJS.getString("recv_address")+"' ,now())";
			
			db.execute(inserSQL);
			
			//将购买终端的费用转到运维管理部
			String inOrgId=Var.get("operation_rog_orgid");//运营管理机构机构编号
			
			VirementManager vm=new VirementManager();
			
//			orgId=Var.get("operation_rog_orgid");//运营管理机构机构编号
			
			String paymoney=businessJS.getString("paymoney");
			String iremakers=orgId+"终端，价格为"+paymoney+"元。";
			vm.execute(db, 
					"",
					inOrgId,
					"",
					SystemAccountClass.GROUP_CASH_ACCOUNT,
					paymoney,
					iremakers,
					"",
					"",
					false);
			
			
			//更新控制使用费为1年
			String spaceYears=Var.get("space_years");
			
			//更新空间使用费到期时间
//			String updateSQL="UPDATE lxny_space_usage_fee SET due_time=now()+interval '"+spaceYears +
//						" year ',status='1'  WHERE id=( SELECT id FROM lxny_space_usage_fee WHERE org_code='"+orgId+"' "
//								+ " ORDER BY due_time DESC LIMIT 1 )";
			SpecRechangeService srs=new SpecRechangeService();
			String rechangeId=srs.addBuySpecRecord(db, "1",paymoney, "1", orgId);
			srs.updateDueTimeToConfirm(db,rechangeId);
			
//			db.execute(updateSQL);
			
			
			//增加审核流程主任和副主任关系
			addDirector(db,director,secondDirector,orgId);
			
			
			updateProcessBuissnes(id, db, "1");
		}
		
		return reuslt.toString();
	}

	
	/**
	 * 增加审核流程主任和副主任关系
	 * @param db
	 * @param director 主任
	 * @param secondDirector 副主任
	 * @param orgId 机构ID
	 * @throws JDBCException 
	 */
	private void addDirector(DB db, String director, String secondDirector, String orgId) throws JDBCException {
		
		String id=UUID.randomUUID().toString();
		
		//修改审核角色为副主任
		String sql = " UPDATE am_member SET audit_role =1 WHERE loginaccount = '"+secondDirector+"' ";
		db.execute(sql);
//				//修改审核角色为主任
		sql = " UPDATE am_member SET audit_role =2 WHERE loginaccount = '"+director+"' ";
		db.execute(sql);
		
		
		String insertSQL="INSERT INTO mall_auditpersonsetting( "+
             "  id, orgid, second_director, director) "+
             "  VALUES (?, ?, ?, ?)";
		
		
		
		
		db.execute(insertSQL,new String[]{
				id,orgId,secondDirector,director
		}, new int[]{Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,Type.VARCHAR});
	}

}
