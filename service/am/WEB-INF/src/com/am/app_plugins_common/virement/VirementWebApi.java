package com.am.app_plugins_common.virement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.task.instance.MemberAuthorityBadgeTask;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.TaskEngine;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * 转账 webApi
 * @author mac
 *
 */
public class VirementWebApi implements IWebApiService{
	
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		String logMsg="";
		
		//转出会员id
		String outMemberId = request.getParameter("outMemberid");
		logMsg+="outMemberId="+outMemberId+"\n";
		
		//转出账户
		String outAccountId = request.getParameter("outAccountId");
		logMsg+="outAccountId="+outAccountId+"\n";
		
		//转入账户
		String inAccountId = request.getParameter("inAccountId");
		logMsg+="inAccountId="+inAccountId+"\n";
		
		//转账金额 单位元
		String virementNumber  = request.getParameter("virementNumber");
		logMsg+="virementNumber="+virementNumber+"\n";
		
		//转入会员登录账号
		String inMemberId = request.getParameter("inMemberid");
		logMsg+="inMemberId="+inMemberId+"\n";
		
		//转入描述
		String iremakers = request.getParameter("in_remarks");
		logMsg+="iremakers="+iremakers+"\n";
		
		//转出描述
		String oremakers = request.getParameter("out_remarks");
		logMsg+="oremakers="+oremakers+"\n";
		
		//是否收取手续费
		boolean isFreebl=true;
		String isFree=request.getParameter("isFree");
		
		if("false".equals(isFree)){
			isFreebl=false;
		}else{
			isFreebl=true;
		}
		logMsg+="isFreebl="+isFreebl+"\n";
		
		
		/**
		 * 业务参数 
		 */
		String business = request.getParameter("business");
		logMsg+="business="+business;
		
		logger.info("会员转账接口调用信息:\n"+logMsg);
		
		DB db =null;
		VirementManager virementManager = new VirementManager();
		JSONObject jsonObj = null;
		try {
			db = DBFactory.newDB();
			
			//inMemberid:15202909616
			//如果有inMemberid 则表示给他人转账，如果没有inMemberid 表示给自己转账
			if(!Checker.isEmpty(inMemberId)){
				//
				String sql = "select id from am_member where loginaccount = '"+inMemberId+"'";
				
				MapList mapList = db.query(sql);
				if(mapList.size()>0){
					inMemberId = mapList.getRow(0).get("id");
					jsonObj = virementManager.execute(db,outMemberId,inMemberId,outAccountId,inAccountId,virementNumber,iremakers,oremakers,business,isFreebl);
				}else{
					jsonObj = new JSONObject();
					sql = "select id from mall_account where id = '"+inMemberId+"'";
					MapList mapLists = db.query(sql);
					if(!Checker.isEmpty(mapLists))
					{
						jsonObj = virementManager.execute(db,outMemberId,inMemberId,outAccountId,inAccountId,virementNumber,iremakers,oremakers,business,isFreebl);
					
					}else
					{
						try {
							jsonObj.put("code", "999");
							jsonObj.put("msg", "目标账号人不存在！");
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					
				}
			}else{
				//自己账号内部转账
				jsonObj = virementManager.execute(db,outMemberId,inMemberId,outAccountId,inAccountId,virementNumber,iremakers,oremakers,business,isFreebl);
				try {
					if(jsonObj!=null&&jsonObj.has("code")&&"0".equals(jsonObj.getString("code"))){
						
						//任务一
						//自己账号内部转账，如果转账成功，则如果转入账号为保证金账号，则需要触发任务
//						inAccountId=CREDIT_MARGIN_ACCOUNT
						//如果入账账号为现金账户
						if(SystemAccountClass.CREDIT_MARGIN_ACCOUNT.equals(inAccountId)){
							
							//理性农业  会员权限徽章任务，具体权限参考需求 社员分类划分
							TaskEngine taskEngine=TaskEngine.getInstance();
							RunTaskParams params=new RunTaskParams();
							params.setTaskCode(MemberAuthorityBadgeTask.TASK_ECODE); //会员权限徽章任务
							
							//任务触发点，信用保证金账号充值
							params.pushParam(MemberAuthorityBadgeTask.TRIGGER_POINT,
									MemberAuthorityBadgeTask.CREDIT_MARGIN_RECHANGE);
							//信用保证金账号 充值金额，单位元
							params.pushParam(MemberAuthorityBadgeTask.CREDIT_MARGIN_RECHANGE, virementNumber);
							
							//会员id
							params.setMemberId(outMemberId);
							taskEngine.executTask(params);
						}
						
						//任务二
						//如果充值账号为消费账号，则需要触发社员等级任务
						if(SystemAccountClass.CONSUMER_ACCOUNT.equals(inAccountId)){
							//理性农业  会员权限徽章任务，具体权限参考需求 社员分类划分
							TaskEngine taskEngine=TaskEngine.getInstance();
							RunTaskParams params=new RunTaskParams();
							params.setTaskCode(MemberAuthorityBadgeTask.TASK_ECODE); //会员权限徽章任务
							
							//任务触发点，消费账号充值
							params.pushParam(MemberAuthorityBadgeTask.TRIGGER_POINT,
									MemberAuthorityBadgeTask.CONSUMER_RECHANGE);
							//消费账号充值，单位元
							params.pushParam(MemberAuthorityBadgeTask.CONSUMER_RECHANGE, virementNumber);
							
							//会员id
							params.setMemberId(outMemberId);
							taskEngine.executTask(params);
						}
						
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			logger.info("jsonObj："+jsonObj);
		} catch (JDBCException e) {
			e.printStackTrace();
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		if(jsonObj==null){
			jsonObj = new JSONObject();
			try {
				jsonObj.put("code", "999");
				jsonObj.put("msg", "执行失败");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		return jsonObj.toString();
	}
}
