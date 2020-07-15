package com.am.frame.transactions.withdraw;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * 提现管理
 * @author wz
 * 2016-04-26
 */
public class WidthDrawManager {

	/**
	 * 
	 * @param db
	 * @param outMemberId 提现用户
	 * @param outAccountCode 提现账号的sa_code
	 * @param inAccountId 提现银行卡
	 * @param virementNumber 提现金额
	 * @param account_type 提现类型 1 社员 2 机构
	 * @param remarks 说明
	 * @return
	 */
	public JSONObject execute(DB db ,String outMemberId, String outAccountCode,String inAccountId,String virementNumber,String account_type,String remarks) {
		
		JSONObject resultJson = new JSONObject();
		
		boolean flag = true;
		/**
		 * 查询 用户账号
		 */
		String osql = "SELECT mai.id FROM mall_account_info as mai"
				+ " left join mall_system_account_class as msac on mai.a_class_id = msac.id "
				+ " WHERE 1 = 1 and  mai.member_orgid_id = '"+outMemberId+"' and sa_code = '"+outAccountCode+"'";
		
		String ssql = "select * from MALL_MEMBER_BANK where id='"+inAccountId+"' and member_orgid_id = '"+outMemberId+"'";
		
		try {
			
			MapList oList = db.query(osql);
			MapList iList = db.query(ssql);
			
			if(Checker.isEmpty(oList) ){
				resultJson.put("code", "401");
				resultJson.put("msg", "用户账户不存在");
			}
			if(Checker.isEmpty(iList)){
				resultJson.put("code", "401");
				resultJson.put("msg", "用户银行卡不存在");
			}
			if(flag){
				
				resultJson = executeRun(db,outMemberId,oList.getRow(0).get("id"),iList.getRow(0).get("id"),virementNumber,account_type,remarks);
			
			}
			
		} catch (Exception e) {
			
			resultJson = new JSONObject();
			try {
				resultJson.put("code", "999");
				resultJson.put("msg", "操作失败");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
		return resultJson;
	}
	
	/**
	 * 提现操作
	 * @param outMemberId 提现用户
	 * @param outAccountCode 提现账号的sa_code
	 * @param inAccountId 提现银行卡
	 * @param virementNumber 提现金额
	 * @param account_type 提现类型
	 * @param remarks 说明
	 * @return
	 */
	public JSONObject executeRun(DB db,String outMemberid, String outAccountId,String inAccountId,String virementNumber,String account_type,String remarks) {
		
		//出账用户id
		JSONObject resultJson = new JSONObject();
		
		try {
			
			if(db.getConnection().getAutoCommit()){
				db.getConnection().setAutoCommit(false);
			}
		
			MapList list = getAccountInfo(db, outAccountId);
			
			String isql ="";
			
			boolean flag = true;
			
			//判断交易额度
			if(changeY2F(virementNumber)>(Long.parseLong((list.getRow(0).get("max_gmv"))))){
				
				flag = false;
				resultJson.put("code", "404");
				resultJson.put("msg", "交易金额超出账户最大交易金额限制");
			
			}else if(changeY2F(virementNumber)<(list.getRow(0).getLong("min_gmv",0))){
				
				flag = false;
				resultJson.put("code", "405");
				resultJson.put("msg", "交易金额账户小于最小交易金额限制");
			
			}
			
			//先判断交易额度限制
			if(flag){
				//在判断账户余额
				if(changeY2F(virementNumber)<=(list.getRow(0).getLong("balance",0))){
					
					Long virement_money = 0L ;
				    
					if(!Checker.isEmpty(list.getRow(0).get("cash_fee_ratio"))){
				    	virement_money=(long)Math.floor(changeY2F(virementNumber)*(new Float(list.getRow(0).get("cash_fee_ratio"))));
				    }
				
					Long money = ((list.getRow(0).getLong("balance",0)))-changeY2F(virementNumber);
					
					int reslut = updateAccountInfo(db, outAccountId,money);
					
					String id = UUID.randomUUID().toString();
					id = id.replaceAll("-", "");
					//交易记录id
					String Transactionrecord_id=UUID.randomUUID().toString();
					
					//现金账户类型
					String AccountClassName ="";
					
					//1 为社员 2 为机构
					if("1".equals(account_type)){
						isql = "insert into withdrawals (id,member_id,out_account_id,in_account_id,mention_time,cash_withdrawal,settlement_state,counter_fee,remarks,account_type,notify_details) "
								+ "values('"+id+"','"+outMemberid+"','"+outAccountId+"','"+inAccountId+"','now()','"+(changeY2F(virementNumber)-virement_money)+"','1'"
								+ " ,'"+virement_money+"','"+remarks+"','"+account_type+"','"+Transactionrecord_id+"')";
						AccountClassName = "CASH_ACCOUNT";
					}else{
						isql = "insert into withdrawals (id,member_id,out_account_id,in_account_id,mention_time,cash_withdrawal,settlement_state,counter_fee,remarks,account_type,notify_details) "
								+ "values('"+id+"','"+outMemberid+"','"+outAccountId+"','"+inAccountId+"','now()','"+(changeY2F(virementNumber)-virement_money)+"','0'"
								+ " ,'"+virement_money+"','"+remarks+"','"+account_type+"','"+Transactionrecord_id+"')";
						AccountClassName = "GROUP_CASH_ACCOUNT";
					}
					
					
					db.execute(isql);
					

					String querySQL="SELECT acinfo.id,sac.id AS sa_class_id "+
									" FROM mall_account_info AS acinfo "+
									" LEFT JOIN mall_system_account_class AS sac ON acinfo.a_class_id=sac.id "+
									" WHERE acinfo.member_orgid_id=? "+
									" AND sac.sa_code=? ";
					
					MapList accountClassMapList=db.query(querySQL,new String[]{
							outMemberid,AccountClassName
					},new int[]{
							Type.VARCHAR,Type.VARCHAR
					});
					
					

//					//增加交易记录
					
					String inserSQL="INSERT INTO mall_trade_detail( "
				            +" id, member_id, account_id, sa_class_id,counter_fee, trade_time, trade_total_money,  "
				            +" rmarks, create_time, trade_type, trade_state,  "
				            +" business_json, is_process_buissnes,  "
				            +" record_trade_total_money,user_submit_trade_money) "
				            +" VALUES ('"+Transactionrecord_id+"','"+outMemberid+"', '"+outAccountId+"', '"+accountClassMapList.getRow(0).get("sa_class_id")+"','"+virement_money+"', now(), "+changeY2F(virementNumber)+",  "
				            +" '"+remarks+"', now(),'1','1',  "
				            +" '', 1, "
				            +" "+changeY2F(virementNumber)+","+changeY2F(virementNumber)+")"; 
					
					db.execute(inserSQL);
					
					
					
					if(reslut>0){
						
						resultJson.put("code", "0");
						resultJson.put("msg", "提现申请成功");
						resultJson.put("id", id);
					
					}else{
						
						resultJson.put("code", "401");
						resultJson.put("msg", "提现失败，入账账户不存在");
					
					}
					
				}else{
					resultJson.put("code", "402");
					resultJson.put("msg", "账户余额不足");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			try {
				resultJson.put("code", "403");
				resultJson.put("msg", "系统出现错误");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		
		return resultJson;
	}
	
	/**
	 * 获取用户账户信息
	 * @param db
	 * @param accountId
	 * @return
	 */
	private MapList getAccountInfo(DB db,String accountId) throws Exception{
		
		MapList list = null;
		
		String sql = "";	
			//查询前台  用户账户表
			sql = "select mai.*"
					+ " ,myac.sa_code"
					+ " ,myac.max_gmv"
					+ " ,myac.min_gmv"
					+ " ,myac.transfer_fee_ratio"
					+ ",myac.cash_fee_ratio"
					+ " FROM mall_account_info as mai"
					+ " left join mall_system_account_class as myac on "
					+ " myac.id=mai.a_class_id "
					+ " WHERE mai.id ='"+accountId+"'"
					+ " and myac.status_valid='1'";	
		
		list = db.query(sql); 
		return list;
	}
	
	/**
	 * 更新用户账户余额信息
	 * @param db
	 * @param accountId
	 * @return
	 */
	private int updateAccountInfo(DB db,String accountId,Long money) throws Exception{
		
		int list = 0;
		
		String sql = "";	
		//查询前台  用户账户表
		sql = "update mall_account_info set balance = '"+money+"' where id='"+accountId+"'";	
		list = db.execute(sql); 
		return list;
	}
	
	 /**   
     * 将元为单位的转换为分 替换小数点，支持以逗号区分的金额  
     *   
     * @param amount  
     * @return  
     */    
    public static Long changeY2F(String amount){    
        String currency =  amount.replaceAll("\\$|\\￥|\\,", "");  //处理包含, ￥ 或者$的金额    
        int index = currency.indexOf(".");    
        int length = currency.length();    
        Long amLong = 0l;    
        if(index == -1){    
            amLong = Long.valueOf(currency+"00");    
        }else if(length - index >= 3){    
            amLong = Long.valueOf((currency.substring(0, index+3)).replace(".", ""));    
        }else if(length - index == 2){    
            amLong = Long.valueOf((currency.substring(0, index+2)).replace(".", "")+0);    
        }else{    
            amLong = Long.valueOf((currency.substring(0, index+1)).replace(".", "")+"00");    
        }    
        return amLong;    
    }
}
