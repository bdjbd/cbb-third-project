package com.am.frame.member.ui;

import com.am.frame.member.util.DataToImgUI;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 *@author 张少飞
 *@create 2017年6月3日
 *@version
 *说明：华池后台会员认证表单UI
 */
public class HuaChiMemberCheckedFormUI implements UnitInterceptor {

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {	
//		System.err.println(">>>>>>>>>>>>>");
		//获得单元数据 转换图片数据
		DataToImgUI.getIstance().intercept(ac, unit, "handheld_id_photo", "14%", "auto");
		return unit.write(ac);
		
	}
}
