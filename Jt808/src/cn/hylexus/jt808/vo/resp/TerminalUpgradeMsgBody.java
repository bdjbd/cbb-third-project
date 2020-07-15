package cn.hylexus.jt808.vo.resp;

import java.util.Arrays;

import io.netty.channel.Channel;

/**
 * 终端升级消息体
 * @author 王成阳
 */
public class TerminalUpgradeMsgBody {
	
	//byte[0] 升级类型  (0：终端，12：道路运输证 IC 卡读卡器，52：北斗卫星定位模块) 
	private int UpgradeType;
	//byte[1-5] 制造商ID  
	private int manufacturerID;
	//byte[6] 版本号长度
	private int versionNumLength;
	//String[7] 版本号
	private String versionNumber;
	//Dword[7-n] 升级数据包长度
	private int UpgradeDataLength;
	//[11+n] 升级包数据
	private byte[] UpgradeData;
	
	//管道
	private Channel channel;
	
	public TerminalUpgradeMsgBody(){
		
	}

	public int getUpgradeType() {
		return UpgradeType;
	}

	public void setUpgradeType(int upgradeType) {
		UpgradeType = upgradeType;
	}

	public int getManufacturerID() {
		return manufacturerID;
	}

	public void setManufacturerID(int manufacturerID) {
		this.manufacturerID = manufacturerID;
	}

	public int getVersionNumLength() {
		return versionNumLength;
	}

	public void setVersionNumLength(int versionNumLength) {
		this.versionNumLength = versionNumLength;
	}

	public String getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

	public int getUpgradeDataLength() {
		return UpgradeDataLength;
	}

	public void setUpgradeDataLength(int upgradeDataLength) {
		UpgradeDataLength = upgradeDataLength;
	}

	public byte[] getUpgradeData() {
		return UpgradeData;
	}

	public void setUpgradeData(byte[] upgradeData) {
		UpgradeData = upgradeData;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	@Override
	public String toString() {
		return "TerminalUpgradeMsgBody [UpgradeType=" + UpgradeType + ", manufacturerID=" + manufacturerID
				+ ", versionNumLength=" + versionNumLength + ", versionNumber=" + versionNumber + ", UpgradeDataLength="
				+ UpgradeDataLength + ", UpgradeData=" + Arrays.toString(UpgradeData) + "]";
	}
	
	
	
}
