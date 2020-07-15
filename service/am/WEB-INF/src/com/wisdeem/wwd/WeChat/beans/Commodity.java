package com.wisdeem.wwd.WeChat.beans;
import java.sql.Timestamp;

/**
 *   说明:
 * 		商品Beans
 *   @creator	岳斌
 *   @create 	Nov 15, 2013 
 *   @version	$Id
 */

public class Commodity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Long comdyClassId;
	private String aorg;
	private Long parentId;
	private String className;
	private Timestamp createTime;
	private Timestamp lastModifyTime;
	private Short dataStatus;
	private String explain;

	// Constructors

	/** default constructor */
	public Commodity() {
	}

	/** minimal constructor */
	public Commodity(Long comdyClassId, String aorg) {
		this.comdyClassId = comdyClassId;
		this.aorg = aorg;
	}

	/** full constructor */
	public Commodity(Long comdyClassId, String aorg, Long parentId,
			String className, Timestamp createTime, Timestamp lastModifyTime,
			Short dataStatus, String explain) {
		this.comdyClassId = comdyClassId;
		this.aorg = aorg;
		this.parentId = parentId;
		this.className = className;
		this.createTime = createTime;
		this.lastModifyTime = lastModifyTime;
		this.dataStatus = dataStatus;
		this.explain = explain;
	}

	// Property accessors

	public Long getComdyClassId() {
		return this.comdyClassId;
	}

	public void setComdyClassId(Long comdyClassId) {
		this.comdyClassId = comdyClassId;
	}

	public String getAorg() {
		return this.aorg;
	}

	public void setAorg(String aorg) {
		this.aorg = aorg;
	}

	public Long getParentId() {
		return this.parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Timestamp getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getLastModifyTime() {
		return this.lastModifyTime;
	}

	public void setLastModifyTime(Timestamp lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
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

}