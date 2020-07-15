package cn.hylexus.jt808.vo.req;

/**
 * OBD本次结束时上报0x09
 * @author 王成阳
 * 2018-3-5
 */
public class ObdOverDataRpt {
	
	//平均车速(100m/h)
	//2byte
	private int averageSpeed;
	//平均油耗(L/100km)
	//1byte
	private int averageFuelConsumption;
	//本次行驶里程(km)
	//4byte
	private int travelMileage;
	//本次耗油量(0.1L)
	//4byte
	private int oilConsumption;
	//预留 
	//8byte
	private String reserved;
	
	public ObdOverDataRpt(){
		
	}

	public int getAverageSpeed() {
		return averageSpeed;
	}

	public void setAverageSpeed(int averageSpeed) {
		this.averageSpeed = averageSpeed;
	}

	public int getAverageFuelConsumption() {
		return averageFuelConsumption;
	}

	public void setAverageFuelConsumption(int averageFuelConsumption) {
		this.averageFuelConsumption = averageFuelConsumption;
	}

	public int getTravelMileage() {
		return travelMileage;
	}

	public void setTravelMileage(int travelMileage) {
		this.travelMileage = travelMileage;
	}

	public int getOilConsumption() {
		return oilConsumption;
	}

	public void setOilConsumption(int oilConsumption) {
		this.oilConsumption = oilConsumption;
	}

	public String getReserved() {
		return reserved;
	}

	public void setReserved(String reserved) {
		this.reserved = reserved;
	}
	
	@Override
	public String toString() {
		return "ObdOverDataRpt [averageSpeed=" + averageSpeed + ",  averageFuelConsumption=" + averageFuelConsumption + ",  travelMileage=" + travelMileage +",  oilConsumption=" + oilConsumption +",  reserved=" + reserved +"]";
	}
	
}
