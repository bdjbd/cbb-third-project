package com.am.frame.other.taskInterface.impl.zyb;

import java.util.Map;

/**
 * @author YueBin
 * @create 2016年6月28日
 * @version 
 * 说明:<br />
 * xml处理完成回调接口
 */
public abstract class  IProcessZybClasBackAbstract implements IProcessZybCallBack {

	protected String result;
	protected String busseData;
	protected Map<String, String> extendData;
	
	
	/**
	 * @param result  处理结果 （0=待执行【首次执行及重试均为该状态】，1=成功，3=失败【经过重试后失败】）
	 * @param orderCode  AM业务系统订单号
	 * @param busseData 业务数据
	 * @param extendData 扩展参数
	 */
	public IProcessZybClasBackAbstract(String result, String busseData,
			Map<String, String> extendData) {
		this.result=result;
		this.busseData=busseData;
		this.extendData=extendData;
	}
	

}
