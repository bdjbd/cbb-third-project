/*
 * 权限分配模块保存Action
 * 丁照祥
 * 2012-05-16
 * */
package com.fastunit.app.menupurview;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;

public class MenuPurviewSaveAction extends DefaultAction {
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		String roleid=ac.getRequestParameter("abdp_roleterminalmenu.form.roleid");
		String mainactivitysetid=ac.getRequestParameter("abdp_roleterminalmenu.form.mainactivitysetid");
		String p0=ac.getRequestParameter("abdp_roleterminalmenu.form.p0");
		String p1=ac.getRequestParameter("abdp_roleterminalmenu.form.p1");
		String p2=ac.getRequestParameter("abdp_roleterminalmenu.form.p2");
		String p3=ac.getRequestParameter("abdp_roleterminalmenu.form.p3");
		
		String delSql="delete from ABDP_ROLECLIENTMAIN where roleid='"+roleid+"'";
		db.execute(delSql);
		
		Table table = new Table("app","ABDP_ROLECLIENTMAIN");
		TableRow insertRow=table.addInsertRow();
		insertRow.setValue("roleid",roleid);
		insertRow.setValue("mainactivitysetid",mainactivitysetid);
		
		if(p0.isEmpty()&&p1.isEmpty()&&p2.isEmpty()&&p3.isEmpty())
		{
			//保留上次的权限信息
		}
		else
		{
			String delRMSql="delete from ABDP_ROLETERMINALMENU where roleid='"+roleid+"'";
			db.execute(delRMSql);
			String[] arr_p0=p0.split(",");
			String[] arr_p1=p1.split(",");
			String[] arr_p2=p2.split(",");
			String[] arr_p3=p3.split(",");
			
			Table tableRM=new Table("app","ABDP_ROLETERMINALMENU");
			for(int i=0;i<arr_p0.length;i++)
			{
				String purview0=arr_p0[i];
				if(!purview0.trim().isEmpty())
				{
					TableRow rmRow = tableRM.addInsertRow();
					rmRow.setValue("roleid",roleid);
					rmRow.setValue("menuid",purview0);
					rmRow.setValue("permission",0);
				}
			}
			for(int i=0;i<arr_p1.length;i++)
			{
				String purview1=arr_p1[i];
				if(!purview1.trim().isEmpty())
				{
					TableRow rmRow = tableRM.addInsertRow();
					rmRow.setValue("roleid",roleid);
					rmRow.setValue("menuid",purview1);
					rmRow.setValue("permission",1);
				}
			}
			for(int i=0;i<arr_p2.length;i++)
			{
				String purview2=arr_p2[i];
				if(!purview2.trim().isEmpty())
				{
					TableRow rmRow = tableRM.addInsertRow();
					rmRow.setValue("roleid",roleid);
					rmRow.setValue("menuid",purview2);
					rmRow.setValue("permission",2);
				}
			}
			for(int i=0;i<arr_p3.length;i++)
			{
				String purview3=arr_p3[i];
				if(!purview3.trim().isEmpty())
				{
					TableRow rmRow = tableRM.addInsertRow();
					rmRow.setValue("roleid",roleid);
					rmRow.setValue("menuid",purview3);
					rmRow.setValue("permission",3);
				}
			}
			db.save(tableRM);
		}
		db.save(table);
		ac.setSessionAttribute("app.abdp_roleterminalmenu.form.roleid",roleid);
		//super.doAction(db, ac);
	}
	
}
