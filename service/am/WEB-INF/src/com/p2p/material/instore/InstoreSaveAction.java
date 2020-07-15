package com.p2p.material.instore;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;

/**
 * 出库保存
 * @author Administrator
 *
 */
public class InstoreSaveAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		Table table=ac.getTable("P2P_INSTORE");
		
		db.save(table);
		
//		//入库数量
//		String counts=ac.getRequestParameter("p2p_instore.form.counts");
//		//物资编码
//		String materialscode=ac.getRequestParameter("p2p_instore.form.materialscode");
//		
//		//更新物资编码中的数量
//		String updateSQL="UPDATE p2p_MaterialsCode FROM amount=COALESCE("+counts+",0)+amount WHERE code='"+materialscode+"'";
//		db.execute(updateSQL);
	}
}
