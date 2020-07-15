package com.p2p.base.task;

import com.p2p.base.ParametersList;

/**
 * Author: Mike 2014年7月16日 说明：
 * 
 **/
public abstract class UserTaskAbstract implements IUserTask {

	public static final String tag="UserTaskAbstract";
	/**任务参数**/
	protected ParametersList targetParame=new ParametersList();
	/**会员编号**/
	protected String memberCode;
	/**用户任务ID**/
	protected String userTaskId;
	/**任务状态   未完成=1/完成=0；**/
	protected String taskRunState="1";
	
}
