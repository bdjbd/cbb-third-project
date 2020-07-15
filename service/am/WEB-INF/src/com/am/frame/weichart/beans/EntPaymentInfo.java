package com.am.frame.weichart.beans;
/**
 * @author YueBin
 * @create 2016年9月8日
 * @version 
 * 说明:<br />
 * 微信企业支付用户 bans
 * 
 * <xml>
<mch_appid>wxe062425f740c30d8</mch_appid>
<mchid>10000098</mchid>
<nonce_str>3PG2J4ILTKCH16CQ2502SI8ZNMTM67VS</nonce_str>
<partner_trade_no>100000982014120919616</partner_trade_no>
<openid>ohO4Gt7wVPxIT1A9GjFaMYMiZY1s</openid>
<check_name>OPTION_CHECK</check_name>
<re_user_name>张三</re_user_name>
<amount>100</amount>
<desc>节日快乐!</desc>
<spbill_create_ip>10.2.3.10</spbill_create_ip>
<sign>C97BDBACF37622775366F38B629F45E3</sign>
</xml>
 * 
 */
public class EntPaymentInfo {

	
	private String mch_appid;
	private String mchid;
	private String nonce_str;
	private String partner_trade_no;
	private String openid;
	private String check_name;
	private String re_user_name;
	private String amount;
	private String desc;
	private String spbill_create_ip;
	private String sign;
	private String key;
	
	
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getMch_appid() {
		return mch_appid;
	}
	public void setMch_appid(String mch_appid) {
		this.mch_appid = mch_appid;
	}
	public String getMchid() {
		return mchid;
	}
	public void setMchid(String mchid) {
		this.mchid = mchid;
	}
	public String getNonce_str() {
		return nonce_str;
	}
	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}
	public String getPartner_trade_no() {
		return partner_trade_no;
	}
	public void setPartner_trade_no(String partner_trade_no) {
		this.partner_trade_no = partner_trade_no;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	

	/**
	 * NO_CHECK：不校验真实姓名 
	 * FORCE_CHECK：强校验真实姓名（未实名认证的用户会校验失败，无法转账） 
	 * OPTION_CHECK：针对已实名认证的用户才校验真实姓名（未实名认证用户不校验，可以转账成功
	 * @param check_name
	 */
	public String getCheck_name() {
		return check_name;
	}
	
	
	/**
	 * NO_CHECK：不校验真实姓名 
	 * FORCE_CHECK：强校验真实姓名（未实名认证的用户会校验失败，无法转账） 
	 * OPTION_CHECK：针对已实名认证的用户才校验真实姓名（未实名认证用户不校验，可以转账成功
	 * @param check_name
	 */
	public void setCheck_name(String check_name) {
		this.check_name = check_name;
	}
	public String getRe_user_name() {
		return re_user_name;
	}
	public void setRe_user_name(String re_user_name) {
		this.re_user_name = re_user_name;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getSpbill_create_ip() {
		return spbill_create_ip;
	}
	public void setSpbill_create_ip(String spbill_create_ip) {
		this.spbill_create_ip = spbill_create_ip;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
	
	
	
}
