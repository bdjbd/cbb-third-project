package com.am.frame.elect;

import java.io.Serializable;

import com.fastunit.Row;


/**
 * 企业电子券
 * @author Administrator
 *
 */
public class EterpElectTicket implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String id;    
	private String orgid;    
	private String sname;    
	private String electtickettype;    
	private Double cash ;    
	private int scoreValue ;    
	private String iconpath;    
	private int effectivedate;    
	private String datastatus;    
	private String createtime;    
	private String operar;    
	private String expired;
	
	public EterpElectTicket(){}
	
	public EterpElectTicket(Row row){
		
		if(row!=null){
			this.id=row.get("id");
			this.sname=row.get("sname");
			this.electtickettype=row.get("electtickettype");
			this.cash=row.getDouble("cash", 0);
			this.scoreValue=row.getInt("scorevalue", 0);
			this.iconpath=row.get("iconpath");
			this.effectivedate=row.getInt("effectivedate",0);
			this.datastatus=row.get("datastatus");
			this.createtime=row.get("createtime");
			this.operar=row.get("operar");
			this.expired=row.get("expired");
			this.orgid=row.get("orgid");
			
		}
		
	}
	
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrgid() {
		return orgid;
	}
	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}
	public String getSname() {
		return sname;
	}
	public void setSname(String sname) {
		this.sname = sname;
	}
	public String getElecttickettype() {
		return electtickettype;
	}
	public void setElecttickettype(String electtickettype) {
		this.electtickettype = electtickettype;
	}
	public Double getCash() {
		return cash;
	}
	public void setCash(Double cash) {
		this.cash = cash;
	}
	public String getIconpath() {
		return iconpath;
	}
	public void setIconpath(String iconpath) {
		this.iconpath = iconpath;
	}
	public int getEffectivedate() {
		return effectivedate;
	}
	public void setEffectivedate(int effectivedate) {
		this.effectivedate = effectivedate;
	}
	public String getDatastatus() {
		return datastatus;
	}
	public void setDatastatus(String datastatus) {
		this.datastatus = datastatus;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getOperar() {
		return operar;
	}
	public void setOperar(String operar) {
		this.operar = operar;
	}
	public String getExpired() {
		return expired;
	}
	public void setExpired(String expired) {
		this.expired = expired;
	}   
	
	public int getScoreValue() {
		return scoreValue;
	}

	public void setScoreValue(int scoreValue) {
		this.scoreValue = scoreValue;
	}

	@Override
	public int hashCode() {
		int result=17;
		result=11*result+id.hashCode();
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null)return false;
		if(obj==this)return true;
		if(obj instanceof EterpElectTicket){
			EterpElectTicket eet=(EterpElectTicket)obj;
			return eet.getId().equals(this.id);
		}
		return false;
	}
	
	
}
