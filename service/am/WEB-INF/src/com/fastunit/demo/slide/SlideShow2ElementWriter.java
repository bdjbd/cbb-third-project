package com.fastunit.demo.slide;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.ElementWriter;
import com.fastunit.view.element.ElementComponent;

public class SlideShow2ElementWriter implements ElementWriter {

	private static final Logger log = LoggerFactory
			.getLogger(SlideShow2ElementWriter.class);

	private static final String slideId = "ss2";
	private static final int width = 300;
	private static final int height = 200;

	@Override
	public void write(ActionContext ac, StringBuffer html, ElementComponent ec,
			Row row) {
		html.append("<div style=\"height:").append(height + 65).append("px\">");
		html.append("<div id=\"").append(slideId).append("\" class=\"slideshow\">");
		html.append("<img />");// src='images/1.jpg'
		html.append("</div>");
		html.append("<script type=\"text/javascript\">");
		html.append("window.addEvent('domready', function(){");
		html.append("var data = {");
		MapList list = getData();
		for (int i = 0; i < list.size(); i++) {
			Row dataRow = list.getRow(i);
			String picture = dataRow.get("picture");
			String title = dataRow.get("title");
			String link = dataRow.get("link");
			String thumbnail = dataRow.get("picture");
			html.append("'").append(picture).append("':{caption:'").append(title)
					.append("',thumbnail:'").append(thumbnail).append("',href:'").append(
							link).append("'}");
			if (i < list.size() - 1) {
				html.append(",");
			}
		}
		html.append("};");
		html.append("var myShow = new Slideshow('").append(slideId).append(
				"', data, {controller:false,captions: true,  thumbnails: true, width: ").append(width)
				.append(",height: ").append(height).append("});");
		html.append("});");
		html.append("</script>");
		html.append("</div>");
	}

	private MapList getData() {
		MapList data = new MapList();
		data.put(0, "picture", "/domain/demo/slideshow2/1.jpg");
		data.put(0, "title", "title 1");
		data.put(0, "link", "/demo/slideshow2.do");
		data.put(1, "picture", "/domain/demo/slideshow2/2.jpg");
		data.put(1, "title", "title 2");
		data.put(1, "link", "/demo/slideshow2.do");
		data.put(2, "picture", "/domain/demo/slideshow2/3.jpg");
		data.put(2, "title", "title 3");
		data.put(2, "link", "/demo/slideshow2.do");
		data.put(3, "picture", "/domain/demo/slideshow2/4.jpg");
		data.put(3, "title", "title 4");
		data.put(3, "link", "/demo/slideshow2.do");
		// try {
		// DB db = DBFactory.getDB();
		// data = db.query("");
		// } catch (Exception e) {
		// log.error("error", e);
		// }
		return data;
	}

}
