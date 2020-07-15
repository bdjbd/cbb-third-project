package com.am.frame.webapi.member.Action;

import java.util.UUID;

import com.fastunit.context.ActionContext;
import com.fastunit.support.action.DefaultAction;

import com.am.frame.badge.AMBadgeManager;
import com.am.frame.badge.BadgeImpl;
import com.am.frame.task.instance.ConsumerInterestTask;
import com.am.frame.task.instance.MemberAuthorityBadgeTask;
import com.am.frame.task.instance.MemberInfoPerfectionTask;
import com.am.frame.task.instance.PromotionTask;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.TaskEngine;
import com.am.frame.webapi.member.service.SystemAccountServer;
import com.am.sdk.md5.MD5Util;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.util.Checker;

/**
 * @author xiechao
 * @create 2016年10月26日11:50:29
 * @version 
 * 
 * 农厂注册为生产者，区县联合社和扶贫办通用
 */

public class FarmAddProducersAction extends DefaultAction{

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {

		
		DB db=null;
		db=DBFactory.newDB();
		
		//登录账号
		String login_account = ac.getRequestParameter("farm_am_member.form.loginaccount");
		//密码
		String login_password = MD5Util.getSingleton().textToMD5L32(ac.getRequestParameter("farm_am_member.form.loginpassword"));
		//用户名
		String membername = ac.getRequestParameter("farm_am_member.form.membername");
		//推广码
		String invitation_code = "";
		//组织机构
		String org_code = ac.getRequestParameter("farm_am_member.form.orgcode");
		//土地面积
		String land = ac.getRequestParameter("farm_am_member.form.land");
		//省
		String province = ac.getRequestParameter("farm_am_member.form.province");
		//市
		String city = ac.getRequestParameter("farm_am_member.form.city");
		//区
		String zzone = ac.getRequestParameter("farm_am_member.form.zone");
		//会员身份
		String member_identity = ac.getRequestParameter("farm_am_member.form.member_identity");
		//社员类型
		String member_type = ac.getRequestParameter("farm_am_member.form.member_type");
		//添加会员的机构
		String org_id = ac.getVisitor().getUser().getOrgId();
		
		
		
		//判断用户是否为贫困户：0否1是
		String is_poor_type = ac.getRequestParameter("farm_am_member.form.poor");
		int is_poor = 0;
		if("是".equals(is_poor_type)){
			is_poor = 1;
		}
		if("否".equals(is_poor_type)){
			is_poor = 0;
		}
		
		
		//判断用户是否为技术专家：1是0否
		int is_auth = 0;
		if(member_identity.equals("93b46702-d0c2-4cc2-bee2-202ecc3469ff")){
			is_auth = 1;
		}
		
		//获取机构id，查询区域类型
		String orgid = ac.getVisitor().getUser().getOrgId();
		String SQL = "SELECT * FROM service_mall_info WHERE orgid='"+orgid+"' ";
		MapList area_type = db.query(SQL);
		

		//验证手机号
		if(login_account.length()!=11){
			ac.getActionResult().addErrorMessage("手机号不合法，添加失败！");
			ac.getActionResult().setSuccessful(false);
			
		}else{
			
			//检查用户是否存在
			String checkUserSQL = "SELECT * FROM am_member WHERE loginaccount = '"+login_account+"'";
			MapList maplist = db.query(checkUserSQL);
			
			
			
			if(maplist.size()>0)
			{
				//判断用户存在，且操作用户为扶贫办，01中央扶贫办，02省级扶贫办，03市级扶贫办，04，区县扶贫办，最后判断该用户是否属于该扶贫办下设成员
				if(is_poor>0){
					if("04".equals(area_type.getRow(0).get("area_type"))){
						if(province.equals(maplist.getRow(0).get("province")) && city.equals(maplist.getRow(0).get("city"))&& zzone.equals(maplist.getRow(0).get("zzone"))){
							String updateIs_poorSql = " UPDATE am_member SET is_poor = '1' WHERE loginaccount = '"+login_account+"'";
							db.execute(updateIs_poorSql);
							ac.getActionResult().addSuccessMessage("用户存在，已将该用户添加为贫困户！");
						}else{
							ac.getActionResult().addErrorMessage("用户存在，但不属于该机构的下设成员，因此无法添加为贫困户！");
							ac.getActionResult().setSuccessful(false);
						}
					}
					if("03".equals(area_type.getRow(0).get("area_type"))){
						if(province.equals(maplist.getRow(0).get("province"))&& city.equals(maplist.getRow(0).get("city"))){
							String updateIs_poorSql = " UPDATE am_member SET is_poor = '1' WHERE loginaccount = '"+login_account+"'";
							db.execute(updateIs_poorSql);
							ac.getActionResult().addSuccessMessage("用户存在，已将该用户添加为贫困户！");
						}else{
							ac.getActionResult().addErrorMessage("用户存在，但不属于该机构的下设成员，因此无法添加为贫困户！");
							ac.getActionResult().setSuccessful(false);
						}
					}
					if("02".equals(area_type.getRow(0).get("area_type"))){
						if(province.equals(maplist.getRow(0).get("province"))){
							String updateIs_poorSql = " UPDATE am_member SET is_poor = '1' WHERE loginaccount = '"+login_account+"'";
							db.execute(updateIs_poorSql);
							ac.getActionResult().addSuccessMessage("用户存在，已将该用户添加为贫困户！");
						}else{
							ac.getActionResult().addErrorMessage("用户存在，但不属于该机构的下设成员，因此无法添加为贫困户！");
							ac.getActionResult().setSuccessful(false);
						}
					}
					if("01".equals(area_type.getRow(0).get("area_type"))){
						if("+province+".equals(maplist.getRow(0).get("province"))&&"+city+".equals(maplist.getRow(0).get("city"))&&"+zzone+".equals(maplist.getRow(0).get("zzone"))){
							String updateIs_poorSql = " UPDATE am_member SET is_poor = '1' WHERE loginaccount = '"+login_account+"'";
							db.execute(updateIs_poorSql);
							ac.getActionResult().addSuccessMessage("用户存在，已将该用户添加为贫困户！");
						}
					}
				}else{
					ac.getActionResult().addErrorMessage("用户已存在，添加失败！");
					ac.getActionResult().setSuccessful(false);
				}
		
			}else{
				
				String memberId=UUID.randomUUID().toString();
				String regUserSQL = "INSERT INTO am_member "
						+ "(id, loginaccount, loginpassword, phone,"
						+ " orgcode,province,city,zzone,member_type,"
						+ " member_identity,land_area,is_auth,is_poor,"
						+ " membername,wxheadimg,org_id "
						+ "  )"
						+ "  VALUES ('"+memberId+"', '"+login_account+"', '"+login_password+"', '"+login_account+"',"
						+ " '"+org_code+"','"+province+"','"+city+"','"+zzone+"','"+member_type+"',"
						+ " '"+member_identity+"','"+land+"',"+is_auth+",'"+is_poor+"',"
						+ " '"+membername+"','"+"/img/default_head.png"+"','"+org_id+"' "
						+ " )";
				db.execute(regUserSQL);
				
				
				//添加技术专家徽章
				String Badge_JSZJ = BadgeImpl.Badge_JSZJ;
				
				if(member_identity.equals("93b46702-d0c2-4cc2-bee2-202ecc3469ff")){
					AMBadgeManager badgeManager = new AMBadgeManager();
					badgeManager.addBadgeByEntBadgeCode(db,Badge_JSZJ,memberId);
				}
					
				//初始化用户任务
				initUserTask(db,memberId,org_code,invitation_code,member_type);
					
				//初始化用户帐号
				initUserSystemAccount(db,memberId,org_code,member_type);
				
			}
		} 
		db.close();
		return ac;
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
			
			
			//理性农业消费分利任务依赖 社员徽章任务业务数据
			if(!Checker.isEmpty(invitationCode)){
				params=new RunTaskParams();
				params.pushParam(PromotionTask.INVITATION_CODE, invitationCode);
				params.setTaskCode(ConsumerInterestTask.TASK_ECODE); //消费分利任务
				params.setMemberId(memberId);
				taskEngine.executTask(params);
			}
			
			
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
	public void updateVolunteers(String volunteers_id,DB db) throws Exception
	{
		String sql = "select * from volunteers_record where id = '"+volunteers_id+"'";
		MapList list  = db.query(sql);
		if(!Checker.isEmpty(list))
		{
			String usql ="update volunteers_record set surplus_num = surplus_num+1 where id = '"+volunteers_id+"'";
			db.execute(usql);
		}
		
	}
		

	
}
