package com.am.frame.badge.impl.authBadge;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author YueBin
 * @create 2016年6月13日
 * @version 
 * 说明:<br />
 * 社员权限 条件
 *
 */
public class TaskConditionParams {
	
	public TaskConditionParams(){}
	
	public TaskConditionParams(JSONObject params){
		if(params!=null){
			try {
				this.RESISTER=params.getBoolean("RESISTER");
				this.CREDIT_MARGIN_MEONY=params.getString("CREDIT_MARGIN_MEONY");
				this.COMUNT_FREEZER_DEPOSIT=params.getString("COMUNT_FREEZER_DEPOSIT");
				this.CONSUMER_RECHANGE=params.getString("CONSUMER_RECHANGE");
				this.HELP_FARMER=params.getInt("HELP_FARMER");
				this.INVST_PROJECT_MEONY=params.getString("INVST_PROJECT_MEONY");
				this.INVITE_MEMBER_NUMBER=params.getJSONArray("INVITE_MEMBER_NUMBER");
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**是否注册  RESISTER**/
	private boolean RESISTER=true;
	/**信用保证金（元）**/
	private String CREDIT_MARGIN_MEONY;
	/**社区冷柜押金**/
	private String COMUNT_FREEZER_DEPOSIT;
	/**消费账户 充值(元)**/
	private String CONSUMER_RECHANGE;
	/**帮借农户**/
	private int HELP_FARMER;
	/**投资项目股金总额（元）**/
	private String INVST_PROJECT_MEONY;
	/**邀请社员**/
	private JSONArray  INVITE_MEMBER_NUMBER;
	
	/**返回JSON格式字符串**/
	public JSONObject toJson(){
		JSONObject result=new JSONObject();
		try {
			result.put("RESISTER",RESISTER);
			result.put("CREDIT_MARGIN_MEONY",CREDIT_MARGIN_MEONY);
			result.put("COMUNT_FREEZER_DEPOSIT",COMUNT_FREEZER_DEPOSIT);
			result.put("CONSUMER_RECHANGE",CONSUMER_RECHANGE);
			result.put("HELP_FARMER",HELP_FARMER);
			result.put("INVST_PROJECT_MEONY",INVST_PROJECT_MEONY);
			result.put("INVITE_MEMBER_NUMBER",INVITE_MEMBER_NUMBER);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean isRESISTER() {
		return RESISTER;
	}
	public void setRESISTER(boolean rESISTER) {
		RESISTER = rESISTER;
	}
	public String getCREDIT_MARGIN_MEONY() {
		return CREDIT_MARGIN_MEONY;
	}
	public void setCREDIT_MARGIN_MEONY(String cREDIT_MARGIN_MEONY) {
		CREDIT_MARGIN_MEONY = cREDIT_MARGIN_MEONY;
	}
	public String getCOMUNT_FREEZER_DEPOSIT() {
		return COMUNT_FREEZER_DEPOSIT;
	}
	public void setCOMUNT_FREEZER_DEPOSIT(String cOMUNT_FREEZER_DEPOSIT) {
		COMUNT_FREEZER_DEPOSIT = cOMUNT_FREEZER_DEPOSIT;
	}
	public String getCONSUMER_RECHANGE() {
		return CONSUMER_RECHANGE;
	}
	public void setCONSUMER_RECHANGE(String cONSUMER_RECHANGE) {
		CONSUMER_RECHANGE = cONSUMER_RECHANGE;
	}
	public int getHELP_FARMER() {
		return HELP_FARMER;
	}
	public void setHELP_FARMER(int hELP_FARMER) {
		HELP_FARMER = hELP_FARMER;
	}
	public String getINVST_PROJECT_MEONY() {
		return INVST_PROJECT_MEONY;
	}
	public void setINVST_PROJECT_MEONY(String iNVST_PROJECT_MEONY) {
		INVST_PROJECT_MEONY = iNVST_PROJECT_MEONY;
	}
	public JSONArray getINVITE_MEMBER_NUMBER() {
		return INVITE_MEMBER_NUMBER;
	}
	public void setINVITE_MEMBER_NUMBER(JSONArray iNVITE_MEMBER_NUMBER) {
		INVITE_MEMBER_NUMBER = iNVITE_MEMBER_NUMBER;
	}

}









