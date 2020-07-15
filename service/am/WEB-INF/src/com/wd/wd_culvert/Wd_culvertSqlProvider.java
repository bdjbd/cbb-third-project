package com.wd.wd_culvert;
import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;
/***
 * 涵洞信息列表
 * @author 霍凯丽
 *
 */
public class Wd_culvertSqlProvider implements SqlProvider{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {		
      Row row= FastUnit.getQueryRow(ac, "wd_blj", "wd_culvert.query");
       String name="";
       String type="";
       if(row!=null){
    	  name= row.get("route_name");
    	  type=row.get("culvert_type");
       }
       String sqlWhere  = "";
       if(row!=null){
           if(!Checker.isEmpty(name)){
        	   sqlWhere+=" and route.route_name  like'%"+name+"%'"; 
           }
          if(!Checker.isEmpty(type)){ 
        	  sqlWhere+=" and c.culvert_type ='"+type+"'"; 
           }
       }
       String sql = "";
       sql = sql + " select route.route_name,route.division,c.culvert_id,c.culvert_point,mx.fldvaluename as culvert_type," +
       		       " c.culvert_length,c.span,c.culvert_height,m.fldvaluename as import_type,x.fldvaluename as export_type," +
       		       " c.create_year,c.longitude,c.latitude,c.remark " +
       		       " from WD_CULVERT c " +
       		       " left join wd_route route " +
       		       " on c.route_id = route.route_id left join sdatadictionarymx mx on c.culvert_type = mx.dictionarymxid " +
       		       " left join sdatadictionarymx m on c.import_type = m.dictionarymxid " +
       		       " left join sdatadictionarymx x on c.export_type = x.dictionarymxid where 1=1 "+
          		   sqlWhere+
          		   "  order by  c.modifytime  desc";
       ac.setSessionAttribute("wd_blj.wd_culvert.query", sql);
       return sql; 
	}
}
