package com.am.instore.server;

import org.json.JSONException;
import org.json.JSONObject;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月29日
 *@version
 *说明：
 */
public class UpdateOutStoreInfoServer {

	/**
	 * 检查库存数量是否满足出库数量
	 * @param materialsCode 物料编码
	 * @param storeAllocId  货位ID
	 * @param counts   数量
	 * @param db
	 * @throws JDBCException 
	 */
	public JSONObject storeAllocInfo(String materialsCode, String storeAllocId,
			String counts, DB db) throws JDBCException {
		Long stockCounts = 0L;
    	Long outStoreCounts = Long.parseLong(counts);
		 String querSQl = "SELECT counts FROM mall_store_alloc_info  WHERE MaterialsCode='"+materialsCode+"' AND StoreAllocID='"+storeAllocId+"'";
		 MapList list = db.query(querSQl);
		 if (!Checker.isEmpty(list)) {
			//数量
			stockCounts = Long.parseLong(list.getRow(0).get("counts"));
		 }
		 JSONObject resultJson  = new JSONObject();
		 try { 
			 if(outStoreCounts>stockCounts){
			 	resultJson.put("code", "403");
				resultJson.put("msg", "库存数量不足！");
			 }else{
				resultJson.put("code", "0");
				resultJson.put("msg", "库存数量充足！");
			 }
		 } catch (JSONException e) {
				e.printStackTrace();
			}
		 return resultJson;
	}

	/**
	 * 更新物资编码表的(减去)数量和出库单价
	 * @param materialsCode 物料编码
	 * @param counts  数量
	 * @param outprice  出库单价
	 * @param db
	 * @throws JDBCException 
	 */
	public void updateMaterialsCode(String materialsCode, String counts, 
			String outprice, DB db) throws JDBCException {

		if(!Checker.isEmpty(materialsCode)){
			String querMaterialsCode = "select * from p2p_MaterialsCode WHERE code='"+materialsCode+"' ";
			MapList materialsCodeList = db.query(querMaterialsCode);
			if (!Checker.isEmpty(materialsCodeList)) {
				//出库平均价格
				String outAvgPrice = materialsCodeList.getRow(0).get("out_avg_price");
				//判断出库价格是否为0  不为0时价格除以2
				if("0".equals(outAvgPrice)){
					String updateMaterialsCodeSql = "UPDATE p2p_materialscode SET out_avg_price=(out_avg_price+"+outprice+") WHERE code='"+materialsCode+"' ";
					db.execute(updateMaterialsCodeSql);
				}else{
					String updateMaterialsCodeSql = "UPDATE p2p_materialscode SET out_avg_price=(out_avg_price+"+outprice+")/2 WHERE code='"+materialsCode+"' ";
					db.execute(updateMaterialsCodeSql);
				}
			}
			//更新重量
			String updateMaterialsSql = "UPDATE p2p_materialscode SET amount=amount-"+counts+" WHERE code='"+materialsCode+"' ";
			
			db.execute(updateMaterialsSql);
			
		}
	}

	/**
	 * 更新货位库存信息表的数量(减去)
	 * @param materialsCode  物资编码
	 * @param counts  数量
	 * @param storeAllocId   货位ID
	 * @param db
	 * @throws JDBCException 
	 */
	public void updateStoreAllocInfo(String materialsCode, String counts,
			String storeAllocId, DB db) throws JDBCException {
		
		String updateStoreAllocInfoSql = "UPDATE mall_store_alloc_info SET counts=counts-'"+counts+"' WHERE materialscode='"+materialsCode+"' AND storeallocid='"+storeAllocId+"' ";
		db.execute(updateStoreAllocInfoSql);
		//查询货位库存信息表中数量是否为0
		String storeAllocInfoSql =" SELECT * FROM mall_store_alloc_info WHERE materialscode='"+materialsCode+"' AND storeallocid='"+storeAllocId+"' ";
		MapList storeAllocInfoList = db.query(storeAllocInfoSql);
		if (!Checker.isEmpty(storeAllocInfoList)) {
			//数量
			String stockCounts = storeAllocInfoList.getRow(0).get("counts");
			//判断数量是否为0  为0则删除此数据
			if("0".equals(stockCounts)){
				String deleteStoreAllocInfoSql = "DELETE FROM mall_store_alloc_info WHERE materialscode='"+materialsCode+"' AND storeallocid='"+storeAllocId+"' ";
				db.execute(deleteStoreAllocInfoSql);
			}
		}
	}
	
	/**
	 * 更新出库单状态 1，起草  2，出库中，3，已出库
	 * @param id  出库单ID
	 * @param db
	 * @throws JDBCException 
	 */
	public void updateOutStore(String id, DB db) throws JDBCException {
		if(!Checker.isEmpty(id)){
			String updateOutStoreSql = "UPDATE p2p_outstore SET datatstatus=3 WHERE id='"+id+"' ";
			db.execute(updateOutStoreSql);
		}
	}
	
	/**
	 * 更新调拨管理状态  1，起草  2，调拨中，3,已调拨
	 * @param id  调拨管理ID
	 * @param db
	 * @throws JDBCException 
	 */
	public void updateAllocation(String id, DB db) throws JDBCException {

		if(!Checker.isEmpty(id)){
			String updateAllocationSql = "UPDATE mall_allocation SET datatstatus=3 WHERE id='"+id+"' ";
			db.execute(updateAllocationSql);
		}
	}

}
