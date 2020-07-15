package com.fastunit.demo.ajax;

import com.fastunit.Ajax;
import com.fastunit.context.ActionContext;
import com.fastunit.support.Action;

public class TestAjaxAction implements Action {
	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		Ajax ajax = new Ajax(ac);

		int num1 = ac.getRequestParameterInt("ajax.num1", 0);
		int num2 = ac.getRequestParameterInt("ajax.num2", 0);
		int sum = num1 + num2;
		// 1、Action的“自定义参数”
		// 2、追加HTML（针对js触发方法doAjax()中的id）
		ajax.addHtml("<div>add by addHtml()</div>");
		// 3、设置表单值
		ajax.setValueByName(0, "ajax.sum", sum);
		// 4、设置HTML
		ajax.replace("text", " (=" + num1 + "+" + num2 + ")");
		// 5、设置单元
		ajax.replace("unit4", "demo", "ajax.unit4");
		// 6、设置属性
		ajax.setAttributeByName(0, "ajax.num2", "class", "RED");
		// ajax.setAttributeByName(0, "ajax.load1", "disabled", "true");
		// 7、设置隐藏
		ajax.setHiddenById("unit1", true);
		// 8、设置脚本
		ajax.addScript("alert('你好，" + ac.getVisitor().getUser().getName() + "');");

		ajax.send();
		return ac;
	}

}
