package cn.hylexus.jt808.service;


import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import cn.hylexus.jt808.server.GlobalParametersManager;
import cn.hylexus.jt808.server.SessionManager;
import cn.hylexus.jt808.util.ActionManagerService;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;

/**
 * 服务端主动发送
 * @author 王成阳
 * 2018-3-1 
 */
public class ControlServices 
{
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private volatile boolean isRunning = false;
	
    private void run()
    { 
    	Channel ctx=null;
    	String channelID="";
    	String cmdType = "";
    	String cmdContent = "";
    	String terminalPhone = "";
    	String commandID="";
    	String serialNumber = "";
    	 
    	
    	TerminalUpgradeMsg terminalUpgradeMsg = new TerminalUpgradeMsg();
    	TerminalParamSettingMsg terminalParamSettingMsg = new TerminalParamSettingMsg();
    	GlobalParametersManager gpm=GlobalParametersManager.getInstance();
    	ActionManagerService ams=ActionManagerService.getInstance();
    	
    	logger.debug(">>>>>>>>>>>Control服务启动成功...");
    	
//    	sendTaskLoop:
    		
    	while(this.isRunning)
    	{
            try
            {
            	//获得服务端id
            	String tcpServerID = SessionManager.ServerID;
            	
            	//String tcpServerID = "tcpserverid";
            	Map<String, SocketChannel> map = ChannelsMap.getChannels();
            	//System.err.println("输出map:" + map);
            	
//            	System.err.println("**********TcpServerID:" + tcpServerID);
            	logger.debug("[TcpServerID] = " + tcpServerID);
            	
            	
            	//访问命令API：传递服务器ID获得本服务器注册车辆的所有命令
            	JSONObject jsonObject =  ams.SendCarCommand(tcpServerID);
            	
            	 JSONArray jsonArray = jsonObject.getJSONArray("DATA");
            	
            	logger.debug("[检查新命令API返回] = " + jsonObject);
            	logger.debug("jsonArray = " + jsonArray);
            	
            	//循环得到的所有命令
            	for(int i=0;i<jsonArray.length();i++)
            	{
            		//logger.info("进入for循环");
            		JSONObject resultObject = jsonArray.getJSONObject(i);
            		
            		//获得channelID
            		channelID = resultObject.getString("CHANNELID");

	            	//获得命令号
            		commandID = resultObject.getString("COMMANDID");
            		
            		//命令类型
            		cmdType = resultObject.getString("CMDTYPE");
            		
            		//命令内容
            		cmdContent = resultObject.getString("CMDCONTENT");
            		
            		//终端ID
            		terminalPhone = resultObject.getString("DEVICE_SN_NUMBER");
            		
            		logger.debug("[管道ID] = " + channelID);
            		logger.debug("[命令ID] = " + commandID);
            		logger.debug("[命令类型] = " + cmdType);
            		logger.debug("[终端ID] = " + terminalPhone);
            		logger.debug("[命令内容] = " + cmdContent);
            		
            		//流水号
            		serialNumber = resultObject.getString("SERIAL_NUMBER");
            		System.err.println("[命令流水号] = " + serialNumber);
            		SessionManager.SerialNumber = serialNumber;
            		
            		
            		//System.err.println("......输出map=" + ChannelsMap.getChannels().containsKey(channelID));
            		//System.err.println("**********ctx:" + ChannelsMap.getGatewayChannel(channelID));
            		
            		
	            	//判断channelID是否存在，如果存在则进行下面的操作
	            	if(ChannelsMap.getChannels().containsKey(channelID))
	            	{
	            		logger.debug("[管道连接存在！！]");
	            		
	            		//获得ctx
		            	ctx=ChannelsMap.getGatewayChannel(channelID);
		            	logger.debug("[ctx] = " + ctx); 
		            	
		            	//根据诺维命令号获得对应JT808命令号
		            	String jt808Command = getJt808CommandID(commandID);
		            	
		            	//参数设置
		            	if (jt808Command.equals("0x8103")) {
							
		            		logger.debug("[参数设置!!]");
		            		
		            		//下发参数设置协议
		            		terminalParamSettingMsg.processParamSettingMsg(resultObject, ctx);
						}
		            	//终端升级
		            	else if (jt808Command.equals("0x8108")) {
							
		            		logger.debug("[终端升级!!]");
		            		
		            		//下发终端升级协议
		            		terminalUpgradeMsg.processTerminalUpgradeMsg(resultObject, ctx);
						}
		            	//命令不存在
		            	else {
		            		logger.debug("[命令不存在！]");
						}
	            	}
	            	else
	            	{
	            		logger.debug("[管道连接不存在!!]");
	            		//调用命令执行结果API 设置操作失败
	            		JSONObject SendExecuteResult = ams.SendExecuteResult(tcpServerID,channelID,String.valueOf(commandID),"4",serialNumber);
	            		logger.debug("[命令执行结果API返回]" + SendExecuteResult.getString("code"));
	            		//返回0=失败;1=成功
	            		if (SendExecuteResult.getString("code") == "0") {
	            			logger.debug("[调用命令执行结果API异常]");
						}
	            	}
            	}
                
            	//访问一次后台服务器
            	Thread.sleep(Long.parseLong(gpm.getValue(gpm.ControlServerSleep))); 
            }
            catch(Exception e)
            {
            	logger.debug("[线程停止！！]");
            	logger.error("[异常捕获]" + e);
            	e.printStackTrace();
            	
            	//break sendTaskLoop;
            }
        }
    }
    
    public synchronized void startServer() {
		if (this.isRunning) {
			throw new IllegalStateException(this.getName() + " is already started .");
		}
		this.isRunning = true;

		new Thread(() -> {
			try {
				this.run();
			} catch (Exception e) {
				this.logger.info("Control服务启动出错:{}", e.getMessage());
				e.printStackTrace();
			}
		}, this.getName()).start();
	}

	public synchronized void stopServer() {
		if (!this.isRunning) {
			throw new IllegalStateException(this.getName() + " is not yet started .");
		}
		this.isRunning = false;

		this.logger.info("Control服务已经停止...");
		
		System.out.println("Control服务已经停止...");
	}
	
	private String getName() {
		return "Control-Services";
	}
	
	
	/**
	 * 根据诺维命令号获得JT808命令号
	 * 远程升级： 诺维 = 10008				JT808 = 0x8108
	 * 参数设置： 诺维 = 10006				JT808 = 0x8103
	 * @param nwcommand	诺维命令号
	 * @return jt808命令号
	 */
	private String getJt808CommandID(String nwcommand) {
		
		if (nwcommand.equals("10008")) {
			
			return "0x8108";
			
		}else if (nwcommand.equals("10006")) {
			
			return "0x8103";
			
		}else {
			return "another";
		}
	}
}
