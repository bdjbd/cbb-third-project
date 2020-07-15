package com.am.frame.notice;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.push.server.PushNotification;
import com.fastunit.Ajax;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月21日
 * @version 说明:<br />
 *          发现消息Action
 */
public class SendNoticeAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {

		// 1，获取通知内容
		String noticeIds = ac.getRequestParameter("noticeid");

		String[] noticeId = noticeIds.split(",");
		
		String acParam=ac.getActionParameter();
		
		PushNotification pn=null;

		if (!Checker.isEmpty(noticeId)) {
			
			//遍历多个通知进行发送
			for (int j = 0; j < noticeId.length; j++) {

				String findNoticeSQL = "SELECT * FROM am_notice  WHERE id=?";

				MapList map = db.query(findNoticeSQL, noticeId[j], Type.VARCHAR);

				if (!Checker.isEmpty(map)) {

					// 2，根据通知ID获取通知消息接受范围的用户的DBPustUserId和BDPushChannelID
					Row noticeRow = map.getRow(0);

					String orgcode = noticeRow.get("orgcode");

					if("terminal".equals(acParam)){
						//给所有终端发送
						String getMemberSQL = "SELECT * FROM am_member  WHERE orgcode LIKE '"+ orgcode + "%' ";

						MapList userMap = db.query(getMemberSQL);

						JSONObject tJsonObj = new JSONObject();
						try {
							tJsonObj.put("ID",noticeRow.get("id"));
						} catch (JSONException e) {
							e.printStackTrace();
						}

						if (!Checker.isEmpty(userMap)) {
							// 3，循环将通知内容发生到用户
							for (int i = 0; i < userMap.size(); i++) {

								Row userRow = userMap.getRow(i);

								Long channelId = 0l;
								String userId = "";
								try {
									// 防止解析ChannelID出错！
									channelId = Long.parseLong(userRow
											.get("bdpushchannelid"));
									userId = userRow.get("bdpushuserid");

									if (Checker.isEmpty(userId)) {
										throw new Exception("百度推送，用户ID为空！");
									}
								} catch (Exception e) {
									continue;
								}

								// 推送消息
								pn= new PushNotification(userId,
										channelId);

								pn.send(noticeRow.get("title"),
										noticeRow.get("createdate"), tJsonObj,
										userRow.getInt("bdpushdevicetype", 3));
							}
						}
					}else{
						
						String getMemberSQL = "SELECT * FROM am_terminal WHERE orgcode LIKE '"+ orgcode + "%' ";

						MapList userMap = db.query(getMemberSQL);

						JSONObject tJsonObj = new JSONObject();
						try {
							tJsonObj.put("ID",noticeRow.get("id"));
						} catch (JSONException e) {
							e.printStackTrace();
						}

						if (!Checker.isEmpty(userMap)) {
							// 3，循环将通知内容发生到用户
							for (int i = 0; i < userMap.size(); i++) {

								Row userRow = userMap.getRow(i);

								Long channelId = 0l;
								String userId = "";
								try {
									// 防止解析ChannelID出错！
									channelId = Long.parseLong(userRow
											.get("bdchannelid"));
									userId = userRow.get("bduserid");

									if (Checker.isEmpty(userId)) {
										throw new Exception("百度推送，用户ID为空！");
									}
								} catch (Exception e) {
									continue;
								}

								// 推送消息
								pn= new PushNotification(userId,
										channelId);

								pn.send(noticeRow.get("title"),
										noticeRow.get("createdate"), tJsonObj,
										userRow.getInt("bdpushdevicetype", 3));
							}
						}
					}
					
					
					
					//更新通知发送次数和最后发送日期
					String updateInfoSQL="UPDATE am_notice SET senddate=now(),count=COALESCE(count,0)+1 WHERE id=?";
					db.execute(updateInfoSQL,noticeRow.get("id"),Type.VARCHAR);
				}
			}

			Ajax ajax = new Ajax(ac);
			ajax.addScript("alert('发送完成！');location.reload();");
			ajax.send();

		}
		
		//设置返回路径
		ac.getActionResult().setSuccessful(true);
		ac.getActionResult().setUrl("/am_bdp/am_notice_assembly.do?m=s");

	}
}
