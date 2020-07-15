package com.p2p.base.task;

import com.p2p.base.ParametersList;

/**
 * Author: Mike
 * 2014年7月16日
 * 说明：用户任务接口
 *
 **/
public interface IUserTask {
	 /** @pdOid 2aeda589-492f-4d17-8377-12848b6eb573 */
	   ParametersList getTaskParam();
	   
	   /** 更新任务数据
	    * 判断任务数据是否达到完成条件
	    * 如达到完成条件则更新改任务状态为：任务完成，同时为用户更新任务奖励
	    * 返回任务参数是否更新成功
	    *
	    * @pdOid dc92bce8-d35a-43fd-a150-601691a181c5 */
	   boolean saveTaskParam();
	   
	   /** 返回用户任务完成状态的字符串，可以带换行，需要在不同客户端显示
	    * 自动计算任务完成度，并生成相应字符串
	    *
	    * @pdOid 31bbece7-c9ea-4d97-aa8f-3fccb61cfa64 */
	   String getTaskCompletionStatus();
	   
	   /** 初始化方法
	    *
	    * @param userID 当前登录用户的ID
	    * @param userTaskID 用户任务ID
	    * @pdOid 0b441444-3f21-443f-bcc3-6cd1040a1765 */
	   void init(String userID, String userTaskID);
	   
	   /** 返回用户任务界面的Html代码。
	    *
	    * @pdOid 56b77277-340a-4502-ad7e-db06cc9271ff */
	   String getUserTaskInterface();
	   
	   
}
