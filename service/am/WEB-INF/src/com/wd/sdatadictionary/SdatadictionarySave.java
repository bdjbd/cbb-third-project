package com.wd.sdatadictionary;

import java.util.List;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;
//保存按钮
public class SdatadictionarySave extends DefaultAction {
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception { 
		Table Tablea = ac.getTable("SDATADICTIONARYHZ");//父表
		List<TableRow> tr = Tablea.getRows();
		Table tableb = ac.getTable("SDATADICTIONARYMX");//字表
		List<TableRow> tr1=tableb.getRows();
		if (tr.get(0).isInsertRow()) {
			db.save(Tablea);
			String id = tr.get(0).getValue("dictionaryid");//取出主表中的主键
			List<TableRow> tls = tableb.getRows();
			for (int i = 0;i < tls.size();i++) {
				TableRow trb = tls.get(i);
				trb.setValue("dictionaryid", id);//把主表中的主键依次放入子表中的外键
			}
			db.save(tableb);
			ac.setSessionAttribute("wd_blj.sdatadictionaryhz.form.dictionaryid", id);//把主表中主键放入到Session中去
		} else {
			for(int i=0;i<tr1.size();i++){	
				System.out.println(tr1.get(i).getValue("dictionaryid"));
				System.out.println(tr1.get(i).getValue("fldvaluename"));
				   if(tr1.get(i).isInsertRow()){
				String zid=(String)ac.getSessionAttribute("wd_blj.sdatadictionaryhz.form.dictionaryid");
			     TableRow trb = tr1.get(i);
			    trb.setValue("dictionaryid", zid);//把主表中的主键依次放入子表中的外键
				         }
				   }
			    //db.save(Tablea);
				db.save(tableb);
			}
			
			}
		
	}		

