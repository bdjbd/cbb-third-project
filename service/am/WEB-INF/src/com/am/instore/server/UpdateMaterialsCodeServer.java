package com.am.instore.server;

import java.util.UUID;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月28日
 *@version
 *说明：入库管理server
 */
public class UpdateMaterialsCodeServer {

	/**
	 * 更新物资编码表的数量和单价
	 * @param materialsCode  物资编码
	 * @param counts  重量
	 * @param inPrice  单价（分）
	 * @param db
	 * @throws JDBCException 
	 */
	public void updateMaterialsCode(String materialsCode, String counts,
			String inPrice, DB db) throws JDBCException {
		
		if(!Checker.isEmpty(materialsCode)){
			String querMaterialsCode = "select * from p2p_MaterialsCode WHERE code='"+materialsCode+"' ";
			MapList materialsCodeList = db.query(querMaterialsCode);
			if (!Checker.isEmpty(materialsCodeList)) {
				//入库平均价格
				String avgprice = materialsCodeList.getRow(0).get("avgprice");
				//判断入库价格是否为0  不为0时价格除以2
				if("0".equals(avgprice)){
					String updateMaterialsCodeSql = "UPDATE p2p_materialscode SET avgprice=(avgprice+"+inPrice+") WHERE code='"+materialsCode+"' ";
					db.execute(updateMaterialsCodeSql);
				}else{
					String updateMaterialsCodeSql = "UPDATE p2p_materialscode SET avgprice=(avgprice+"+inPrice+")/2 WHERE code='"+materialsCode+"' ";
					db.execute(updateMaterialsCodeSql);
				}
			}
			//更新重量
			String updateMaterialsSql = "UPDATE p2p_materialscode SET amount=amount+"+counts+" WHERE code='"+materialsCode+"' ";
			
			db.execute(updateMaterialsSql);
			
		}
	}

	/**
	 * 更新货位库存信息表的物资编码和数量
	 * @param materialsCode  物资编码
	 * @param counts   数量
	 * @param storeAllocId   货位id
	 * @param db
	 * @throws JDBCException 
	 */
	public void updateStoreAllocInfo(String materialsCode, String counts,
			String storeAllocId, DB db) throws JDBCException {
		String uuid = UUID.randomUUID().toString();
		//查询货位库存信息表中有没有相应的入库货物
		String querSql =" SELECT * FROM mall_store_alloc_info WHERE materialscode='"+materialsCode+"' AND storeallocid='"+storeAllocId+"' ";
		MapList list = db.query(querSql);
		//如果有修改没有新增
		if (!Checker.isEmpty(list)) {
			String updateStoreAllocInfoSql = "UPDATE mall_store_alloc_info SET counts=counts+'"+counts+"' WHERE materialscode='"+materialsCode+"' AND storeallocid='"+storeAllocId+"' ";
			db.execute(updateStoreAllocInfoSql);
		}else{
			String sqlinsert = "INSERT INTO mall_store_alloc_info( id, materialscode, StoreAllocID, counts,create_time) "
					+ "values ('"+uuid+"','"+materialsCode+ "','"+storeAllocId+ "','"+counts+ "',now()) ";
			db.execute(sqlinsert);
		}
	}

}
