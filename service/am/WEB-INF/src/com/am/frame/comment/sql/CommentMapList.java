package com.am.frame.comment.sql;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.MapListFactory;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年5月11日
 *@version
 *说明：公共评论模块
 */
public class CommentMapList implements MapListFactory{

	private MapList mapList = new MapList();
	
	@Override
	public MapList getMapList(ActionContext ac) {
		
		String contentId = ac.getRequestParameter("contentid");
		DB db = null;
		
			try {
				db = DBFactory.newDB();
				//查询主评论
				String topCommentSql = " select mem.membername,to_char(dy.create_time,'yyyy-mm-dd HH24:MI:ss') AS time,dy.* "
						+ " FROM dynamiccomment AS dy "
						+ " LEFT JOIN am_member AS mem ON dy.praiseid=mem.id "
						+ " WHERE dy.contentid='"+contentId+"' AND (dy.tomemberid='' OR dy.tomemberid is null) "
						+ " ORDER BY dy.create_time DESC ";
				MapList topCommentList = db.query(topCommentSql);
				if(!Checker.isEmpty(topCommentList)){
					for(int i = 0; i < topCommentList.size(); i++){
						
						String id = topCommentList.getRow(i).get("id");
						String membername = topCommentList.getRow(i).get("membername");
						String commentcontent = topCommentList.getRow(i).get("commentcontent");
						MapList contList= new MapList();
						contList.put(i, "id", id);
						contList.put(i, "membername", membername);
						contList.put(i, "commentcontent",commentcontent);
						contList.put(i, "time",topCommentList.getRow(i).get("time"));
						mapList.add(contList);
						
						loadeTree(id,"&nbsp;&nbsp;",db);
					}
				}
				
			} catch (JDBCException e) {
				e.printStackTrace();
			}finally{
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
			
		for (int i = 0; i < mapList.size(); i++) {
			if(mapList.getRow(i).get(0)==null){
				mapList.removeRow(i);
				i--;
			}
		}
		

		return mapList;
	}
	
	public MapList loadeTree(String id,String prex,DB db){
		
		//查询子回复
		String sql = " SELECT amm.membername AS membername,to_char(com.create_time,'yyyy-mm-dd HH24:MI:ss') AS time,com.commentcontent,com.id, "
				+ " (SELECT membername FROM am_member WHERE id =(SELECT praiseid FROM dynamiccomment WHERE id =com.tomemberid )) AS tomembername"
				+ " FROM  dynamiccomment  AS com"
				+ " LEFT JOIN am_member AS amm ON com.praiseid=amm.id "
				+ " WHERE com.tomemberid= '"+id+"' ";
		
		MapList contList  = null;
		try {
			
			MapList mlist = db.query(sql);
			
				for (int i = 0; i < mlist.size(); i++) {
					contList= new MapList();
					String membername = prex+mlist.getRow(i).get("membername");
					contList.put(i, "id",mlist.getRow(i).get("id"));
					contList.put(i, "membername", membername);
					contList.put(i, "commentcontent",mlist.getRow(i).get("commentcontent"));
					contList.put(i, "tomembername", mlist.getRow(i).get("tomembername"));
					contList.put(i, "time",mlist.getRow(i).get("time"));
					mapList.add(contList);
					
					
					loadeTree(mlist.getRow(i).get("id"),prex+"&nbsp;&nbsp;&nbsp;&nbsp;",db);
				}
				
			
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
