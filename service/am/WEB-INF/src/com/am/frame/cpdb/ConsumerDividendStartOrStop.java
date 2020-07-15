package com.am.frame.cpdb;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/** * @author  作者：yangdong
 * @date 创建时间：2016年3月15日 上午11:12:20
 * @version 消费分利层级启用，停用
 */
public class ConsumerDividendStartOrStop extends DefaultAction{
	
	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		String consumerid=ac.getRequestParameter("consumerid");
		
		DB db=DBFactory.getDB();
		String checkSQL="SELECT * FROM consumer_dividend  WHERE id='"+consumerid+"'";
		MapList map=db.query(checkSQL);
		
		if(!Checker.isEmpty(map)){
			String dataStatus=map.getRow(0).get("status");
			String updateSQL="";
			
			if(Checker.isEmpty(dataStatus)){//起草--》启用
				updateSQL="UPDATE consumer_dividend SET status='1' WHERE id='"+consumerid+"'";
			}
			
			if("1".equalsIgnoreCase(dataStatus)){//启用-->>停用
				updateSQL="UPDATE consumer_dividend SET status='0' WHERE id='"+consumerid+"'";
			}
			if("0".equalsIgnoreCase(dataStatus)){//停用-->>启用
				updateSQL="UPDATE consumer_dividend SET status='1' WHERE id='"+consumerid+"'";
			}
			if(!Checker.isEmpty(updateSQL)){
				db.execute(updateSQL);
			}
		}
		
		ac.getActionResult().setUrl("/am_bdp/consumer_dividend.do?m=e&clear=am_bdp.consumer_dividend.query");
		ac.getActionResult().setSuccessful(true);
		return ac;
	}
}
