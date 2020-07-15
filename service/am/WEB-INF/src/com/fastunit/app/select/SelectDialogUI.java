package com.fastunit.app.select;

import com.fastunit.FastUnit;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.Unit;
import com.fastunit.constants.ListSelectMode;
import com.fastunit.context.ActionContext;
import com.fastunit.context.ActionContextHelper;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;
import com.fastunit.view.unit.component.ListUnit;

/*
 * 设置选择事件
 */
public class SelectDialogUI implements UnitInterceptor {

	private static final String KEY_CHECKBOX = "com.com.fastunit.app.select.SelectDialogUI.checkbox";

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		ListUnit listUnit = (ListUnit) unit.getElement("list").getObject();
		// 获得单选还是多选
		boolean isCheckBox;
		Row row = FastUnit.getQueryRow(ac, "app", unit.getId());
		if (row != null) {
			// 表单提交后，依据查询单元数据
			isCheckBox = "1".equals(row.get("checkbox"));
		} else {
			// 刚进入页面时，依据外部参数
			isCheckBox = "1".equals(ActionContextHelper.getRequest2Session(ac,
					"checkbox", KEY_CHECKBOX, null));
		}
		unit.getElement("checkbox").setDefaultValue(isCheckBox ? "1" : "0");
		// 列表选择模式
		listUnit.setListSelectMode(isCheckBox ? ListSelectMode.CHECKBOX
				: ListSelectMode.RADIO);

		// 选择事件
		MapList data = listUnit.getData();
		if (!Checker.isEmpty(data)) {
			String event = isCheckBox ? "onclick=\"setCheckBox(this)\""
					: "onclick=\"setRadio(this)\"";
			for (int i = 0; i < data.size(); i++) {
				listUnit.setListSelectAttribute(i, event);
			}
		}
		// 页面onload事件
		unit.getPage().setBodyAttribute(
				isCheckBox ? "onload=\"initCheckBox()\"" : "onload=\"initRadio()\"");

		return unit.write(ac);
	}
}
