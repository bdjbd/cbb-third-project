package com.am.frame.systemAccount.service;

import org.jgroups.util.UUID;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * 账号操作Service
 * @author yuebin
 *
 */
public class AccountService {
	
	/**
	 * 更新可用额度
	 * @param db DB
	 * @param memberOrOrgId  机构/会员id
	 * @param amount  增加的额度,单位元
	 * @param classCode  账号类型
	 * @throws JDBCException 
	 */
	public void updateAvailableCashAmount(DB db,String amount,String memberOrOrgId,String classCode,String remarks) throws Exception{
		
		String querySQL="SELECT acinfo.id,sac.id AS sa_class_id "+
						" FROM mall_account_info AS acinfo "+
						" LEFT JOIN mall_system_account_class AS sac ON acinfo.a_class_id=sac.id "+
						" WHERE acinfo.member_orgid_id=? "+
						" AND sac.sa_code=? ";
		
		MapList map=db.query(querySQL,new String[]{
				memberOrOrgId,classCode
		},new int[]{
				Type.VARCHAR,Type.VARCHAR
		});
		
		if(!Checker.isEmpty(map)){
			//账号ID
			String accid=map.getRow(0).get("id");
			String sa_class_id=map.getRow(0).get("sa_class_id");
			
			if(amount!=null&&amount.contains(".")){
				amount=amount.substring(0, amount.indexOf("."));
			}
			
			
			//更新额度
			String udpateSQL="UPDATE mall_account_info SET available_cash_amount=COALESCE(available_cash_amount,0)+? "+
					 		"  WHERE id=? ";
			db.execute(udpateSQL,new String[]{
					amount,accid
			}, new int[]{
					Type.DOUBLE,Type.VARCHAR
			});
			
			
			//增加交易记录
			String id=UUID.randomUUID().toString();
			String inserSQL="INSERT INTO mall_trade_detail( "
		            +" id, member_id, account_id, sa_class_id, trade_time, trade_total_money,  "
		            +" rmarks, create_time, trade_type, trade_state,  "
		            +" business_json, is_process_buissnes,  "
		            +" record_trade_total_money) "
		            +" VALUES ('"+id+"','"+memberOrOrgId+"', '"+accid+"', '"+sa_class_id+"', now(), "+amount+",  "
		            +" '"+remarks+"', now(),'2','1',  "
		            +" '', 1, "
		            +" "+amount+")"; 
			
			db.execute(inserSQL);
			
		}
		
		
	}
	
}
