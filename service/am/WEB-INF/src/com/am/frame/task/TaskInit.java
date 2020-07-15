package com.am.frame.task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月19日
 * @version 
 * 说明:<br />
 */
public class TaskInit {

	/**
	 * 根据机构ID，企业任务ID初始化会员任务
	 * @param enterTaskId
	 * @param orgid
	 */
	public static void initUserTask(String orgid,String enterTaskId){
		try {
			DB db=DBFactory.getDB();
			String findTaskByOrgidSQL="SELECT * FROM am_enterpriseTask AS et "
					+ " LEFT JOIN am_taskTemplate AS tt "
					+ " ON et.tasktemplateid=tt.id "
					+ " WHERE et.orgid='"+orgid+"'  "
					+ " AND et.id='"+enterTaskId+"'"
					+ " AND tt.templatestate='1'";
			
			MapList map=db.query(findTaskByOrgidSQL);
			//根据企业编号和企业任务编号查询初始化会员任务
			if(Checker.isEmpty(map))return ;
			
			IUserTask userTask=(IUserTask)Class.forName(map.getRow(0).get("classpath")).newInstance();
			
			String taskparame=map.getRow(0).get("taskparame");
			
			//为企业所有用户初始化次任务
			//查询企业用户
			String findOrgMember="SELECT * FROM am_Member WHERE orgcode='"+orgid+"' ";
			
			MapList memberMap=db.query(findOrgMember);
			
			if(Checker.isEmpty(memberMap))return;
			
			List<String[]> members=new ArrayList<String[]>();
			
			for(int i=0;i<memberMap.size();i++){
				members.add(new String[]{memberMap.getRow(i).get("id")});
			}
			
			//为会员任务表添加任务
			db.executeBatch("INSERT INTO am_usertask ("
					+ "id,enterpriseTaskid,taskrunstate,member_code,taskparame) VALUES ("
					+ "'"+UUID.randomUUID().toString()+"',"
					+ "'enterTaskId','1',?,'"+taskparame+"')", members,new int[]{Type.VARCHAR});
		
			
		} catch (JDBCException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
}
