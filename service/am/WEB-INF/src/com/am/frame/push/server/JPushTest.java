package com.am.frame.push.server;

import java.util.Collection;
import java.util.LinkedList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.p2p.service.IWebApiService;


public class JPushTest implements IWebApiService {

	public static final String registrationID ="171976fa8a89f79ab6b";
//	mi5:18071adc0304e05b243
//	zte:120c83f7602d278d51e
//	minote:140fe1da9ea275d924d
//	samsung:190e35f7e04c8fddb7f
	
	public static void main(String[] args) {
		//注册id
		Collection<Object> coll = new LinkedList<Object>();
		coll.add(registrationID);
		
		
		
		JSONObject json = new JSONObject();
		JSONObject data = new JSONObject();
		JSONObject url = new JSONObject();
	
		try {
			json.put("title", "理性农业");
			json.put("describe", "哈哈");
			
			url.put("curseid", "fe2331e0-5b3f-4975-bf27-e4c916b8adc9");
			url.put("acbid", "76e7cc67-d7b4-4f29-8908-7f711fa30383");
			url.put("signtype", "1");
			url.put("cscode", "482d732b-631f-4ef2-91dd-2cdbda9d71cb_1_1ad864fd-e1fd-4f67-8dcd-dc968f91dd38_1_2");
			data.put("url","common.boySign");//common.boySign
			data.put("params", url);
			json.put("DATA",data);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		JPushNotice.sendNoticeById(json, coll, "1");
	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		
		//注册id
				Collection<Object> coll = new LinkedList<Object>();
				coll.add(registrationID);
				
				
				JSONObject json = new JSONObject();
				JSONObject data = new JSONObject();
				JSONObject url = new JSONObject();
				
				try {
					json.put("title", "职联宝");
					json.put("describe", "您该签到了!");
					
					url.put("curseid", "fe2331e0-5b3f-4975-bf27-e4c916b8adc9");
					url.put("acbid", "76e7cc67-d7b4-4f29-8908-7f711fa30383");
					url.put("signtype", "1");
					url.put("cscode", "482d732b-631f-4ef2-91dd-2cdbda9d71cb_1_1ad864fd-e1fd-4f67-8dcd-dc968f91dd38_1_2");
					data.put("url","common.boySign");//common.boySign
					data.put("params", url);
					json.put("DATA",data);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				JPushNotice.sendNoticeById(json, coll, "2");
		
		return null;
	}
}
