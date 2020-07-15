package com.am.food_safety.sqlProvider;




import com.fastunit.FastUnit;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TotalFoodSafetySqlProvider implements SqlProvider {

	@Override
	public String getSql(ActionContext ac) {
		
		String sql="select trim(to_char(COALESCE(mtd.counter_fee/100.0,0),'99999999999990D99')) as counter_fee ,"
				+ " trim(to_char(COALESCE(mtd.trade_total_money/100.0,0),'99999999999990D99')) as trade_total_money,"
				+ " to_char(mtd.trade_time, 'yyyy-MM-DD') as trade_time,mtd.trade_type,mtd.rmarks  from MALL_TRADE_DETAIL as mtd "+
				"   left join mall_system_account_class as msac on msac.id = mtd.sa_class_id "+
				"   where mtd.member_id = '$U{orgid}' "+
				"   and msac.sa_code='GROUP_FOOD_SAFETY_TRACING_ACCOUNT' ";
				
		
		//获取查询单元
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp","mall_account_total_saftyfood_info.query");
		String trade_time_start = null;
		String trade_time_end = null;
		String trade_typeq = null;
		
		if(queryRow!=null){
		 trade_time_start = queryRow.get("trade_time_start");
		 trade_time_end = queryRow.get("trade_time_end");
		 trade_typeq = queryRow.get("trade_typeq");
		}
		else{
			String querySLQ="SELECT to_char(now()- interval '1 mon','yyyy-MM-dd') AS mon";
			
			DB db=null;
			try{
				db=DBFactory.newDB();
				
				MapList map=db.query(querySLQ);
				
				if(!Checker.isEmpty(map)){
					
					trade_time_start =map.getRow(0).get("mon");	
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally {
				if(db!=null){
					try {
						db.close();
					} catch (JDBCException e) {
						e.printStackTrace();
					}
				}
			}
			Date date=new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			trade_time_end = dateFormat.format(date);
			
		}
		
		
		if(!Checker.isEmpty(trade_time_start)&&!Checker.isEmpty(trade_time_end)){
			sql+=" AND to_date(to_char(mtd.trade_time, 'yyyy-MM-DD'),'yyyy-MM-DD') >= to_date('"+trade_time_start+"','yyyy-MM-dd')"
					+ " AND to_date(to_char(mtd.trade_time, 'yyyy-MM-DD'),'yyyy-MM-DD') <= to_date('"+trade_time_end+"','yyyy-MM-dd') ";
		}
		
		if(!Checker.isEmpty(trade_typeq)){
			sql+=" AND  mtd.trade_type= "+trade_typeq;
		}
		
		sql+="   ORDER BY mtd.create_time";
		return sql;
	}

}
