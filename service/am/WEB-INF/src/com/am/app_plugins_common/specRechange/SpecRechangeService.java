package com.am.app_plugins_common.specRechange;

import java.util.UUID;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * 空间购买服务类
 * @author yuebin
 *
 */
public class SpecRechangeService {

	
	/**
	 * 新增一条空间购买记录
	 * @param db DB
	 * @param years  购买年限
	 * @param pay_money 购买金额
	 * @param status  交易状态  0=待支付,1=支付完成,2=支付失败
	 * @param orgid  购买机构id
	 * @return
	 * @throws JDBCException 
	 */
	public String addBuySpecRecord(DB db,String years,
			String pay_money,String status,
			String orgid) throws JDBCException{
		String id=UUID.randomUUID().toString();
		
		Table table=new Table("am_bdp", "LXNY_SPACE_USAGE_FEE");
		
		TableRow tr=table.addInsertRow();
		
		tr.setValue("buy_time_length", years);
		tr.setValue("transaction_amount", pay_money);
		tr.setValue("status",status);
		tr.setValue("org_code",orgid);
		
		db.save(table);
		
		id=tr.getValue("id");
		return id;
	}
	
	/***
	 * 更新rechangeId 对应的机构为支付成功，
	 * 支付成功会更新最后的到期时间
	 * @param db
	 * @param orgid 机构ID'
	 * @param rechangeId 购买空间记录ID
	 * @throws JDBCException 
	 */
	public void updateDueTimeToConfirm(DB db, String rechangeId) throws JDBCException {
		String querySQL="SELECT org_code,buy_time_length FROM lxny_space_usage_fee WHERE id=? ";
		MapList orgMap=db.query(querySQL, rechangeId, Type.VARCHAR);
		
		if(!Checker.isEmpty(orgMap)){
			String orgCode=orgMap.getRow(0).get("org_code");
			String buy_time_length=orgMap.getRow(0).get("buy_time_length");
			
			if(orgCode!=null&&buy_time_length!=null){
				//更新到期时间为购买后的某一年
				String udpateSQL="UPDATE lxny_space_usage_fee "
						+ " SET due_time=(SELECT get_exprit_date_unit(COALESCE(max(due_time),now()),"+buy_time_length+",'year') "
						+ "               FROM lxny_space_usage_fee WHERE status='1' AND org_code='"+orgCode+"'),"
					    + " status='1' WHERE id='"+rechangeId+"' ";
				
				db.execute(udpateSQL);
			}
			
			
		}
	}
	
	
}
