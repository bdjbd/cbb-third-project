package com.am.frame.unitedpress.reward;

import java.util.Map;

import com.fastunit.jdbc.DB;

/**
 * 购买服务类商品奖励类
 * @author yuebin
 */
public interface IBuyServiceShopReward{
	
	/**招商费用用后的金额**/
	public static final String REWARD_MEONY="REWARD_MEONY";
	
	/**
	 * 购买服务类商品奖励类
	 * @param db  DB
	 * @param groupTableName 机构对应的表明
	 * @param orgId  机构id
	 * @param params  参数
	 * @return JSONObject
	 */
	public String executeReward(DB db,String groupTableName,String orgId,Map<String,String> params);
}
