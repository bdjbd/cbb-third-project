package com.cdms.org;

import java.util.List;

import com.fastunit.context.ActionContext;
import com.fastunit.support.TreeWriter;
import com.fastunit.util.Checker;
import com.fastunit.view.tree.Tree;
import com.fastunit.view.tree.TreeConfig;

public class StaticTree implements TreeWriter {
	private static final String BLANK = "&nbsp;&nbsp;";
	private TreeConfig config;
	private String checkboxName = "";

	public String write(ActionContext ac, Tree tree) {
		this.config = tree.getConfig();
		this.checkboxName = this.config.getDivAttribute();
		StringBuffer html = new StringBuffer(
				"<div id=\"_statictree\" class=\"tree\">");
		this.write(tree, html, 0, tree.getId());
		html.append("</div>");
		return html.toString();
	}

	private void write(Tree tree, StringBuffer html, int tab, String parentId) {
		String id = tree.getId();
		html.append("<div id=\"").append(id).append("\"");
		String title = tree.getTitle();
		if (!Checker.isEmpty(title)) {
			html.append(" title=\"").append(title).append("\"");
		}

		html.append(">");

		for (int attribute = 0; attribute < tab; ++attribute) {
			html.append("&nbsp;&nbsp;");
		}

		html.append("<img class=ic src=\"").append(tree.getIcon())
				.append("\">");
		String arg10;
		if (this.config.isSupportCheck()) {
			html.append("<input id=\"").append(parentId)
					.append("\" type=checkbox class=cc name=")
					.append(this.checkboxName).append(" value=\"")
					.append(tree.getCheckValue()).append("\"");
			arg10 = tree.getCheckAttribute();
			if (!Checker.isEmpty(arg10)) {
				html.append(" ").append(arg10);
			}

			html.append(" />");
		}

//		arg10 = tree.getAttribute();
//		if (!Checker.isEmpty(arg10)) {
//			html.append(arg10);
//		}

		html.append("<a unselectable=on>").append(tree.getName())
				.append("</a>");
		html.append("</div>");
		List leafs = tree.getLeafs();
		if (leafs != null) {
			int leafTab = tab + 1;

			for (int i = 0; i < leafs.size(); ++i) {
				this.write((Tree) leafs.get(i), html, leafTab, id);
			}
		}

	}
}
