package com.p2p.pay;

import com.fastunit.Row;

/**
 * 我的支付
 * @author Administrator
 **id character varying(32) NOT NULL, -- 主键
	*paycode character varying(100), -- 订单支付，在订单号前面加字符“D”...
	*paydatetime time with time zone, -- 支付时间
	*paymoney numeric(9,2), -- 支付金额
	*alipayordercode character varying(100), -- 支付宝订单号
	*paycontent character varying(128), -- 订单支付及订单号码、充值支付
	*paysource integer, -- 1,订单...
	*member_code  biginteger ---会员编号
	*CONSTRAINT pk_p2p_memberpayment PRIMARY KEY (id)
 */
public class MemberPayment {

	private String id ;// 主键
	private String payCode ;// 订单支付，在订单号前面加字符“D”...
	private String payDateTime ;// 支付时间
	private double payMoney ;//支付金额
	private String alipayOrderCode;// 支付宝订单号
	private String payContent;//订单支付及订单号码、充值支付
	private int paySource;//1,订单2,充值.
	private String memberCode;//会员编号
	private boolean complete;
	
	
	public MemberPayment(){}
	
	public MemberPayment(Row row){
		
		if(row!=null){
			this.id=row.get("id");
			this.alipayOrderCode=row.get("alipayordercode");
			this.memberCode=row.get("memberid");
			this.payCode=row.get("paycode");
			this.payContent=row.get("paycontent");
			this.payDateTime=row.get("paydatetime");
			this.payMoney=row.getDouble("paymoney", 0);
			this.paySource=row.getInt("paysource",-1);
			
			String comp=row.get("iscomplete");
			
			if("0".equals(comp)){
				this.complete=false;
			}else{
				this.complete=true;
			}
			
		}
	}
	
	/**
	 * 主键
	 * @return
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * 主键
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * 支付单号: 订单支付，在订单号前面加字符“D” ,充值支付，在充值号前面加字符"C"
	 * @return
	 */
	public String getPayCode() {
		return payCode;
	}
	
	/**
	 * 支付单号: 订单支付，在订单号前面加字符“D” ,充值支付，在充值号前面加字符"C"
	 * @param payCode
	 */
	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}
	
	/**
	 * 支付时间
	 * @return
	 */
	public String getPayDateTime() {
		return payDateTime;
	}
	
	/**
	 * 支付时间
	 * @param payDateTime
	 */
	public void setPayDateTime(String payDateTime) {
		this.payDateTime = payDateTime;
	}
	
	/**
	 * 支付金额
	 * @return
	 */
	public double getPayMoney() {
		return payMoney;
	}
	
	/**
	 * 支付金额
	 * @param payMoney
	 */
	public void setPayMoney(double payMoney) {
		this.payMoney = payMoney;
	}
	/**
	 * 支付宝订单号
	 * @return
	 */
	public String getAlipayOrderCode() {
		return alipayOrderCode;
	}
	/**
	 * 支付宝订单号
	 * @param alipayOrderCode
	 */
	public void setAlipayOrderCode(String alipayOrderCode) {
		this.alipayOrderCode = alipayOrderCode;
	}
	
	/**
	 * 支付内容
	 * @return
	 */
	public String getPayContent() {
		return payContent;
	}
	
	/**
	 * 支付内容
	 * @param payContent
	 */
	public void setPayContent(String payContent) {
		this.payContent = payContent;
	}
	
	/**
	 * 支付来源:1,订单;2,充值
	 * @return
	 */
	public int getPaySource() {
		return paySource;
	}
	
	/**
	 * 支付来源:1,订单;2,充值
	 * @param paySource
	 */
	public void setPaySource(int paySource) {
		this.paySource = paySource;
	}

	/**
	 * 会员编号
	 * @return
	 */
	public String getMemberCode() {
		return memberCode;
	}

	/**
	 * 会员编号
	 * @return
	 */
	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	
}
