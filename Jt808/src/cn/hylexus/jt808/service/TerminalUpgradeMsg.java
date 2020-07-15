package cn.hylexus.jt808.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.hylexus.jt808.common.TPMSConsts;
import cn.hylexus.jt808.server.GlobalParametersManager;
import cn.hylexus.jt808.service.codec.MsgEncoder;
import cn.hylexus.jt808.util.ActionManagerService;
import cn.hylexus.jt808.util.HexStringUtils;
import cn.hylexus.jt808.vo.resp.TerminalUpgradeMsgBody;
import io.netty.channel.Channel;
/**
 * 终端升级消息处理
 * @author 王成阳
 * 2018-3-8 
 */
public class TerminalUpgradeMsg extends BaseMsgProcessService{

    private GlobalParametersManager gpm = GlobalParametersManager.getInstance();
    private MsgEncoder msgEncoder;
    private TerminalUpgradeMsgBody terminalUpgradeMsgBody;
    ActionManagerService ams=ActionManagerService.getInstance();
    
    public TerminalUpgradeMsg(){
    	this.msgEncoder = new MsgEncoder();
    	this.terminalUpgradeMsgBody = new TerminalUpgradeMsgBody();
    }
    
    /** 
     * 发送第一包升级数据 
	 * @param  cmdContent 命令内容 
	 * @param  channelID  管道id
     */
    public void processTerminalUpgradeMsg(JSONObject cmdContent , Channel channelID){
    	
    	log.debug(">>>>>[远程升级第一包数据处理] = " + cmdContent);
    	
    	int flowId = super.getFlowId(channelID);
    	
    	try {
			JSONObject content = new JSONObject(cmdContent.getString("CMDCONTENT"));
			System.err.println("222" + content);
			//获得内容JSONARRAY数组
			JSONArray contentArray = new JSONArray(content.getString("DATA"));
			System.err.println("333" + contentArray);
			//将数组转换成JSON对象
			JSONObject contentObject = contentArray.getJSONObject(0);
			System.err.println("444" + contentObject);
			//获得内容的filePath数组
			JSONArray filePathArray = new JSONArray(contentObject.getString("FILE_PATH"));
			System.err.println("555" + filePathArray);
			//将数组转换成JSON对象
			JSONObject filePathObject = filePathArray.getJSONObject(0);
			System.err.println("666" + filePathObject);
			
    		//终端手机号
    		String terminalPhone = cmdContent.getString("DEVICE_SN_NUMBER");
    		log.debug("[输出终端手机号] = " + terminalPhone);
			//版本号
			String versionNumber = contentObject.getString("VERSION_NUMBER");
			log.debug("[升级版本号] = " + versionNumber);
//			System.err.println("升级版本号长度 = " + versionNumber.length());
			//filePath的path值
			String path = filePathObject.getString("path");
			log.debug("[升级地址]" + path);
	    	
	    	//读取文件获得字节数组
			byte[] cmdContentByte =  getCmdContent(path);
			log.debug("---------------升级文件字节---------------" + HexStringUtils.toHexString(cmdContentByte));
			//System.err.println("输出字节长度:" + cmdContentByte.length);
			
			int len = cmdContentByte.length;
			log.debug("[字符串文件长度]" + len);
			
			//获得每组升级数据长度
			//1024-消息头-消息体(升级类型+制造商ID+版本号长度+版本号+升级数据包长度)-校验码
			//1024-16-(1+5+1+versionNumber.length()+4)-1
			int every = TPMSConsts.EVERY_LENGTH;
//			log.debug("********************每组：" +  every + "个");
			
			//分组数 = 数据总长度/每组数据长度 + 1 (因为有余数,不管醉红藕一组有多少数据,都被分为一组)  
			int group = len/every; 
//			log.debug("********************一共分了：" + (group+1) + "组");
			//余数
			int remainder = len%every;
//			log.debug("********************余数：" + remainder);
			
    		//升级类型  (0：终端，12：道路运输证 IC 卡读卡器，52：北斗卫星定位模块) 
        	terminalUpgradeMsgBody.setUpgradeType(0);
        	//制造商ID
        	terminalUpgradeMsgBody.setManufacturerID(0);
        	//版本号长度
        	terminalUpgradeMsgBody.setVersionNumLength(versionNumber.length());
        	//版本号
        	terminalUpgradeMsgBody.setVersionNumber(versionNumber);
			//升级数据包长度
        	terminalUpgradeMsgBody.setUpgradeDataLength(every);
        	//升级数据
        	byte[] groupByte = new byte[TPMSConsts.EVERY_LENGTH];
    		System.arraycopy(cmdContentByte, 0, groupByte, 0, groupByte.length);
    		terminalUpgradeMsgBody.setUpgradeData(groupByte);
    		
    		
    		
			//调API把总包数及第一包数据存进数据库
    		JSONObject terminalUpgradeUpdate = ams.SendTerminalUpgradeUpdate(sessionManager.SerialNumber, group+1, 1);
    		
    		log.debug("[数据包长度] = " + groupByte.length);
    		log.debug("[第一包终端升级数据] = " + HexStringUtils.toHexString(groupByte));
    		log.debug("[第一包数据更新结果] = " + terminalUpgradeUpdate);
    		
	        //编码产生新的报文
            byte[] bs = this.msgEncoder.encode4TerminalUpgradeRespOne(terminalUpgradeMsgBody, flowId, terminalPhone, group+1, 1);
            //发送终端
            super.send2Client(channelID, bs);
			log.debug("[下发第一包升级包] = " + HexStringUtils.toHexString(bs));
			
            
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	
	/**
	 * 获得升级文件字节数组
	 * @param path 文件路径
	 * @return buffer 升级文件字节数组
	 */
	public byte[] getCmdContent(String path) {
		
		byte[] buffer = new byte[1024];
		
		//http文件请求
		URL url;
		
		//输入流
		InputStream fis = null;
		
		//存放输入流的缓冲对象
		BufferedInputStream bis = null; 
		
		//输出流
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 

		try {
			
			url = new URL(gpm.getValue(gpm.AnnexServerHost) + path);
			
			//把文件路径告诉流
			fis =  url.openStream();
			
			//把文件读取流对象传递给缓存读取流对象
			bis = new BufferedInputStream(fis);  
			
			//获得缓存读取流开始的位置
			int len = 0;
			
			//定义一个容量来盛放
            byte[] buf = new byte[1024]; 

            while ((len = bis.read(buf)) != -1) {  
                // 如果有数据的话，就把数据添加到输出流  
                //这里直接用字符串StringBuffer的append方法也可以接收  
                baos.write(buf, 0, len);  
            }  
            
            // 把文件输出流的数据，放到字节数组  
            buffer = baos.toByteArray();  
            
            //System.err.println(HexStringUtils.toHexString(buffer));
		
        } catch (Exception e) {
			e.printStackTrace();
		}finally {
			 try {
				//关闭所有流
				baos.close();
	            bis.close();  
	            fis.close();  
			} catch (IOException e) {
				e.printStackTrace();
			}  
		} 
		return buffer;
	}
}
