package com.am.content;

import com.ambdp.content.ContentSqlProvider;
import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2015年1月29日
 * @version 
 * 说明:<br />menuid=a
 */
public class TitleUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		DB db=DBFactory.newDB();
		
		String menuid=ac.getRequestParameter("menucode");
		
		if(Checker.isEmpty(menuid)){
			menuid=(String)ac.getSessionAttribute(ContentSqlProvider.am_content_menucode);
		}
		
		MapList map=db.query("SELECT name,key FROM contentmenu  WHERE key='"+menuid+"'");
		
		if(!Checker.isEmpty(map)){
			unit.setTitle(map.getRow(0).get("name"));
		}else{
			Table table=new Table("am_bdp","CONTENTMENU");
			TableRow inserTR=table.addInsertRow();
			inserTR.setValue("key", menuid);
			db.save(table);
		}
		
		db.close();
		
		return unit.write(ac);
	}

}
