package com.am.frame.task;

/**
 * 用户任务接口
 * 
 * @author Administrator
 *
 */
public interface IUserTask {
	/**任务说明，进度分割符 "|"**/
	public static final String TASK_EXPLAIN_PROGRESS_DECOLLATOR="|";
	/**任务参数分割符 ";" **/
	public static final String TASK_PARAMTERE_DECOLLATOR=";";
	/**
	 * 初始化用户任务
	 * @param memberCode  用户ID
	 * @param taskId  任务ID
	 */
	public void init(String memberCode,String taskId);
	
	/**
	 * 获取任务HTML展示代码
	 * @return  HTML格式代码
	 */
	public String getHtml();
	
}
