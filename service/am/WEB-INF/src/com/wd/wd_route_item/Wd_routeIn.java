package com.wd.wd_route_item;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

public class Wd_routeIn implements SqlProvider{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
       Row row = FastUnit.getQueryRow(ac, "wd_blj", "wd_routeinfor.query"); 
       String sql="select * from WD_ROUTE order by modifytime desc";
       String sql1="select * from WD_ROUTE ";
       String  name="";
       String  num="";
//       String  code="";
       if (row != null) {
    	   name=row.get("route_name1");
           num=row.get("route_num1");
		}
       if(row != null){
			if(!Checker.isEmpty(name)){
				sql1 += " where route_name like '%"+name+"%' order by modifytime desc ";
			}
			if(!Checker.isEmpty(num)){
				sql1 += " where route_num like '%"+num+"%' order by modifytime desc ";
			}
			return sql1;
		}else{
			return sql;
		}
     }
}