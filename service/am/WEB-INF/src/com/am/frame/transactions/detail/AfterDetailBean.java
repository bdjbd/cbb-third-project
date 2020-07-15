package com.am.frame.transactions.detail;

import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;

/** 
 * @author  wz  
 * @descriptions 前台交易记录表映射
 * @date 创建时间：2016年4月14日 上午11:46:12 
 * @version 1.0   
 */
public class AfterDetailBean {
	
	
	private String id;
	
	//会员id
	private String member_id;
	
	//备注
	private String rmarks;
	
	//业务ID
	private String business_id;
	
	//交易总金额	
	private long trade_total_money;
	
	//手续费
	private long counter_fee;
	
	//用户提交交易金额
	private long userSubmitTrade_money;
	
	//创建时间
	private String create_time;
	
	//交易时间
	private String trade_time;
	
	//交易账户类型
	private String  sa_class_id;
	
	//转账类型
	private String trade_type;
	
	//业务参数集合
	private String business_json;
	
	//交易状态
	private String trade_state;
	
	//业务处理状态
	private String is_process_buissnes;
	
	//账户id
	private String account_id;
	
	//用户个人操作原因填写
	private String person_remaker;

	

	private Table tranTable=new Table("am_bdp", "mall_trade_detail");
	
	private TableRow tableRow =  null;
	
	public Table getTranTable() {
		
		return tranTable;
	
	}
	
	public TableRow getTableRow() {
		return tableRow;
	}


	public void setTableRow(TableRow tableRow) {
		this.tableRow = tableRow;
	}




	public String getTrade_type() {
		
		return this.tableRow.getValue("trade_type");
	}

	public void setTrade_type(String trade_type) {
		this.tableRow.setValue("trade_type", trade_type);
	}

	public String getSa_class_id() {
		
		return this.tableRow.getValue("sa_class_id");
	
	}
	
	public void setSa_class_id(String sa_class_id) {
		
		this.tableRow.setValue("sa_class_id", sa_class_id);
	
	}
	
	public String getTrade_time() throws Exception{
		
		return this.tableRow.getValue("trade_time");
	
	}
	
	public void setTrade_time(String trade_time) {
		
		 this.tableRow.setValue("trade_time", trade_time);
		
	}
	
	public String getCreate_time() throws Exception{
		
		return this.tableRow.getValue("create_time");
	}
	
	public void setCreate_time(String create_time){
		
		
		this.tableRow.setValue("create_time", create_time);
	
	}
	
	
	public String getId() {
		
		return this.tableRow.getValue("id");
	
	}
	
	public void setId(String id){
		
		this.tableRow.setValue("id", id);
	
	}
	
	
	public String getMember_id() {
		
		return this.tableRow.getValue("member_id");
	
	}
	
	public void setMember_id(String member_id) {
		
		this.tableRow.setValue("member_id", member_id);
	
	}
	
	public String getAccount_id() {
		
		return this.tableRow.getValue("account_id");
	
	}
	
	public void setAccount_id(String account_id) {
		
		this.tableRow.setValue("account_id", account_id);
	
	}
	
	public String getRmarks() {
		
		return this.tableRow.getValue("rmarks");
	
	}
	
	public void setRmarks(String rmarks) {
		
		this.tableRow.setValue("rmarks", rmarks);
	
	}
	
	public String getBusiness_id() {
		
		return this.tableRow.getValue("business_id");
	
	}
	
	public void setBusiness_id(String business_id) {
		
		this.tableRow.setValue("business_id", business_id);
	
	}
	
	public long getTrade_total_money() {
		
		return Long.parseLong(this.tableRow.getValue("trade_total_money"));
		
	}
	
	public void setTrade_total_money(long trade_total_money) {
		
		this.tableRow.setValue("trade_total_money", trade_total_money);
	
	}
	
	public long getUserSubmitTrade_money() {
		
		return Long.parseLong(this.tableRow.getValue("user_submit_trade_money"));
		
	}
	
	public void setUserSubmitTrade_money(long user_submit_trade_money) {
		
		this.tableRow.setValue("user_submit_trade_money", user_submit_trade_money);
	
	}
	
	public long getCounter_fee() {
		
		return Long.parseLong(this.tableRow.getValue("counter_fee"));
	
	}
	
	public void setCounter_fee(long counter_fee) {
		
		this.tableRow.setValue("counter_fee", counter_fee);
	
	}

	public String getBusiness_json() {
		return this.tableRow.getValue("business_json");
	}

	public void setBusiness_json(String business_json) {

		this.tableRow.setValue("business_json", business_json);
	}

	public String getTrade_state() {
		
		return this.tableRow.getValue("trade_state");
		
	}
	
	/**
	 * 1=支出，只要是支出均是负数
	 * 2=收入，只要是收入均是正数
	 * @param trade_state
	 */
	public void setTrade_state(String trade_state) {
		
		this.tableRow.setValue("trade_state", trade_state);
	}

	public String getIs_process_buissnes() {
		
		return is_process_buissnes;
	}

	public void setIs_process_buissnes(String is_process_buissnes) {
		
		this.is_process_buissnes = is_process_buissnes;
	}
	
	public String getPerson_remaker() {
		return this.tableRow.getValue("person_remaker");
	}

	public void setPerson_remaker(String person_remaker) {
		this.tableRow.setValue("person_remaker", person_remaker);
	}
	
	
	@Override
	public String toString() {
		return "";
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	
	
}
