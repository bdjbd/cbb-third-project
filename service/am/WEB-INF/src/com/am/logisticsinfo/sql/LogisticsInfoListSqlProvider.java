package com.am.logisticsinfo.sql;

import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;

/** * @author  作者：yangdong
 * @date 创建时间：2016年5月4日 下午2:34:40
 * @version 
 * @parameter  
 * @since  
 * @return
 */
public class LogisticsInfoListSqlProvider implements SqlProvider{

	@Override
	public String getSql(ActionContext arg0) {
		// 获取查询单元
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT *  ");
		sql.append(" FROM mall_logistics_info AS mli ");
		sql.append(" LEFT JOIN mall_logistics_vehicles AS mlv ON mlv.id = mli.mall_logistics_vehicles_id ");
		sql.append(" WHERE 1=1 ");
		sql.append("  ");
		sql.append("  ");
		sql.append("  ");
		sql.append("  ");
		sql.append("  ");
		sql.append("  ");
		sql.append("  ");
		
		return null;
	}

}
