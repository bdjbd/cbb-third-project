package com.wisdeem.wwd.msg;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

public class FsendmessSqlProvider implements SqlProvider{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
	    Row query = FastUnit.getQueryRow(ac, "wwd", "ws_fsendmess.query");
	    String orgid = ac.getVisitor().getUser().getOrgId();
	    String sql = "select a.fsend_id,a.public_id as publicid,a.msg_type,a.send_time,b.public_id,a.msg_status  from WS_FSENDMESS a left join WS_PUBLIC_ACCOUNTS b on " +
	    		"a.public_id=b.public_id where b.orgid = '"+orgid+"'";
	    String public_id = "";
	    String start_time="";
	    String end_time="";
	    if(query!=null){
	    	public_id = query.get("publicid");
	    	start_time = query.get("send_time");
	    	end_time = query.get("send_time.t");
	    }
	    if(!Checker.isEmpty(public_id)){
	    	sql+=" and a.public_id = '"+public_id+"' ";
	    }
	    if(!Checker.isEmpty(start_time)){
	    	sql+=" and to_char(a.send_time,'YYYY-MM-dd') >='"+start_time+"' ";
	    }
	    if(!Checker.isEmpty(end_time)){
	    	sql+=" and to_char(a.send_time,'YYYY-MM-dd') <= '"+end_time+"'";
	    }
	    sql+=" order by a.send_time desc";
		return sql;
	}

}
