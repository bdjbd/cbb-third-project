package com.p2p.recharge;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

public class P2pRechargeVerifyListSqlProvider implements SqlProvider {

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		String member_codename="";
		String member_phone="";
		String rechargedatetiemT="";
		String rechargedatetiem="";
		String verifystatus="";
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT m.mem_name,m.phone,rm.RMID,rm.member_code,rm.rechargeMoney,");
		sql.append(" m.cash,");
		sql.append( " rm.creater,rm.verifyer,rm.verifyStatus, ");
		sql.append(" to_char(rm.rechargeDateTiem,'yyyy-mm-dd hh24:mi:ss') AS rtm, ");
		sql.append(" m.mem_name AS member_name    ");
		sql.append(" FROM p2p_rechargemanage AS rm    ");
		sql.append(" LEFT JOIN ws_member AS m ON rm.member_code=m.member_code     ");
		sql.append(" where 1=1 AND  (verifystatus =1 OR verifystatus=2)");
		
		Row query = FastUnit.getQueryRow(ac, "wwd", "p2p_recharge_verify.query");
		if(query!=null){
			member_codename=query.get("member_codename");
			member_phone=query.get("member_phone");
			verifystatus=query.get("verifystatus");
			rechargedatetiemT=query.get("rechargedatetiem.t");
			rechargedatetiem=query.get("rechargedatetiem");
		}
		if(!Checker.isEmpty(member_codename)){
			sql.append(" AND m.mem_name like '%"+member_codename+"%'");
		}
		if(!Checker.isEmpty(member_phone)){
			sql.append(" AND m.phone like '%"+member_phone+"%'");
		}
		if(!Checker.isEmpty(verifystatus)){
			sql.append(" AND rm.verifystatus=").append(verifystatus);
		}
		if(!Checker.isEmpty(rechargedatetiem)){
			sql.append(" AND rechargeDateTiem>to_date('"+rechargedatetiem+" 00:00:00','yyyy-mm-dd  HH24:mi:ss') ");
		}
		if(!Checker.isEmpty(rechargedatetiemT)){
			sql.append(" AND rechargeDateTiem<to_date('"+rechargedatetiemT+" 24:59:59','yyyy-mm-dd  HH24:mi:ss') ");
		}
		sql.append(" ORDER BY rechargeDateTiem DESC ");
		
		return sql.toString();
	}

}
