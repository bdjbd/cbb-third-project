package com.am.frame.weichart.beans;
/**
 * @author YueBin
 * @create 2016年3月20日
 * @version 
 * 说明:<br />
 */
public class BizOrder {

	private String descript;
	private String id;
	private int totalFee;
	private String attch;
	
	public void setDescript(String descript) {
		this.descript = descript;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTotalFee(int totalFee) {
		this.totalFee = totalFee;
	}

	public String getDescript() {
		return descript;
	}

	public String getId() {
		return id;
	}
	/**
	 * 单位为分
	 * @return
	 */
	public int getTotalFee() {
		return totalFee;
	}

	public String getAttch() {
		return attch;
	}

	public void setAttch(String attch) {
		this.attch = attch;
	}

}
