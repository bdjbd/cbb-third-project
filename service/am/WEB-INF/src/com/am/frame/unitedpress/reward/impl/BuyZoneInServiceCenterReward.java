package com.am.frame.unitedpress.reward.impl;

import com.fastunit.Var;
import com.fastunit.jdbc.DB;

/**
 * 购买区县内服务类商品，服务中心获取服务费
 * 区县内服务类商品有：
 *  联合社；合作社；配送中心；涉农企业；自然村农厂；配送人员；物流车辆；
 * @author yuebin
 *
 */
public class BuyZoneInServiceCenterReward extends AbstractBuyServiceCenterRewardImpl {

	@Override
	public long executeReward(DB db, String orgId, String areaType, long buyPrice, String gAP_name) throws Exception {
		double rewardMeont=0;

		logger.info(" 购买区县内级别服务类商品"+gAP_name+",机构ID："+orgId);
		
		executeRewardRecord(db, orgId);
		
		
		
		//1,向上级执行奖励,区县内级别的服务类商品购买，购买区县内服务类商品，总服务中心获取招商费比例
		double ccScRatio=Var.getDouble("country_zone_in_sc_ratio",0);
		//单位分
		rewardMeont=buyPrice*ccScRatio;
		//总服务中心
		String rewardOrgId="country_SC";
		long rewardMeontL = saveMerchantServiceFeeRecord(db, gAP_name, rewardMeont, rewardOrgId);
		//执行总服务中心的奖励
		executeRewardRecord(db, rewardOrgId);
		
		
		String[] orgidSplit=orgId.split("_");
		//省服务中心机构ID
		rewardOrgId=orgidSplit[0]+"_"+orgidSplit[1]+"_SC";
		ccScRatio=Var.getDouble("province_zone_in_sc_ratio",0);
		rewardMeont=buyPrice*ccScRatio;
		rewardMeontL+= saveMerchantServiceFeeRecord(db, gAP_name, rewardMeont, rewardOrgId);
		//执行省服务中心的奖励
		executeRewardRecord(db, rewardOrgId);
		
		
		//市服务中心机构ID
		rewardOrgId=orgidSplit[0]+"_"+orgidSplit[1]+"_"+orgidSplit[2]+"_SC";
		ccScRatio=Var.getDouble("city_zone_in_sc_ratio",0);
		rewardMeont=buyPrice*ccScRatio;
		rewardMeontL+= saveMerchantServiceFeeRecord(db, gAP_name, rewardMeont, rewardOrgId);
		//执行市服务中心的奖励
		executeRewardRecord(db, rewardOrgId);
		
		
		//区县务中心机构ID
		rewardOrgId=orgidSplit[0]+"_"+orgidSplit[1]+"_"+orgidSplit[2]+"_"+orgidSplit[3]+"_SC";
		ccScRatio=Var.getDouble("zone_zone_in_sc_ratio",0);
		rewardMeont=buyPrice*ccScRatio;
		rewardMeontL+= saveMerchantServiceFeeRecord(db, gAP_name, rewardMeont, rewardOrgId);
		//执行区县务中心的奖励
		executeRewardRecord(db, rewardOrgId);
		
		return rewardMeontL;
	}

}
