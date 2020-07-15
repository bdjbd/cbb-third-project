package cn.hylexus.jt808.util;


import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hylexus.jt808.server.GlobalParametersManager;
import cn.hylexus.jt808.util.HttpRequest;
import io.netty.channel.Channel;
import cn.hylexus.jt808.service.AsynchronousHttpPost;




/**
 * 跳转API公共方法
 * @author 王成阳
 * 2018-1-24
 */
public class ActionManagerService {
	
	Logger log = LoggerFactory.getLogger(getClass());
	private GlobalParametersManager gpm=GlobalParametersManager.getInstance();
	AsynchronousHttpPost asynchronousHttpPost = new AsynchronousHttpPost();
	private static ActionManagerService single = new ActionManagerService();
	
	private ActionManagerService()
	{
	}
	
    public static ActionManagerService getInstance() 
    {  
       return single;  
    }
	
	/**
	 * 检查是否有新命令API
	 * @param TcpServerID 服务端生成的TcpServerID
	 * @return {DATA:[ {TcpServerID,ChannelID,DEVICE_SN_NUMBER/终端id,COMMANDID,CmdType:1=远程升级;2=参数设置,CmdContent:{}}]}
	 */
	public JSONObject SendCarCommand(String tcpServerID) 
	{
		String tParam="tcpServerID=" + tcpServerID;
		String rString= HttpRequest.sendPost(gpm.getValue(gpm.RestfulAPIServerHost)+"com.am.nw.api.ApiCarCommand.do",tParam);
		
			try {
				if (rString == "") {
					JSONObject jsonObject = new JSONObject();
					JSONArray jsonArray = new JSONArray();
					jsonObject.put("DATA", jsonArray);
					rString = jsonObject.toString();
				}
				JSONObject jsonObject = new JSONObject(rString);
				return jsonObject;
			
			} catch (JSONException e) {
			
				e.printStackTrace();
				return null ;
			}
	}
	
	/**
	 * 执行结果API
	 * @param tcpServerID   服务端启动时生成
	 * @param ChannelID     管道id
	 * @param commandID     命令id
	 * @param state         3=执行完成；4=执行失败
	 * @param serialNumber  流水号
	 * @return {code:"",msg:""} 0=失败; 1=成功;
	 */
	public JSONObject SendExecuteResult(String tcpServerID, String channelID , String commandID , String state , String serialNumber) 
	
	{
		String tParam = "tcpServerID="+tcpServerID+"&channelID="+channelID+"&commandID="+commandID+"&state="+state+"&serialNumber="+serialNumber;
		
		String rString = HttpRequest.sendPost(gpm.getValue(gpm.RestfulAPIServerHost)+"com.am.nw.api.ApiExecuteResult.do",tParam);
		
		try {
			
			JSONObject jsonObject = new JSONObject(rString);
			return jsonObject;
		
		} catch (JSONException e) {
			
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 请求注册API
	 * @param terminalId	终端id
	 * @param TcpServerID	服务端启动时生成
	 * @param channelID		管道id
	 * @return {code:"",msg:""} 2=注册失败,该终端不存在;1=注册成功
	 */
	public JSONObject SendRequestRegister(String terminalId , String TcpServerID , String channelID) 
	
	{
		String tParam = "terminalId=" + terminalId + "&TcpServerID=" + TcpServerID + "&channelID=" + channelID ;
		
		String rString = HttpRequest.sendPost(gpm.getValue(gpm.RestfulAPIServerHost)+"com.am.nw.api.ApiRequestRegister.do",tParam);
		
		try {
			
			JSONObject jsonObject = new JSONObject(rString);
			return jsonObject;
		
		} catch (JSONException e) {
		
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * 终端心跳API
	 * @param terminalId	终端id
	 * @param ctx			连接管道
	 * @return {code:"",msg:""} 0=失败；1=成功; 
	 */
	public void SendHeartBeat(String terminalId,Channel ctx)
	{
		ArrayList<NameValuePair> reqParams = new ArrayList<NameValuePair>();
		
		reqParams.add(new BasicNameValuePair("terminalId", terminalId));
		
		asynchronousHttpPost.AsynchronousHttpPost(gpm.getValue(gpm.RestfulAPIServerHost)+"com.am.nw.api.ApiHeartBeat.do", reqParams,ctx);
		
	}
	
	/** 
	 * 终端位置上报API
	 * @param TerminalLocationParams		API请求参数
	 * @param ctx							连接管道
	 * @return {code:"",msg:""} 0=失败；1=成功;
	 */
	public void SendTerminalLocationTest(String terminalLocationParams,Channel ctx)
	{
		ArrayList<NameValuePair> reqParams = new ArrayList<NameValuePair>();
		
		reqParams.add(new BasicNameValuePair("terminalLocationParams", terminalLocationParams));
		
		System.err.println(reqParams);
		log.info("[ActionManagerService发送请求开始！]");
		asynchronousHttpPost.AsynchronousHttpPost(gpm.getValue(gpm.RestfulAPIServerHost)+"com.am.nw.api.ApiReportingTerminalLocationData.do", reqParams,ctx);

	}
	
	/**
	 * 终端升级修改API
	 * @param serialNumber      终端命令流水号
	 * @param total_package		总包数
	 * @param current_package   当前包数
	 * @return {code:"",msg:""} 0=失败；1=成功;
	 */
	public JSONObject SendTerminalUpgradeUpdate(String serial_number , int total_package , int current_package)
	{
		String tParam = "serial_number=" + serial_number + "&total_package=" + total_package + "&current_package=" + current_package ;
		
		String rString = HttpRequest.sendPost(gpm.getValue(gpm.RestfulAPIServerHost)+"com.am.nw.api.ApiTerminalUpgradeUpdate.do", tParam);

		try {
			JSONObject jsonObject = new JSONObject(rString);
			return jsonObject;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 终端升级查看API
	 * @param serialNumber		终端命令流水号
	 * @return {DATA:[ {CmdContent:{},total_package,current_package}]}
	 */
	public JSONObject SendTerminalUpgradeSelect(String serial_number)
	{
		String tParam = "serial_number=" + serial_number;
		
		String rString = HttpRequest.sendPost(gpm.getValue(gpm.RestfulAPIServerHost)+"com.am.nw.api.ApiTerminalUpgradeSelect.do", tParam);

		try {
			JSONObject jsonObject = new JSONObject(rString);
			return jsonObject;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}
