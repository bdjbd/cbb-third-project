package com.ambdp.ui;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 * 修改物业管理-一键报警保修页面标题UI
 */
public class PropertytelTitleUI implements UnitInterceptor {

	private static final String PropertytelTitleUI_SESSION_TITLE="PropertytelTitleUI_SESSION_TITLE";
	
	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		//先从Session获取标题
		String title =ac.getRequestParameter("title");

		if(Checker.isEmpty(title)||"null".equals(title)){
			title=(String)ac.getSessionAttribute(PropertytelTitleUI_SESSION_TITLE);
		}
		if(!Checker.isEmpty(title)&&!"null".equals(title)){
			ac.setSessionAttribute(PropertytelTitleUI_SESSION_TITLE,title);
		}
		
		String menuCode=ac.getRequestParameter("menucode");
		
		if(!Checker.isEmpty(menuCode)){
			ac.setSessionAttribute("menucode",menuCode);
		}
		
		if(Checker.isEmpty(menuCode)){
			menuCode=ac.getRequestParameter("am_mobliemenuid");
		}
		
//		title = new String(title.getBytes("iso-8859-1"),"UTF-8");
		DB db=DBFactory.getDB();
		MapList map=db.query("SELECT * FROM adv_title_ui_set WHERE menuId=? ",menuCode,Type.VARCHAR);
		
		if(!Checker.isEmpty(map)){
			Row row=map.getRow(0);
			if(!Checker.isEmpty(row.get("title"))&&!"null".equals(row.get("title"))){
				title=row.get("title");
			}
			
		}else{
			Table table=new Table("am_bdp","adv_title_ui_set");
			TableRow tr=table.addInsertRow();
			tr.setValue("menuid", menuCode);
			db.save(table);
		}
		
		unit.setTitle(title);
		
		return unit.write(ac);
	}

}
