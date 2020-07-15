package com.wisdeem.wwd.goods;

import java.sql.Connection;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.context.ActionResult;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;
import com.wisdeem.wwd.WeChat.Utils;

public class SaveAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		ActionResult ar = ac.getActionResult();
		Table table = ac.getTable("WS_COMMODITY");
		String suf = ac.getRequestParameter("ws_commodity_edit.suf");
		String cCode = ac.getRequestParameter("ws_commodity_edit.c_code_s");
		String parent_id = ac.getRequestParameter("ws_commodity_edit.parent_id");
		String comdtyType=ac.getRequestParameter("ws_commodity_edit.comdytype");
		
		String sql = "select comdity_id from ws_commodity_name where comdity_class_id="
				+ parent_id + "";
		String comdyClassId="";
		MapList mapList = db.query(sql);
		if (mapList.size() > 0) {
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("该分类下有商品，不能再添加子分类！");
			return;

		} else {
			if (table != null && table.getRows().size() > 0) {
				TableRow tabTr = table.getRows().get(0);
				String c_code = suf +"-"+cCode;
				tabTr.setValue("c_code",c_code);
				db.save(table);
				if (ar.isSuccessful()) {
					TableRow r = table.getRows().get(0);
					comdyClassId = r.getValue("comdy_class_id");
					ar.setUrl("/wwd/ws_commodity.form.do?parent_id=" + comdyClassId);
					ar.setScript("window.parent.document.frames[0].location='/wwd/goodsorts.tree.do?comdytype="+comdtyType+"'");
				}
			}
		}
//		comdy_class_id
		String fileName=Utils.getFastUnitFilePath("WS_COMMODITY", "fu_listimage", comdyClassId);
		if(fileName!=null&&fileName.length()>1){
			fileName=fileName.substring(0, fileName.length()-1);
			String updateSql="UPDATE WS_COMMODITY  SET listimage='"
			+fileName+"'  WHERE COMDY_CLASS_ID="+comdyClassId+" ";
			System.out.println(fileName);
			Connection conn=db.getConnection();
			conn.createStatement().execute(updateSql);
			conn.commit();
		}
		
	}
}
