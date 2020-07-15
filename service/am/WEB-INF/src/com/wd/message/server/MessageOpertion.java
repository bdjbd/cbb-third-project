package com.wd.message.server;

import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;

import com.wd.comp.Constant;
import com.wd.database.DataBaseFactory;
import com.wd.message.SendMessageToAndroidClient;
import com.wd.socket.SocketServer;
import com.wd.tools.DatabaseAccess;

/**
 * 消息操作类 依fastunit提供的连接操作数据库
 */
public class MessageOpertion {

	/**
	 * 将新消息的接收者发给服务器，通知推送。
	 * 
	 * @param receiver
	 *            接收者账号，多个账号之间用逗号分割
	 */
	private void pushMessage(String receivers) {
		new SocketServer().NotifyPush(receivers);
		// 发送消息给android
		SendMessageToAndroidClient.sentMessageToAllClient(receivers);
	}

	/**
	 * 将要发送的消息保存到数据库，如果有多个接收者，会拆成多条信息保存。
	 * 
	 * @param content
	 *            消息内容
	 * @param senderuser
	 *            发送者
	 * @param receiver
	 *            接收者，多个接收者以英文逗号隔开
	 * @param msgType
	 *            消息类型
	 * @param pcurl
	 *            pc端链接
	 * @param androidurl
	 *            android端链接
	 * @return 成功失败标识
	 */
	public String addMessage(String content, String senderuser,
			String receiver, String msgType, String pcUrl, String androidUrl) {
		String[] receiverArray = receiver.split(",");
		for (String receiverUserID : receiverArray) {
			String id=UUID.randomUUID().toString();
			String sql = "insert into ABDP_MESSAGE"
					+ "(id,Content,Senderuser,Sendertime,Receiveuser,Messagrtype,pcUrl,androidUrl)"
					+ "values('"+id+"','" + content + "','" + senderuser
					+ "'," + DataBaseFactory.getDataBase().getSysdateStr()
					+ ",'" + receiverUserID + "','" + msgType + "','" + pcUrl
					+ "','" + androidUrl + "')";
			DatabaseAccess.execute(sql);
		}
		// 发送消息
		pushMessage(receiver);
		return Constant.SUCCESS;
	}
	
	/**
	 * 根据机构推送消息
	 * 将要发送的消息保存到数据库，会拆成多条信息保存。
	 * 
	 * @param content
	 *            消息内容
	 * @param senderuser
	 *            发送者
	 * @param org
	 *            机构
	 * @param msgType
	 *            消息类型
	 * @param pcurl
	 *            pc端链接
	 * @param androidurl
	 *            android端链接
	 * @return 成功失败标识
	 */
	public String addMessageByOrg(String content, String senderuser,
			String org, String msgType, String pcUrl, String androidUrl) {
		String receivers = "";
		String strSql = "select userid from auser where orgid='"+org+"'";
		JSONArray js = DatabaseAccess.query(strSql);
		for(int i = 0;i<js.length();i++){
			try {
				if(i == 0){
					receivers  = js.getJSONObject(i).getString("USERID");
				}else {
					receivers += ","+ js.getJSONObject(i).getString("USERID");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.addMessage(content, senderuser, receivers, msgType, pcUrl, androidUrl);
		
		return Constant.SUCCESS;
	}
	
	
	
	/**
	 * 根据用户组推送消息
	 * 将要发送的消息保存到数据库，会拆成多条信息保存。
	 * 
	 * @param content
	 *            消息内容
	 * @param senderuser
	 *            发送者
	 * @param groupid
	 *            用户组
	 * @param msgType
	 *            消息类型
	 * @param pcurl
	 *            pc端链接
	 * @param androidurl
	 *            android端链接
	 * @return 成功失败标识
	 */
	public String addMessageByGroup(String content, String senderuser,
			String groupid, String msgType, String pcUrl, String androidUrl) {
		String receivers = "";
		String strSql = "select userid from ausergroup where groupid='"+groupid+"'";
		JSONArray js = DatabaseAccess.query(strSql);
		for(int i = 0;i<js.length();i++){
			try {
				if(i == 0){
					receivers  = js.getJSONObject(i).getString("USERID");
				}else {
					receivers += ","+ js.getJSONObject(i).getString("USERID");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.addMessage(content, senderuser, receivers, msgType, pcUrl, androidUrl);
		
		return Constant.SUCCESS;
	}
	
	

	/**
	 * 查询指定接受者的消息
	 * 
	 * @param receiver
	 *            接收者账号
	 * @return org.JSONArray
	 */
	public JSONArray selectMessage(String receiveUserID) {
		String sql ="select id,Content,to_char(Sendertime,'yyyy-MM-DD HH24:MI:ss')as Sendertime,a.username as Senderuser,Messagrtype,pcurl,androidurl "
				+ " from ABDP_MESSAGE m left join auser a on a.userid = m.Senderuser"
				+ " where Receiveuser='"+receiveUserID+"' order by Sendertime desc";
		return DatabaseAccess.query(sql);
	}

	/**
	 * 批量删除消息
	 * 
	 * @param ids
	 *            多个id之间用逗号分割
	 * @return 成功失败标识
	 */
	public String deleteMessage(String ids) {
		String[] idsArray = ids.split(",");
		for (String str : idsArray) {
			String sql = "delete from ABDP_MESSAGE where id ='" + str + "'";
			DatabaseAccess.execute(sql);
		}
		return Constant.SUCCESS;
	}

	/**
	 * 删除指定人所有消息
	 * 
	 * @param ids
	 *            多个id之间用逗号分割
	 * @return 成功失败标识
	 */
	public String deleteAllMessage(String receiveUserID) {
		String sql = "delete from ABDP_MESSAGE where RECEIVEUSER ='"
				+ receiveUserID + "'";
		DatabaseAccess.execute(sql);
		return Constant.SUCCESS;
	}
}