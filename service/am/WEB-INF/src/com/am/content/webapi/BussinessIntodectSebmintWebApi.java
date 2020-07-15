package com.am.content.webapi;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.Type;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年6月7日
 * @version 
 * 说明:<br />
 * 同步后台业务介绍功能，如果没有，则新增一条数据
 */
public class BussinessIntodectSebmintWebApi implements IWebApiService {

	/****/
	public static final String BIND_MENUCODE="business_inducted";
	
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		if("false".contentEquals(Var.get("business_inducted_switch"))){
			return "";
		}
		
		//业务模块代码
		String code=request.getParameter("inductCode");
		//业务名称
		String name=request.getParameter("name");
		String orgCode=request.getParameter("orgcode");
		
		String querySQL="SELECT  * "
				+ " FROM am_Content "
				+ " WHERE am_mobliemenuid='business_inducted' AND business_code='"+code+"' "
						+ " AND orgcode='"+orgCode+"' ";
		
		DBManager db=new DBManager();
		
		MapList map=db.query(querySQL);
		
		if(Checker.isEmpty(map)){
			//如果业务代码不存在，新增一条
			String uuid=UUID.randomUUID().toString();
			String inserSQL="INSERT INTO am_content( id, am_mobliemenuid, title,business_code,OrgCode,createdate )"
					+ "  VALUES "
					+ "(?,?,?,?,?,now()) ";
			db.execute(inserSQL,new String[]{
					uuid,BIND_MENUCODE,name,code,orgCode
			},new int[]{
					Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,Type.VARCHAR
			});
		}
		
		
		return "";
	}

}
