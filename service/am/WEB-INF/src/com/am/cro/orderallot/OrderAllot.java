package com.am.cro.orderallot;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;

/**
 * 订单分配
 * @author guorenjie
 */
public class OrderAllot extends DefaultAction{
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		Table table = ac.getTable("CRO_CARREPAIRORDER");
		db.save(table);
		TableRow tr = table.getRows().get(0);
		// id
		String id = tr.getValue("id");
		//预约类型
		String repair_class = tr.getValue("repair_class");
		//维修人员id
		String repairmanid = tr.getValue("repairmanid");
		
		//派单时间
		String distributeleafletstime = tr.getValue("distributeleafletstime");
		//订单状态
		String orderstate = ac.getActionParameter();
		
		String params = " repair_class ="+repair_class+",repairmanid='"+repairmanid+"',distributeleafletstime='"+distributeleafletstime+"',orderstate="+orderstate;
		
		String sql = "update CRO_CARREPAIRORDER set"+params+" where id='"+id+"'";
		db.execute(sql);
	}
}
