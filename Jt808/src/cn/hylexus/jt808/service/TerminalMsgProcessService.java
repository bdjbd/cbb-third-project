package cn.hylexus.jt808.service;

import cn.hylexus.jt808.vo.req.LocationInfoUploadMsg;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import cn.hylexus.jt808.common.TPMSConsts;
import cn.hylexus.jt808.server.GlobalParametersManager;
import cn.hylexus.jt808.server.SessionManager;
import cn.hylexus.jt808.service.codec.MsgEncoder;
import cn.hylexus.jt808.util.ActionManagerService;
import cn.hylexus.jt808.util.HexStringUtils;
import cn.hylexus.jt808.util.HttpAsyncMap;
import cn.hylexus.jt808.util.LocationInfoUploadUtils;
import cn.hylexus.jt808.vo.PackageData;
import cn.hylexus.jt808.vo.PackageData.MsgHeader;
import cn.hylexus.jt808.vo.Session;
import cn.hylexus.jt808.vo.req.TerminalAuthenticationMsg;
import cn.hylexus.jt808.vo.req.TerminalCommonMsg;
import cn.hylexus.jt808.vo.req.TerminalRegisterMsg;
import cn.hylexus.jt808.vo.req.TerminalUpgradeResultMsg;
import cn.hylexus.jt808.vo.resp.ServerCommonRespMsgBody;
import cn.hylexus.jt808.vo.resp.TerminalRegisterMsgRespBody;
import cn.hylexus.jt808.vo.resp.TerminalUpgradeMsgBody;

/**
 * 消息数据处理
 */
public class TerminalMsgProcessService extends BaseMsgProcessService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private MsgEncoder msgEncoder;
    private SessionManager sessionManager; 
    private TerminalUpgradeMsg terminalUpgradeMsg; 
    private TerminalUpgradeMsgBody terminalUpgradeMsgBody; 
    GlobalParametersManager gpm=GlobalParametersManager.getInstance();
    
    //跳转API方法
    ActionManagerService ams = ActionManagerService.getInstance();
    
    public TerminalMsgProcessService() {
        this.msgEncoder = new MsgEncoder();
        this.sessionManager = SessionManager.getInstance();
        this.terminalUpgradeMsg = new TerminalUpgradeMsg();
        this.terminalUpgradeMsgBody = new TerminalUpgradeMsgBody();
    }
     
    /**
     * 终端注册
     */
    public void processRegisterMsg(TerminalRegisterMsg msg) throws Exception {
        log.debug(">>>>>解析终端注册消息体 = {}", JSON.toJSONString(msg, true));
       
        //获得sessionId
        final String sessionId = Session.buildId(msg.getChannel());
        //通过sessionId获得session
        Session session = sessionManager.findBySessionId(sessionId);
        //如果session不存在则生成新的
        if (session == null) {
            session = Session.buildSession(msg.getChannel(), msg.getMsgHeader().getTerminalPhone());
        }
        //是否通过身份验证
        session.setAuthenticated(true);
        //终端
        session.setTerminalPhone(msg.getMsgHeader().getTerminalPhone());
        sessionManager.put(session.getId(), session);
        
        //调用终端注册API
        JSONObject SendRequestRegister = ams.SendRequestRegister(msg.getMsgHeader().getTerminalPhone(), sessionManager.ServerID,  sessionId);
        //终端注册结果：2=注册失败，该终端不存在；1=注册成功；
        String registerResult = SendRequestRegister.getString("code");
        log.debug("[终端注册结果] = " + SendRequestRegister);
        
        //终端注册应答消息体.
        TerminalRegisterMsgRespBody respMsgBody = new TerminalRegisterMsgRespBody();
       
        if (registerResult.equals("1")) {
            //结果
            respMsgBody.setReplyCode(TerminalRegisterMsgRespBody.success);
            //应答流水号
            respMsgBody.setReplyFlowId(msg.getMsgHeader().getFlowId());
            //鉴权码=终端编号
            respMsgBody.setReplyToken(msg.getMsgHeader().getTerminalPhone());
            log.debug("[鉴权码] = " + msg.getMsgHeader().getTerminalPhone());
            
            int flowId = super.getFlowId(msg.getChannel());
            
            //编码产生新协议
            byte[] bs = this.msgEncoder.encode4TerminalRegisterResp(msg, respMsgBody, flowId);
            log.debug("[<<<<<下发终端注册应答] = " + HexStringUtils.toHexString(bs));
            //发送终端
            super.send2Client(msg.getChannel(), bs);
		}
        if (registerResult.equals("2")) {
        	log.debug("[<<<<<终端注册失败关闭连接]");
			msg.getChannel().close();
//			HttpAsyncMap.removeGatewayChannel(msg.getChannel().id().asLongText());
		}
    }
    
    /**
     * 终端鉴权
     */
    public void processAuthMsg(TerminalAuthenticationMsg msg) throws Exception {
        // TODO 暂时每次鉴权都成功
        log.debug(">>>>>解析终端鉴权消息体 = {}", JSON.toJSONString(msg, true));

        final String sessionId = Session.buildId(msg.getChannel());
        Session session = sessionManager.findBySessionId(sessionId);
        if (session == null) {
            session = Session.buildSession(msg.getChannel(), msg.getMsgHeader().getTerminalPhone());
        }
        session.setAuthenticated(true);
        session.setTerminalPhone(msg.getMsgHeader().getTerminalPhone());
        sessionManager.put(session.getId(), session);
        
        String authCode = msg.getAuthCode();
        log.debug("[鉴权码] = " + authCode);
        
        //调用终端注册API
        JSONObject SendRequestRegister = ams.SendRequestRegister(authCode, sessionManager.ServerID,  sessionId);
        //终端注册结果：2=注册失败，该终端不存在；1=注册成功；
        String registerResult = SendRequestRegister.getString("code");
        log.debug("[终端鉴权结果] = " + SendRequestRegister);
        
        //平台通用应答消息体 
        ServerCommonRespMsgBody respMsgBody = new ServerCommonRespMsgBody();
        
        if (registerResult.equals("1")) {
        	 respMsgBody.setReplyCode(ServerCommonRespMsgBody.success);
        	 log.debug("[鉴权成功 !!]" );
		}else {
			 respMsgBody.setReplyCode(ServerCommonRespMsgBody.failure);
			 log.debug("[鉴权失败!!]");
		}
       
        respMsgBody.setReplyFlowId(msg.getMsgHeader().getFlowId());
        respMsgBody.setReplyId(msg.getMsgHeader().getMsgId());
       
        int flowId = super.getFlowId(msg.getChannel());
        byte[] bs = this.msgEncoder.encode4ServerCommonRespMsg(msg, respMsgBody, flowId);
        log.debug("<<<<<[下发终端鉴权通用应答] = " + HexStringUtils.toHexString(bs));
        
        super.send2Client(msg.getChannel(), bs);
    }
    
    /**
     * 终端心跳
     */
    public void processTerminalHeartBeatMsg(PackageData req) throws Exception {
        log.debug(">>>>>[解析心跳消息体] = {}", JSON.toJSONString(req, true));
        
        ams.SendHeartBeat(req.getMsgHeader().getTerminalPhone(),req.getChannel());
        log.info("[终端心跳结束！！] ");
        
        final MsgHeader reqHeader = req.getMsgHeader();
        ServerCommonRespMsgBody respMsgBody = new ServerCommonRespMsgBody(reqHeader.getFlowId(), reqHeader.getMsgId(),ServerCommonRespMsgBody.success);
        
        int flowId = super.getFlowId(req.getChannel());
        //生成平台通用应答
        byte[] bs = this.msgEncoder.encode4ServerCommonRespMsg(req, respMsgBody, flowId);
        log.debug("<<<<<[下发终端心跳通用应答] = " + HexStringUtils.toHexString(bs));
        
        // 反向生成终端位置上报请求协议(仅测试用)
        // byte[] bs = this.msgEncoder.createTestLocationInfoUploadMsg(req, flowId);
        // 反向生成终端升级结果通知协议(仅测试用)
        // byte[] bs = this.msgEncoder.createTestTerminaluUpgradeResult(req, flowId);
        // 反向生成终端通用应答协议(仅测试用)
        //byte[] bs = this.msgEncoder.createTestTerminalCommandResp(req, flowId);
        
        super.send2Client(req.getChannel(), bs);
    }

    /**
     * 终端注销
     */
    public void processTerminalLogoutMsg(PackageData req) throws Exception {
        log.info(">>>>>[解析终端注销消息体] = {}", JSON.toJSONString(req, true));
        final MsgHeader reqHeader = req.getMsgHeader();
        
        ServerCommonRespMsgBody respMsgBody = new ServerCommonRespMsgBody(reqHeader.getFlowId(), reqHeader.getMsgId(),ServerCommonRespMsgBody.success);
        
        int flowId = super.getFlowId(req.getChannel());
        byte[] bs = this.msgEncoder.encode4ServerCommonRespMsg(req, respMsgBody, flowId);
        log.debug("<<<<<[下发终端注销通用应答] = " + HexStringUtils.toHexString(bs));
        
        super.send2Client(req.getChannel(), bs);
    }
    
    /**
     * 位置信息汇报请求
     */
    public void processLocationInfoUploadMsg(LocationInfoUploadMsg req) throws Exception {
    	
    	LocationInfoUploadUtils locationInfoUploadUtils = new LocationInfoUploadUtils();
    	 final MsgHeader reqHeader = req.getMsgHeader();
    	
        log.debug(">>>>>[解析位置信息上报消息体] = {}", JSON.toJSONString(req, true));
   
        //拼参数
        // **********终端id
		String terminalId = req.getMsgHeader().getTerminalPhone();
		log.debug("[终端id] = " + terminalId);
        
//        // **********报警标志/alarmFlag(共32个标志位,其中1=有报警,0=无报警)
//		int  alarmFlag = req.getWarningFlagField();
//		JSONObject alarmFlagJson = new JSONObject(locationInfoUploadUtils.setFlagStatus(alarmFlag, 0));
//		System.err.println("********************报警标志位：" + alarmFlag);
//		System.err.println("********************报警标志/alarmFlag：" + alarmFlagJson.toString());
//        
//        // **********状态/status(共32个标志位,参考0x0200表25状态位定义)`
//		int status = req.getStatusField();
//		JSONObject statusJson = new JSONObject(locationInfoUploadUtils.setFlagStatus(status, 1));
//		System.err.println("********************状态标志位：" + status);
//		System.err.println("********************状态/status：" + statusJson.toString());
        
        // **********实时位置/location
		//实时位置/location
		//纬度/latitude
		double latitude = req.getLatitude();
		//经度/longitude
		double longitude = req.getLongitude();
		//高程/high
		int high = req.getElevation();
		//速度/speed
		double speed = (req.getSpeed());
		//方向/direc
		int direc = req.getDirection();
		//时间/datetime
		String datetime = "20" +  req.getTime();
		
		log.debug("[报警标志位] = " + req.getWarningFlag());
		log.debug("[状态位] = " + req.getStatus());
		
		JSONObject location = new JSONObject();
		location.put("latitude", latitude);
		location.put("longitude", longitude);
		location.put("high", high);
		location.put("speed", speed);
		location.put("direc", direc);
		location.put("datetime", datetime);
		
		log.debug("[实时位置/location] = " + location.toString());
		
		// **********附加实时信息/appendInfo
		//里程/mileage/0x01
		double mileage = (req.getMileage());
		//油量/oilmass/0x02
		int oilmass = (req.getOilVolume());
		//速度/drivingRecordspeed/0x03
		int drivingRecordspeed = (req.getGetSpeed());
		//报警事件ID/alarmEventId/0x04)
		String alarmEventId = "";
		if (req.getConfirmAlarm() == null) {
			alarmEventId = "0";
		}else {
			alarmEventId = req.getConfirmAlarm();
		}
		
		JSONObject appendInfo = new JSONObject();
		appendInfo.put("mileage", mileage);
		appendInfo.put("oilmass", oilmass);
		appendInfo.put("drivingRecordspeed", drivingRecordspeed);
		appendInfo.put("alarmEventId", alarmEventId);
		
		log.debug("[附加实时信息/appendInfo] = " + appendInfo.toString());

		// **********OBD实时数据/ObdRealTimeDataRpt/0x08
		//电平电压/levelVoltage
		JSONObject ObdRealTimeDataRpt = new JSONObject();
		
		if (req.getObdRealTimeDataRpt() == null) {
			
			ObdRealTimeDataRpt.put("levelVoltage", 0);
			ObdRealTimeDataRpt.put("engineSpeed", 0);
			ObdRealTimeDataRpt.put("instantaneousSpeed", 0);
			ObdRealTimeDataRpt.put("throttleOpening", 0);
			ObdRealTimeDataRpt.put("engineLoad", 0);
			ObdRealTimeDataRpt.put("coolantTemperature", 0);
			ObdRealTimeDataRpt.put("instFuelConsumption", 0);
			ObdRealTimeDataRpt.put("totalMileage", 0);
			ObdRealTimeDataRpt.put("accOilConsumption", 0);
			
		}else {
			
			int levelVoltage = req.getObdRealTimeDataRpt().getLevelVoltage();
			//发动机转速/engineSpeed
			int engineSpeed = req.getObdRealTimeDataRpt().getEngineSpeed();
			//瞬时车速/instantaneousSpeed
			int instantaneousSpeed = req.getObdRealTimeDataRpt().getInstantaneousSpeed();
			//节气门开度/throttleOpening
			int throttleOpening = req.getObdRealTimeDataRpt().getThrottleOpening();
			//发动机负荷/engineLoad
			int engineLoad = req.getObdRealTimeDataRpt().getEngineLoad();
			//冷却液温度/coolantTemperature
			int coolantTemperature = req.getObdRealTimeDataRpt().getCoolantTemperature();
			//瞬时油耗/instFuelConsumption
			int instFuelConsumption = req.getObdRealTimeDataRpt().getInstFuelConsumption();
			//总里程/totalMileage
			int totalMileage = req.getObdRealTimeDataRpt().getTotalMileage();
			//累计耗油量/accOilConsumption
			int accOilConsumption = req.getObdRealTimeDataRpt().getAccOilConsumption();
			
			ObdRealTimeDataRpt.put("levelVoltage", levelVoltage);
			ObdRealTimeDataRpt.put("engineSpeed", engineSpeed);
			ObdRealTimeDataRpt.put("instantaneousSpeed", instantaneousSpeed);
			ObdRealTimeDataRpt.put("throttleOpening", throttleOpening);
			ObdRealTimeDataRpt.put("engineLoad", engineLoad);
			ObdRealTimeDataRpt.put("coolantTemperature", coolantTemperature);
			ObdRealTimeDataRpt.put("instFuelConsumption", instFuelConsumption);
			ObdRealTimeDataRpt.put("totalMileage", totalMileage);
			ObdRealTimeDataRpt.put("accOilConsumption", accOilConsumption);
		}

		log.debug("[OBD实时数据/ObdRealTimeDataRpt] = " + ObdRealTimeDataRpt.toString());
		
        // **********OBD本次行驶结束数据/ObdOverDataRpt/0x09
		JSONObject ObdOverDataRpt = new JSONObject();
		
		if (req.getObdOverDataRpt() == null) {
			ObdOverDataRpt.put("averageSpeed", 0);
			ObdOverDataRpt.put("averageFuelConsumption", 0);
			ObdOverDataRpt.put("travelMileage", 0);
			ObdOverDataRpt.put("oilConsumption", 0);
		}else {
			
			//平均车速/averageSpeed
			int averageSpeed = req.getObdOverDataRpt().getAverageSpeed();
			//平均油耗/averageFuelConsumption
			int averageFuelConsumption = req.getObdOverDataRpt().getAverageFuelConsumption();
			//本次行驶里程/travelMileage
			int travelMileage = req.getObdOverDataRpt().getTravelMileage();
			//本次耗油量/oilConsumption
			int oilConsumption = req.getObdOverDataRpt().getOilConsumption();
			
			
			ObdOverDataRpt.put("averageSpeed", averageSpeed);
			ObdOverDataRpt.put("averageFuelConsumption", averageFuelConsumption);
			ObdOverDataRpt.put("travelMileage", travelMileage);
			ObdOverDataRpt.put("oilConsumption", oilConsumption);
		}
		
		log.debug("[OBD本次行驶结束数据/ObdOverDataRpt] = " + ObdOverDataRpt.toString());
		
        // **********本次行驶结束数据/TerminalVehicleRpt/0x10
		JSONObject TerminalVehicleRpt = new JSONObject();
		
		if (req.getReserveAdditionalRpt() == null) {
			TerminalVehicleRpt.put("startDateTime", "00000000000000");
			TerminalVehicleRpt.put("referenceSource", 0);
			TerminalVehicleRpt.put("travelDuration", 0);
			TerminalVehicleRpt.put("resetDuration", 0);
			TerminalVehicleRpt.put("rapidAccelerationCnt", 0);
			TerminalVehicleRpt.put("rapidDecelerationCnt", 0);
			TerminalVehicleRpt.put("oilConsumption", 0);
			TerminalVehicleRpt.put("drivingMileage", 0);
			TerminalVehicleRpt.put("sharpTurnCnt", 0);
			TerminalVehicleRpt.put("steepRoadCnt", 0);
		}else {
		//起始时间/startDateTime
		String startDateTime = req.getReserveAdditionalRpt().getStartDateTime();
		//时长统计参考源/referenceSource
		int referenceSource = req.getReserveAdditionalRpt().getReferenceSource();
		//本次行驶时长/travelDuration
		int travelDuration = req.getReserveAdditionalRpt().getTravelDuration();
		//本次静止时长/resetDuration
		int resetDuration = req.getReserveAdditionalRpt().getResetDuration();
		//本次急加速次数/rapidAccelerationCnt
		int rapidAccelerationCnt = req.getReserveAdditionalRpt().getRapidAccelerationCnt();
		//本次急减速次数/rapidDecelerationCnt
		int rapidDecelerationCnt = req.getReserveAdditionalRpt().getRapidDecelerationCnt();
		//本次耗油量/oilConsumption
		int oilConsumption_ter = req.getReserveAdditionalRpt().getOilConsumption();
		//本次行驶里程/drivingMileage
		int drivingMileage = req.getReserveAdditionalRpt().getDrivingMileage();
		//本次急转弯次数/sharpTurnCnt
		int sharpTurnCnt = req.getReserveAdditionalRpt().getSharpTurnCnt();
		//本次急变道次数/steepRoadCnt
		int steepRoadCnt = req.getReserveAdditionalRpt().getSteepRoadCnt();
		
		TerminalVehicleRpt.put("startDateTime", startDateTime);
		TerminalVehicleRpt.put("referenceSource", referenceSource);
		TerminalVehicleRpt.put("travelDuration", travelDuration);
		TerminalVehicleRpt.put("resetDuration", resetDuration);
		TerminalVehicleRpt.put("rapidAccelerationCnt", rapidAccelerationCnt);
		TerminalVehicleRpt.put("rapidDecelerationCnt", rapidDecelerationCnt);
		TerminalVehicleRpt.put("oilConsumption", oilConsumption_ter);
		TerminalVehicleRpt.put("drivingMileage", drivingMileage);
		TerminalVehicleRpt.put("sharpTurnCnt", sharpTurnCnt);
		TerminalVehicleRpt.put("steepRoadCnt", steepRoadCnt);
			
		}

		log.debug("[本次行驶结束数据/TerminalVehicleRpt] = " + TerminalVehicleRpt.toString());
		
		//构建API参数
		String apiParamJson = locationInfoUploadUtils.getApiParam(terminalId, req.getWarningFlag(), req.getStatus(), location, appendInfo, ObdRealTimeDataRpt, ObdOverDataRpt, TerminalVehicleRpt); 
//		JSONObject apiParamJson = locationInfoUploadUtils.getApiParam(terminalId, req.getWarningFlag(), req.getStatus(), location, appendInfo, ObdRealTimeDataRpt, ObdOverDataRpt, TerminalVehicleRpt);
		log.debug("[API参数json] = " + apiParamJson);
		
		
		log.info("[位置上报开始响应！！]");
        // 调API
        ams.SendTerminalLocationTest(apiParamJson,req.getChannel());
        log.info("[位置上报响应结束！！]");
        
//		  ams.SendTerminalLocationTest(apiParamJson);
//        log.debug("[API返回结果] = " + SendTerminalLocation);
        
        //返回结果
        ServerCommonRespMsgBody respMsgBody;
        
//		if (SendTerminalLocation.getString("code").equals("1")) {
			
	        respMsgBody = new ServerCommonRespMsgBody(reqHeader.getFlowId(), reqHeader.getMsgId(),ServerCommonRespMsgBody.success);
	        log.info("[终端位置上报成功!!]" );
//		}else{
//			respMsgBody = new ServerCommonRespMsgBody(reqHeader.getFlowId(), reqHeader.getMsgId(),ServerCommonRespMsgBody.failure);
//			log.info("[终端位置上报失败!!]");
//		}
        
        int flowId = super.getFlowId(req.getChannel());
        byte[] bs = this.msgEncoder.encode4ServerCommonRespMsg(req, respMsgBody, flowId);
        log.debug("<<<<<[下发位置上报通用应答 = ] = " + HexStringUtils.toHexString(bs));
        super.send2Client(req.getChannel(), bs);
    }
    
    
    /**
     * 终端升级结果通知
     */
    public void processTerminalUpgradeResultMsg(TerminalUpgradeResultMsg msg) throws Exception {
        log.debug(">>>>>[解析终端升级结果消息体]:{}", JSON.toJSONString(msg, true));
       
//        System.err.println("ServerID=" + sessionManager.ServerID);
        
        //判断升级结果(0：成功,1：失败,2：取消 )
        int upgradeResult = msg.getUpgradeResult();
        log.debug("[终端升级结果] = " + upgradeResult);
        
        //获得命令表流水号
        log.debug("[命令表流水号] = " + sessionManager.SerialNumber);
        
       //获得sessionId
       String sessionId = Session.buildId(msg.getChannel());
       
       JSONObject result = new JSONObject();
       
       //执行成功
        if (upgradeResult == 0) {
			
        	result = ams.SendExecuteResult(sessionManager.ServerID,sessionId, "10008", "3", sessionManager.SerialNumber);
        	
        	log.info("[程升级成功!!]");
        	log.debug("<<<<<[命令执行结果] = " + result);
       
        //执行失败	
		}else if (upgradeResult == 1) {
			
			result = ams.SendExecuteResult(sessionManager.ServerID, sessionId, "10008", "4", sessionManager.SerialNumber);
			
        	log.info("[远程升级失败!!]");
        	log.debug("<<<<<[命令执行结果] = " + result);
		
        //终端取消升级	
		}else if (upgradeResult == 2) {
			
			result = ams.SendExecuteResult(sessionManager.ServerID, sessionId, "10008", "5", sessionManager.SerialNumber);
			
			log.info("[远程升级取消!!]");
			log.debug("<<<<<[命令执行结果] = " + result);
		}
    }
    
    /**
     * 远程升级通用应答
     */
    public void processUpgradeTerminalCommandtMsg(TerminalCommonMsg msg) throws Exception {
        log.debug(">>>>>[远程升级终端通用应答]:{}", JSON.toJSONString(msg, true));
       
        //流水号
		int flowId = super.getFlowId(msg.getChannel());
        //终端手机号
		String terminalPhone = msg.getMsgHeader().getTerminalPhone();
		
        //调API查找命令内容、当前包数、总包数
		//{DATA:[ {CmdContent:{},total_package,current_package}]}
		JSONObject terminalUpgradeSelect = ams.SendTerminalUpgradeSelect(sessionManager.SerialNumber);
		
		///////////////////////////////////////////////////////////////////
		//获得内容JSONARRAY数组
		JSONArray jsonArray = new JSONArray(terminalUpgradeSelect.getString("DATA"));
		JSONObject jsonObject = jsonArray.getJSONObject(0);
		
        int current_package = jsonObject.getInt("CURRENT_PACKAGE");
        int total_package = jsonObject.getInt("TOTAL_PACKAGE");
        
        System.err.println("111 = " + jsonObject);
        JSONObject cmd_content = new JSONObject(jsonObject.getString("CMD_CONTENT"));
        System.err.println("222 = " + cmd_content);
    	//获得内容JSONARRAY数组
		JSONArray contentArray = new JSONArray(cmd_content.getString("DATA"));
		System.err.println("333 = " + contentArray);
		//将数组转换成JSON对象
		JSONObject contentObject = contentArray.getJSONObject(0);
		System.err.println("444 = " + contentObject);
		//获得内容的filePath数组
		JSONArray filePathArray = new JSONArray(contentObject.getString("FILE_PATH"));
		System.err.println("555 = " + filePathArray);
		//将数组转换成JSON对象
		JSONObject filePathObject = filePathArray.getJSONObject(0);
		System.err.println("666 = " + filePathObject);
		
		//拆json获得命令地址
		String path = filePathObject.getString("path");
		log.debug("[升级地址] = " + path);
		//版本号
		String versionNumber = contentObject.getString("VERSION_NUMBER");
		log.debug("[升级版本号] = " + versionNumber);
		log.debug("[升级版本号长度] = " + versionNumber.length());
        
        //单包数量
        int every = 0;
        
        //读取文件获得字节数组
        byte[] contentByte = terminalUpgradeMsg.getCmdContent(path);
        
		int len = contentByte.length;
		log.debug("[升级文件长度] = " + len);

		//最后一组数据包长度
		int remainder = len % TPMSConsts.EVERY_LENGTH;
		log.debug("[升级总包数] = " + "：" + total_package);
		log.debug("[余数] = " + "：" + remainder);
		
		//第一组到倒数第二组数据包长度为every,最后一组数据包长度为remainder
		if (current_package+1 < total_package) {
			every = TPMSConsts.EVERY_LENGTH;
		}else if (current_package+1 >= total_package) {
			every = remainder;
		}
		log.debug("[单包长度] = " + every);
		
        //判断终端回复结果(0：成功/确认；1：失败；2：消息有误；3：不支持)
        byte replyCode = msg.getReplyCode();
        
//		//升级类型  (0：终端，12：道路运输证 IC 卡读卡器，52：北斗卫星定位模块) 
//    	terminalUpgradeMsgBody.setUpgradeType(0);
//    	//制造商ID
//    	terminalUpgradeMsgBody.setManufacturerID(0);
//    	//版本号长度
//    	terminalUpgradeMsgBody.setVersionNumLength(versionNumber.length());
//    	//版本号
//    	terminalUpgradeMsgBody.setVersionNumber(versionNumber);
//		//升级数据包长度
//    	terminalUpgradeMsgBody.setUpgradeDataLength(every);
    	//升级数据
    	byte[] upgradeByte = new byte[every];
        
        if (replyCode == 0) {
        	log.info("[终端通用回复成功!!]");
        	log.debug("[当前包数] = " + (current_package + 1));
        	
        	if ((current_package+1) > total_package) {
				log.info("[升级包已发完]");
			}else {
	    		System.arraycopy(contentByte,current_package * TPMSConsts.EVERY_LENGTH, upgradeByte, 0, upgradeByte.length);
	    		terminalUpgradeMsgBody.setUpgradeData(upgradeByte);
	    		//log.debug("[升级数据包内容] = " + HexStringUtils.toHexString(upgradeByte));
	    		
	            log.debug("------------------------------------------------------------------------------------------------------");
	            log.debug("[升级数据] = " + HexStringUtils.toHexString(upgradeByte));
	            log.debug("------------------------------------------------------------------------------------------------------");
	    		
		        //编码产生新的报文
	            byte[] bs = this.msgEncoder.encode4TerminalUpgradeRespOther(terminalUpgradeMsgBody, flowId, terminalPhone, total_package, current_package+1);
	            
	            //发送终端
	            super.send2Client(msg.getChannel(), bs);
	            
	            //调API把总包数及当前包数存进数据库
	    		JSONObject terminalUpgradeUpdate = ams.SendTerminalUpgradeUpdate(sessionManager.SerialNumber, total_package, current_package+1);
	    		log.debug("<<<<<[升级进度更新结果] = " + terminalUpgradeUpdate);
	    		
			}
		}else {
			
			log.info("[终端通用回复失败!!]");
			log.debug("[当前包数] = " + current_package);
			
    		System.arraycopy(contentByte,(current_package-1) * TPMSConsts.EVERY_LENGTH, upgradeByte, 0, upgradeByte.length);
    		terminalUpgradeMsgBody.setUpgradeData(upgradeByte);
			
            log.debug("------------------------------------------------------------------------------------------------------");
            log.debug("[升级数据] = " + HexStringUtils.toHexString(upgradeByte));
            log.debug("------------------------------------------------------------------------------------------------------");
    		
	        //编码产生新的报文
            byte[] bs = this.msgEncoder.encode4TerminalUpgradeRespOther(terminalUpgradeMsgBody, flowId, terminalPhone, total_package, current_package);
            
            //发送终端
            super.send2Client(msg.getChannel(), bs);
            
    		//调API把总包数及当前包数存进数据库
    		JSONObject terminalUpgradeUpdate = ams.SendTerminalUpgradeUpdate(sessionManager.SerialNumber, total_package, current_package);
    		log.debug("<<<<<[数据更新结果] = " + terminalUpgradeUpdate);
		}
    }
    
    /**
     * 参数设置终端通用应答
     */
    public void processParamSettingTerminalCommandtMsg(TerminalCommonMsg msg) throws Exception {
        log.debug(">>>>>[参数设置终端通用应答]:{}", JSON.toJSONString(msg, true));
        //log.debug("ServerID = " + sessionManager.ServerID);
        
        //结果(0：成功/确认；1：失败；2：消息有误；3：不支持) 
        byte replyCode = msg.getReplyCode();
        log.debug("[通用应答结果] = " + replyCode);
        
        //获得命令表流水号
       log.debug("[命令表流水号] = " + sessionManager.SerialNumber);
        
       //获得sessionId
       String sessionId = Session.buildId(msg.getChannel());
       
       JSONObject result = new JSONObject();
       
       //执行成功
        if (replyCode == 0) {
			
        	result = ams.SendExecuteResult(sessionManager.ServerID,sessionId, "10006", "3", sessionManager.SerialNumber);
        	log.info("[参数设置成功!!]");
        	log.debug("[命令执行结果] = " + result);
       
        //执行失败	
		}else{
			
			result = ams.SendExecuteResult(sessionManager.ServerID, sessionId, "10006", "4", sessionManager.SerialNumber);
        	log.debug("[参数设置失败!!!]");
        	log.debug("[命令执行结果] = " + result);
		}
    }
}
