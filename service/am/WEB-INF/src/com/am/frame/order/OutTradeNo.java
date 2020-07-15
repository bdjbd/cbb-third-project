package com.am.frame.order;


/**
 * 商户订单号
 * @author Administrator
 *
 */
public class OutTradeNo {
	private String prefix;
	private String membeCode;
	private String orderCode;
	
	public OutTradeNo(){}
	
	public OutTradeNo(String outTradeNo){
		if(outTradeNo!=null&&outTradeNo.contains("@")){
			String[] s=outTradeNo.split("@");
			if(s.length>2){
				this.prefix=s[0];
				this.membeCode=s[1];
				this.orderCode=s[2];
			}
		}
	}
	
	
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getMembeCode() {
		return membeCode;
	}
	public void setMembeCode(String membeCode) {
		this.membeCode = membeCode;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
}
