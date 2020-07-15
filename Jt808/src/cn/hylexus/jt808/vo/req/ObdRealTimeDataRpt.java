package cn.hylexus.jt808.vo.req;

/**
 * OBD实时上报数据0x08
 * @author 王成阳
 * 2018-3-5
 */
public class ObdRealTimeDataRpt {
	
	//电平电压(100mV)
	//1byte
	private int levelVoltage;
	//发动机转速(rpm) 
	//2byte
	private int engineSpeed;
	//瞬时车速(100m/h)
	//2byte
	private int instantaneousSpeed;
	//节气门开度(%) 
	//1byte
	private int throttleOpening;
	//发动机负荷(%) 
	//1byte
	private int engineLoad;
	//冷却液温度(度) 
	//1byte
	private int coolantTemperature;
	//瞬时油耗(L/100km) 
	//1byte
	private int instFuelConsumption;
	//总里程(km) 
	//4byte
	private int totalMileage;
	//累计油耗(0.1L) 
	//4byte
	private int accOilConsumption;
	//预留 
	//8byte
	private String reserved;
	
	public ObdRealTimeDataRpt() {
		
	}

	public int getLevelVoltage() {
		return levelVoltage;
	}

	public void setLevelVoltage(int levelVoltage) {
		this.levelVoltage = levelVoltage;
	}

	public int getEngineSpeed() {
		return engineSpeed;
	}

	public void setEngineSpeed(int engineSpeed) {
		this.engineSpeed = engineSpeed;
	}

	public int getInstantaneousSpeed() {
		return instantaneousSpeed;
	}

	public void setInstantaneousSpeed(int instantaneousSpeed) {
		this.instantaneousSpeed = instantaneousSpeed;
	}

	public int getThrottleOpening() {
		return throttleOpening;
	}

	public void setThrottleOpening(int throttleOpening) {
		this.throttleOpening = throttleOpening;
	}

	public int getEngineLoad() {
		return engineLoad;
	}

	public void setEngineLoad(int engineLoad) {
		this.engineLoad = engineLoad;
	}

	public int getCoolantTemperature() {
		return coolantTemperature;
	}

	public void setCoolantTemperature(int coolantTemperature) {
		this.coolantTemperature = coolantTemperature;
	}

	public int getInstFuelConsumption() {
		return instFuelConsumption;
	}

	public void setInstFuelConsumption(int instFuelConsumption) {
		this.instFuelConsumption = instFuelConsumption;
	}

	public int getTotalMileage() {
		return totalMileage;
	}

	public void setTotalMileage(int totalMileage) {
		this.totalMileage = totalMileage;
	}

	public int getAccOilConsumption() {
		return accOilConsumption;
	}

	public void setAccOilConsumption(int accOilConsumption) {
		this.accOilConsumption = accOilConsumption;
	}

	public String getReserved() {
		return reserved;
	}

	public void setReserved(String reserved) {
		this.reserved = reserved;
	}
	
	@Override
	public String toString() {
		return "ObdRealTimeDataRpt [levelVoltage=" + levelVoltage + ",  engineSpeed=" + engineSpeed + ",  instantaneousSpeed=" + instantaneousSpeed + ",  throttleOpening=" + throttleOpening + ",  engineLoad=" + engineLoad + ",  coolantTemperature=" + coolantTemperature + ",  instFuelConsumption=" + instFuelConsumption + ",  totalMileage=" + totalMileage + ",  accOilConsumption=" + accOilConsumption + ",  reserved=" + reserved + "]";
	}
	
}
