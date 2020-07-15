package com.am.app_plugins_common.collection;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

//收藏方法
public class CollectionManager implements IWebApiService 
{

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response)
	{
		
		String id = request.getParameter("id");
		String memberid = request.getParameter("member_id");
		String type = request.getParameter("type");
		
		DBManager db = new DBManager();
		
		String sql = "select * from am_membercollection where member_id ='"+memberid+"' and collectionid='"+id+"' ";
		
		String isql = "insert into am_membercollection (id,member_id,type,collectionid,createdate)"
				+ " values('"+UUID.randomUUID()+"','"+memberid+"','"+type+"','"+id+"','now()')";
		MapList clist = db.query(sql);
		
		if(!Checker.isEmpty(clist))
		{
			for (int i = 0; i < clist.size(); i++) 
			{
				sql = "delete from am_membercollection where id='"+clist.getRow(i).get("id")+"'";
				db.update(sql);
			}
			db.update(isql);
			
		}else
		{
			db.update(isql);
		}
		
		return null;
	}

}
