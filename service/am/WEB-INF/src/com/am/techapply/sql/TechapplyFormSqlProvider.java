package com.am.techapply.sql;

import com.am.frame.systemAccount.SystemAccountClass;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;

/** * @author  作者：yangdong
 * @date 创建时间：2016年5月3日 下午6:44:27
 * @version 
 */
public class TechapplyFormSqlProvider  implements SqlProvider{
	
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
	@Override
	public String getSql(ActionContext ac) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT mta.id,mta.membername AS member_name,mta.is_auth,mta.membersex AS member_sex,");
		sql.append(" mta.phone AS member_phone,mta.email AS member_email, ");
		sql.append(" mta.memberaddress AS member_address,mta.wxnickname, ");
		sql.append(" mta.identitycardnumber, ");
		sql.append(" (SELECT up_member.membername AS upmembername ");
		sql.append(" 	FROM( ");
		sql.append(" 		SELECT CASE WHEN LENGTH (upid) > 0 THEN SUBSTRING (upid, 0, LENGTH(upid)) ELSE '' ");
		sql.append(" 		END AS mupid ,* FROM am_member ");
		sql.append(" 	)d1 ");
		sql.append(" LEFT JOIN am_Member AS up_member ON d1.mupid = up_member. ID ");
		sql.append(" WHERE d1.id = mta.id) AS member_upname, ");
		sql.append(" (SELECT balance/100 FROM mall_account_info WHERE member_orgid_id = mta.id AND a_class_id in ");
		sql.append(" 	(SELECT id FROM mall_system_account_class WHERE sa_code = '"+FOOD_SAFETY_TRACING_ACCOUNT+"')  ");
		sql.append(" ) AS food_safety_tracing_account, ");
		sql.append(" (SELECT balance/100 FROM mall_account_info WHERE member_orgid_id = mta.id AND a_class_id in ");
		sql.append(" 	(SELECT id FROM mall_system_account_class WHERE sa_code = '"+IDENTITY_STOCK_ACCOUNT+"')  ");
		sql.append(" ) AS identity_stock_account, ");
		sql.append(" (SELECT balance/100 FROM mall_account_info WHERE member_orgid_id = mta.id AND a_class_id in ");
		sql.append(" 	(SELECT id FROM mall_system_account_class WHERE sa_code = '"+BONUS_ACCOUNT+"')  ");
		sql.append(" ) AS bonus_account, ");
		sql.append(" (SELECT balance/100 FROM mall_account_info WHERE member_orgid_id = mta.id AND a_class_id in ");
		sql.append(" 	(SELECT id FROM mall_system_account_class WHERE sa_code = '"+CREDIT_MARGIN_ACCOUNT+"')  ");
		sql.append(" ) AS credit_margin_account, ");
		sql.append(" (SELECT balance/100 FROM mall_account_info WHERE member_orgid_id = mta.id AND a_class_id in ");
		sql.append(" 	(SELECT id FROM mall_system_account_class WHERE sa_code = '"+CONSUMER_ACCOUNT+"')  ");
		sql.append(" ) AS consumer_account,mta.remark ");
		sql.append(" FROM am_member AS mta ");
		sql.append(" LEFT JOIN am_member_identity AS ame ON ame.id = mta.member_identity ");
		sql.append(" WHERE 1=1 AND UPPER(ame.id_code) = 'AM_TECHNOLOGIST'  ");
		sql.append(" $SQL[[ and mta.id = '$RS{mall_tech_apply.form.id,am_bdp.mall_tech_apply.form.id}']] ");
		return sql.toString();
	}

}
