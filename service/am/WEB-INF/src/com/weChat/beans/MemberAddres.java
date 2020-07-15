package com.weChat.beans;
/**
 *   说明:
 * 		会员地址Beans
 *   @creator	岳斌
 *   @create 	Nov 15, 2013 
 *   @version	$Id
 */

public class MemberAddres implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Long addresId;
	private Member member;
	private Integer recvProvince;
	private Integer recvCity;
	private Integer recvArea;
	private String recvDetail;
	private String recvName;
	private String recvPhone;
	private String recvPostalCode;

	// Constructors

	/** default constructor */
	public MemberAddres() {
	}

	/** minimal constructor */
	public MemberAddres(Long addresId, Member member) {
		this.addresId = addresId;
		this.member = member;
	}

	/** full constructor */
	public MemberAddres(Long addresId, Member member, Integer recvProvince,
			Integer recvCity, Integer recvArea, String recvDetail,
			String recvName, String recvPhone, String recvPostalCode) {
		this.addresId = addresId;
		this.member = member;
		this.recvProvince = recvProvince;
		this.recvCity = recvCity;
		this.recvArea = recvArea;
		this.recvDetail = recvDetail;
		this.recvName = recvName;
		this.recvPhone = recvPhone;
		this.recvPostalCode = recvPostalCode;
	}

	// Property accessors

	public Long getAddresId() {
		return this.addresId;
	}

	public void setAddresId(Long addresId) {
		this.addresId = addresId;
	}

	public Member getMember() {
		return this.member;
	}

	public void setMember(Member member) {
		this.member = member;
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

}