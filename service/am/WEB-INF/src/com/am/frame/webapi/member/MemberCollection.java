package com.am.frame.webapi.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.p2p.service.IWebApiService;

/*
 * 会员收藏
 * */
public class MemberCollection implements IWebApiService 
{
	
	final Logger logger = LoggerFactory.getLogger(MemberCollection.class);
	
	@Override
	public String execute(HttpServletRequest request,HttpServletResponse response)
	{
		String memberid = request.getParameter("Member_ID");
		String type = request.getParameter("Type");
		String collectionid = request.getParameter("CollectionID");
		logger.info("memberid=" + memberid + ",type=" + type + ",collectionid=" + collectionid);
		//注册用户
		String collectionSQL = "INSERT INTO am_MemberCollection(id, Member_ID, Type, CollectionID, CreateDate) VALUES (?, ?, ?, ?, ?)";
		//检查是否存在相同收藏
		String checkSQL = "SELECT * FROM am_MemberCollection WHERE Member_ID = ? AND CollectionID = ?";
		String result = null;
		DB db=null;
		Connection connect=null;
		try 
		{
			db=DBFactory.newDB();
			connect=db.getConnection();
			PreparedStatement pareSta = connect
					.prepareStatement(checkSQL);
			pareSta.setString(1, memberid);
			pareSta.setString(2, collectionid);
			ResultSet rset = pareSta.executeQuery();
			if (rset.next()) 
			{
				result = "{\"CODE\" : \"1\",\"MSG\" : \"已存在该收藏\"}";
				pareSta.close();
			} 
			else 
			{
				pareSta.close();
				pareSta = connect.prepareStatement(collectionSQL,Statement.RETURN_GENERATED_KEYS);
				pareSta.setString(1, UUID.randomUUID().toString());
				pareSta.setString(2, memberid);
				pareSta.setString(3, type);
				pareSta.setString(4, collectionid);
				
				//SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Timestamp ts = Timestamp.valueOf(sf.format(new java.util.Date()));
				pareSta.setTimestamp(5, ts);
				
				pareSta.execute();
				result = "{\"CODE\" : \"0\",\"MSG\" : \"收藏成功\"}";
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			logger.error(e.getMessage());
			result = "{\"CODE\" : \"2\",\"MSG\" : \"发生异常，收藏失败\"}";
		} 
		finally 
		{
			try 
			{	
				if(connect!=null){
					connect.close();
				}
				if(db!=null){
					db.close();
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
		return result;
	}
	
}
