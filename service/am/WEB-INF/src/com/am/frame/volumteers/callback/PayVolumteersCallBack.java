package com.am.frame.volumteers.callback;

import org.jgroups.util.UUID;
import org.json.JSONObject;

import com.am.frame.transactions.callback.IBusinessCallBack;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.util.Checker;

/**
 * 购买志愿者服务账号等级操作业务回调
 * @author mac
 *
 */
public class PayVolumteersCallBack implements IBusinessCallBack 
{

	@Override
	public String callBackExec(String id, String business, DB db, String type) throws Exception 
	{
		
		JSONObject businessJso = new JSONObject(business);
		
		//志愿者等级配置管理表id
		String volunteers_id = businessJso.getString("volunteers_id");
		
		//入账账号
		String in_account_code = businessJso.getString("in_account_code");
		
		//会员id
		String memberid = businessJso.getString("memberid");
		
		//订单id
		String orders = businessJso.getString("orders");
		
		//支付金额
		String paymoney = businessJso.getString("paymoney");
		
		String sql ="select * from volunteers_record where member_id = '"+memberid+"'";
		
		String usql = "";
		MapList list = db.query(sql);
		
		String ssql ="select * from volunteers_manager where id = '"+volunteers_id+"'";
		
		MapList slist = db.query(ssql);
		
		int i = 0;
		
		
		if(!Checker.isEmpty(list) && !Checker.isEmpty(slist))
		{
			usql = "update volunteers_record set leve = '"+slist.getRow(0).get("leve")+"',people_num = '"+slist.getRow(0).get("peoper_num")+"' where member_id='"+memberid+"'";
			i = db.execute(usql);
		
		}else
		{
			usql = "insert into volunteers_record (id,member_id,leve,people_num,surplus_num,create_time)"
					+ " values('"+UUID.randomUUID()+"','"+memberid+"','"+slist.getRow(0).get("leve")+"','"+slist.getRow(0).get("peoper_num")+"','0',now())";
			i = db.execute(usql);
		}
		
		JSONObject resoult = new JSONObject();
		
		
		resoult.put("code", "0");
		
		return resoult.toString();
	}

}
