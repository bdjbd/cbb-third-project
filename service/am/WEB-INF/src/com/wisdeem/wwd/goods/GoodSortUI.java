package com.wisdeem.wwd.goods;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.UnitInterceptor;

public class GoodSortUI implements UnitInterceptor {
	private static final long serialVersionUID = -3349672306115769003L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		String parent_id = ac.getRequestParameter("parent_id");
		DB db=DBFactory.getDB();
		if (parent_id != "") {
			String sql = "select c_code from ws_commodity where comdy_class_id="
					+ parent_id + "";
			MapList list = db.query(sql);
			if (list.size() > 0) {
				String c_code = list.getRow(0).get("c_code");
				ac.setRequestParameter("c_code",c_code);
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
