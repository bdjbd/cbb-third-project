package com.am.app_plugins_common.specRechange.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;


/***
 * 空间购买UI
 * @author yuebin
 *1，获取后台变量设置的每年的空间使用费用单价,单位元  space_use_annual_fee
 *
 */
public class SpecBuyRechangeUI implements UnitInterceptor {
	
	private static final long serialVersionUID = 1L;
	
	/**每年的空间使用费用单价,单位元**/
	public static final String space_use_annual_fee="space_use_annual_fee";
	
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		String orgId=ac.getRequestParameter("orgid");
		
		if(Checker.isEmpty(orgId)){
			orgId=ac.getSessionAttribute("ss.rechange_space.orgid", "");
		}
		
		String querySQL="SELECT msc.*, "+
						"  trim(to_char(COALESCE(msc.tearmin_seal_price/100.0,0),'99999999999990D99')) as tearmin_seal_pricef ,"+
						"  trim(to_char(COALESCE(msc.space_eveyear_use_free/100.0,0),'99999999999990D99')) as space_eveyear_use_freef"+
						" 	FROM mall_service_commodity AS msc "+
						" 	LEFT JOIN mall_service_comd_type AS sct ON msc.sc_type=sct.id "+
						" 	LEFT JOIN aorgtype AS at ON at.orgtype=sct.org_type "+
						" 	LEFT JOIN service_mall_info AS sminfo ON sminfo.tablename=sct.t_table_name AND sminfo.area_type=at.area_type"+
						" 	WHERE sminfo.loginaccount=? ";
		
		DB db=null;
		
		db=DBFactory.newDB();
		
		
		MapList orgMap=db.query(querySQL,orgId, Type.VARCHAR);
		
		if(!Checker.isEmpty(orgMap)){
			String space_eveyear_use_freef=orgMap.getRow(0).get("space_eveyear_use_freef");
			unit.getElement("price").setDefaultValue(space_eveyear_use_freef);
		}
		
		db.close();
		
		return unit.write(ac);
	}

}
