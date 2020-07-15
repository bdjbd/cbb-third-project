package com.am.mall.commodity;

import org.json.JSONObject;

import com.am.frame.webapi.db.DBManager;
import com.am.mall.beans.commodity.Commodity;
import com.am.mall.beans.commodity.CommoditySpecifications;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2014年11月18日
 * @version 
 * 说明:<br />
 * 商品管理类
 */
public class CommodityManager {

	private static CommodityManager comdityManager;
	
	private CommodityManager(){
	}
	
	public static CommodityManager getInstance(){
//		if(comdityManager==null){
//			comdityManager=new CommodityManager();
//		}
//		return comdityManager;
		return new CommodityManager(); 
	}
	
	
	/**
	 * 根据商品ID获取商品
	 * @param id 商品ID
	 * @return 商品实例
	 */
	public Commodity getCommodityByID(String commodityID){
		Commodity comdity=null;
		DB db= null;
		try{
			db=DBFactory.newDB();
			
			comdity=getCommodityByID(commodityID,db);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		return comdity;
	}
	
	/**
	 * 根据商品ID获取商品
	 * @param id 商品ID
	 * @param db DB
	 * @return 商品实例
	 */
	public Commodity getCommodityByID(String commodityID,DB db){
		Commodity comdity=null;
		try{
			String findSQL="SELECT * FROM mall_commodity WHERE id=?";
			
			MapList map=db.query(findSQL, commodityID, Type.VARCHAR);
			comdity=new Commodity(map);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return comdity;
	}
	
	/**
	 * 根据商品ID获取商品奖励规则<br />
	 * 商品奖励规则字段：<br />
	 *  id,commodityid,rewardruleid,parameters
	 * @param commodityId
	 * @return
	 */
	public MapList getCommodityRewardRuleById(String commodityId){

		MapList map=new MapList();
		DB db =null;
		try{
			db=DBFactory.newDB();
			
			map=getCommodityRewardRuleById(commodityId, db);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		
		return map;
	}
	
	
	/**
	 * 根据商品ID获取商品奖励规则<br />
	 * 商品奖励规则字段：<br />
	 *  id,name,explain,classpath,globalparam
	 * @param commodityId
	 * @param DB 
	 * @return
	 * @throws JDBCException 
	 */
	public MapList getCommodityRewardRuleById(String commodityId,DB db) throws JDBCException{

		String findRewaldSQL="SELECT mrrs.* FROM mall_CommodityRewardRule AS crr LEFT JOIN mall_RewardRuleSetup AS mrrs ON crr.rewardruleid=mrrs.id WHERE crr.commodityid=?";
		MapList map=db.query(findRewaldSQL,commodityId,Type.VARCHAR);
		return map;
	}
	
	/**
	 * 根据商品ID获取商品奖励规则<br />
	 * 商品奖励规则字段：<br />
	 *  id,commodityid,rewardruleid,parameters
	 * @param commodityId
	 * @param DB 
	 * @return
	 * @throws JDBCException 
	 */
	public MapList getCommodityRewardRuleOfCommodityById(String commodityId,DB db) throws JDBCException{

		String findRewaldSQL="SELECT mrrs.* FROM mall_CommodityRewardRule AS crr LEFT JOIN mall_RewardRuleSetup AS mrrs ON crr.rewardruleid=mrrs.id WHERE crr.commodityid=?";
		MapList map=db.query(findRewaldSQL,commodityId,Type.VARCHAR);
		return map;
	}

	/**
	 * 根据商品规格ID，获取商品规格信息
	 * @param specId 商品规格ID
	 * @return 商品规格
	 */
	public CommoditySpecifications getCommoditSpecificationsByID(String specId){
		
		CommoditySpecifications result=null;
		DB db=null;
		try {
			
			db = DBFactory.newDB();
			
			result=getCommoditSpecificationsByID(specId, db);
		} catch (JDBCException e) {
			e.printStackTrace();
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
		
	}
	
	/**
	 * 根据商品规格ID，获取商品规格信息
	 * @param specId 商品规格ID
	 * @param db DB
	 * @return 商品规格
	 * @throws JDBCException 
	 */
	public CommoditySpecifications getCommoditSpecificationsByID(String specId,DB db) throws JDBCException{
		
		String findSQL="SELECT * FROM mall_CommoditySpecifications WHERE id=?";
		
		MapList map=db.query(findSQL, specId, Type.VARCHAR);
		
		CommoditySpecifications spec=new CommoditySpecifications(map);
		
		return spec;
	}
	
	/**
	 * 获取商品规格信息
	 * @param db
	 * @param specId
	 * @param comdityId
	 * @return
	 * @throws JDBCException 
	 */
	public JSONObject getCommodityPaymentInfo(DB db, String specId,
			String comdityId,String className) throws Exception {
		
		JSONObject result=new JSONObject();
		
		StringBuilder querySQL=new StringBuilder();
		//根据规格ID,查询商品信息SQL
		querySQL.append("SELECT spec.id AS specid,spec.name AS specName,spec.specimages,"
				+ "	spec.price AS specprice,spec.stock AS specStock,carType.store_name AS carTypeName, ");
		querySQL.append("	comd.discount,comd.discountdate,store.store_name,comd.id AS comdid,comd.mall_class ");
		querySQL.append("	,comd.*      ");
		querySQL.append("	FROM mall_CommoditySpecifications AS spec  ");
		querySQL.append("	LEFT JOIN mall_Commodity AS comd ON spec.CommodityID=comd.id ");
		querySQL.append("	LEFT JOIN mall_Store AS store ON store.id=comd.store   ");
		querySQL.append("	LEFT JOIN mall_store AS carType ON carType.id=comd.car_type ");
		querySQL.append("	WHERE spec.id=?                  ");
		
		MapList comdMap=db.query(querySQL.toString(),specId,Type.VARCHAR);
		
		
		DBManager dbManager=new DBManager();
		if(!Checker.isEmpty(comdMap)){
			//商品信息
			JSONObject comdObj=dbManager.mapListToJSon(comdMap).getJSONObject(0);
			result.put("COMD_INFO", comdObj);
		}
		
		//2,查询商品的订购须信息
		querySQL=new StringBuilder(); 
		querySQL.append(" SELECT * FROM mall_StoreInfo WHERE store_id=? AND classname=? ");
		
		comdMap=db.query(querySQL.toString(),new String[]{
			comdityId,className
		},new int[]{
			Type.VARCHAR,Type.VARCHAR
		});
		
		if(!Checker.isEmpty(comdMap)){
			//商品信息
			JSONObject comdObj=dbManager.mapListToJSon(comdMap).getJSONObject(0);
			result.put(className, comdObj);
		}else{
			JSONObject item=new JSONObject();
			item.put("CONTENT","");
			result.put(className,item);
		}
		
		return result;
	}
	
}
