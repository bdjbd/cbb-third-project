package com.am.frame.other.taskInterface.impl.entity;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author YueBin
 * @create 2016年6月27日
 * @version 
 * 说明:<br />
 */
/****
<ticketOrder>
<orderCode>t2014120400222601</orderCode>必填你们的子订单编码
<credentials>              实名制的必填，非实名制可以不填
	<credential>
		<name>帅哥</name> （真实姓名）
		<id>330182198804273139</id> (实名制商品需要传多个身份证）
	</credential>
</credentials>
<price>100.00</price>票价，必填，线下要统计的
<quantity>1</quantity>必填票数量
<totalPrice>1.00</totalPrice>必填子订单总价
<occDate>2014-12-09</occDate>必填日期（游玩日期）
<goodsCode>20140331011721</goodsCode> 必填 商品编码，同票型编码
<goodsName>商品名称</goodsName> -----商品名称 
<remark>商品名称</remark> -----备注 
</ticketOrder>
***/
@XmlRootElement(name="ticketOrder")
public class TicketOrder {
	private String orderCode;//>t2014120400222601</orderCode>必填你们的子订单编码

	private List<Credential> credentials=new ArrayList<Credential>() ;//实名制的必填，非实名制可以不填
//			<name>帅哥</name> （真实姓名）
//			<id>330182198804273139</id> (实名制商品需要传多个身份证）
	private String price;//>100.00;//</price>票价，必填，线下要统计的
	private String quantity;//>1;//</quantity>必填票数量
	private String totalPrice;//>1.00;//</totalPrice>必填子订单总价
	private String occDate;//>2014-12-09;//</occDate>必填日期（游玩日期）
	private String goodsCode;//>20140331011721;//</goodsCode> 必填 商品编码，同票型编码
	private String goodsName;//>商品名称;//</goodsName> -----商品名称 
	private String remark;//>商品名称;//</remark> -----备注 
	
	@XmlElement(name="orderCode")
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	
	@XmlElement(name="price")
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	
	@XmlElement(name="quantity")
	public String getQuantity() {
		return quantity;
	}
	
	
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	
	@XmlElement(name="totalPrice")
	public String getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	@XmlElement(name="occDate")
	public String getOccDate() {
		return occDate;
	}
	public void setOccDate(String occDate) {
		this.occDate = occDate;
	}
	
	@XmlElement(name="goodsCode")
	public String getGoodsCode() {
		return goodsCode;
	}
	public void setGoodsCode(String goodsCode) {
		this.goodsCode = goodsCode;
	}
	
	@XmlElement(name="goodsName")
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	
	@XmlElement(name="remark")
	public String getRemark() {
		return remark;
	}
	
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	@XmlElement(name="credentials")
	public List<Credential> getCredentials() {
		return credentials;
	}
	public void setCredentials(List<Credential> credentials) {
		this.credentials = credentials;
	}
	
	
	
}
