package com.am.frame.webapi.member;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.TaskEngine;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年3月18日
 * @version 
 * 说明:<br />
 * 修改会员信息
 */
public class ModifyMemberInfo implements IWebApiService {

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		JSONObject result =new JSONObject();
		try {
			result.put("DATA","");
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		
		String sql=request.getParameter("content");
		String memberId=request.getParameter("memberId");
		
		DB db=null;
		
		if(!Checker.isEmpty(sql)){
			try {
				db=DBFactory.newDB();
				
				//将移动端提交的数据进行转码
				sql=URLDecoder.decode(sql,"UTF-8");
				
				logger.info("UpdateSQLIWebApiSQL:"+sql);
				//执行SQL
				int count=db.execute(sql);
				result.put("COUNT",count);
				
				
				//执行会员信息完善任务
				RunTaskParams params=new RunTaskParams();
				params.setMemberId(memberId);
				params.setTaskName("完善会员信息任务");//企业任务名
				
				logger.info("完善会员信息任务");
				//执行任务
				TaskEngine.getInstance().executTask(params);
				
			} catch (Exception e) {
				e.printStackTrace();
				try {
					result.put("errcode", "40007");
					result.put("errmsg",e.getMessage());
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
			}finally{
				if(db!=null){
					try {
						db.close();
					} catch (JDBCException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return result.toString();
	}

}
