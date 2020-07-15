package com.fastunit.demo.element;

import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.ElementWriter;
import com.fastunit.view.element.ElementComponent;

public class HelloElementWriter implements ElementWriter {

	@Override
	public void write(ActionContext ac, StringBuffer html, ElementComponent ec,
			Row row) {
		String userName = ac.getVisitor().getUser().get("username");
		html.append("hello ").append(userName).append("!");
	}

}
