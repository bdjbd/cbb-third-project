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
 
<order>
	<certificateNo>330182198804273139</certificateNo>身份证号
	<linkName>庄工</linkName>联系人必填
	<linkMobile>13625814109</linkMobile>必填
	<orderCode>t20141204002226</orderCode>你们的订单编码（或别的），要求唯一，我回调你们通知检票完了的标识及取消订单
	<orderPrice>200.00</orderPrice>订单总价格
	<groupNo></groupNo>团号
	<payMethod></payMethod>支付方式值spot现场支付vm备佣金，zyb智游宝支付 
	<ticketOrders>
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
	</ticketOrders>
</order>
 *****/
@XmlRootElement(name="order")
public class Order {
	//身份证号
	private String certificateNo="";
	//联系人必填
	private String linkName;
	//联系电话
	private String linkMobile;
	//你们的订单编码（或别的），要求唯一，我回调你们通知检票完了的标识及取消订单
	private String orderCode;
	//订单总价格
	private String orderPrice;
	//团号
	private String groupNo;
	//支付方式值spot现场支付vm备佣金，zyb智游宝支付 
	private String payMethod="vm";
	
	private String assistCheckNo;//>00055359</assistCheckNo>辅助码
	private String src="interface";//>interface</src>
	
	
	private List<TicketOrder> ticketOrders=new ArrayList<TicketOrder>();
	
	/**
	 * 身份证号
	 * @return
	 */
	@XmlElement(name="certificateNo")
	public String getCertificateNo() {
		return certificateNo;
	}
	
	/**
	 * 身份证号
	 * @param certificateNo
	 */
	public void setCertificateNo(String certificateNo) {
		this.certificateNo = certificateNo;
	}
	
	
	/**
	 * 联系人必填
	 * @param linkName
	 */
	@XmlElement(name="linkName")
	public String getLinkName() {
		return linkName;
	}
	
	/**
	 * 联系人必填
	 * @param linkName
	 */
	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}
	
	/**
	 * 联系电话
	 * @return
	 */
	@XmlElement(name="linkMobile")
	public String getLinkMobile() {
		return linkMobile;
	}
	
	/**
	 * 联系电话
	 * @param linkName
	 */
	public void setLinkMobile(String linkMobile) {
		this.linkMobile = linkMobile;
	}
	
	/**
	 * 你们的订单编码（或别的），要求唯一，我回调你们通知检票完了的标识及取消订单
	 * @param orderCode
	 */
	@XmlElement(name="orderCode")
	public String getOrderCode() {
		return orderCode;
	}
	
	/**
	 * 你们的订单编码（或别的），要求唯一，我回调你们通知检票完了的标识及取消订单
	 * @param orderCode
	 */
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	
	/**
	 * 订单总价格
	 * @return
	 */
	@XmlElement(name="orderPrice")
	public String getOrderPrice() {
		return orderPrice;
	}
	
	/***
	 * 订单总价格
	 * @param orderPrice
	 */
	public void setOrderPrice(String orderPrice) {
		this.orderPrice = orderPrice;
	}
	
	/**
	 * 团号
	 * @return
	 */
	@XmlElement(name="groupNo")
	public String getGroupNo() {
		return groupNo;
	}
	
	
	/**
	 * /团号
	 * @param groupNo
	 */
	public void setGroupNo(String groupNo) {
		this.groupNo = groupNo;
	}
	
	/**
	 * 支付方式值spot现场支付vm备佣金，zyb智游宝支付 
	 * @return
	 */
	@XmlElement(name="payMethod")
	public String getPayMethod() {
		return payMethod;
	}
	
	/**
	 * 支付方式值spot现场支付vm备佣金，zyb智游宝支付 
	 * @param payMethod
	 */
	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
	}
	
	@XmlElement(name="ticketOrders")
	public List<TicketOrder> getTicketOrders() {
		return ticketOrders;
	}
	public void setTicketOrders(List<TicketOrder> ticketOrders) {
		this.ticketOrders = ticketOrders;
	}
	
	/**
	 * 00055359</assistCheckNo>辅助码
	 * @return
	 */
	@XmlElement(name="assistCheckNo")
	public String getAssistCheckNo() {
		return assistCheckNo;
	}
	
	/**
	 * 00055359</assistCheckNo>辅助码
	 * @param assistCheckNo
	 */
	public void setAssistCheckNo(String assistCheckNo) {
		this.assistCheckNo = assistCheckNo;
	}
	
	@XmlElement(name="src")
	public String getSrc() {
		return src;
	}
	/**
	 * interface
	 * @param src
	 */
	public void setSrc(String src) {
		this.src = src;
	}
	
}
