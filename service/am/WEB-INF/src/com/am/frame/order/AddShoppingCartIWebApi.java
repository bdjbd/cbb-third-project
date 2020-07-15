package com.am.frame.order;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.fastunit.util.RandomUtil;
import com.p2p.service.IWebApiService;

/**
 * @author Mike
 * @create 2014年12月1日
 * @version 
 * 说明:<br />
 * 加入购物车接口<br />
 * DATA={"MEMBERID":"会员ID","COMMODITYID":"商品ID","GROUPSALEID":"套餐ID","ORGCODE":"机构编号"}
 * 
 */
public class AddShoppingCartIWebApi implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		//构造返回结果
		JSONObject job=new JSONObject();
		
		//获取订单信息
		
		String data=request.getParameter("DATA");
			DB db=null;
		try{
			
			if(!Checker.isEmpty(data)){
				
				db=DBFactory.newDB();
				
				//加入购物车提交订单数据
				List<String[]> result=insertAddShoppingCartData(data, db);
				
				String orderIds="";
				
				StringBuilder sb=new StringBuilder();
				
				for(int i=0;i<result.size();i++){
					
					sb.append(",");
					sb.append(result.get(i)[0]);
				}
				
				if(sb.length()>1){
					sb.delete(0, 1);
					
					orderIds=sb.toString().replace(",","','");
					orderIds="'"+orderIds+"'";
				}
				
				job.put("COUNT",result.size());
				job.put("ERRCODE",0);
				job.put("ERRMSG","加入购物车成功!");
				job.put("ORDERID",orderIds);
			}else{
				throw new Exception("提交数据不能为空！");
			}
		}catch(Exception e){
			
			try{
				
				job.put("COUNT",0);
				job.put("ERRCODE",1);
				job.put("ERRMSG","加入购物车失败。"+e.getMessage());
				job.put("PAYMONEY","");
				job.put("COMDITYNAME","");
				job.put("PAYID","");
				
			}catch(JSONException e1){
			}
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		
		return job.toString();
	}

	/**
	 * 将数据加入到会员订单表中
	 * @param data  JSON格式的字符串，字符串格式如下：<br /><b>
	 * {"MEMBERID":"会员ID",
	 * "SPECIFICATIONSID":"规格ID,在非套餐加入购物车时，此字段必须要有"
	 * "GROUPSALEID":"套餐ID","ORGCODE":"机构编号","ISGROUPSALE":"是否为套餐，TRUE，是，FALSE，不是"}</ b>
	 * @throws Exception 
	 */
	private List<String[]> insertAddShoppingCartData(String data,DB db) throws Exception {
		
		List<String[]> orderIds=new ArrayList<String[]>();
		
		JSONObject dataJs=new JSONObject(data);
		
		//解析参数
		//会员ID
		String memberId=dataJs.getString("MEMBERID");
		//套餐ID
		String groupSaleId=dataJs.getString("GROUPSALEID");
		
		//机构编号
		String orgCode=dataJs.getString("ORGCODE");
		//是否为套餐 false 不是，true 是套餐
		boolean  isGrouup=false;
		
		if(dataJs.has("ISGROUPSALE")){
			isGrouup=dataJs.getBoolean("ISGROUPSALE");
		}
		
		if(isGrouup){
			//套餐加入购物车
			orderIds=addMemberOrderGroupSale(groupSaleId, memberId, orgCode, db);
		}else{
			//商品ID
			
			if(dataJs.has("SPECIFICATIONSID")){
				//规格ID
				String spceId=dataJs.getString("SPECIFICATIONSID");
				
				//普通加入购物车
				orderIds=addShoppingCart("",spceId, memberId, orgCode, db);
			}else{
				throw new Exception("加入购物车时应选择商品规格");
			}
			
		}
		 
		return orderIds;
	}

	

	/**
	 * 普通加入购物车
	 * @param commodityId 商品ID
	 * @param spceId 规格id
	 * @param memberId 会员ID
	 * @param orgCode 会员机构ID
	 * @param db  DB
	 * @return
	 * @throws JDBCException 
	 */
	private List<String[]> addShoppingCart(String commodityId,String spceId, String memberId,
			String orgCode, DB db) throws JDBCException {
		
		List<String[]> orderIds=new ArrayList<String[]>();
		
		String findComdSQL=
				"SELECT c.id AS commodityid,c.name AS commodityname,c.*,"+
				"		cs.id AS specid,cs.name AS specname,"+
				"       cs.price AS saleprice,cs.*         "+
				"		FROM mall_Commodity AS c                                             "+
				"		LEFT JOIN mall_CommoditySpecifications AS cs ON c.id=cs.commodityid  "+
				"		WHERE cs.id=?   ";
		
		MapList map=db.query(findComdSQL, spceId, Type.VARCHAR);

		orderIds=insertMemberOrderCart(map,memberId,orgCode,db);
		
		return orderIds;
	}

	
	/**
	 * 向数据库中插入完整的订单信息 状态为购物车1
	 * @param map  商品数据集合
	 * @param memberId 收藏人ID
	 * @param orgCode  机构编号
	 * @param db db
	 * @return
	 * @throws JDBCException 
	 */
	private List<String[]> insertMemberOrderCart(MapList map, String memberId,
			String orgCode, DB db) throws JDBCException {
		
		List<String[]> params=new ArrayList<String[]>();
		
		if(!Checker.isEmpty(map)){
			
			//订单ID
			String orderId="";
			String orderCode="";
			
			String orderState="1";
			
			String salenumber="1";
			
			String insertSQL=
					"INSERT INTO mall_memberorder(                                                   "+
					"            id, memberid, commodityid, orderstate,"+
					" 			paymentmode, commodityname,  "+
					"            specname, specprice, salenumber, saleprice,                         "+
					"            ordercode,                                                          "+
					"            orgcode, specid, createdate                                         "+
					"            )                                                                   "+
					"    VALUES (                                                                    "+
					"    				?, ?, ?, ?, ?, ?,                                            "+
					"    				?, ?, ?, ?,                                                  "+
					"            ?,                                                                  "+
					"            ?, ?, now()                                                         "+
					"            )                                                                   ";
			
			for(int i=0;i<map.size();i++){
				
				Row row=map.getRow(i);
				//生成订单ID
				orderId=UUID.randomUUID().toString().toUpperCase().replaceAll("-","0");
				//订单编号
				orderCode=System.currentTimeMillis()+""+RandomUtil.getRandomString(3).toUpperCase();
				
				params.add(
						new String[]{
								orderId,memberId,row.get("commodityid"),orderState,
								row.get("paymentmode"),row.get("commodityname"),
								row.get("specname"),row.get("saleprice"),salenumber,row.get("saleprice"),
								orderCode,
								orgCode,row.get("specid")});
			
			}
			
			//保存订单信息
			db.executeBatch(insertSQL, params,
					new int[]{
					Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,
					Type.VARCHAR,Type.VARCHAR,
					Type.VARCHAR,Type.DECIMAL,Type.INTEGER,Type.DECIMAL,
					Type.VARCHAR,
					Type.VARCHAR,Type.VARCHAR
			});
			
		}
		
		return params;
	}

	/**
	 * 套餐加入购物车接口
	 * @param string
	 * @param memberId
	 * @param orgCode
	 * @param db
	 * @return
	 * @throws JDBCException 
	 */
	private List<String[]>  addMemberOrderGroupSale(String groupSaleId, String memberId,String orgCode, DB db) throws JDBCException {
		
		List<String[]> orderIds=new ArrayList<String[]>();
		
//		String sql="SELECT * FROM mall_Commodity  WHERE id IN("
//				+ " SELECT GroupCommodityID "
//				+ " FROM mall_CommodityGroupsSaleSet  "
//				+ " WHERE CommodityGroupSaleID=? )";
		
		//套餐商品集合
		MapList map=getPackageInfoByPackageId(db, groupSaleId);
		
		//更加ID将商品添加到订单表中
		orderIds=insertOrderGroupSale( map,  memberId,  orgCode,db);
		
		return orderIds;
	}

	/**
	 * 
	 * @param map
	 * @param memberId
	 * @param orgCode
	 * @param db
	 * @param isGroupSale
	 * @return
	 * @throws JDBCException
	 */
	private List<String[]> insertOrderGroupSale(MapList map, String memberId, String orgCode,DB db) throws JDBCException {
		
		List<String[]> params=new ArrayList<String[]>();
		
		//订单ID
		String orderId="";
		//订单编号
		String orderCode="";
		//销售价格，按照套餐里面的价格计算
		String salePrice="0";
		
		if(!Checker.isEmpty(map)){
			String isnertSQL=
						"INSERT INTO mall_memberorder(                      "+
						"            id, memberid, commodityid, orderstate, "+
						"            ordercode,                             "+
						"            orgcode, createdate,IsGroupSale,        "+
						"            SaleNumber, SalePrice,TotalPrice,SpecPrice,specid)       "+
						"    VALUES (?, ?, ?,'1',                            "+
						"    			 ?,                                 "+
						"    			 ?,now(),'1',"+
						"    			 '1',?,?,?,?)                              ";
//			 id, memberid, commodityid, orderstate, 
//			 paymentmode, commodityname, 
//			 specname, specprice, salenumber, saleprice,  
//			 ordercode,                                     
//			 orgcode, specid, createdate                     
			
			
			for(int i=0;i<map.size();i++){
					
				orderId=UUID.randomUUID().toString().toUpperCase().replaceAll("-","0");
				orderCode=System.currentTimeMillis()+""+RandomUtil.getRandomString(3).toUpperCase();
				salePrice=map.getRow(i).get("gprice");
				
				params.add(new String[]{
							orderId,memberId,map.getRow(i).get("id"),
							orderCode,
							orgCode,
							salePrice,salePrice,salePrice,
							map.getRow(i).get("spec_id")
				});
			}
				
			db.executeBatch(isnertSQL,params,
						new int[]{
						Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,
						Type.VARCHAR,
						Type.VARCHAR,
						Type.DECIMAL,Type.DECIMAL,Type.DECIMAL,
						Type.VARCHAR
			});
		}
		
		return params;
	}

	
	
	/**
	 * 根据套餐id查询商品信息
	 * @param db
	 * @param packageId
	 * @return MapList gtitle 套餐名称，comdityId 商品ID，gprice商品套餐价格
	 * @throws JDBCException 
	 */
	private MapList getPackageInfoByPackageId(DB db, String packageId) throws JDBCException {
		StringBuilder querySQL=new StringBuilder();
		
		querySQL.append("SELECT cgs.title AS gtitle,cgSet.GroupCommodityID AS comdityId,cgset.price AS gprice, ");
		querySQL.append("   cgSet.spec_id, ");
		querySQL.append("	cmd.*                                                                                ");
		querySQL.append("	FROM mall_CommodityGroupSale AS cgs                                                  ");
		querySQL.append("	LEFT JOIN mall_CommodityGroupsSaleSet AS cgSet ON cgs.id=cgSet.CommodityGroupSaleID  ");
		querySQL.append("	LEFT JOIN mall_Commodity AS cmd ON cmd.id=cgSet.GroupCommodityID                     ");
		querySQL.append("	WHERE cgs.id=?                                 ");
		querySQL.append("UNION                                                                                 ");
		querySQL.append("SELECT title AS gtitle,thiscommodityid AS comdityId,cgs.price AS gprice ,             ");
		querySQL.append("   cgs.spec_id, ");
		querySQL.append("	cmd.*                                                                                ");
		querySQL.append("	FROM mall_CommodityGroupSale AS cgs                                                  ");
		querySQL.append("	LEFT JOIN mall_Commodity AS cmd ON cmd.id=cgs.thiscommodityid                        ");
		querySQL.append("	WHERE cgs.id=?                                 ");
		
		MapList packageMap=db.query(querySQL.toString(),
				new String[]{packageId,packageId},
				new int[]{Type.VARCHAR,Type.VARCHAR});
		return packageMap;
	}

}
