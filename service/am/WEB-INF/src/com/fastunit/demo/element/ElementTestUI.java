package com.fastunit.demo.element;

import java.util.HashMap;
import java.util.Map;

import com.fastunit.Element;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.constants.UnitShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 * 元素综合测试
 * 
 * @author jim
 */
public class ElementTestUI implements UnitInterceptor {

	private static final String UNITID = "ec.test";

	private static final String UNIT_TYPE = "unittype";
	private static final String UNIT_SHOWMODE = "unitshowmode";

	// private static final String attribute = "attribute";
	private static final String LINK = "attribute";

	// private static final String Wrapper_attribute = "attribute";
	// private static final String Wrapper_prefix = "attribute";

	private static Map elements = new HashMap();

	static {
		// 增加Flash默认属性
		Map attributes = new HashMap();
		attributes.put(LINK, "");
		elements.put("13", attributes);
	}

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		Unit container = getContainer(unit, ac);

		return unit.write(ac);
	}

	// 提取预设单元？？？以区别元素在表单、列表和查询下的不同表现
	private Unit getContainer(Unit unit, ActionContext ac) {
		String unitType = ac.getRequestParameter(UNITID + "." + UNIT_TYPE,
				UnitShowMode.EDIT);
		Element unitElement = unit.getElement(unitType);
		unitElement.setShowMode(ElementShowMode.CONTROL);
		Unit container = (Unit) unitElement.getObject();
		String showMode = ac.getRequestParameter(UNITID + "." + UNIT_SHOWMODE,
				UnitShowMode.EDIT);
		container.setShowMode(showMode);
		return container;
	}

	private String getAttribute(String elementType, ActionContext ac,
			String name, String defaultValue) {
		String value = ac.getRequestParameter(UNITID + "." + name);
		if (value == null) {
			Map attributes = (Map) elements.get(elementType);
			if (attributes != null) {
				value = (String) attributes.get(name);
			}
		}
		return value == null ? defaultValue : value;
	}

	private int getAttributeInt(String elementType, ActionContext ac,
			String name, int defaultValue) {
		int attribute = defaultValue;
		String attributeString = getAttribute(elementType, ac, name, null);
		if (attributeString != null && !"".equals(attributeString)) {
			try {
				attribute = Integer.parseInt(attributeString);
			} catch (Exception e) {
			}
		}
		return attribute;
	}
}
