package com.am.frame.badge.impl;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.badge.impl.authBadge.AuthorityPointParams;
import com.am.frame.badge.impl.authBadge.TaskConditionParams;

/**
 * @author YueBin
 * @create 2016年6月13日
 * @version 
 * 说明:<br />
 * 社员权限徽章参数
 */
public class AuthorityLevelBadgeParams {
	/**社员类型  "社员类型，1=消费者社员；2=单位消费者社员；3=生产者社员**/
	private String memberType;
	
	/**社员权限等级 
	 * 消费社员等级：LEVEL_WAIT,LEVEL_1_1,LEVEL_1_2,LEVEL_1_3,LEVEL_1_4,LEVEL_1_5 
	 * 生产社员等级：LEVEL_2_1,LEVEL_2_2,LEVEL_2_3,LEVEL_2_4,LEVEL_2_5",
	 * **/
	private String level;
	/**GET_INVST_PROJECTBONUS 获得股份分红 **/
	private boolean GET_INVST_PROJECTBONUS=false;
	/**邀请下级社员获得服务费**/
	private boolean GET_INVITE_FREE=false;
	/**条件**/
	private TaskConditionParams TASK_CONDITION;
	/**权利**/
	private AuthorityPointParams AuthorityPoint;
	
	public AuthorityLevelBadgeParams(){}
	
	public AuthorityLevelBadgeParams(JSONObject params){
		if(params!=null){
			try {
				this.level=params.getString("level");
				this.memberType=params.getString("memberType");
				this.GET_INVST_PROJECTBONUS=params.getBoolean("GET_INVST_PROJECTBONUS");
				this.GET_INVITE_FREE=params.getBoolean("GET_INVITE_FREE");
				this.TASK_CONDITION=new TaskConditionParams(params.getJSONObject("TASK_CONDITION"));
				this.AuthorityPoint=new AuthorityPointParams(params.getJSONObject("AuthorityPoint"));// params.getInt("HELP_FARMER");
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**返回JSON格式字符串**/
	public JSONObject toJson(){
		JSONObject result=new JSONObject();
		try {
			result.put("level",level);
			result.put("memberType",memberType);
			result.put("GET_INVST_PROJECTBONUS",GET_INVST_PROJECTBONUS);
			result.put("GET_INVITE_FREE",GET_INVITE_FREE);
			result.put("TASK_CONDITION",TASK_CONDITION.toJson());
			result.put("AuthorityPoint",AuthorityPoint.toJOSN());
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	public String getMemberType() {
		return memberType;
	}
	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public TaskConditionParams getTASK_CONDITION() {
		return TASK_CONDITION;
	}
	public void setTASK_CONDITION(TaskConditionParams tASK_CONDITION) {
		TASK_CONDITION = tASK_CONDITION;
	}
	public AuthorityPointParams getAuthorityPoint() {
		return AuthorityPoint;
	}
	public void setAuthorityPoint(AuthorityPointParams authorityPoint) {
		AuthorityPoint = authorityPoint;
	}

	public boolean isGET_INVST_PROJECTBONUS() {
		return GET_INVST_PROJECTBONUS;
	}

	public void setGET_INVST_PROJECTBONUS(boolean gET_INVST_PROJECTBONUS) {
		GET_INVST_PROJECTBONUS = gET_INVST_PROJECTBONUS;
	}

	public boolean isGET_INVITE_FREE() {
		return GET_INVITE_FREE;
	}

	public void setGET_INVITE_FREE(boolean gET_INVITE_FREE) {
		GET_INVITE_FREE = gET_INVITE_FREE;
	}
	
	
}
