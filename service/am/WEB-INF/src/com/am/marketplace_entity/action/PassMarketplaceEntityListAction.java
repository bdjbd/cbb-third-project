package com.am.marketplace_entity.action;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * @author  作者：yangdong
 * @date 创建时间：2016年4月26日 上午11:56:53
 * @version 土地流转列表审核通过
 */
public class PassMarketplaceEntityListAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// 通过审核
		String[] list = ac.getRequestParameters("_s_mall_marketplace_entity.list");
		String[] ids = ac.getRequestParameters("mall_marketplace_entity.list.id.k");
		String[] auditfee= ac.getRequestParameters("mall_marketplace_entity.list.audit_fee");
		String[] member_id= ac.getRequestParameters("mall_marketplace_entity.list.memberid");
		String[] release_status = ac.getRequestParameters("mall_marketplace_entity.list.release_status");
		
		String userId=ac.getVisitor().getUser().getId();
		if(!Checker.isEmpty(list)){
			for(int i = 0; i <list.length; i++ ){
				if(list[i].equals("1")){
					
					
					//审核通过  ，像抗风险自救金账户转入手续费
					VirementManager vir = new VirementManager();
					if("0".equals(release_status[i])){		
					vir.execute(db,"",member_id[i],"",SystemAccountClass.ANTI_RISK_SELF_SAVING_ACCOUNT,auditfee[i],"发布农村大市场摊位手续费", "", "", false);
					}else{
					vir.execute(db,"",member_id[i],"",SystemAccountClass.GROUP_ANTI_RISK_SELF_SAVING_ACCOUNT,auditfee[i],"发布农村大市场摊位手续费", "", "", false);
					}
					
					String sql = " UPDATE mall_marketplace_entity SET status = 3,shelve_time='now()',operationer='"+userId+"' WHERE id = '"+ids[i]+"' ";
					db.execute(sql);
				}
			}
		}
	}
}
