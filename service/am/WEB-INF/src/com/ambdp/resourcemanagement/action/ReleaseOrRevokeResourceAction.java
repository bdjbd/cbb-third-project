package com.ambdp.resourcemanagement.action;

import java.io.File;

import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/** 
 * @author  作者：yangdong
 * @date 创建时间：2016年5月5日 下午4:45:20
 * @version 发布或撤销资源
 */
public class ReleaseOrRevokeResourceAction extends DefaultAction {

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		String id=ac.getRequestParameter("id");
		
		DB db=DBFactory.getDB();
		String checkSQL="SELECT * FROM mjyc_resourcemanagement  WHERE id= ? ";
		MapList map=db.query(checkSQL,id,Type.VARCHAR);
		
		if(!Checker.isEmpty(map)){
			String name = map.getRow(0).get("name");
			String dataStatus=map.getRow(0).get("status");
			String serverRoute = "";
			String serverroute = map.getRow(0).get("serverroute");
			
			if(!Checker.isEmpty(map.getRow(0).get("serverroute"))){
				String[] url = map.getRow(0).get("serverroute").split("bdpserverroute");
				if(url.length == 2){
					serverRoute = map.getRow(0).get("serverroute").split("bdpserverroute")[1].toString();
				}else{
					serverRoute = File.separator+serverroute;
				}	
			}
			String client_route = "";
			String updateSQL="";
			if("0".equals(dataStatus)){//发布
				client_route = Var.get("resource_client_route") +"files"+ serverRoute;
				updateSQL="UPDATE mjyc_resourcemanagement SET status='1',clientroute=? WHERE id= ? ";
			}
			
			if("1".equalsIgnoreCase(dataStatus)){//1 撤销
				updateSQL="UPDATE mjyc_resourcemanagement SET status='0',clientroute=? WHERE id=? ";
			}
			
			if(!Checker.isEmpty(updateSQL)){
				db.execute(updateSQL,new String[] {client_route,id},new int[] {Type.VARCHAR, Type.VARCHAR});
			}
		}
		
		ac.getActionResult().setUrl("/am_bdp/mjyc_resourcemanagement.do?m=s&clear=am_bdp.mjyc_resourcemanagement.query");
		ac.getActionResult().setSuccessful(true);
		return ac;
	}
}
