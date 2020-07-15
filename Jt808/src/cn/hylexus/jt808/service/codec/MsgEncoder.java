package cn.hylexus.jt808.service.codec;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hylexus.jt808.common.TPMSConsts;
import cn.hylexus.jt808.util.BitOperator;
import cn.hylexus.jt808.util.HexStringUtils;
import cn.hylexus.jt808.util.JT808ProtocolUtils;
import cn.hylexus.jt808.vo.PackageData;
import cn.hylexus.jt808.vo.req.TerminalRegisterMsg;
import cn.hylexus.jt808.vo.resp.ServerCommonRespMsgBody;
import cn.hylexus.jt808.vo.resp.TerminalRegisterMsgRespBody;
import cn.hylexus.jt808.vo.resp.TerminalUpgradeMsgBody;

/**
 * 编码器
 */
public class MsgEncoder {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	private BitOperator bitOperator;
	private JT808ProtocolUtils jt808ProtocolUtils;
	
	public MsgEncoder() {
		this.bitOperator = new BitOperator();
		this.jt808ProtocolUtils = new JT808ProtocolUtils();
	}
	 
	/**
	 * 终端注册应答编码
	 * @param req				终端注册请求消息体
	 * @param respMsgBody		终端注册应答消息体
	 * @param flowId			应答流水号
	 * @return					终端注册应答协议
	 * @throws Exception
	 */
	public byte[] encode4TerminalRegisterResp(TerminalRegisterMsg req, TerminalRegisterMsgRespBody respMsgBody,int flowId) throws Exception {
		// 消息体字节数组
		byte[] msgBody = null;
		// 鉴权码(STRING) 只有在成功后才有该字段
		if (respMsgBody.getReplyCode() == TerminalRegisterMsgRespBody.success) {
			//System.err.println("鉴权成功");
			msgBody = this.bitOperator.concatAll(Arrays.asList(//
//					bitOperator.integerTo2Bytes(respMsgBody.getReplyFlowId()), // 流水号(2)
//					new byte[] { respMsgBody.getReplyCode() }, // 结果
//					respMsgBody.getReplyToken().getBytes(TPMSConsts.string_charset)// 鉴权码(STRING)

					bitOperator.integerTo2Bytes(respMsgBody.getReplyFlowId()),
					bitOperator.integerTo1Bytes(respMsgBody.getReplyCode()),
					respMsgBody.getReplyToken().getBytes(TPMSConsts.string_charset)// 鉴权码(STRING)
			));
		} else {
			System.err.println("鉴权失败");
			msgBody = this.bitOperator.concatAll(Arrays.asList(//
//					bitOperator.integerTo2Bytes(respMsgBody.getReplyFlowId()), // 流水号(2)
//					new byte[] { respMsgBody.getReplyCode() }// 错误代码
					
					bitOperator.integerTo2Bytes(respMsgBody.getReplyFlowId()),
					bitOperator.integerTo1Bytes(respMsgBody.getReplyCode())
			));
		}

		// 消息头和消息体
		int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(msgBody.length, 0b000, false, 0);
		byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader(req.getMsgHeader().getTerminalPhone(),TPMSConsts.cmd_terminal_register_resp, msgBody, msgBodyProps, flowId);
		byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBody);

		// 校验码
		//int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
		int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length);
		// 连接并且转义
		return this.doEncode(headerAndBody, checkSum);
	}
	
	/**
	 * 平台通用应答
	 * @param req					协议请求消息体					
	 * @param respMsgBody			通用应答消息体			
	 * @param flowId				应答流水号
	 * @return						通用应答协议
	 * @throws Exception
	 */
	// public byte[] encode4ServerCommonRespMsg(TerminalAuthenticationMsg req,
	// ServerCommonRespMsgBody respMsgBody, int flowId) throws Exception {
	public byte[] encode4ServerCommonRespMsg(PackageData req, ServerCommonRespMsgBody respMsgBody, int flowId) throws Exception {
		byte[] msgBody = this.bitOperator.concatAll(Arrays.asList(//
				bitOperator.integerTo2Bytes(respMsgBody.getReplyFlowId()), // 应答流水号
				bitOperator.integerTo2Bytes(respMsgBody.getReplyId()), // 应答ID,对应的终端消息的ID
				//new byte[] { respMsgBody.getReplyCode() }// 结果
				bitOperator.integerTo1Bytes(respMsgBody.getReplyCode())
				
		));
		
//		System.err.println("输出应答流水号 = " + respMsgBody.getReplyFlowId());
//		System.err.println("输出应答ID = " + respMsgBody.getReplyId());
//		System.err.println("输出结果 = " + respMsgBody.getReplyCode());
		
		// 消息头
		int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(msgBody.length, 0b000, false, 0);
		byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader(req.getMsgHeader().getTerminalPhone(),TPMSConsts.cmd_common_resp, msgBody, msgBodyProps, flowId);
//		System.err.println("输出应答流水号 =" + flowId);
		byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBody);
		// 校验码
		int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length);
		// 连接并且转义
		return this.doEncode(headerAndBody, checkSum);
	}
	
	/**
	 * 终端升级第一包
	 * @param respMsgBody		消息体
	 * @param flowId			流水号
	 * @param terminalPhone		终端手机号
	 * @param totalPackages		总包数
	 * @param currentPackage	当前包数
	 * @return
	 * @throws Exception
	 */
	public byte[] encode4TerminalUpgradeRespOne(TerminalUpgradeMsgBody terminalUpgradeMsgBody , int flowId , String terminalPhone , int totalPackages , int currentPackage) throws Exception {
		
		// 消息体字节数组
		byte[] msgBody = this.bitOperator.concatAll(Arrays.asList(
				bitOperator.integerTo1Bytes(terminalUpgradeMsgBody.getUpgradeType()), 			// 升级类型(0：终端，12：道路运输证 IC 卡读卡器，52：北斗卫星定位模块) 
				bitOperator.integerTo5Bytes(terminalUpgradeMsgBody.getManufacturerID()),		// 制造商ID
				bitOperator.integerTo1Bytes(terminalUpgradeMsgBody.getVersionNumLength()),		// 版本号长度
				terminalUpgradeMsgBody.getVersionNumber().getBytes(),							// 版本号
				bitOperator.integerTo4Bytes(terminalUpgradeMsgBody.getUpgradeDataLength()),		// 升级数据包长度
				terminalUpgradeMsgBody.getUpgradeData()											// 升级数据包
		));

		// 消息头和消息体
		int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(msgBody.length, 0b000, true, 0);
		byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader(terminalPhone, TPMSConsts.cmd_terminal_upgrade, msgBody, msgBodyProps, flowId, totalPackages, currentPackage);
		byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBody);
		
		// 校验码
		int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length);
		
		//logger.debug("检验码 = " + checkSum);
		//logger.debug("转义前的消息 = " + HexStringUtils.toHexString(headerAndBody));
		
		// 连接并且转义
		return this.doEncode(headerAndBody, checkSum);
	}
	
	/**
	 * 终端升级剩余包
	 * @param respMsgBody		消息体
	 * @param flowId			流水号
	 * @param terminalPhone		终端手机号
	 * @param totalPackages		总包数
	 * @param currentPackage	当前包数
	 * @return
	 * @throws Exception
	 */
	public byte[] encode4TerminalUpgradeRespOther(TerminalUpgradeMsgBody terminalUpgradeMsgBody , int flowId , String terminalPhone , int totalPackages , int currentPackage) throws Exception {
		
		// 消息体字节数组
		byte[] msgBody = this.bitOperator.concatAll(Arrays.asList(
				terminalUpgradeMsgBody.getUpgradeData()	// 升级数据包
		));

		// 消息头和消息体
		int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(msgBody.length, 0b000, true, 0);
		byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader(terminalPhone, TPMSConsts.cmd_terminal_upgrade, msgBody, msgBodyProps, flowId, totalPackages, currentPackage);
		byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBody);
		
		// 校验码
		int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length);
		
		//logger.debug("检验码 = " + checkSum);
		//logger.debug("转义前的消息 = " + HexStringUtils.toHexString(headerAndBody));
		
		
		// 连接并且转义
		return this.doEncode(headerAndBody, checkSum);
	}
	
	
	
	/**
	 * 终端参数设置
	 * @param msgBodyBytes		消息体数组
	 * @param terminalPhone		终端手机号
	 * @param flowId			流水号
	 * @return
	 * @throws Exception
	 */
	public byte[] encode4ParamSetting(byte[] msgBodyBytes, String terminalPhone, int flowId) throws Exception {
		
		// 消息头和消息体
		int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(msgBodyBytes.length, 0b000, false, 0);
		byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader(terminalPhone,TPMSConsts.cmd_terminal_param_settings, msgBodyBytes, msgBodyProps, flowId);
		byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBodyBytes);
		
		//System.err.println("输出消息头和消息体 = " + HexStringUtils.toHexString(headerAndBody));
		
		// 校验码
		int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length);

		//System.err.println("输出校验码 = " +  checkSum);
		
		// 连接并且转义
		return this.doEncode(headerAndBody, checkSum);
	}
	
	/**
	 * 连接并转译
	 * @param headerAndBody
	 * @param checkSum
	 * @return
	 * @throws Exception
	 */
	private byte[] doEncode(byte[] headerAndBody, int checkSum) throws Exception {
		
		byte[] noEscapedBytes = this.bitOperator.concatAll(Arrays.asList(
				new byte[] { TPMSConsts.pkg_delimiter }, 		// 0x7e
				headerAndBody, 									// 消息头+ 消息体
				bitOperator.integerTo1Bytes(checkSum), 			// 校验码
				new byte[] { TPMSConsts.pkg_delimiter }			// 0x7e
		));
		
		logger.debug("输出校验码 = " + HexStringUtils.toHexString(bitOperator.integerTo1Bytes(checkSum)));
		
		// 转义
		return jt808ProtocolUtils.doEscape4Send(noEscapedBytes, 1, noEscapedBytes.length - 1);
	}
	
	/**
	 * 构建终端位置上报测试协议
	 * @param req				终端心跳请求消息
	 * @param flowId			应答流水号
	 * @return					终端位置上报测试协议
	 * @throws Exception
	 */
	public byte[] createTestLocationInfoUploadMsg(PackageData req,int flowId) throws Exception {
		//====================位置记本信息begin====================
		// 1、byte4 			报警标志			DWORD			int 		warningFlagField
		int warningFlagField = 10;
		// 2、byte4 			状态				DWORD32			int			statusField
		int statusField = 10;
		// 3、byte4 			纬度				DWORD32 		float		latitude
		byte [] latitude = new byte[4];
		// 4、byte4			经度				DWORD32			float 		longitude
		byte [] longitude = new byte[4];
		// 5、byte2 			高程				WORD16 			int			elevation
		int elevation = 100;
		// 6、byte2 			速度				WORD 			float		speed
		byte [] speed = new byte[2];
		// 7、byte2 			方向				WORD			int			direction
		int direction = 100;
		// 8、byte6 			时间				BCD[6]			Date		time	
		String time = "201803";
		//====================位置基本信息end====================
		
		//====================位置附加信息项格式begin====================
		// byte1	附加信息ID		BYTE		String
		// byte1	附加信息长度		BYTE		int
		// 			附加信息  
		
		// 0x01		byte4	      	【里程】							int			mileage
		int mileage = 300;
		// 0x02     	byte2	        【油量】							int			oilVolume
		int oilVolume = 100;
		// 0x03		byte2	      	【行驶记录功能获取的速度】		int			getSpeed
		int getSpeed = 300;
		// 0x04		byte2	      	【报警事件ID】					int			confirmAlarm
		int confirmAlarm = 100;
		
		// 0x08		    byte25	      【OBD实时上报】								obdRealTimeDataRpt
		
		//				byte1		电平电压							int			levelVoltage
		int levelVoltage = 100;
		//				byte2		发动机转速						int			engineSpeed
		int engineSpeed = 1000;
		//				byte2		瞬时车速							int			instantaneousSpeed
		int instantaneousSpeed = 100;
		//				byte1		节气门开度						int			throttleOpening
		int throttleOpening = 100;
		//				byte1		发动机负荷						int			engineLoad
		int engineLoad = 100;
		//				byte1		冷却液温度						int			coolantTemperature
		int coolantTemperature = 100;
		//				byte1		瞬时油耗							int			instFuelConsumption
		int instFuelConsumption = 100;
		//				byte4		总里程							int			totalMileage
		int totalMileage = 100;
		//				byte4		累计油耗量						int			accOilConsumption
		int accOilConsumption = 100;
		// 				byte8		预留								String		reserved
		String obdRealTimeDataRptReserved = "00000000";
		
		// 0x09		byte19                   【OBD本次结束时上报】							obdOverDataRpt
		
		//				byte2		平均车速							int			averageSpeed
		int averageSpeed = 100;
		//				byte1		平均油耗							int			averageFuelConsumption
		int averageFuelConsumption = 100;
		//				byte4		本次行驶里程						int			travelMileage
		int travelMileage = 100;
		//				byte4		本次油耗量						int			oilConsumption
		int obdOverDataRptOilConsumption = 60;
		//				byte8		预留								String		reserved
		String obdOverDataRptReserved = "00000000";
		
		// 0x10		    byte31           【选择预留的附加信息】						reserveAdditionalRpt
		
		//				byte6		起始时间							String		startDateTime		
		String startDateTime = "please";
		//				byte1		时长统计参考源					int			referenceSource
		int referenceSource = 100;
		//				byte4		本次行驶时长						int			travelDuration
		int travelDuration = 30;
		//				byte4		本次静止时长						int			resetDuration
		int resetDuration = 30;
		//				byte2		本次急加速次数					int			rapidAccelerationCnt
		int rapidAccelerationCnt = 10;
		//				byte2		本次急减速次数					int			rapidDecelerationCnt
		int rapidDecelerationCnt = 20;
		//				byte2		本次油耗量						int			oilConsumption
		int reserveAdditionalRptOilConsumption = 200;
		//				byte2		本次行驶里程						int			drivingMileage
		int drivingMileage = 200;
		//				byte2		本次急转弯次数					int			sharpTurnCnt
		int sharpTurnCnt = 5;
		//				byte2		本次急变道次数					int			steepRoadCnt
		int steepRoadCnt = 6;
		//				byte4		预留								String		reserved
		String reserveAdditionalRptReserved = "0000";
		//====================位置附加信息项格式end====================	
		
		
		// 消息体字节数组
		byte[] msgBody = this.bitOperator.concatAll(Arrays.asList(
				//====================位置记本信息begin====================
				// 1、byte4 			报警标志			DWORD			int 		warningFlagField	
				bitOperator.integerTo4Bytes(warningFlagField), 
				// 2、byte4 			状态				DWORD32			int			statusField
				bitOperator.integerTo4Bytes(statusField),
				// 3、byte4 			纬度				DWORD32 		float		latitude
				latitude,
				// 4、byte4			经度				DWORD32			float 		longitude
				longitude,
				// 5、byte2 			高程				WORD16 			int			elevation
				bitOperator.integerTo2Bytes(elevation),
				// 6、byte2 			速度				WORD 			float		speed
				speed,
				// 7、byte2 			方向				WORD			int			direction
				bitOperator.integerTo2Bytes(direction),
				// 8、byte6 			时间				BCD[6]			Date		time
				time.getBytes(),
				//====================位置基本信息end====================
				
				//====================位置附加信息项格式begin====================
				// byte1	附加信息ID		BYTE		String
				// byte1	附加信息长度		BYTE		int
				// 			附加信息  
				
				// 1、0x01		byte4		里程							int			mileage
				bitOperator.integerTo1Bytes(0x01),
				bitOperator.integerTo1Bytes(4),
				bitOperator.integerTo4Bytes(mileage),
				
				// 2、0x02     	byte2		油量							int			oilVolume
				bitOperator.integerTo1Bytes(0x02),
				bitOperator.integerTo1Bytes(2),
				bitOperator.integerTo2Bytes(oilVolume),
				// 3、0x03		byte2		行驶记录功能获取的速度		int			getSpeed
				bitOperator.integerTo1Bytes(0x03),
				bitOperator.integerTo1Bytes(2),
				bitOperator.integerTo2Bytes(getSpeed),
				// 4、0x04		byte2		报警事件ID					int			confirmAlarm
				bitOperator.integerTo1Bytes(0x04),
				bitOperator.integerTo1Bytes(2),
				bitOperator.integerTo2Bytes(confirmAlarm),
				
				// 5、0x08		byte25		OBD实时上报					int			obdRealTimeDataRpt
				bitOperator.integerTo1Bytes(0x08),
				bitOperator.integerTo1Bytes(25),
				//				byte1		电平电压						int			levelVoltage
				bitOperator.integerTo1Bytes(levelVoltage),
				//				byte2		发动机转速					int			engineSpeed
				bitOperator.integerTo2Bytes(engineSpeed),
				//				byte2		瞬时车速						int			instantaneousSpeed
				bitOperator.integerTo2Bytes(instantaneousSpeed),
				//				byte1		节气门开度					int			throttleOpening
				bitOperator.integerTo1Bytes(throttleOpening),
				//				byte1		发动机负荷					int			engineLoad
				bitOperator.integerTo1Bytes(engineLoad),
				//				byte1		冷却液温度					int			coolantTemperature
				bitOperator.integerTo1Bytes(coolantTemperature),
				//				byte1		瞬时油耗						int			instFuelConsumption
				bitOperator.integerTo1Bytes(instFuelConsumption),
				//				byte4		总里程						int			totalMileage
				bitOperator.integerTo4Bytes(totalMileage),
				//				byte4		累计油耗量					int			accOilConsumption
				bitOperator.integerTo4Bytes(accOilConsumption),
				// 				byte8		预留							String		reserved
				obdRealTimeDataRptReserved.getBytes(),
				
					
				// 6、0x09		byte19      OBD本次结束时上报						obdOverDataRpt
				bitOperator.integerTo1Bytes(0x09),
				bitOperator.integerTo1Bytes(19),
				//				byte2		平均车速						int			averageSpeed
				bitOperator.integerTo2Bytes(averageSpeed),
				//				byte1		平均油耗						int			averageFuelConsumption
				bitOperator.integerTo1Bytes(averageFuelConsumption),
				//				byte4		本次行驶里程					int			travelMileage
				bitOperator.integerTo4Bytes(travelMileage),
				//				byte4		本次油耗量					int			oilConsumption
				bitOperator.integerTo4Bytes(obdOverDataRptOilConsumption),
				//				byte8		预留							String		reserved
				obdOverDataRptReserved.getBytes(),
				
				// 7、0x10		byte31            选择预留的附加信息						reserveAdditionalRpt
				bitOperator.integerTo1Bytes(0x10),
				bitOperator.integerTo1Bytes(31),
				//				byte6		起始时间						String		startDateTime
				startDateTime.getBytes(),
				//				byte1		时长统计参考源				int			referenceSource
				bitOperator.integerTo1Bytes(referenceSource),
				//				byte4		本次行驶时长					int			travelDuration
				bitOperator.integerTo4Bytes(travelDuration),
				//				byte4		本次静止时长					int			resetDuration
				bitOperator.integerTo4Bytes(resetDuration),
				//				byte2		本次急加速次数				int			rapidAccelerationCnt
				bitOperator.integerTo2Bytes(rapidAccelerationCnt),
				//				byte2		本次急减速次数				int			rapidDecelerationCnt
				bitOperator.integerTo2Bytes(rapidDecelerationCnt),
				//				byte2		本次油耗量					int			oilConsumption
				bitOperator.integerTo2Bytes(reserveAdditionalRptOilConsumption),
				//				byte2		本次行驶里程					int			drivingMileage
				bitOperator.integerTo2Bytes(drivingMileage),
				//				byte2		本次急转弯次数				int			sharpTurnCnt
				bitOperator.integerTo2Bytes(sharpTurnCnt),
				//				byte2		本次急变道次数				int			steepRoadCnt
				bitOperator.integerTo2Bytes(steepRoadCnt),
				//				byte4		预留							String		reserved
				reserveAdditionalRptReserved.getBytes()
				//====================位置附加信息项格式end====================	
		));

		// 消息头和消息体
		int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(msgBody.length, 0b000, false, 0);
		byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader(req.getMsgHeader().getTerminalPhone(),TPMSConsts.msg_id_terminal_location_info_upload, msgBody, msgBodyProps, flowId);
		byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBody);
		
		// 校验码
		int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
		// 连接并且转义
		return this.doEncode(headerAndBody, checkSum);
	}
	
	/**
	 * 构建终端升级结果通知协议
	 * @param req				终端心跳请求消息
	 * @param flowId			应答流水号
	 * @return					终端升级结果通知 协议
	 * @throws Exception
	 */
	public byte[] createTestTerminaluUpgradeResult(PackageData req,int flowId) throws Exception {
		
		// 消息体字节数组
		byte[] msgBody = this.bitOperator.concatAll(Arrays.asList(

				// 1、byte[0] 			升级类型(0：终端,12：道路运输证 IC 卡读卡器,52：北斗卫星定位模块 )	
				bitOperator.integerTo1Bytes(0), 
				// 2、byte[1] 			升级结果(0：成功,1：失败,2：取消)
				bitOperator.integerTo1Bytes(1)
		));
		
		// 消息头和消息体
		int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(msgBody.length, 0b000, false, 0);
		byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader(req.getMsgHeader().getTerminalPhone(),TPMSConsts.cmd_terminal_upgrade_result, msgBody, msgBodyProps, flowId);
		byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBody);
		
		// 校验码
		int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
		// 连接并且转义
		return this.doEncode(headerAndBody, checkSum);
	}
	
	/**
	 * 构建终端通用应答协议
	 * @param req				终端心跳请求消息
	 * @param flowId			应答流水号
	 * @return					终端通用应答协议
	 * @throws Exception
	 */
	public byte[] createTestTerminalCommandResp(PackageData req,int flowId) throws Exception {
		
		byte[] result = {0};
		
		// 消息体字节数组
		byte[] msgBody = this.bitOperator.concatAll(Arrays.asList(

				// 1、byte[0-1] 			应答流水号	WORD
				bitOperator.integerTo2Bytes(0), 
				// 2、byte[2-3] 			应答 ID 		WORD 
				bitOperator.integerTo2Bytes(0x8108),
				// 3、byte[4]			结果			byte		0：成功/确认；1：失败；2：消息有误；3：不支持  
				result
		));
		
		// 消息头和消息体
		int msgBodyProps = this.jt808ProtocolUtils.generateMsgBodyProps(msgBody.length, 0b000, false, 0);
		byte[] msgHeader = this.jt808ProtocolUtils.generateMsgHeader(req.getMsgHeader().getTerminalPhone(),TPMSConsts.msg_id_terminal_common_resp, msgBody, msgBodyProps, flowId);
		byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBody);
		
		// 校验码
		int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
		// 连接并且转义
		return this.doEncode(headerAndBody, checkSum);
	}
	
}
