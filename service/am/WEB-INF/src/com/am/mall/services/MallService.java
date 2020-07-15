package com.am.mall.services;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * 商城商品处理服务类
 * @author yuebin
 *
 */
public class MallService {

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	public long reward(DB db, double buyPrice,String province,String city,String zone, String commodityName)throws Exception {
		double rewardMeont=0;
		
		logger.info("购买商品,分配服务费到服务中心.");
		
		
		//1,给区县服务中心分配服务费
		String orgId="org_P"+province+"_C"+city+"_Z"+zone+"_SC";
		String rewardOrgId=orgId;
		double ccScRatio=Var.getDouble("ZONE_ORDER_SERVICE_FREE_RATIO",0);
		rewardMeont=buyPrice*ccScRatio*100;
		long rewardMeontL = saveMerchantServiceFeeRecord(db, commodityName, rewardMeont, rewardOrgId);
		
		executeRewardRecord(db, rewardOrgId);
		
		
		//2,给市服务中心分配服务费
		rewardOrgId="org_P"+province+"_C"+city+"_SC";
		ccScRatio=Var.getDouble("CITY_ORDER_SERVICE_FREE_RATIO",0);
		rewardMeont=buyPrice*ccScRatio*100;
		rewardMeontL+= saveMerchantServiceFeeRecord(db, commodityName, rewardMeont, rewardOrgId);
		
		executeRewardRecord(db, rewardOrgId);
		
		//3,给省服务中心分配服务费
		rewardOrgId="org_P"+province+"_SC";
		ccScRatio=Var.getDouble("PROVINCE_ORDER_SERVICE_FREE_RATIO",0);
		rewardMeont=buyPrice*ccScRatio*100;
		rewardMeontL+= saveMerchantServiceFeeRecord(db, commodityName, rewardMeont, rewardOrgId);
		
		executeRewardRecord(db, rewardOrgId);
		
		//4,给总服务中心分配服务费
		rewardOrgId="country_SC";
		ccScRatio=Var.getDouble("COUNTRY_ORDER_SERVICE_FREE_RATIO",0);
		rewardMeont=buyPrice*ccScRatio*100;
		rewardMeontL+= saveMerchantServiceFeeRecord(db, commodityName, rewardMeont, rewardOrgId);
		
		executeRewardRecord(db, rewardOrgId);
		
		return rewardMeontL;
	}


	
	
	
	/**
	 * 保存商品购买服务费记录
	 * @param db
	 * @param commodityName 商品名称
	 * @param rewardMeont
	 * @param rewardOrgId
	 * @return
	 * @throws JDBCException
	 */
	public long saveMerchantServiceFeeRecord(DB db, String commodityName, double rewardMeont, String rewardOrgId)
			throws JDBCException {
		String rmarks="区域内社员购买"+commodityName+"，获得服务费。";
		long rewardMeontL=VirementManager.double2Long(rewardMeont);
		//增加奖励数据到 招商服务费记录表中
		Table table=new Table("am_bdp","MALL_BUY_SHOP_BONUS_RECORD");
		TableRow inserRow=table.addInsertRow();
		inserRow.setValue("member_id",rewardOrgId);
		inserRow.setValue("trade_total_money",rewardMeontL);
		inserRow.setValue("record_trade_total_money",rewardMeontL);
		inserRow.setValue("trade_type",2);
		inserRow.setValue("rmarks",rmarks);
		db.save(table);
		
		return rewardMeontL;
	}



	
	/**
	 * 检查orgId 机构奖励未执行的数据，进行商品购买服务费的奖励操作
	 * @param db
	 * @param orgId
	 * @throws JDBCException
	 * @throws JSONException
	 */
	public void executeRewardRecord(DB db, String orgId) throws JDBCException, JSONException {
		
		//检查机构是否存在，如果不存在，则不奖励了
		String checkOrgSQL="SELECT * FROM aorg WHERE orgid=? ";
		
		MapList map=db.query(checkOrgSQL,orgId,Type.VARCHAR);
		
		if(Checker.isEmpty(map)){
			logger.info("机构"+orgId+"不存在，未将服务费分配记录表中缓存数据进行奖励处理。");
			return ;
		}
		
		
		//1,检查是否有下级购买奖励记录为处理，如果有，则只需奖励，如果没有，则不处理
		String qurySQL="SELECT * FROM mall_buy_shop_bonus_record WHERE member_id=? AND is_allot=0 ";
		MapList recrodMap=db.query(qurySQL,new String[]{
				orgId
		},new int[]{
				Type.VARCHAR
		});
		
		
		
		if(!Checker.isEmpty(recrodMap)){
			VirementManager vm=new VirementManager();
			
			//更新是否处理状态
			String updateRecordSQL="UPDATE mall_buy_shop_bonus_record SET is_allot=1 WHERE id=? ";
			
			for(int i=0;i<recrodMap.size();i++){
				Row row=recrodMap.getRow(i);
				
				String id=row.get("id");
				String memberId=row.get("member_id");
				double payMeont=VirementManager.changeF2Y(row.getLong("trade_total_money",0));
				
				String iremakers=row.get("rmarks");
				
				//执行状态
				 JSONObject trReslt=vm.execute(db, 
						"",
						memberId,
						"",
						SystemAccountClass.GROUP_CASH_ACCOUNT,
						payMeont+"",
						iremakers,
						"",
						"",
						false);
				 
				 logger.info("服务中心获取商品购买服务费，执行转账结果："+trReslt+"\n奖励机构："+memberId+" 金额："+payMeont);
				 
				if(trReslt!=null&&"0".equals(trReslt.get("code"))){
					
					//转账成功，将分配状态修改为已分配
					db.execute(updateRecordSQL,new String[]{
							id
					},new int[]{
							Type.VARCHAR
					});
				}
				
			}
		}
	}
	
}
