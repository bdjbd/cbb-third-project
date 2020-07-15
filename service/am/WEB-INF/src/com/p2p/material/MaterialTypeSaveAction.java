package com.p2p.material;

import com.fastunit.Ajax;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;


/**
 * 物质分类保存Action
 * @author Administrator
 *
 */
public class MaterialTypeSaveAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		Table table=ac.getTable("P2P_MATERIALSTYPE");
		db.save(table);
		String id=table.getRows().get(0).getValue("id");
		
		String preTypecode=ac.getRequestParameter("p2p_materialstype.form.pre_typecode");
		String typecodeSuff=ac.getRequestParameter("p2p_materialstype.form.typecode_suff");
		String updateSQL="UPDATE p2p_materialsType SET typecode='"+preTypecode+"_"+typecodeSuff+"' WHERE id='"+id+"' ";
		db.execute(updateSQL);
		
		ac.getActionResult().setUrl("/am_bdp/p2p_materialstype.form.do?m=s&p2p_materialstype.form.id=" + id);
//		ac.getActionResult().setScript("window.parent.document.frames[0].location='/am_bdp/p2p_materialstype.tree.do?m=s';");
		
		Ajax ajax=new Ajax(ac);
		ajax.addScript("window.parent.location.reload()");
		ajax.send();
	}
}
