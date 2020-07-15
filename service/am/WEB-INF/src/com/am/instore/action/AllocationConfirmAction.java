package com.am.instore.action;

import org.json.JSONObject;

import com.am.instore.server.UpdateMaterialsCodeServer;
import com.am.instore.server.UpdateOutStoreInfoServer;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月30日
 *@version
 *说明：调拨管理--确认调拨Action
 */
public class AllocationConfirmAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//调拨ID
		String Id=ac.getRequestParameter("id");
		
		String querSql = " SELECT info.materialscode,info.out_storeallocid,info.in_storeallocid,info.counts  "
				+ "	FROM mall_allocation AS alloc "
				+ " LEFT JOIN mall_allocation_list_info AS info ON alloc.id=info.allocation_id "
				+ " WHERE alloc.id='"+Id+"' ";
		MapList list = db.query(querSql);
		if (!Checker.isEmpty(list)) {
				for (int i = 0; i < list.size(); i++) {
					//物资编码
					String materialsCode = list.getRow(i).get("materialscode");
					//出库货位ID
					String outStoreAllocId = list.getRow(i).get("out_storeallocid");
					//入库货位ID
					String inStoreAllocId = list.getRow(i).get("in_storeallocid");
					//数量
					String counts = list.getRow(i).get("counts");
					
					UpdateOutStoreInfoServer server = new UpdateOutStoreInfoServer();
					//检查库存数量是否满足调拨数量
					JSONObject jso = server.storeAllocInfo(materialsCode,outStoreAllocId,counts,db);
					
					if("403".equals(jso.get("code"))){
						ac.getActionResult().setSuccessful(false);
						ac.getActionResult().addErrorMessage(jso.getString("msg"));
						return;
					}else{
						//调拨出库方法(减去)
						server.updateStoreAllocInfo(materialsCode,counts,outStoreAllocId,db);
						
						//调拨入库方法(加上)
						UpdateMaterialsCodeServer inStoreServer = new UpdateMaterialsCodeServer();
						inStoreServer.updateStoreAllocInfo(materialsCode,counts,inStoreAllocId,db);
						
						//更新调拨管理状态
						server.updateAllocation(Id,db);
					}
				}
			}
	}

}
