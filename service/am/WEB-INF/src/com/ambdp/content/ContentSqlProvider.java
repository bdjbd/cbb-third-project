package com.ambdp.content;

import org.slf4j.Logger;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月4日
 * @version 
 * 说明:<br />
 * 内容列表界面SqlProvider
 */
public class ContentSqlProvider implements SqlProvider {


	private static final long serialVersionUID = 1L;
	
	/**content menucode KEY**/
	public static final String am_content_menucode="content.ss.menucode";
	
	private Logger logger=org.slf4j.LoggerFactory.getLogger(getClass());
	
	@Override
	public String getSql(ActionContext ac) {
		
		String orgId="";
		String menuCode="";
		
		//menucode,am_content_menucode
		orgId=ac.getVisitor().getUser().getOrgId();
		menuCode=ac.getRequestParameter("menucode");
		
		boolean b= Checker.isEmpty(menuCode);
		if(Checker.isEmpty(menuCode))
		{
			menuCode=(String)ac.getSessionAttribute(am_content_menucode);
		}else{
			ac.setSessionAttribute(am_content_menucode, menuCode);
		}
		
		//获取查询条件
		Row query = FastUnit.getQueryRow(ac, "am_bdp", "am_content_query");
		
		StringBuilder sql= new StringBuilder();
		sql.append(" SELECT * FROM am_Content AS ac LEFT JOIN am_Member AS am ON ac.memberid=am.id   ");
    //  sql.append(" WHERE  ac.orgcode LIKE '%"+orgId+"%' AND ac.am_mobliemenuid = '"+menuCode+"' ");  //当前登录机构及下属机构的数据
		sql.append(" WHERE  ac.orgcode = '"+orgId+"' AND ac.am_mobliemenuid = '"+menuCode+"' ");  //仅限当前登录机构的数据
		
		if(query!=null){
			//标题：
			String title=query.get("titles");
			//发布日期开始
			String createDateStart=query.get("createdate_start");
			//发布日期结束
			String createDateEnd=query.get("createdate_end");
			
			if(!Checker.isEmpty(title)){
				sql.append(" AND ac.title LIKE '%"+title+"%'");
			}
			
			if(!Checker.isEmpty(createDateStart))
			{
				sql.append(" AND to_char(ac.createdate,'YYYY-MM-dd') >= '"+createDateStart+"'");
			}
			
			if(!Checker.isEmpty(createDateEnd)){
				sql.append(" AND to_char(ac.createdate,'YYYY-MM-dd') <= '"+createDateEnd+"'");
			}
		}
		
		sql.append(" ORDER BY COALESCE(ac.sort,-1),createdate DESC ");
		
		
		return sql.toString();
	}

}
