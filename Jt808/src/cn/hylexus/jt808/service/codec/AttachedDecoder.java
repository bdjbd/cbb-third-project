package cn.hylexus.jt808.service.codec;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hylexus.jt808.util.HexStringUtils;
import cn.hylexus.jt808.vo.LocationInfoAttachedAnalysis;
import cn.hylexus.jt808.vo.req.LocationInfoUploadMsg;
import cn.hylexus.jt808.vo.req.ObdOverDataRpt;
import cn.hylexus.jt808.vo.req.ObdRealTimeDataRpt;
import cn.hylexus.jt808.vo.req.ReserveAdditionalRpt;
/**
 * 位置上报附加协议解析
 * 0x01 里程，DWORD，1/10km，对应车上里程表读数				[28-33]			4
 * 0x02 油量，WORD，1/10L，对应车上油量表读数				    [34-37]			2
 * 0x03 行驶记录功能获取的速度，WORD，1/10km/h			    	[38-41]			2
 * 0x04 需要人工确认报警事件的ID，WORD，从 1 开始计数			[42-45]			2
 * 0x08 OBD实时上报数据										[46-72]			25
 * 0x09 OBD本次结束时上报									[73-93]			19
 * 0x10 选择预留的附加信息									[94-126]		31
 * @author 王成阳
 * 2018-3-6
 */ 
public class AttachedDecoder {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	private MsgDecoder msgDecoder;
	private ObdRealTimeDataRpt obdRealTimeDataRpt; 
	private ReserveAdditionalRpt reserveAdditionalRpt; 
	private ObdOverDataRpt obdOverDataRpt ;
  
	public AttachedDecoder() {
		this.msgDecoder = new MsgDecoder();
		this.obdRealTimeDataRpt = new ObdRealTimeDataRpt();
		this.reserveAdditionalRpt = new ReserveAdditionalRpt();
		this.obdOverDataRpt= new ObdOverDataRpt();
	}
	
	/**
	 * 附加协议解析
	 * @param  locationInfoAttachedAnalysis 附加协议解析对象
	 * @return LocationInfoUploadMsg
	 */
	public LocationInfoUploadMsg toDecoder(LocationInfoAttachedAnalysis locationInfoAttachedAnalysis , LocationInfoUploadMsg locationInfoUploadMsg) {
		
		logger.debug(">>>>>[进入附加协议解析类输出id] = " + locationInfoAttachedAnalysis.getId());
		
		//用于打印16进制的字符串
		String testMileage = "";
		String testOilVolume = "";
		String testGetSpeed = "";
		
		String id = locationInfoAttachedAnalysis.getId();
		byte [] content = locationInfoAttachedAnalysis.getContent();
		
		switch (id) {
			//里程
			case "1":
					int mileage = 0;
					
					if (content.length == 4) {
						mileage = msgDecoder.parseIntFromBytes(content, 0, 4)/10;
					}
					//打印16进制
					testMileage  = HexStringUtils.toHexString(HexStringUtils.parseFromBytes(content, 0, 4));
					
					locationInfoUploadMsg.setMileage(mileage) ;	
					locationInfoUploadMsg.setType("mileage");
					//System.err.println("[里程 (16)] = " + testMileage);
					logger.debug("[里程] = " + mileage);
		break;
			//油量
			case "2":
					int oilVolume = 0 ;
					
					if (content.length == 2) {
						oilVolume = msgDecoder.parseIntFromBytes(content, 0, 2)/10;
					}
					
					//打印16进制
					testOilVolume = HexStringUtils.toHexString(HexStringUtils.parseFromBytes(content, 0, 2));
					
					locationInfoUploadMsg.setOilVolume(oilVolume);
					locationInfoUploadMsg.setType("oilVolume");
//					System.err.println("<<<<<<<<<<<<<<<<<<油耗(16) = " + testOilVolume);
					logger.debug("[油耗] = " + oilVolume);
		break;
		 	//行驶记录功能获取的速度
			case "3":
					int getSpeed = 0 ;
					
					if (content.length == 2 ) {
						getSpeed = msgDecoder.parseIntFromBytes(content, 0, 2)/10;
					}
					
					//打印16进制
					testGetSpeed = HexStringUtils.toHexString(HexStringUtils.parseFromBytes(content, 0, 2));
					
					locationInfoUploadMsg.setGetSpeed(getSpeed);
					locationInfoUploadMsg.setType("getSpeed");
//					System.err.println("<<<<<<<<<<<<<<<<<<行驶记录功能获取的速度(16) = " + testGetSpeed);
					System.err.println("[行驶记录功能获取的速度] = " + getSpeed);
						
		break;
			//需要人工确认报警事件的ID
			case "4":
					int confirmAlarm = 0;
					
					if (content.length ==2) {
						confirmAlarm = msgDecoder.parseIntFromBytes(content, 0, 2);	
					}
					
					locationInfoUploadMsg.setConfirmAlarm(String.valueOf(confirmAlarm));
					locationInfoUploadMsg.setType("confirmAlarm");
					logger.debug("[需要人工确认报警事件的ID] = " + confirmAlarm);
		break;
			//OBD实时上报数据
			case "8":
					obdRealTimeDataRpt = toObdObdRealTimeDataRpt(content);
					locationInfoUploadMsg.setObdRealTimeDataRpt(obdRealTimeDataRpt);
					locationInfoUploadMsg.setType("obdRealTimeDataRpt");
					logger.debug("[OBD实时上报数据] = " + obdRealTimeDataRpt);
					
		break;
			//OBD本次结束时上报
			case "9":
					obdOverDataRpt = toObdOverDataRpt(content);
					locationInfoUploadMsg.setObdOverDataRpt(obdOverDataRpt);
					locationInfoUploadMsg.setType("obdOverDataRpt");
					logger.debug("[OBD本次结束时上报] = " + obdOverDataRpt);
		break;
			//选择预留的附加信息
			case "10":
					reserveAdditionalRpt = toReserveAdditionalRpt(content);
					locationInfoUploadMsg.setReserveAdditionalRpt(reserveAdditionalRpt);
					locationInfoUploadMsg.setType("reserveAdditionalRpt");
					System.err.println("[选择预留的附加信息] = " + reserveAdditionalRpt);
		break;			
			case "0":
					locationInfoUploadMsg.setType("Other");
		break;
		
		}
		return locationInfoUploadMsg;
	}
	
	/**
	 * OBD实时上报数据0x08
	 * 水平电压      		100mV				[0]				1byte
	 * 发动机转速 		rpm1byte			[1-2]			2byte
	 * 瞬时车速 			100m/h			[3-4]			2byte
	 * 节气门开度       		%				[5]				1byte
	 * 发动机负荷		%					[6] 			1byte
	 * 冷却液温度		度					[7] 			1byte
	 * 瞬时油耗			L/100km			[8]				1byte
	 * 总里程			km					[9-12]			4byte
	 * 累计油耗量		0.1L				[13-16]			4byte
	 * 预留								[17-24]			8byte
	 * @param content 附加协议内容
	 * @return ObdRealTimeDataRpt
	 */
	public ObdRealTimeDataRpt toObdObdRealTimeDataRpt(byte [] content) {
		
		if (content.length == 25) {
			//水平电压
			int levelVoltage = msgDecoder.parseIntFromBytes(content, 0, 1);
			obdRealTimeDataRpt.setLevelVoltage(levelVoltage);
			//发动机转速
			int engineSpeed = msgDecoder.parseIntFromBytes(content, 1, 2);
			obdRealTimeDataRpt.setEngineSpeed(engineSpeed);
			//瞬时车速
			int instantaneousSpeed = msgDecoder.parseIntFromBytes(content, 3, 2);
			obdRealTimeDataRpt.setInstantaneousSpeed(instantaneousSpeed);
			//节气门开度
			int throttleOpening = msgDecoder.parseIntFromBytes(content, 5, 1);
			obdRealTimeDataRpt.setThrottleOpening(throttleOpening);
			//发动机负荷
			int engineLoad = msgDecoder.parseIntFromBytes(content, 6, 1);
			obdRealTimeDataRpt.setEngineLoad(engineLoad);
			//冷却液温度
			int coolantTemperature = msgDecoder.parseIntFromBytes(content, 7,1);
			obdRealTimeDataRpt.setCoolantTemperature(coolantTemperature);
			//瞬时油耗
			int instFuelConsumption = msgDecoder.parseIntFromBytes(content, 8, 1);
			obdRealTimeDataRpt.setInstFuelConsumption(instFuelConsumption);
			//总里程
			int totalMileage = msgDecoder.parseIntFromBytes(content, 9, 4);
			obdRealTimeDataRpt.setTotalMileage(totalMileage);
			//累计油耗量
			int accOilConsumption = msgDecoder.parseIntFromBytes(content, 13, 4);
			obdRealTimeDataRpt.setAccOilConsumption(accOilConsumption);
			//预留
			String reserved = msgDecoder.parseStringFromBytes(content, 17, 8);
			obdRealTimeDataRpt.setReserved(reserved);
			
			return obdRealTimeDataRpt;
			
		}else {
			
			return null;
		}
	}
	
	/**
	 * OBD本次结束时上报0x09
	 * 平均车速			100m/h			[0-1]			2byte
	 * 平均油耗			L/100km			[2]				1byte
	 * 本次行驶里程		km					[3-6]			4byte
	 * 本次油耗量		0.1L				[7-10]			4byte
	 * 预留								[11-18]			8byte
	 * @param content 附加协议内容
	 * @return ObdOverDataRpt
	 */
	public ObdOverDataRpt toObdOverDataRpt(byte [] content) {
		
		if (content.length == 19) {
			//平均车速
			int averageSpeed = msgDecoder.parseIntFromBytes(content, 0, 2);
			obdOverDataRpt.setAverageSpeed(averageSpeed);
			//平均油耗
			int averageFuelConsumption = msgDecoder.parseIntFromBytes(content, 2, 1);
			obdOverDataRpt.setAverageFuelConsumption(averageFuelConsumption);
			//本次行驶里程
			int travelMileage = msgDecoder.parseIntFromBytes(content, 3, 4);
			obdOverDataRpt.setTravelMileage(travelMileage);
			//本次油耗量
			int oilConsumption = msgDecoder.parseIntFromBytes(content, 7, 4);
			obdOverDataRpt.setOilConsumption(oilConsumption);
			//预留
			String reserved = msgDecoder.parseStringFromBytes(content, 11, 8);
			obdOverDataRpt.setReserved(reserved);
			
			return obdOverDataRpt;
		}else {
			
			return null;
		}
	}
	
	/**
	 * 选择预留的附加信息0x10
	 * 起始时间			YY-MM-DD-hh-mm-ss	[0-5]				6byte
	 * 时长统计参考源	1:OBD、2:GPS				[6]					1byte		
	 * 本次行驶时长		秒						[7-10]				4byte
	 * 本次静止时长		秒						[11-14]				4byte
	 * 本次急加速次数	次						[15-16]				2byte
	 * 本次急减速次数	次						[17-18]				2byte
	 * 本次油耗量		升						[19-20]				2byte
	 * 本次行驶里程		百米						[21-22]				2byte
	 * 本次急转弯次数	次						[23-24]				2byte
	 * 本次急变道次数	次						[25-26]				2byte
	 * 预留									[27-30]				4byte
	 * @param content 附加协议内容
	 * @return ReserveAdditionalRpt
	 */
	public ReserveAdditionalRpt toReserveAdditionalRpt(byte [] content) {
		
		if (content.length == 31) {
			//起始时间
			String startDateTime = msgDecoder.parseBcdStringFromBytes(content, 0, 6);
			reserveAdditionalRpt.setStartDateTime(startDateTime);
			//时长统计参考源
			int referenceSource = msgDecoder.parseIntFromBytes(content, 6, 1);
			reserveAdditionalRpt.setReferenceSource(referenceSource);
			//本次行驶时长
			int travelDuration = msgDecoder.parseIntFromBytes(content, 7, 4);
			reserveAdditionalRpt.setTravelDuration(travelDuration);
			//本次静止时长
			int resetDuration = msgDecoder.parseIntFromBytes(content, 11, 4);
			reserveAdditionalRpt.setResetDuration(resetDuration);
	        //本次急加速次数
			int rapidAccelerationCnt = msgDecoder.parseIntFromBytes(content, 15, 2);
			reserveAdditionalRpt.setRapidAccelerationCnt(rapidAccelerationCnt);
			//本次急减速次数
			int rapidDecelerationCnt = msgDecoder.parseIntFromBytes(content, 17, 2);
			reserveAdditionalRpt.setRapidDecelerationCnt(rapidDecelerationCnt);
			//本次油耗量
			int oilConsumption = msgDecoder.parseIntFromBytes(content, 19, 2);
			reserveAdditionalRpt.setOilConsumption(oilConsumption);
			//本次行驶里程
			int drivingMileage = msgDecoder.parseIntFromBytes(content, 21, 2);
			reserveAdditionalRpt.setDrivingMileage(drivingMileage);
			//本次急转弯次数
			int sharpTurnCnt = msgDecoder.parseIntFromBytes(content, 23, 2);
			reserveAdditionalRpt.setSharpTurnCnt(sharpTurnCnt);
			//本次急变道次数
			int steepRoadCnt = msgDecoder.parseIntFromBytes(content, 25, 2);
			reserveAdditionalRpt.setSteepRoadCnt(steepRoadCnt);
			//预留
			String reserved = msgDecoder.parseStringFromBytes(content, 27, 4);
			reserveAdditionalRpt.setReserved(reserved);
			
			return reserveAdditionalRpt;
			
		}else {
			
			return null;
		}
	}
}
