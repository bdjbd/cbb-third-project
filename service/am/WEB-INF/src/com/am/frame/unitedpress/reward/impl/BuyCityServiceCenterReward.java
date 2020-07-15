package com.am.frame.unitedpress.reward.impl;

import com.fastunit.Var;
import com.fastunit.jdbc.DB;


/**
 * 购买市级别的服务中心，给上级提取招商服务费
 * @author yuebin
 *
 */
public class BuyCityServiceCenterReward extends AbstractBuyServiceCenterRewardImpl {

	@Override
	public long executeReward(DB db, String orgId, String areaType, long buyPrice, String gAP_name) throws Exception {
		double rewardMeont=0;

		logger.info(" 购买市级别服务类商品,服务类商品机构ID："+orgId);
		
		
		executeRewardRecord(db, orgId);
		
		//2,向上级执行奖励,市级别服务商品购买，全国服务中心分配招商费，省服务中心招商费
		double ccScRatio=Var.getDouble("country_city_sc_ratio",0);
		//单位分
		rewardMeont=buyPrice*ccScRatio;
		//总服务中心
		String rewardOrgId="country_SC";
		long rewardMeontL = saveMerchantServiceFeeRecord(db, gAP_name, rewardMeont, rewardOrgId);
		//执行总服务中心的奖励
		executeRewardRecord(db, rewardOrgId);
		
		
		String[] orgidSplit=orgId.split("_");
		rewardOrgId=orgidSplit[0]+"_"+orgidSplit[1]+"_SC";
		ccScRatio=Var.getDouble("province_city_sc_ratio",0);
		rewardMeont=buyPrice*ccScRatio;
		rewardMeontL+= saveMerchantServiceFeeRecord(db, gAP_name, rewardMeont, rewardOrgId);
		
		//执行省服务中心的奖励
		executeRewardRecord(db, rewardOrgId);
		
		return rewardMeontL;
	}


	
}
