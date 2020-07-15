package com.am.mall.beans.member;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月25日
 * @version 
 * 说明:<br />
 * 获取收获地址
 */
public class MemberAddres {

	private String id;
	private String amMemberId;
	private String recvDetail;
	private String recvName;
	private String recvPhone;
	private String recvPostalCode;
	private String longitud;
	private String latitude;
	private String defaultAddr;
	private String recvProvince;
	private String recvCity;
	private String recvArea;
	
	
	public MemberAddres(){}
	
	public MemberAddres(MapList map){
		
		if(!Checker.isEmpty(map)){
			Row row=map.getRow(0);
			
			this.id=row.get("id");
			this.amMemberId=row.get("am_memberid");
			this.recvDetail=row.get("recv_detail");
			this.recvName=row.get("recv_name");
			this.recvPhone=row.get("recv_phone");
			this.recvPostalCode=row.get("recv_postal_code");
			this.longitud=row.get("longitud");
			this.latitude=row.get("latitude");
			this.defaultAddr=row.get("defaultaddr");
			this.recvProvince=row.get("recv_province");
			this.recvCity=row.get("recv_city");
			this.recvArea=row.get("recv_area");
		}
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAmMemberId() {
		return amMemberId;
	}
	public void setAmMemberId(String amMemberId) {
		this.amMemberId = amMemberId;
	}
	public String getRecvDetail() {
		return recvDetail;
	}
	public void setRecvDetail(String recvDetail) {
		this.recvDetail = recvDetail;
	}
	public String getRecvName() {
		return recvName;
	}
	public void setRecvName(String recvName) {
		this.recvName = recvName;
	}
	public String getRecvPhone() {
		return recvPhone;
	}
	public void setRecvPhone(String recvPhone) {
		this.recvPhone = recvPhone;
	}
	public String getRecvPostalCode() {
		return recvPostalCode;
	}
	public void setRecvPostalCode(String recvPostalCode) {
		this.recvPostalCode = recvPostalCode;
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
	public String getDefaultAddr() {
		return defaultAddr;
	}
	public void setDefaultAddr(String defaultAddr) {
		this.defaultAddr = defaultAddr;
	}

	public String getRecvProvince() {
		return recvProvince;
	}

	public void setRecvProvince(String recvProvince) {
		this.recvProvince = recvProvince;
	}

	public String getRecvCity() {
		return recvCity;
	}

	public void setRecvCity(String recvCity) {
		this.recvCity = recvCity;
	}

	public String getRecvArea() {
		return recvArea;
	}

	public void setRecvArea(String recvArea) {
		this.recvArea = recvArea;
	}
	
}
