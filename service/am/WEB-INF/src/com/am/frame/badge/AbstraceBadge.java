package com.am.frame.badge;

import org.json.JSONObject;

import com.am.frame.badge.entity.EnterpriseBadge;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年4月15日
 * @version 
 * 说明:<br />
 * 徽章接口
 */
public abstract class AbstraceBadge implements BadgeImpl{
	
	protected EnterpriseBadge entBadge;
	
	
	/**
	 * 根据企业徽章id初始化徽章
	 * @param db
	 * @param id
	 */
	@Override
	public void init(DB db,String id){
		try{
			String querSQL="SELECT * FROM mall_EnterpriseBadge  WHERE id=? ";
			MapList map=db.query(querSQL, id, Type.VARCHAR);
			if(!Checker.isEmpty(map)){
				entBadge=new EnterpriseBadge(map.getRow(0));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据企业徽章编号初始化企业徽章
	 * @param db
	 * @param entBadgeCode
	 */
	@Override
	public void initByEntBadgeCode(DB db,String entBadgeCode){
		try{
			String querSQL="SELECT * FROM mall_EnterpriseBadge  WHERE ent_badge_code=? ";
			MapList map=db.query(querSQL, entBadgeCode, Type.VARCHAR);
			if(!Checker.isEmpty(map)){
				entBadge=new EnterpriseBadge(map.getRow(0));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 为会员添加徽章，如果已经有次企业编码的徽章，
	 * 则删除以前的徽章，然后增加新徽章
	 * @return 	KEY "ID","ENTERPRISEBADGEID","MEMBERID"
	 * 		,"BADGEPARAME"
	 */
	@Override
	public JSONObject add(DB db,String memberId){
		JSONObject result=new JSONObject();
		
		//如果当前用户有对应企业编号的徽章，则删除徽章
		String delSQL="DELETE FROM mall_UserBadge WHERE memberid='"+memberId+"' AND enterprisebadgeid='"+entBadge.getId()+"' ";
		try {
			db.execute(delSQL);
		} catch (JDBCException e1) {
			e1.printStackTrace();
		}
		
		Table useBadgeTable=new Table("am_bdp","mall_userbadge");
		TableRow row=useBadgeTable.addInsertRow();
		
		row.setValue("enterprisebadgeid",entBadge.getId());
		row.setValue("memberid",memberId);
		row.setValue("badgeparame",entBadge.getBadgeParame());
		
		try {
			db.save(useBadgeTable);
			
			String id=useBadgeTable.getRows().get(0).getValue("id");
		
			result.put("ID",id);
			result.put("ENTERPRISEBADGEID", entBadge.getId());
			result.put("MEMBERID", memberId);
			result.put("BADGEPARAME", entBadge.getBadgeParame());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@Override
	public JSONObject del(DB db,String userId){
		JSONObject result=new JSONObject();
		
		result=del(db, userId,entBadge.getId());
		return result;
	}
	
	public JSONObject del(DB db,String userId,String entBageId){
		JSONObject result=new JSONObject();
		
		String delSQL="DELETE FROM mall_UserBadge WHERE memberid=? AND EnterpriseBadgeID=? ";
		try {
			db.execute(delSQL,
					new String[]{userId,entBageId}
			,new int[]{Type.VARCHAR,Type.VARCHAR});
			
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		return result;
	}
}
