package com.am.complaintmanager.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/** 
 * @author  作者：yangdong
 * @date 创建时间：2016年4月28日 上午10:24:02
 * @version 投诉管理表单ui
 */
public class ComplaintRefundFormUI implements UnitInterceptor {
	
	private static final long serialVersionUID = 1L;
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		String id = ac.getRequestParameter("mall_complaint_refund.form.id");
		DB db = null;
		String state = null;
		MapList maplist = null;
		try{
			db = DBFactory.newDB();
			String sql = " SELECT state FROM mall_refund WHERE id = '"+id+"' ";
			maplist = db.query(sql);
			if(!Checker.isEmpty(maplist)){
				state = maplist.getRow(0).get(0);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			db.close();
		}
		
		if(!Checker.isEmpty(state)){
			if(state.equals("2")){
				unit.getElement("realrefundmenoy").setShowMode(ElementShowMode.CONTROL_DISABLED);
				unit.getElement("refundexplain").setShowMode(ElementShowMode.CONTROL_DISABLED);
			}
		}
		
		
		return unit.write(ac);
	}
	
}
