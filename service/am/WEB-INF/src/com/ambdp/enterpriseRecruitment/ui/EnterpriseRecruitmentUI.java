package com.ambdp.enterpriseRecruitment.ui;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/** 
 * @author  作者：yangdong
 * @date 创建时间：2016年10月25日 下午9:40:06
 * @explain 说明 : 
 */
public class EnterpriseRecruitmentUI implements UnitInterceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3877699636362253187L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception 
	{
		String id=ac.getRequestParameter("lxny_myapplication.form.id");
		
		DBManager db = new DBManager();
		String SQL = "select * from lxny_myapplication where id ='"+id+"'";
		MapList list = db.query(SQL);
		
		if(!Checker.isEmpty(list))
		{
			if(!"0".equals(list.getRow(0).get("status")))
			{
				unit.getElement("adopt").setShowMode(ElementShowMode.REMOVE);
				unit.getElement("reject").setShowMode(ElementShowMode.REMOVE);
			}
		}
		
		return unit.write(ac);
	}

}
