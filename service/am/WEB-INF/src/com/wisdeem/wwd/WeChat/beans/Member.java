package com.wisdeem.wwd.WeChat.beans;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.fastunit.Row;

/**
 *   说明:
 * 		会员Beans
 *   @creator	岳斌
 *   @create 	Nov 15, 2013 
 *   @version	$Id
 */
public class Member implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Long memberCode;
	private String aorg;
	private Long groupId;
	private String wshopName;
	private String openid;
	private String wshaopPassword;
	private String nickname;
	private String memName;
	private Short gender;
	private Integer age;
	private String phone;
	private String email;
	private String hobby;
	private String postalCode;
	private Integer grade;
	private Integer integration;
	private Timestamp lastLoginTime;
	private String lastLogintube;
	private String lastLautitube;
	private String precision;
	private Timestamp createTime;
	private Timestamp lastModiftyTime;
	private Short dataStatus;
	private String explain;
	private Set wsOrders = new HashSet(0);
	private Set memberAddreses = new HashSet(0);


	/** default constructor */
	public Member() {
	}

	/** minimal constructor */
	public Member(Long memberCode, String aorg) {
		this.memberCode = memberCode;
		this.aorg = aorg;
	}

	/** full constructor */
	public Member(Long memberCode, String aorg, Long groupId, String wshopName,
			String wshaopPassword, String nickname, String memName,
			Short gender, Integer age, String phone, String email,
			String hobby, String postalCode, Integer grade,
			Integer integration, Timestamp lastLoginTime, String lastLogintube,
			String lastLautitube, String precision, Timestamp createTime,
			Timestamp lastModiftyTime, Short dataStatus, String explain,
			Set wsOrders, Set memberAddreses) {
		this.memberCode = memberCode;
		this.aorg = aorg;
		this.groupId = groupId;
		this.wshopName = wshopName;
		this.wshaopPassword = wshaopPassword;
		this.nickname = nickname;
		this.memName = memName;
		this.gender = gender;
		this.age = age;
		this.phone = phone;
		this.email = email;
		this.hobby = hobby;
		this.postalCode = postalCode;
		this.grade = grade;
		this.integration = integration;
		this.lastLoginTime = lastLoginTime;
		this.lastLogintube = lastLogintube;
		this.lastLautitube = lastLautitube;
		this.precision = precision;
		this.createTime = createTime;
		this.lastModiftyTime = lastModiftyTime;
		this.dataStatus = dataStatus;
		this.explain = explain;
		this.wsOrders = wsOrders;
		this.memberAddreses = memberAddreses;
	}


	public Member(Row row) {
		this.openid=row.get("OPENID", "");
		this.nickname=row.get("NICKNAME","");
		this.aorg=row.get("ORGID","");
		this.memberCode=Long.parseLong(row.get("MEMBER_CODE","0"));
	}

	public Member(HttpServletRequest req) {
		this.openid=req.getParameter("openid");
		this.nickname=req.getParameter("nickname");
//		this.wshopName=req.getParameter("wshop_name");
//		this.wshaopPassword=req.getParameter("wshop_password");
		this.age=Integer.parseInt(req.getParameter("age"));
		this.gender=Short.parseShort(req.getParameter("gender"));
		this.phone=req.getParameter("phone");
		this.email=req.getParameter("email");
		this.postalCode=req.getParameter("postal_code");
		String memberCode=req.getParameter("member_code");
		if(memberCode!=null&&memberCode!=""){
			this.memberCode=Long.parseLong(memberCode);
		}
		Map map=req.getParameterMap();
		String temp="";
		for(int i=1;i<9;i++){
			String key="hobby"+i;
			if(map.containsKey(key)){
				temp+=i+",";
			}
		}
		if(temp.length()>0){
			this.hobby=temp.substring(0, temp.length());
		}
	}

	public Long getMemberCode() {
		return this.memberCode;
	}

	public void setMemberCode(Long memberCode) {
		this.memberCode = memberCode;
	}

	public String getAorg() {
		return this.aorg;
	}

	public void setAorg(String aorg) {
		this.aorg = aorg;
	}

	public Long getGroupId() {
		return this.groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getWshopName() {
		return this.wshopName;
	}

	public void setWshopName(String wshopName) {
		this.wshopName = wshopName;
	}

	public String getWshaopPassword() {
		return this.wshaopPassword;
	}

	public void setWshaopPassword(String wshaopPassword) {
		this.wshaopPassword = wshaopPassword;
	}

	public String getNickname() {
		return this.nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getMemName() {
		return this.memName;
	}

	public void setMemName(String memName) {
		this.memName = memName;
	}

	public Short getGender() {
		return this.gender;
	}

	public void setGender(Short gender) {
		this.gender = gender;
	}

	public Integer getAge() {
		return this.age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHobby() {
		return this.hobby;
	}

	public void setHobby(String hobby) {
		this.hobby = hobby;
	}

	public String getPostalCode() {
		return this.postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public Integer getGrade() {
		return this.grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public Integer getIntegration() {
		return this.integration;
	}

	public void setIntegration(Integer integration) {
		this.integration = integration;
	}

	public Timestamp getLastLoginTime() {
		return this.lastLoginTime;
	}

	public void setLastLoginTime(Timestamp lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getLastLogintube() {
		return this.lastLogintube;
	}

	public void setLastLogintube(String lastLogintube) {
		this.lastLogintube = lastLogintube;
	}

	public String getLastLautitube() {
		return this.lastLautitube;
	}

	public void setLastLautitube(String lastLautitube) {
		this.lastLautitube = lastLautitube;
	}

	public String getPrecision() {
		return this.precision;
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}

	public Timestamp getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getLastModiftyTime() {
		return this.lastModiftyTime;
	}

	public void setLastModiftyTime(Timestamp lastModiftyTime) {
		this.lastModiftyTime = lastModiftyTime;
	}

	public Short getDataStatus() {
		return this.dataStatus;
	}

	public void setDataStatus(Short dataStatus) {
		this.dataStatus = dataStatus;
	}

	public String getExplain() {
		return this.explain;
	}

	public void setExplain(String explain) {
		this.explain = explain;
	}

	public Set getWsOrders() {
		return this.wsOrders;
	}

	public void setWsOrders(Set wsOrders) {
		this.wsOrders = wsOrders;
	}

	public Set getMemberAddreses() {
		return this.memberAddreses;
	}

	public void setMemberAddreses(Set memberAddreses) {
		this.memberAddreses = memberAddreses;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}
	
	@Override
	public String toString() {
		String result="{\"MEMBERCODE\":"+this.memberCode+",\"OPENID\":\""+this.openid+"\",\"ORGID\":\""+this.aorg+"\",\"NICKNAME\":\""+this.nickname+"\"}";
		return result;
	}
}