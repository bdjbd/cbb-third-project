package com.am.frame.transactions.rechange;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.util.Checker;

/**
 * 充值操作
 * @author mac
 *
 */
public class Rechange 
{
	
	private Logger logger=LoggerFactory.getLogger(getClass());
	//向汽修厂系统现金账户，进行充值操作（单位：分）
	public JSONObject rechangeExc(Long money,String inAccountCode,String inMemberId,String reamrks){
		
		logger.info(reamrks+" money:"+money+"\t inAccountCode:"+inAccountCode+"\t inMemberId:"+inMemberId);
		
		JSONObject resObj = new JSONObject();
		
		DBManager db = new DBManager();
		//获取汽修厂系统现金账户信息
		MapList list = getAccountInfo(db,inAccountCode,inMemberId);
		try 
		{
			if(!Checker.isEmpty(list))
			{
				//在系统账户表中，更新汽修厂的当前余额、累计资金量（单位：分）
				String sql = "update mall_account_info "
	    				+ " set balance='"+(Long.parseLong(list.getRow(0).get("balance"))+money)+"',total_amount ='"+(Long.parseLong(list.getRow(0).get("total_amount"))+money)+"'  where id ='"+list.getRow(0).get("id")+"'";
				
				db.execute(sql);
				//充值完成后，继续在交易记录表中，新增一条充值信息
				String isql = "insert into mall_trade_detail (id,member_id,account_id,sa_class_id"
						+ ",trade_time,trade_total_money,rmarks"
						+ ",create_time,business_id,trade_type,trade_state,business_json"
						+ ",is_process_buissnes,ty_pay_id) "
						+ "values('"+UUID.randomUUID()+"','"+inMemberId+"','"+list.getRow(0).get("id")+"','"+list.getRow(0).get("class_id")+"'"
								+ ",now(),'"+money+"','充值',now(),'','2','1','','0','')";
				db.execute(isql);
				//充值完成，则返回code为0
				resObj.put("code","0");
				resObj.put("msg",reamrks+"成功！");
				
			}else
			{
				
				resObj.put("code","999");
				resObj.put("msg","账户不存在");
				
			}
		} catch (JSONException e) 
		{
			e.printStackTrace();
		}
		
		return resObj;
	}
	
	//向汽修厂系统现金账户，进行充值操作（单位：分）
	public JSONObject rechangeExc(Long money,String inAccountCode,String inMemberId)
	{
		JSONObject resObj = new JSONObject();
		
		String reamrks="充值";
		resObj=rechangeExc(money, inAccountCode, inMemberId,reamrks);
		return resObj;
	}
	
	/**
	 * 获取用户账户信息
	 * @param db
	 * @param accountId
	 * @return
	 */
	public  MapList getAccountInfo(DBManager db,String outAccountCode,String inMemberId){
		
		MapList list = null;
		
		String sql = "";	
		//后台
			sql = "select mai.*"
					+ " ,myac.max_gmv"
					+ " ,myac.min_gmv"
					+ " ,myac.sa_code"
					+ " ,myac.id as class_id"
					+ " ,myac.transfer_fee_ratio"
					+ " FROM mall_account_info as mai"
					+ " left join mall_system_account_class as myac on "
					+ " myac.id=mai.a_class_id "
					+ " WHERE mai.member_orgid_id ='"+inMemberId+"'"
					+ " and myac.status_valid='1' and myac.sa_code = '"+outAccountCode+"'";	
		try {
			list = db.query(sql); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
}
