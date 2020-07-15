package com.am.frame.transactions.callback;

import com.fastunit.jdbc.DB;

/**
 * 定义 回调接口执行方法
 * @author mac
 *
 */
public interface IBusinessCallBack {
	
	/**
	 * 出账和入账都会回调次接口
	 * 业务支付，转账回调接口，业务数在bussinesss中，success_call_back：“成功业务回调业务处理类的全限定名” 
	 * 		lost_call_back:"失败业务处理的类全限定名"
	 * @param id (mall_trade_detail.id) 交易明细ID
	 * @param db  DB
	 * @param type  1 出账  2 入账  
	 * @return 
	 * @throws Exception 
	 */
	public String callBackExec(String id,String business,DB db,String type) throws Exception;

}
