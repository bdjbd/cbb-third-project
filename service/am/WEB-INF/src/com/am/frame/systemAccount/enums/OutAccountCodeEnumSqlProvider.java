package com.am.frame.systemAccount.enums;

import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/**
 * 支付页面出账账号枚举SQLprovider
 * @author yuebin
 * 1，接受参数 out_account_code 
 * 2，如果是查看模式，直接显示所有的系统账号
 */
public class OutAccountCodeEnumSqlProvider implements SqlProvider {

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		String sql="SELECT sa_code AS value,sa_name AS name "+
					" FROM mall_system_account_class "+
					" WHERE status_valid=1 AND account_type=2 AND terminal_type='WEB' "+
					" ORDER BY sa_code DESC ";
		
		//获取页面模式
		String m=ac.getRequestParameter("m");
		if("s".equals(m)){
			return sql;
		}else{
			//获取出账账号信息 ,然后根据账号过滤账号下拉枚举
			String outAccountCode=ac.getRequestParameter("out_account_code"); 
			if(outAccountCode==null){
				outAccountCode=ac.getSessionAttribute("ss.payment.out_account_code").toString();
			}
			
			if(!Checker.isEmpty(outAccountCode)){
				
				ac.setSessionAttribute("ss.payment.out_account_code", outAccountCode);
				
				String[] outAccounts=outAccountCode.split(",");
				String queryWhere="";
				for(String acc:outAccounts){
					queryWhere+="'"+acc+"',";
				}
				queryWhere+="''";
				
				sql="SELECT sa_code AS value,sa_name AS name "+
					" FROM mall_system_account_class "+
					" WHERE sa_code IN ("+queryWhere+")";
			}
		}
		
		return sql;
	}

}
