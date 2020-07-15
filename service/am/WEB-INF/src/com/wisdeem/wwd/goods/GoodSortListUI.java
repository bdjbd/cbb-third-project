package com.wisdeem.wwd.goods;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

public class GoodSortListUI implements UnitInterceptor{
	private static final long serialVersionUID = -3349672306115769003L;
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		MapList lstMajorApp = unit.getData();
		if(lstMajorApp.size()>0){
			String Code = lstMajorApp.getRow(0).get("c_code");
			if(Code!=""&&Code.length()>1){
				int index=Code.lastIndexOf("-");
				if(index>=0){
					String codeId = Code.substring(0,index);
					String codeno=Code.substring(Code.lastIndexOf("-")+1,Code.length());
					ac.setRequestParameter("suf",codeId);
					ac.setRequestParameter("c_code_s",codeno);
				}
			}
		}
		
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
