package com.am.frame.task.reward.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.ITask;
import com.am.frame.task.task.ITaskReward;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年3月10日
 * @version 
 * 说明:<br />
 * 奖励积分
 */
public class RewardScoreRule implements ITaskReward {

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public boolean execute(RunTaskParams param) {
		
		DB db=null;
		try{
			db=DBFactory.newDB();
			
			//奖励积分，奖励规则配置积分，为默认值
			String score=param.getRewParams().getJSONObject("rewardParams").getString("score");
			
			//检查是否有任务计算出的奖励值
			if(param.getParams(ITask.REWARD_VALUE)!=null){
				score=param.getParams(ITask.REWARD_VALUE)+"";
				logger.info("任务设定奖励值："+score);
			}
			
			int intScore=0;
			if(!Checker.isEmpty(score)){
				Double d=new Double(score);
				intScore=d.intValue();
			}
			
			logger.info("奖励积分："+intScore);
			
			List<String> memberIds=param.getTargetMemberList();
			//根据积分
			String updateSQL="UPDATE am_member SET score=COALESCE(score,0)+"
					+intScore+" WHERE id=? ";
			
			List<String[]>  updateSQLparams=new ArrayList<String[]>();
			
			//记录到会员积分记录中
			Table scoreTable=new Table("am_bdp", "AM_SCORE_DETAIL");
			
			for(int i=0;i<memberIds.size();i++){
				updateSQLparams.add(new String[]{memberIds.get(i)} );
				TableRow inserRow=scoreTable.addInsertRow();
				//s_type   0:增加 ;1:扣除 
				inserRow.setValue("s_type",0);
				//score_value 积分值
				inserRow.setValue("score_value",intScore);
				//设置会员id
				inserRow.setValue("member_id",memberIds.get(i));
				//remark 积分操作原因
				inserRow.setValue("remark",param.getTaskName()+".奖励积分。");
			}
			//更新奖励目标会员的积分
			db.executeBatch(updateSQL, updateSQLparams,new int[]{Type.VARCHAR});
			//更新奖励会员的积分记录
			db.save(scoreTable);
			
		}catch(Exception e){
			e.printStackTrace();
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		return true;
	}

}
