package com.fastunit.view.element.component;

import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.AbstractElementWriter;
import com.fastunit.util.Checker;
import com.fastunit.view.element.ElementComponent;
import com.fastunit.view.element.ElementUtil;

public class StampElementWriter extends AbstractElementWriter {
	private static final String DEFAULT_STAMP = "/files/stamp/blank.png";
	private static final String DEFAULT_STAMP2 = "/files/stamp/blank2.png";

	@Override
	protected void writeText(ActionContext ac, StringBuffer html,
			ElementComponent ec, Row row) {
		String value = ElementUtil.getValue(ac, ec, row);
		if (Checker.isEmpty(value)) {
			value = DEFAULT_STAMP2;
		}
		html.append("<img src=\"").append(value).append("\" />");
	}

	@Override
	protected void writeTextWithValue(ActionContext ac, StringBuffer html,
			ElementComponent ec, Row row) {
		writeText(ac, html, ec, row);
		ElementUtil.setHidden(ac, html, ec, row);
	}

	@Override
	protected void writeControl(ActionContext ac, StringBuffer html,
			ElementComponent ec, Row row) {
		String value = ElementUtil.getValue(ac, ec, row);
		if (Checker.isEmpty(value)) {
			value = DEFAULT_STAMP;
		}
		html.append("<img src=\"").append(value).append("\" onclick=\"$f92(this,'")
				.append(ElementUtil.getAlias(ec)).append("')\" />");
		ElementUtil.setHidden(ac, html, ec, row);
	}

	@Override
	protected void writeReadOnlyControl(ActionContext ac, StringBuffer html,
			ElementComponent ec, Row row) {
		writeControl(ac, html, ec, row);
	}

	@Override
	protected void writeDisabledControl(ActionContext ac, StringBuffer html,
			ElementComponent ec, Row row) {
		writeControl(ac, html, ec, row);
	}

	@Override
	protected void writeHidden(ActionContext ac, StringBuffer html,
			ElementComponent ec, Row row) {
		ElementUtil.setHidden(ac, html, ec, row);
	}

}
