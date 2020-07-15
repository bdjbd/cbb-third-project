package com.ambdp.instore.sql;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/** * @author  作者：yangdong
 * @date 创建时间：2016年1月15日 下午6:15:27
 * @version 说明：入库查询sql
 */
public class InstoreSqlprovider implements SqlProvider{

	@Override
	public String getSql(ActionContext ac) {
		
		//当前登录人的组织机构
		String orgid = ac.getVisitor().getUser().getOrgId();
		System.out.println(orgid);
		// 入库查询sql
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT pin.* FROM p2p_InStore AS pin  ");
		sql.append(" LEFT JOIN p2p_MaterialsCode AS pmc ON pin.MaterialsCode=pmc.code  ");
		sql.append(" WHERE pmc.orgid='"+orgid+"' ");
		
		//获取查询单元
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp", "p2p_instore.query");
		
		String materialscode = null;
		String incode = null;
		if(queryRow != null){
			materialscode = queryRow.get("materialscodes");
			incode = queryRow.get("incodes");
		}
		
		if(!Checker.isEmpty(materialscode)){
			sql.append(" AND pmc.cname like  '%"+materialscode+"%' ");
		}
		
		if(!Checker.isEmpty(incode)){
			sql.append(" AND pin. incode like  '%"+incode+"%' ");
		}
		
		sql.append(" ORDER BY pin.creattiem DESC ");
		
		return sql.toString();
	}

}
