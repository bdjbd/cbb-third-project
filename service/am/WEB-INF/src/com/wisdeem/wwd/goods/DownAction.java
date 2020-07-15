package com.wisdeem.wwd.goods;

import java.util.ArrayList;
import java.util.List;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;

public class DownAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {

		// 获取当前登陆人id
		String username = ac.getVisitor().getUser().getId();
		// 获得要修改的数据的编号
//		String isUpdate[] = ac.getRequestParameters("_s_ws_commodity_name.list");
		String isUpdate[] = ac.getRequestParameters("_s_p2p_shop_authen.list");
		String ids[] = ac.getRequestParameters("p2p_shop_authen.list.comdity_id");
		String data_status[] = ac.getRequestParameters("p2p_shop_authen.list.data_status");
		List<String[]> list = new ArrayList<String[]>();
		for (int i = 0; i < isUpdate.length; i++) {
			if ("1".equals(isUpdate[i])) {
				if ("1".equals(data_status[i])) {
					ac.getActionResult().addErrorMessage("第"+(i+1)+"行，起草状态的不能下架，请重新选择！");
					return;
				}else if("3".equals(data_status[i])){
					ac.getActionResult().addErrorMessage("第"+(i+1)+"行，商品已经下架，请重新选择！");
					return;
				}
				list.add(new String[] { ids[i] });
			}
		}
		// 批量更新
		String sql = "update ws_commodity_name set data_status=3 where comdity_id = ? ";
		db.executeBatch(sql, list, new int[] { Type.INTEGER });
		ac.getActionResult().addSuccessMessage("下架成功");
	}

}
