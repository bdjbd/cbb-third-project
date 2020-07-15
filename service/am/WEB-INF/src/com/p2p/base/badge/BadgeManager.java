package com.p2p.base.badge;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.base.ParametersList;

/**
 * Author: Mike 2014年7月18日 说明：
 * 
 **/
public class BadgeManager {

	private ParametersList params=new ParametersList();

	/**
	 * 初始化方法 返回为false时表明该用户没有拥有该徽章
	 * 
	 * @param userID
	 *            当前登录用户ID
	 * @param BadgeCode
	 *            徽章模板代码
	 * @pdOid 6fa86908-229d-46eb-baf5-165482d1f25c
	 */
	public boolean init(String userID, String BadgeCode) {
		 
		try {
			DB db= DBFactory.getDB();
			 String sql="SELECT ub.badgeParame FROM p2p_userbadge AS ub "
				   		+ " LEFT JOIN p2p_enterpriseBadge AS eb "
				   		+ " ON ub.enterpriseBadgeID=eb.id "
				   		+ " LEFT JOIN p2p_badgeTemplate AS bt "
				   		+ " ON eb.badgeTemplateID=bt.id "
				   		+ " WHERE ub.member_code="+userID
				   		+ " AND bt.badgecode='"+BadgeCode+"'";
		MapList map=db.query(sql);
		if(Checker.isEmpty(map))return false;
		String badgeParam=map.getRow(0).get("badgeparame");
		String[] paramsKV=badgeParam.split(";");
		for(String kqv:paramsKV){
			this.params.add(kqv.split("=")[0], kqv.split("=")[1]);;
		}
		return true;
		} catch (JDBCException e) {
			e.printStackTrace();
			return false;
		}
	   }

	/**
	 * 获得徽章参数列表
	 * 
	 * @pdOid cb6f6462-3499-4662-b2a4-a3705415f8b9
	 */
	public ParametersList getBadgeList() {
		return params;
	}

	/**
	 * 通过徽章参数名称获得参数值
	 * 
	 * @param name
	 * @pdOid 04d9bcc9-b61d-4ab3-ae3b-08c59d7baec9
	 */
	public String getBadgeValueOfName(String name) {
		return this.params.getValueOfName(name);
	}
}
