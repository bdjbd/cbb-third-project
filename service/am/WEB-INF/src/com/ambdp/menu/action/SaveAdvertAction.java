package com.ambdp.menu.action;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;
import com.wisdeem.wwd.WeChat.Utils;

/**
 * 广告保存按钮
 * 保存上次图片的文件路径
 * @author gonghuabin
 * 
 */
public class SaveAdvertAction extends DefaultAction {
	final Logger logger = Logger.getLogger(SaveAdvertAction.class);
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// super.doAction(db, ac);
		Table table = ac.getTable("am_advert");
		db.save(table);
		String menucode = ac.getRequestParameter("am_advert_form.am_mobliemenuid");
		//内容组件Id
		String amContentid = ac.getRequestParameter("am_advert_form.am_contentid");
		//类型
		String contentUrl = ac.getRequestParameter("am_advert_form.content_url");
		System.out.println(menucode);
		
		String id = table.getRows().get(0).getValue("id");
		ac.setSessionAttribute("am_bdp.am_advert_form.id", id);

		Connection conn = db.getConnection();
		String updateSql = "  ";
//		fu_listimgage  listimgage 广告图片
		String fileName = Utils.getFastUnitFilePath("AM_ADVERT","fu_listimgage", id);
		if (fileName != null && fileName.length() > 1) {
			fileName = fileName.substring(0, fileName.length() - 1);
			updateSql = "UPDATE am_advert  SET listimgage ='" + fileName
					+ "'  WHERE id='" + id + "' ";
			
			conn.createStatement().execute(updateSql);
		}
		//判断类型是否为空  不等于空时 将广告类型url添加到广告表中
		if(!Checker.isEmpty(contentUrl)){
			String advertType = " SELECT * FROM am_advert_type WHERE id = '"+contentUrl+"' ";
			MapList advertTypeList = db.query(advertType);
			if (!Checker.isEmpty(advertTypeList)) {
				//路由URL
				String routeUrl = advertTypeList.getRow(0).get("route_url");
				
				String targetUrl=routeUrl.replaceAll("\\[ID\\]", amContentid);
				
				String updateAdvertSql = "UPDATE am_advert  SET url ='" +targetUrl+"'  WHERE id='" + id + "' ";
			
				db.execute(updateAdvertSql);
			}
		}
		conn.commit();
	}
}
