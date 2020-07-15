package com.am.frame.push.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.p2p.service.IWebApiService;

import cn.jiguang.commom.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;

public class GetVerificationById implements IWebApiService{

	//在极光注册上传应用的 appKey 和 masterSecret  
	private static final String appKey ="7e24b21e7e277ff09a82c2bf";
	private static final String masterSecret = "91e35dba7745eaf7be5c2fd1";
    public static final String TAG = "tag_api";
	
	public static void main(String[] args) {
		ClientConfig clientConfig = ClientConfig.getInstance();
		clientConfig.setTimeToLive(10);
		clientConfig.setMaxRetryTimes(1);
		JPushClient jpushClient = new JPushClient(masterSecret,appKey,null,clientConfig);
		
		try {
			System.out.println(jpushClient.isDeviceInTag("1", "13065ffa4e3b646a1d3"));
		} catch (APIConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (APIRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		
		ClientConfig clientConfig = ClientConfig.getInstance();
		clientConfig.setTimeToLive(10);
		clientConfig.setMaxRetryTimes(1);
		JPushClient jpushClient = new JPushClient(masterSecret,appKey,null,clientConfig);
		
		
			
			DBManager db = new DBManager();
			
			String SQL = "SELECT * FROM mall_mobile_type_record";
			
			MapList maplist = db.query(SQL);
			
			for (int i = 0; i < maplist.size(); i++) {
				
				try {
					System.out.println(jpushClient.isDeviceInTag("tag_api", maplist.getRow(i).get("xtoken")));
				} catch (APIConnectionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (APIRequestException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		
		return null;
	}

}
