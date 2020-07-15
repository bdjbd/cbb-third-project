package com.am.app_plugins_common.entity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/***
 * 支付Business
 * 
 * @author yuebin
 * 
 */
public class PayBusiness {

	 private String payment_id;//
	 private String in_account_code;//:$scope.pageData.accountId,
	 private String memberid;//:$scope.pageData.memberid,
	 private String orders;//:uuid,
	 private String paymoney;//:new Number($scope.pageData.paymoney),
	 private String success_call_back;//:'com.am.frame.payment.business.PayBusinessCallBack'  
	
	 
	 private Map<String,String> otherParams=new HashMap<String,String>();
			 
	
	public JSONObject getBusinessJSON(){
		JSONObject business=new JSONObject();
		
		
		try {
			business.put("payment_id", payment_id);
			business.put("in_account_code", in_account_code);
			business.put("memberid", memberid);
			business.put("orders", orders);
			business.put("paymoney", paymoney);
			business.put("success_call_back", success_call_back);
			
			for(String key:otherParams.keySet()){
				business.put(key, otherParams.get(key));
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return business;
	}
	
	
	public void addParamas(String key,String value){
		otherParams.put(key, value);
	}
	
	public String getParams(String key){
		return otherParams.get(key);
	}


	public String getPayment_id() {
		return payment_id;
	}


	public void setPayment_id(String payment_id) {
		this.payment_id = payment_id;
	}


	public String getIn_account_code() {
		return in_account_code;
	}


	public void setIn_account_code(String in_account_code) {
		this.in_account_code = in_account_code;
	}


	public String getMemberid() {
		return memberid;
	}


	public void setMemberid(String memberid) {
		this.memberid = memberid;
	}


	public String getOrders() {
		return orders;
	}


	public void setOrders(String orders) {
		this.orders = orders;
	}


	public String getPaymoney() {
		return paymoney;
	}


	public void setPaymoney(String paymoney) {
		this.paymoney = paymoney;
	}


	public String getSuccess_call_back() {
		return success_call_back;
	}


	public void setSuccess_call_back(String success_call_back) {
		this.success_call_back = success_call_back;
	}
	
	
	
}
