package com.am.mall.beans.order;

import java.io.Serializable;

import com.am.frame.util.ShareCodeUtil;
import com.fastunit.MapList;
import com.fastunit.Row;

/**
 * @author Mike
 * @create 2014年11月14日
 * @version 
 * 说明:<br />
 * 会员订单对象
 */
public class MemberOrder implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	 private String id;
	 private String orderCode;
	 private String memberID;
	 private String commodityID;
	 private String orderState;
	 private String paymentMode;
	 private String commodityName;
	 private String specName;
	 private double specPrice;
	 private String specId;
	 private long saleNumber;  
	 private double salePrice;
	 private double postage;
	 private double preferentialPrice;
	 private String preferentialDetil;
	 private double totalPrice;
	 private String paymentDate;
	 private String completeDate;
	 private String contact;
	 private String contactPhone;
	 private String address;
	 private String zipCode;
	 private String longitud;
	 private String latitude;
	 private String orgcode;
	 private String menuCode;
	 private String payId;
//	 private  int  mrole;           
//	 private  int  isAuth;             
//	 private  String remark;            
//	 private  long creditMarginAct;   
//	 private  long identityCapitalAct;
//	 private  long bonusAct;      
//	 private  long rebateAct;      
//	 private  long consumerAct;
//	 private  long consumerUnit;
//	 private  long foodSafetyAct;
//	 private  long creditCard;
//	 private  long consumerCapitalAct;
//	 private  String villageName;
//	 private  String heirName; 
//	 private  String heirPhone;   
//	 private  String heirIdcardCode; 
//	 private  String province;
//	 private  String city;          
//	 private  String zzone;

	//送货方式
	private String shippingMethod;
	 
	 public MemberOrder(){}
	 
	 public MemberOrder(MapList map){
		 this(map.getRow(0));
	 }
	 
	 public MemberOrder(Row row){
		 if(row!=null){
			 this.id=row.get("id");   
			 this.orderCode=row.get("ordercode");   
			 this.memberID=row.get("memberid");   
			 this.commodityID=row.get("commodityid");   
			 this.orderState=row.get("orderstate");   
			 this.paymentMode=row.get("paymentmode");   
			 this.commodityName=row.get("commodityname");   
			 this.specName=row.get("specname");   
			 this.specPrice=row.getDouble("specprice",0);   
			 this.saleNumber =row.getLong("salenumber",0);   
			 this.salePrice=row.getDouble("saleprice",0);   
			 this.postage=row.getDouble("postage",0);   
			 this.preferentialPrice=row.getDouble("preferentialprice",0);   
			 this.preferentialDetil=row.get("preferentialdetil");   
			 this.totalPrice=row.getDouble("totalprice",0);   
			 this.paymentDate=row.get("paymentdate");   
			 this.completeDate=row.get("completedate");   
			 this.contact=row.get("contact");   
			 this.contactPhone=row.get("contactphone");   
			 this.address=row.get("address");   
			 this.zipCode=row.get("zipcode");   
			 this.longitud=row.get("longitud");   
			 this.latitude=row.get("latitude");   
			 this.orgcode=row.get("orgcode");   
			 this.menuCode=row.get("menucode"); 
			 this.specId=row.get("specid");
			 this.shippingMethod=row.get("shipping_method");
			 this.payId=row.get("pay_id");
//			 this.mrole=row.getInt("mrole",-1);           
//			 this.isAuth=row.getInt("is_auth",-1);             
//			 this.remark=row.get("remark");            
//			 this.creditMarginAct=row.getLong("credit_margin_act",0);   
//			 this.identityCapitalAct=row.getLong("identity_capital_act",0); 
//			 this.bonusAct=row.getLong("bonus_act",0); 
//			 this.rebateAct=row.getLong("rebate_act",0);      
//			 this.consumerAct=row.getLong("consumer_act",0); 
//			 this.consumerUnit=row.getLong("consumer_unit",0); 
//			 this.foodSafetyAct=row.getLong("food_safety_act",0); 
//			 this.creditCard=row.getLong("credit_card",0); 
//			 this.consumerCapitalAct=row.getLong("consumer_capital_act",0); 
//			 this.villageName=row.get("village_name");
//			 this.heirName=row.get("heir_name"); 
//			 this.heirPhone=row.get("heir_phone");   
//			 this.heirIdcardCode=row.get("heir_idcard_code"); 
//			 this.province=row.get("province");
//			 this.city=row.get("city");          
//			 this.zzone=row.get("zzone");
			 
		 }
	 }
	 
	 
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public String getMemberID() {
		return memberID;
	}
	public void setMemberID(String memberID) {
		this.memberID = memberID;
	}
	public String getCommodityID() {
		return commodityID;
	}
	public void setCommodityID(String commodityID) {
		this.commodityID = commodityID;
	}
	public String getOrderState() {
		return orderState;
	}
	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}
	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
	public String getCommodityName() {
		return commodityName;
	}
	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}
	public String getSpecName() {
		return specName;
	}
	public void setSpecName(String specName) {
		this.specName = specName;
	}
	public double getSpecPrice() {
		return specPrice;
	}
	public void setSpecPrice(double specPrice) {
		this.specPrice = specPrice;
	}
	public long getSaleNumber() {
		return saleNumber;
	}
	public void setSaleNumber(long saleNumber) {
		this.saleNumber = saleNumber;
	}
	public double getSalePrice() {
		return salePrice;
	}
	public void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
	}
	public double getPostage() {
		return postage;
	}
	public void setPostage(double postage) {
		this.postage = postage;
	}
	public double getPreferentialPrice() {
		return preferentialPrice;
	}
	public void setPreferentialPrice(double preferentialPrice) {
		this.preferentialPrice = preferentialPrice;
	}
	public String getPreferentialDetil() {
		return preferentialDetil;
	}
	public void setPreferentialDetil(String preferentialDetil) {
		this.preferentialDetil = preferentialDetil;
	}
	public double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	public String getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}
	public String getCompleteDate() {
		return completeDate;
	}
	public void setCompleteDate(String completeDate) {
		this.completeDate = completeDate;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getLongitud() {
		return longitud;
	}
	public void setLongitud(String longitud) {
		this.longitud = longitud;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getOrgcode() {
		return orgcode;
	}
	public void setOrgcode(String orgcode) {
		this.orgcode = orgcode;
	}
	public String getMenuCode() {
		return menuCode;
	}
	public void setMenuCode(String menuCode) {
		this.menuCode = menuCode;
	}

	public String getSpecId() {
		return specId;
	}

	public void setSpecId(String specId) {
		this.specId = specId;
	}
	
	/**
	 * 获取订单的送货方式
	 * @return  1:即时送货；2：预约送货
	 */
	 public String getShippingMethod() {
		return shippingMethod;
	}


	public String getOderCodeByMemberId(String memberPhone){
		 String reuslt=memberPhone;
		 long time=System.currentTimeMillis();
		 reuslt=ShareCodeUtil.toSerialCode(time);
		 if(reuslt!=null&&reuslt.contains("#")){
			 reuslt=reuslt.replace("#","");
		 }
		 return reuslt;
	 }

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}
	
	
	
}
