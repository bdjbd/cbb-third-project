package com.wisdeem.wwd.commodity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * 删除商品Action，上架商品不可以删除
 * @author Administrator
 *
 */
public class DeleteCommodityDeleAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		String select[]=ac.getRequestParameters("_s_ws_commodity_name.list");
		String commodityId[]=ac.getRequestParameters("ws_commodity_name.list.comdity_id.k");
		//商品状态  1,起草；2，上架；3，下架
		String dataStatus[]=ac.getRequestParameters("ws_commodity_name.list.data_status");
		//编号
		String comdityCode[]=ac.getRequestParameters("ws_commodity_name.list.comdity_code");
		List<String[]>  deleCommdityIds=new ArrayList<String[]>();
		List<String>  dotDeleCommditCode=new ArrayList<String>();
		
		if(!Checker.isEmpty(select)){
			for (int i = 0; i < select.length; i++) {
				if ("1".equals(select[i])) {// 1为选中
					if("3".equalsIgnoreCase(dataStatus[i])||"1".equalsIgnoreCase(dataStatus[i])){
						//下架或者起草状态的商品可以删除
						deleCommdityIds.add(new String[]{commodityId[i]});
					}else{
						dotDeleCommditCode.add(comdityCode[i]);
					}
				}
			}
			
			String deleteSQL="DELETE FROM ws_commodity_name  WHERE comdity_id=?";
			db.executeBatch(deleteSQL, deleCommdityIds,new int[]{Type.INTEGER});
			StringBuffer msg = new StringBuffer();
			msg.append("删除执行成功！");
			msg.append("删除"+deleCommdityIds.size()+"条。");
			if(!Checker.isEmpty(dotDeleCommditCode)){
				msg.append("其中有 "+dotDeleCommditCode.size()+"无法删除:");
				msg.append(Arrays.toString(dotDeleCommditCode.toArray()));
				msg.append(" \n下架后可以删除。");
			}
			ac.getActionResult().addSuccessMessage(msg.toString());
			ac.getActionResult().setSuccessful(true);
		}
	}
}
