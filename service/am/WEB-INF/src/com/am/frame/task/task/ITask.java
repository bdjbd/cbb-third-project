package com.am.frame.task.task;

import com.am.frame.task.params.RunTaskParams;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.exception.JDBCException;

/**
 * 用户任务接口
 * 
 * @author Administrator
 *
 */
public interface ITask {
	
	/**奖励值**/
	public static final String REWARD_VALUE="ITask.REWARD_VALUE";
	/**
	 * 任务触发
	 * strJSON 为JSON结构，不同任务其值不同
	 */
    boolean execute(RunTaskParams strJson);
    
    /****
     * 给用户初始化任务
     * @param memberId 会员id
     * @param entTaskId 企业任务id
     * @return
     */
    boolean addTaskByUserId(String memberId,String entTaskId,DB db)throws JDBCException;
    
    /***
     * 初始化任务数据
     * @param strJson
     */
    void initTask(RunTaskParams strJson);
    
    /**
     * 得到任务变量
     * @param taskID
     * @return
     */
    String getTaskJson(String taskID);
	
	/**
	 * 得到任务HtmlView模板
	 * @return  HTML格式代码
	 */
    String getTaskHtmlView(String taskID);
	
}
