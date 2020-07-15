package com.am.frame.notice.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import com.am.frame.push.server.JPushNotice;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.util.Checker;


/**
 * 汽车公社 发送通知管理类  原始备份  241行与274行的判断条件有误，建议修改
 * @author mac
 *
 */
public class SendNoticeManager_1 {
	
	
	
	
	/**
	 * 单个发送通知 同时插入 通知关系表
	 * @param member_id   会员id
	 * @param contentJso  内容json   {title:'标题',content:'内容',ur:'链接'}
	 */
	public void sendOne(DB db,MapList noticelist,String member_id) throws Exception
	{
		
		String sql = "SELECT * FROM mall_mobile_type_record WHERE member_id = '"+member_id+"'";
		MapList mapList = db.query(sql);
		if(!Checker.isEmpty(mapList)){
			
			String url =noticelist.getRow(0).get("url");
			
			JSONObject json = new JSONObject();
			JSONObject data = new JSONObject();
			
			json.put("title", noticelist.getRow(0).get("title"));
			json.put("describe",noticelist.getRow(0).get("content"));
			data.put("url",url.substring(0, url.indexOf("/{")));
			data.put("params", new JSONObject(url.substring(url.indexOf("/{")+1,url.length())));
			json.put("DATA",data);

				
			for (int i = 0; i < mapList.size(); i++) {
				//1为android 2为ios
				if("1".equals(mapList.getRow(i).get("mobile_type"))){
					
					Collection<Object> coll = new LinkedList<Object>();
					coll.add(mapList.getRow(i).get("xtoken"));
					
					JPushNotice.sendNoticeById(json, coll, "1");
					
				}else if("2".equals(mapList.getRow(i).get("mobile_type"))){
					
					Collection<Object> coll = new LinkedList<Object>();
					coll.add(mapList.getRow(i).get("xtoken"));
					
					JPushNotice.sendNoticeById(json, coll, "2");
					
				}
			}
		}
		
	}
	
		
	/**
	 * 发送到所有用户通知
	 * @param db
	 * @param id
	 * @param noticeMapList
	 * @throws Exception
	 */
	public void sendAllUserMenber (DB db,MapList noticeMapList) throws Exception
	{
		//发送所有的用户设备
		String mql = "select * from am_member";
		
		MapList mmlist = db.query(mql);
		
		String ssql = "SELECT mmtr.* FROM am_member AS am "
				+ "LEFT JOIN mall_mobile_type_record AS mmtr ON am.id = mmtr.member_id ";
		
		MapList iMapList = db.query(ssql);
		
		if(!Checker.isEmpty(iMapList))
		{
				
			String url =noticeMapList.getRow(0).get("url");
			
			JSONObject json = new JSONObject();
			JSONObject data = new JSONObject();
			
			json.put("title", noticeMapList.getRow(0).get("title"));
			json.put("describe",noticeMapList.getRow(0).get("content"));
			data.put("url",url.substring(0, url.indexOf("/{")));
			data.put("params", new JSONObject(url.substring(url.indexOf("/{")+1,url.length())));
			json.put("DATA",data);
			
			sendList(db, iMapList, json);
		}

	}
	
	/**
	 * 
	 * @param db
	 * @param id 通知id
	 * @param noticeMapList 通知maplist
	 * @param label 会员标签名称
	 * @throws Exception
	 */
	public void sendByUserMenber (DB db,MapList noticeMapList,String label) throws Exception
	{
		//原始的左联接SQL
		String ssql ="SELECT mmtr.* FROM  mall_mobile_type_record as  mmtr "
				+ "LEFT JOIN  mall_member_label_relation  as mmlr on mmtr.member_id =  mmlr.member_id "
				+ "LEFT JOIN  mall_member_label as mml ON mmlr.label_id = mml.id"
				+ " WHERE mml.id  = '"+label+"' ";
		System.err.println("执行查询sql》》》"+ssql);
		MapList list = db.query(ssql);
		
		if(!Checker.isEmpty(list))
		{
			String url =noticeMapList.getRow(0).get("url");
			
			JSONObject json = new JSONObject();
			JSONObject data = new JSONObject();
			
			json.put("title", noticeMapList.getRow(0).get("title"));
			json.put("describe",noticeMapList.getRow(0).get("content"));
			data.put("url",url.substring(0, url.indexOf("/{")));
			data.put("params", new JSONObject(url.substring(url.indexOf("/{")+1,url.length())));
			json.put("DATA",data);
			System.err.println("注意拼接的json:"+json.toString());
			sendList(db, list, json);  //此行报错
		}else{
			System.err.println("警告：本地查询结果为空！》》》"+ssql);
		}
	}
	
	
	
	/**
	 * 多个发送通知  类似会员标签类的
	 * @param db
	 * @param memberList  会员设备类型 mapList
	 * @param contentJso  内容json   {title:'标题',content:'内容',ur:'链接'}
	 */
	public void sendList(DB db,MapList mapList,JSONObject contentJso) throws Exception
	{
		
		//用户ios key   待发送的苹果设备key集合（每一个元素都是String型）
		List<String> iosListKey = new ArrayList<String>();
		
		//用户android key  待发送的安卓设备key集合（每一台设备的key都是String型）
		List<String> androidListKey = new ArrayList<String>();
		
		for (int i = 0; i < mapList.size(); i++) {
		
			//1 android 2 ios
			if("1".equals(mapList.getRow(i).get("mobile_type"))){
				
				androidListKey.add(mapList.getRow(i).get("xtoken"));
				
			}else if("2".equals(mapList.getRow(i).get("mobile_type"))){
				
				iosListKey.add(mapList.getRow(i).get("xtoken"));
				
			}
		}
		
		//去重操作    去重后的安卓设备key集合
		List<String> androidlistTemp= new ArrayList<String>(); 
		
		Iterator<String> it=androidListKey.iterator();  
		 
		while(it.hasNext()){  
			 String a=it.next(); 
			 if(!Checker.isEmpty(a))
			 {
			  if(androidlistTemp.contains(a)){  
			   it.remove();  
			  }  
			  else{  
				  androidlistTemp.add(a);  
			  }  
			 }
		}
		
		//去重操作  去重后的ios设备key集合
		List<String> ioslistTemp= new ArrayList<String>();  
		
		Iterator<String> iit=iosListKey.iterator();  
		 
		while(iit.hasNext()){  
		  
			 String a=iit.next();
		  
			 if(!Checker.isEmpty(a))
			 {
				 
			 
			  if(ioslistTemp.contains(a)){  
				  iit.remove();  
			  }  
			  else{  
				  ioslistTemp.add(a);  
			  }  
			 }
		}
		//（去重后）设备key集合有值，说明目标设备确实存在 
		if(!Checker.isEmpty(ioslistTemp) || !Checker.isEmpty(androidlistTemp)){
			//信鸽推送，已不用
//			XingePushNotice push = new XingePushNotice();
			
			//将完整的安卓设备key分成 android_i 次（向上取整）发送，确保每次发送设备不超过950台
			int android_i  = (int)Math.ceil(androidlistTemp.size()/950);
			
			//把每一轮循环所截取的950台安卓设备key值集合片段，由ArrayL转存成Collection集合，每次循环最后，调用极光推送方法，可能极光推送需要用Collection格式的集合吧
			Collection androidcol = null;
			//用来存放每一小段区间的安卓设备集合
			List<String> android_list = null;
			//分n次发送，每次发送不超过950台;每次从第subscript台设备开始往后数950台设备进行发送，（比如：第一次发送从第0台到第950台设备，第二次发送给从第951台到第1901台设备，……）
			int subscript = 0;//每次发送的设备的起始下标，从第几台设备开始发送（其实就是0、951、1901……，确保每次发送不超过950台）
			//共有 list_size 台安卓设备
			int list_size = androidlistTemp.size();
			//从大集合中每次只取出950台设备，得到每一小段区间的设备集合，每一小段再分别调用发送方法，总共能发送几次，就循环几次，注意最后一段可能不足950台（用最后的下标控制截取台数）
			for (int i = 0; i <= android_i; i++) {
				//用来存放每一小段区间的安卓设备集合，前n-1段，该区间集合都是950台设备，最后一段可能不足950台
				android_list = new ArrayList<String>();
				//安卓设备总台数(大集合的size，下标已确定) < 本次发送的设备的起始下标(下标每次+950，可能会出现下标越界异常)
				if(list_size<subscript)  //建议将条件改为list_size<950  ，当大集合的数量不足950时，直接全部发送，此时只有一道循环
				{
					//从完整的安卓设备key集合中，取出一段子集合，从第0台设备（注意是下标）截取到第0+950台设备（注意是下标）
					//此时subscript=0，直接从大集合第0位截取到大集合最后一位，建议改为subList(0, list_size)
					android_list.addAll(androidlistTemp.subList(subscript, list_size));
				}else
				//如果大集合包含的设备数量，达到或超过950台，就需要分段发送了
				{
					//分多次分送，比如第一次从第0台到第950台（实际到第949台），0~949,950~1099,1100~2049,……
					android_list.addAll(androidlistTemp.subList(subscript, subscript+950));//此行报错
				}
				//起始下标从0开始，每次发送950台后，起始下标从950台的后一位开始
				subscript=subscript+951;
				//把本轮循环所截取的小集合片段，转成Collection集合，可能极光推送需要用Collection格式的集合吧
				androidcol = android_list;
				//对当前这950台设备，发送极光推送    参数：消息内容，这950台安卓设备key值集合，发送类型（1为安卓设备，2为ios设备）
				JPushNotice.sendNoticeById(contentJso,androidcol,"1");
				
			}
			
			
			//所要发送的设备集合，必须分成 ios_i 段，分段处理
			int ios_i  = (int)Math.ceil(ioslistTemp.size()/950);
			Collection ioscol = null;
			
			List<String> ios_list = null;
			
			int ios_subscript = 0;
			
			int ios_list_size = ioslistTemp.size();
			
			for (int i = 0; i <= android_i; i++) {
				android_list = new ArrayList<String>();
				if(ios_list_size<ios_subscript)  //建议将条件改为ios_list_size<950  ，当大集合的数量不足950时，直接全部发送，此时只有一道循环
				{
					android_list.addAll(ioslistTemp.subList(ios_subscript,ios_list_size));
				}else
				{
					android_list.addAll(ioslistTemp.subList(ios_subscript, ios_subscript+950));
				}
				
				ios_subscript=ios_subscript+951;
				ioscol = ios_list;
				
				JPushNotice.sendNoticeById(contentJso,ioscol,"2");
				
			}
			
			
//			
//			Collection ioscol= ioslistTemp;
//			
//			if(androidcol.size()>0)
//			{
//				JPushNotice.sendNoticeById(contentJso,androidcol,"1");
//			}
//			if(ioscol.size()>0)
//			{
//				JPushNotice.sendNoticeById(contentJso,ioscol,"2");
//			}
			
//			push.sendALlListDevice(contentJso, ioslistTemp, androidlistTemp);
			
		}
		
	}
	

}
