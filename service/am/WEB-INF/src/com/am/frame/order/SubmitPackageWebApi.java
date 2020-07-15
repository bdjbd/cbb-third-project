package com.am.frame.order;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.am.mall.beans.order.MemberOrder;
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
 * @author YueBin
 * @create 2016年5月22日
 * @version 
 * 说明:<br />
 */
public class SubmitPackageWebApi implements IWebApiService {
	
	/**
     * 提交套餐购买
     * @param memberid 会员ID
     * @param packageId 套餐id
     * @returns
     */
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject result=new JSONObject();
		//会员ID
		String memberId=request.getParameter("memberId");
		//套餐ID
		String packageId=request.getParameter("packageId");
		String orgCode=request.getParameter("orgId");
		
		DB db=null;
		
		try{
			db=DBFactory.newDB();
			if(!Checker.isEmpty(memberId)&&!Checker.isEmpty(packageId)){
				//1,获取套餐信息
				MapList packageMap=getPackageInfoByPackageId(db,packageId);
				//2,将套餐添加到社员订单表中
				result=createOrdersByPackageMap(db,packageMap,memberId,orgCode);
			}
		}catch(Exception e){
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e1) {
					e1.printStackTrace();
				}
			}
		}
		return result.toString();
	}
	
	
	/**
	 * 根据套餐信息创建订单信息
	 * @param db
	 * @param packageMap 商品套餐信息
	 * @param memberId
	 * @param orgCode 
	 * @throws JDBCException 
	 */
	private JSONObject createOrdersByPackageMap(DB db, MapList packageMap, String memberId, String orgCode) throws Exception {
		
		JSONObject result=new JSONObject();
		
		/***订单批处理参数集合 此集合的第一个字段为订单ID***/
		List<String[]> orderParams=new ArrayList<String[]>();
		/**保存购物车状态的订单编号，并将此数据删除**/
		List<String[]> orderCodes=new ArrayList<String[]>();
		
		//订单id
		List<String> oriders=new ArrayList<String>();
		
		String queryMemberSQL="SELECT * FROM am_member WHERE id=? ";
		MapList memberMap=db.query(queryMemberSQL,memberId,Type.VARCHAR);
		
		String memberPhone=memberMap.getRow(0).get("LOGINACCOUNT");
		
		
		if(!Checker.isEmpty(packageMap)){
			
			MemberOrder memberOrder=new MemberOrder();
			
			for(int i=0;i<packageMap.size();i++){
				Row row=packageMap.getRow(i);
				
				String orderId=UUID.randomUUID().toString();
				oriders.add(orderId);
				
				orderParams.add(new String[]{
						orderId,memberId, row.get("COMDITYID"),"1", 
						"",row.get("NAME"),//支付方式，商品名称
						"","",//规格名称，规格价格
						"1",/**SCALENUMBER销售数据量**/
						row.get("GPRICE"),//销售价格
						"0.00", //Postage
						row.get("GPRICE"),//TotalPrice
						"","","",//ZipCode
						"","",
						System.currentTimeMillis()+""+RandomUtil.getRandomString(3).toUpperCase(),
						orgCode,   //orgcode
						"","","",//省，市，区
						"","1"});
				orderCodes.add(new String[]{memberOrder.getOderCodeByMemberId(memberPhone)});
			}
		}
		
		
		//删除本次在购车中包含的商品
		String deleteSQL="DELETE FROM mall_memberorder WHERE ordercode=?";
		
		db.executeBatch(deleteSQL, orderCodes,new int[]{Type.VARCHAR});
		
		
		//批量处理插入数据库数据。
		String insertSQL=
						"INSERT INTO mall_memberorder(                                        "+
						"            id, memberid, commodityid, orderstate,                   "+
						"            paymentmode, commodityname, specname, specprice,         "+
						"            salenumber, saleprice, postage,       "+
						"            totalprice, "+
						"            contact,contactphone, address, zipcode,                  "+
						"            longitud, latitude, ordercode, orgcode,                  "+
						"			 province_id,city_id,zone_id,	"+
						"            specid,IsGroupSale)                  "+
						"    VALUES (                                                         "+
						"	    	?, ?, ?, ?,                                  "+
						"	    	?, ?, ?, ?,                                               "+
						"	    	?, ?, ?,                                             "+
						"           ?,                                              "+
						"           ?, ?, ?, ?,                                               "+
						"           ?, ?, ?, ?,                                        "+
						"           ?, ?, ?,                                       "+
						"           ?,?);                                              ";
		
		//保存新订单
		db.executeBatch(insertSQL, orderParams, new int[]{
				Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,
				Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,Type.DECIMAL,
				Type.INTEGER,Type.DECIMAL,Type.DECIMAL,
				Type.DECIMAL,
				Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,
				Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,
				Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,
				Type.VARCHAR,Type.VARCHAR
		});
		
		result.put("ORDERIDS", oriders.toArray());
		return result;
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
		querySQL.append("	cmd.*                                                                                ");
		querySQL.append("	FROM mall_CommodityGroupSale AS cgs                                                  ");
		querySQL.append("	LEFT JOIN mall_CommodityGroupsSaleSet AS cgSet ON cgs.id=cgSet.CommodityGroupSaleID  ");
		querySQL.append("	LEFT JOIN mall_Commodity AS cmd ON cmd.id=cgSet.GroupCommodityID                     ");
		querySQL.append("	WHERE cgs.id=?                                 ");
		querySQL.append("UNION                                                                                 ");
		querySQL.append("SELECT title AS gtitle,thiscommodityid AS comdityId,cgs.price AS gprice ,             ");
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
