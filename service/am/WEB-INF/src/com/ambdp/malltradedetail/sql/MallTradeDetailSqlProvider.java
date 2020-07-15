package com.ambdp.malltradedetail.sql;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/** * @author  作者：yangdong
 * @date 创建时间：2016年4月12日 下午3:35:31
 * @version 账户交易明细列表sql
 * @parameter  
 * @since  
 * @return
 */
public class MallTradeDetailSqlProvider implements SqlProvider{

	@Override
	public String getSql(ActionContext ac) {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT am.membername AS member_id,msac.sa_name AS account_id, ");
		sql.append(" to_char(mtd.trade_time,'YYYY-MM-dd HH:mm:SS') AS trade_time, ");
		sql.append(" trade_total_money/100 AS trade_total_money,counter_fee/100 AS counter_fee,mtd.* ");
		sql.append(" FROM mall_trade_detail AS mtd ");
		sql.append(" LEFT JOIN am_member AS am ON am.id = mtd.member_id ");
		sql.append(" LEFT JOIN mall_account_info AS mai ON mai. ID = mtd.account_id ");
		sql.append(" LEFT JOIN mall_system_account_class AS msac ON msac.id = mai.a_class_id ");
		sql.append(" WHERE 1=1 ");
		
		//获取查询单元
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp","mall_trade_detail.query");
		String member_id = null;
		String account_id = null;
		
		//获取查询数据
		if(queryRow != null){
			member_id = queryRow.get("member_ids");
			account_id = queryRow.get("account_id");
		}
		
		if(!Checker.isEmpty(member_id)){
			sql.append(" AND am.membername LIKE '%"+member_id+"%' ");
		}
		
		if(!Checker.isEmpty(account_id)){
			sql.append(" AND msac.sa_name LIKE '%"+account_id+"%' ");
		}
		return sql.toString();
	}

}
