package com.am.expert_compound.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年5月3日
 *@version
 *说明：科普引智大院文章发布Action
 */
public class ArticlesPublishedFormAction extends DefaultAction {
	
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// 通过审核
		String id = ac.getRequestParameter("mall_cope_article.form.id");
		String reviewRemarks = ac.getRequestParameter("mall_cope_article.form.review_remarks");
		String paramStatus = ac.getActionParameter();
		String sql = "";
		boolean flag = true;
		if("1".equals(paramStatus)){
			if(Checker.isEmpty(reviewRemarks)){
				flag = false;
			}
			//发布
			sql = " UPDATE mall_cope_article SET status = 3,release_time=now(),review_remarks = '"+reviewRemarks+"' WHERE id = '"+id+"' ";
		}else if("2".equals(paramStatus)){
			//驳回
			sql = " UPDATE mall_cope_article SET status = 4,review_remarks = '"+reviewRemarks+"' WHERE id = '"+id+"' ";
		}
		
		if(flag){
			
			db.execute(sql);
			ac.getActionResult().setSuccessful(true);
		
		}else{
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("审核说明不能为空");
		}
	}
	
	
}
