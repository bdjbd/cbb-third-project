package cn.hylexus.jt808.service;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hylexus.jt808.service.codec.MsgEncoder;
import cn.hylexus.jt808.util.BitOperator;
import cn.hylexus.jt808.util.HexStringUtils;
import io.netty.channel.Channel;

/**
 * 参数设置消息处理
 * @author 王成阳
 * 2018-3-8
 */
public class TerminalParamSettingMsg extends BaseMsgProcessService{
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	BitOperator bitOperator = new BitOperator();
	MsgEncoder encoder = new MsgEncoder();
	
	/** 
	 * 生成消息并发送
	 * @param cmdContent	命令内容 s
	 * @param channelID		管道ID
	 */
	public void processParamSettingMsg(JSONObject cmdContent , Channel channelID ) {
		
		log.debug(">>>>>>>>>>[参数设置数据处理]= " + cmdContent);
		
		try { 
			
			//终端手机号
			String terminalPhone = cmdContent.getString("DEVICE_SN_NUMBER");
			log.debug("[输出终端手机号] = " + terminalPhone);
			
			//流水号
			int flowId = super.getFlowId(channelID);
			
			// 获得消息体内容
			JSONObject cmdContentObject = new JSONObject(cmdContent.getString("CMDCONTENT"));
			System.err.println("111:" + cmdContentObject);
			JSONArray cmdContentArray = new JSONArray(cmdContentObject.getString("DATA"));
			System.err.println("222:" + cmdContentArray);
			JSONObject content = cmdContentArray.getJSONObject(0);
			System.err.println("333:" + content);
			
			//心跳间隔0x0001
			//int heartiInterval = content.getInt("HEARBEAT_SENT_INTERVAL");
			int heartiInterval = processJsonValueInt(content, "HEARBEAT_SENT_INTERVAL", 30);
			//String heartiInterval1 = "" + heartiInterval;
			log.debug("[心跳间隔] = " + heartiInterval);
			
			//主服务器APN,无线通信拨号访问点。若网络制式为 CDMA，则该处为PPP 拨号号码	0x0010
			String mainServerApn = content.getString("MAIN_SERVER_APN");
			log.debug("[主服务器APN] = " + mainServerApn);
			
			//主服务器地址,IP 或域名0x0013
			String mainServerAddr = content.getString("MAIN_SERVER_IP");
			log.debug("[主服务器地址,IP 或域名] = " + mainServerAddr);
			
			//备份服务器 APN，无线通信拨号访问点0x0014
			String backupServerApn = content.getString("SPARE_SERVER_APN");
			log.debug("[备份服务器 APN，无线通信拨号访问点] = " + backupServerApn);
			
			//备份服务器地址,IP 或域名0x0017
			String backupsServerAddr = content.getString("SPARE_SERVER_IP");
			log.debug("[备份服务器地址,IP 或域名0x0017] =" + backupsServerAddr);
			
			//服务器tcp端口0x0018
			//int tcpPort = content.getInt("SERVER_TCP_PORT");
			int tcpPort = processJsonValueInt(content, "SERVER_TCP_PORT", 8888);
			//String tcpPort1 = "" + tcpPort;
			log.debug("[服务器tcp端口] = " + tcpPort);
			
			//位置汇报策略，0：定时汇报；1：定距汇报；2：定时和定距汇报0x0020
			//int locRptStrategy = content.getInt("POSITION_REPORT_PROGRAM");
			int locRptStrategy = processJsonValueInt(content, "POSITION_REPORT_PROGRAM", 0);
			//String locRptStrategy1 = "" + locRptStrategy;
			log.debug("[位置汇报策略，0：定时汇报；1：定距汇报；2：定时和定距汇报] = " + locRptStrategy);
			
			//驾驶员未登录汇报时间间隔，单位为秒（s），>0  0x0022
			//int driverUnregRptTmIntv = content.getInt("NOT_LOGIN_REPORT_TIME_INTERVAL");
			int driverUnregRptTmIntv = processJsonValueInt(content, "NOT_LOGIN_REPORT_TIME_INTERVAL", 30);
			//String driverUnregRptTmIntv1 = "" + driverUnregRptTmIntv;
			log.debug("[驾驶员未登录汇报时间间隔,单位为秒] = " + driverUnregRptTmIntv);
			
			//休眠时汇报时间间隔，单位为秒（s），>0	0x0027
			//int dormancyRptTmIntv = content.getInt("SLEEP_REPORT_TIME_INTERVAL");
			int dormancyRptTmIntv = processJsonValueInt(content, "SLEEP_REPORT_TIME_INTERVAL", 30);
			//String dormancyRptTmIntv1 = "" + dormancyRptTmIntv;
			log.debug("[眠时汇报时间间隔,单位为秒] = " + dormancyRptTmIntv);
			
			//紧急报警时汇报时间间隔，单位为秒（s），>0 	0x0028
			//int emergAlarmRptTmIntv = content.getInt("ALARM_REPORT_TIME_INTERVAL");
			int emergAlarmRptTmIntv = processJsonValueInt(content, "ALARM_REPORT_TIME_INTERVAL", 30);
			//String emergAlarmRptTmIntv1 = "" + emergAlarmRptTmIntv;
			log.debug("[紧急报警时汇报时间间隔] = " + emergAlarmRptTmIntv);
			
			//缺省时间汇报间隔，单位为秒（s），>0	0x0029
			//int defaultTmRptIntv = content.getInt("DEFUALT_REPORT_TIME_INTERVAL");
			int defaultTmRptIntv = processJsonValueInt(content, "DEFUALT_REPORT_TIME_INTERVAL", 30);
			//String defaultTmRptIntv1 = "" +defaultTmRptIntv;
			log.debug("[缺省时间汇报间隔,单位为秒] = " + defaultTmRptIntv);
			
			//缺省距离汇报间隔，单位为米（m），>0	0x002C
			//int defaultDistRptIntv = content.getInt("DEFUALT_REPORT_DISTANCE");
			int defaultDistRptIntv = processJsonValueInt(content, "DEFUALT_REPORT_DISTANCE", 200);
			//String defaultDistRptIntv1 = "" + defaultDistRptIntv;
			log.debug("[缺省距离汇报间隔,单位为米] = " + defaultDistRptIntv);
			
			//休眠时汇报距离间隔，单位为米（m），>0	0x002E
			//int dormancyRptDistIntv = content.getInt("SLEEP_REPORT_DISTANCE");
			int dormancyRptDistIntv = processJsonValueInt(content, "SLEEP_REPORT_DISTANCE", 200);
			//String dormancyRptDistIntv1 = "" + dormancyRptDistIntv;
			log.debug("[休眠时汇报距离间隔，单位为米] = " + dormancyRptDistIntv);
			
			//复位电话号码，可采用此电话号码拨打终端电话让终端复位	0x0041
			String resetPhoneNum = content.getString("RESET_PHONE");
			log.debug("[复位电话号码，可采用此电话号码拨打终端电话让终端复位] = " + resetPhoneNum);
			
			//最高速度 单位km/h	0x0055
			//int topSpeed = content.getInt("OVER_SPEED_LIMIT");
			int topSpeed = processJsonValueInt(content, "OVER_SPEED_LIMIT", 200);
			//String topSpeed1 = "" + topSpeed;
			log.debug("[最高速度 单位km/h] = " + topSpeed);
			
			//超速持续时间 单位 秒	0x0056
			//int overspeedDuration = content.getInt("TIMEOUT_DURATION");
			int overspeedDuration = processJsonValueInt(content, "TIMEOUT_DURATION", 30);
			//String overspeedDuration1 = "" + overspeedDuration;
			log.debug("[超速持续时间 单位 秒] = " + overspeedDuration);
			
			//连续驾驶时间门限 单位 秒	0x0057
			//int conDrivingTimeThreshold = content.getInt("COTINUOUS_DRIVING_TIME_THRESHOL");
			int conDrivingTimeThreshold = processJsonValueInt(content, "COTINUOUS_DRIVING_TIME_THRESHOL", 30);
			//String conDrivingTimeThreshold1 = "" + conDrivingTimeThreshold;
			log.debug("[连续驾驶时间门限 单位 秒] = " + conDrivingTimeThreshold);
			
			//当天累计驾驶时间门限，单位为秒(s)	0x0058
			//int dayTotalDrivTmThreshold = content.getInt("DAY_DRIVING_TIME_THRESHOLD");
			int dayTotalDrivTmThreshold = processJsonValueInt(content, "DAY_DRIVING_TIME_THRESHOLD", 300);
			//String dayTotalDrivTmThreshold1 = "" + dayTotalDrivTmThreshold;
			log.debug("[当天累计驾驶时间门限,单位为秒] = " + dayTotalDrivTmThreshold);
			
			//最小休息时间，单位为秒(s)		0x0059
			//int minResTm = content.getInt("MINIMUM_REST_TIME");
			int minResTm = processJsonValueInt(content, "MINIMUM_REST_TIME", 30);
			//String minResTm1 = "" + minResTm;
			log.debug("[最小休息时间,单位为秒] = " + minResTm);
			
			//最长停车时间，单位为秒(s)		0x005A
			//int maxParkTm = content.getInt("MAXIMUM_REST_TIME");
			int maxParkTm = processJsonValueInt(content, "MAXIMUM_REST_TIME", 300);
			//String maxParkTm1 = "" + maxParkTm;
			log.debug("[最长停车时间,单位为秒] = " + maxParkTm);
			
			//超速报警预警差值，单位为 1/10Km/h		0x005B
			//int overspeedAlarmDiff = content.getInt("OVERSPEED_ALARM_WARN_DIFFERECE"); 
			int overspeedAlarmDiff = processJsonValueInt(content, "OVERSPEED_ALARM_WARN_DIFFERECE", 1);
			//String overspeedAlarmDiff1 = "" + overspeedAlarmDiff;
			log.debug("[超速报警预警差值,单位为 1/10Km/h] = " + overspeedAlarmDiff);
			
			//疲劳驾驶预警差值，单位为秒（s），>0	0x005C
			//int fatigueDrivWarnDiff = content.getInt("FATIGUE_DRIVING_WARN_DIFFERENCE");
			int fatigueDrivWarnDiff = processJsonValueInt(content, "FATIGUE_DRIVING_WARN_DIFFERENCE", 30);
			//String fatigueDrivWarnDiff1 = "" + fatigueDrivWarnDiff;
			log.debug("[疲劳驾驶预警差值,单位为秒] = " + fatigueDrivWarnDiff);
			
			//车辆里程表读数，1/10km	0x0080
			//int	vehicleOdometerReadings = content.getInt("VEHICLE_ADOMETER_READING");
			int	vehicleOdometerReadings = processJsonValueInt(content, "VEHICLE_ADOMETER_READING", 30);
			//String vehicleOdometerReadings1 = "" + vehicleOdometerReadings;
			log.debug("[车辆里程表读数,1/10km] = " + vehicleOdometerReadings);
			
			//车辆所在的省域 ID	0x0081
			//int vehicleProvinceId = content.getInt("CAR_IN_PROVINCE");
			int vehicleProvinceId = processJsonValueInt(content, "CAR_IN_PROVINCE", 8100);
			//String vehicleProvinceId1 = "" + vehicleProvinceId;
			log.debug("[车辆所在的省域 ID] = " + vehicleProvinceId);
			
			//车辆所在的市域 ID	0x0082
			//int vehicleCityId = content.getInt("CAR_INCITY_ID");
			int vehicleCityId = processJsonValueInt(content, "CAR_INCITY_ID", 8200);
			//String vehicleCityId1 = "" + vehicleCityId;
			log.debug("[车辆所在的市域 ID] = " + vehicleCityId);
			
			//公安交通管理部门颁发的机动车号牌	0x0083	licensePlateNumber
			String licensePlateNumber = content.getString("PLATE_NUMBE");
			logger.debug("[公安交通管理部门颁发的机动车号牌] = " + licensePlateNumber);
			
			//车牌颜色，按照 JT/T415-2006 的 5.4.12	 0x0084
			//int licensePlateColor = content.getInt("LICENSE_PLATE_COLOR");
			int licensePlateColor = processJsonValueInt(content, "LICENSE_PLATE_COLOR", 111);
			//String licensePlateColor1 = "" + licensePlateColor;
			log.debug("[车牌颜色] = " + licensePlateColor);
			
			// 消息体字节数组
			byte[] msgBody = this.bitOperator.concatAll(Arrays.asList(
					//参数总数(含保留位33,不含保留位27) 	1byte
					bitOperator.integerTo1Bytes(27),
					
					// 0x0001	4byte	心跳间隔heartiInterval		 
					bitOperator.integerTo4Bytes(0x0001),
					bitOperator.integerTo1Bytes(4),
					bitOperator.integerTo4Bytes(heartiInterval),
					
					
					// 0x010			主服务器APN无线通信拨号访问点。若网络制式为 CDMA则该处为PPP 拨号号码mainServerApn
					bitOperator.integerTo4Bytes(0x0010),
					bitOperator.integerTo1Bytes(mainServerApn.length()),
					mainServerApn.getBytes(),
					
					// 0x0013	 		 主服务器地址,IP 或域名mainServerAddr
					bitOperator.integerTo4Bytes(0x0013),
					bitOperator.integerTo1Bytes(mainServerAddr.length()),
					mainServerAddr.getBytes(),
					
					// 0x0014	 		 备份服务器 APN无线通信拨号访问点backupServerApn
					bitOperator.integerTo4Bytes(0x0014),
					bitOperator.integerTo1Bytes(backupServerApn.length()),
					backupServerApn.getBytes(),
					

					// 0x0017	 		 备份服务器地址,IP 或域名backupsServerAddr
					bitOperator.integerTo4Bytes(0x0017),
					bitOperator.integerTo1Bytes(backupsServerAddr.length()),
					backupsServerAddr.getBytes(),
					
					// 0x0018	4byte	 服务器tcp端口tcpPort
					bitOperator.integerTo4Bytes(0x0018),
					bitOperator.integerTo1Bytes(4),
					bitOperator.integerTo4Bytes(tcpPort),
					
					// 0x0020	4byte	 位置汇报策略0：定时汇报；1：定距汇报；2：定时和定距汇报locRptStrategy
					bitOperator.integerTo4Bytes(0x0020),
					bitOperator.integerTo1Bytes(4),
					bitOperator.integerTo4Bytes(locRptStrategy),
					
					// 0x0022	4byte 	 驾驶员未登录汇报时间间隔单位为秒（s）>0 driverUnregRptTmIntv
					bitOperator.integerTo4Bytes(0x0022),
					bitOperator.integerTo1Bytes(4),
					bitOperator.integerTo4Bytes(driverUnregRptTmIntv),
					
					// 0x0027	4byte	 休眠时汇报时间间隔单位为秒（s）>0 dormancyRptTmIntv
					bitOperator.integerTo4Bytes(0x0027),
					bitOperator.integerTo1Bytes(4),
					bitOperator.integerTo4Bytes(dormancyRptTmIntv),
					
					// 0x0028	4byte	 紧急报警时汇报时间间隔单位为秒（s）>0 emergAlarmRptTmIntv
					bitOperator.integerTo4Bytes(0x0028),
					bitOperator.integerTo1Bytes(4),
					bitOperator.integerTo4Bytes(emergAlarmRptTmIntv),
					
					// 0x0029	4byte	 缺省时间汇报间隔单位为秒（s）>0 defaultTmRptIntv
					bitOperator.integerTo4Bytes(0x0029),
					bitOperator.integerTo1Bytes(4),
					bitOperator.integerTo4Bytes(defaultTmRptIntv),
					
					// 0x002C	4byte	 缺省距离汇报间隔单位为米（m）>0 defaultDistRptIntv
					bitOperator.integerTo4Bytes(0x002C),
					bitOperator.integerTo1Bytes(4),
					bitOperator.integerTo4Bytes(defaultDistRptIntv),
					
					// 0x002E	4byte	 休眠时汇报距离间隔单位为米（m）>0	 dormancyRptDistIntv
					bitOperator.integerTo4Bytes(0x002E),
					bitOperator.integerTo1Bytes(4),
					bitOperator.integerTo4Bytes(dormancyRptDistIntv),
					
					// 0x0041	 		 复位电话号码可采用此电话号码拨打终端电话让终端复位 resetPhoneNum
					bitOperator.integerTo4Bytes(0x0041),
					bitOperator.integerTo1Bytes(resetPhoneNum.length()),
					resetPhoneNum.getBytes(),
					
					// 0x0055	4byte	 最高速度 单位km/h topSpeed
					bitOperator.integerTo4Bytes(0x0055),
					bitOperator.integerTo1Bytes(4),
					bitOperator.integerTo4Bytes(topSpeed),
					
					// 0x0056	4byte	 超速持续时间 单位 秒 overspeedDuration
					bitOperator.integerTo4Bytes(0x0056),
					bitOperator.integerTo1Bytes(4),
					bitOperator.integerTo4Bytes(overspeedDuration),
					
					// 0x0057	4byte 	 连续驾驶时间门限 单位 秒 conDrivingTimeThreshold
					bitOperator.integerTo4Bytes(0x0057),
					bitOperator.integerTo1Bytes(4),
					bitOperator.integerTo4Bytes(conDrivingTimeThreshold),
					
					// 0x0058	4byte 	 当天累计驾驶时间门限单位为秒（s）	dayTotalDrivTmThreshold	
					bitOperator.integerTo4Bytes(0x0058),
					bitOperator.integerTo1Bytes(4),
					bitOperator.integerTo4Bytes(dayTotalDrivTmThreshold),
					
					// 0x0059	4byte 	 最小休息时间单位为秒（s） minResTm
					bitOperator.integerTo4Bytes(0x0059),
					bitOperator.integerTo1Bytes(4),
					bitOperator.integerTo4Bytes(minResTm),
					
					// 0x005A	4byte 	 最长停车时间单位为秒（s）maxParkTm
					bitOperator.integerTo4Bytes(0x005A),
					bitOperator.integerTo1Bytes(4),
					bitOperator.integerTo4Bytes(maxParkTm),
					
					// 0x005B	2byte 	 超速报警预警差值单位为 1/10Km/h overspeedAlarmDiff
					bitOperator.integerTo4Bytes(0x005B),
					bitOperator.integerTo1Bytes(2),
					bitOperator.integerTo2Bytes(overspeedAlarmDiff),
					
					// 0x005C	2byte 	 疲劳驾驶预警差值单位为秒（s）>0 fatigueDrivWarnDiff
					bitOperator.integerTo4Bytes(0x005C),
					bitOperator.integerTo1Bytes(2),
					bitOperator.integerTo2Bytes(fatigueDrivWarnDiff),
					
					// 0x0080	4byte	 车辆里程表读数1/10km vehicleOdometerReadings
					bitOperator.integerTo4Bytes(0x0080),
					bitOperator.integerTo1Bytes(4),
					bitOperator.integerTo4Bytes(vehicleOdometerReadings),
					
					// 0x0081	2byte	 车辆所在的省域 ID vehicleProvinceId		
					bitOperator.integerTo4Bytes(0x0081),
					bitOperator.integerTo1Bytes(2),
					bitOperator.integerTo2Bytes(vehicleProvinceId),
					
					// 0x0082	2byte	 车辆所在的市域 ID vehicleCityId
					bitOperator.integerTo4Bytes(0x0082),
					bitOperator.integerTo1Bytes(2),
					bitOperator.integerTo2Bytes(vehicleCityId),
					
					// 0x0083			 公安交通管理部门颁发的机动车号牌 licensePlateNumber
					bitOperator.integerTo4Bytes(0x0083),
					bitOperator.integerTo1Bytes(licensePlateNumber.length()),
					licensePlateNumber.getBytes(),
					
					// 0x0084	1byte	 车牌颜色按照 JT/T415-2006 的 5.4.12  licensePlateColor
					bitOperator.integerTo4Bytes(0x0084),
					bitOperator.integerTo1Bytes(1),
					bitOperator.integerTo1Bytes(licensePlateColor)
					
					//reserve1	
//					bitOperator.integerTo4Bytes(30),
//					bitOperator.integerTo1Bytes(0),
//					bitOperator.integerTo4Bytes(0),
					
//					//reserve2 
//					bitOperator.integerTo4Bytes(31),
//					bitOperator.integerTo1Bytes(0),
//					bitOperator.integerTo4Bytes(0),
					
//					//reserve3 
//					bitOperator.integerTo4Bytes(32),
//					bitOperator.integerTo1Bytes("".length()),
//					"".getBytes(),
					
//					//reserve4 
//					bitOperator.integerTo4Bytes(33),
//					bitOperator.integerTo1Bytes("".length()),
//					"".getBytes(),
					
//					//reserve5 
//					paramItemDouble.setId(34);
//					paramItemDouble.setValue(0);
//					paramConf.setReserve5(paramItemDouble);
					
//					//reserve6
//					paramItemDouble.setId(35);
//					paramItemDouble.setValue(0);
//					paramConf.setReserve6(paramItemDouble);
			));
			
			  //编码产生新的报文
			  byte[] bs = encoder.encode4ParamSetting(msgBody, terminalPhone, flowId);
			  
			  log.debug("<<<<<<<<<<[下发参数设置协议] = " + HexStringUtils.toHexString(bs));
			  
			  //发送终端
	          super.send2Client(channelID, bs);	
			
			} catch (Exception e) {
		}

	}
	
	/**
	 * json对象value为数字时判空并给定默认值
	 * @param content				JSON对象
	 * @param paramName				需要判空的参数名			
	 * @param defaultValue			给定的默认值
	 * @return defaultValue			给定默认值
	 * @throws JSONException 
	 */
	private int processJsonValueInt(JSONObject content , String paramName , int defaultValue){
		
		try {
			if (!"".equals(content.getString(paramName))) {
				defaultValue = content.getInt(paramName);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return defaultValue;
	}
	
}
