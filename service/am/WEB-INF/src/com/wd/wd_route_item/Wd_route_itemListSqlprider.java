package com.wd.wd_route_item;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;
/***
 * 路段列表查询
 * @author 霍凯丽
 *
 */
public class Wd_route_itemListSqlprider implements SqlProvider{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
       Row row = FastUnit.getQueryRow(ac, "wd_blj", "wd_route_item.query"); 
       String  name="";
       String  num="";
       String  code="";
       if (row != null) {
    	   name=row.get("route_name");
           num=row.get("route_num");
           code=row.get("item_code");	
		}
       String sqlWhere = "";
       if(!Checker.isEmpty(code)){
     		sqlWhere+="  and  b.item_code like '%"+code+"%'";
  	   }
  	   if(!Checker.isEmpty(num)){
  			sqlWhere+="  and a.route_num like '%"+num+"%'";
       }
  	   if(!Checker.isEmpty(name)){
  			sqlWhere+="  and a.route_name  like '%"+name+"%'";
  	   }
       String sql=" SELECT  a.route_id, a.route_name,a.route_num ,b.sortnum ,b.begin_point,b.begin_latitude,b.end_point,b.begin_longitude,b.thickness_djc,b.remark,b.item_id,b.item_code" +
       		      " from  wd_route_item  b  " +
       		      " LEFT JOIN wd_route  a" +
          		  " ON b.route_id = a.route_id where 1=1 "+
    		      sqlWhere +
    		      " order by a.route_name asc,b.item_code asc,b.modifytime  desc ";
       ac.setSessionAttribute("wd_blj.wd_route_item.query", sql);
       return sql;  
	}

}
