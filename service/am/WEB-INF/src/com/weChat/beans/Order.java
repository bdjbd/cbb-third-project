package com.weChat.beans;

import java.sql.Timestamp;
import java.util.Date;

/**
 *   说明:
 * 		订单Beans
 *   @creator	岳斌
 *   @create 	Nov 15, 2013 
 *   @version	$Id
 */
public class Order implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String orderCode;
	private String aorg;
	private Member member;
	private Timestamp createTime;
	private Integer recvProvince;
	private Integer recvCity;
	private Integer recvArea;
	private String recvDetail;
	private String recvName;
	private String recvPhone;
	private String recvPostalCode;
	private Short payment;
	private Short dataStatus;
	private Date resnTrsatnTime;
	private Double total;

	// Constructors

	/** default constructor */
	public Order() {
	}

	/** minimal constructor */
	public Order(String orderCode, String aorg) {
		this.orderCode = orderCode;
		this.aorg = aorg;
	}

	/** full constructor */
	public Order(String orderCode, String aorg, Member member,
			Timestamp createTime, Integer recvProvince, Integer recvCity,
			Integer recvArea, String recvDetail, String recvName,
			String recvPhone, String recvPostalCode, Short payment,
			Short dataStatus, Date resnTrsatnTime, Double total) {
		this.orderCode = orderCode;
		this.aorg = aorg;
		this.member = member;
		this.createTime = createTime;
		this.recvProvince = recvProvince;
		this.recvCity = recvCity;
		this.recvArea = recvArea;
		this.recvDetail = recvDetail;
		this.recvName = recvName;
		this.recvPhone = recvPhone;
		this.recvPostalCode = recvPostalCode;
		this.payment = payment;
		this.dataStatus = dataStatus;
		this.resnTrsatnTime = resnTrsatnTime;
		this.total = total;
	}

	// Property accessors

	public String getOrderCode() {
		return this.orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getAorg() {
		return this.aorg;
	}

	public void setAorg(String aorg) {
		this.aorg = aorg;
	}

	public Member getMember() {
		return this.member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public Timestamp getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Integer getRecvProvince() {
		return this.recvProvince;
	}

	public void setRecvProvince(Integer recvProvince) {
		this.recvProvince = recvProvince;
	}

	public Integer getRecvCity() {
		return this.recvCity;
	}

	public void setRecvCity(Integer recvCity) {
		this.recvCity = recvCity;
	}

	public Integer getRecvArea() {
		return this.recvArea;
	}

	public void setRecvArea(Integer recvArea) {
		this.recvArea = recvArea;
	}

	public String getRecvDetail() {
		return this.recvDetail;
	}

	public void setRecvDetail(String recvDetail) {
		this.recvDetail = recvDetail;
	}

	public String getRecvName() {
		return this.recvName;
	}

	public void setRecvName(String recvName) {
		this.recvName = recvName;
	}

	public String getRecvPhone() {
		return this.recvPhone;
	}

	public void setRecvPhone(String recvPhone) {
		this.recvPhone = recvPhone;
	}

	public String getRecvPostalCode() {
		return this.recvPostalCode;
	}

	public void setRecvPostalCode(String recvPostalCode) {
		this.recvPostalCode = recvPostalCode;
	}

	public Short getPayment() {
		return this.payment;
	}

	public void setPayment(Short payment) {
		this.payment = payment;
	}

	public Short getDataStatus() {
		return this.dataStatus;
	}

	public void setDataStatus(Short dataStatus) {
		this.dataStatus = dataStatus;
	}

	public Date getResnTrsatnTime() {
		return this.resnTrsatnTime;
	}

	public void setResnTrsatnTime(Date resnTrsatnTime) {
		this.resnTrsatnTime = resnTrsatnTime;
	}

	public Double getTotal() {
		return this.total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

}