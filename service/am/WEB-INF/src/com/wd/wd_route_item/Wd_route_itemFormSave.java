package com.wd.wd_route_item;
import java.util.List;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;
public class Wd_route_itemFormSave extends DefaultAction{	
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {	
		String name=ac.getRequestParameter("wd_route_item.form.route_name");
		String num=ac.getRequestParameter("wd_route_item.form.route_num");
		String code=ac.getRequestParameter("wd_route_item.form.item_code");
		String begin_point=ac.getRequestParameter("wd_route_item.form.begin_point");
		String begin_longitude=ac.getRequestParameter("wd_route_item.form.begin_longitude");
		String begin_latitude=ac.getRequestParameter("wd_route_item.form.begin_latitude");
		String end_point=ac.getRequestParameter("wd_route_item.form.end_point");
		String remark=ac.getRequestParameter("wd_route_item.form.remark");
		String sql=" select route_id from WD_ROUTE where route_name= "+name+"and route_num ="+num;
		 MapList list=db.query(sql);
		 Table table1 =ac.getTable("WD_ROUTE_ITEM");//字表
		 List<TableRow> tr1=table1.getRows();  
		String id= list.getRow(0).get("route_id");//取出主表中的主键	      
		// String id = tr.get(0).getValue("route_id");
		 tr1.get(0).setValue("route_id", id);
		 tr1.get(0).setValue("item_code", code);
		 tr1.get(0).setValue("begin_point", begin_point);
		 tr1.get(0).setValue("begin_longitude", begin_longitude);
		 tr1.get(0).setValue("begin_latitude", begin_latitude);
		 tr1.get(0).setValue("end_point", end_point);
		 tr1.get(0).setValue("remark", remark);
		 db.save(table1);
		 ac.setSessionAttribute("wd_blj.wd_route_item.form.route_id",id);	
		 System.out.println("3333333333333333333333333"+ac.getSessionAttribute("route_id","111111111"));		
	}

}
