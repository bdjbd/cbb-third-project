package com.am.frame.member.memberinfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.task.instance.GetVolunteerAccountWithQualificationItask;
import com.am.frame.task.instance.MemberInfoPerfectionTask;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.TaskEngine;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.p2p.service.IWebApiService;

/**
 * 修改用户资料
 * @author 张少飞    2017/7/18
 */

public class UpdateMemberInfo implements IWebApiService{

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public String execute(HttpServletRequest request,HttpServletResponse response) {
		
		String params = request.getParameter("params");
		JSONObject resultJSON = new JSONObject();
		DB db =null;
			
		try {
			
			JSONObject JsonObj = new JSONObject(params);
			db=DBFactory.newDB();
			
			StringBuffer sf = new StringBuffer();
			//会员姓名
			sf.append("UPDATE am_member SET membername='"+JsonObj.get("membername")+"'");
			//生日
			if(!"null".equals(JsonObj.get("memberbirthday").toString()))
			{
				sf.append(" ,memberbirthday='"+JsonObj.get("memberbirthday")+"'");	
			}else
			{
				sf.append(" ,memberbirthday=null");	
			}
			//年龄
			sf.append(" ,age='"+JsonObj.get("age")+"'");
			//性别
			sf.append(" ,membersex='"+JsonObj.get("membersex")+"'");
			//手机号
			sf.append(" ,phone='"+JsonObj.get("phone")+"'");
			//会员ID									
			sf.append(" WHERE id ='"+JsonObj.get("memberid")+"'");
			
			db.execute(sf.toString());
			
			
			
			//执行会员信息完善任务
			RunTaskParams taskParams=new RunTaskParams();
			taskParams.setMemberId(JsonObj.get("memberid").toString());
			taskParams.setTaskCode(MemberInfoPerfectionTask.MEMBER_INFO_COMPLETE_TASK);
			logger.info("完善会员信息任务");
			//执行任务
			TaskEngine.getInstance().executTask(taskParams);
			
			taskParams.setMemberId(JsonObj.get("memberid").toString());
			taskParams.setTaskCode(GetVolunteerAccountWithQualificationItask.TASK_ECODE);
			logger.info("完善会员信息任务");
			//执行任务
			TaskEngine.getInstance().executTask(taskParams);
			
			resultJSON.put("CODE", "1");
			resultJSON.put("MSG", "更新成功!");
		} catch (Exception e) {
			e.printStackTrace();
			try{
				resultJSON.put("CODE", "0");
				resultJSON.put("MSG", "更新失败!");
			}catch(Exception e1){
				e1.printStackTrace();
			}
		}finally{
			try{
				if(db!=null){
					db.close();
				}
			}catch(Exception e1){
				e1.printStackTrace();
			}
		}
		
		return resultJSON.toString();
	}

}
