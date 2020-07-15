package com.wd.wd_culvert;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

public class Wd_routeInfor implements SqlProvider{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
       Row row = FastUnit.getQueryRow(ac, "wd_blj", "wd_route_culvert.query"); 
       String sql="SELECT * FROM WD_ROUTE where 1=1  ";
       String sql1="SELECT * FROM WD_ROUTE where 1=1  ";
       String  num="";
       String  name="";
       String  division="";
       if (row != null) {
    	   num=row.get("route_num1");
    	   name=row.get("route_name1");  
           division=row.get("division1");
		}
       if(row != null){
    	   if(!Checker.isEmpty(name)){
    		   sql1+=" and route_name like '%"+ name+"%'"; 		   
    	   }
    	   if(!Checker.isEmpty(num)){
    		   sql1+="  and route_num like'%"+ num+"%'";   
    	   }
    	   if(!Checker.isEmpty(division)){
    		   sql1+="  and division like'%"+ division+"%'";  
    	   }
    	   return sql1;
    	   
       }else{
    	   return sql;
       }
       
}
}