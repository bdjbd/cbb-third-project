package com.p2p.base.task;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.base.ParametersList;
import com.p2p.base.task.param.TaskParamActionParamObj;
import com.p2p.base.task.param.TaskParamterManager;
import com.wisdeem.wwd.WeChat.Utils;

/**
 * Author: Mike 2014年7月20日 说明：推广任务
 * 
 **/
public class PopulMemberTask extends UserTaskAbstract {
	private static final String tag="PopulMemberTask";

	//奖励积分=30;已推广人数=0;目标推广人数=2
	/** 目标数量 **/
	public static final String FINSH_NUMBER = "目标推广人数";
	/** 已推广 **/
	public static final String NOW_NUMBER = "已推广人数";
	/** 奖品 **/
	public static final String PRIZE = "奖品";
	/** 奖励 积分**/
	public static final String REWARD_SCORE = "奖励积分";
	/**奖励电子券**/
	public static final String REWARD_ELECT = "奖励电子券";
	/**奖励徽章**/
	public static final String REWARD_BADGE= "奖励徽章";
	
	private TaskParamterManager tpm=new TaskParamterManager();
	
	public PopulMemberTask()
	{
		
		
/*		ITgTask.updateJL(TaskParamList);
		ITgTask.getHtml(TaskParamList);
		
		TaskParamList(member_code,taskID,value);
		
		p2p.task.TaskParamterManager.init()
		{
			mParameterAction.put(FINSH_NUMBER, "p2p.task.tgtask.xxxx");
			mParameterAction.put(NOW_NUMBER, "p2p.task.tgtask.xxxx");
			mParameterAction.put(REWARD_SCORE, "p2p.task.tgtask.xxxx");
			mParameterAction.put(REWARD_ELECT, "p2p.task.tgtask.xxxx");
			mParameterAction.put(REWARD_BADGE, "p2p.task.tgtask.xxxx");
		}
		
		mParameterAction.geJL(TaskParamList,paramName,paramValue);
		{
			反射处理类；
			return 
		}
		
		mParameterAction.getHtml(TaskParamList,paramName,paramValue)
		{
			反射处理类；
			
			return html;
		}*/
		
	}

	@Override
	public ParametersList getTaskParam() {
		return targetParame;
	}

	/**
	 * 更新任务数据 判断任务数据是否达到完成条件 如达到完成条件则更新改任务状态为：任务完成，同时为用户更新任务奖励 返回任务参数是否更新成功
	 * 
	 * @pdOid dc92bce8-d35a-43fd-a150-601691a181c5
	 */
	@Override
	public boolean saveTaskParam() 
	{
		boolean result = false;
		DB db=null;
		Connection conn=null;
		
		try 
		{
			//判断任务是否完成，对比目标人数与已推广人数
			if(this.getTaskParam().getValueOfName(FINSH_NUMBER).equals(this.getTaskParam().getValueOfName(NOW_NUMBER)))
			{
				this.taskRunState="0";
			}
			
			// 保存任务参数到数据库
			String sql = "UPDATE p2p_usertask   "
					+ " SET "
					+ " taskrunstate='"+this.taskRunState+"', " 
					+ " taskparame='"+this.targetParame.toString()+"' " 
					+ " WHERE id='"+this.userTaskId+"'";
			
			db=DBFactory.newDB();
			
			conn = db.getConnection();
			conn.createStatement().executeUpdate(sql);
			
			
			// 检查任务是否完成
			if("0".equals(this.taskRunState))
			{
				//任务完成
				Utils.Log(tag, "任务完成");
				
				// 完成奖励
				/*String rewardScore=this.getTaskParam().getValueOfName(REWARD_SCORE);
				String rewardElect=this.getTaskParam().getValueOfName(REWARD_ELECT);
				String rewardBadge=this.getTaskParam().getValueOfName(REWARD_BADGE);*/
				
				
				TaskParamActionParamObj tpap=new TaskParamActionParamObj();
				
				for(int i=0;i<getTaskParam().getCoutnt();i++)
				{
					tpm.executeJL(tpap, getTaskParam().getName(i), getTaskParam().getValueOfIndex(i));
				}
				
				
				
				/*if(rewardScore!=null)
				{
					//积分奖励
					//奖品=1，表示奖励积分 奖励=10表示奖励10个积分
					sql="UPDATE  ws_member  SET integration="
							+ " ( SELECT CASE   WHEN integration IS NULL THEN 0  ELSE integration END  "
							+ " FROM ws_member "
							+ " WHERE member_code="+this.memberCode+")+"+rewardScore
							+ "  WHERE member_code="+this.memberCode;
					conn.createStatement().execute(sql);
				}
				if(rewardElect!=null)
				{
					//TODO 电子券奖励
				}
				if(rewardBadge!=null)
				{
					//徽章奖励
					//奖品=2,表示奖励徽章  奖励=徽章唯一编码
					sql="INSERT INTO p2p_userbadge(id, enterprisebadgeid, member_code) "
							+ " VALUES "
							+ "('"+UUID.randomUUID()+"', '"+rewardBadge+"', '"+this.memberCode+"');";
					conn.createStatement().execute(sql);
				}
				result=true;*/
			}
			else
			{
				// 没有完成推出
				result=false;
			}
			
			
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
			} finally {
				result = false;
			}

		}
		return result;
	}


	@Override
	public String getUserTaskInterface() 
	{
		String tHtml="";
		
		TaskParamActionParamObj tpap=new TaskParamActionParamObj();
		
		tpap.MemberCode="";
		tpap.TaskID="";
		
		for(int i=0;i<getTaskParam().getCoutnt();i++)
		{
			tHtml+= tpm.getHtml(tpap, getTaskParam().getName(i), getTaskParam().getValueOfIndex(i));
			tHtml+= "<br>";
		}
		
		return tHtml;
		
		
		/*String str="";
		if("0".equalsIgnoreCase(this.taskRunState))
		{
			str="您已经完成任务";
		}
		else if("1".equalsIgnoreCase(this.taskRunState))
		{
			<img src="/img/integral.png">任务说明：<br>
			完成2个人的推广，可以获取10积分。<br>
			您的推广码是：<br><br>
			<img src="/img/integral.png">任务完成情况：<br>
			目前已完成0人的推广，请再接再厉。
			
			//奖励积分=30;已推广人数=0;目标推广人数=2
			//完成目标可以获取30分的奖励   
			String rewardScore=this.getTaskParam().getValueOfName(REWARD_SCORE);
			String rewardElect=this.getTaskParam().getValueOfName(REWARD_ELECT);
			String rewardBadge=this.getTaskParam().getValueOfName(REWARD_BADGE);
			if(rewardScore!=null){
				//积分奖励奖励任务
				str="【任务说明】<br>"
						+ "完成"+this.getTaskParam().getValueOfName(FINSH_NUMBER)+"个人的推广，可以获取"+rewardScore+"积分。<br>"
						+"您的推广码是："+this.memberCode+"<br>"
						+"【任务完成情况】<br>"
						+"目前已完成"+this.getTaskParam().getValueOfName(NOW_NUMBER)+"人的推广，请再接再厉。";
			}
			if(rewardElect!=null){
				//TODO 电子券奖励任务
			}
			if(rewardBadge!=null){
				//徽章奖励任务
				try{
					String sql="SELECT * FROM p2p_EnterpriseBadge  WHERE id='"+rewardBadge+"'";
					DB db=DBFactory.getDB();
					MapList bagedMap=db.query(sql);
//					Connection conn = DBUtil.getConnection();
//					PreparedStatement pste = conn.prepareStatement(sql);
//					pste.setString(1,this.getTaskParam().getValueOfName(rewardBadge));
//					//保存数据
//					ResultSet rst=pste.executeQuery(sql);
					
					String badgeName="";
					String badgeIconPath="";
					if(!Checker.isEmpty(bagedMap)){
//						badgeName=rst.getString("badgename");
//						badgeIconPath=rst.getString("badgeiconpath");
						badgeName=bagedMap.getRow(0).get("badgename");
						badgeIconPath=bagedMap.getRow(0).get("badgeiconpath");
					}
					str="【任务说明】<br>"
							+ "完成"+this.getTaskParam().getValueOfName(FINSH_NUMBER)+"个人的推广，可以获取&nbsp;"+badgeName
							+"<img src='#IMG_ROOT#"+badgeIconPath+"' style='width:30px;height:auto;vertical-align:bottom;' /> <br>"
							+"您的推广码是："+this.memberCode+"<br>"
							+"【任务完成情况】<br>"
							+ "目前已完成"+this.getTaskParam().getValueOfName(NOW_NUMBER)+"人的推广，请再接再厉。";
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}else{
			str="";
		}
		return str;*/
	}

	
	@Override
	public String getTaskCompletionStatus() {
		String str="";
		
		return str;
	}
	
	/**
	 * 更新用推广任务
	 * @param popCode  推广码
	 */
	public void updatePopMemberTask(String userID){
		if(userID==null)return ;
		
		DB db=null;
		Connection conn=null;
		
		try {
			
			db=DBFactory.newDB();
			
			Utils.Log(tag, "更新推广任务，用户ID"+userID);
			conn=db.getConnection();
			String sql="SELECT * FROM p2p_UserTask  WHERE taskrunstate='1' AND member_code="+userID;
			Statement stat=conn.createStatement();
			ResultSet rs=stat.executeQuery(sql);
			if(rs.next()){
				String userTaskID=rs.getString("id");
				//初始化任务
				init(userID, userTaskID);
				//修改任务参数
				int nowNumber=0;
				try{
					nowNumber=this.getTaskParam().getValueOfName(NOW_NUMBER)==null?0:Integer.parseInt(this.getTaskParam().getValueOfName(NOW_NUMBER));
					nowNumber++;
					this.getTaskParam().add(NOW_NUMBER, nowNumber+"");
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}
			//保存用户任务信息
			saveTaskParam();
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
	}
	
	/**
	 * 初始化方法
	 * 
	 * @param userID
	 *            当前登录用户的ID
	 * @param userTaskID
	 *            用户任务ID
	 * @pdOid 44cd6674-debb-454c-bd74-db6cc78dd33f
	 */
	@Override
	public void init(String userID, String userTaskID) {
		this.memberCode = userID;
		this.userTaskId = userTaskID;
		try {
			DB db = DBFactory.getDB();
			
			String sql = "SELECT * FROM p2p_usertask WHERE member_code="
					+ userID + " AND id='" +this.userTaskId + "'";
			
			MapList map = db.query(sql);
			
			if (Checker.isEmpty(map))
				return;
			
			//获取任务状态
			String taskStatus=map.getRow(0).get("taskrunstate");
			if("0".equals(taskStatus))return;
			
			//获取任务参数
			String parame = map.getRow(0).get("taskparame");
			if (parame == null)
				return;
			
			String[] params = parame.split(";");
			for(int i=0;i<params.length;i++)
			{
				String[] kvs = params[i].split("=");
				targetParame.add(kvs[0], kvs[1]);
			}
		} catch (JDBCException e) {
			e.printStackTrace();
		}
	}
	
}
