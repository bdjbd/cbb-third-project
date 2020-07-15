package com.am.borrowing.sql;

import com.am.frame.systemAccount.SystemAccountClass;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;

/** * @author  作者：yangdong
 * @date 创建时间：2016年4月28日 下午5:37:21
 * @version 借款创业表单sql
 */
public class BorrowingFormSqlProvider implements SqlProvider {

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext arg0) {
		
		//食品安全追溯账户
		String FOOD_SAFETY_TRACING_ACCOUNT = SystemAccountClass.FOOD_SAFETY_TRACING_ACCOUNT;
		//身份股金账户
		String IDENTITY_STOCK_ACCOUNT = SystemAccountClass.IDENTITY_STOCK_ACCOUNT;
		//经营分红账户
		String BONUS_ACCOUNT = SystemAccountClass.BONUS_ACCOUNT;
		//信用保证金账户
		String CREDIT_MARGIN_ACCOUNT = SystemAccountClass.CREDIT_MARGIN_ACCOUNT;
		//消费账户
		String CONSUMER_ACCOUNT = SystemAccountClass.CONSUMER_ACCOUNT;
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ame.membername AS member_name,ame.phone AS phone,ame.email As email,ame.membersex AS sex,  ");
		sql.append(" ame.wxnickname AS wxnickname, ");
		sql.append(" (SELECT balance/100 FROM mall_account_info WHERE member_orgid_id = mbr.member_id AND a_class_id = ");
		sql.append(" 	(SELECT id FROM mall_system_account_class WHERE sa_code = '"+FOOD_SAFETY_TRACING_ACCOUNT+"')  ");
		sql.append(" ) AS food_safety_tracing_account, ");
		sql.append(" (SELECT balance/100 FROM mall_account_info WHERE member_orgid_id = mbr.member_id AND a_class_id = ");
		sql.append(" 	(SELECT id FROM mall_system_account_class WHERE sa_code = '"+IDENTITY_STOCK_ACCOUNT+"')  ");
		sql.append(" ) AS identity_stock_account, ");
		sql.append(" (SELECT balance/100 FROM mall_account_info WHERE member_orgid_id = mbr.member_id AND a_class_id = ");
		sql.append(" 	(SELECT id FROM mall_system_account_class WHERE sa_code = '"+BONUS_ACCOUNT+"')  ");
		sql.append(" ) AS bonus_account, ");
		sql.append(" (SELECT balance/100 FROM mall_account_info WHERE member_orgid_id = mbr.member_id AND a_class_id = ");
		sql.append(" 	(SELECT id FROM mall_system_account_class WHERE sa_code = '"+CREDIT_MARGIN_ACCOUNT+"')  ");
		sql.append(" ) AS credit_margin_account, ");
		sql.append(" (SELECT balance/100 FROM mall_account_info WHERE member_orgid_id = mbr.member_id AND a_class_id = ");
		sql.append(" 	(SELECT id FROM mall_system_account_class WHERE sa_code = '"+CONSUMER_ACCOUNT+"')  ");
		sql.append(" ) AS consumer_account, ");
		sql.append(" msc.price AS amount_of_subsidy,(msc.price/100) AS amount_of_subsidy_fmt, (mbr.credit_margin /100) AS credit_margin, ");
		sql.append(" mbr.* FROM  mall_borrowing_records AS mbr ");
		sql.append(" LEFT JOIN am_member AS ame ON ame.id = mbr.member_id ");
		sql.append(" LEFT JOIN mall_service_commodity AS msc ON  msc.id = mbr.sc_id ");
		sql.append(" WHERE 1=1  ");
		sql.append(" $SQL[[ and mbr.id = '$RS{mall_borrowing_records.form.id,am_bdp.mall_borrowing_records.form.id}']] ");
		return sql.toString();
	}

}
