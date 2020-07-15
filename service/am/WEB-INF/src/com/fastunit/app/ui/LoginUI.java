package com.fastunit.app.ui;

import org.json.JSONArray;

import com.fastunit.MapList;
import com.fastunit.Page;
import com.fastunit.Row;
import com.fastunit.Var;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.PageInterceptor;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年7月25日
 * @version 
 * 说明:<br />
 * 理性农业 获取当前机构的名称
 */
public class LoginUI implements PageInterceptor {

	private static final long serialVersionUID = 1L;


	@Override
	public void intercept(ActionContext ac, Page page) throws Exception {
		String orgid=ac.getRequestParameter("orgid");
		
		String version=ac.getRequestParameter("version");
		
		//获取后台下载地址和版本号
		String downloadUrl=Var.get("lxny_operation_download_url");
		//系统现有版本
		String newVersion=Var.get("lxny_operation_version");
		
		String path = Var.get("LD_APP_ANDROID_PATH_org");
		page.getAttributes().put("LD_APP_ANDROID_PATH_org",path);
		
		if(!Checker.isEmpty(orgid)){
			String querySQL="SELECT * FROM aorg WHERE orgid=? ";
			DB db=DBFactory.getDB();
			
			MapList map=db.query(querySQL,orgid, Type.VARCHAR);
			
			if(!Checker.isEmpty(map)){
				//机构名称
				String orgName=map.getRow(0).get("orgname");
				page.getAttributes().put("current-org-name", orgName);
				page.getAttributes().put("current-suffix", "APP智能管理终端");
				page.getAttributes().put("versionquery", "&version="+version);
				page.getAttributes().put("new_version",newVersion);
				page.getAttributes().put("download_url", downloadUrl);
			}
			
		}else{
			orgid=ac.getVisitor().getUser().getOrgId();
			page.getAttributes().put("current-orgid", orgid);
		}
		
		page.getAttributes().put("app_version", Var.get("APP_VER_org"));
		
		page.write(ac);
	}

	public void test(){
		DB db=null;
		try{
			//企业简介
			String intsText="";
			//介绍ID
			String id=null;
			
			//成员设置
			String storeId_01="";
			//组织活动设置
			String storeId_02="";
			
			db=DBFactory.newDB();
			
			//获取当前机构的几个ID
			String orgid="";//request.getParameter("orderid");
			String querySQL="SELECT * FROM am_org_introduce WHERE org_id=? ";
			MapList map=db.query(querySQL, orgid, Type.VARCHAR);
			
			if(!Checker.isEmpty(map)){
				intsText=map.getRow(0).get("introduce_info");
				id=map.getRow(0).get("id");
				
				storeId_01=id+"_01";
				storeId_02=id+"_02";
			}
			
			//2,获取成员设置数据集合
			String queryUserSetSQL="SELECT * FROM mall_StoreImageList WHERE store_id=? ";
			MapList userSetMap=db.query(queryUserSetSQL,storeId_01,Type.VARCHAR);
			if(!Checker.isEmpty(userSetMap)){
				for(int i=0;i<userSetMap.size();i++){
					//id,classname,imagelist,store_id,cretetime
					//imagelist "[{"path":"/files/MALL_STOREIMAGELIST/a32b233b-c442-40e0-b94d-23010f11df6f/bdp_imagelist/0f13467c-c545-4fd2-ad2f-f08e5daeb4b4.png","name":"0f13467c-c545-4fd2-ad2f-f08e5daeb4b4.png"},{"path":"/files/MALL_STOREIMAGELIST/a32b233b-c442-40e0-b94d-23010f11df6f/bdp_imagelist/0f13467c-c545-4fd2-ad2f-f08e5daeb4b4_(1).png","name":"0f13467c-c545-4fd2-ad2f-f08e5daeb4b4_(1).png"},{"path":"/files/MALL_STOREIMAGELIST/a32b233b-c442-40e0-b94d-23010f11df6f/bdp_imagelist/50.png","name":"50.png"},{"path":"/files/MALL_STOREIMAGELIST/a32b233b-c442-40e0-b94d-23010f11df6f/bdp_imagelist/83d3393e-e0cc-451c-a657-48ec0a289273.png","name":"83d3393e-e0cc-451c-a657-48ec0a289273.png"}]"
					Row row=userSetMap.getRow(i);
					String imagelist=row.get("imagelist");
					//分类名
					String className=row.get("classname");
					
					if(!Checker.isEmpty(imagelist)){
						JSONArray imagelists=new JSONArray(imagelist);
						
						for(int j=0;j<imagelists.length();j++){
							//图片路径
							String path=imagelists.getJSONObject(i).getString("path");
						}
					}
					
				}
			}
			
			//3，获取组织活动设置数据集合
			MapList activitySetMap=db.query(queryUserSetSQL,storeId_02,Type.VARCHAR);
			if(!Checker.isEmpty(activitySetMap)){
				for(int i=0;i<activitySetMap.size();i++){
					Row row=userSetMap.getRow(i);
					String imagelist=row.get("imagelist");
					//分类名
					String className=row.get("classname");
					
					if(!Checker.isEmpty(imagelist)){
						JSONArray imagelists=new JSONArray(imagelist);
						
						for(int j=0;j<imagelists.length();j++){
							//图片路径
							String path=imagelists.getJSONObject(i).getString("path");
							
						}
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
