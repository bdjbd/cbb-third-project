package com.am.frame.payment.entity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fastunit.MapList;

/**
 * 支付请求参数
 * @author yuebin  <br/>
 *memberId:f193be7d-d9af-4fec-b3d7-4227915e18e6 会员ID      <br/>
 * inAccountCode:  入账账号编码 转账时有      <br/>
 * 	outAccountCode:UNIONPAY_ACCOUNT_MODE  出帐账号编码      <br/>
 * 	pay_id:F7200D38-42EE-4BB0-A343-4D78E38D7EE7 支付ID      <br/>
 * pay_money:44.8  支付金额，单元      <br/>
 * pay_type:1           支付类型,支付类型  1 支付 2 充值      <br/>
 * account_type:  账户类型  1 系统账户 2 支付宝 3 微信 4 银联      <br/>
 * business:{      <br/>
 * 						"payment_id":"F7200D38-42EE-4BB0-A343-4D78E38D7EE7",      <br/>
 * 						"memberid":"f193be7d-d9af-4fec-b3d7-4227915e18e6",      <br/>
 * 						"orders":"7DB39221647A452FB3606F3A0D4B79ED,",      <br/>
 * 						"paymoney":44.8,      <br/>
 * 						"success_call_back":"com.am.frame.order.process.OrderBusinessCallBack"      <br/>
 * 			}      <br/>
 * inremakes:,502胶   入账描述      <br/>
 * outremakes:,502胶   出账描述      <br/>
 * platform:1    平台类型  1 移动端  2 pc端      <br/>
 */
public class PaymentRequestEntity {

	/**用户id**/
	private String memberId;
	
	/**出账账户code*/
	private String outAccountCode;
	
	/**入账账户code**/
	private String inAccountCode;
	
	/**支付ID**/
	private String payId;
	
	/**支付金额**/
	private String payMoney;
	
	/**支付类型  1 支付 2 充值**/
	private String payType;
	
	/**账户类型  1 系统账户 2 支付宝 3 微信 4 银联**/
	private String accountType;
	
	/**业务参数 **/
	private String business;
	
	/**入账描述**/
	private String inrRemakes;
	
	/**出账描述**/
	private String outRemakes;
	
	/**平台类型  1 移动端  2 pc端**/
	private String platform;
	
	/**商品名称**/
	private String commodityName;
	
	/***openid 微信支付是使用**/
	private String openid;
	
	/**微信支付附件字符串**/
	private String attchStr;
	
	private MapList  accountList;
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getOutAccountCode() {
		return outAccountCode;
	}

	public void setOutAccountCode(String outAccountCode) {
		this.outAccountCode = outAccountCode;
	}

	public String getInAccountCode() {
		return inAccountCode;
	}

	public void setInAccountCode(String inAccountCode) {
		this.inAccountCode = inAccountCode;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(String payMoney) {
		this.payMoney = payMoney;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getBusiness() {
		return business;
	}

	public void setBusiness(String business) {
		this.business = business;
	}

	public String getInrRemakes() {
		return inrRemakes;
	}

	public void setInrRemakes(String inrRemakes) {
		this.inrRemakes = inrRemakes;
	}

	public String getOutRemakes() {
		return outRemakes;
	}

	public void setOutRemakes(String outRemakes) {
		this.outRemakes = outRemakes;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getAttchStr() {
		return attchStr;
	}

	public void setAttchStr(String attchStr) {
		this.attchStr = attchStr;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	/**
	 * 从HttpServletRequest 获取参数，如果HttpServletRequest不存在，则返回false
	 * @param key
	 * @return
	 */
	public String getParams(String key){
		String reuslt=null;
		if(request!=null){
			reuslt=request.getParameter(key);
		}
		return reuslt;
	}
	
	/**
	 * 获取请求地址IP地址，在微信支付时，需要用到此值
	 * 如果没有设置HttpServletRequest，则返回null
	 * @return
	 */
	public String getRemoteAddr(){
		String ip=null;
		if(request!=null){
			ip=request.getRemoteAddr();
		}
		return ip;
	}

	public MapList getAccountList() {
		return accountList;
	}

	public void setAccountList(MapList accountList) {
		this.accountList = accountList;
	}
	
	
	
}
