package com.fastunit.demo.element;

import com.fastunit.Element;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.context.ActionContextHelper;
import com.fastunit.dev.BeanEnumerationHelper;
import com.fastunit.framework.config.BeanFactory;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.view.config.ElementType;
import com.fastunit.view.element.ElementComponent;
import com.fastunit.view.element.component.SuggestionElement;
import com.fastunit.view.enumeration.EnumerationFactory;
import com.fastunit.view.enumeration.EnumerationUtil;
import com.fastunit.view.tree.TreeProvider;
import com.fastunit.view.unit.UnitComponent;
import com.fastunit.view.unit.UnitFactory;
import com.fastunit.view.util.Html;

public class ElementShowModeUI implements UnitInterceptor {

	private final static String ELEMENT_ID = "abc";
	private final static String ELEMENT_NAME = "ABC";
	private final static String VALUE = "123";
	private final static String LINK = "http://www.fastunit.com";
	private final static String LINK_IMAGE = "/domain/demo/element/java.png";
	private final static String LINK_BOX = "/common/images/tool/save.png,/common/images/tool/save_d.png";
	private final static String LINK_FLASH = "/domain/demo/element/flash.swf";
	private final static String LINK_IFRAME = "/demo/ec.iframe.do";

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		ElementComponent ec = getElement(ac, unit);
		ec.setUnit((UnitComponent) unit);

		MapList showModes = EnumerationFactory.getEnumeration(ac, "dev",
				"elementshowmode");
		MapList data = new MapList();
		for (int i = 0; i < showModes.size(); i++) {
			Row row = showModes.getRow(i);
			String showMode = row.get("value");
			if ("9".equals(showMode)) {
				break;
			}
			ec.setShowMode(Integer.parseInt(showMode));
			data.put(i, "showmode", row.get("name"));// 名称
			StringBuffer code = new StringBuffer();
			ec.write(ac, code, -1, null);
			data.put(i, "display", code.toString());// 展现
			// 替换"<",">"
			data.put(i, "html", Html.replaceTags(code.toString()));// HTML代码
		}
		unit.setData(data);
		return unit.write(ac);
	}

	private ElementComponent getElement(ActionContext ac, Unit unit)
			throws Exception {
		int componentId = Integer.parseInt(ActionContextHelper.getRequest2Session(
				ac, "es.element", "es.element", Integer
						.toString(ElementType.TEXT_FIELD)));
		ElementComponent component = BeanFactory.getElementComponent(Integer
				.toString(componentId));

		// 设置元素基本属性
		component.setId(ELEMENT_ID);
		component.setName(ELEMENT_NAME);
		component.setDefaultValue(VALUE);

		// 设置元素专有属性
		switch (componentId) {
		case ElementType.ANCHOR:
			component.setLink(LINK);
			break;
		case ElementType.IMAGE:
			component.setLink(LINK_IMAGE);
			break;
		case ElementType.FLASH:
			component.setLink(LINK_FLASH);
			component.setWidth(110);
			component.setHeight(80);
			break;
		case ElementType.IFRAME:
			component.setLink(LINK_IFRAME);
			component.setCustom("width=230px height=60px");
			break;
		case ElementType.BUTTON:
			component.setCustom("onclick=alert()");
			break;
		case ElementType.BUTTON_BOX:
			component.setLink(LINK_BOX);
			component.setCustom("onclick=alert('ButtonBox')");
			break;
		case ElementType.SELECT:
		case ElementType.RADIO:
		case ElementType.MULTI_SELECT_FIELD:
		case ElementType.RADIO_FIELD:
		case ElementType.GLOBAL_RADIO_FIELD:
		case ElementType.CHECKBOX_SET:
			component.setDefaultValue("1");
			component.setObject(EnumerationFactory.getEnumeration(ac, "demo", "sex"));
			break;
		case ElementType.CHECKBOX_FIELD:
			component.setDefaultValue("1");
			break;
		case ElementType.SELECT_FIELD:
			component.setDefaultValue("101");
			component.setObject(EnumerationFactory.getEnumeration(ac, "demo",
					"selectfield"));
			break;
		case ElementType.SUGGESTION:
			component.setDefaultValue("");
			((SuggestionElement) component).setEnumerationId("suggestion");
			break;
		case ElementType.ORDER:
			component.setCustom("a,字段a;b,字段b;c,字段c;");
		case ElementType.UNIT:
			component.setObject(UnitFactory.getUnit(ac, "demo", "ec.unit", null));
			break;
		case ElementType.TREE:
			component.setObject(TreeProvider.getTree(ac, "demo", "statictree"));
			break;
		case ElementType.CHART:
			component.setObject("echart");
			break;
		case 90://ckeditor
			component.setCustom("Sample");
			component.setHeight(70);
			unit.getPage().addTopJsPath("/common/ckeditor/ckeditor.js");
			break;
		case ElementType.EDITOR:
			component.setCustom("Sample");
			component.setHeight(70);
			unit.getPage().addTopJsPath("/common/fckeditor/fckeditor.js");
			break;
		case ElementType.STAMP:
			component.setDefaultValue("");
			break;
		default:
			break;
		}

		// 设置下拉选项
		Element element = unit.getElement("element");
		element.setObject(EnumerationUtil.resolve(ac, getElementMapList()));
		element.setDefaultValue(Integer.toString(componentId));
		return component;
	}

	private MapList getElementMapList() throws Exception {
		MapList e = BeanEnumerationHelper.getEnumeration(BeanFactory.ELEMENT_NAME);
		e.removeRow("value", Integer.toString(ElementType.TREE));
		e.removeRow("value", Integer.toString(ElementType.CHART));
		e.removeRow("value", Integer.toString(ElementType.CUSTOM));
		return e;
	}

}
