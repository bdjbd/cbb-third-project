package com.am.frame.other.taskInterface.impl.zyb;

import java.util.Map;

import com.fastunit.jdbc.DB;

/**
 * @author YueBin
 * @create 2016年6月27日
 * @version 
 * 说明:<br />
 * 
 */
public interface IProcessZybCallBack {
	
	/**
	 *  业务接口处理
	 * @param result  处理结果 （0=待执行【首次执行及重试均为该状态】，1=成功，3=失败【经过重试后失败】）
	 * @param orderCode  AM业务系统订单号
	 * @param busseData 业务数据
	 * @param extendData 扩展参数
	 * @return
	 */
	public String  process(DB db, String taskRecordId,
			String requestData, String responseData, long taskRetryTime,
			long maxRetryTime, String busseData,String result,Map<String,String> extendData);

}
