package com.am.app_plugins.cooperative.start_business;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * 手机端服务类商品查询 api
 * @author wz
 * 
 * 此类如果查询详情，和服务类商品类别和详情
 * 1，如果参数id，则表示查询服务类商品的详情。
 * 2，如果没有参数ID也无参数memberid，则表示查询服务类商品
 * 3，如果参数有memberid，但是无id，则表示查询当前人的创业内容
 * 4，省市区需要判断是否可以购买
 *
 *a,扶贫办，省，市，区/县都可以有，并且只能有一个。
 *b,配送中心，只能有区县配送中心,而且只能有一个。
 *c,联合社,区县联合社只能有一个    //2016-12-02(已确认) 联合社区县联合社可以有多个,只有区县联合社，无省，市联合社
 *d,服务中心，省，市，区/县都可以有，并且只能有一个。
 *
 *aorgtype>area_type:01:全国;02:省;03:市;04:区县;
 *
 *
 */
public class GetServiceMallInfo implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		
		JSONObject result = new JSONObject();
		
		DB db = null;
		//省份
		String province = request.getParameter("province");
		//城市
		String city = request.getParameter("city");
		//区县
		String zone = request.getParameter("zone");
		//会员id
		String memberid = request.getParameter("memberid");
		
		String id = request.getParameter("id");
				
		
		try{
			db=DBFactory.newDB();
			
			if(!Checker.isEmpty(memberid)&&Checker.isEmpty(province)){
				//有会员id，则为查询我的创业
				//列举所有的服务类商品
				String queyrSQL="SELECT sinfo.* ,m.membername,"+
						       "    d1.t_name,d1.sc_name,d1.sc_type,d1.sc_explain,d1.price,d1.sc_content, "+
						       "     torder.id AS orderid,torder.buyer,torder.order_status "+
						       "    FROM service_mall_info  AS sinfo "+
						       "    LEFT JOIN am_terminal_order_manager AS torder ON torder.org_id=sinfo.orgid  "+
						       "    LEFT JOIN (  "+
						       "    SELECT msc.id AS scid,msc.sc_name,msc.sc_type,msc.sc_explain,msc.price,msc.sc_content, "+
						       "    	msct.t_name,msct.t_code,msct.t_table_name,msct.org_type ,ot.area_type "+
						       "    	FROM mall_service_commodity AS msc  "+
						       "    	LEFT JOIN mall_service_comd_type AS msct ON msc.sc_type=msct.id  "+
						       "		LEFT JOIN aorgtype AS ot ON ot.orgtype=msct.org_type   "+
						       "    ) d1 ON d1.t_table_name=sinfo.tablename AND (d1.area_type=sinfo.area_type)  "+
						       "    LEFT JOIN am_member AS m ON m.id=sinfo.purchaser  "+
						       "    WHERE sinfo.purchaser=? ";
//				AND d1.area_type=sinfo.area_type
				MapList map=db.query(queyrSQL, memberid, Type.VARCHAR);
				if(!Checker.isEmpty(map)){
					DBManager dbm=new DBManager();
					JSONArray data=dbm.mapListToJSon(map);
					result.put("DATA", data);
				}
				
			}else if(!Checker.isEmpty(id)){
				//如果有id，则为查看服务器详情，填写创业详情
				//根据ID查询联合社，合作社，配送中心的详情
				String sql = "SELECT msc.*,msct.t_name,msct.t_code,msct.t_table_name,msct.create_time,msct.admin_role_code,msct.org_type FROM mall_service_commodity as msc "
			    		+ " LEFT JOIN mall_service_comd_type as msct"
			    		+ " ON msc.sc_type = msct.id"
			    		+ " WHERE status = '1' "
			    		+ " and msc.id = '"+id+"'" ;
				
				MapList serivceMapList = db.query(sql);
				JSONArray typeJso = new JSONArray();
				//type 为 1 则为没有过滤省市区的服务类商品
				typeJso.put(formatJSONArray(serivceMapList)) ;
				return typeJso.toString();
				
			}else{
				//列举所有的服务类商品
				String areaType="01";
				if(!Checker.isEmpty(province)&&Checker.isEmpty(city)&&Checker.isEmpty(zone)){
					//省
					areaType="02";
				}
				if(!Checker.isEmpty(province)&&!Checker.isEmpty(city)&&Checker.isEmpty(zone)){
					//市
					areaType="03";
				}
				if(!Checker.isEmpty(province)&&!Checker.isEmpty(city)&&!Checker.isEmpty(zone)){
					//区县
					areaType="04";
				}
				
				JSONArray data=getServiceInfoData(db,memberid,province,city,zone,areaType);
				
				result.put("DATA", data);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		
		return result.toString();
	}
	
	
	
	/**
	 * 根据省市区获取 联合社，合作社，区县配送中心，涉农企业，自然村农厂，
	 * 配送人员，物流车辆，扶贫办，区县服务中心。
	 * 
	 * @param db  DB
	 * @param province  省
	 * @param city  市
	 * @param zone 区县
	 * @param areaType 区域类型 01:全国;02:省;03:市;04:区县;
	 * @return 服务类商品数据结合，JSON格式
	 */
	private JSONArray getServiceInfoData(DB db,String memberId, String province, 
			String city, String zone,String areaType) throws Exception{
		JSONArray reuslt=new JSONArray();
		
		//联合社可以有多个
		//扶贫办，省，市，区均有扶贫办，只能有一个
		//配送中心，区县配送中心，只能有一个
		//区县服务中心，区县，只能有一个
		//合作社，只要区县，可以是多个
		//涉农企业，区县，可以是多个
		//自然农场，区县，可以是多个
		//配送人员，区县，可以是多个
		//物流车辆，区县，可以是多个
		
		//联合社，只有区县联合社，区县联合社只能有多个
		JSONObject item=getUnitedPressData(db,memberId,province,city,zone,areaType);
		if(item!=null){
			reuslt.put(item);
		}
		
		//扶贫办
		item=getIprccOffice(db,memberId,province,city,zone,areaType);
		if(item!=null){
			reuslt.put(item);
		}
		
		//配送中心
		item=getMallDistributionCenter(db,memberId,province,city,zone,areaType);
		if(item!=null){
			reuslt.put(item);
		}
		
		//区县服务中心
		item=getServiceCenter(db,memberId,province,city,zone,areaType);
		if(item!=null){
			reuslt.put(item);
		}
		
		//合作社
		item=getMallCooperative(db,memberId,province,city,zone,areaType);
		if(item!=null){
			reuslt.put(item);
		}
		
		//涉农企业
		item=getMallAgriculturalOrg(db,memberId,province,city,zone,areaType);
		if(item!=null){
			reuslt.put(item);
		}
		
		//自然农场
		item=getHomeFarm(db,memberId,province,city,zone,areaType);
		if(item!=null){
			reuslt.put(item);
		}
		
		//配送人员
		if("04".equals(areaType)){
			item=getMallDistributionPersonnel(db,memberId,province,city,zone,areaType);
			if(item!=null){
				reuslt.put(item);
			}
		}
		
		
		//物流车辆
		if("04".equals(areaType)){
			item=getMallLogisticsVehicles(db,memberId,province,city,zone,areaType);
			if(item!=null){
				reuslt.put(item);
			}
		}
		
		
		return reuslt;
	}

	/**
	 * 物流车辆
	 * @param db
	 * @param memberId
	 * @param province
	 * @param city
	 * @param zone
	 * @return
	 */
	private JSONObject getMallLogisticsVehicles(DB db, String memberId, String province, String city, 
			String zone,String areaType)throws Exception {
		JSONObject result=null;
		//物流车辆
		String tableName="mall_logistics_vehicles";
		
		result=getShowDataByTableName(db, tableName,areaType);
		if(result!=null){
			result.put("AREA_TYPE",areaType);
			//物流车辆  可以有多个
			result.put("ALREADY_BUY",false);
		}
		
		return result;
	}



	/**
	 * 配送人员  可以有多个
	 * @param db
	 * @param memberId
	 * @param province
	 * @param city
	 * @param zone
	 * @return
	 */
	private JSONObject getMallDistributionPersonnel(DB db, String memberId, String province, String city, String zone,String areaType)throws Exception {
		JSONObject result=null;
		//配送人员
		String tableName="mall_distribution_personnel";
		
		result=getShowDataByTableName(db, tableName,areaType);
		if(result!=null){
			result.put("AREA_TYPE",areaType);
			//配送人员  可以有多个
			result.put("ALREADY_BUY",false);
		}
		return result;
	}


	
	/**
	 * 农厂自然村
	 * @param db
	 * @param memberId
	 * @param province
	 * @param city
	 * @param zone
	 * @return
	 * @throws Exception
	 */
	private JSONObject getHomeFarm(DB db, String memberId, String province, String city, String zone,String areaType)throws Exception {
		JSONObject result=null;
		//农厂自然村
		String tableName="home_farm";
		
		result=getShowDataByTableName(db, tableName,areaType);
		
		if(result!=null){
			result.put("AREA_TYPE",areaType);
			//农厂自然村 可以有多个
			result.put("ALREADY_BUY",false);
		}
			
		return result;
	}



	/***
	 * 获取涉农企业，涉农企业只有区县涉农企业，可以有多个涉农企业
	 * @param db
	 * @param memberId
	 * @param province
	 * @param city
	 * @param zone
	 * @return
	 */
	private JSONObject getMallAgriculturalOrg(DB db, String memberId, String province, String city, String zone,String areaType)throws Exception {
		JSONObject result=null;
		//涉农企业
		String tableName="mall_agricultural_org";
		result=getShowDataByTableName(db, tableName,areaType);
		if(result!=null){
			result.put("AREA_TYPE",areaType);
			//涉农企业 可以有多个
			result.put("ALREADY_BUY",false);
		}
		
		return result;
	}



	/**
	 * 获取合作社，合作社只有区县合作社，可以有多个区县合作社
	 * @param db
	 * @param memberId
	 * @param province
	 * @param city
	 * @param zone
	 * @return
	 */
	private JSONObject getMallCooperative(DB db, String memberId, String province, String city, String zone,String areaType)throws Exception {
		JSONObject result=null;
		//合作社
		String tableName="mall_cooperative";
		
		result=getShowDataByTableName(db, tableName,areaType);
		if(result!=null){
			result.put("AREA_TYPE",areaType);
			//合作社 可以有多个
			result.put("ALREADY_BUY",false);
		}
		
		
		return result;
	}



	/**
	 * 服务中心
	 * @param db
	 * @param memberId
	 * @param province
	 * @param city
	 * @param zone
	 * @return
	 */
	private JSONObject getServiceCenter(DB db, String memberId, String province, String city, String zone,String areaType) throws Exception{
		JSONObject result=null;
		//服务中心
		String tableName="mall_service_center";
		
		result=getShowDataByTableName(db, tableName,areaType);
		if(result!=null){
			result.put("AREA_TYPE",areaType);
			result.put("ALREADY_BUY",checkisExist(db,tableName,province,city,zone,areaType));
		}
		
		return result;
	}



	private boolean checkisExist(DB db, String tableName, String province, String city, 
			String zone, String areaType) throws JDBCException {
		boolean result=false;
		
		//区县服务中心查询
		String querySQL="SELECT * FROM "+tableName
				+ " WHERE area_type='"+areaType+"' AND f_status='1' ";
		
		//判断机构是否已经存在
		if("04".equals(areaType)){
			//区县级别
			querySQL+=" AND province_id='"+province+"' "
			+ " AND zone_id='"+zone+"' ";
		}
		if("03".equals(areaType)){
			//市级别
			querySQL+=" AND province_id='"+province+"' "
					+ " AND city_id='"+city+"' ";
		}
		if("02".equals(areaType)){
			//省级别
			querySQL+=" AND province_id='"+province+"' ";
		}
		
		//查询该省对应的服务中心
		MapList map=db.query(querySQL);
		
		if(!Checker.isEmpty(map)){
			result=true;
		}else{
			result=false;
		}
		return result;
	}



	/**
	 * 获取配送配送中心，区县配送配送中心，只能有个一个
	 * @param db
	 * @param memberId
	 * @param province
	 * @param city
	 * @param zone
	 * @param areaType 
	 * @return
	 */
	private JSONObject getMallDistributionCenter(DB db, String memberId, String province, 
			String city, String zone,String areaType)throws Exception {
		JSONObject result=null;
		
		//配送配送中心
		String tableName="mall_distribution_center";
		
		result=getShowDataByTableName(db, tableName,areaType);
		if(result!=null){
			result.put("AREA_TYPE",areaType);
			//区县配送中心只能有一个
			result.put("ALREADY_BUY",checkisExist(db, tableName, province, city, zone, areaType));
		}
		
		return result;
	}



	/**
	 * 扶贫办，省，市，区县都有，且只能有一个
	 * @param db
	 * @param memberId
	 * @param province
	 * @param city
	 * @param zone
	 * @param area_type 区域类型 01：全国; 02：省; 03：市;04：区县
	 * @return
	 */
	private JSONObject getIprccOffice(DB db, String memberId, String province, String city, 
			String zone,String areaType)  throws Exception {
		JSONObject result=null;
		
		String tableName="iprcc_office";
		
		//1,查询对应的省市区是否有扶贫办
		//2,如果有对应的扶贫办，则返回无法购买，如果没有则显示可以购买
		result=getShowDataByTableName(db, tableName,areaType);
		if(result!=null){
			result.put("AREA_TYPE",areaType);
			result.put("ALREADY_BUY",checkisExist(db, tableName, province, city, zone, areaType));
		}
		
		return result;
	}



	/**
	 * 联合社，只有区县联合社，区县联合社只能有一个
	 * @param db
	 * @param memberId
	 * @param province
	 * @param city
	 * @param zone
	 * @param areaType 
	 * @return
	 * @throws JDBCException 
	 */
	private JSONObject getUnitedPressData(DB db, String memberId, 
			String province, String city, String zone,String areaType) throws Exception {
		
		JSONObject result=null;
		
		String tableName="united_press";
		
		//2,如果有对应的联合社，则返回无法购买，如果没有则显示可以购买
		
		result=getShowDataByTableName(db, tableName,areaType);
		if(result!=null){
			result.put("AREA_TYPE",areaType);
//			result.put("ALREADY_BUY",checkisExist(db, tableName, province, city, zone, areaType));
			result.put("ALREADY_BUY",false);
		}
			
		
		return result;
	}


	
	/**
	 * 根据表明获取商品信息
	 * @param db
	 * @param tableName
	 * @param areaType 01:全国;02:省;03:市;04:区县;
	 * @return
	 * @throws Exception
	 */
	public JSONObject getShowDataByTableName(DB db,String tableName,String areaType) throws Exception{
		
		JSONObject result=null;
		
		//查询服务类商品信息
		String querySQL="SELECT msc.*,msct.t_name,msct.t_code,msct.t_table_name,msct.org_type "+
				"  FROM mall_service_commodity AS msc "+
				"  LEFT JOIN mall_service_comd_type AS msct ON msc.sc_type=msct.id  "+
				"  LEFT JOIN aorgtype AS ogt ON ogt.orgtype=msct.org_type  "+
				"  WHERE t_table_name='"+tableName+"'  "+
				"  AND status='1' ";
				
		
		//配送人员和配送连无机构类型
		if(!"mall_distribution_personnel".equalsIgnoreCase(tableName)
				&&!"mall_logistics_vehicles".equalsIgnoreCase(tableName)){
			querySQL+="  AND ogt.area_type='"+areaType+"' ";
		}
		
		MapList dataMap=db.query(querySQL);
		
		DBManager dbm=new DBManager();
		
		if(!Checker.isEmpty(dataMap)){
			JSONArray itemData=dbm.mapListToJSon(dataMap);
			if(itemData!=null){
				result=itemData.getJSONObject(0);
			}
		}
		
		return result;
	}
	
	
	/**
	 * 将maplist转换为jsonArray
	 * @param list
	 * @return
	 */
	public JSONArray formatJSONArray(MapList list){
		
		JSONArray returnJsonArray = new JSONArray();
		
		if(!Checker.isEmpty(list)){
			
			int row_count=list.size();
			try {
				
				for(int i=0;i<row_count;i++) 
				{
					Row row=list.getRow(i);
					
					int column_count=row.size();
					
					JSONObject jo = new JSONObject();
					
					for (int j =0; j<column_count;j++) {
						
						String currentValue=row.get(j);
						
						if (currentValue == null || currentValue.trim().equalsIgnoreCase("null")) {
							
								jo.put(row.getKey(j).toUpperCase(), "");
							
						} else {
							jo.put(row.getKey(j).toUpperCase(),currentValue);
						}
					}
				
					returnJsonArray.put(jo);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}		
		}
		return returnJsonArray;
	}

}
