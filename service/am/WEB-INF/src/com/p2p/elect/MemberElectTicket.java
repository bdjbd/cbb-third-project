package com.p2p.elect;

import java.io.Serializable;

import com.fastunit.Row;


/**
 * 用户电子券
 * @author Administrator
 *
 */
public class MemberElectTicket implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String id;//会员电子券ID
	private String memberCode;//会员ID
	private String usestatus;//使用状态  1，有效 2，已经使用。
	private String getdatetime;//获取日期
	private String usedatetime;//消费日期
	private String expired;//过期时间
	private String eterpElectTicketId;//企业电子券ID
	
	public MemberElectTicket(){}
	
	public MemberElectTicket(Row row){
		if(row!=null){
			this.id=row.get("id");
			this.memberCode=row.get("member_code");
			this.usestatus=row.get("usestatus");
			this.getdatetime=row.get("getdatetime");
			this.usedatetime=row.get("usedatetime");
			this.expired=row.get("expired");
			this.eterpElectTicketId=row.get("eterpelectticketid");
		}
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMemberCode() {
		return memberCode;
	}
	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}
	public String getUsestatus() {
		return usestatus;
	}
	public void setUsestatus(String usestatus) {
		this.usestatus = usestatus;
	}
	public String getGetdatetime() {
		return getdatetime;
	}
	public void setGetdatetime(String getdatetime) {
		this.getdatetime = getdatetime;
	}
	public String getUsedatetime() {
		return usedatetime;
	}
	public void setUsedatetime(String usedatetime) {
		this.usedatetime = usedatetime;
	}
	public String getExpired() {
		return expired;
	}
	public void setExpired(String expired) {
		this.expired = expired;
	}
	
	public String getEterpElectTicketId() {
		return eterpElectTicketId;
	}

	public void setEterpElectTicketId(String eterpElectTicketId) {
		this.eterpElectTicketId = eterpElectTicketId;
	}

	@Override
	public String toString() {
		return "ID:"+this.id+" memberCode:"+this.memberCode+"  usestatus:"+
				this.usestatus+" getdatetime:"+this.getdatetime+"  usedatetime:"+
				this.usedatetime+" expired:"+this.expired;
	}
	
	@Override
	public int hashCode() {
		int result=17;
		result=31*result+id==null?0:id.hashCode();
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null)return false;
		if(obj==this)return true;
		if(obj instanceof MemberElectTicket){
			MemberElectTicket other=(MemberElectTicket)obj;
			return this.id.equals(other.getId());
		}
		return false;
	}
	
	
}
