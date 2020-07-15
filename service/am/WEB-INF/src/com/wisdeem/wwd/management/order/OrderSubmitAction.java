package com.wisdeem.wwd.management.order;

import java.util.ArrayList;
import java.util.List;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;
import com.p2p.business.DispatcherOrderService;

public class OrderSubmitAction extends DefaultAction{
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {

		// 获取当前登陆人id
		String username = ac.getVisitor().getUser().getId();
		// 获得要修改的数据的编号
		String isUpdate[] = ac.getRequestParameters("_s_ws_order.list");
		String ids[] = ac.getRequestParameters("ws_order.list.order_code");
		String data_status[] = ac.getRequestParameters("ws_order.list.data_status");
		
		List<String[]> list = new ArrayList<String[]>();
		for (int i = 0; i < isUpdate.length; i++) {
			if ("1".equals(isUpdate[i])) {
//				if ("3".equals(data_status[i])) {
//					ac.getActionResult().addErrorMessage("已交易的订单不能再确认，请重新选择！");
//					return;
//				}
				if("6".equals(data_status[i])){//订单完成。
					//更新销售数量
					DispatcherOrderService.updateModiftyAmount(db,ids[i]);
					
					//更新下单人积分
					DispatcherOrderService.updateMemberScore(db, ids[i]);
					
					//修改库存
					DispatcherOrderService.updateStore(db, ids[i]);
				}
				list.add(new String[] { data_status[i],ids[i] });
			}
		}
		// 批量更新
		String sql = "update WS_ORDER set data_status=? where order_code = ?";
		db.executeBatch(sql, list, new int[] { Type.INTEGER,Type.VARCHAR });
		ac.getActionResult().addSuccessMessage("确认成功");
	}
}
