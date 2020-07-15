package com.am.frame.push.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jiguang.commom.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

import com.fastunit.Var;

/**
 * 极光推送server类
 * @author 鲜琳
 *2016-9-28 10:18:57
 */

public class JPushNotice {

    protected static final Logger LOG = LoggerFactory.getLogger(JPushNotice.class);

    //在极光注册上传应用的 appKey 和 masterSecret  
	private static final String appKey =Var.get("jpush_appKey");
	private static final String masterSecret = Var.get("jpush_masterSecret");
    public static final String TAG = "tag_api";
   
	/**
	 * 发送通知至单个消息
	 * @param obj
	 * @param coll
	 * @param type
	 */
	public static void sendNoticeById(JSONObject obj,Collection<Object> coll,String type)
	{
		ClientConfig clientConfig = ClientConfig.getInstance();
		clientConfig.setTimeToLive(10);
		clientConfig.setMaxRetryTimes(1);
		JPushClient jpushClient = new JPushClient(masterSecret,appKey,null,clientConfig);
		
		try{
			PushPayload payload = null;
			//android
			if("1".equals(type))
			{
				payload = buildPushObject_all_registrationID_android_alert(coll, obj);
			
			}else if("2".equals(type)) //ios
			{
				payload = buildPushObject_ios_tagAnd_alertWithExtrasAndMessage(coll, obj);
			}
			
			PushResult result = jpushClient.sendPush(payload);
			LOG.info("Got result - " + result);
			
		} catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
            
        }
		catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Code: " + e.getErrorCode());
            LOG.info("Error Message: " + e.getErrorMessage());
            LOG.info("Msg ID: " + e.getMsgId());
        } catch (Exception e) {
//			e.printStackTrace();
        	LOG.error("Error response from JPush server.Exception Should review and fix it. ", e);
		}
		
	}
	
	/**
	 * 发送通知到所有设备
	 * @param obj
	 */
	public void sendNoticeAll(JSONObject obj)
	{
		ClientConfig clientConfig = ClientConfig.getInstance();
		clientConfig.setTimeToLive(10);
		JPushClient jpushClient = new JPushClient(masterSecret,appKey,null,clientConfig);
		
		try{
			PushPayload payload = null;
			
			payload = buildPushObject_android_and_ios(obj);
			
			PushResult result = jpushClient.sendPush(payload);
			LOG.info("Got result - " + result);
			
		} catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
            
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Code: " + e.getErrorCode());
            LOG.info("Error Message: " + e.getErrorMessage());
            LOG.info("Msg ID: " + e.getMsgId());
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 推送至所有安卓设备、苹果设备
	 * @describe 描述
	 * @title 标题
	 * @DATA 附带参数
	 * @return
	 * @throws JSONException 
	 */
	private static PushPayload buildPushObject_android_and_ios(JSONObject obj) throws JSONException {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.all())
                .setNotification(Notification.newBuilder()
                		.setAlert(obj.get("describe"))
                		.addPlatformNotification(AndroidNotification.newBuilder()
                				.setTitle(obj.getString("title")).build())
                		.addPlatformNotification(IosNotification.newBuilder()
                				.incrBadge(1)
                				.addExtra("jsonExtra",obj.getJSONObject("DATA").toString()).build())
                		.build())
                .build();
    }
	
	 
	 /**
	  * 按照注册id推送给苹果设备,可多个
	  * @param coll
	  * @param obj
	  * @return
	  * @throws JSONException
	  */
	private static PushPayload buildPushObject_ios_tagAnd_alertWithExtrasAndMessage(Collection<Object> coll,JSONObject obj) throws JSONException {
	     
		Iterator<Object> s = coll.iterator();
		
		String[] arr = null;
		
		ArrayList list = new ArrayList();

		while (s.hasNext()) {
			list.add(s.next());
		}
		
		arr = (String[])list.toArray(new String[list.size()]);
		
		
		 	
		 	return PushPayload.newBuilder()
	                .setPlatform(Platform.ios())
	                .setAudience(Audience.registrationId(arr))
	                .setNotification(Notification.newBuilder()
	                        .addPlatformNotification(IosNotification.newBuilder()
	                                .setAlert(obj.get("title"))
	                                .setBadge(1)
	                                .addExtra("jsonExtra", obj.getJSONObject("DATA").toString())
	                                .build())
	                        .build())
	                 .setMessage(Message.content(obj.get("describe").toString()))
	                 .setOptions(Options.newBuilder()
	                         .setApnsProduction(Boolean.parseBoolean(Var.get("jpush_IOS_status")))
	                         .build())
	                 .build();
	    }
	
	/**
	 * 按照注册id推送安卓设备,可多个
	 * @param coll
	 * @return
	 * @throws JSONException 
	 */
	private static PushPayload buildPushObject_all_registrationID_android_alert(Collection coll,JSONObject obj) throws JSONException {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.registrationId(coll))
                .setNotification(Notification.newBuilder()
                				.setAlert(obj.get("describe"))
                				.addPlatformNotification(AndroidNotification.newBuilder()
                					.setTitle(obj.getString("title"))
                					.addExtra("jsonExtra",obj.getJSONObject("DATA").toString()).build()
                				).build()).build();
    }
}
