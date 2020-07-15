package com.fastunit.demo.list;

import java.util.List;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;
/**
 * 
 * @author 张少飞  2017/7/26    fastUnit练习
 * 选择框控制（没有价格的设为失效；设置上次选中的数据）
 */
public class ListSelectUI implements UnitInterceptor {

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		// 获得列表单元的所有行 
		MapList listData = unit.getData();
		// 没有价格的行 设为禁用
		for (int i = 0; i < listData.size(); i++) {
			String price = listData.getRow(i).get("price");
			//无值验证：包括null 和 ""
			if (Checker.isEmpty(price)) {
				//使目标行无法被选中和操作
				unit.setListSelectAttribute(i, "disabled");
			}
		}
		// 设置上次action触发保存的选择数据（session中记载有登陆者的历史选择，在刷新页面或浏览其他页面后，回到当前页面时，仍将其默认选中）
		List<String> selectedBookId = (List) ac
				.getSessionAttribute(ListSelectAction.KEY);
		// ListSelectAction.KEY = "com.fastunit.demo.list.select";
		// selectedBookId = [003, 005];
		
		if (!Checker.isEmpty(selectedBookId)) {
			for (int i = 0; i < listData.size(); i++) {
				String bookId = listData.getRow(i).get("bookid");
				if (selectedBookId.contains(bookId)) {
					listData.put(i, "_s_list.select", "1");// 设为选中
				}
			}
		}
		// 清除Session（清除历史记录，列表不再有默认选中项）
		ac.removeSessionAttribute(ListSelectAction.KEY);
		
		return unit.write(ac);
	}

}
