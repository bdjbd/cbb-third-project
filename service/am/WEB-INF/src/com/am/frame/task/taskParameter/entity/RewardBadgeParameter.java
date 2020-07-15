package com.am.frame.task.taskParameter.entity;

import java.util.UUID;

import com.am.frame.task.TaskActionParametes;
import com.am.frame.task.taskParameter.ITaskParameterGetHtml;
import com.am.frame.task.taskParameter.ITaskParameterReward;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
/***
 * 徽章奖励参数
 * @author Administrator
 *
 */
public class RewardBadgeParameter implements ITaskParameterGetHtml,
		ITaskParameterReward {

	
	@Override
	public String executeRewar(String parameterName, String parameterValue,TaskActionParametes taskActionParam) {
		String result="";
		try {
			//为会员增加徽章
			String sql="INSERT INTO am_userbadge(id, enterprisebadgeid, memberId)  VALUES "
					+ "('"+UUID.randomUUID()+"', '"+parameterValue+"', '"+taskActionParam.memberCode+"')";
			
			result=String.valueOf(DBFactory.getDB().execute(sql));
			
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public String getHtml(String parameterName, String parameterValue,TaskActionParametes taskActionParam) {
		String html="";
		try{
			String sql="SELECT * FROM am_EnterpriseBadge  WHERE id='"+parameterValue+"'";
			
			DB db=DBFactory.getDB();
			
			MapList bagedMap=db.query(sql);

			String badgeName="";
			String badgeIconPath="";
			
			if(!Checker.isEmpty(bagedMap)){
				badgeName=bagedMap.getRow(0).get("badgename");
				badgeIconPath=bagedMap.getRow(0).get("badgeiconpath");
			}
			
			html="可以获取&nbsp;"+badgeName+"<img src='#IMG_ROOT#"+badgeIconPath
					+"' style='width:30px;height:auto;vertical-align:bottom;' />";
			
		}catch(Exception e){
			
			e.printStackTrace();
		}
		return html;
	}

}
