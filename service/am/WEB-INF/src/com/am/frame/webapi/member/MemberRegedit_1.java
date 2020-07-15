package com.am.frame.webapi.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.member.MemberManager;
import com.am.frame.task.instance.MemberAuthorityBadgeTask;
import com.am.frame.task.instance.MemberInfoPerfectionTask;
import com.am.frame.task.instance.PromotionTask;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.TaskEngine;
import com.am.frame.webapi.member.service.SystemAccountServer;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * 会员注册
 * */
public class MemberRegedit_1 implements IWebApiService 
{
	
	final Logger logger = LoggerFactory.getLogger(MemberRegedit_1.class);
	
	@Override
	public String execute(HttpServletRequest request,HttpServletResponse response)
	{
		//判断系统是否更新，暂停注册
		String to_update =  Var.get("to_update");
		if("1".equals(to_update))
		{
			String result = "{\"CODE\" : \"2\",\"MSG\" : \"系统升级中，暂停注册\"}";
			return result;
		}

		logger.info("会员注册");
		
		String isRegister=Var.get("is_register");
		
		//注册手机号即为账号
		String phone="";
		//用户名
		String login_account = request.getParameter("login_account");
		//默认手机号为用户名
		phone=login_account;
		//加密后的密码
		String login_password = request.getParameter("login_password");
		//推广码(推广会员的手机号码)
		String invitation_code = request.getParameter("invitation_code");
		//组织机构代码
		String org_code = request.getParameter("org_code");
		//会员身份
		//String member_identity = request.getParameter("member_identity");
	
		//注册社员类型   0消费社员  1  生产社员  2 单位
		//String account_type = request.getParameter("account_type");
		
		String member_type ="";
	
		String result = null;
		
		/**
		 * 注册需求修改
		 * @author   作者：谢超
		 * @date 修改时间：2016年11月17日17:12:11
		 * 注册时选择社员身份是技术专家是，默认技术专家审核状态是：已认证
		 * 
		 * */
		//技术专家审核状态1已认证0未认证
		int is_auth = 0;
				
	
		
		
		//理性农业邀请码验证功能
//		String msg=validateRegisterCode(member_type,invitation_code);
		/**
		 * 所填邀请码用户的志愿者等级记录表id
		 */
//		String volunteers_id = "";
//		
//		try {
//			
//			JSONObject validateRegisterObj = new JSONObject(msg);
//			
//			if("0".equals(validateRegisterObj.getString("CODE")) || Checker.isEmpty(validateRegisterObj.getString("CODE")))
//			{
//				if(validateRegisterObj.has("volunteers_id"))
//				{
//					volunteers_id = validateRegisterObj.getString("volunteers_id");
//				}
//			
//			}else
//			{
//				return validateRegisterObj.toString();
//			}
//			
//		} catch (JSONException e1) {
//			e1.printStackTrace();
//		}
		
		
		//注册用户
		String regUserSQL = "INSERT INTO am_member "
				+ "(id, loginaccount, loginpassword, phone,"
				+ " orgcode,membername,wxheadimg,is_auth,register_inv_code_id,registrationdate "
				+ "  )"
				+ "  VALUES (?, ?, ?, ?,"
				+ " ?,?,?,?,?, 'now()' "
				+ " )";
		
		//检查用户是否存在
		String checkUserSQL = "SELECT * FROM am_member WHERE loginaccount = ?";
		//logger.info(regUserSQL);
		DB db=null;
		Connection conn=null;
		try 
		{
			db=DBFactory.newDB();
			//在邀请码表中 查询当前用户注册所填邀请码的id
			String querySQL=" select id from am_MemberInvitationCode where invitationcode='"+invitation_code+"'";
			
			MapList invitationIdMap = db.query(querySQL);
			//邀请码的id
			String register_inv_code_id = "";
			if(!Checker.isEmpty(invitationIdMap)){
				//邀请码的id
				register_inv_code_id = invitationIdMap.getRow(0).get("id");
				System.err.println("当前注册会员的邀请码ID》》》"+register_inv_code_id);
			}else if(Checker.isEmpty(invitationIdMap)){
				//用户所填邀请码不存在
				result = "{\"CODE\" : \"2\",\"MSG\" : \"此邀请码不存在，注册失败\"}";
				return result;
			}
			conn=db.getConnection();
			PreparedStatement pareSta = conn.prepareStatement(checkUserSQL);
			pareSta.setString(1, login_account);
			ResultSet rset = pareSta.executeQuery();
			if (rset.next()) 
			{
				result = "{\"CODE\" : \"1\",\"MSG\" : \"注册失败，用户已存在\"}";
				pareSta.close();
			}else{
				//设置会员表主键ID
				String memberId = UUID.randomUUID().toString();  
				System.err.println("当前注册会员的主键ID》》》"+memberId);
				pareSta.close();
				pareSta = conn.prepareStatement(regUserSQL,Statement.RETURN_GENERATED_KEYS);
				pareSta.setString(1, memberId);
				pareSta.setString(2, login_account);
				pareSta.setString(3, login_password);
				pareSta.setString(4, phone);//登录帐号是手机号
				pareSta.setString(5, org_code);
				pareSta.setString(6, login_account);
				pareSta.setString(7, "/img/default_head.png");
				pareSta.setInt(8, is_auth);
				pareSta.setString(9, register_inv_code_id);
				pareSta.execute();
				result = "{\"CODE\" : \"0\",\"MSG\" : \"注册成功\"}";
				
				
				//初始化用户任务
				initUserTask(db,memberId,org_code,invitation_code,member_type);
				
				//初始化用户帐号
				initUserSystemAccount(db,memberId,org_code,member_type);
				
				//更新志愿者邀请人数
//				if(!Checker.isEmpty(volunteers_id))
//				{
//					updateVolunteers(volunteers_id,memberId,db);
//				}
				
				//更新上下级关系为 1 
				String usql = "update am_member_distribution_map set invitation_status = '1'"
						+ " where sub_member_id = '"+memberId+"' and level = '1'";
				db.execute(usql);
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			logger.error(e.getMessage());
			result = "{\"CODE\" : \"2\",\"MSG\" : \"注册失败，添加用户失败\"}";
		} 
		finally 
		{
			try 
			{
				if(conn!=null){
					conn.close();
				}
				if(db!=null){
					db.close();
				}
				
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
		
		logger.info("用户注册返回结果："+result);
		
		return result;
	}
	
	
	/**
	 * 社员邀请码验证
	 * @param member_type
	 * @param invitation_code
	 * @return
	 */
	private String validateRegisterCode(String member_type,
			String invitation_code) {
		
		String result="";
		
		//理性农业业务需求，根据不同的角色给on规划初始化邀请码
		MemberManager mManager=new MemberManager();
		
		try {
			result=mManager.validateRegisterCode(member_type,invitation_code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		logger.info("社员邀请码验证结果："+result);
		
		return result;
	}

	/**
	 * 初始化用户帐号
	 * @param db  DB
	 * @param memberId 会员ID
	 * @param org_code  机构编号
	 */
	private void initUserSystemAccount(DB db, String memberId, String org_code,String memberType){
		SystemAccountServer saServer=new SystemAccountServer();
		
		try{
			saServer.initUserSystemAccount(db,memberId,org_code,memberType);
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	/**
	 * 初始化用户任务，如果用户有任务，推广等相关任务，则执行推广任务
	 * 理性农业业务特殊说明：
	 * 任务执行顺序为：
	 * 1，推广任务
	 * 2，会员权限徽章任务
	 * 3，消费分利任务
	 * @param db  DB
	 * @param memberId  会员ID
	 * @param org_code  机构ID
	 * @param invitationCode 邀请码
	 * @param member_type //1=消费者社员，2=单位消费者社员,3=生产者社员
	 */
	private void initUserTask(DB db, String memberId, String org_code,String invitationCode,String member_type) {
		
		try{
			
			TaskEngine taskEngine=TaskEngine.getInstance();
			taskEngine.addUserAllTask(memberId, org_code, db);
			
			//执行注册任务
			RunTaskParams params=new RunTaskParams();
			params.setMemberId(memberId);
			params.setTaskCode("MEBER_REGISTER");
			taskEngine.executTask(params);
			
			//推广任务 坚持是否有推广码，如果有，进行推广任务
			if(!Checker.isEmpty(invitationCode)){
				//推广任务
				params=new RunTaskParams();
				params.pushParam(PromotionTask.INVITATION_CODE, invitationCode);
				params.setTaskCode(PromotionTask.TASK_ECODE); //推广任务
				params.setMemberId(memberId);
				taskEngine.executTask(params);
				
			}
			
			//理性农业  会员权限徽章任务，具体权限参考需求 社员分类划分
			//理性农业 社员徽章任务 社员徽章任务业务数据依赖于推广任务。推广任务需要先执行。
			params=new RunTaskParams();
			params.setTaskCode(MemberAuthorityBadgeTask.TASK_ECODE); //会员权限徽章任务
			params.pushParam(MemberAuthorityBadgeTask.TRIGGER_POINT,MemberAuthorityBadgeTask.USER_REGISTER);
			params.setMemberId(memberId);
			taskEngine.executTask(params);
			
			//执行会员 接受邀请任务(信息完善任务)
			RunTaskParams taskParams=new RunTaskParams();
			taskParams.setMemberId(memberId);
			taskParams.setTaskCode(MemberInfoPerfectionTask.MEMBER_INFO_COMPLETE_TASK);
			//执行任务
			TaskEngine.getInstance().executTask(taskParams);

		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 更新志愿者等级记录人数
	 * @param volunteers_id 所填邀请码用户的志愿者等级记录表id
	 */
	public void updateVolunteers(String volunteers_id,String memberId,DB db) throws Exception
	{
		String sql = "select * from volunteers_record where id = '"+volunteers_id+"'";
		MapList list  = db.query(sql);
		if(!Checker.isEmpty(list))
		{
			String usql ="update volunteers_record set surplus_num = surplus_num+1 where id = '"+volunteers_id+"'";
			db.execute(usql);
		}
		
		//更新奖励任务次数 2016-12-05
		String updateSQL="UPDATE am_member SET is_reward=0 WHERE id=? ";
		db.execute(updateSQL, memberId, Type.VARCHAR);
	}
	
}
