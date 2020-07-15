package com.am.frame.unitedpress.reward.impl;

import org.json.JSONException;

import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.exception.JDBCException;

/**
 * 购买省级别的服务类商品市奖励规则
 * @author yuebin
 *
 */
public class BuyProvinceServiceCenterReward extends AbstractBuyServiceCenterRewardImpl {

	/**
	 * 购买省服务中心奖励
	 * @param db
	 * @param orgId
	 * @param gAP_name 
	 * @param buy_price 购买金额
	 * @throws JDBCException 
	 * @return 分配的金额
	 * @throws JSONException 
	 */
	@Override
	public long executeReward(DB db, String orgId, String areaType, long buyPrice, String gAP_name) throws Exception {
		
		double rewardMeont=0;

		logger.info(" 购买省服务级别服务类商品,机构ID："+orgId+"，机构名称："+gAP_name+",获取服务费。");
		
		//1,检查如果此机构是新建机构，则将缓存数据的数据线奖励给此机构。
		executeRewardRecord(db, orgId);
		
		//2,向上级执行奖励,省服务中心购买，只需向全国服务中心分配招商费
		double pScRatio=Var.getDouble("country_province_sc_ratio",0);
		
		//单位分
		rewardMeont=buyPrice*pScRatio;
		
		//总服务中心
		String rewardOrgId="country_SC";
		
		long rewardMeontL = saveMerchantServiceFeeRecord(db, gAP_name, rewardMeont, rewardOrgId);
		
		//执行奖励
		executeRewardRecord(db, rewardOrgId);
		
		return rewardMeontL;
	}
	
	
	

}
