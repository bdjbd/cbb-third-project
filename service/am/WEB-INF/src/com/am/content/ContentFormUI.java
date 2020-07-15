package com.am.content;

import com.ambdp.content.ContentSqlProvider;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2015年1月29日
 * @version 
 * 说明:<br />
 * 内容Form表单UI 根据内容菜单配置表，进行视图控制
 */
public class ContentFormUI implements UnitInterceptor {
	
	private static final long serialVersionUID = 1L;
	
//'notice'
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		DB db=DBFactory.getDB();
		
		String menuid=ac.getRequestParameter("menucode");
		String m=ac.getRequestParameter("m");
		
		if(Checker.isEmpty(menuid)){
			menuid=(String)ac.getSessionAttribute(ContentSqlProvider.am_content_menucode);
		}
		
		//查询在当前视图下不显示的元素SQL
		String sql="SELECT field,show FROM contentMenu AS m "
				+ "	RIGHT JOIN contentAttribute AS atr "
				+ " ON m.id=atr.contentID "
				+ " WHERE m.key='"+menuid+"' "
				+ " AND atr.viewmodel LIKE '%"+m+"%' "
				+ " AND atr.show=1";
		
		//查询不需要显示的字段
		MapList map=db.query(sql);
		
		if(!Checker.isEmpty(map)){
			for(int i=0;i<map.size();i++){
				Row row=map.getRow(i);
				String eleName=row.get("field").toLowerCase();
				if(unit.getElement(eleName)!=null){
					unit.getElement(eleName).setShowMode(ElementShowMode.REMOVE);
				}
				
			}
		}
		
		return unit.write(ac);
	}

}
