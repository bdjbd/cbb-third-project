package com.p2p.service;

import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.task.UserTaskManage;
import com.am.frame.task.toPromoteTask.ToPromote;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.base.badge.BadgeManager;
import com.p2p.business.DispatcherOrderService;
import com.p2p.business.ManPowerDispatcherOrder;
import com.p2p.elect.ElecticketServer;
import com.p2p.map.GpsPointDistance;
import com.p2p.member.UpdateMemberInfo;
import com.p2p.service.bt.SendMessageManager;
import com.p2p.tools.sms.SMSIdentifyingCode;
import com.p2p.util.DBUtil;
import com.p2p.ver.AppVersionManager;
import com.wisdeem.wwd.WeChat.Utils;

/**
 * Author: Mike 2014年7月15日 
 * 说明：
 * 
 **/
public class DataService {

	public static final String tag="DataService";
	public static final String USERID="P2P_USERID";
	
	
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	public  String service(String action, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String content=request.getParameter("content");
		
		//客户端类型
		String clientType=request.getParameter("clienttype");
		if("mobile.user.client".equalsIgnoreCase(clientType))
		{
			//手机端JQM提交，转码
			if(content!=null)
			{
				content=new String(request.getParameter("content").getBytes("iso8859-1"),"UTF-8");
			}
		}
		
		String result = "";
		this.request=request;
		this.response=response;
		
		if ("Query".equalsIgnoreCase(action)) 
		{
			// 查询Action
			logger.info("执行查询 Query,输入："+content);
			
			result = execute(content);
			logger.info("执行查询 Query,输出："+result);
			
			
		} 
		else if ("insert".equalsIgnoreCase(action)
				|| "update".equalsIgnoreCase(action)
				|| "delete".equalsIgnoreCase(action)) 
		{
			//数据更新接口
			logger.info("数据更新接口  Query,输入："+content);
//			result = executeUpdate(content);//2016年10月13日  屏蔽执行update接口 
			logger.info("数据更新接口  Query,输出："+content);
		} 
		else if ("login".equalsIgnoreCase(action)) 
		{
			// 登录Action
			
			// 用户名
			String wshop_name = request.getParameter("wshop_name");
			// 密码
			String userPass = request.getParameter("wshaop_password");
			// 检查验证码
			String verifCode = request.getParameter("verifcode");
			// 机构ID
			String orgid = request.getParameter("orgid");
			
			result = validUser(verifCode, wshop_name, userPass, orgid, request);
		} 
		else if ("register".equalsIgnoreCase(action)) 
		{
			//用户注册接口
			
			// 用户名
			String wshop_name = request.getParameter("wshop_name");
			// 密码
			String userPass = request.getParameter("wshaop_password");
			// 机构ID
			String orgid = request.getParameter("orgid");
			//推广号
			String popuCode=request.getParameter("popuCode");
			
			result = registerUser(wshop_name, userPass, orgid,popuCode);
			
		} 
		else if("findPasswordByEmail".equalsIgnoreCase(action))
		{
			//找回密码接口
			
			// 用户名
			String email = request.getParameter("email");
			// 机构ID
			String orgid = request.getParameter("orgid");
			result = findPasswordByEmail(email, orgid);
			
		} 
		else if("getUserTask".equalsIgnoreCase(action))
		{
			//获取用户任务信息接口
			
			String taskId=request.getParameter("taskId");
			String memberCode=request.getParameter("memberCode");
			
			result=getUserTaskByTaskId(taskId,memberCode);
		} 
		else if("getUserBadge".equalsIgnoreCase(action))
		{
			//获取用户徽章接口，memberCode(用户编号)
			String badgeCode=request.getParameter("badgeCode");
			String memberCode=request.getParameter("memberCode");
			String param=request.getParameter("param");
			Utils.Log(tag, param);
			
			param=URLDecoder.decode(param, "UTF-8");
			
			Utils.Log(tag, param);
			
			if("mobile.user.client".equalsIgnoreCase(clientType))
			{
				//手机端JQM提交，转码
				if(param!=null){
					param=new String(param.getBytes("iso8859-1"),"UTF-8");
				}
			}
			Utils.Log(tag, param);
			result=getUserBadge(badgeCode,memberCode,param);
			
		} 
		else if("dispatcherOrder".equalsIgnoreCase(action))
		{
			//自动派单接口
			String orderCode=request.getParameter("orderCode");
			DispatcherOrderService.dispatcherOrder(orderCode);
			
		}
		//维修人员接单接口
		else if("accpetsOrder".equalsIgnoreCase(action))
		{
			
			String orderCode=request.getParameter("orderCode");
			String memberCode=request.getParameter("memberCode");
			String OrgID=request.getParameter("OrgID");
			
			//如果接口参数为空，则取默认值为 ekx
			if(OrgID==null)
				OrgID="ekx";
			
			Utils.Log(tag, "accpetsOrder : orderCode="+orderCode+";memberCode="+memberCode);
			
			result=accpetsOrder(orderCode,memberCode,OrgID);
			
			Utils.Log(tag, "accpetsOrder  result"+result);
		}
		//向客户终端推送消息接口
		else if("sendMessageToUser".equalsIgnoreCase(action))
		{
			System.out.println( "sendMessageToUser  Start ...");
			
			String tUserID=request.getParameter("UserID");
			Long tChannelID=Long.parseLong(request.getParameter("ChannelID"));
			String tContent=request.getParameter("Content");
			
			SendMessageManager smm=new SendMessageManager(tUserID,tChannelID);
			result=smm.send(tContent, 3);

		}
		// 订单完成接口
		else if("overOrder".equalsIgnoreCase(action))
		{
			String OrgID=request.getParameter("OrgID");
			//订单编号
			String OrderCode=request.getParameter("orderCode");
			//维修人员编号
			String MemberCode=request.getParameter("memberCode");
			
			result=DispatcherOrderService.OverOrder( OrgID, OrderCode, MemberCode);
		}
		//获得Gps2点之间距离
		else if("GpsPointDistance".equalsIgnoreCase(action))
		{
			//第一个坐标点
			double lat1=Double.parseDouble(request.getParameter("lat1"));
			double lng1=Double.parseDouble(request.getParameter("lng1"));
			
			//第一个坐标点
			double lat2=Double.parseDouble(request.getParameter("lat2"));
			double lng2=Double.parseDouble(request.getParameter("lng2"));
			
			//获得距离
			double distance=GpsPointDistance.getDistance(lat1, lng1, lat2, lng2);
			
			result="距离：" + Double.toString(distance);
			
			//"1408177638155";11230
			//测试时间间隔，分钟
			long num=DispatcherOrderService.isMemberTimeOut("11230","1408177638155");
			result+="<br>超时时间：" + num + ";";
		}
		//获得最新app版本下载地址
		else if("CheckVersion".equalsIgnoreCase(action))
		{
			//应用名称
			String AppName = request.getParameter("AppName").toString();
			
			/*
			 应用版本号，版本号规则如下：
			 a.b.c
			 a，主版本号，程序功能大范围升级，取值范围 1-9
			 b，二级版本号，功能升级版本号，取值范围0-999
			 c,三级版本号,Bug修正版本号，取值范围0-999
			 例子： 1.0.3
			 */
			String AppThisVer = request.getParameter("AppThisVer").toString();
			
			result=AppVersionManager.checkAppVerGetUrl(AppName, AppThisVer);
		}else if("manpowerDispatcherOrder".equalsIgnoreCase(action)){//地图人工派单接口 
			//订单号
			String orderCoder=request.getParameter("orderCoder");
			//会员编号
			String memberCode=request.getParameter("memberCode");
			
			ManPowerDispatcherOrder manDispatcherOrder=new ManPowerDispatcherOrder();
			
			result=manDispatcherOrder.dispatcher(orderCoder, memberCode)+"";
		}
		else if("SMSCode".equalsIgnoreCase(action))
		{
			//获得短信验证码
			String sPhone = request.getParameter("Phone");
			String sTemplateName = request.getParameter("TemplateName");

			String tContent = Var.get(sTemplateName);

			SMSIdentifyingCode sic = new SMSIdentifyingCode("");
			

			result = sic.getCode(tContent, sPhone);
			
			//result+=sic.srm.getMessageString();
			
			Utils.Log(tag, "SMSCode.do 调用返回信息：" + result + ";");
			
			//调用找回密码接口
			if("SMS_SEND_FindPassWord".equalsIgnoreCase(sTemplateName))
			{
				if(sic.srm.getValue("result").equalsIgnoreCase("0"))
				{
					UpdateMemberInfo updateMemberInfo=new UpdateMemberInfo();
					//更新sPhone的用户密码为result
					updateMemberInfo.updatePassword(sPhone, result);
				}
			}
			
			Utils.Log(tag, "SMSCode.do result ：" + result + ";");
		}
		else if("ExchangeTicket".equalsIgnoreCase(action))
		{//  积分兑换接口
			//ExchangeTicket.do?Member_Code,EterpElectTicketID
			String memberCode=request.getParameter("Member_Code");
			String electTicketId=request.getParameter("EterpElectTicketID");
			
			//Utils.executeResult(4004, "用户已存在");
			result=ElecticketServer.getInstance().exchangeTicket(memberCode,electTicketId);
		}
		else
		{
			IWebApiService ias=classNameToObject(action);
			
			if(ias!=null)
			{
				result=ias.execute(request, response);
			}
			else
			{
				result="接口调用失败:" + action + "不存在！";
				Utils.Log(tag, "接口调用失败:" + action + "不存在！;");
			}

		}

		//输出接口调试信息
		Utils.Log(tag, "接口版本 :v1.0.11;");
		Utils.Log(tag, "1.接口调用 :" + action + ";");
		Utils.Log(tag, "2.接口参数:" + getRequestParamList(request));
		Utils.Log(tag, "3.接口返回值:" + result);
		
		//this.response.getWriter().write(result);
		return result;
	}
	
	//判断值是否为null，如果为null则返回空字符串
	public static String getRequestValue(String value)
	{
		String rValue=value;
		if(value==null)
			rValue="";
		return rValue;
	}
	
	//遍历Request的所有参数
	private String getRequestParamList(HttpServletRequest request)
	{
		String rValue="";
		String tName = "";
		String tValue="";
		
		Enumeration rnames=request.getParameterNames();
		
		for (Enumeration e = rnames; e.hasMoreElements();) 
		{
			tName = e.nextElement().toString();
			tValue = request.getParameter(tName);
			
			rValue+="[" + tName + "]=[" + tValue + "];\n";
		}
		
		
		return rValue;
	}

	/**
	 * 接单接口  完成接单三步曲；
	 * <ul><li>修改派单状态为接单.</li>
	 * 	<li>修改订单状态为配送中.</li>
	 * <li>修改维修人员状态为工作中。</li></ul>
 	 * @param orderCode  接单编号
	 * @param memberCode 会员编号
	 * @return  TRUE 派单成功，FALISE 派单失败。
	 */
	private String accpetsOrder(String orderCode, String memberCode,String orgID) {
		
		String rValue="false"; //返回执行成功的信息
		String msg=""; //调试信息
		String rMessage=""; //返回至用户的提示信息
		boolean isAccpetsOrder=false;//是否可以接单
		
		DB db=null;
		
		try 
		{
			db = DBFactory.newDB();
			
			//获得系统变量 AcceptOrderCashThreshold ,如未设置则返回300
			Double CashThreshold=Double.parseDouble(com.fastunit.Var.get("AcceptOrderCashThreshold","300"));
			
			//判断是否已经接单
			String tSql="SELECT count(order_code) FROM p2p_DispatchRecod  WHERE order_code='"
					+orderCode+"' AND ORStatus=0";
			Utils.Log(tag,"判断是否已经接单:" + tSql + ";<br>");
			
			String OrderState=getDBTableTopRowField(tSql,db);
			msg+="判断是否已经接单(OrderState=" + OrderState + ")：" + tSql + ";<br>";
			
			//判断现金是否满300元
			tSql="select cash from ws_member where member_code=" + memberCode + "";
			Utils.Log(tag,"判断现金是否满300元:" + tSql + ";<br>");
			
			String CashOK=getDBTableTopRowField(tSql,db);
			msg+="判断现金是否满300元(CashOK=" + CashOK + "元)：" + tSql + ";<br>";
			
			
			//判断是否有人接单
			if(OrderState.length()>0)
			{
				isAccpetsOrder=true;
				rMessage="\n" + orderCode + "号订单无人受理";
				
				//判断会员现金是否足够
				if(Double.parseDouble(CashOK)>CashThreshold)
				{
					isAccpetsOrder=true;
					rMessage+="并且您的现金余额为" + CashOK + "元，您接单成功。";
				}
				else
				{
					rMessage="\n您的现金余额为" + CashOK + "元，小于最小接单金额" + CashThreshold + "元您接单失败。";
					isAccpetsOrder=false;
				}
			}
			else
			{
				rMessage="\n" + orderCode + "号订单已经被别人接单，您接单失败。";
				isAccpetsOrder=false;
			}
			
			
				
			//处理接单
			if (isAccpetsOrder)
			{
				// 修改派单状态为已接单 orstatus=1
				tSql = "UPDATE  p2p_DispatchRecod  SET member_code='"
						+ memberCode
						+ "',orstatus=1,recv_time=now()  WHERE order_code='"
						+ orderCode + "' ";
				int num=db.execute(tSql);
				
				msg+="修改派单状态为已接单 orstatus=1(已更新=" + num + "行)：" + tSql + ";<br>";

				// 修改订单状态为配送中，data_status=5
				tSql = "UPDATE ws_order  SET data_status=5" 
						+ ",InfoCosts=(total * (select projectfeeratio from ws_org_baseinfo where orgid='" + orgID + "'))"
						+"  WHERE order_code='"
						+ orderCode + "'";
				num=db.execute(tSql);
				
				//20140831 商品信息费需求变更
				updateInfoCosts(orderCode,memberCode,orgID,db);
				
				
				msg+="修改订单状态为配送中，data_status=5(已更新=" + num + "行)：" + tSql + ";<br>";

				// 修改人员状态为工作中 recvstatus 1,工作中状态，当派单成功后自动设置；
				tSql = "UPDATE ws_member  SET recvstatus=1  WHERE member_code="
						+ memberCode + "";
				num=db.execute(tSql);
				
				msg+="修改人员状态为工作中 recvstatus 1(已更新=" + num + "行)：" + tSql + ";<br>";
				
				rValue= "true";
			}

			
		} 
		catch (Exception e) 
		{
			msg+="Error:" + e.getMessage() + "<br>";
			e.printStackTrace();
			rValue= "false";
		}finally{
			try{
				if(db!=null){
					db.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		Utils.Log(tag, msg);
		
		return rValue.toUpperCase() + "," + rMessage;
	}

	/**
	 * 服务项目手续费扣款
	 * @param orderCode  订单编号
	 * @param memberCode 接单会员
	 * @param orgID  订单机构编号
	 * <br/>检查服务是否有手续费：<br/>如果有，在0-1之间，表示扣款比例，如果大于1,表示扣款金额.</br>
	 * 如果没有手续费，则按照企业基本信息手续费处理。(企业信息基本费用已在上一步处理，在此方法中不实现。)
	 * @throws JDBCException 
	 */
	private void updateInfoCosts(String orderCode, String memberCode,
			String orgID,DB db) throws JDBCException {
		//根据订单号查询服务商品手续费
		String checkSQL="SELECT ord.total,ord.total,wcn.ProjectFeeRatio,ord.order_code "
				+ " FROM ws_order AS ord "
				+ " LEFT JOIN ws_commodity_name AS wcn "
				+ " ON ord.comdity_id=wcn.comdity_id "
				+ " WHERE ord.order_code='"+orderCode+"'";
		Utils.Log(tag,"根据订单号查询服务商品手续费SQL:" + checkSQL);
		MapList map=db.query(checkSQL);
		Double infoCosts=0d;
		//判断商品手续费是否存在
		if(!Checker.isEmpty(map)&&!Checker.isEmpty(map.getRow(0).get("projectfeeratio"))){
			Utils.Log(tag,"商品手续费:" + map.getRow(0).get("projectfeeratio"));
			//如果存在，判断是手续费欢是比例
			Double feeRatio=Double.parseDouble(map.getRow(0).get("projectfeeratio"));
			if(feeRatio<=1){
				//比例，按照商品价格计算
				infoCosts=map.getRow(0).getDouble("total", 1)*feeRatio;
			}else if(feeRatio>1){
				//手续费，直接更新
				infoCosts=feeRatio;
			}
			String updateSQL= "UPDATE ws_order  SET data_status=5" 
					+ ",InfoCosts="+infoCosts
					+"  WHERE order_code='"
					+ orderCode + "'";
			Utils.Log(tag,"更新商品手续费SQL:" + updateSQL);
			db.execute(updateSQL);
		}
		//如果不存在则跳出，不错处理，在上面已经处理
	}

	/**
	 * 返回用户徽章值
	 * @param badgeCode 徽章编码
	 * @param memberCode 用户ID
	 * @param params  徽章参数
	 * @return
	 */
	private String getUserBadge(String badgeCode, String memberCode,String params) {
		BadgeManager bm=new BadgeManager();
		bm.init(memberCode, badgeCode);
		return bm.getBadgeValueOfName(params);
	}

	/***
	 * 根据任务ID获取任务界面
	 * @param taskId  任务ID
	 * @param memberCode 会编号
	 * @return
	 */
	private String getUserTaskByTaskId(String taskId, String memberCode) {
		
		String result="{\"DATA\":\"\"}";
		
		if(taskId!=null||memberCode!=null){
			result=UserTaskManage.getInstance().getHtml(memberCode, taskId);
			result="{\"DATA\":\""+result+"\"}";
		}
		return result;
	}

	/***
	 * 执行查询SQL
	 * 
	 * @param stat
	 * @param sql
	 * @return 返回格式{"DATA":[]}
	 * @throws SQLException
	 */
	public static String execute(String sql) 
	{
		String result = "{\"DATA\":[]}";
		DB db=null;
		try 
		{
			db=DBFactory.newDB();
			
			ResultSet rst=db.getResultSet(sql);
			result = DBUtil.resultSetToJSON(rst).toString();
			
		}catch (Exception e){
			e.printStackTrace();
			result = "{\"errcode\":40007,\"errmsg\":\""+e.getMessage()+"\"}";
		}finally{
			try{
				if(db!=null){
					db.close();
				}
			}catch(Exception e){
				
			}
		}
		return result;
	}

	/**
	 * 执行UPdate数据，返回影响行数
	 * 
	 * @param sql
	 * @return 需要新增主键返回格式：<br>
	 *         {"COUNT":"影响行数","ID":"新增生成主见"} <br>
	 *         无主键，修改，更新返回影响行：<br>
	 *         {"COUNT":"影响行数"} 行数为-1，表示执行失败
	 */
	public  String executeUpdate(String sql) {
		String result = "{}";
		DB db=null;
		try{
			db=DBFactory.newDB();
			
			int count = 0;
			Utils.Log(tag, "调用接口UPdate：" + sql);
			if (sql.contains("?")) {
				String pk = UUID.randomUUID().toString();
				sql = sql.replaceAll("\\?", pk);
				count=db.execute(sql);
				result = "{\"COUNT\":\"" + count + "\",\"ID\":\"" + pk + "\"}";
			} else {
				count=db.execute(sql);
				result = "{\"COUNT\":\"" + count + "\"}";
			}
			
			count=db.execute(sql);
			result = "{\"COUNT\":\"" + count + "\"}";
			
		}catch(Exception e){
			e.printStackTrace();
			result = "{'errcode':40007,'errmsg':'"+e.getMessage()+"'}";
		}finally{		
			try{
				if(db!=null){
					db.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return result;	
	}

	/**
	 * 验证用户 用户登录
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return 验证结果
	 * @throws SQLException
	 */
	public  String validUser(String verifCode, String wshop_name,
			String userPass, String orgid, HttpServletRequest request) {
		int rows = -1;
		String memberCode=null;
		
		DB db=null;
		Connection conn=null;
		
		try {
			db=DBFactory.newDB();
			
			// 检查用户是否存在
			String checkUserSQL = "SELECT * FROM ws_member WHERE wshop_name=? AND wshaop_password=? AND orgid=?";
			conn=db.getConnection();
			
			PreparedStatement pst = conn.prepareStatement(
					checkUserSQL);
			pst.setString(1, wshop_name);
			pst.setString(2, userPass);
			pst.setString(3, orgid);
			ResultSet rest = pst.executeQuery();
			rest.next();
			rows = rest.getRow();
			memberCode=rest.getString("member_code");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn!=null){
					conn.close();
				}
				if(db!=null){
					db.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (rows == 1) {
			//登录成功后绑定用户ID到ＳＥＳＳＩＯＮ中
			this.request.getSession().setAttribute(USERID, memberCode);
			return Utils.executeResult(0, "登录成功");
		}
		return Utils.executeResult(40001, "用户名或密码错误");
	}

	/**
	 * 注册用户
	 * 
	 * @param email
	 *            邮箱
	 * @param password
	 *            密码
	 * @return
	 */
	public  String registerUser(String wshop_name, String password,
			String orgid,String pupCode) {
		//WSHOP_NAME   WSHAOP_PASSWORD
		//检查用户是否存在
		String checkUserSQL = "SELECT * FROM ws_member WHERE wshop_name=? AND orgid=? ";
		String result = null;
		DB db=null;
		Connection conn=null;
		
		try 
		{
			db=DBFactory.newDB();
			conn=db.getConnection();
			
			PreparedStatement pareSta = conn.prepareStatement(checkUserSQL);
			
			pareSta.setString(1, wshop_name);
			pareSta.setString(2, orgid);
			ResultSet rset = pareSta.executeQuery();
			if (rset.next()) 
			{
				result = Utils.executeResult(4004, "用户已存在");
				pareSta.close();
			} 
			else 
			{
				String registerSQL = "INSERT INTO ws_member (orgid,wshop_name,wshaop_password,phone) "
						+ "VALUES(?,?,?,?)";
				pareSta.close();
				
				pareSta =conn.prepareStatement(registerSQL,Statement.RETURN_GENERATED_KEYS);
				pareSta.setString(1, orgid);
				pareSta.setString(2, wshop_name);
				pareSta.setString(3, password);
				pareSta.setString(4, wshop_name);
				
				boolean isreg = pareSta.execute();
				
				ResultSet rs=pareSta.getGeneratedKeys();
				String memberCode="";
				
				if(rs.next())
				{
					memberCode=rs.getLong("member_code")+"";
				}
				if (memberCode!=null)
				{
					result = Utils.executeResult(0, "注册成功");
					//需求   20140825  将用户名保存到用户手机号码中
					saveUserPhone(wshop_name,memberCode,db);
					
					
					//注册成后绑定用户ID到ＳＥＳＳＩＯＮ中
					this.request.getSession().setAttribute(USERID, memberCode);
					UserTaskManage taskManage=UserTaskManage.getInstance();
					//初始化注册用户任务
					taskManage.initUserTask(orgid, memberCode);;
					
//					//推广任务跟新
//					PopulMemberTask pupTask=new PopulMemberTask();
////					//推广码为用户编号
//					pupTask.updatePopMemberTask(pupCode);
					
					//推广任务
					ToPromote promote=new ToPromote();
					promote.updateTaskProgress(pupCode);
					
				}
			}

		} 
		catch (Exception e)
		{
			e.printStackTrace();
			
			Utils.Log(tag, "registerUser().e" + e.getMessage());
		} 
		finally 
		{
			try {
				if(conn!=null){
					conn.close();
				}
				if(db!=null){
					db.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}
	
	/**
	 * 保存用户帐号到用户电话  帐号即电话
	 * @param wshop_name 帐号即电话
	 * @param memberCode  用户编号
	 */
	private void saveUserPhone(String wshop_name,String memberCode,DB db) {
		String sql="UPDATE ws_member SET phone='"+wshop_name+"'  WHERE member_code="+memberCode;
		try {
			db.execute(sql);
		} catch (JDBCException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据用户邮箱和机构向邮箱发送密码
	 * @param email 用户邮箱及用户名
	 * @param orgid 机构编号
	 * @return
	 */
	public  String findPasswordByEmail(String email,String orgid){
		String findUserSQL="SELECT * FROM ws_member WHERE email=? AND orgid=?";
		String result="";
		
		DB db=null;
		Connection conn=null;
		
		try {
			db=DBFactory.newDB();
			conn=db.getConnection();
			
			PreparedStatement pste=conn.prepareStatement(findUserSQL);
			pste.setString(1, email);
			pste.setString(2, orgid);
			ResultSet rset=pste.executeQuery();
//			if(rset.getRow()>0){
				//获取随机字符串，长度为6个
				String newPassword=Utils.getRandomStr(6);
				//实用MD5加密随机字符
				newPassword=DigestUtils.md5Hex(newPassword);
				System.out.println("findEmail:" + email);
				Utils.sendEmailTest(email,
						"找回密码",
						"这是您在电商服务平台的密码："+newPassword+"，请您登录后重新修改密码。");
				Utils.sendEmailTest("554146205@qq.com", "找密码", "密码编号");
				String updatePassSQL="UPDATE ws_member SET wshaop_password=? WHERE orgid=? AND email=?";
				pste.clearParameters();
				pste=conn.prepareStatement(updatePassSQL);
				pste.setString(1, newPassword);
				pste.setString(2, orgid);
				pste.setString(3, email);
				result=Utils.executeResult(0, "修改密码成功");

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(conn!=null){
					conn.close();
				}
				if(db!=null){
					db.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	//获得结果集中首行的第一个字段值
	public static String getDBTableTopRowField(String sql,DB db) throws JDBCException
	{
		String rValue="";
		MapList map=db.query(sql);
		
		if(!Checker.isEmpty(map))
			rValue=map.getRow(0).get(0);
		
		db.close();
		
		return rValue;
	}
	
	
	//依据类名反射出对象
		private IWebApiService classNameToObject(String className)
		{
			IWebApiService result=null;

			try 
			{
				result=(IWebApiService)Class.forName(className).newInstance();
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			return result;
		}

}
