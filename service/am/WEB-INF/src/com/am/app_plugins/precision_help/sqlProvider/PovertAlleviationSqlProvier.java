package com.am.app_plugins.precision_help.sqlProvider;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;
/** * @author  作者：qintao
 * @date 创建时间：2016年11月26
 * @version 借款创业管理sql
 */
public class PovertAlleviationSqlProvier implements SqlProvider {

	private static final long serialVersionUID = 1L;

	
	@Override
	public String getSql(ActionContext ac) {
		// 1,获取查询单元
				Row queryRow = FastUnit.getQueryRow(ac, "am_bdp","lxny_anti_poverty_funds_record.query");
				String anti_total_amount="";
				if (queryRow != null) {
					 anti_total_amount = queryRow.get("anti_total_amounts");
				}

		
		String sql="SELECT anti_amount/100 AS anti_amounts,"
				+ "anti_total_amount/100 AS anti_total_amounts,"
				+ " *,to_char(create_time,'yyyy-mm-dd HH24:MI:SS') AS create_times "
				+ "FROM lxny_anti_poverty_funds_record";
		
		if(!Checker.isEmpty(anti_total_amount)){
		
			if (Checker.isInteger(anti_total_amount)) {
				int anti_total_amounts=Integer.parseInt(anti_total_amount)*100;
				sql+=" where anti_total_amount="+anti_total_amounts+" ORDER BY create_time DESC";
			}else{
				sql+=" where 1=2 ";
			}
		}else{
			sql+=" ORDER BY create_time DESC";
		}
		
		return sql;
	}

	
	
}
