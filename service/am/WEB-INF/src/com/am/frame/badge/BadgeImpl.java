package com.am.frame.badge;

import org.json.JSONObject;

import com.fastunit.jdbc.DB;

/**
 * @author YueBin
 * @create 2016年4月15日
 * @version 
 * 说明:<br />
 * 徽章接口
 */
public interface BadgeImpl {
	/**分红权徽章**/
	public static final String Badge_FH="Badge.FH.LEVEL";
	/**折扣徽章**/
	public static final String Badge_ZK="BadgeImpl.Badge.ZK";
	/**提现徽章**/
	public static final String Badge_TJ="BadgeImpl.Badge.TJ";
	/**维修人员徽章**/
	public static final String Badge_WXRJ="BadgeImpl.Badge.WXRJ";
	/**技术专家徽章**/
	public static final String Badge_JSZJ = "Badge.FH.Technical.exp";
	/**物流车辆徽章**/
	public static final String Badge_WLCL = "Badge.wlcl";
	/**配送人员徽章**/
	public static final String Badge_PSRY = "Badge.psry";
	/**
	 * 根据企业徽章id初始化徽章
	 * @param db
	 * @param id
	 */
	public void init(DB db,String id);
	
	/**
	 * 根据企业徽章编号初始化企业徽章
	 * @param db
	 * @param entBadgeCode
	 */
	public void initByEntBadgeCode(DB db,String entBadgeCode);
	
	/**
	 * 给会员增加徽章
	 * @param memberId  会员ID
	 * @param DB db com.fastunit.jdbc.DB
	 * @return  添加成功后返回具体实现
	 */
	public JSONObject add(DB db,String memberId);
	
	/**
	 * 删除会员徽章
	 * @param userId
	 * @param entBgCode 
	 * @param DB db   com.fastunit.jdbc.DB
	 * @return 删除成功后返回具体实现
	 */
	public JSONObject del(DB db,String userId);
	
}
