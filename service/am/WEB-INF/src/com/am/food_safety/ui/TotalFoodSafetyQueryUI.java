package com.am.food_safety.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;


/**
 * 食品安全追溯账号UI
 * @author yuebin
 *
 */
public class TotalFoodSafetyQueryUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		String querySLQ="SELECT to_char(now()- interval '1 mon','yyyy-MM-dd') AS mon";
		
		DB db=null;
		
		String startDate="";
		try{
			db=DBFactory.newDB();
			
			MapList map=db.query(querySLQ);
			
			if(!Checker.isEmpty(map)){
				startDate=map.getRow(0).get("mon");
				
				unit.getElement("trade_time_start").setDefaultValue(startDate);
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if(db!=null){
				db.close();
			}
		}
		
		
		return unit.write(ac);
	}

}
