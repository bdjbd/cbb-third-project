package com.cdms.ExcelOutPut.pojo;

public class JavaBean {
	private String license_plate_number;	//车牌号
	private String orgname;    //机构名称
	private String driver;     //驾驶员
	private String speeding_sum;   //超速次数
	private String fatigue_sum;    //疲劳驾驶次数
	private String rapid_acceleration_sum; //急加速次数
	private String deceleration_sum;  //急减速次数
	private String corner_sum;     //急转弯次数
	private String lane_change_sum; //急变道次数
	private String mileage_oil_sum; //行车里程油耗
	private String work_oil;    //工作里程油耗
	private String notwork_oil_sum; //非工作里程/油耗
	private String task_oil_sum;  //作业里程/油耗
	private String notask_oil_sum; //非作业里程/油耗
	private String illegal_sum;  //违章次数
	private String idling_abnormality; //怠速异常总时长
	private String notwork_time_car_sum; //非工作时间用车总时长
	private String region_out_tim; //区域外用车总时长
	private String region_out_in_sum;  //进出区域总次数
	private String work_time_sum; //考勤总时长
	
	//无参构造
	public JavaBean()
	{	
	}
	//有参构造
	public JavaBean(String license_plate_number,String orgname
					,String driver,String speeding_sum,String fatigue_sum
					,String rapid_acceleration_sum,String deceleration_sum
					,String corner_sum,String lane_change_sum,String mileage_oil_sum
					,String work_oil,String notwork_oil_sum,String task_oil_sum
					,String notask_oil_sum,String illegal_sum,String idling_abnormality
					,String notwork_time_car_sum,String region_out_tim,String region_out_in_sum
					,String work_time_sum){
		
		this.license_plate_number=license_plate_number;
		this.orgname=orgname;
		this.driver=driver;
		this.speeding_sum=speeding_sum;
		this.fatigue_sum=fatigue_sum;
		this.rapid_acceleration_sum=rapid_acceleration_sum;
		this.deceleration_sum=deceleration_sum;
		this.corner_sum=corner_sum;
		this.lane_change_sum=lane_change_sum;
		this.mileage_oil_sum=mileage_oil_sum;
		this.work_oil=work_oil;
		this.notwork_oil_sum=notwork_oil_sum;
		this.task_oil_sum=task_oil_sum;
		this.notask_oil_sum=notask_oil_sum;
		this.illegal_sum=illegal_sum;
		this.idling_abnormality=idling_abnormality;
		this.notwork_time_car_sum=notwork_time_car_sum;
		this.region_out_tim=region_out_tim;
		this.region_out_in_sum=region_out_in_sum;
		this.work_time_sum=work_time_sum;
		}
	
	
	public String getLicense_plate_number() {
		return license_plate_number;
	}
	public void setLicense_plate_number(String license_plate_number) {
		this.license_plate_number = license_plate_number;
	}
	public String getOrgname() {
		return orgname;
	}
	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getSpeeding_sum() {
		return speeding_sum;
	}
	public void setSpeeding_sum(String speeding_sum) {
		this.speeding_sum = speeding_sum;
	}
	public String getFatigue_sum() {
		return fatigue_sum;
	}
	public void setFatigue_sum(String fatigue_sum) {
		this.fatigue_sum = fatigue_sum;
	}
	public String getRapid_acceleration_sum() {
		return rapid_acceleration_sum;
	}
	public void setRapid_acceleration_sum(String rapid_acceleration_sum) {
		this.rapid_acceleration_sum = rapid_acceleration_sum;
	}
	public String getDeceleration_sum() {
		return deceleration_sum;
	}
	public void setDeceleration_sum(String deceleration_sum) {
		this.deceleration_sum = deceleration_sum;
	}
	public String getCorner_sum() {
		return corner_sum;
	}
	public void setCorner_sum(String corner_sum) {
		this.corner_sum = corner_sum;
	}
	public String getLane_change_sum() {
		return lane_change_sum;
	}
	public void setLane_change_sum(String lane_change_sum) {
		this.lane_change_sum = lane_change_sum;
	}
	public String getMileage_oil_sum() {
		return mileage_oil_sum;
	}
	public void setMileage_oil_sum(String mileage_oil_sum) {
		this.mileage_oil_sum = mileage_oil_sum;
	}
	public String getWork_oil() {
		return work_oil;
	}
	public void setWork_oil(String work_oil) {
		this.work_oil = work_oil;
	}
	public String getNotwork_oil_sum() {
		return notwork_oil_sum;
	}
	public void setNotwork_oil_sum(String notwork_oil_sum) {
		this.notwork_oil_sum = notwork_oil_sum;
	}
	public String getTask_oil_sum() {
		return task_oil_sum;
	}
	public void setTask_oil_sum(String task_oil_sum) {
		this.task_oil_sum = task_oil_sum;
	}
	public String getNotask_oil_sum() {
		return notask_oil_sum;
	}
	public void setNotask_oil_sum(String notask_oil_sum) {
		this.notask_oil_sum = notask_oil_sum;
	}
	public String getIllegal_sum() {
		return illegal_sum;
	}
	public void setIllegal_sum(String illegal_sum) {
		this.illegal_sum = illegal_sum;
	}
	public String getIdling_abnormality() {
		return idling_abnormality;
	}
	public void setIdling_abnormality(String idling_abnormality) {
		this.idling_abnormality = idling_abnormality;
	}
	public String getNotwork_time_car_sum() {
		return notwork_time_car_sum;
	}
	public void setNotwork_time_car_sum(String notwork_time_car_sum) {
		this.notwork_time_car_sum = notwork_time_car_sum;
	}
	public String getRegion_out_tim() {
		return region_out_tim;
	}
	public void setRegion_out_tim(String region_out_tim) {
		this.region_out_tim = region_out_tim;
	}
	public String getRegion_out_in_sum() {
		return region_out_in_sum;
	}
	public void setRegion_out_in_sum(String region_out_in_sum) {
		this.region_out_in_sum = region_out_in_sum;
	}
	public String getWork_time_sum() {
		return work_time_sum;
	}
	public void setWork_time_sum(String work_time_sum) {
		this.work_time_sum = work_time_sum;
	}
}
