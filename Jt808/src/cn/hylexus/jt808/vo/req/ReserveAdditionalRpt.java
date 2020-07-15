package cn.hylexus.jt808.vo.req;


/**
 * 选择预留的附加信息0x10
 * @author 王成阳
 * 2018-3-5
 */
public class ReserveAdditionalRpt {

	//起始时间(YY-MM-DD-hh-mm-ss)
	//BCD[6]
	private String startDateTime;
	//时长统计参考源(1:OBD 2:GPS)
	//1byte
	private int referenceSource;
	//本次行驶时长(秒)
	//4byte
	private int travelDuration;
	//本次静止时长(秒)
	//4byte
	private int resetDuration;
	//本次急加速次数(次)
	//2byte
	private int rapidAccelerationCnt;
	//本次急减速次数(次)
	//2byte
	private int rapidDecelerationCnt;
	//本次耗油量(升)
	//2byte
	private int oilConsumption;
	//本次行驶里程(百米)
	//2byte
	private int drivingMileage;
	//本次急转弯次数(次)
	//2byte
	private int sharpTurnCnt;
	//本次急变道次数(次)
	//2byte
	private int steepRoadCnt;
	//预留
	//4byte
	private String reserved;
	
	public ReserveAdditionalRpt(){
		
	}

	
	public String getStartDateTime() {
		return startDateTime;
	}


	public void setStartDateTime(String startDateTime2) {
		this.startDateTime = startDateTime2;
	}




	public int getReferenceSource() {
		return referenceSource;
	}



	public void setReferenceSource(int referenceSource) {
		this.referenceSource = referenceSource;
	}



	public int getTravelDuration() {
		return travelDuration;
	}



	public void setTravelDuration(int travelDuration) {
		this.travelDuration = travelDuration;
	}



	public int getResetDuration() {
		return resetDuration;
	}



	public void setResetDuration(int resetDuration) {
		this.resetDuration = resetDuration;
	}



	public int getRapidAccelerationCnt() {
		return rapidAccelerationCnt;
	}



	public void setRapidAccelerationCnt(int rapidAccelerationCnt) {
		this.rapidAccelerationCnt = rapidAccelerationCnt;
	}



	public int getRapidDecelerationCnt() {
		return rapidDecelerationCnt;
	}



	public void setRapidDecelerationCnt(int rapidDecelerationCnt) {
		this.rapidDecelerationCnt = rapidDecelerationCnt;
	}



	public int getOilConsumption() {
		return oilConsumption;
	}



	public void setOilConsumption(int oilConsumption) {
		this.oilConsumption = oilConsumption;
	}



	public int getDrivingMileage() {
		return drivingMileage;
	}



	public void setDrivingMileage(int drivingMileage) {
		this.drivingMileage = drivingMileage;
	}



	public int getSharpTurnCnt() {
		return sharpTurnCnt;
	}



	public void setSharpTurnCnt(int sharpTurnCnt) {
		this.sharpTurnCnt = sharpTurnCnt;
	}



	public int getSteepRoadCnt() {
		return steepRoadCnt;
	}



	public void setSteepRoadCnt(int steepRoadCnt) {
		this.steepRoadCnt = steepRoadCnt;
	}



	public String getReserved() {
		return reserved;
	}



	public void setReserved(String reserved) {
		this.reserved = reserved;
	}



	@Override
	public String toString() {
		return "ReserveAdditionalRpt [startDateTime=" + startDateTime + ", referenceSource=" + referenceSource + ", travelDuration=" + travelDuration + ", resetDuration=" + resetDuration + ", rapidAccelerationCnt=" + rapidAccelerationCnt + ", rapidDecelerationCnt=" + rapidDecelerationCnt + ", oilConsumption=" + oilConsumption + ", drivingMileage=" + drivingMileage + ", sharpTurnCnt=" + sharpTurnCnt + ", steepRoadCnt=" + steepRoadCnt + ", Reserved=" + reserved +"]";
	}
}
