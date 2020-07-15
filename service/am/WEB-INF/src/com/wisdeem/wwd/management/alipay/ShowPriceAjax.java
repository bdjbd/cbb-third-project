package com.wisdeem.wwd.management.alipay;

import com.fastunit.Ajax;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.Action;

public class ShowPriceAjax  implements Action {
	
	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		Ajax ajax = new Ajax(ac);

		String monthly_fee_id = ac.getRequestParameter("ws_alipay_order.form.monthly_fee_id");
		String priceSQL="SELECT montyly_money from WS_ENTERPRISE_MONTH_TARIFF where monthly_fee_id="+monthly_fee_id+"";
		DB db = DBFactory.getDB();
		MapList rs = db.query(priceSQL);
		String price ="";
		if(rs.size()!=0){
		  price = rs.getRow(0).get("montyly_money");
		  ajax.addScript("document.getElementsByName(\"ws_alipay_order.form.price\")[0].value="+price+"");
		 // ajax.replace("text", " " + price + " ");
		  ajax.send();
		}
		//String quantity = ac.getRequestParameter("ws_alipay_order.form.quantity");

		ajax.send();
		return ac;
	}
}
