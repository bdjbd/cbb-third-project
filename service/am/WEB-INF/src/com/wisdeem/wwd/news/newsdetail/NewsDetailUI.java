package com.wisdeem.wwd.news.newsdetail;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

public class NewsDetailUI implements UnitInterceptor {
	
	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		MapList data = unit.getData();
		// 没有价格的设为禁用
		for (int i = 0; i < data.size(); i++) {
			String dataStatus = data.getRow(i).get("datastatus");
			if ("2".equals(dataStatus)) {
				data.getRow(i).get("release", "已发布");
			}
		}
		return unit.write(ac);
	}

}
