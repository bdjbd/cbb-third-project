package com.am.frame.other.taskInterface;

import com.fastunit.jdbc.DB;

/**
 * @author YueBin
 * @create 2016年6月26日
 * @version 
 * 说明:<br />
 * 第三方接口业务实现类接口。
 execute(DB db,接口任务执行ID)
	{
		判断任务状态==0
		{
			生成接口请求数据
			请求接口，记录返回数据、重试次数、任务最后执行时间
			如果成功，修改任务状态，并执行成功后的业务处理
			如果失败，判断最大重试次数，超出则设置任务状态为失败并执行失败后的业务处理，反之等待再次执行
		}
	} 
 */
public interface ITaskOtherInterface {
	
	/**
	 * 接口任务参数
	 * @param db    DB 
	 * @param taskRecordId  接口任务执行记录表主键
	 * 	判断任务状态==0
	 * 	{
	 * 		生成接口请求数据
	 * 		请求接口，记录返回数据、重试次数、任务最后执行时间
	 * 		如果成功，修改任务状态，并执行成功后的业务处理
	 * 		如果失败，判断最大重试次数，超出则设置任务状态为失败并执行失败后的业务处理，反之等待再次执行
	 * 	}
	 * 
	 */
	public void execute(DB db,String taskRecordId);
}
