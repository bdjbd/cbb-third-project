package com.p2p.pay;

import java.sql.ResultSet;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.util.DBUtil;

/**
 * 支付管理类
 * @author Administrator
 *
 */
public class PayManager {

	private static PayManager payManager;
	private PayManager(){}
	
	
	public static PayManager getInstance(){
		if(payManager==null){
			payManager=new PayManager();
		}
		return payManager;
	}
	
	
	
	/**
	 * 保存会员支付数据到数据库
	 * @param mebPay MemberPayment
	 * @return true 保存成功，false 保存失败
	 * 
	 */
	public boolean createMemberPayment(MemberPayment memberPay){
		
		boolean result=false;
		
		try{
			DB db=DBFactory.getDB();
			
			String sql=
					"INSERT INTO mall_memberpayment(                                               "+
					"            id, paycode, paydatetime, paymoney, alipayordercode, paycontent, "+
					"            paysource,memberid)                                                       "+
					"    VALUES (uuid_generate_v4(), ?, now(), ?, ?, ?,"+
					"            ?,?)";
			
			int res=db.execute(sql, 
					new String[]{memberPay.getPayCode(),String.valueOf(memberPay.getPayMoney()),memberPay.getAlipayOrderCode(),memberPay.getPayContent()
					,String.valueOf(memberPay.getPaySource()),memberPay.getMemberCode()},
					new int[]{Type.VARCHAR,Type.DECIMAL,Type.VARCHAR,Type.VARCHAR
					,Type.INTEGER,Type.INTEGER});
			
			if(res>0){
				result=true;
			}
			
		}catch(JDBCException e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	/**
	 * 获取我的支付信息JSON格式数据
	 * @param memberCode
	 * @return JSON格式数据  {"DATA":[{item},{item}]}
	 */
	public String getMyPaymentToJSON(String memberCode){
		String result = "{\"DATA\":[]}";
		try {
			DB db=DBFactory.getDB();
			
			String sql="SELECT * FROM mall_MemberPayment  WHERE memberid=?";
			
			ResultSet rst=db.getResultSet(sql,new String[]{memberCode},new int[]{Type.VARCHAR});
					
			result = DBUtil.resultSetToJSON(rst).toString();
			
		} catch (Exception e) {
			e.printStackTrace();
			result = "{'errcode':40007,'errmsg':'"+e.getMessage()+"'}";
		}
		return result;
	}
	
	
	/**
	 * 现金支付
	 * @param memberCoder 会员编号
	 * @param payMoney  支付金额
	 * @return
	 */
	public boolean cashPay(String memberCode,Double payMoney){
		
		boolean result=false;
		
		try{
			//检查现金是否足够
			
			DB db=DBFactory.getDB();
			
			String checkSQL="SELECT cash FROM am_member  WHERE id='"+memberCode+"' AND cash>="+payMoney;
			
			MapList map=db.query(checkSQL);
			
			if(!Checker.isEmpty(map)){
				//足够扣除现金
				String sql="UPDATE  am_member  SET cash=COALESCE(cash,0)-"+payMoney
						+" WHERE id='"+memberCode+"'";
				
				if(db.execute(sql)>0){
					result=true;
				}
			}
			
		}catch(JDBCException e){
			e.printStackTrace();
		}
		return result;
	}
}
