package cn.hylexus.jt808.vo.req;

import cn.hylexus.jt808.vo.PackageData;

/**
 * 终端升级结果通知
 */
public class TerminalUpgradeResultMsg extends PackageData{

	// 升级类型(0：终端,12：道路运输证 IC 卡读卡器,52：北斗卫星定位模块 )
	// byte[0]
	private int  upgradeType;
	
	// 升级结果
	// byte[1](0：成功,1：失败,2：取消 )
	private int upgradeResult;
	
	//命令表流水号
	private String serialNumber;
	
	public TerminalUpgradeResultMsg() {
	}

	public TerminalUpgradeResultMsg(PackageData packageData) {
		this();
		this.channel = packageData.getChannel();
		this.checkSum = packageData.getCheckSum();
		this.msgBodyBytes = packageData.getMsgBodyBytes();
		this.msgHeader = packageData.getMsgHeader();
	}

	public int getUpgradeType() {
		return upgradeType;
	}

	public void setUpgradeType(int upgradeType) {
		this.upgradeType = upgradeType;
	}

	public int getUpgradeResult() {
		return upgradeResult;
	}

	public void setUpgradeResult(int upgradeResult) {
		this.upgradeResult = upgradeResult;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	@Override
	public String toString() {
		return "TerminalUpgradeResultMsg [upgradeType=" + upgradeType + ", upgradeResult=" + upgradeResult
				+ ", serialNumber=" + serialNumber + "]";
	}

	
}
