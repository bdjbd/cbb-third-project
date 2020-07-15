package com.ambdp.repair;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月5日
 * @version 
 * 说明:<br />
 * 报修模块SQLProvider
 */
public class RepairSqlProvider implements SqlProvider {

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		
		String where="";
		
		//获取查询条件
		Row query = FastUnit.getQueryRow(ac, "am_bdp", "am_content_repair_query");
		
		if(query!=null){
			//标题：
			String title=query.get("actitle");
			//发布日期开始
			String createDateStart=query.get("createdate_start");
			//发布日期结束
			String createDateEnd=query.get("createdate_end");
			//状态 datastate
			String dataState=query.get("adatastate");
			
			if(!Checker.isEmpty(title)){
				
				where+=" AND ac.title LIKE '%"+title+"%'";
			}
			if(!Checker.isEmpty(createDateStart)&&!Checker.isEmpty(createDateEnd)){
				where+=" AND ac.createdate between "+createDateStart+" AND "+createDateEnd;
			}
			if(!Checker.isEmpty(dataState)){
				where+=" AND ac.datastate='"+dataState+"'";
			}
			
		}
		
		String org=ac.getVisitor().getUser().getOrgId();
		
		String sql=
		"SELECT phone,membername,email,memberaddress,ac.*,               "+
		"	to_char(ac.createdate,'yyyy-mm-dd HH24:MI:ss') AS createDate_s "+ 
		"	FROM am_content AS ac LEFT JOIN am_member AS am                "+
		"	ON am.id=ac.memberid                                           "+
		"	WHERE ac.am_mobliemenuid='repair'                              "+
		"	AND ac.orgcode LIKE '%"+org+"%' "+
		where+" ORDER BY createDate_s DESC ";
		
		return sql;
	}

}
