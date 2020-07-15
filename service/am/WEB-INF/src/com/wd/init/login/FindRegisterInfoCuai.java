package com.wd.init.login;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import com.fastunit.MapList;
import com.fastunit.User;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.user.UserFactory;
import com.fastunit.util.Checker;
import com.wd.ICuai;
import com.wd.database.DataBaseFactory;
import com.wd.init.mainActivity.FindLoginUserMainActivitySetCuai;
import com.wd.init.permission.FindPermissionByUserID;
import com.wd.message.FindMessageCuai;
import com.wd.tools.DatabaseAccess;
import com.wd.tools.Hmac;

/**
 * 查找设备注册信息
 * 
 * @author llm,zhouxn
 * @time 2012-5-12 
 */
public class FindRegisterInfoCuai implements ICuai {
	private static final Logger LOG = LoggerFactory
			.getLogger(FindRegisterInfoCuai.class);
	/**
	 * 查找设备注册信息,当前用户的基本信息,当前用户的历史消息以及主活动
	 * @param 设备信息
	 * @return 设备注册信息,当前用户的基本信息,当前用户的历史消息以及主活动
	 * 其中，returnStr为false时表示未授权,true表示已授权，-1表示查询失败
	 */
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		LOG.info("登录参数："+jsonArray.toString()+"登录时间："+getSystemDateTime());
		String returnStr = "false";
		try { 
			String pImei = jsonArray.getString(0).toString();
			String phoneStr = jsonArray.getString(1);
			JSONArray js_user = jsonArray.getJSONArray(2);
			User user = UserFactory.getUser(js_user.getString(0));
			String orgid="";
			if(user!=null)
			{
				//获取当前登录人的机构
				orgid=user.getOrgId();
			}
			JSONObject js = this.getAuthorizecodeByImei(pImei);
			// 如果木有记录,则往数据库中添加记录。
			if (js == null || js.length()<=0) {
				if (phoneStr.equals("null") || phoneStr.length() == 0
						|| phoneStr == null) {
					phoneStr = "";
				}
				String sql2 = "insert into abdp_terminalmanage(tid,imei,regdate,phoneno) values('"
						+ UUID.randomUUID().toString()
						+ "','"
						+ jsonArray.getString(0)
						+ "',"
						+ DataBaseFactory.getDataBase().getSysdateStr()
						+ ",'"
						+ phoneStr + "')";
				DatabaseAccess.execute(sql2);
			}else if(js.toString().indexOf("errorMsg")>=0){
				//查询授权信息时发生异常
				returnStr = "-1";
			}
			else {
				String authorizecode = js.getString("AUTHORIZECODE").trim();
				String regorgid=js.getString("ORGID");
				if (authorizecode != null && authorizecode.length() != 0
						&& !authorizecode.equals("null")) {
					String eAuthorizecode = Hmac.encryptHMAC(pImei);
					if(regorgid==null || regorgid.trim().equalsIgnoreCase(""))
					{
						// 验证授权码
							if (eAuthorizecode.equals(authorizecode) ) {
								returnStr = "true";
							}
					}
					else
					{
						// 验证授权码和机构,只能是授权给机构下的用户才能使用该终端
						if (eAuthorizecode.equals(authorizecode) && regorgid.equalsIgnoreCase(orgid)) 
						{
							returnStr = "true";
						}
					}
				}
			}
		} catch (Exception e) {
			returnStr = "-1";
			e.printStackTrace();
		}
		JSONArray js = new JSONArray();
		js.put(returnStr);
		
		JSONArray userParam=null;
		try {
			userParam = jsonArray.getJSONArray(2);
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		//验证用户信息
		findUserInfo(userParam,js);
		//查询历史消息
		findMessage(userParam,js);
		//查询当前用户的主活动
		findMainActivity(userParam,js);
		//检查当前登录人是否需要同步离线数据
		checkOffLine(userParam,js);
		return js;
	}

	private JSONObject getAuthorizecodeByImei(String imei) {
		JSONObject returnJsonObject = new JSONObject();
		try {
			String sql = "select tid,t.imei,t.authorizecode,orgid from abdp_TERMINALMANAGE t where t.imei='"
					+ imei + "'";
			JSONArray js = DatabaseAccess.query(sql);
			if (js != null && js.length() > 0) {
				//LOG.info("终端授权信息:"+js.toString()+",时间:"+getSystemDateTime());
				returnJsonObject =new JSONObject(js.getString(0));
			}
		} catch (JSONException e) {
			//LOG.info("查询终端设备授权信息发生异常,时间:"+getSystemDateTime());
			try {
				returnJsonObject.put("errorMsg","-1");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return returnJsonObject;
	}
	
	/**
	 * 验证当前登录人的身份
	 * @param userParam 用户名和密码
	 * @param js 给android端返回的数据集合
	 * */
	private void findUserInfo(JSONArray userParam,JSONArray js)
	{
		if(userParam==null)
		{
			return;
		}
		//LOG.info("验证当前登录人的身份,参数:"+userParam.toString()+",时间:"+getSystemDateTime());
		//验证用户身份
		FindUserInfoCuai user=new FindUserInfoCuai();
		JSONArray userReg=user.doAction(userParam);
		if(userReg!=null)
		{
			//LOG.info("获取当前登录人的信息,参数:"+userReg.toString()+",时间:"+getSystemDateTime());
			js.put(userReg);
		}
	}
	
	/**
	 * 查询当前登录人的历史消息
	 * @param userParam 用户名和密码
	 * @param js 给android端返回的数据集合
	 * */
	private void findMessage(JSONArray userParam,JSONArray js)
	{
		if(userParam==null)
		{
			return;
		}
		//LOG.info("查询当前登录人的历史消息,参数"+userParam.toString()+",时间："+getSystemDateTime());
		JSONArray js_receive=new JSONArray();
		JSONObject jo=new JSONObject();
		try {
			jo.put("receiveUserID",userParam.get(0));
			js_receive.put(jo);
			FindMessageCuai message=new FindMessageCuai();
			JSONArray messageJs=message.doAction(js_receive);
			if(messageJs!=null)
			{
				js.put(messageJs);
			}
		} catch (JSONException e) {
			//LOG.info("查询当前登录人的历史消息发生了异常,时间："+getSystemDateTime());
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 查询当前登录人的主活动
	 * @param userParam 用户名和密码
	 * @param js 给android端返回的数据集合
	 * */
	private void findMainActivity(JSONArray userParam,JSONArray js)
	{
		if(userParam==null)
		{
			return;
		}
		//LOG.info("查询当前登录人的主活动,参数"+userParam.toString()+",时间："+getSystemDateTime());
		FindLoginUserMainActivitySetCuai mainActivity=new FindLoginUserMainActivitySetCuai();
		JSONArray mainActivityJs=mainActivity.doAction(userParam);
		if(mainActivityJs!=null)
		{
			//LOG.info("获得当前登录人的主活动,主活动"+mainActivityJs.toString()+",时间："+getSystemDateTime());
			js.put(mainActivityJs);
		}
	}
	
	/**
	 * 获取当前系统时间
	 * */
	private String getSystemDateTime()
	{
		Calendar ca = Calendar.getInstance();
		Date now =ca.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(now);
	}
	
	/**
	 * 检查当前登录人是否需要同步离线数据
	 * */
	private void checkOffLine(JSONArray jsonArray,JSONArray data) {
		DB db=null;
		try {
			db=DBFactory.getDB();
		} catch (JDBCException e1) {
			e1.printStackTrace();
		}
		FindPermissionByUserID findPer=new FindPermissionByUserID();
		//获取当前登录人模块权限集合
		JSONArray js=findPer.doAction(jsonArray);
		if(js!=null)
		{
			String mid="";
			try {
				for (int i = 0; i < js.length(); i++) {
					JSONObject jo = js.getJSONObject(i);
					JSONArray childMenuList = jo.getJSONArray("childMenuList");
					for (int j = 0; j < childMenuList.length(); j++) {
						JSONObject jon=childMenuList.getJSONObject(j);
						mid+="'"+jon.getString("ID")+"',";
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if(!mid.equalsIgnoreCase("") && mid.endsWith(","))
			{
				mid=mid.substring(0,mid.length()-1);
			}
			
			String sql="select tablename,selectsql,createsql from abdp_offlinemoduel a,abdp_offlinetable b " +
					"where a.tid=b.tid and a.mid in ("+mid+") order by id";
			MapList list=null;
			try {
				if(mid!=null && !mid.trim().equalsIgnoreCase(""))
				{
					list=db.query(sql);
				}
			} catch (JDBCException e) {
				e.printStackTrace();
			}
			if(!Checker.isEmpty(list) && list.size()>0)
			{
				data.put("true");
			}
			else
			{
				data.put("false");
			}
		}
		
	}
}
