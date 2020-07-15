package com.ambdp.outstore.sql;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/** * @author  作者：yangdong
 * @date 创建时间：2016年1月15日 下午6:39:08
 * @version 说明：出库查询sql
 */
public class OutstoreSqlprovider implements SqlProvider{

	@Override
	public String getSql(ActionContext ac) {
		
		//当前登录人的组织机构
		String orgid = ac.getVisitor().getUser().getOrgId();
		
		// 出库查询sql
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT pos.* FROM p2p_OutStore AS pos ");
		sql.append(" LEFT JOIN p2p_MaterialsCode AS pmc ON pos.code=pmc.code  ");
		sql.append(" WHERE pmc.orgid='"+orgid+"'  ");
		
		//获取查询单元
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp", "p2p_outstore.query");
		
		String materialscode = null;
		String outcode = null;
		if(queryRow != null){
			materialscode = queryRow.get("codes");
			outcode = queryRow.get("outcodes");
		}
		
		if(!Checker.isEmpty(materialscode)){
			sql.append(" AND pmc.cname like  '%"+materialscode+"%' ");
		}
		
		if(!Checker.isEmpty(outcode)){
			sql.append(" AND pos.outcode like  '%"+outcode+"%' ");
		}
		
		
		sql.append(" ORDER BY creattiem DESC ");
		
		return sql.toString();
	}

}
