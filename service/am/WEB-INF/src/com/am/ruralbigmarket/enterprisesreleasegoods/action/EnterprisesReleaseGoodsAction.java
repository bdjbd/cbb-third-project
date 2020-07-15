package com.am.ruralbigmarket.enterprisesreleasegoods.action;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;
/** 
 * @author  作者：qintao
 * @date 创建时间：2016年11月26日
 * @explain 说明 : 企业发布商品
 */

public class EnterprisesReleaseGoodsAction extends DefaultAction {
	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		//会员id、机构id
		String memberid=ac.getRequestParameter("member_id");
		//id
		String Id=ac.getRequestParameter("id");
		//手续费
		String audit_fee=Var.get("marketplace_release_free");
		DB db=DBFactory.getDB();
		String checkSQL="SELECT * FROM mall_marketplace_entity WHERE id='"+Id+"'";
		MapList map=db.query(checkSQL);
		
		if(!Checker.isEmpty(map)){
			String dataStatus=map.getRow(0).get("status");
			String updateSQL="";
			
			if("1".equals(dataStatus)||"7".equals(dataStatus)){
				updateSQL="UPDATE mall_marketplace_entity SET status='2',audit_fee='"+audit_fee+"',shelve_time='now()' WHERE id='"+Id+"'";
			}
			if(!Checker.isEmpty(updateSQL)){
				db.execute(updateSQL);
				VirementManager vir = new VirementManager();
				
				vir.execute(db,memberid,"",SystemAccountClass.GROUP_CASH_ACCOUNT,"",audit_fee,"", "发布商品手续费", "", false);
			}
		}
		return ac;
	}
}
