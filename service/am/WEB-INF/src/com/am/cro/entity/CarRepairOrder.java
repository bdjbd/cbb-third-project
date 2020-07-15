package com.am.cro.entity;

import java.io.Serializable;

import com.fastunit.MapList;
import com.fastunit.Row;
/**
 * 车辆维修预约单
 * @author guorenjie
 *
 */
public class CarRepairOrder implements Serializable  {
	//私有化静态常量
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String orgCode;
	private String memberId;
	private String payState;//支付状态 '0'=未支付  '1'=已支付
	private double totalMoney;//订单金额
	private String orderExplain;//订单说明
	
	//无参构造器
	public CarRepairOrder(){}
	 //对外构造器   MapList构造器
	 public CarRepairOrder(MapList map){
		 this(map.getRow(0));
	 }
	 //全参构造器  Row构造器
	 public CarRepairOrder(Row row){
		 if(row!=null){
			 this.id=row.get("id");
			 this.orgCode = row.get("orgcode");
			 this.memberId = row.get("memberid");
			 this.payState = row.get("paystate");
			 this.totalMoney = row.getDouble("totalmoney", 0.0);
			 this.orderExplain = row.get("orderexplain");
		 }
	 }
	//get set 方法 
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getPayState() {
		return payState;
	}

	public void setPayState(String payState) {
		this.payState = payState;
	}

	public double getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(double totalMoney) {
		this.totalMoney = totalMoney;
	}
	//私有化静态常量 不必再set，直接可以get
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getOrderExplain() {
		return orderExplain;
	}

	public void setOrderExplain(String orderExplain) {
		this.orderExplain = orderExplain;
	}

}
