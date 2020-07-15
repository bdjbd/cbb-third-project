package cn.hylexus.jt808.vo.req;


import org.json.JSONObject;

import cn.hylexus.jt808.vo.PackageData;


/**
 * 位置信息汇报请求
 */ 
public class LocationInfoUploadMsg extends PackageData {
	// 报警标志
	// byte[0-3]
	//private int warningFlagField;
	// byte[4-7] 状态(DWORD(32))
	//private int statusField;
	// byte[8-11] 纬度(DWORD(32))
	private double latitude;
	// byte[12-15] 经度(DWORD(32))
	private double longitude;
	// byte[16-17] 高程(WORD(16)) 海拔高度，单位为米（ m）
	// TODO ==>int?海拔
	private int elevation;
	// byte[18-19] 速度(WORD) 1/10km/h
	// TODO ==>float?速度
	private double speed;
	// byte[20-21] 方向(WORD) 0-359，正北为 0，顺时针
	private int direction;
	// byte[22-x] 时间(BCD[6]) YY-MM-DD-hh-mm-ss
	// GMT+8 时间，本标准中之后涉及的时间均采用此时区
	private String time;
	
	//**附加协议
	//里程，DWORD，1/10km，对应车上里程表读数 
	//byte[28-31]
	private double mileage;
	//油量，WORD，1/10L，对应车上油量表读数
	//byte[32-33]
	private int oilVolume;
	//行驶记录功能获取的速度，WORD，1/10km/h
	//byte[34-35]
	private int getSpeed;
	//需要人工确认报警事件的 ID，WORD，从 1 开始计数 
	//byte[36-37]
	private String  confirmAlarm;
	
	//OBD实时上报数据[38-64]
	private ObdRealTimeDataRpt obdRealTimeDataRpt;
    //OBD本次结束时上报[65-85]
	private ObdOverDataRpt obdOverDataRpt;
	//选择预留的附加信息[86-118]
	private ReserveAdditionalRpt reserveAdditionalRpt;
	//**附加协议
	
	//判断是哪个附加协议
	private String type;
	
	//报警标志位
	private JSONObject warningFlag;
	//状态标志位
	private JSONObject status;
	
	public LocationInfoUploadMsg() {
	}

	public LocationInfoUploadMsg(PackageData packageData) {
		this();
		this.channel = packageData.getChannel();
		this.checkSum = packageData.getCheckSum();
		this.msgBodyBytes = packageData.getMsgBodyBytes();
		this.msgHeader = packageData.getMsgHeader();
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getElevation() {
		return elevation;
	}

	public void setElevation(int elevation) {
		this.elevation = elevation;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

//	public int getWarningFlagField() {
//		return warningFlagField;
//	}
//
//	public void setWarningFlagField(int warningFlagField) {
//		this.warningFlagField = warningFlagField;
//	}
//
//	public int getStatusField() {
//		return statusField;
//	}
//
//	public void setStatusField(int statusField) {
//		this.statusField = statusField;
//	}
	
	public double getMileage() {
		return mileage;
	}

	public void setMileage(double mileage) {
		this.mileage = mileage;
	}

	public int getOilVolume() {
		return oilVolume;
	}

	public void setOilVolume(int oilVolume) {
		this.oilVolume = oilVolume;
	}

	public int getGetSpeed() {
		return getSpeed;
	}

	public void setGetSpeed(int getSpeed) {
		this.getSpeed = getSpeed;
	}

	public String getConfirmAlarm() {
		return confirmAlarm;
	}

	public void setConfirmAlarm(String confirmAlarm) {
		this.confirmAlarm = confirmAlarm;
	}
	
	public ObdRealTimeDataRpt getObdRealTimeDataRpt() {
		return obdRealTimeDataRpt;
	}

	public void setObdRealTimeDataRpt(ObdRealTimeDataRpt obdRealTimeDataRpt) {
		this.obdRealTimeDataRpt = obdRealTimeDataRpt;
	}

	public ObdOverDataRpt getObdOverDataRpt() {
		return obdOverDataRpt;
	}

	public void setObdOverDataRpt(ObdOverDataRpt obdOverDataRpt) {
		this.obdOverDataRpt = obdOverDataRpt;
	}

	public ReserveAdditionalRpt getReserveAdditionalRpt() {
		return reserveAdditionalRpt;
	}

	public void setReserveAdditionalRpt(ReserveAdditionalRpt reserveAdditionalRpt) {
		this.reserveAdditionalRpt = reserveAdditionalRpt;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	

	public JSONObject getWarningFlag() {
		return warningFlag;
	}

	public void setWarningFlag(JSONObject warningFlag) {
		this.warningFlag = warningFlag;
	}

	public JSONObject getStatus() {
		return status;
	}

	public void setStatus(JSONObject status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "LocationInfoUploadMsg [latitude=" + latitude + ", longitude=" + longitude + ", elevation=" + elevation
				+ ", speed=" + speed + ", direction=" + direction + ", time=" + time + ", mileage=" + mileage
				+ ", oilVolume=" + oilVolume + ", getSpeed=" + getSpeed + ", confirmAlarm=" + confirmAlarm
				+ ", obdRealTimeDataRpt=" + obdRealTimeDataRpt + ", obdOverDataRpt=" + obdOverDataRpt
				+ ", reserveAdditionalRpt=" + reserveAdditionalRpt + ", type=" + type + ", warningFlag=" + warningFlag
				+ ", status=" + status + "]";
	}
}
