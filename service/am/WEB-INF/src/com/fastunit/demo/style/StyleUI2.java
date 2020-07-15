package com.fastunit.demo.style;

import com.fastunit.Element;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

public class StyleUI2 implements UnitInterceptor {

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		// 获取a元素的Wrapper
		Element a = unit.getElement("a");
		// 获取b元素及其Wrapper
		Element b = unit.getElement("b");
		// 此处将b元素的显示模式设为控件，来体现与a元素(文本方式)在控制字体颜色时的区别：
		// 控件方式：Element.setCss()，设置在<input>内；
		// 文本方式：ElementWrapper.setCss()，设置在<td>内。
		b.setShowMode(ElementShowMode.CONTROL);
		// 获取数据
		MapList data = unit.getData();

		// 获取指定的警戒值
		int aAlertValue = ac.getRequestParameterInt("style.ui2.avalue", 50);
		int bAlertValue = ac.getRequestParameterInt("style.ui2.bvalue", 50);
		int cdAlertValue = ac.getRequestParameterInt("style.ui2.cdvalue", 50);
		// css定义，来自'/domain/demo/demo.css'
		String aWrapperCss = "awrapper";
		String bCss = "belement";
		String bWrapperCss = "bwrapper";
		String cdRowCss = "cdrow";

		// 遍历数据，开始控制...
		for (int i = 0; i < data.size(); i++) {
			Row row = data.getRow(i);
			int aValue = row.getInt("a", 0);
			int bValue = row.getInt("b", 0);
			int cValue = row.getInt("c", 0);
			int dValue = row.getInt("d", 0);

			if (aValue > aAlertValue) {
				// 控制a列td的css
				a.setTdCss(i, aWrapperCss);
			}
			if (bValue < bAlertValue) {
				// 控制b列元素的颜色
				b.setCss(i, bCss);
				b.setTdCss(i, bWrapperCss);
			}
			if (cValue + dValue > cdAlertValue) {
				// 控制行背景色
				unit.setRowCss(i, cdRowCss);
			}
		}

		return unit.write(ac);
	}
}
