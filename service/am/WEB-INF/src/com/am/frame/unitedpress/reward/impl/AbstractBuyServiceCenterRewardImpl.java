package com.am.frame.unitedpress.reward.impl;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.virement.VirementManager;
import com.am.frame.unitedpress.reward.IBuyServiceShopReward;
import com.am.mall.services.MallService;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * 购买配送中心奖励
 * @author yuebin
 */
public abstract class AbstractBuyServiceCenterRewardImpl implements IBuyServiceShopReward {


	protected Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public String executeReward(DB db, String groupTableName, String orgId, Map<String, String> params){
		
		JSONObject reuslt=new JSONObject();
		
		String getInFoSQL="SELECT area_type,* FROM "+groupTableName+" WHERE orgid=? ";
		
		try{
			MapList groupInfoMap=db.query(getInFoSQL,new String[]{
					orgId
			},new int[]{
					Type.VARCHAR
			});
			
			//
			if(!Checker.isEmpty(groupInfoMap)){
				//01 全国;02 省;03 市;04 区县
				String area_type=groupInfoMap.getRow(0).get("area_type");
				String GAP_name=groupInfoMap.getRow(0).get("gap_name");
				
				long buy_price=groupInfoMap.getRow(0).getLong("buy_price", 0);
				
				long rewardMeony=0;
				
				//对新创建的服务中心执行商品服务费的转账操作。
				MallService ms=new MallService();
				ms.executeRewardRecord(db, orgId);
				
				//根据区域类型，对上级服务中心进行招商服务费的奖励
//				if("02".equals(area_type)){
//					//02 省
//					rewardMeony=rewardProvide(db,orgId,buy_price,GAP_name);
//					
//				}
//				if("03".equals(area_type)){
//					//03 市
//					rewardMeony=rewardCity(db,orgId,buy_price,GAP_name);			
//				}
//				if("04".equals(area_type)){
//					//04 区县
//					rewardMeony=rewardZone(db,orgId,buy_price,GAP_name);	
//				}
				
				rewardMeony=executeReward(db,orgId,area_type,buy_price,GAP_name);
				
				reuslt.put(REWARD_MEONY, rewardMeony);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return reuslt.toString();
	}
	
	
	/**
	 * 给上级提取招商服务费
	 * @param db DB
	 * @param orgId  购买机构ID
	 * @param areaType   区域类型:01 全国;02 省;03 市;04 区县  注，区县服务中心，
	 * 					涉农企业等区域类型相同，物流车辆和区县配送人员无区域类型
	 * @param buy_price 购买人
	 * @param gAP_name  购买机构的名称
	 * @return  返回抽取服务费的总额
	 */
	public abstract long executeReward(DB db, String orgId, String areaType,long buy_price, String gAP_name)throws Exception;
	


	
	/**
	 * 保存招商服务费记录
	 * @param db
	 * @param gAP_name
	 * @param rewardMeont
	 * @param rewardOrgId
	 * @return
	 * @throws JDBCException
	 */
	protected long saveMerchantServiceFeeRecord(DB db, String gAP_name, double rewardMeont, String rewardOrgId)
			throws JDBCException {
		String rmarks="购买"+gAP_name+"，机构"+rewardOrgId+"获得招商服务费："
			+VirementManager.changeF2Y(+VirementManager.double2Long(rewardMeont))+"元。";
		
		long rewardMeontL=VirementManager.double2Long(rewardMeont);
		
		//增加奖励数据到 招商服务费记录表中
		Table table=new Table("am_bdp","MERCHANT_SERVICE_FEE_RECORD");
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
	 * 检查orgId 机构奖励未执行的数据，进行招商服务费的奖励操作
	 * 此方法只有在购买市，省服务中心，如果是其他机构，则无影响.
	 * @param db
	 * @param orgId
	 * @throws JDBCException
	 * @throws JSONException
	 */
	protected void executeRewardRecord(DB db, String orgId) throws JDBCException, JSONException {
		
		//检查机构是否存在，如果不存在，则不奖励了
		String checkOrgSQL="SELECT * FROM aorg WHERE orgid=? ";
		
		MapList map=db.query(checkOrgSQL,orgId,Type.VARCHAR);
		
		if(Checker.isEmpty(map)){
			logger.info("机构"+orgId+"不存在，未将招商记录表中缓存数据进行奖励处理。");
			return ;
		}
		
		
		//1,检查是否有下级购买奖励记录为处理，如果有，则只需奖励，如果没有，则不处理
		String qurySQL="SELECT * FROM merchant_service_fee_record WHERE member_id=? AND is_allot=0 ";
		MapList recrodMap=db.query(qurySQL,new String[]{
				orgId
		},new int[]{
				Type.VARCHAR
		});
		
		
		if(!Checker.isEmpty(recrodMap)){
			VirementManager vm=new VirementManager();
			
			//更新是否处理状态
			String updateRecordSQL="UPDATE merchant_service_fee_record SET is_allot=1 WHERE id=? ";
			
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
				 
				 logger.info("服务中心获取奖励，执行转账结果："+trReslt);
				 
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
