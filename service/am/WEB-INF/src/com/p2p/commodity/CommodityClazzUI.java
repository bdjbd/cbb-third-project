package com.p2p.commodity;

import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 * 商品分类UI
 * @author Administrator
 *
 */
public class CommodityClazzUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		String comdytype=ac.getRequestParameter("comdytype");
		if(comdytype==null){
			comdytype=(String)ac.getSessionAttribute("comdytype");
		}
		if(comdytype!=null&&"1".equals(comdytype)){
			//商品分类
			unit.setTitle("商品分类");
		}else if(comdytype!=null&&"2".equals(comdytype)){
			//项目分类
			unit.setTitle("项目分类");
		}else if(comdytype!=null&&"3".equals(comdytype)){
			//服务项目分类
			unit.setTitle("服务项目分类");
		}
		return unit.write(ac);
	}

}
