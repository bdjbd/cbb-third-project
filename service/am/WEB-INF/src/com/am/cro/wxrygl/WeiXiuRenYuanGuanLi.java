package com.am.cro.wxrygl;

import java.util.UUID;

import com.am.common.util.FileUtils;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;

/**
 * 维修人员管理保存身份证照片
 * 
 * @author guorenjie
 */
public class WeiXiuRenYuanGuanLi extends DefaultAction {
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
			
			Table table = ac.getTable("CRO_REPAIRMAN");
			db.save(table);
			TableRow tr = table.getRows().get(0);
			// id
			String id = tr.getValue("id");
			String idcardimagepath = new FileUtils().getFastUnitFilePathJSON(
					"CRO_REPAIRMAN", "bdp_idcardimagepath", id);
			// 保存sql
			String updateSql = "UPDATE CRO_REPAIRMAN set idcardimagepath='"
					+ idcardimagepath + "' WHERE id='" + id + "' ";
			db.execute(updateSql);
			String params = ac.getActionParameter();	
			if (params.equals("saveandadd")) 
			{
				String uuid = UUID.randomUUID().toString();
				ac.getActionResult().setSuccessful(true);
				ac.getActionResult().setUrl("/am_bdp/cro_repairman.form.do?cro_repairman.form.id='"+uuid+"'");
				
			}
	}
}
