package com.am.borrowing.sql;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/** * @author  作者：yangdong
 * @date 创建时间：2016年4月28日 下午4:59:57
 * @version 借款创业管理sql
 */
public class BorrowingSqlProvider implements SqlProvider{

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp","mall_borrowing_records.query");
		String membername = null;
		String status = null;
		if(queryRow != null){
			membername = queryRow.get("member_name");
			status = queryRow.get("statuss");
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ame.membername AS member_id,ame.membersex AS sex,ame.phone AS phone,msc.price/100 AS borrow_money,mbr.*  ");
		sql.append(" FROM mall_borrowing_records AS mbr ");
		sql.append(" LEFT JOIN am_member AS ame ON ame.id = mbr.member_id ");
		sql.append(" LEFT JOIN mall_service_commodity AS msc ON  msc.id = mbr.sc_id ");
		sql.append(" WHERE 1=1 ");
		
		if(!Checker.isEmpty(membername)){
			sql.append(" AND UPPER(ame.membername) LIKE UPPER('%"+membername+"%') ");
		}
		
		if(!Checker.isEmpty(status)){
			sql.append(" AND mbr.status = "+status+" ");
		}
		
		sql.append(" ORDER BY create_time DESC ");
		return sql.toString();
	}

}
