package com.am.frame.unitedpress.reward.impl;

import com.fastunit.Var;
import com.fastunit.jdbc.DB;

/**
 * 购买区县级别服务类商品时，对上级服务中心购买分配服务费
 * 注：此类不使用区县配送中心，合作社，联合社等
 * @author yuebin
 *
 */
public class BuyZoneServiceCenterReward extends AbstractBuyServiceCenterRewardImpl {

	@Override
	public long executeReward(DB db, String orgId, String areaType, long buyPrice, String gAP_name) throws Exception {
		double rewardMeont=0;

		logger.info(" 购买区县级别服务类商品"+gAP_name+",机构ID："+orgId);
		
		executeRewardRecord(db, orgId);
		
		//2,向上级执行奖励,区县级别的服务类商品购买，全国服务中心分配招商费，省服务中心招商费
		double ccScRatio=Var.getDouble("country_zone_sc_ratio",0);
		//单位分
		rewardMeont=buyPrice*ccScRatio;
		//总服务中心
		String rewardOrgId="		";
		long rewardMeontL = saveMerchantServiceFeeRecord(db, gAP_name, rewardMeont, rewardOrgId);
		//执行总服务中心的奖励
		executeRewardRecord(db, rewardOrgId);
		
		
		String[] orgidSplit=orgId.split("_");
		//省服务中心机构ID
		rewardOrgId=orgidSplit[0]+"_"+orgidSplit[1]+"_SC";
		ccScRatio=Var.getDouble("province_zone_sc_ratio",0);
		rewardMeont=buyPrice*ccScRatio;
		rewardMeontL+= saveMerchantServiceFeeRecord(db, gAP_name, rewardMeont, rewardOrgId);
		//执行省服务中心的奖励
		executeRewardRecord(db, rewardOrgId);
		
		
		//市服务中心机构ID
		rewardOrgId=orgidSplit[0]+"_"+orgidSplit[1]+"_"+orgidSplit[2]+"_SC";
		ccScRatio=Var.getDouble("city_zone_sc_ratio",0);
		rewardMeont=buyPrice*ccScRatio;
		rewardMeontL+= saveMerchantServiceFeeRecord(db, gAP_name, rewardMeont, rewardOrgId);
		//执行市服务中心的奖励
		executeRewardRecord(db, rewardOrgId);
		
		return rewardMeontL;
	}
	
	

}
