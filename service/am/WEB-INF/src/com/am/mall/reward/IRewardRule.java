package com.am.mall.reward;
/**
 * @author Mike
 * @create 2014年11月20日
 * @version 
 * 说明:<br />
 */
public interface IRewardRule {
	 
	/**
	* 接收参数依据参数计算结果；将结果写入；记录台账；
    * @param rrp 规则参数
    * @return
    **/
	double execute(RewardRuleParam rrp);
}
