package com.am.storestock.sql;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/**
 * @author  作者：yangdong
 * @date 创建时间：2016年4月30日 上午10:00:32
 * @version 仓库库存查询sqlprovider
 */
public class StoreStockReprotSqlProvider implements SqlProvider {

	@Override
	public String getSql(ActionContext ac) {
		
		//获取查询单元
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp", "p2p_store_stock.query");
		
		//仓库名称
		String storename = null;
		//货位编号
		String alloccode = null;
		//物资编码
		String materials_code = null;
		
		
		if(queryRow != null){
			storename = queryRow.get("storename");
			alloccode = queryRow.get("alloccode");
			materials_code = queryRow.get("materials_code");
		}
		
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT store.sname AS sname,storealloc.code AS sacode,mater.name AS materialscode, ");
		sql.append(" mater.amount AS counts,trim(to_char(COALESCE(mater.avgprice/100),'99999999999990D99')) AS in_avgprice, ");
		sql.append(" trim(to_char(COALESCE(mater.out_avg_price/100),'99999999999990D99')) AS out_avg_price ");
		sql.append(" FROM p2p_store AS store ");
		sql.append(" LEFT JOIN p2p_storealloc AS storealloc ON storealloc.storeid = store.id ");
		sql.append(" LEFT JOIN mall_store_alloc_info AS msai ON msai.storeallocid = storealloc.id ");
		sql.append(" LEFT JOIN p2p_materialscode AS mater ON mater.code = msai.materialscode ");
		sql.append(" WHERE store.orgid = '$U{orgid}' ");
		
		if(!Checker.isEmpty(storename)){
			sql.append(" AND UPPER(store.sname) LIKE UPPER('%"+storename+"%') ");
		}
		
		if(!Checker.isEmpty(alloccode)){
			sql.append(" AND UPPER(storealloc.code) LIKE UPPER('"+alloccode+"') ");
		}
		
		if(!Checker.isEmpty(materials_code)){
			sql.append(" AND UPPER(mater.name) LIKE UPPER('"+materials_code+"') ");
		}
		
		sql.append(" ORDER BY store.sname DESC,storealloc.code ASC,mater.cname ASC ");
		
		return sql.toString();
	}

}
