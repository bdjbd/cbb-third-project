package com.fastunit.demo.style;

import com.fastunit.Element;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;
/**
 * 
 * @author 张少飞  2017/7/26  fastUnit练习 
 * 使用拦截器控制单元和元素属性：
 */
public class StyleUI1 implements UnitInterceptor {

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		// 获取控制单元
		Unit target = (Unit) unit.getElement("unit").getObject();

		// 设置标题
		String title = ac.getRequestParameter("style.ui1.title");
		if (!Checker.isEmpty(title)) {
			target.setTitle(title);
		}
		// 删除元素  下拉枚举，从1开始
		int removeIndex = ac.getRequestParameterInt("style.ui1.removeindex", -1);
		if (removeIndex > 0) {  
			target.removeElement(removeIndex - 1);  //数组下标，从0开始
		}
		// 设为文本  下拉枚举，从1开始
		int controlIndex = ac.getRequestParameterInt("style.ui1.controlindex", 1);
		Element element = target.getElement(controlIndex - 1);   //数组下标，从0开始
		if (element != null) {
			element.setShowMode(ElementShowMode.TEXT);  //TEXT为文本模式
		}

		// 将改动的各元素 进行提交并生效
		unit.getElement("title").setDefaultValue(title);
		unit.getElement("removeindex").setDefaultValue(
				Integer.toString(removeIndex));
		unit.getElement("controlindex").setDefaultValue(
				Integer.toString(controlIndex));
		return unit.write(ac);
	}

}
