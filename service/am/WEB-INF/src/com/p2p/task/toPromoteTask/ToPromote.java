package com.p2p.task.toPromoteTask;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.util.Checker;
import com.p2p.task.IUserTask;
import com.p2p.task.UserTaskAbstract;
import com.wisdeem.wwd.WeChat.Utils;


/**
 * 任务推广类
 * @author Administrator
 */
public class ToPromote extends UserTaskAbstract implements IUserTask {
	/**会员累计推广任务 任务模板ID**/
	private static final String TO_PROMOTE_TEMPID="04a928f0-4301-4a9b-afa8-85acbcdf9612";
	
	/** 目标推广数量 **/
	public static final String FINSH_PROMOTE_NUMBER = "目标推广人数";
	/** 已推广 **/
	public static final String NOW_PROMOTE_NUMBER = "已推广人数";
	
	
	public String tag="com.p2p.task.toPromoteTask.ToPromote";
	
	/**
	 * 推广任务
	 * @param promoteCode 推广码
	 * @return
	 */
	public String updateTaskProgress(String promoteCode){
		if(promoteCode!=null){
			Utils.Log(tag, "更新推广任务，推广码："+promoteCode);
			try{
				String sql=
						"SELECT ut.* FROM p2p_enterprisetask AS et                          "+
						"	LEFT  JOIN p2p_usertask AS ut ON ut.enterprisetaskid=et.id     "+
						"	WHERE et.tasktemplateid='"+TO_PROMOTE_TEMPID+"' "+
						"	AND ut.member_code="+promoteCode+
						" AND ut.taskrunstate='1'";
				
				DB db=DBFactory.getDB();
				MapList userTaskMap=db.query(sql);
				
				if(!Checker.isEmpty(userTaskMap)){
					for(int i=0;i<userTaskMap.size();i++){
						//初始化任务
						this.init(promoteCode, userTaskMap.getRow(i).get("id"));
						
						//更新任务
						updateTask();
					}
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return null;
	}

	
	/**
	 * 更新任务参数
	 */
	private void updateTask(){
		//修改任务参数
		int nowNumber=0;
		nowNumber=Integer.parseInt(this.taskProgressParams.getValueOfName(NOW_PROMOTE_NUMBER));
		nowNumber++;
		taskProgressParams.add(NOW_PROMOTE_NUMBER, nowNumber+"");
		
		if(taskExplainParams.getValueOfName(FINSH_PROMOTE_NUMBER).equals(taskProgressParams.getValueOfName(NOW_PROMOTE_NUMBER))){
			this.taskRunState="0";
		}
		
		//更新任务参数到数据库
		saveTaskParams();
		
		if("0".equals(taskRunState)){
			//完成任务，执行任务奖励
			executeReward();
		}
	}
	
	
	@Override
	public String getHtml() {
		
		String html="【任务说明】<br>";
		html+=getParamSetHtml(taskExplainParams);
		
		html+="您的推广码是："+userId+"<br>";
		
		html+="【任务完成情况】<br>";
		html+=getParamSetHtml(taskProgressParams);
		
		return html;
	}
}