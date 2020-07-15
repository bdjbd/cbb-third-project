package com.am.frame.badge;

import org.json.JSONObject;

import com.am.frame.badge.entity.BadgeTemplate;
import com.am.frame.badge.entity.EnterpriseBadge;
import com.am.frame.badge.entity.UserBadge;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.base.ParametersList;

/**
 * @author Mike
 * @create 2014年11月19日
 * @version 
 * 说明:<br />
 */
public class AMBadgeManager {
	
	private ParametersList params=new ParametersList();

	/**
	 * 初始化方法 返回为false时表明该用户没有拥有该徽章
	 * 
	 * @param userID
	 *            当前登录用户ID
	 * @param BadgeCode
	 *            徽章模板代码
	 */
	public boolean init(String userID, String BadgeCode) {
		DB db=null;
		try {
			db= DBFactory.newDB();
			 
			String sql="SELECT ub.badgeParame FROM am_userbadge AS ub "
				   		+ " LEFT JOIN am_enterpriseBadge AS eb "
				   		+ " ON ub.enterpriseBadgeID=eb.id "
				   		+ " LEFT JOIN am_badgeTemplate AS bt "
				   		+ " ON eb.badgeTemplateID=bt.id "
				   		+ " WHERE ub.memberId="+userID
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
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
	  }

	/**
	 * 获得徽章参数列表
	 * 
	 */
	public ParametersList getBadgeList() {
		return params;
	}

	/**
	 * 通过徽章参数名称获得参数值
	 * 
	 * @param name
	 */
	public String getBadgeValueOfName(String name) {
		return this.params.getValueOfName(name);
	}

	/**
	 * 根据徽章编码获取徽章模版
	 * @param badgeCode  企业徽章code
	 * @return  徽章模板
	 */
	public BadgeTemplate initBadgeTemplateByCode(DB db,String badgeCode){
		BadgeTemplate result=null;
		try{
			String querySQL="SELECT * FROM mall_BadgeTemplate WHERE BadgeCode=? ";
			MapList map=db.query(querySQL, badgeCode,Type.VARCHAR);
			if(!Checker.isEmpty(map)){
				result=new BadgeTemplate(map.getRow(0));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 根据徽章id获取徽章模版
	 * @param badgeCode  企业徽章code
	 * @return  徽章模板
	 */
	public BadgeTemplate initBadgeTemplateById(DB db,String id){
		BadgeTemplate result=null;
		try{
			String querySQL="SELECT * FROM mall_BadgeTemplate WHERE id=? ";
			MapList map=db.query(querySQL, id,Type.VARCHAR);
			if(!Checker.isEmpty(map)){
				result=new BadgeTemplate(map.getRow(0));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 根据企业徽章编码初始化企业徽章
	 * @param badgeCode  企业徽章id
	 * @return  徽章模板
	 */
	public EnterpriseBadge initBadgeTemplateByEntId(DB db,String entBadgeId){
		EnterpriseBadge result=null;
		try{
			String querySQL="SELECT * FROM mall_EnterpriseBadge WHERE ent_badge_code=? ";
			MapList map=db.query(querySQL, entBadgeId,Type.VARCHAR);
			if(!Checker.isEmpty(map)){
				result=new EnterpriseBadge(map.getRow(0));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 根据会员id初始化会员徽章
	 * @param memberId 会员id
	 * @param entBgCode 企业徽章编码
	 * @return
	 */
	public UserBadge initBadgeByMemberId(DB db,String memberId,String entBgCode){
		UserBadge result=null;
		try{
			String querySQL="SELECT * FROM mall_UserBadge AS ubad "+
							" LEFT JOIN mall_EnterpriseBadge AS entbad ON ubad.EnterpriseBadgeID=entbad.id "+
							" LEFT JOIN mall_BadgeTemplate AS tbad ON tbad.id=entbad.BadgeTemplateID "+
							" WHERE ubad.memberid=? AND entbad.ent_badge_code=? ";
			
			MapList map=db.query(querySQL,
					new String[]{memberId,entBgCode},
					new int[]{Type.VARCHAR,Type.VARCHAR});
			
			if(!Checker.isEmpty(map)){
				result=new UserBadge(map.getRow(0));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 根据企业徽章编号初始化企业徽章
	 * @param entBadgeCode  企业徽章编号
	 * @return 企业徽章
	 */
	public EnterpriseBadge initBadgeByCode(DB db,String entBadgeCode){
		EnterpriseBadge result=null;
		try{
			String querySQL="SELECT * FROM mall_EnterpriseBadge WHERE ent_badge_code=? ";
			MapList map=db.query(querySQL, entBadgeCode,Type.VARCHAR);
			if(!Checker.isEmpty(map)){
				result=new EnterpriseBadge(map.getRow(0));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 根据会员id获取会员的分红权比例
	 * @param memberId 会员id
	 * @return
	 */
	public double getBounsRatio(DB db,String memberId){
		double ratio=0;
		String querySQL="SELECT  tbad.BadgeCode,* FROM mall_UserBadge AS ubad   "+
						"  LEFT JOIN mall_EnterpriseBadge AS entbad ON ubad.EnterpriseBadgeID=entbad.id  "+   
						"  LEFT JOIN mall_BadgeTemplate AS tbad ON tbad.id=entbad.BadgeTemplateID     "+
						"  WHERE 1=1 AND ubad.memberid=?  "+
						"  AND tbad.BadgeCode LIKE 'LEVEL_%' ORDER BY tbad.createdate DESC  ";
		try {
			MapList map=db.query(querySQL, 
					new String[]{memberId},
					new int[]{Type.VARCHAR});
			if(!Checker.isEmpty(map)){
				UserBadge userBadge=new UserBadge(map.getRow(0));
				String params=userBadge.getBadgeParame();
				try{
//				{"AuthorityPoint":
//					{"ACCESS_ROUTE":
//									{"SANC_COMMODITY":true,
//										"BUY_COMMODITY":true,
//										"CUSTOM_RECHANGE":true,
//										"FULL_TIME_PROFESSIONAL":true
//									},
//						"GET_OOD":false,
//						"OOD_RATIO":0.5  //分红比例
//					},
//						"GET_INVST_PROJECTBONUS":true,
//						"GET_INVITE_FREE":true
//					}
					
					JSONObject paramsJs=new JSONObject(params);
					
					JSONObject authorityPoint=paramsJs.getJSONObject("AuthorityPoint");
					
					ratio=authorityPoint.getDouble("OOD_RATIO");
					
				}catch(Exception e){
					e.printStackTrace();
					ratio=0;
				}
			}
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		return ratio;
	}

	
	/**
	 * 根据企业徽章编号删除指定会员的徽章
	 * @param db
	 * @param entBadgeCode 企业徽章编号
	 * @param memberId  会员id
	 */
	public void deleteBadgeByEntBadgeCode(DB db, String entBadgeCode, String memberId) {
		EnterpriseBadge entBadge=initBadgeTemplateByEntId(db, entBadgeCode);
		BadgeTemplate badgeTemp=initBadgeTemplateById(db, entBadge.getBadgeTemplateID());
		String badgeClassPath=badgeTemp.getClassPath();
		
		BadgeImpl badgeImpl=null;
		
		if(!Checker.isEmpty(badgeClassPath)){
			try {
				badgeImpl=(BadgeImpl) Class.forName(badgeClassPath).newInstance();
				badgeImpl.initByEntBadgeCode(db, entBadgeCode);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		if(badgeImpl!=null){
			badgeImpl.del(db, memberId);
		}
		
	}
	
	/**
	 * 给会员添加徽章
	 * @param db DB
	 * @param entBadgeCode  企业徽章编码
	 * @param memberId  需要添加会员的会员id
	 * @return 	KEY "ID","ENTERPRISEBADGEID","MEMBERID"
	 * 		,"BADGEPARAME"    ID,社员徽章ID
	 * 
	 */
	public JSONObject addBadgeByEntBadgeCode(DB db, String entBadgeCode, String memberId) {
		JSONObject result=new JSONObject();
		
		EnterpriseBadge entBadge=initBadgeTemplateByEntId(db, entBadgeCode);
		BadgeTemplate badgeTemp=initBadgeTemplateById(db, entBadge.getBadgeTemplateID());
		String badgeClassPath=badgeTemp.getClassPath();
		
		BadgeImpl badgeImpl=null;
		
		if(!Checker.isEmpty(badgeClassPath)){
			try {
				badgeImpl=(BadgeImpl) Class.forName(badgeClassPath).newInstance();
				badgeImpl.initByEntBadgeCode(db, entBadgeCode);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		if(badgeImpl!=null){
			result=badgeImpl.add(db, memberId);
//			try {
//			result.put("ID","2bcb062b-e196-43f9-9803-54f1b204bc82");
//			result.put("ENTERPRISEBADGEID","941ce2c0-f8e5-4688-a38a-47693304bb59");
//			result.put("MEMBERID", "941ce2c0-f8e5-4688-a38a-47693304bb59");
//			result.put("BADGEPARAME", "{\"FH_RATIO\":\"0.1\"}");
//			String sql="insert into MALL_USERBADGE (id,enterprisebadgeid,memberid,badgeparame) values (?,?,?,?)";
//			
//				db.execute(sql,
//						new String[]{
//						"2bcb062b-e196-43f9-9803-54f1b204bc82",
//						"941ce2c0-f8e5-4688-a38a-47693304bb59",
//						"941ce2c0-f8e5-4688-a38a-47693304bb59",
//						"186726e4-b516-4aa9-94d7-814bc1f6fa61",
//						"{\"FH_RATIO\":\"0.1\"}"
//				},new int[]{
//						Type.VARCHAR,
//						Type.VARCHAR,
//						Type.VARCHAR,
//						Type.VARCHAR,
//						Type.VARCHAR
//				});
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			 
		}
		return result;
	}
	
	/**
	 * 根据会员ID，企业徽章编号，获取社员徽章集合
	 * @param db  DB
	 * @param memberId 会员ID
	 * @param badgeTaskEntCode  企业徽章编码
	 * @throws JDBCException 
	 * @return MapList  keys:id(社员徽章ID),
	 *  EnterpriseBadgeID(企业徽章ID),memberId(社员ID),BadgeParame(社员徽章参数),ent_badge_code(企业徽章编号)
	 */
	public MapList getUserBadgetMapList(DB db, String memberId,
			String badgeTaskEntCode) throws JDBCException {
		String querySQL="SELECT ub.*,eb.ent_badge_code FROM mall_UserBadge AS ub "+
				" LEFT JOIN mall_EnterpriseBadge AS eb ON ub.EnterpriseBadgeID=eb.id "+
				" WHERE ub.memberid=? AND eb.ent_badge_code=? ";
		
		MapList result=db.query(querySQL,new String[]{
				memberId,badgeTaskEntCode
		},new int[]{
				Type.VARCHAR,Type.VARCHAR
		});
		
		return result;
	}
	
	
	/**
	 * 更新会员徽章参数
	 * @param db  DB
	 * @param badgeId  徽章ID
	 * @param badgeParams 社员徽章参数
	 * @throws JDBCException 
	 */
	public void updateUserBadgeParams(DB db, String badgeId, String badgeParams) throws JDBCException {
		String updateSQL=" UPDATE mall_UserBadge SET BadgeParame=? WHERE id=? ";
		db.execute(updateSQL,new String[]{
				badgeParams,badgeId
		},new int[]{
				Type.VARCHAR,Type.VARCHAR
		});
	}
	
	
	
}
