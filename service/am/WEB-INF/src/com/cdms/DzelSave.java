package com.cdms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.p2p.service.IWebApiService;

public class DzelSave implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		String fencetype="";
		String electronic_fence_name=request.getParameter("electronic_fence_name");
		String orgname=request.getParameter("orgname");
		 fencetype=request.getParameter("fencetype");
		String id=request.getParameter("id");
		String type=request.getParameter("type");
		String electronic_fence_range=request.getParameter("electronic_fence_range");
		String orgcode="";
		if(fencetype!=""){
		if(fencetype.equals("监控进围栏")){
			fencetype="1";
		}else if(fencetype.equals("监控出围栏")){
			fencetype="2";
		}
		}
		DBManager db=new DBManager();
		String osql="select orgid from aorg where orgname='"+orgname+"'";			
		MapList mapList =db.query(osql);
		if(!type.equals("3")){
			orgcode=mapList.getRow(0).get("orgid");
		}
		
	   String isql="insert into cdms_electronicfence (id,electronic_fence_name,orgcode,electronic_fence_type) "
	   		+ "VALUES('"+id+"','"+electronic_fence_name+"','"+orgcode+"','"+fencetype+"')";
	   String usql="update cdms_electronicfence set electronic_fence_name='"+electronic_fence_name+"',orgcode='"+orgcode+"' "
	   		+ ",electronic_fence_type='"+fencetype+"' where id='"+id+"'";
	   System.out.println("围栏范围="+electronic_fence_range);
	   String usql1="update cdms_electronicfence set electronic_fence_range='"+electronic_fence_range+"' "
		   		+ "  where id='"+id+"'";
	   if(type.equals("1")){
		   db.execute(isql);
		   db.execute(usql1);
	   }else if(type.equals("2")){
		   db.execute(usql);
	   }else{
		   db.execute(usql1);
	   }
	   
	   
		return"成功";
	}

}
