package com.am.frame.webapi.member;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * 用户登录
 * */
public class MemberLogin implements IWebApiService 
{
	
	final Logger logger = LoggerFactory.getLogger(MemberLogin.class);
	
	@Override
	public String execute(HttpServletRequest request,HttpServletResponse response)
	{
		
		//1=qq登录  2=微信登录  3=微博登录 空为正常登录
		String login_type = request.getParameter("logintype");
		String login_account = request.getParameter("login_account");
		String login_password = request.getParameter("login_password");
		String devicetype = request.getParameter("DeviceType");
		String street = request.getParameter("street");
		String lat = request.getParameter("lat");
		String lng = request.getParameter("lng");
		
		String member_type ="";//社员类型
		
		
		//App版本号(谢超2017年2月28日11:07:57)
		String version = request.getParameter("version") == null ? "":request.getParameter("version");
		//获取后台强制升级版本号，若是与App版本号不一致，则不允许登录
		String App_VER_org =  Var.get("App_VER_org_force");
		//获取后台强制升级版本号提示
		String App_VER_org_tip =  Var.get("App_VER_org_tip");
		
		if(!"0".equals(App_VER_org))
		{
			if(!version.equals(App_VER_org))
			{
				String result = "{\"CODE\" : \"1\",\"MSG\" : \""+App_VER_org_tip+"\"}";
				return result;
			}
		}
		
		
		//检查用户是否存在
		String checkUserSQL = "SELECT * FROM am_member WHERE loginaccount = '" 
				+ login_account + "' AND loginpassword = '" + login_password + "'  ";
//						+ "AND member_type='"+member_type+"'";  //2016.06.03 去掉类型判断

		String memberID = null;
		
		String tCode="2";
		String tMsg="";
		String tMemberData="";
		String loginInfoId="";
		MapList map = null;
		JSONObject memberInfo=null;
		
		try 
		{
			DBManager db =new DBManager();
			
			if(!Checker.isEmpty(login_type))
			{
				//第三方登录
				map = otherLogin(db,login_account,login_type,login_password);
				
			}else{
				
				map = db.query(checkUserSQL);
			}
			
			if (!Checker.isEmpty(map))
			{
				memberID=map.getRow(0).get("id");
				String status=map.getRow(0).get("status");//0：正常 ；1：已经注销
				//判断账号是否被冻结
				String account_freeze=map.getRow(0).get("account_freeze");
				if(account_freeze.equals("1"))
				{
					tCode="1";
					tMsg="帐号已冻结";
					tMemberData="{}";
				}else
				{
						if("1".equals(status)){
							tCode="1";
							tMsg="帐号已注销";
							tMemberData="{}";
						}else{
							tCode="0";
							tMsg="登录成功";
										
		//					String tSql="SELECT a.*,b.orgname FROM am_member a,aorg b "
		//							+ "where a.orgcode=b.orgid and a.id='" + memberID + "'";
							
							String tSql = "SELECT  a.*,b.orgname,ami.id_name,ami.member_type as imember_type,ami.id_code from am_member as  a "
									+ " left join aorg as b on a.orgcode=b.orgid "
									+ " left join am_member_identity as ami on ami.id = a.member_identity"
									+ "	where  a.id='" + memberID + "'";
							
//							tMemberData=LoadMenus.loadTableDataOfSql(tSql);
							
							
							JSONArray memberInfos=db.mapListToJSon(db.query(tSql));
							
							if(memberInfos!=null&&memberInfos.length()>0){
								memberInfo=memberInfos.getJSONObject(0);
							}
							
							
							//登录成功后绑定用户ID到SESSION中
							request.getSession().setAttribute("AMBDP_USERID", memberID);
							
							//记录登录情况
							loginInfoId=recordMemberLoginInfo(memberID,devicetype,street,lat,lng);
						}
				}
			}
			else
			{
				tCode="1";
				tMsg="登录失败，用户名或密码错误！";
				tMemberData="{}";
			}		
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			logger.error(e.getMessage());

			tCode="2";
			tMsg="登录失败，请稍后再试！";
		}

//		return "{\"CODE\" : \"" + tCode + "\",\"MSG\" : \"" + tMsg + "\",\"MEMBER_DATA\" : " + tMemberData + ",\"LOGININFOID\":\""+loginInfoId+"\"}";
	
		JSONObject result=new JSONObject();
		
		try {
			result.put("CODE", tCode);
			result.put("MSG", tMsg);
			result.put("MEMBER_DATA",memberInfo);
			result.put("LOGININFOID", loginInfoId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return result.toString();
	}

	
	
	//第三方登录
	private MapList otherLogin(DBManager db,String login_account,String type,String login_password) throws Exception
	{
		JSONObject json = new JSONObject();
		
		String sql = "SELECT * FROM am_member where 1=1 ";
		
		String insert = "";
		
		MapList rslist = null;
		
		if(!Checker.isEmpty(login_account))
		{
			//qq登录
			if("1".equals(type))
			{
				sql += " AND qq_openid = '"+login_account+"'";
		
				rslist = db.query(sql);
				
				if(Checker.isEmpty(rslist))
				{
					insert = "INSERT INTO am_member (id,qq_openid,create_tim,loginpassworde,pwd_security_grade) values('"+UUID.randomUUID()+"','"+login_account+"',now(),'"+login_password+"','弱')";
					
					db.execute(insert);
					
					rslist = db.query(sql);
					
				}
			}
			if("2".equals(type))
			{
				//微信登录
				sql += " AND wx_openid = '"+login_account+"'";
				
				rslist = db.query(sql);
				
				if(Checker.isEmpty(rslist))
				{
					insert = "INSERT INTO am_member (id,wx_openid,create_time,loginpassword,pwd_security_grade) values('"+UUID.randomUUID()+"','"+login_account+"',now(),'"+login_password+"','弱')";
					
					db.execute(insert);
					
					rslist = db.query(sql);
					
				}
			}
			if("3".equals(type))
			{
				sql += " AND sl_openid = '"+login_account+"'";
				
				rslist = db.query(sql);
				//微博登录
				if(Checker.isEmpty(rslist))
				{
					insert = "INSERT INTO am_member (id,sl_openid,create_time,loginpassword,pwd_security_grade) values('"+UUID.randomUUID()+"','"+login_account+"',now(),'"+login_password+"','弱')";
					
					db.execute(insert);
					
					rslist = db.query(sql);
					
				}
			}
		}
		
		
		return rslist;
	}
	
	/**
	 * 向会员登录情况表中添加数据
	 * @param db
	 * @param memberID
	 * @throws JDBCException 
	 */
	private String recordMemberLoginInfo(String memberID, String devicetype, String street, String lat, String lng) throws JDBCException {
		
		String id=UUID.randomUUID().toString();
		
//		Table table=new Table("am_bdp", "AM_MEMBERLOGININFO");
//		
//		TableRow row=table.addInsertRow();
//		
//		
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
//		
//		row.setValue("am_memberid", memberID);
//		row.setValue("logindate", df.format(new Date()));
//		row.setValue("terminaltypename", devicetype);
//		row.setValue("loginplace", street);
//		row.setValue("latitude", lat);
//		row.setValue("longitude", lng);
//		
//		logger.debug("  devicetype=" + devicetype);
//		
//		db.save(table);
		
//		return table.getRows().get(0).getValue("id");
		
		
		String inserSQL="INSERT INTO "
				+ " am_memberlogininfo("
				+ " id,am_memberid,logindate,"
				+ " terminaltypename,longitude,latitude,"
				+ " loginplace) VALUES ("
				+ " ?,?,now(),"
				+ " ?,?,?,"
				+ "?) ";
		
		DBManager db=new DBManager();
		
		db.execute(inserSQL,
				new String[]{
				id,memberID
				,devicetype,lng,lat
				,street
		},
				new int[]{
				Type.VARCHAR,Type.VARCHAR
				,Type.VARCHAR,Type.VARCHAR,Type.VARCHAR
				,Type.VARCHAR
		});
		
		return id;
	}
	
}
